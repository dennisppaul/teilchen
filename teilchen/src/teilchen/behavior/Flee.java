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

    private Vector3f mFleePosition;

    private Vector3f mForce;

    private float mWeight = 1;

    public Flee() {
        mFleePosition = new Vector3f();
        mForce = new Vector3f();
    }

    public Vector3f position() {
        return mFleePosition;
    }

    public void setPositionRef(final Vector3f thePoint) {
        mFleePosition = thePoint;
    }

    public void update(float theDeltaTime, IBehaviorParticle theParent) {
        mForce.sub(theParent.position(), mFleePosition);
        final float myDistanceToPoint = mForce.length();
        if (myDistanceToPoint > SMALLEST_ACCEPTABLE_DISTANCE) {
            mForce.scale(theParent.maximumInnerForce() / myDistanceToPoint);
            mForce.sub(mForce, theParent.velocity());
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
