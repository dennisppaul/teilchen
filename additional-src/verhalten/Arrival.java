/*
 * Verhalten
 *
 * Copyright (C) 2005 Patrick Kochlik + Dennis Paul
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


/*
 * 'arrival' steers towards a defined pointa and decelerates while coming closer.
 */


package verhalten;


import mathematik.Vector3f;


/**
 *
 * @deprecated PORTED TO PARTICLES
 */

public class Arrival
    implements IVerhaltenBehavior {

    private Vector3f _myArrivalPosition;

    private Vector3f _myDebugResult;

    private float _myOutterRadius;

    private float _myInnerRadius;

    private float _myBreakSpeed;

    private boolean arrived;

    private boolean arriving;

    public Arrival() {
        _myDebugResult = new Vector3f();
        _myArrivalPosition = new Vector3f();
        _myOutterRadius = 200.0f;
        _myInnerRadius = 10.0f;
        _myBreakSpeed = 1.0f;
        arrived = false;
        arriving = false;
    }


    public void get(final Engine theEngine,
                    Vector3f theDirection) {
        theDirection.sub(_myArrivalPosition, theEngine.position());
        float myDistanceToArrivalPoint = theDirection.length();
        /* set properties */
        if (myDistanceToArrivalPoint < _myOutterRadius) {
            arriving = true;
        } else {
            arriving = false;
        }
        if (myDistanceToArrivalPoint < _myInnerRadius) {
            arrived = true;
        } else {
            arrived = false;
        }
        /* get direction */
        if (myDistanceToArrivalPoint > Verhalten.SMALLEST_ACCEPTABLE_DISTANCE) {
            if (!arriving && !arrived) {
                /* outside of the outter radius continue 'seeking' */
                theDirection.scale(1 / myDistanceToArrivalPoint);
                theDirection.scale(theEngine.getMaximumSpeed());
            }
            if (arriving && !arrived) {
                /* break */
                float mySpeed = myDistanceToArrivalPoint / _myBreakSpeed;
                mySpeed = Math.min(mySpeed, _myOutterRadius);
//                mySpeed = Math.min(mySpeed, theEngine.getMaximumSpeed());
                theDirection.scale(mySpeed / myDistanceToArrivalPoint, theDirection);
            }
            theDirection.sub(theDirection, theEngine.velocity());
        } else {
            theDirection.set(0, 0, 0);
        }
        _myDebugResult.set(theDirection);
    }


    public void setPosition(Vector3f thePosition) {
        _myArrivalPosition.set(thePosition);
    }


    public void setPositionRef(Vector3f thePosition) {
        _myArrivalPosition = thePosition;
    }


    public Vector3f getPosition() {
        return _myArrivalPosition;
    }


    public boolean hasArrived() {
        return arrived;
    }


    public boolean isArriving() {
        return arriving;
    }


    public float getOutterRadius() {
        return _myOutterRadius;
    }


    public void setOutterRadius(float theOutterRadius) {
        _myOutterRadius = theOutterRadius;
    }


    public float getInnerRadius() {
        return _myInnerRadius;
    }


    public void setInnerRadius(float theInnerRadius) {
        _myInnerRadius = theInnerRadius;
    }


    public float getBreakSpeed() {
        return _myBreakSpeed;
    }


    public void setBreakSpeed(float theBreakSpeed) {
        _myBreakSpeed = theBreakSpeed;
    }


    public Vector3f direction() {
        return _myDebugResult;
    }
}
