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

package teilchen.integration;

import processing.core.PVector;
import teilchen.Particle;
import teilchen.Physics;

import java.util.ArrayList;

/**
 * classic fourth-order runge-kutta integration.
 */
public class RungeKutta implements Integrator {

    private static final float[] STAGE_OFFSET = {0.5f, 0.5f, 1.0f};
    private static final float[] STAGE_WEIGHT = {1.0f, 2.0f, 2.0f, 1.0f};

    private final ArrayList<PVector> mOriginalPositions  = new ArrayList<>();
    private final ArrayList<PVector> mOriginalVelocities = new ArrayList<>();
    private final ArrayList<PVector> mOriginalForces     = new ArrayList<>();
    private final ArrayList<PVector> mSumVelocities      = new ArrayList<>();
    private final ArrayList<PVector> mSumAccelerations   = new ArrayList<>();

    public void step(final float pDeltaTime, final Physics pParticleSystem) {
        final ArrayList<Particle> mParticles = pParticleSystem.particles();
        synchronized (mParticles) {
            /* inner forces ( e.g behaviors ) are evaluated once per step */
            pParticleSystem.accumulateInnerForces(pDeltaTime);

            final int mSize = mParticles.size();
            IntegrationUtil.checkContainerSize(mSize, mOriginalPositions, PVector.class);
            IntegrationUtil.checkContainerSize(mSize, mOriginalVelocities, PVector.class);
            IntegrationUtil.checkContainerSize(mSize, mOriginalForces, PVector.class);
            IntegrationUtil.checkContainerSize(mSize, mSumVelocities, PVector.class);
            IntegrationUtil.checkContainerSize(mSize, mSumAccelerations, PVector.class);
            IntegrationUtil.saveState(mParticles, mOriginalPositions, mOriginalVelocities, mOriginalForces);
            for (int i = 0; i < mSize; i++) {
                mSumVelocities.get(i).set(0, 0, 0);
                mSumAccelerations.get(i).set(0, 0, 0);
            }

            for (int k = 0; k < 4; k++) {
                /* evaluate derivatives ( k1 .. k4 ) at current state */
                if (k > 0) {
                    IntegrationUtil.restoreForces(mParticles, mOriginalForces);
                }
                pParticleSystem.applyForces(pDeltaTime, false);

                for (int i = 0; i < mSize; i++) {
                    final Particle mParticle = mParticles.get(i);
                    if (mParticle.fixed()) {
                        continue;
                    }
                    final PVector v = mParticle.velocity();
                    final PVector f = mParticle.force();
                    final float   w = STAGE_WEIGHT[k];
                    final float   m = mParticle.mass();
                    final PVector mSumV = mSumVelocities.get(i);
                    final PVector mSumA = mSumAccelerations.get(i);
                    mSumV.add(v.x * w, v.y * w, v.z * w);
                    mSumA.add(f.x * w / m, f.y * w / m, f.z * w / m);

                    final PVector x0 = mOriginalPositions.get(i);
                    final PVector v0 = mOriginalVelocities.get(i);
                    if (k < 3) {
                        /* move to next evaluation point */
                        final float h = STAGE_OFFSET[k] * pDeltaTime;
                        final float s = h / m;
                        final float vx = v.x, vy = v.y, vz = v.z;
                        mParticle.position().set(x0.x + vx * h, x0.y + vy * h, x0.z + vz * h);
                        mParticle.velocity().set(v0.x + f.x * s, v0.y + f.y * s, v0.z + f.z * s);
                    } else {
                        /* combine */
                        final float h = pDeltaTime / 6.0f;
                        mParticle.position().set(x0.x + mSumV.x * h, x0.y + mSumV.y * h, x0.z + mSumV.z * h);
                        mParticle.velocity().set(v0.x + mSumA.x * h, v0.y + mSumA.y * h, v0.z + mSumA.z * h);
                    }
                }
            }
        }
    }
}
