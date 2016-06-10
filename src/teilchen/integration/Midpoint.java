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

import java.util.ArrayList;
import teilchen.Particle;
import teilchen.Physics;

public class Midpoint
        implements IIntegrator {

    private final ArrayList<Derivate3f> mK1 = new ArrayList<>();

    public void step(final float theDeltaTime, final Physics theParticleSystem) {

        Util.checkContainerSize(theParticleSystem.particles().size(), mK1, Derivate3f.class);

        /* one */
        theParticleSystem.applyForces(theDeltaTime);
        Util.calculateDerivatives(theParticleSystem.particles(), mK1);
        for (int i = 0; i < theParticleSystem.particles().size(); i++) {
            Particle myParticle = theParticleSystem.particles().get(i);
            if (!myParticle.fixed()) {
                myParticle.position().x += mK1.get(i).px * theDeltaTime / 2;
                myParticle.position().y += mK1.get(i).py * theDeltaTime / 2;
                myParticle.position().z += mK1.get(i).pz * theDeltaTime / 2;
                myParticle.position().x += mK1.get(i).vx * theDeltaTime / 2;
                myParticle.position().y += mK1.get(i).vy * theDeltaTime / 2;
                myParticle.position().z += mK1.get(i).vz * theDeltaTime / 2;
            }
        }

        /* two */
        theParticleSystem.applyForces(theDeltaTime);
        Util.calculateDerivatives(theParticleSystem.particles(), mK1);
        for (int i = 0; i < theParticleSystem.particles().size(); i++) {
            Particle myParticle = theParticleSystem.particles().get(i);
            if (!myParticle.fixed()) {
                myParticle.position().x += mK1.get(i).px * theDeltaTime;
                myParticle.position().y += mK1.get(i).py * theDeltaTime;
                myParticle.position().z += mK1.get(i).pz * theDeltaTime;
                myParticle.velocity().x += mK1.get(i).vx * theDeltaTime;
                myParticle.velocity().y += mK1.get(i).vy * theDeltaTime;
                myParticle.velocity().z += mK1.get(i).vz * theDeltaTime;
            }
        }
    }
}
