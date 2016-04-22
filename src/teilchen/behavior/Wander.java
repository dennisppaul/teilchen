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
import static processing.core.PVector.*;
import teilchen.IBehaviorParticle;
import teilchen.util.Random;
import static teilchen.util.Util.isNaN;

public class Wander
        implements IBehavior {

    static final long serialVersionUID = 4957162698340669663L;

    private final PVector mForce;

    private float mSteeringStrength;

    private float mSteeringOffset;

    private float mCurrentSteeringStrength;

    private final PVector mUpVector;

    private float mWeight;

    private final Random mRandom;

    private boolean mActive;

    public Wander() {
        mRandom = new Random();

        mForce = new PVector();
        mSteeringStrength = 10f;
        mSteeringOffset = 5f;

        mUpVector = new PVector(0, 0, 1);

        mWeight = 1;
        mActive = true;
    }

    public boolean active() {
        return mActive;
    }

    public void active(boolean pActive) {
        mActive = pActive;
    }

    public PVector force() {
        return mForce;
    }

    public float weight() {
        return mWeight;
    }

    public void weight(float theWeight) {
        mWeight = theWeight;
    }

    public void update(float pDeltaTime, IBehaviorParticle pParent) {
        if (mActive && pParent.velocity().mag() > 0) {
            mCurrentSteeringStrength += mRandom.getFloat(-0.5f, 0.5f) * mSteeringOffset;
            mCurrentSteeringStrength = Math.max(Math.min(mCurrentSteeringStrength, mSteeringStrength), -mSteeringStrength);

            final PVector mWanderTarget = new PVector();
            cross(mUpVector, pParent.velocity(), mWanderTarget);
            mWanderTarget.normalize();
            mWanderTarget.mult(mCurrentSteeringStrength);
            if (isNaN(mWanderTarget)) {
                mForce.set(0, 0, 0);
            } else {
                mult(mWanderTarget, mWeight, mForce);
            }
        } else {
            mForce.set(0, 0, 0);
        }
    }

    public PVector upvector() {
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
