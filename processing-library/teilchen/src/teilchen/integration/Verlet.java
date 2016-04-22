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
import static processing.core.PVector.sub;
import teilchen.Particle;
import teilchen.Physics;

public class Verlet
        implements IIntegrator {

    private final PVector temp1;

    private final PVector temp2;

    private float _myDamping;

    public Verlet() {
        this(1.0f);
    }

    public Verlet(final float theDamping) {
        _myDamping = theDamping;
        temp1 = new PVector();
        temp2 = new PVector();
    }

    public float damping() {
        return _myDamping;
    }

    public void damping(float theDamping) {
        _myDamping = theDamping;
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

    private void integrate(float theDeltaTime, Particle theParticle) {
        final PVector myOldPosition = new PVector();
        myOldPosition.set(theParticle.position());

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
        sub(theParticle.position(), theParticle.old_position(), theParticle.velocity());
        theParticle.velocity().mult(1.0f / theDeltaTime);

        /* x' = x + (x - ox) + a*dt^2 */
        temp1.set(theParticle.force());
        temp1.mult(1.0f / theParticle.mass());
        temp1.mult(theDeltaTime * theDeltaTime);
        sub(theParticle.position(), theParticle.old_position(), temp2);

        temp2.mult(_myDamping);

        theParticle.position().add(temp1);
        theParticle.position().add(temp2);

        /* --- */
        theParticle.old_position().set(myOldPosition);
    }
}
