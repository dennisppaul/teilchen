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


public class Gravity
        implements IForce {

    private boolean _myActive;

    private Vector3f _myForce;

    public Gravity(final Vector3f theForce) {
        _myActive = true;
        _myForce = theForce;
    }

    public Gravity() {
        this(new Vector3f(0, 9.81f, 0));
    }

    public Gravity(float theX, float theY, float theZ) {
        this(new Vector3f(theX, theY, theZ));
    }

    public Vector3f force() {
        return _myForce;
    }

    public void apply(final float theDeltaTime, final Physics theParticleSystem) {
        for (final Particle myParticle : theParticleSystem.particles()) {
            if (!myParticle.fixed()) {
                myParticle.force().add(_myForce);
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
