/*
 * Teilchen
 *
 * This file is part of the *teilchen* library (https://github.com/dennisppaul/teilchen).
 * Copyright (c) 2020 Dennis P Paul.
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

public class Euler implements IIntegrator {

    public Euler() {
        mTemp1 = new PVector();
        mTemp2 = new PVector();
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

    private void integrate(final float pDeltaTime, final Particle pParticle) {
        mTemp1.set(pParticle.force());
        mTemp1.mult(pDeltaTime / pParticle.mass());

        mTemp2.set(pParticle.velocity());
        mTemp2.mult(pDeltaTime);

        pParticle.velocity().add(mTemp1);
        pParticle.position().add(mTemp2);
    }
    private final PVector mTemp1;
    private final PVector mTemp2;
}

/* this version scales better with the other integrators but is definitly slower */
//import java.util.Vector;
//
//import particles.IParticle;
//import particles.ParticleSystem;
//
//
//public class Euler
//    implements IIntegrator {
//
//    private final Vector<Derivate3f> mK1 = new Vector<Derivate3f> ();
//
//    public void step(final float theDeltaTime, final ParticleSystem pParticleSystem) {
//
//        IntegrationUtil.checkContainerSize(pParticleSystem.particles().size(), mK1, Derivate3f.class);
//
//        pParticleSystem.applyForces(theDeltaTime);
//        IntegrationUtil.calculateDerivatives(pParticleSystem.particles(), mK1);
//        for (int i = 0; i < pParticleSystem.particles().size(); i++) {
//            Particle mParticle = pParticleSystem.particles().get(i);
//            if (!mParticle.fixed()) {
//                mParticle.position().x += mK1.get(i).px * theDeltaTime;
//                mParticle.position().y += mK1.get(i).py * theDeltaTime;
//                mParticle.position().z += mK1.get(i).pz * theDeltaTime;
//                mParticle.velocity().x += mK1.get(i).vx * theDeltaTime;
//                mParticle.velocity().y += mK1.get(i).vy * theDeltaTime;
//                mParticle.velocity().z += mK1.get(i).vz * theDeltaTime;
//            }
//        }
//    }
//}

