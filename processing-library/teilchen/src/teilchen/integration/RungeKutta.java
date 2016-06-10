/*
 * Teilchen
 *
 * Copyright (C) 2015
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

package teilchen.integration;

import processing.core.PVector;
import teilchen.Particle;
import teilchen.Physics;

import java.util.ArrayList;

public class RungeKutta implements IIntegrator {

    private final ArrayList<PVector> mOriginalPositions;

    private final ArrayList<PVector> mOriginalVelocities;

    private final ArrayList<PVector> mK1Forces;

    private final ArrayList<PVector> mK1Velocities;

    private final ArrayList<PVector> mK2Forces;

    private final ArrayList<PVector> mK2Velocities;

    private final ArrayList<PVector> mK3Forces;

    private final ArrayList<PVector> mK3Velocities;

    private final ArrayList<PVector> mK4Forces;

    private final ArrayList<PVector> mK4Velocities;

    public RungeKutta() {
        mOriginalPositions = new ArrayList<PVector>();
        mOriginalVelocities = new ArrayList<PVector>();
        mK1Forces = new ArrayList<PVector>();
        mK1Velocities = new ArrayList<PVector>();
        mK2Forces = new ArrayList<PVector>();
        mK2Velocities = new ArrayList<PVector>();
        mK3Forces = new ArrayList<PVector>();
        mK3Velocities = new ArrayList<PVector>();
        mK4Forces = new ArrayList<PVector>();
        mK4Velocities = new ArrayList<PVector>();
    }

    public void step(final float theDeltaTime, final Physics theParticleSystem) {

        final int mySize = theParticleSystem.particles().size();
        Util.checkContainerSize(mySize, mOriginalPositions, PVector.class);
        Util.checkContainerSize(mySize, mOriginalVelocities, PVector.class);
        Util.checkContainerSize(mySize, mK1Forces, PVector.class);
        Util.checkContainerSize(mySize, mK1Velocities, PVector.class);
        Util.checkContainerSize(mySize, mK2Forces, PVector.class);
        Util.checkContainerSize(mySize, mK2Velocities, PVector.class);
        Util.checkContainerSize(mySize, mK3Forces, PVector.class);
        Util.checkContainerSize(mySize, mK3Velocities, PVector.class);
        Util.checkContainerSize(mySize, mK4Forces, PVector.class);
        Util.checkContainerSize(mySize, mK4Velocities, PVector.class);

        /* save original position and velocities */
        for (int i = 0; i < theParticleSystem.particles().size(); ++i) {
            final Particle myParticle = theParticleSystem.particles().get(i);
            if (!myParticle.fixed()) {
                mOriginalPositions.get(i).set(myParticle.position());
                mOriginalVelocities.get(i).set(myParticle.velocity());
            }
        }

        /* get all the k1 values */
        theParticleSystem.applyForces(theDeltaTime);

        /* save the intermediate forces */
        for (int i = 0; i < theParticleSystem.particles().size(); ++i) {
            final Particle myParticle = theParticleSystem.particles().get(i);
            if (!myParticle.fixed()) {
                mK1Forces.get(i).set(myParticle.force());
                mK1Velocities.get(i).set(myParticle.velocity());
            }
        }

        /* get k2 values */
        for (int i = 0; i < theParticleSystem.particles().size(); ++i) {
            final Particle myParticle = theParticleSystem.particles().get(i);
            if (!myParticle.fixed()) {
                final PVector originalPosition = mOriginalPositions.get(i);
                final PVector k1Velocity = mK1Velocities.get(i);

                myParticle.position().x = originalPosition.x + k1Velocity.x * 0.5f * theDeltaTime;
                myParticle.position().y = originalPosition.y + k1Velocity.y * 0.5f * theDeltaTime;
                myParticle.position().z = originalPosition.z + k1Velocity.z * 0.5f * theDeltaTime;

                final PVector originalVelocity = mOriginalVelocities.get(i);
                final PVector k1Force = mK1Forces.get(i);

                myParticle.velocity().x = originalVelocity.x + k1Force.x * 0.5f * theDeltaTime / myParticle.mass();
                myParticle.velocity().y = originalVelocity.y + k1Force.y * 0.5f * theDeltaTime / myParticle.mass();
                myParticle.velocity().z = originalVelocity.z + k1Force.z * 0.5f * theDeltaTime / myParticle.mass();
            }
        }

        theParticleSystem.applyForces(theDeltaTime);

        /* save the intermediate forces */
        for (int i = 0; i < theParticleSystem.particles().size(); ++i) {
            final Particle myParticle = theParticleSystem.particles().get(i);
            if (!myParticle.fixed()) {
                mK2Forces.get(i).set(myParticle.force());
                mK2Velocities.get(i).set(myParticle.velocity());
            }
        }

        /* get k3 values */
        for (int i = 0; i < theParticleSystem.particles().size(); ++i) {
            final Particle myParticle = theParticleSystem.particles().get(i);
            if (!myParticle.fixed()) {
                final PVector originalPosition = mOriginalPositions.get(i);
                final PVector k2Velocity = mK2Velocities.get(i);

                myParticle.position().x = originalPosition.x + k2Velocity.x * 0.5f * theDeltaTime;
                myParticle.position().y = originalPosition.y + k2Velocity.y * 0.5f * theDeltaTime;
                myParticle.position().z = originalPosition.z + k2Velocity.z * 0.5f * theDeltaTime;

                final PVector originalVelocity = mOriginalVelocities.get(i);
                final PVector k2Force = mK2Forces.get(i);

                myParticle.velocity().x = originalVelocity.x + k2Force.x * 0.5f * theDeltaTime / myParticle.mass();
                myParticle.velocity().y = originalVelocity.y + k2Force.y * 0.5f * theDeltaTime / myParticle.mass();
                myParticle.velocity().z = originalVelocity.z + k2Force.z * 0.5f * theDeltaTime / myParticle.mass();
            }
        }

        theParticleSystem.applyForces(theDeltaTime);

        /* save the intermediate forces */
        for (int i = 0; i < theParticleSystem.particles().size(); ++i) {
            final Particle myParticle = theParticleSystem.particles().get(i);
            if (!myParticle.fixed()) {
                (mK3Forces.get(i)).set(myParticle.force());
                (mK3Velocities.get(i)).set(myParticle.velocity());
            }
        }

        /* get k4 values */
        for (int i = 0; i < theParticleSystem.particles().size(); ++i) {
            final Particle myParticle = theParticleSystem.particles().get(i);
            if (!myParticle.fixed()) {
                final PVector originalPosition = mOriginalPositions.get(i);
                final PVector k3Velocity = mK3Velocities.get(i);

                myParticle.position().x = originalPosition.x + k3Velocity.x * theDeltaTime;
                myParticle.position().y = originalPosition.y + k3Velocity.y * theDeltaTime;
                myParticle.position().z = originalPosition.z + k3Velocity.z * theDeltaTime;

                final PVector originalVelocity = mOriginalVelocities.get(i);
                final PVector k3Force = mK3Forces.get(i);

                myParticle.velocity().x = originalVelocity.x + k3Force.x * theDeltaTime / myParticle.mass();
                myParticle.velocity().y = originalVelocity.y + k3Force.y * theDeltaTime / myParticle.mass();
                myParticle.velocity().z = originalVelocity.z + k3Force.z * theDeltaTime / myParticle.mass();
            }
        }

        theParticleSystem.applyForces(theDeltaTime);

        /* save the intermediate forces */
        for (int i = 0; i < theParticleSystem.particles().size(); ++i) {
            final Particle myParticle = theParticleSystem.particles().get(i);
            if (!myParticle.fixed()) {
                mK4Forces.get(i).set(myParticle.force());
                mK4Velocities.get(i).set(myParticle.velocity());
            }
        }

        /* put them all together and what do you get? */
        for (int i = 0; i < theParticleSystem.particles().size(); ++i) {
            final Particle myParticle = theParticleSystem.particles().get(i);
            if (!myParticle.fixed()) {
                /* update position */
                final PVector originalPosition = mOriginalPositions.get(i);
                final PVector k1Velocity = mK1Velocities.get(i);
                final PVector k2Velocity = mK2Velocities.get(i);
                final PVector k3Velocity = mK3Velocities.get(i);
                final PVector k4Velocity = mK4Velocities.get(i);

                myParticle.position().x = originalPosition.x + theDeltaTime / 6.0f * (k1Velocity.x + 2.0f * k2Velocity.x + 2.0f * k3Velocity.x + k4Velocity.x);
                myParticle.position().y = originalPosition.y + theDeltaTime / 6.0f * (k1Velocity.y + 2.0f * k2Velocity.y + 2.0f * k3Velocity.y + k4Velocity.y);
                myParticle.position().z = originalPosition.z + theDeltaTime / 6.0f * (k1Velocity.z + 2.0f * k2Velocity.z + 2.0f * k3Velocity.z + k4Velocity.z);

                /* update velocity */
                final PVector originalVelocity = mOriginalVelocities.get(i);
                final PVector k1Force = mK1Forces.get(i);
                final PVector k2Force = mK2Forces.get(i);
                final PVector k3Force = mK3Forces.get(i);
                final PVector k4Force = mK4Forces.get(i);

                myParticle.velocity().x = originalVelocity.x + theDeltaTime / (6.0f * myParticle.mass()) * (k1Force.x + 2.0f * k2Force.x + 2.0f * k3Force.x + k4Force.x);
                myParticle.velocity().y = originalVelocity.y + theDeltaTime / (6.0f * myParticle.mass()) * (k1Force.y + 2.0f * k2Force.y + 2.0f * k3Force.y + k4Force.y);
                myParticle.velocity().z = originalVelocity.z + theDeltaTime / (6.0f * myParticle.mass()) * (k1Force.z + 2.0f * k2Force.z + 2.0f * k3Force.z + k4Force.z);
            }
        }
    }
}
