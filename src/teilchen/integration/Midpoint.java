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

package teilchen.integration;

import teilchen.Particle;
import teilchen.Physics;

import java.util.ArrayList;

public class Midpoint implements IIntegrator {

    private final ArrayList<Derivate3f> mK1 = new ArrayList<>();

    public void step(final float pDeltaTime, final Physics pParticleSystem) {

        IntegrationUtil.checkContainerSize(pParticleSystem.particles().size(), mK1, Derivate3f.class);

        /* one */
        pParticleSystem.applyForces(pDeltaTime);
        IntegrationUtil.calculateDerivatives(pParticleSystem.particles(), mK1);
        for (int i = 0; i < pParticleSystem.particles().size(); i++) {
            Particle mParticle = pParticleSystem.particles().get(i);
            if (!mParticle.fixed()) {
                mParticle.position().x += mK1.get(i).px * pDeltaTime / 2;
                mParticle.position().y += mK1.get(i).py * pDeltaTime / 2;
                mParticle.position().z += mK1.get(i).pz * pDeltaTime / 2;
                mParticle.position().x += mK1.get(i).vx * pDeltaTime / 2;
                mParticle.position().y += mK1.get(i).vy * pDeltaTime / 2;
                mParticle.position().z += mK1.get(i).vz * pDeltaTime / 2;
            }
        }

        /* two */
        pParticleSystem.applyForces(pDeltaTime);
        IntegrationUtil.calculateDerivatives(pParticleSystem.particles(), mK1);
        for (int i = 0; i < pParticleSystem.particles().size(); i++) {
            Particle mParticle = pParticleSystem.particles().get(i);
            if (!mParticle.fixed()) {
                mParticle.position().x += mK1.get(i).px * pDeltaTime;
                mParticle.position().y += mK1.get(i).py * pDeltaTime;
                mParticle.position().z += mK1.get(i).pz * pDeltaTime;
                mParticle.velocity().x += mK1.get(i).vx * pDeltaTime;
                mParticle.velocity().y += mK1.get(i).vy * pDeltaTime;
                mParticle.velocity().z += mK1.get(i).vz * pDeltaTime;
            }
        }
    }
}
