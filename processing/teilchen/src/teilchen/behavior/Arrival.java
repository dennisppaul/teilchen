/*
 * Particles
 *
 * Copyright (C) 2012
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


import mathematik.Vector3f;

import teilchen.IBehaviorParticle;


public class Arrival
    implements IBehavior, Verhalten {

    static final long serialVersionUID = 6897889750581191781L;

    private Vector3f _mySeekPosition;

    private final Vector3f _myForce;

    private float _myWeight;

    private float _myOutterRadius;

//    private float _myArrivedRadius;

    private float _myBreakForce;

    private boolean _myArriving;

    private boolean _myArrived;

    public Arrival() {
        _myOutterRadius = 50.0f;
        _myBreakForce = 50.0f;
//        _myArrivedRadius = 5.0f;
        _myArriving = false;
        _myForce = new Vector3f();
        _mySeekPosition = new Vector3f();
        _myWeight = 1;
        _myArrived = false;
    }


    public boolean arriving() {
        return _myArriving;
    }


    public boolean arrived() {
        return _myArrived;
    }


    public Vector3f position() {
        return _mySeekPosition;
    }


    public void setPositionRef(final Vector3f thePoint) {
        _mySeekPosition = thePoint;
    }


    public void breakforce(float theBreakForce) {
        _myBreakForce = theBreakForce;
    }


    public float breakforce() {
        return _myBreakForce;
    }


    public void breakradius(float theOutterRadius) {
        _myOutterRadius = theOutterRadius;
    }


    public float breakradius() {
        return _myOutterRadius;
    }


//    public void arrivedradius(float theArrivedRadius) {
//        _myArrivedRadius = theArrivedRadius;
//    }
//
//
//    public float arrivedradius() {
//        return _myArrivedRadius;
//    }


    public void update(float theDeltaTime, IBehaviorParticle theParent) {
        _myForce.sub(_mySeekPosition, theParent.position());
        final float myDistanceToArrivalPoint = _myForce.length();

        /* set properties */
        if (myDistanceToArrivalPoint < _myOutterRadius) {
            _myArriving = true;
        } else {
            _myArriving = false;
            _myArrived = false;
        }
        /* get direction */
        if (!_myArriving) {
            /* outside of the outter radius continue 'seeking' */
            _myForce.scale(theParent.maximumInnerForce() / myDistanceToArrivalPoint);
            _myForce.sub(_myForce, theParent.velocity());
        } else {
            if (theParent.velocity().lengthSquared() > -SMALLEST_ACCEPTABLE_DISTANCE &&
                theParent.velocity().lengthSquared() < SMALLEST_ACCEPTABLE_DISTANCE) {
                /* sleep */
                _myForce.set(0, 0, 0);
                _myArrived = true;
            } else {
                /* break */
                _myForce.set(theParent.velocity().x * -_myBreakForce,
                             theParent.velocity().y * -_myBreakForce,
                             theParent.velocity().z * -_myBreakForce);
            }
        }
        _myForce.scale(weight());
    }


    public Vector3f force() {
        return _myForce;
    }


    public float weight() {
        return _myWeight;
    }


    public void weight(float theWeight) {
        _myWeight = theWeight;
    }
}
