/*
 * Teilchen
 *
 * Copyright (C) 2020
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
import teilchen.constraint.IConstraint;
import teilchen.force.IForce;
import teilchen.force.Spring;
import teilchen.force.ViscousDrag;
import teilchen.integration.IIntegrator;
import teilchen.integration.Midpoint;
import teilchen.integration.Verlet;
import teilchen.util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Physics {

    public static final float EPSILON = 0.001f;
    public static boolean HINT_UPDATE_OLD_POSITION = true;
    public boolean HINT_OPTIMIZE_STILL = true;
    public boolean HINT_RECOVER_NAN = true;
    public boolean HINT_REMOVE_DEAD = true;
    public boolean HINT_SET_VELOCITY_FROM_PREVIOUS_POSTION = true;
    private final ArrayList<Particle> mParticles;
    private final ArrayList<IForce> mForces;
    private final ArrayList<IConstraint> mConstraints;
    private IIntegrator mIntegrator;

    public Physics() {
        mParticles = new ArrayList<>();
        mForces = new ArrayList<>();
        mConstraints = new ArrayList<>();
        mIntegrator = new Midpoint();
    }

    /* particles */
    public boolean add(Particle pParticle, boolean pPreventDuplicates) {
        if (pPreventDuplicates) {
            for (Particle p : mParticles) {
                if (p == pParticle) {
                    return false;
                }
            }
            mParticles.add(pParticle);
        }
        return true;
    }

    public void add(Particle pParticle) {
        mParticles.add(pParticle);
    }

    public void add(Collection<? extends Particle> pParticles) {
        mParticles.addAll(pParticles);
    }

    public void remove(Particle pParticle) {
        mParticles.remove(pParticle);
    }

    public void remove(Collection<? extends Particle> pParticles) {
        mParticles.removeAll(pParticles);
    }

    public ArrayList<Particle> particles() {
        return mParticles;
    }

    public Particle particles(final int pIndex) {
        return mParticles.get(pIndex);
    }

    public BasicParticle makeParticle(final PVector pPosition) {
        BasicParticle myParticle = makeParticle();
        myParticle.setPositionRef(pPosition);
        myParticle.old_position().set(myParticle.position());
        return myParticle;
    }

    public BasicParticle makeParticle() {
        BasicParticle myParticle = new BasicParticle();
        mParticles.add(myParticle);
        return myParticle;
    }

    public BasicParticle makeParticle(final float x, final float y) {
        BasicParticle myParticle = makeParticle();
        myParticle.position().set(x, y);
        myParticle.old_position().set(myParticle.position());
        return myParticle;
    }

    public BasicParticle makeParticle(final float x, final float y, final float z) {
        BasicParticle myParticle = makeParticle();
        myParticle.position().set(x, y, z);
        myParticle.old_position().set(myParticle.position());
        return myParticle;
    }

    public BasicParticle makeParticle(final float x, final float y, final float z, final float pMass) {
        BasicParticle myParticle = makeParticle();
        myParticle.position().set(x, y, z);
        myParticle.mass(pMass);
        myParticle.old_position().set(myParticle.position());
        return myParticle;
    }

    public BasicParticle makeParticle(final PVector pPosition, final float pMass) {
        BasicParticle myParticle = makeParticle();
        myParticle.setPositionRef(pPosition);
        myParticle.old_position().set(myParticle.position());
        myParticle.mass(pMass);
        return myParticle;
    }

    public <T extends Particle> T makeParticle(Class<T> pParticleClass) {
        T myParticle;
        try {
            myParticle = pParticleClass.newInstance();
            mParticles.add(myParticle);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex);
            myParticle = null;
        }
        return myParticle;
    }

    public void removeTags() {
        for (final Particle myParticle : mParticles) {
            myParticle.tag(false);
        }
    }

    /* forces */
    public boolean add(Spring pSpring, boolean pPreventDuplicates) {
        if (pPreventDuplicates) {
            for (IForce f : mForces) {
                if (f instanceof Spring) {
                    Spring s = (Spring) f;
                    if (s == pSpring ||
                        (s.a() == pSpring.a() && s.b() == pSpring.b()) ||
                        (s.b() == pSpring.a() && s.a() == pSpring.b())) {
                        return false;
                    }
                }
            }
            mForces.add(pSpring);
        }
        return true;
    }

    public void add(IForce pForce) {
        if (pForce instanceof ViscousDrag && mIntegrator instanceof Verlet) {
            System.err.println("### WARNING / `ViscousDrag` has no effect with `Verlet` " +
                               "integration. use `Verlet.damping(float pDamping)` instead.");
        }
        mForces.add(pForce);
    }

    public void addForces(final ArrayList<? extends IForce> pForces) {
        mForces.addAll(pForces);
    }

    public void remove(IForce pForce) {
        mForces.remove(pForce);
    }

    public ArrayList<IForce> forces() {
        return mForces;
    }

    public IForce forces(final int pIndex) {
        return mForces.get(pIndex);
    }

    public void applyForces(final float pDeltaTime) {
        /* accumulate inner forces */
        synchronized (mParticles) {
            for (Particle myParticle : mParticles) {
                if (!myParticle.fixed()) {
                    /* accumulate inner forces */
                    myParticle.accumulateInnerForce(pDeltaTime);
                }
            }
        }

        /* add new forces to each particle */
        synchronized (mForces) {
            for (IForce mForce : mForces) {
                if (mForce.active()) {
                    mForce.apply(pDeltaTime, this);
                }
            }
        }
    }

    public <T extends IForce> T makeForce(Class<T> pForceClass) {
        T myForce;
        try {
            myForce = pForceClass.newInstance();
            mForces.add(myForce);
        } catch (Exception ex) {
            myForce = null;
        }
        return myForce;
    }

    public Spring makeSpring(final Particle pA, final Particle pB) {
        Spring mySpring = new Spring(pA, pB);
        mForces.add(mySpring);
        return mySpring;
    }

    public Spring makeSpring(final Particle pA, final Particle pB, final float pRestLength) {
        Spring mySpring = new Spring(pA, pB, pRestLength);
        mForces.add(mySpring);
        return mySpring;
    }

    public Spring makeSpring(final Particle pA,
                             final Particle pB,
                             final float pSpringConstant,
                             final float pSpringDamping) {
        Spring mySpring = new Spring(pA, pB, pSpringConstant, pSpringDamping);
        mForces.add(mySpring);
        return mySpring;
    }

    public Spring makeSpring(final Particle pA,
                             final Particle pB,
                             final float pSpringConstant,
                             final float pSpringDamping,
                             final float pRestLength) {
        Spring mySpring = new Spring(pA, pB, pSpringConstant, pSpringDamping, pRestLength);
        mForces.add(mySpring);
        return mySpring;
    }

    /* constraints */
    public void add(final IConstraint pConstraint) {
        mConstraints.add(pConstraint);
    }

    public void addConstraints(final ArrayList<? extends IConstraint> pConstraints) {
        mConstraints.addAll(pConstraints);
    }

    public void remove(final IConstraint pConstraint) {
        mConstraints.remove(pConstraint);
    }

    public ArrayList<IConstraint> constraints() {
        return mConstraints;
    }

    public IConstraint constraints(final int pIndex) {
        return mConstraints.get(pIndex);
    }

    /* integration */
    public void setIntegratorRef(IIntegrator pIntegrator) {
        mIntegrator = pIntegrator;
    }

    public IIntegrator getIntegrator() {
        return mIntegrator;
    }

    public void step(final float pDeltaTime, final int pIterations) {
        for (int i = 0; i < pIterations; i++) {
            step(pDeltaTime / (float) pIterations);
        }
    }

    public void step(final float pDeltaTime) {
        handleForces();
        integrate(pDeltaTime);
        handleParticles(pDeltaTime);
        handleConstraints();
        postHandleParticles(pDeltaTime);
    }

    public void purge() {
        synchronized (mForces) {
            final Iterator<IForce> i = mForces.iterator();
            while (i.hasNext()) {
                final IForce f = i.next();
                if (f.dead()) {
                    i.remove();
                }
            }
        }
        synchronized (mParticles) {
            final Iterator<Particle> i = mParticles.iterator();
            while (i.hasNext()) {
                final Particle p = i.next();
                if (p.dead()) {
                    i.remove();
                }
            }
        }
        synchronized (mConstraints) {
            final Iterator<IConstraint> i = mConstraints.iterator();
            while (i.hasNext()) {
                final IConstraint c = i.next();
                if (c.dead()) {
                    i.remove();
                }
            }
        }
    }

    protected synchronized void integrate(float pDeltaTime) {
        mIntegrator.step(pDeltaTime, this);
    }

    protected synchronized void handleForces() {
        if (HINT_REMOVE_DEAD) {
            synchronized (mForces) {
                final Iterator<IForce> i = mForces.iterator();
                while (i.hasNext()) {
                    final IForce mForce = i.next();
                    if (mForce.dead()) {
                        i.remove();
                    }
                }
            }
        }
    }

    protected synchronized void handleConstraints() {
        synchronized (mConstraints) {
            final Iterator<IConstraint> i = mConstraints.iterator();
            while (i.hasNext()) {
                final IConstraint mConstraint = i.next();
                mConstraint.apply(this);
                if (HINT_REMOVE_DEAD) {
                    if (mConstraint.dead()) {
                        i.remove();
                    }
                }
            }
        }
    }

    protected synchronized void handleParticles(float pDeltaTime) {
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
                    final float mySpeed = Util.lengthSquared(mParticle.velocity());
                    mParticle.still(mySpeed > -EPSILON && mySpeed < EPSILON);
                }
            }
        }
    }

    protected synchronized void postHandleParticles(float pDeltaTime) {
        if (HINT_SET_VELOCITY_FROM_PREVIOUS_POSTION || HINT_UPDATE_OLD_POSITION) {
            synchronized (mParticles) {
                for (Particle mParticle : mParticles) {
                    if (HINT_SET_VELOCITY_FROM_PREVIOUS_POSTION) {
                        if (mParticle.fixed()) {
                            mParticle.velocity().set(PVector.sub(mParticle.position(), mParticle.old_position()));
                        }
                    }
                    if (HINT_UPDATE_OLD_POSITION) {
                        mParticle.old_position().set(mParticle.position());
                    }
                }
            }
        }
    }
}
