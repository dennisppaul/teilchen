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
 * second-order midpoint integration. evaluates forces at the start of the step, advances half a step, evaluates
 * forces again at the midpoint and then advances the original state a full step with the midpoint derivatives.
 */
public class Midpoint implements Integrator {

    private final ArrayList<PVector> mOriginalPositions  = new ArrayList<>();
    private final ArrayList<PVector> mOriginalVelocities = new ArrayList<>();
    private final ArrayList<PVector> mOriginalForces     = new ArrayList<>();

    public void step(final float pDeltaTime, final Physics pParticleSystem) {
        final ArrayList<Particle> mParticles = pParticleSystem.particles();
        synchronized (mParticles) {
            /* inner forces ( e.g behaviors ) are evaluated once per step */
            pParticleSystem.accumulateInnerForces(pDeltaTime);

            final int mSize = mParticles.size();
            IntegrationUtil.checkContainerSize(mSize, mOriginalPositions, PVector.class);
            IntegrationUtil.checkContainerSize(mSize, mOriginalVelocities, PVector.class);
            IntegrationUtil.checkContainerSize(mSize, mOriginalForces, PVector.class);
            IntegrationUtil.saveState(mParticles, mOriginalPositions, mOriginalVelocities, mOriginalForces);

            /* one: derivatives at start, advance half a step */
            pParticleSystem.applyForces(pDeltaTime, false);
            final float mHalfDeltaTime = pDeltaTime * 0.5f;
            for (int i = 0; i < mSize; i++) {
                final Particle mParticle = mParticles.get(i);
                if (!mParticle.fixed()) {
                    final PVector x0 = mOriginalPositions.get(i);
                    final PVector v0 = mOriginalVelocities.get(i);
                    final PVector f  = mParticle.force();
                    final float   s  = mHalfDeltaTime / mParticle.mass();
                    mParticle.position().set(x0.x + v0.x * mHalfDeltaTime,
                                             x0.y + v0.y * mHalfDeltaTime,
                                             x0.z + v0.z * mHalfDeltaTime);
                    mParticle.velocity().set(v0.x + f.x * s, v0.y + f.y * s, v0.z + f.z * s);
                }
            }

            /* two: derivatives at midpoint, advance original state a full step */
            IntegrationUtil.restoreForces(mParticles, mOriginalForces);
            pParticleSystem.applyForces(pDeltaTime, false);
            for (int i = 0; i < mSize; i++) {
                final Particle mParticle = mParticles.get(i);
                if (!mParticle.fixed()) {
                    final PVector x0 = mOriginalPositions.get(i);
                    final PVector v0 = mOriginalVelocities.get(i);
                    final PVector vm = mParticle.velocity();
                    final PVector f  = mParticle.force();
                    final float   s  = pDeltaTime / mParticle.mass();
                    mParticle.position().set(x0.x + vm.x * pDeltaTime,
                                             x0.y + vm.y * pDeltaTime,
                                             x0.z + vm.z * pDeltaTime);
                    mParticle.velocity().set(v0.x + f.x * s, v0.y + f.y * s, v0.z + f.z * s);
                }
            }
        }
    }
}
