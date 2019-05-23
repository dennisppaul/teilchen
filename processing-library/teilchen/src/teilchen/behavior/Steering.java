
/*
 * Teilchen
 *
 * Copyright (C) 2015
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

import processing.core.PVector;
import teilchen.IBehaviorParticle;

public class Steering implements IBehavior,
                                 Verhalten {

    private final PVector mForce;
    private final PVector mUPVector;

    private float mWeight;

    private float mSteering = 0.0f;

    private boolean mActive = true;

    public Steering() {
        mForce = new PVector();
        mUPVector = new PVector(0, 0, -1);
        mWeight = 1;
    }

    public void update(float theDeltaTime, IBehaviorParticle pParent) {
        if (mActive) {
            /* 2D warning -- ignoring z-axis for now */
            PVector mDirection = teilchen.util.Util.clone(pParent.velocity());
            if (teilchen.util.Util.lengthSquared(mDirection) > 0) {
                mDirection.normalize();
                PVector r = new PVector();
                PVector.cross(mDirection, mUPVector, r);
                mDirection.set(r);
                mDirection.mult(mSteering);
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

    public PVector force() {
        return mForce;
    }

    public PVector upvector() {
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
