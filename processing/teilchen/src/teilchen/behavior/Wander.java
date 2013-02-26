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


package teilchen.behavior;


import mathematik.Random;
import mathematik.Vector3f;

import teilchen.IBehaviorParticle;


public class Wander
    implements IBehavior {

    static final long serialVersionUID = 4957162698340669663L;

    private Vector3f _myTempForce;

    private float _mySteeringStrength;

    private float _mySteeringOffset;

    private float _myCurrentSteeringStrength;

    private Vector3f _myWanderTarget;

    private Vector3f _myUpVector;

    private float _myWeight;

    private final Random _myRandom;

    public Wander() {
        _myRandom = new Random();

        _myTempForce = new Vector3f();
        _mySteeringStrength = 10f;
        _mySteeringOffset = 5f;

        _myWanderTarget = new Vector3f();
        _myUpVector = new Vector3f(0, 0, 1);

        _myWeight = 1;
    }


    public Vector3f force() {
        return _myTempForce;
    }


    public float weight() {
        return _myWeight;
    }


    public void weight(float theWeight) {
        _myWeight = theWeight;
    }


    public void update(float theDeltaTime, IBehaviorParticle theParent) {
        if (theParent.velocity().length() > 0) {
            _myCurrentSteeringStrength += _myRandom.getFloat( -0.5f, 0.5f) * _mySteeringOffset;
            _myCurrentSteeringStrength = Math.max(Math.min(_myCurrentSteeringStrength, _mySteeringStrength),
                                                  -_mySteeringStrength);

            _myWanderTarget.cross(_myUpVector, theParent.velocity());
            _myWanderTarget.normalize();
            _myWanderTarget.scale(_myCurrentSteeringStrength);
            if (_myWanderTarget.isNaN()) {
                _myTempForce.set(0, 0, 0);
            } else {
                _myTempForce.scale(_myWeight, _myWanderTarget);
            }
        } else {
            _myTempForce.set(0, 0, 0);
        }
    }


    public Vector3f upvector() {
        return _myUpVector;
    }


    public float steeringstrength() {
        return _mySteeringStrength;
    }


    public void steeringstrength(final float theSteeringStrength) {
        _mySteeringStrength = theSteeringStrength;
    }


    public float steeringoffset() {
        return _mySteeringOffset;
    }


    public void steeringoffset(final float theSteeringOffset) {
        _mySteeringOffset = theSteeringOffset;
    }
}
