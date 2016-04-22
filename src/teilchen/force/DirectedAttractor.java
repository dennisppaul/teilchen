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
package teilchen.force;

import processing.core.PVector;
import teilchen.Particle;
import teilchen.Physics;
import static teilchen.util.Util.lengthSquared;

public class DirectedAttractor
        extends Attractor {

    private final PVector myVectorA = new PVector();

    private final PVector myVectorB = new PVector();

    public DirectedAttractor() {
        super();
    }

    public void apply(float theDeltaTime, Physics theParticleSystem) {
        for (final Particle myParticle : theParticleSystem.particles()) {
            /* each particle */
            if (!myParticle.fixed()) {
                PVector.sub(_myPosition, myParticle.position(), myTemp);

                final float myDistance = fastInverseSqrt(1 / lengthSquared(myTemp));
                if (myDistance < _myRadius) {

                    myVectorA.set(myTemp);
                    myVectorA.mult(1.0f / myDistance);
                    myVectorB.normalize(myParticle.velocity());
                    float myAngle = myVectorA.dot(myVectorB);

                    float myFallOff = 1f - myDistance / _myRadius;
                    final float myForce = myAngle * myFallOff * myFallOff * _myStrength;
                    myTemp.mult(myForce / myDistance);
                    myParticle.force().add(myTemp);
                }
            }
        }
    }
}
