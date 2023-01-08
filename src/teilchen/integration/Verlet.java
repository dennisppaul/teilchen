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

import java.util.Iterator;

import static processing.core.PVector.sub;

public class Verlet implements IIntegrator {

    private float mDamping;
    private final PVector temp1;
    private final PVector temp2;

    public Verlet() {
        this(1.0f);
    }

    public Verlet(final float pDamping) {
        mDamping = pDamping;
        temp1 = new PVector();
        temp2 = new PVector();
    }

    public float damping() {
        return mDamping;
    }

    public void damping(float pDamping) {
        mDamping = pDamping;
    }

    public void step(final float pDeltaTime, final Physics pParticleSystem) {

        pParticleSystem.applyForces(pDeltaTime);

        synchronized (pParticleSystem.particles()) {
            final Iterator<Particle> mIterator = pParticleSystem.particles().iterator();
            while (mIterator.hasNext()) {
                final Particle mParticle = mIterator.next();
                if (!mParticle.fixed()) {
                    integrate(pDeltaTime, mParticle);
                }
            }
        }
    }

    private void integrate(float pDeltaTime, Particle pParticle) {
        final PVector mOldPosition = new PVector();
        mOldPosition.set(pParticle.position());

        /*
         Physics simulation using Verlet integration
         sgreen@nvidia.com 6/2002

         based on Thomas Jakobsen's "Advanced Character Physics":
         http://www.ioi.dk/Homepages/tj/publications/gdc2001.htm

         basic idea:
         x' = x + v*dt
         v' = v + a*dt

         x' = x + (v + a*dt) * dt
         = x + v*dt + a*dt^2

         v ~= (x - ox) / dt

         x' = x + (x - ox) + a*dt^2
         */

        /* v ~= (x - ox) / dt */
        sub(pParticle.position(), pParticle.old_position(), pParticle.velocity());
        pParticle.velocity().mult(1.0f / pDeltaTime);

        /* x' = x + (x - ox) + a*dt^2 */
        temp1.set(pParticle.force());
        temp1.mult(1.0f / pParticle.mass());
        temp1.mult(pDeltaTime * pDeltaTime);
        sub(pParticle.position(), pParticle.old_position(), temp2);

        temp2.mult(mDamping);

        pParticle.position().add(temp1);
        pParticle.position().add(temp2);

        /* --- */
        pParticle.old_position().set(mOldPosition);
    }
}
