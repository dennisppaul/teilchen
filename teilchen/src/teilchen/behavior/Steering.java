
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

public class Steering implements IBehavior,
                                 Verhalten {

    private final Vector3f mForce;
    private final Vector3f mUPVector;

    private float mWeight;

    private float mSteering = 0.0f;

    private boolean mActive = true;

    public Steering() {
        mForce = new Vector3f();
        mUPVector = new Vector3f(0, 0, -1);
        mWeight = 1;
    }

    public void update(float theDeltaTime, IBehaviorParticle pParent) {
        if (mActive) {
            /* 2D warning -- ignoring z-axis for now */
            Vector3f mDirection = new Vector3f(pParent.velocity());
            if (mDirection.lengthSquared() > 0) {
                mDirection.normalize();
                mDirection = mathematik.Util.cross(mDirection, mUPVector);
                mDirection.scale(mSteering);
                mForce.set(mDirection);
            } else {
                mForce.set(0, 0, 0);
            }
        } else {
            mForce.set(0, 0, 0);
        }
    }

    public float steering_strength() {
        return mSteering;
    }

    public void steering_strength(float pSteering) {
        mSteering = pSteering;
    }

    public Vector3f force() {
        return mForce;
    }

    public Vector3f upvector() {
        return mUPVector;
    }

    public float weight() {
        return mWeight;
    }

    public void weight(float pWeight) {
        mWeight = pWeight;
    }

    public void active(boolean pActive) {
        mActive = pActive;
    }

}
