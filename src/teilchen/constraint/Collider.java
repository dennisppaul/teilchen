/*
 * Teilchen
 *
 * This file is part of the *teilchen* library (https://github.com/dennisppaul/teilchen).
 * Copyright (c) 2024 Dennis P Paul.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * {@link http://www.gnu.org/licenses/lgpl.html}
 *
 */

package teilchen.constraint;

import processing.core.PVector;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.force.Force;

import java.util.ArrayList;
import java.util.List;

/**
 * base class for colliders. colliders keep particles from passing through geometry ( planes, triangles, spheres,
 * lines ).
 * <p>
 * colliders are constraints, i.e they are applied after integration. they test the path a particle traveled during
 * the last step ( from <code>old_position()</code> to <code>position()</code> ) and therefore work with all
 * integrators and do not miss fast particles ( no tunneling ).
 * <p>
 * on collision the particle is moved back to the free side of the geometry ( the penetration is mirrored and scaled
 * by the coefficient of restitution ), the normal component of the velocity is reflected and scaled by the
 * coefficient of restitution while the tangential component is kept. <code>old_position()</code> is updated
 * accordingly so that colliders also work with <code>Verlet</code> integration.
 */
public abstract class Collider implements Constraint {

    /**
     * minimum distance particles are kept away from the surface after a collision. this prevents particles from
     * resting exactly on a surface and being classified on the wrong side due to rounding.
     */
    public float SKIN = Physics.EPSILON;

    /**
     * maximum number of times a particle is resolved per step. more than one iteration is needed if resolving one
     * contact causes another one, e.g in concave corners of a mesh.
     */
    public int MAX_ITERATIONS = 8;

    private final long mID;
    private boolean mActive = true;
    private float mCoefficientOfRestitution = 1.0f;
    private boolean mDead = false;
    private boolean mHit = false;
    private Force mOwner = null;
    private long mOwnerStep = -1;
    private boolean mDetached = false;
    private Physics mPhysics = null;
    private int mIteration = 0;
    private boolean mDeferred = false;
    private boolean mProposed = false;
    private final float[] mProposal = new float[7];
    private float mProposalT;

    protected Collider() {
        mID = Physics.getUniqueID();
    }

    public void apply(final Physics pParticleSystem) {
        mHit = false;
        if (!mActive || !ownerAllowsApply(pParticleSystem)) {
            return;
        }
        prepare();
        mPhysics = pParticleSystem;
        for (final Particle mParticle : pParticleSystem.particles()) {
            if (!mParticle.fixed()) {
                /* repeat while resolving one contact causes another ( e.g in concave corners ) */
                for (mIteration = 0; mIteration < MAX_ITERATIONS && collide(mParticle); mIteration++) {
                    mParticle.tag(true);
                    mHit = true;
                }
            }
        }
        mPhysics = null;
    }

    /**
     * @return start of the path the particle traveled during the current step. use this instead of
     * <code>old_position()</code> for collision tests.
     */
    protected final PVector pathStart(Particle pParticle) {
        return mPhysics != null ? mPhysics.collisionOrigin(pParticle) : pParticle.old_position();
    }
    /**
     * applies several colliders jointly. for each particle the earliest contact along its path ( over all colliders )
     * is resolved first, then the colliders are tested again. this is more robust than applying colliders one after
     * the other, e.g for separate triangles that form a connected surface. <code>Physics</code> uses this for all
     * colliders in its constraints.
     */
    public static void applyAll(final Physics pParticleSystem, final List<Collider> pColliders) {
        final ArrayList<Collider> mActive = new ArrayList<>(pColliders.size());
        int mMaxIterations = 0;
        for (Collider c : pColliders) {
            c.mHit = false;
            if (c.mActive && c.ownerAllowsApply(pParticleSystem)) {
                c.prepare();
                c.mPhysics = pParticleSystem;
                c.mDeferred = true;
                mActive.add(c);
                mMaxIterations = Math.max(mMaxIterations, c.MAX_ITERATIONS);
            }
        }
        if (mActive.isEmpty()) {
            return;
        }
        try {
            for (final Particle mParticle : pParticleSystem.particles()) {
                if (mParticle.fixed()) {
                    continue;
                }
                for (int i = 0; i < mMaxIterations; i++) {
                    Collider mBest = null;
                    for (Collider c : mActive) {
                        c.mProposed = false;
                        c.mIteration = i;
                        if (i < c.MAX_ITERATIONS && c.collide(mParticle) && c.mProposed &&
                            (mBest == null || c.mProposalT < mBest.mProposalT)) {
                            mBest = c;
                        }
                    }
                    if (mBest == null) {
                        break;
                    }
                    final float[] q = mBest.mProposal;
                    mBest.doResolve(mParticle, q[0], q[1], q[2], q[3], q[4], q[5], q[6]);
                    mBest.mHit = true;
                    mParticle.tag(true);
                }
            }
        } finally {
            for (Collider c : mActive) {
                c.mDeferred = false;
                c.mPhysics = null;
            }
        }
    }

    /**
     * called once per step before particles are tested. use it to update cached geometry.
     */
    protected void prepare() {
    }

    /**
     * tests a single particle and resolves a collision.
     *
     * @return true if particle collided
     */
    protected abstract boolean collide(Particle pParticle);

    /**
     * resolves a collision. the contact point is the projection of the particle onto the surface.
     *
     * @param pParticle    colliding particle
     * @param nX           x component of the unit contact normal ( pointing to the free side )
     * @param nY           y component of the unit contact normal
     * @param nZ           z component of the unit contact normal
     * @param pPenetration distance the particle must move along the normal to reach the surface ( > 0 )
     */
    protected final void resolve(final Particle pParticle,
                                 final float nX,
                                 final float nY,
                                 final float nZ,
                                 final float pPenetration) {
        final PVector p1 = pParticle.position();
        resolve(pParticle, nX, nY, nZ, pPenetration,
                p1.x + nX * pPenetration, p1.y + nY * pPenetration, p1.z + nZ * pPenetration);
    }

    /**
     * resolves a collision.
     *
     * @param pParticle    colliding particle
     * @param nX           x component of the unit contact normal ( pointing to the free side )
     * @param nY           y component of the unit contact normal
     * @param nZ           z component of the unit contact normal
     * @param pPenetration distance the particle must move along the normal to reach the surface ( > 0 )
     * @param cX           x component of the point where the path of the particle reached the surface
     * @param cY           y component of contact point
     * @param cZ           z component of contact point
     */
    protected final void resolve(final Particle pParticle,
                                 final float nX,
                                 final float nY,
                                 final float nZ,
                                 final float pPenetration,
                                 final float cX,
                                 final float cY,
                                 final float cZ) {
        if (mDeferred) {
            /* record proposal, see `applyAll` */
            if (!mProposed) {
                mProposed = true;
                mProposal[0] = nX;
                mProposal[1] = nY;
                mProposal[2] = nZ;
                mProposal[3] = pPenetration;
                mProposal[4] = cX;
                mProposal[5] = cY;
                mProposal[6] = cZ;
                /* path parameter of contact point */
                final PVector p0 = pathStart(pParticle);
                final PVector p1 = pParticle.position();
                final float dX = p1.x - p0.x, dY = p1.y - p0.y, dZ = p1.z - p0.z;
                final float l = dX * dX + dY * dY + dZ * dZ;
                mProposalT = l > 0 ? Math.max(0, Math.min(1, ((cX - p0.x) * dX + (cY - p0.y) * dY + (cZ - p0.z) * dZ) / l)) : 0;
            }
            return;
        }
        doResolve(pParticle, nX, nY, nZ, pPenetration, cX, cY, cZ);
    }

    private void doResolve(final Particle pParticle,
                           final float nX,
                           final float nY,
                           final float nZ,
                           final float pPenetration,
                           final float cX,
                           final float cY,
                           final float cZ) {
        final PVector p1 = pParticle.position();
        final PVector p0 = pParticle.old_position();
        final float   e  = mCoefficientOfRestitution;

        /* displacement during last step ( used to keep verlet velocity consistent ) */
        float uX = p1.x - p0.x;
        float uY = p1.y - p0.y;
        float uZ = p1.z - p0.z;

        /* contact point ( lifted to free side ) becomes path start for subsequent colliders */
        if (mPhysics != null) {
            mPhysics.collisionOrigin(pParticle, cX + nX * SKIN, cY + nY * SKIN, cZ + nZ * SKIN);
        }

        /*
         * move back to surface and mirror penetration scaled by restitution. follow-up contacts in the same step
         * ( e.g in concave corners ) only project onto the surface, which guarantees convergence.
         */
        final float mMirror = mIteration == 0 ? e * pPenetration : 0;
        final float mMove = pPenetration + Math.max(mMirror, SKIN);
        p1.add(nX * mMove, nY * mMove, nZ * mMove);

        /* reflect normal component of velocity */
        final PVector v = pParticle.velocity();
        final float mVelocityNormal = v.x * nX + v.y * nY + v.z * nZ;
        if (mVelocityNormal < 0) {
            final float f = (1 + e) * mVelocityNormal;
            v.sub(nX * f, nY * f, nZ * f);
        }

        /* reflect normal component of displacement and derive old position from it */
        final float mDisplacementNormal = uX * nX + uY * nY + uZ * nZ;
        if (mDisplacementNormal < 0) {
            final float f = (1 + e) * mDisplacementNormal;
            uX -= nX * f;
            uY -= nY * f;
            uZ -= nZ * f;
        }
        p0.set(p1.x - uX, p1.y - uY, p1.z - uZ);
        resolved(pParticle);
    }

    /**
     * called after a collision of particle was resolved.
     */
    protected void resolved(Particle pParticle) {
    }

    /**
     * links collider to a force ( e.g a deprecated <code>*Deflector</code> ). the collider is skipped if the force is
     * inactive and becomes dead if the force is dead or was not applied in the current step ( i.e removed from the
     * physics ).
     */
    public void owner(Force pOwner) {
        mOwner = pOwner;
    }

    /**
     * marks owner as applied in the specified step. called by the owning force.
     */
    public void ownerApplied(long pStep) {
        mOwnerStep = pStep;
    }

    /**
     * @return true if the owning force was removed from the physics. a detached collider is dead.
     */
    public boolean detached() {
        return mDetached;
    }

    /**
     * clears detached state, e.g when the owning force is added to a physics again.
     */
    public void attach() {
        mDetached = false;
    }

    /**
     * tests a single particle against collider outside of the simulation step and resolves a collision.
     *
     * @return true if particle collided
     */
    public final boolean apply(final Particle pParticle) {
        mIteration = 0;
        if (!pParticle.fixed() && collide(pParticle)) {
            pParticle.tag(true);
            mHit = true;
            return true;
        }
        return false;
    }

    private boolean ownerAllowsApply(Physics pParticleSystem) {
        if (mOwner == null) {
            return true;
        }
        if (!mOwner.active()) {
            return false;
        }
        if (pParticleSystem != null && mOwnerStep != pParticleSystem.stepCount()) {
            /* owner was removed from physics */
            mDetached = true;
            return false;
        }
        return true;
    }

    /**
     * @return true if any particle collided during the last step
     */
    public boolean hit() {
        return mHit;
    }

    public float coefficientofrestitution() {
        return mCoefficientOfRestitution;
    }

    public void coefficientofrestitution(float pCoefficientOfRestitution) {
        mCoefficientOfRestitution = pCoefficientOfRestitution;
    }

    public boolean active() {
        return mActive;
    }

    public void active(boolean pActiveState) {
        mActive = pActiveState;
    }

    public boolean dead() {
        return mDead || mDetached || (mOwner != null && mOwner.dead());
    }

    public void dead(boolean pDead) {
        mDead = pDead;
    }

    public long ID() {
        return mID;
    }
}
