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


public class Attractor
        implements IForce {

    protected Vector3f _myPosition;

    protected float _myStrength;

    protected float _myRadius;

    protected final Vector3f myTemp = new Vector3f();

    private boolean _myActive;

    public Attractor() {
        _myPosition = new Vector3f();
        _myRadius = 100;
        _myStrength = 1;
        _myActive = true;
    }

    public Vector3f position() {
        return _myPosition;
    }

    public void setPositionRef(Vector3f thePosition) {
        _myPosition = thePosition;
    }

    public float strength() {
        return _myStrength;
    }

    public void strength(float theStrength) {
        _myStrength = theStrength;
    }

    public float radius() {
        return _myRadius;
    }

    public void radius(float theRadius) {
        _myRadius = theRadius;
    }

    public void apply(float theDeltaTime, Physics theParticleSystem) {
        if (_myStrength != 0) {
            for (final Particle myParticle : theParticleSystem.particles()) {
                /* each particle */
                if (!myParticle.fixed()) {
                    myTemp.sub(_myPosition, myParticle.position());
                    final float myDistance = fastInverseSqrt(1 / myTemp.lengthSquared());
                    if (myDistance < _myRadius) {
                        float myFallOff = 1f - myDistance / _myRadius;
                        final float myForce = myFallOff * myFallOff * _myStrength;
                        myTemp.scale(myForce / myDistance);
                        if (!myParticle.fixed()) {
                            myParticle.force().add(myTemp);
                        }
                    }
                }
            }
        }
    }

    protected static float fastInverseSqrt(float v) {
        final float half = 0.5f * v;
        int i = Float.floatToIntBits(v);
        i = 0x5f375a86 - (i >> 1);
        v = Float.intBitsToFloat(i);
        return v * (1.5f - half * v * v);
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
