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


import mathematik.Vector3f;

import teilchen.IBehaviorParticle;


public class Flee
    implements IBehavior, Verhalten {

    static final long serialVersionUID = -6530887943347815188L;

    private Vector3f _myFleePosition;

    private Vector3f _myForce;

    private float _myWeight = 1;

    public Flee() {
        _myFleePosition = new Vector3f();
        _myForce = new Vector3f();
    }


    public Vector3f position() {
        return _myFleePosition;
    }


    public void setPositionRef(final Vector3f thePoint) {
        _myFleePosition = thePoint;
    }


    public void update(float theDeltaTime, IBehaviorParticle theParent) {
        _myForce.sub(theParent.position(), _myFleePosition);
        final float myDistanceToPoint = _myForce.length();
        if (myDistanceToPoint > SMALLEST_ACCEPTABLE_DISTANCE) {
            _myForce.scale(theParent.maximumInnerForce() / myDistanceToPoint);
            _myForce.sub(_myForce, theParent.velocity());
            _myForce.scale(weight());
        } else {
            _myForce.set(0, 0, 0);
        }
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
