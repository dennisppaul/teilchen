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

package teilchen.util;

import processing.core.PVector;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.constraint.Constraint;
import teilchen.constraint.Stick;
import teilchen.cubicle.CubicleAtom;
import teilchen.cubicle.CubicleParticle;
import teilchen.cubicle.CubicleWorld;
import teilchen.cubicle.ICubicleEntity;
import teilchen.force.Force;
import teilchen.force.Spring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import static teilchen.Physics.EPSILON;
import static teilchen.util.Util.distance;
import static teilchen.util.Util.lengthSquared;

/**
 * this manager uses it s own particle system. we could make it more integrated by using a shared physic world. this
 * would of course make everthing more slower.
 */
public class CollisionManager {

    public static final int DISTANCE_MODE_FIXED = 1;
    public static final int DISTANCE_MODE_RADIUS = 0;

    public enum ResolverType {
        //        COLLISION_STICK, COLLISION_SPRING,
        SPRING,
        STICK
    }

    public boolean HINT_IGNORE_STILL_OR_FIXED = false;
    private final Physics mCollisionPhysics;
    private final ArrayList<Force> mResolverForces = new ArrayList<>();
    private final ArrayList<Constraint> mResolverConstraints = new ArrayList<>();
    private float mCollisionResolverInterval = 0;
    private float mCollisionResolverIntervalCounter = 1;
    private float mCollisionSpringConstant;
    private float mCollisionSpringDamping;
    private int mDistanceMode = DISTANCE_MODE_FIXED;
    private float mMinimumDistance;
    private final Random mRandom;
    private final PVector mResolveSamePosition;
    private ResolverType mResolverType;

    public CollisionManager() {
        this(new Physics());
    }

    public CollisionManager(final Physics pPhysics) {
        mCollisionPhysics = pPhysics;
        mResolveSamePosition = new PVector(1, 1, 1);
        mCollisionSpringConstant = 20.0f;
        mCollisionSpringDamping = 1.0f;
        mMinimumDistance = 20;
        mResolverType = ResolverType.SPRING;
        mRandom = new Random();
    }

    public void distancemode(int pDistanceMode) {
        mDistanceMode = pDistanceMode;
    }

    public void setResolverType(ResolverType pResolverType) {
        mResolverType = pResolverType;
    }

    public PVector resolveSamePosition() {
        return mResolveSamePosition;
    }

    public void springDamping(float pSpringDamping) {
        mCollisionSpringDamping = pSpringDamping;
    }

    public float springDamping() {
        return mCollisionSpringDamping;
    }

    public void springConstant(float pSpringConstant) {
        mCollisionSpringConstant = pSpringConstant;
    }

    public float springConstant() {
        return mCollisionSpringConstant;
    }

    public void minimumDistance(float pMinimumDistance) {
        mMinimumDistance = pMinimumDistance;
    }

    public float minimumDistance() {
        return mMinimumDistance;
    }

    public Physics collision() {
        return mCollisionPhysics;
    }

    public ArrayList<Force> collision_forces() {
        return mCollisionPhysics.forces();
    }

    public void loop(float pDeltaTime) {

//        /* collision resolver */
//        if (mCollisionResolverIntervalCounter > mCollisionResolverInterval) {
//            mCollisionResolverIntervalCounter = 0;
////            createCollisionResolvers();
//        } else {
//            mCollisionResolverIntervalCounter += theDeltaTime;
//        }

        /* physics */
        mCollisionPhysics.step(pDeltaTime);

//        /* remove collision resolver */
//        removeCollisionResolver();
    }

    public void autoloop(float pDeltaTime) {
        /* collision resolver */
        if (mCollisionResolverIntervalCounter > mCollisionResolverInterval) {
            mCollisionResolverIntervalCounter = 0;
//            mCollisionResolverIntervalCounter -= mCollisionResolverInterval;
//            mCollisionResolverIntervalCounter %= mCollisionResolverInterval;
            createCollisionResolvers();
        } else {
            mCollisionResolverIntervalCounter += pDeltaTime;
        }

        /* physics */
        mCollisionPhysics.step(pDeltaTime);

        /* remove collision resolver */
        removeCollisionResolver();
    }

    /**
     * removes all collision resolvers ( springs or sticks ) created by this manager. forces and constraints added to
     * the collision physics by other means are kept.
     */
    public void removeCollisionResolver() {
        if (!mResolverForces.isEmpty()) {
            final Set<Force> mRemove = Collections.newSetFromMap(new IdentityHashMap<>());
            mRemove.addAll(mResolverForces);
            synchronized (mCollisionPhysics.forces()) {
                mCollisionPhysics.forces().removeIf(mRemove::contains);
            }
            mResolverForces.clear();
        }
        if (!mResolverConstraints.isEmpty()) {
            final Set<Constraint> mRemove = Collections.newSetFromMap(new IdentityHashMap<>());
            mRemove.addAll(mResolverConstraints);
            synchronized (mCollisionPhysics.constraints()) {
                mCollisionPhysics.constraints().removeIf(mRemove::contains);
            }
            mResolverConstraints.clear();
        }
    }

    public void createCollisionResolvers() {
        for (int i = 0; i < mCollisionPhysics.particles().size(); i++) {
            createCollisionResolver(mCollisionPhysics.particles().get(i), i);
        }
    }

    /**
     * creates collision resolvers for all cubicle particles, searching all cells within collision distance. call
     * <code>CubicleWorld.update()</code> first so that the cells are up to date. assumes the rotation of the world's
     * transform does not scale.
     *
     * @param pWorld world containing the particles
     */
    public void createCollisionResolvers(final CubicleWorld pWorld) {
        final float mReach = getCollisionReach();
        final int mReachX = cellsWithinReach(mReach, pWorld.cellscale().x);
        final int mReachY = cellsWithinReach(mReach, pWorld.cellscale().y);
        final int mReachZ = cellsWithinReach(mReach, pWorld.cellscale().z);
        /* particles already handled as primary particle. used to create each pair only once. */
        final Set<Particle> mVisited = Collections.newSetFromMap(new IdentityHashMap<>());
        for (int i = 0; i < mCollisionPhysics.particles().size(); i++) {
            final Particle mParticle = mCollisionPhysics.particles().get(i);
            if (mParticle instanceof CubicleParticle) {
                createCollisionResolver(pWorld, (CubicleParticle) mParticle, mVisited, mReachX, mReachY, mReachZ);
                mVisited.add(mParticle);
            }
        }
    }

    private float getCollisionReach() {
        if (mDistanceMode != DISTANCE_MODE_RADIUS) {
            return mMinimumDistance;
        }
        float mMaximumRadius = 0;
        for (int i = 0; i < mCollisionPhysics.particles().size(); i++) {
            mMaximumRadius = Math.max(mMaximumRadius, mCollisionPhysics.particles().get(i).radius());
        }
        return 2 * mMaximumRadius;
    }

    private static int cellsWithinReach(final float pReach, final float pCellScale) {
        return pCellScale == 0 ? 0 : (int) Math.ceil(pReach / Math.abs(pCellScale));
    }

    private boolean ignore(Particle pParticle) {
        return HINT_IGNORE_STILL_OR_FIXED && (pParticle.fixed() || pParticle.still());
    }

    private void createCollisionResolver(final Particle pParticle, final int pStart) {
        for (int j = pStart; j < mCollisionPhysics.particles().size(); j++) {
            final Particle mOprParticle = mCollisionPhysics.particles().get(j);
            if (pParticle != mOprParticle) {
                checkPair(pParticle, mOprParticle);
            }
        }
    }

    private void createCollisionResolver(final CubicleWorld pWorld,
                                         final CubicleParticle pParticle,
                                         final Set<Particle> pVisited,
                                         final int pReachX,
                                         final int pReachY,
                                         final int pReachZ) {
        final Vector3i mIndex = pWorld.worldposition2index(pParticle.position());
        final CubicleAtom[][][] mAtoms = pWorld.getDataRef();
        final int mSizeX = mAtoms.length;
        final int mSizeY = mSizeX > 0 ? mAtoms[0].length : 0;
        final int mSizeZ = mSizeY > 0 ? mAtoms[0][0].length : 0;
        /* long, because the index of a particle far outside of the world can be close to the limits of int */
        final long mMinX = (long) mIndex.x - pReachX;
        final long mMaxX = (long) mIndex.x + pReachX;
        final long mMinY = (long) mIndex.y - pReachY;
        final long mMaxY = (long) mIndex.y + pReachY;
        final long mMinZ = (long) mIndex.z - pReachZ;
        final long mMaxZ = (long) mIndex.z + pReachZ;
        for (int x = (int) Math.max(mMinX, 0); x <= Math.min(mMaxX, mSizeX - 1); x++) {
            for (int y = (int) Math.max(mMinY, 0); y <= Math.min(mMaxY, mSizeY - 1); y++) {
                for (int z = (int) Math.max(mMinZ, 0); z <= Math.min(mMaxZ, mSizeZ - 1); z++) {
                    checkEntities(mAtoms[x][y][z], pParticle, pVisited);
                }
            }
        }
        /* particles outside of the world can only be close if the reach extends past the border of the world */
        if (mMinX < 0 || mMaxX >= mSizeX || mMinY < 0 || mMaxY >= mSizeY || mMinZ < 0 || mMaxZ >= mSizeZ) {
            checkEntities(pWorld.getOffWorldAtom(), pParticle, pVisited);
        }
    }

    private void checkEntities(final CubicleAtom pAtom, final CubicleParticle pParticle, final Set<Particle> pVisited) {
        for (final ICubicleEntity mEntity : pAtom.data()) {
            if (mEntity instanceof Particle) {
                final Particle mOprParticle = (Particle) mEntity;
                if (pParticle != mOprParticle && !pVisited.contains(mOprParticle)) {
                    checkPair(pParticle, mOprParticle);
                }
            }
        }
    }

    private void checkPair(final Particle pParticle, final Particle pOprParticle) {
        /* only skip pair if *both* particles can be ignored */
        if (ignore(pParticle) && ignore(pOprParticle)) {
            return;
        }
        if (pParticle.fixed() && pOprParticle.fixed()) {
            return;
        }
        final float mDistance = Util.distance(pParticle.position(), pOprParticle.position());
        final float mMinimumDistance = getMinimumDistance(pParticle, pOprParticle);
        if (mDistance < mMinimumDistance) {
            /*
             * because of the way we handle the collision resolver creation there is no need to check for multiple
             * spring connections.
             */
            if (mResolverType == ResolverType.SPRING) {
                final Spring mSpring = new Spring(pParticle,
                                                  pOprParticle,
                                                  mCollisionSpringConstant,
                                                  mCollisionSpringDamping,
                                                  mMinimumDistance);
                mCollisionPhysics.add(mSpring);
                mResolverForces.add(mSpring);
            } else if (mResolverType == ResolverType.STICK) {
                final Stick mStick = new Stick(pParticle, pOprParticle, mMinimumDistance);
                mCollisionPhysics.add(mStick);
                mResolverConstraints.add(mStick);
            }
            pParticle.tag(true);
            pOprParticle.tag(true);
            /* hack to prevent particles from being in the same place */
            if (mDistance < EPSILON) {
                pOprParticle.position().x += mRandom.getFloat(mResolveSamePosition.x * -0.5f,
                                                              mResolveSamePosition.x * 0.5f);
                pOprParticle.position().y += mRandom.getFloat(mResolveSamePosition.y * -0.5f,
                                                              mResolveSamePosition.y * 0.5f);
                pOprParticle.position().z += mRandom.getFloat(mResolveSamePosition.z * -0.5f,
                                                              mResolveSamePosition.z * 0.5f);
            }
        }
    }

    private float getMinimumDistance(Particle pParticle, Particle mOprParticle) {
        final float mTmpMinimumDistance;

        if (mDistanceMode == DISTANCE_MODE_RADIUS) {
            mTmpMinimumDistance = pParticle.radius() + mOprParticle.radius();
        } else {
            mTmpMinimumDistance = mMinimumDistance;
        }
        return mTmpMinimumDistance;
    }

    public static class CollisionSpring extends Spring {

        public CollisionSpring(Particle pA, Particle pB) {
            super(pA, pB, 2.0f, 0.1f, distance(pA.position(), pB.position()));
        }

        public CollisionSpring(Particle pA, Particle pB, final float pSpringConstant, final float pSpringDamping) {
            super(pA, pB, pSpringConstant, pSpringDamping, distance(pA.position(), pB.position()));
        }

        public CollisionSpring(final Particle pA,
                               final Particle pB,
                               final float pSpringConstant,
                               final float pSpringDamping,
                               final float pRestLength) {
            super(pA, pB, pSpringConstant, pSpringDamping, pRestLength);
        }

        public void apply(final float pDeltaTime, final Physics pParticleSystem) {
            //@TODO("why is this semi-redundant to `Spring`?")
            if (!mA.fixed() || !mB.fixed()) {
                float a2bX = mA.position().x - mB.position().x;
                float a2bY = mA.position().y - mB.position().y;
                float a2bZ = mA.position().z - mB.position().z;
                final float mInversDistance = Util.fastInverseSqrt(a2bX * a2bX + a2bY * a2bY + a2bZ * a2bZ);
                final float mDistance = 1.0F / mInversDistance;

                if (mDistance < mRestLength) {
                    if (mDistance == 0.0F) {
                        a2bX = 0.0F;
                        a2bY = 0.0F;
                        a2bZ = 0.0F;
                    } else {
                        a2bX *= mInversDistance;
                        a2bY *= mInversDistance;
                        a2bZ *= mInversDistance;
                    }

                    final float mSpringForce = -(mDistance - mRestLength) * mSpringConstant;
                    final float Va2bX = mA.velocity().x - mB.velocity().x;
                    final float Va2bY = mA.velocity().y - mB.velocity().y;
                    final float Va2bZ = mA.velocity().z - mB.velocity().z;
                    final float mDampingForce = -mSpringDamping * (a2bX * Va2bX + a2bY * Va2bY + a2bZ * Va2bZ);
                    final float r = mSpringForce + mDampingForce;
                    a2bX *= r;
                    a2bY *= r;
                    a2bZ *= r;
                    if (!mA.fixed()) {
                        mA.force().add(a2bX, a2bY, a2bZ);
                    }

                    if (!mB.fixed()) {
                        mB.force().add(-a2bX, -a2bY, -a2bZ);
                    }
                }
            }
        }
    }

    public static class CollisionStick extends Stick {

        public CollisionStick(Particle pA, Particle pB) {
            super(pA, pB);
        }

        public CollisionStick(final Particle pA, final Particle pB, final float pRestLength) {
            super(pA, pB, pRestLength);
        }

        public void apply(Physics pParticleSystem) {
            //@TODO("why is this semi-redundant to `Stick`?")
            if (!mA.fixed() || !mB.fixed()) {
                PVector.sub(mA.position(), mB.position(), mTempDistanceVector);
                final float mDistanceSquared = lengthSquared(mTempDistanceVector);
                if (mDistanceSquared > 0) {
                    if (mDistanceSquared < mRestLength * mRestLength) {
                        final float mDistance = (float) Math.sqrt(mDistanceSquared);
                        final float mDifference = mRestLength - mDistance;
                        if (mDifference > EPSILON || mDifference < -EPSILON) {
                            if (!mOneWay) {
                                final float mDifferenceScale = 0.5f * mDifference / mDistance;
                                PVector.mult(mTempDistanceVector, mDifferenceScale, mTempVector);
                                mA.position().add(mTempVector);
                                mB.position().sub(mTempVector);
                            } else {
                                final float mDifferenceScale = mDifference / mDistance;
                                PVector.mult(mTempDistanceVector, mDifferenceScale, mTempVector);
                                mB.position().sub(mTempVector);
                            }
                        }
                    }
                } else {
                    mB.position().set(mA.position());
                    mB.position().x += mRestLength;
                    return;
                }
                /* only resolve approaching particles in contact. separating particles keep their velocity. */
                if (updatesVelocity(pParticleSystem) && isApproaching(mA, mB)) {
                    final float dX = mA.position().x - mB.position().x;
                    final float dY = mA.position().y - mB.position().y;
                    final float dZ = mA.position().z - mB.position().z;
                    if (dX * dX + dY * dY + dZ * dZ <= mRestLength * mRestLength + EPSILON) {
                        removeAxialVelocity(mA, mB, mOneWay);
                    }
                }
            }
        }

        private static boolean isApproaching(Particle pA, Particle pB) {
            final float dX = pA.position().x - pB.position().x;
            final float dY = pA.position().y - pB.position().y;
            final float dZ = pA.position().z - pB.position().z;
            final float vX = pA.velocity().x - pB.velocity().x;
            final float vY = pA.velocity().y - pB.velocity().y;
            final float vZ = pA.velocity().z - pB.velocity().z;
            return dX * vX + dY * vY + dZ * vZ < 0;
        }
    }
}
