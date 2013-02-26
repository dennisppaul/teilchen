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

public class Seek
        implements IBehavior, Verhalten {

    static final long serialVersionUID = -3781170603537691477L;

    private Vector3f _mySeekPosition;

    private Vector3f _myForce;

    private float _myWeight = 1;

    private float _myDistanceToPoint;

    private boolean mOverSteer;

    public Seek() {
        _mySeekPosition = new Vector3f();
        _myForce = new Vector3f();
        mOverSteer = false;
    }

    public boolean oversteer() {
        return mOverSteer;
    }

    public void oversteer(boolean pOverSteer) {
        mOverSteer = pOverSteer;
    }

    public Vector3f position() {
        return _mySeekPosition;
    }

    public void setPositionRef(final Vector3f thePoint) {
        _mySeekPosition = thePoint;
    }

    public float distancetopoint() {
        return _myDistanceToPoint;
    }

    public void update(float theDeltaTime, IBehaviorParticle theParent) {
        _myForce.sub(_mySeekPosition, theParent.position());
        _myDistanceToPoint = _myForce.length();
        if (_myDistanceToPoint > SMALLEST_ACCEPTABLE_DISTANCE) {
            _myForce.scale(theParent.maximumInnerForce() / _myDistanceToPoint);
            if (mOverSteer) {
                _myForce.sub(_myForce, theParent.velocity());
            }
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
