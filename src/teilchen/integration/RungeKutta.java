/*
 * Teilchen
 *
 * This file is part of the *teilchen* library (https://github.com/dennisppaul/teilchen).
 * Copyright (c) 2023 Dennis P Paul.
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

    private final ArrayList<PVector> mK1Forces;
    private final ArrayList<PVector> mK1Velocities;
    private final ArrayList<PVector> mK2Forces;
    private final ArrayList<PVector> mK2Velocities;
    private final ArrayList<PVector> mK3Forces;
    private final ArrayList<PVector> mK3Velocities;
    private final ArrayList<PVector> mK4Forces;
    private final ArrayList<PVector> mK4Velocities;
    private final ArrayList<PVector> mOriginalPositions;
    private final ArrayList<PVector> mOriginalVelocities;

    public RungeKutta() {
        mOriginalPositions = new ArrayList<>();
        mOriginalVelocities = new ArrayList<>();
        mK1Forces = new ArrayList<>();
        mK1Velocities = new ArrayList<>();
        mK2Forces = new ArrayList<>();
        mK2Velocities = new ArrayList<>();
        mK3Forces = new ArrayList<>();
        mK3Velocities = new ArrayList<>();
        mK4Forces = new ArrayList<>();
        mK4Velocities = new ArrayList<>();
    }

    public void step(final float pDeltaTime, final Physics pParticleSystem) {

        final int mSize = pParticleSystem.particles().size();
        IntegrationUtil.checkContainerSize(mSize, mOriginalPositions, PVector.class);
        IntegrationUtil.checkContainerSize(mSize, mOriginalVelocities, PVector.class);
        IntegrationUtil.checkContainerSize(mSize, mK1Forces, PVector.class);
        IntegrationUtil.checkContainerSize(mSize, mK1Velocities, PVector.class);
        IntegrationUtil.checkContainerSize(mSize, mK2Forces, PVector.class);
        IntegrationUtil.checkContainerSize(mSize, mK2Velocities, PVector.class);
        IntegrationUtil.checkContainerSize(mSize, mK3Forces, PVector.class);
        IntegrationUtil.checkContainerSize(mSize, mK3Velocities, PVector.class);
        IntegrationUtil.checkContainerSize(mSize, mK4Forces, PVector.class);
        IntegrationUtil.checkContainerSize(mSize, mK4Velocities, PVector.class);

        /* save original position and velocities */
        for (int i = 0; i < pParticleSystem.particles().size(); ++i) {
            final Particle mParticle = pParticleSystem.particles().get(i);
            if (!mParticle.fixed()) {
                mOriginalPositions.get(i).set(mParticle.position());
                mOriginalVelocities.get(i).set(mParticle.velocity());
            }
        }

        /* get all the k1 values */
        pParticleSystem.applyForces(pDeltaTime);

        /* save the intermediate forces */
        for (int i = 0; i < pParticleSystem.particles().size(); ++i) {
            final Particle mParticle = pParticleSystem.particles().get(i);
            if (!mParticle.fixed()) {
                mK1Forces.get(i).set(mParticle.force());
                mK1Velocities.get(i).set(mParticle.velocity());
            }
        }

        /* get k2 values */
        for (int i = 0; i < pParticleSystem.particles().size(); ++i) {
            final Particle mParticle = pParticleSystem.particles().get(i);
            if (!mParticle.fixed()) {
                final PVector originalPosition = mOriginalPositions.get(i);
                final PVector k1Velocity = mK1Velocities.get(i);

                mParticle.position().x = originalPosition.x + k1Velocity.x * 0.5f * pDeltaTime;
                mParticle.position().y = originalPosition.y + k1Velocity.y * 0.5f * pDeltaTime;
                mParticle.position().z = originalPosition.z + k1Velocity.z * 0.5f * pDeltaTime;

                final PVector originalVelocity = mOriginalVelocities.get(i);
                final PVector k1Force = mK1Forces.get(i);

                mParticle.velocity().x = originalVelocity.x + k1Force.x * 0.5f * pDeltaTime / mParticle.mass();
                mParticle.velocity().y = originalVelocity.y + k1Force.y * 0.5f * pDeltaTime / mParticle.mass();
                mParticle.velocity().z = originalVelocity.z + k1Force.z * 0.5f * pDeltaTime / mParticle.mass();
            }
        }

        pParticleSystem.applyForces(pDeltaTime);

        /* save the intermediate forces */
        for (int i = 0; i < pParticleSystem.particles().size(); ++i) {
            final Particle mParticle = pParticleSystem.particles().get(i);
            if (!mParticle.fixed()) {
                mK2Forces.get(i).set(mParticle.force());
                mK2Velocities.get(i).set(mParticle.velocity());
            }
        }

        /* get k3 values */
        for (int i = 0; i < pParticleSystem.particles().size(); ++i) {
            final Particle mParticle = pParticleSystem.particles().get(i);
            if (!mParticle.fixed()) {
                final PVector originalPosition = mOriginalPositions.get(i);
                final PVector k2Velocity = mK2Velocities.get(i);

                mParticle.position().x = originalPosition.x + k2Velocity.x * 0.5f * pDeltaTime;
                mParticle.position().y = originalPosition.y + k2Velocity.y * 0.5f * pDeltaTime;
                mParticle.position().z = originalPosition.z + k2Velocity.z * 0.5f * pDeltaTime;

                final PVector originalVelocity = mOriginalVelocities.get(i);
                final PVector k2Force = mK2Forces.get(i);

                mParticle.velocity().x = originalVelocity.x + k2Force.x * 0.5f * pDeltaTime / mParticle.mass();
                mParticle.velocity().y = originalVelocity.y + k2Force.y * 0.5f * pDeltaTime / mParticle.mass();
                mParticle.velocity().z = originalVelocity.z + k2Force.z * 0.5f * pDeltaTime / mParticle.mass();
            }
        }

        pParticleSystem.applyForces(pDeltaTime);

        /* save the intermediate forces */
        for (int i = 0; i < pParticleSystem.particles().size(); ++i) {
            final Particle mParticle = pParticleSystem.particles().get(i);
            if (!mParticle.fixed()) {
                (mK3Forces.get(i)).set(mParticle.force());
                (mK3Velocities.get(i)).set(mParticle.velocity());
            }
        }

        /* get k4 values */
        for (int i = 0; i < pParticleSystem.particles().size(); ++i) {
            final Particle mParticle = pParticleSystem.particles().get(i);
            if (!mParticle.fixed()) {
                final PVector originalPosition = mOriginalPositions.get(i);
                final PVector k3Velocity = mK3Velocities.get(i);

                mParticle.position().x = originalPosition.x + k3Velocity.x * pDeltaTime;
                mParticle.position().y = originalPosition.y + k3Velocity.y * pDeltaTime;
                mParticle.position().z = originalPosition.z + k3Velocity.z * pDeltaTime;

                final PVector originalVelocity = mOriginalVelocities.get(i);
                final PVector k3Force = mK3Forces.get(i);

                mParticle.velocity().x = originalVelocity.x + k3Force.x * pDeltaTime / mParticle.mass();
                mParticle.velocity().y = originalVelocity.y + k3Force.y * pDeltaTime / mParticle.mass();
                mParticle.velocity().z = originalVelocity.z + k3Force.z * pDeltaTime / mParticle.mass();
            }
        }

        pParticleSystem.applyForces(pDeltaTime);

        /* save the intermediate forces */
        for (int i = 0; i < pParticleSystem.particles().size(); ++i) {
            final Particle mParticle = pParticleSystem.particles().get(i);
            if (!mParticle.fixed()) {
                mK4Forces.get(i).set(mParticle.force());
                mK4Velocities.get(i).set(mParticle.velocity());
            }
        }

        /* put them all together and what do you get? */
        for (int i = 0; i < pParticleSystem.particles().size(); ++i) {
            final Particle mParticle = pParticleSystem.particles().get(i);
            if (!mParticle.fixed()) {
                /* update position */
                final PVector originalPosition = mOriginalPositions.get(i);
                final PVector k1Velocity = mK1Velocities.get(i);
                final PVector k2Velocity = mK2Velocities.get(i);
                final PVector k3Velocity = mK3Velocities.get(i);
                final PVector k4Velocity = mK4Velocities.get(i);

                mParticle.position().x =
                        originalPosition.x + pDeltaTime / 6.0f * (k1Velocity.x + 2.0f * k2Velocity.x + 2.0f * k3Velocity.x + k4Velocity.x);
                mParticle.position().y =
                        originalPosition.y + pDeltaTime / 6.0f * (k1Velocity.y + 2.0f * k2Velocity.y + 2.0f * k3Velocity.y + k4Velocity.y);
                mParticle.position().z =
                        originalPosition.z + pDeltaTime / 6.0f * (k1Velocity.z + 2.0f * k2Velocity.z + 2.0f * k3Velocity.z + k4Velocity.z);

                /* update velocity */
                final PVector originalVelocity = mOriginalVelocities.get(i);
                final PVector k1Force = mK1Forces.get(i);
                final PVector k2Force = mK2Forces.get(i);
                final PVector k3Force = mK3Forces.get(i);
                final PVector k4Force = mK4Forces.get(i);

                mParticle.velocity().x =
                        originalVelocity.x + pDeltaTime / (6.0f * mParticle.mass()) * (k1Force.x + 2.0f * k2Force.x + 2.0f * k3Force.x + k4Force.x);
                mParticle.velocity().y =
                        originalVelocity.y + pDeltaTime / (6.0f * mParticle.mass()) * (k1Force.y + 2.0f * k2Force.y + 2.0f * k3Force.y + k4Force.y);
                mParticle.velocity().z =
                        originalVelocity.z + pDeltaTime / (6.0f * mParticle.mass()) * (k1Force.z + 2.0f * k2Force.z + 2.0f * k3Force.z + k4Force.z);
            }
        }
    }
}
