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

import java.util.Iterator;
import processing.core.PVector;
import teilchen.Particle;
import teilchen.Physics;

public class Euler
        implements IIntegrator {

    private final PVector mTemp1;

    private final PVector mTemp2;

    public Euler() {
        mTemp1 = new PVector();
        mTemp2 = new PVector();
    }

    public void step(final float theDeltaTime, final Physics theParticleSystem) {

        theParticleSystem.applyForces(theDeltaTime);

        synchronized (theParticleSystem.particles()) {
            final Iterator<Particle> myIterator = theParticleSystem.particles().iterator();
            while (myIterator.hasNext()) {
                final Particle myParticle = myIterator.next();
                if (!myParticle.fixed()) {
                    integrate(theDeltaTime, myParticle);
                }
            }
        }
    }

    private void integrate(final float theDeltaTime, final Particle theParticle) {
        mTemp1.set(theParticle.force());
        mTemp1.mult(theDeltaTime / theParticle.mass());

        mTemp2.set(theParticle.velocity());
        mTemp2.mult(theDeltaTime);

        theParticle.velocity().add(mTemp1);
        theParticle.position().add(mTemp2);
    }
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
//    private final Vector<Derivate3f> myK1 = new Vector<Derivate3f> ();
//
//    public void step(final float theDeltaTime, final ParticleSystem theParticleSystem) {
//
//        Util.checkContainerSize(theParticleSystem.particles().size(), myK1, Derivate3f.class);
//
//        theParticleSystem.applyForces(theDeltaTime);
//        Util.calculateDerivatives(theParticleSystem.particles(), myK1);
//        for (int i = 0; i < theParticleSystem.particles().size(); i++) {
//            Particle myParticle = theParticleSystem.particles().get(i);
//            if (!myParticle.fixed()) {
//                myParticle.position().x += myK1.get(i).px * theDeltaTime;
//                myParticle.position().y += myK1.get(i).py * theDeltaTime;
//                myParticle.position().z += myK1.get(i).pz * theDeltaTime;
//                myParticle.velocity().x += myK1.get(i).vx * theDeltaTime;
//                myParticle.velocity().y += myK1.get(i).vy * theDeltaTime;
//                myParticle.velocity().z += myK1.get(i).vz * theDeltaTime;
//            }
//        }
//    }
//}

