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

package teilchen;

import processing.core.PVector;
import teilchen.constraint.Collider;
import teilchen.constraint.Constraint;
import teilchen.force.Force;
import teilchen.force.Spring;
import teilchen.force.ViscousDrag;
import teilchen.integration.Integrator;
import teilchen.integration.Midpoint;
import teilchen.integration.Verlet;
import teilchen.util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

/**
 * particle system that holds particles, forces and constraints and advances them in time.
 * <p>
 * thread safety: <code>step</code> holds the lock of the particle list for the whole step. adding and removing
 * particles, forces and constraints locks the respective list, so these methods may be called from another thread.
 * iterating the lists returned by <code>particles()</code>, <code>forces()</code> or <code>constraints()</code> from
 * another thread must be done while holding the lock of that list ( <code>synchronized (physics.particles()) {...}
 * </code> ). modifying the particle list from inside a force or constraint during <code>step</code> is not
 * supported.
 */
public class Physics {

    public static       boolean                VERBOSE                                 = false;
    public static final float                  EPSILON                                 = 0.001f;
    public static       boolean                HINT_UPDATE_OLD_POSITION                = true;
    private static final AtomicLong            oID                                     = new AtomicLong(-1);
    public              boolean                HINT_OPTIMIZE_STILL                     = true;
    public              boolean                HINT_RECOVER_NAN                        = true;
    public              boolean                HINT_REMOVE_DEAD                        = true;
    public              boolean                HINT_SET_VELOCITY_FROM_PREVIOUS_POSITION = true;
    private final       ArrayList<Constraint>  mConstraints;
    private final       ArrayList<Force>       mForces;
    private             Integrator             mIntegrator;
    private final       ArrayList<Particle>    mParticles;
    private             long                   mStepCount                              = 0;
    private final       ArrayList<Collider>    mColliders                              = new ArrayList<>();
    private final       IdentityHashMap<Particle, PVector[]> mCollisionOrigins     = new IdentityHashMap<>();

    public Physics() {
        mParticles   = new ArrayList<>();
        mForces      = new ArrayList<>();
        mConstraints = new ArrayList<>();
        mIntegrator  = new Midpoint();
    }

    public static long getUniqueID() {
        return oID.incrementAndGet();
    }

    private static void reportException(String pLocation, Exception pException) {
        System.err.println("### WARNING @ Physics." + pLocation + " / " + pException);
        if (VERBOSE) {
            pException.printStackTrace();
        }
    }

    /* particles */
    public boolean add(Particle pParticle, boolean pPreventDuplicates) {
        synchronized (mParticles) {
            if (pPreventDuplicates) {
                for (final Particle p : mParticles) {
                    if (p == pParticle) {
                        return false;
                    }
                }
            }
            mParticles.add(pParticle);
        }
        return true;
    }

    public void add(Particle pParticle) {
        synchronized (mParticles) {
            mParticles.add(pParticle);
        }
    }

    public void add(Collection<? extends Particle> pParticles) {
        synchronized (mParticles) {
            mParticles.addAll(pParticles);
        }
    }

    public void remove(Particle pParticle) {
        synchronized (mParticles) {
            mParticles.remove(pParticle);
        }
    }

    public void remove(Collection<? extends Particle> pParticles) {
        synchronized (mParticles) {
            mParticles.removeAll(pParticles);
        }
    }

    public ArrayList<Particle> particles() {
        return mParticles;
    }

    public Particle particles(final int pIndex) {
        return mParticles.get(pIndex);
    }

    /**
     * creates a particle at a copy of the specified position. use <code>Particle.setPositionRef(PVector)</code> to
     * share a position vector.
     */
    public BasicParticle makeParticle(final PVector pPosition) {
        BasicParticle mParticle = makeParticle();
        mParticle.position().set(pPosition);
        mParticle.old_position().set(mParticle.position());
        return mParticle;
    }

    public BasicParticle makeParticle() {
        BasicParticle mParticle = new BasicParticle();
        add(mParticle);
        return mParticle;
    }

    public BasicParticle makeParticle(final float x, final float y) {
        BasicParticle mParticle = makeParticle();
        mParticle.position().set(x, y);
        mParticle.old_position().set(mParticle.position());
        return mParticle;
    }

    public BasicParticle makeParticle(final float x, final float y, final float z) {
        BasicParticle mParticle = makeParticle();
        mParticle.position().set(x, y, z);
        mParticle.old_position().set(mParticle.position());
        return mParticle;
    }

    public BasicParticle makeParticle(final float x, final float y, final float z, final float pMass) {
        BasicParticle mParticle = makeParticle();
        mParticle.position().set(x, y, z);
        mParticle.mass(pMass);
        mParticle.old_position().set(mParticle.position());
        return mParticle;
    }

    /**
     * creates a particle at a copy of the specified position. use <code>Particle.setPositionRef(PVector)</code> to
     * share a position vector.
     */
    public BasicParticle makeParticle(final PVector pPosition, final float pMass) {
        BasicParticle mParticle = makeParticle();
        mParticle.position().set(pPosition);
        mParticle.old_position().set(mParticle.position());
        mParticle.mass(pMass);
        return mParticle;
    }

    public <T extends Particle> T makeParticle(Class<T> pParticleClass) {
        T mParticle;
        try {
            mParticle = pParticleClass.getDeclaredConstructor().newInstance();
            add(mParticle);
        } catch (Exception ex) {
            reportException("makeParticle", ex);
            mParticle = null;
        }
        return mParticle;
    }

    public void removeTags() {
        synchronized (mParticles) {
            for (final Particle p : mParticles) {
                p.tag(false);
            }
        }
    }

    /* forces */
    public boolean add(Spring pSpring, boolean pPreventDuplicates) {
        synchronized (mForces) {
            if (pPreventDuplicates) {
                for (final Force f : mForces) {
                    if (f instanceof Spring) {
                        final Spring s = (Spring) f;
                        if (s == pSpring || (s.a() == pSpring.a() && s.b() == pSpring.b()) || (s.b() == pSpring.a() && s.a() == pSpring.b())) {
                            return false;
                        }
                    }
                }
            }
            mForces.add(pSpring);
        }
        return true;
    }

    public void add(Force pForce) {
        warnIfIneffective(pForce);
        synchronized (mForces) {
            mForces.add(pForce);
        }
    }

    public void addForces(final ArrayList<? extends Force> pForces) {
        for (final Force f : pForces) {
            warnIfIneffective(f);
        }
        synchronized (mForces) {
            mForces.addAll(pForces);
        }
    }

    public void remove(Force pForce) {
        synchronized (mForces) {
            mForces.remove(pForce);
        }
    }

    public ArrayList<Force> forces() {
        return mForces;
    }

    public Force forces(final int pIndex) {
        return mForces.get(pIndex);
    }

    private void warnIfIneffective(Force pForce) {
        if (pForce instanceof ViscousDrag && mIntegrator instanceof Verlet) {
            System.err.println("### WARNING / `ViscousDrag` has no effect with `Verlet` integration. use " +
                               "`Verlet.damping(float pDamping)` instead.");
        }
    }

    /**
     * accumulates the inner forces ( e.g behaviors ) of all non-fixed particles.
     */
    public void accumulateInnerForces(final float pDeltaTime) {
        try {
            synchronized (mParticles) {
                for (final Particle p : mParticles) {
                    if (p != null && !p.fixed()) {
                        p.accumulateInnerForce(pDeltaTime);
                    }
                }
            }
        } catch (Exception ex) {
            reportException("accumulateInnerForces", ex);
        }
    }

    /**
     * applies inner forces of particles and all active forces.
     */
    public void applyForces(final float pDeltaTime) {
        applyForces(pDeltaTime, true);
    }

    /**
     * applies all active forces. inner forces of particles ( e.g behaviors ) are only accumulated if
     * <code>pAccumulateInnerForces</code> is true. multi-stage integrators accumulate inner forces once per step and
     * then call this method with <code>false</code> for each stage.
     */
    public void applyForces(final float pDeltaTime, final boolean pAccumulateInnerForces) {
        if (pAccumulateInnerForces) {
            accumulateInnerForces(pDeltaTime);
        }
        try {
            synchronized (mForces) {
                for (final Force f : mForces) {
                    if (f != null && f.active()) {
                        f.apply(pDeltaTime, this);
                    }
                }
            }
        } catch (Exception ex) {
            reportException("applyForces", ex);
        }
    }

    public <T extends Force> T makeForce(Class<T> pForceClass) {
        T mForce;
        try {
            mForce = pForceClass.getDeclaredConstructor().newInstance();
            add(mForce);
        } catch (Exception ex) {
            reportException("makeForce", ex);
            mForce = null;
        }
        return mForce;
    }

    public Spring makeSpring(final Particle pA, final Particle pB) {
        Spring mSpring = new Spring(pA, pB);
        add(mSpring);
        return mSpring;
    }

    public Spring makeSpring(final Particle pA, final Particle pB, final float pRestLength) {
        Spring mSpring = new Spring(pA, pB, pRestLength);
        add(mSpring);
        return mSpring;
    }

    public Spring makeSpring(final Particle pA,
                             final Particle pB,
                             final float pSpringConstant,
                             final float pSpringDamping) {
        Spring mSpring = new Spring(pA, pB, pSpringConstant, pSpringDamping);
        add(mSpring);
        return mSpring;
    }

    public Spring makeSpring(final Particle pA,
                             final Particle pB,
                             final float pSpringConstant,
                             final float pSpringDamping,
                             final float pRestLength) {
        Spring mSpring = new Spring(pA, pB, pSpringConstant, pSpringDamping, pRestLength);
        add(mSpring);
        return mSpring;
    }

    /* constraints */
    public void add(final Constraint pConstraint) {
        synchronized (mConstraints) {
            mConstraints.add(pConstraint);
        }
    }

    public void addConstraints(final ArrayList<? extends Constraint> pConstraints) {
        synchronized (mConstraints) {
            mConstraints.addAll(pConstraints);
        }
    }

    public void remove(final Constraint pConstraint) {
        synchronized (mConstraints) {
            mConstraints.remove(pConstraint);
        }
    }

    public ArrayList<Constraint> constraints() {
        return mConstraints;
    }

    public Constraint constraints(final int pIndex) {
        return mConstraints.get(pIndex);
    }

    /* integration */
    public void setIntegratorRef(Integrator pIntegrator) {
        mIntegrator = pIntegrator;
        synchronized (mForces) {
            for (final Force f : mForces) {
                warnIfIneffective(f);
            }
        }
    }

    public Integrator getIntegrator() {
        return mIntegrator;
    }

    public void step(final float pDeltaTime, final int pIterations) {
        for (int i = 0; i < pIterations; i++) {
            step(pDeltaTime / (float) pIterations);
        }
    }

    /**
     * number of times <code>step(float)</code> has been called. sub-steps count individually.
     */
    public long stepCount() {
        return mStepCount;
    }

    public void step(final float pDeltaTime) {
        synchronized (mParticles) {
            mStepCount++;
            handleNewParticles();
            handleForces();
            integrate(pDeltaTime);
            handleParticles(pDeltaTime);
            handleConstraints();
            postHandleParticles(pDeltaTime);
        }
    }

    public void purge() {
        synchronized (mForces) {
            mForces.removeIf(Force::dead);
        }
        synchronized (mParticles) {
            mParticles.removeIf(Particle::dead);
        }
        synchronized (mConstraints) {
            mConstraints.removeIf(Constraint::dead);
        }
    }

    /**
     * particles that have not been stepped yet ( age 0 ) may have been positioned after creation without updating
     * their old position. for integrators that do not derive velocity from the old position ( i.e not
     * <code>Verlet</code> ) the old position is set to the current position, so that colliders and the fixed
     * particle velocity see a valid previous position.
     */
    protected void handleNewParticles() {
        if (!HINT_UPDATE_OLD_POSITION || mIntegrator instanceof Verlet) {
            return;
        }
        for (final Particle mParticle : mParticles) {
            if (mParticle.age() == 0) {
                mParticle.old_position().set(mParticle.position());
            }
        }
    }

    /**
     * start of the path a particle traveled during the current step as seen by colliders. this is
     * <code>old_position()</code> unless a collider already resolved a collision for the particle in this step, in
     * which case it is the contact point. used by colliders so that a particle deflected by one collider is not
     * deflected back by another ( e.g at seams between triangles ).
     */
    public PVector collisionOrigin(Particle pParticle) {
        final PVector[] mOrigin = mCollisionOrigins.get(pParticle);
        return mOrigin != null ? mOrigin[0] : pParticle.old_position();
    }

    /**
     * sets the start of the path for subsequent colliders in this step. see <code>collisionOrigin(Particle)</code>.
     */
    public void collisionOrigin(Particle pParticle, float x, float y, float z) {
        PVector[] mOrigin = mCollisionOrigins.get(pParticle);
        if (mOrigin == null) {
            mOrigin = new PVector[]{new PVector()};
            mCollisionOrigins.put(pParticle, mOrigin);
        }
        mOrigin[0].set(x, y, z);
    }

    protected synchronized void handleConstraints() {
        mCollisionOrigins.clear();
        synchronized (mConstraints) {
            /* colliders are applied jointly at the position of the first collider */
            mColliders.clear();
            for (final Constraint c : mConstraints) {
                if (c instanceof Collider) {
                    mColliders.add((Collider) c);
                }
            }
            boolean mCollidersApplied = false;
            final Iterator<Constraint> i = mConstraints.iterator();
            while (i.hasNext()) {
                final Constraint mConstraint = i.next();
                if (mConstraint instanceof Collider) {
                    if (!mCollidersApplied) {
                        Collider.applyAll(this, mColliders);
                        mCollidersApplied = true;
                    }
                } else {
                    mConstraint.apply(this);
                }
                if (HINT_REMOVE_DEAD) {
                    if (mConstraint.dead()) {
                        i.remove();
                    }
                }
            }
        }
    }

    protected synchronized void handleForces() {
        if (HINT_REMOVE_DEAD) {
            synchronized (mForces) {
                mForces.removeIf(Force::dead);
            }
        }
    }

    protected synchronized void handleParticles(float pDeltaTime) {
        try {
            synchronized (mParticles) {
                final Iterator<Particle> i = mParticles.iterator();
                while (i.hasNext()) {
                    final Particle mParticle = i.next();
                    /* clear force */
                    mParticle.force().set(0, 0, 0);
                    /* age particle */
                    mParticle.age(mParticle.age() + pDeltaTime);
                    /* remove dead */
                    if (HINT_REMOVE_DEAD) {
                        if (mParticle.dead()) {
                            i.remove();
                            continue;
                        }
                    }
                    /* recover NAN */
                    if (HINT_RECOVER_NAN) {
                        if (Util.isNaN(mParticle.position())) {
                            if (Util.isNaN(mParticle.old_position())) {
                                mParticle.position().set(0, 0, 0);
                            } else {
                                mParticle.position().set(mParticle.old_position());
                            }
                        }
                        if (Util.isNaN(mParticle.velocity())) {
                            mParticle.velocity().set(0, 0, 0);
                        }
                    }
                    /* still */
                    if (HINT_OPTIMIZE_STILL) {
                        final float mSpeedSquared = Util.lengthSquared(mParticle.velocity());
                        mParticle.still(mSpeedSquared < EPSILON);
                    }
                }
            }
        } catch (Exception ex) {
            reportException("handleParticles", ex);
        }
    }

    protected synchronized void integrate(float pDeltaTime) {
        mIntegrator.step(pDeltaTime, this);
    }

    protected synchronized void postHandleParticles(float pDeltaTime) {
        if (HINT_SET_VELOCITY_FROM_PREVIOUS_POSITION || HINT_UPDATE_OLD_POSITION) {
            /* `Verlet` derives velocity from `old_position` and maintains it itself for non-fixed particles */
            final boolean mIntegratorOwnsOldPosition = mIntegrator instanceof Verlet;
            try {
                synchronized (mParticles) {
                    for (final Particle mParticle : mParticles) {
                        if (HINT_SET_VELOCITY_FROM_PREVIOUS_POSITION && pDeltaTime > 0) {
                            /*
                             * fixed particles are not integrated. derive velocity from movement ( e.g by mouse ).
                             * skip the first step of a particle ( age was just incremented from 0 ): its old
                             * position is not valid yet if the position was set after creation, which would
                             * produce a huge velocity spike.
                             */
                            if (mParticle.fixed() && mParticle.age() > pDeltaTime) {
                                final PVector p = mParticle.position();
                                final PVector o = mParticle.old_position();
                                final float   s = 1.0f / pDeltaTime;
                                mParticle.velocity().set((p.x - o.x) * s, (p.y - o.y) * s, (p.z - o.z) * s);
                            }
                        }
                        if (HINT_UPDATE_OLD_POSITION) {
                            if (!mIntegratorOwnsOldPosition || mParticle.fixed()) {
                                mParticle.old_position().set(mParticle.position());
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                reportException("postHandleParticles", ex);
            }
        }
    }
}
