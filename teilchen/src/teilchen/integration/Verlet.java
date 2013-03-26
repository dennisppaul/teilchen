/*
 * Teilchen
 *
 * Copyright (C) 2013
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

import mathematik.Vector3f;

import teilchen.Particle;
import teilchen.Physics;


public class Verlet
        implements IIntegrator {

    private final Vector3f temp1;

    private final Vector3f temp2;

    private float _myDamping;

    public Verlet() {
        this(1.0f);
    }

    public Verlet(final float theDamping) {
        _myDamping = theDamping;
        temp1 = new Vector3f();
        temp2 = new Vector3f();
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

    private final void integrate(float theDeltaTime, Particle theParticle) {
        final Vector3f myOldPosition = new Vector3f(theParticle.position());

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
        theParticle.velocity().sub(theParticle.position(), theParticle.old_position());
        theParticle.velocity().scale(1.0f / theDeltaTime);

        /* x' = x + (x - ox) + a*dt^2 */
        temp1.set(theParticle.force());
        temp1.scale(1.0f / theParticle.mass());
        temp1.scale(theDeltaTime * theDeltaTime);
        temp2.sub(theParticle.position(), theParticle.old_position());

        temp2.scale(_myDamping);

        theParticle.position().add(temp1);
        theParticle.position().add(temp2);

        /* --- */
        theParticle.old_position().set(myOldPosition);
    }
}
