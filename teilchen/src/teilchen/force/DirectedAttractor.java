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
package teilchen.force;


import mathematik.Vector3f;

import teilchen.Particle;
import teilchen.Physics;


public class DirectedAttractor
        extends Attractor {

    private final Vector3f myVectorA = new Vector3f();

    private final Vector3f myVectorB = new Vector3f();

    public DirectedAttractor() {
        super();
    }

    public void apply(float theDeltaTime, Physics theParticleSystem) {
        for (final Particle myParticle : theParticleSystem.particles()) {
            /* each particle */
            if (!myParticle.fixed()) {
                myTemp.sub(_myPosition, myParticle.position());

                final float myDistance = fastInverseSqrt(1 / myTemp.lengthSquared());
                if (myDistance < _myRadius) {

                    myVectorA.scale(1 / myDistance, myTemp);
                    myVectorB.normalize(myParticle.velocity());
                    float myAngle = myVectorA.dot(myVectorB);

                    float myFallOff = 1f - myDistance / _myRadius;
                    final float myForce = myAngle * myFallOff * myFallOff * _myStrength;
                    myTemp.scale(myForce / myDistance);
                    myParticle.force().add(myTemp);
                }
            }
        }
    }
}
