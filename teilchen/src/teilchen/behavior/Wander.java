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

    private Vector3f mForce;

    private float mSteeringStrength;

    private float mSteeringOffset;

    private float mCurrentSteeringStrength;

    private Vector3f mUpVector;

    private float mWeight;

    private final Random mRandom;

    private boolean mActive;

    public Wander() {
        mRandom = new Random();

        mForce = new Vector3f();
        mSteeringStrength = 10f;
        mSteeringOffset = 5f;

        mUpVector = new Vector3f(0, 0, 1);

        mWeight = 1;
        mActive = true;
    }

    public boolean active() {
        return mActive;
    }

    public void active(boolean pActive) {
        mActive = pActive;
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

    public void update(float pDeltaTime, IBehaviorParticle pParent) {
        if (mActive && pParent.velocity().length() > 0) {
            mCurrentSteeringStrength += mRandom.getFloat(-0.5f, 0.5f) * mSteeringOffset;
            mCurrentSteeringStrength = Math.max(Math.min(mCurrentSteeringStrength, mSteeringStrength), -mSteeringStrength);

            final Vector3f mWanderTarget = mathematik.Util.cross(mUpVector, pParent.velocity());
            mWanderTarget.normalize();
            mWanderTarget.scale(mCurrentSteeringStrength);
            if (mWanderTarget.isNaN()) {
                mForce.set(0, 0, 0);
            } else {
                mForce.scale(mWeight, mWanderTarget);
            }
        } else {
            mForce.set(0, 0, 0);
        }
    }

    public Vector3f upvector() {
        return mUpVector;
    }

    public float steeringstrength() {
        return mSteeringStrength;
    }

    public void steeringstrength(final float theSteeringStrength) {
        mSteeringStrength = theSteeringStrength;
    }

    public float steeringoffset() {
        return mSteeringOffset;
    }

    public void steeringoffset(final float theSteeringOffset) {
        mSteeringOffset = theSteeringOffset;
    }
}
