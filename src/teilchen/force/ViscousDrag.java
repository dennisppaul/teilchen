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

import teilchen.Particle;
import teilchen.Physics;
import teilchen.integration.Verlet;

public class ViscousDrag
        implements IForce {

    public float coefficient;

    private boolean _myActive;

    public ViscousDrag(float theCoefficient) {
        coefficient = theCoefficient;
        _myActive = true;
    }

    public ViscousDrag() {
        this(1.0f);
    }

    public final void apply(final float theDeltaTime, final Physics theParticleSystem) {
        if (theParticleSystem.getIntegrator() instanceof Verlet) {
            return;
        }
        if (coefficient != 0) {
            for (final Particle myParticle : theParticleSystem.particles()) {
                if (!myParticle.fixed()) {
                    myParticle.force().add(myParticle.velocity().x * -coefficient,
                                           myParticle.velocity().y * -coefficient,
                                           myParticle.velocity().z * -coefficient);
                }
            }
        }
    }

    public boolean dead() {
        return false;
    }

    public boolean active() {
        return _myActive;
    }

    public void active(boolean theActiveState) {
        _myActive = theActiveState;
    }
}
