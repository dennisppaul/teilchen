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

    private Vector3f mSeekPosition;

    private Vector3f mForce;

    private float mWeight = 1;

    private float mDistanceToPoint;

    private boolean mOverSteer;

    public Seek() {
        mSeekPosition = new Vector3f();
        mForce = new Vector3f();
        mOverSteer = false;
    }

    public boolean oversteer() {
        return mOverSteer;
    }

    public void oversteer(boolean pOverSteer) {
        mOverSteer = pOverSteer;
    }

    public Vector3f position() {
        return mSeekPosition;
    }

    public void setPositionRef(final Vector3f thePoint) {
        mSeekPosition = thePoint;
    }

    public float distancetopoint() {
        return mDistanceToPoint;
    }

    public void update(float theDeltaTime, IBehaviorParticle theParent) {
        mForce.sub(mSeekPosition, theParent.position());
        mDistanceToPoint = mForce.length();
        if (mDistanceToPoint > SMALLEST_ACCEPTABLE_DISTANCE) {
            mForce.scale(theParent.maximumInnerForce() / mDistanceToPoint);
            if (mOverSteer) {
                mForce.sub(mForce, theParent.velocity());
            }
            mForce.scale(weight());
        } else {
            mForce.set(0, 0, 0);
        }
    }

    public Vector3f force() {
        return mForce;
    }

    public float weight() {
        return mWeight;
    }

    public void weight(float theWeight) {
        mWeight = theWeight;
    }
}
