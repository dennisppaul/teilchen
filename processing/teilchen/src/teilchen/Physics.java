/*
 * Teilchen
 *
 * Copyright (C) 2013
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

import mathematik.Vector3f;

import teilchen.constraint.IConstraint;
import teilchen.force.IForce;
import teilchen.force.Spring;
import teilchen.force.ViscousDrag;
import teilchen.integration.IIntegrator;
import teilchen.integration.Midpoint;
import teilchen.integration.Verlet;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;


public class Physics {

    private final Vector<Particle> _myParticles;

    private final Vector<IForce> mForces;

    private final Vector<IConstraint> _myConstraints;

    private IIntegrator _myIntegrator;

    private static final float EPSILON = 0.001f;

    public boolean HINT_OPTIMIZE_STILL = true;

    public boolean HINT_REMOVE_DEAD = true;

    public boolean HINT_RECOVER_NAN = true;

    public int contraint_iterations_per_steps = 1;

    public static boolean HINT_UPDATE_OLD_POSITION = false;

    public Physics() {
        _myParticles = new Vector<Particle>();
        mForces = new Vector<IForce>();
        _myConstraints = new Vector<IConstraint>();
        _myIntegrator = new Midpoint();
    }


    /* particles */
    public void add(Particle theParticle) {
        _myParticles.add(theParticle);
    }

    public void add(Collection<Particle> theParticles) {
        _myParticles.addAll(theParticles);
    }

    public void remove(Particle theParticle) {
        _myParticles.remove(theParticle);
    }

    public void remove(Collection<Particle> theParticles) {
        _myParticles.removeAll(theParticles);
    }

    public Vector<Particle> particles() {
        return _myParticles;
    }

    public Particle particles(final int theIndex) {
        return _myParticles.get(theIndex);
    }

    public BasicParticle makeParticle() {
        BasicParticle myParticle = new BasicParticle();
        _myParticles.add(myParticle);
        return myParticle;
    }

    public BasicParticle makeParticle(final Vector3f thePosition) {
        BasicParticle myParticle = makeParticle();
        myParticle.setPositionRef(thePosition);
        myParticle.old_position().set(myParticle.position());
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

    public BasicParticle makeParticle(final Vector3f thePosition, final float pMass) {
        BasicParticle myParticle = makeParticle();
        myParticle.setPositionRef(thePosition);
        myParticle.old_position().set(myParticle.position());
        myParticle.mass(pMass);
        return myParticle;
    }

    public <T extends Particle> T makeParticle(Class<T> theParticleClass) {
        T myParticle;
        try {
            myParticle = theParticleClass.newInstance();
            _myParticles.add(myParticle);
        } catch (Exception ex) {
            System.err.println(ex);
            myParticle = null;
        }
        return myParticle;
    }

    public void removeTags() {
        for (final Particle myParticle : _myParticles) {
            myParticle.tag(false);
        }
    }


    /* forces */
    public void add(IForce theForce) {
        if (theForce instanceof ViscousDrag && _myIntegrator instanceof Verlet) {
            System.err.println(
                    "### WARNING / 'viscous drag' might have no effect with 'verlet' integration. use 'Verlet.damping(float theDamping)' instead.");
        }
        mForces.add(theForce);
    }

    public void addForces(final Vector<IForce> theForces) {
        mForces.addAll(theForces);
    }

    public void remove(IForce theForce) {
        mForces.remove(theForce);
    }

    public Vector<IForce> forces() {
        return mForces;
    }

    public IForce forces(final int theIndex) {
        return mForces.get(theIndex);
    }

    public void applyForces(final float theDeltaTime) {
        /* accumulate inner forces */
        synchronized (_myParticles) {
            final Iterator<Particle> iter = _myParticles.iterator();
            while (iter.hasNext()) {
                final Particle myParticle = iter.next();
                if (!myParticle.fixed()) {
                    /* accumulate inner forces */
                    myParticle.accumulateInnerForce(theDeltaTime);
                }
            }
        }

        /* add new forces to each particle */
        synchronized (mForces) {
            Iterator<IForce> iter = mForces.iterator();
            while (iter.hasNext()) {
                IForce myForce = iter.next();
                if (myForce.active()) {
                    myForce.apply(theDeltaTime, this);
                }
            }
        }
    }

    public <T extends IForce> T makeForce(Class<T> theForceClass) {
        T myForce;
        try {
            myForce = theForceClass.newInstance();
            mForces.add(myForce);
        } catch (Exception ex) {
            System.out.println(ex);
            myForce = null;
        }
        return myForce;
    }

    public Spring makeSpring(final Particle theA, final Particle theB) {
        Spring mySpring = new Spring(theA, theB);
        mForces.add(mySpring);
        return mySpring;
    }

    public Spring makeSpring(final Particle theA,
                             final Particle theB,
                             final float theRestLength) {
        Spring mySpring = new Spring(theA, theB, theRestLength);
        mForces.add(mySpring);
        return mySpring;
    }

    public Spring makeSpring(final Particle theA,
                             final Particle theB,
                             final float theSpringConstant,
                             final float theSpringDamping) {
        Spring mySpring = new Spring(theA, theB, theSpringConstant, theSpringDamping);
        mForces.add(mySpring);
        return mySpring;
    }

    public Spring makeSpring(final Particle theA,
                             final Particle theB,
                             final float theSpringConstant,
                             final float theSpringDamping,
                             final float theRestLength) {
        Spring mySpring = new Spring(theA, theB, theSpringConstant, theSpringDamping, theRestLength);
        mForces.add(mySpring);
        return mySpring;
    }


    /* constraints */
    public void add(final IConstraint theConstraint) {
        _myConstraints.add(theConstraint);
    }

    public void addConstraints(final Vector<IConstraint> theConstraints) {
        _myConstraints.addAll(theConstraints);
    }

    public void remove(final IConstraint theConstraint) {
        _myConstraints.remove(theConstraint);
    }

    public Vector<IConstraint> constraints() {
        return _myConstraints;
    }

    public IConstraint constraints(final int theIndex) {
        return _myConstraints.get(theIndex);
    }


    /* integration */
    public void setInegratorRef(IIntegrator theIntegrator) {
        _myIntegrator = theIntegrator;
    }

    public IIntegrator getIntegrator() {
        return _myIntegrator;
    }

    public void loop(final float theDeltaTime, final int theIterations) {
        for (int i = 0; i < theIterations; i++) {
            step(theDeltaTime / (float)theIterations);
        }
    }

    public void step(final float theDeltaTime) {
        /* handle forces */
        handleForces();

        /* integrate */
        integrate(theDeltaTime);

        /* handle particles */
        handleParticles(theDeltaTime);

        /* handle constraints */
        handleContraints();
    }

    protected synchronized void integrate(float theDeltaTime) {
        _myIntegrator.step(theDeltaTime, this);
    }

    protected synchronized void handleForces() {
        synchronized (mForces) {
            final Iterator<IForce> iter = mForces.iterator();
            while (iter.hasNext()) {
                final IForce myForce = iter.next();
                if (myForce.dead()) {
                    iter.remove();
                }
            }
        }
    }

    protected synchronized void handleContraints() {
        synchronized (_myConstraints) {
            for (int i = 0; i < contraint_iterations_per_steps; i++) {
                final Iterator<IConstraint> iter = _myConstraints.iterator();
                while (iter.hasNext()) {
                    final IConstraint myContraint = iter.next();
                    myContraint.apply(this);
                }
            }
        }
    }

    protected synchronized void handleParticles(float theDeltaTime) {
        synchronized (_myParticles) {
            final Iterator<Particle> iter = _myParticles.iterator();
            while (iter.hasNext()) {
                final Particle myParticle = iter.next();
                /* clear force */
                myParticle.force().set(0, 0, 0);
                /* age particle */
                myParticle.age(myParticle.age() + theDeltaTime);
                /* remove dead */
                if (HINT_REMOVE_DEAD) {
                    if (myParticle.dead()) {
                        iter.remove();
                    }
                }
                /* recover NAN */
                if (HINT_RECOVER_NAN) {
                    if (myParticle.position().isNaN()) {
                        if (myParticle.old_position().isNaN()) {
                            myParticle.position().set(0, 0, 0);
                        } else {
                            myParticle.position().set(myParticle.old_position());
                        }
                    }
                    if (myParticle.velocity().isNaN()) {
                        myParticle.velocity().set(0, 0, 0);
                    }
                }
                /* still */
                if (HINT_OPTIMIZE_STILL) {
                    final float mySpeed = myParticle.velocity().lengthSquared();
                    myParticle.still(mySpeed > -EPSILON && mySpeed < EPSILON);
                }
            }
        }
    }
}
