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
 * the flee behavior steers away from a defined point.
 */


package verhalten;


import mathematik.Vector3f;


/**
 * @deprecated PORTED TO PARTICLES
 */

public class Flee
    implements IVerhaltenBehavior {

    private Vector3f _myPoint;

    private Vector3f _myDebugResult;

    public Flee() {
        _myPoint = new Vector3f();
        _myDebugResult = new Vector3f();
    }


    public void get(final Engine theEngine,
                    Vector3f theDirection) {
        theDirection.sub(theEngine.position(), _myPoint);
        float myDistanceToPoint = theDirection.length();
        if (myDistanceToPoint > Verhalten.SMALLEST_ACCEPTABLE_DISTANCE) {
            theDirection.scale(1 / myDistanceToPoint);
            theDirection.scale(theEngine.getMaximumSpeed());
            theDirection.sub(theDirection, theEngine.velocity());
        } else {
            theDirection.set(0, 0, 0);
        }
        _myDebugResult.set(theDirection);
    }


    public void setPoint(final Vector3f thePoint) {
        _myPoint.set(thePoint);
    }


    public void setPointRef(final Vector3f thePoint) {
        _myPoint = thePoint;
    }


    public Vector3f getPoint() {
        return _myPoint;
    }


    public Vector3f direction() {
        return _myDebugResult;
    }
}
