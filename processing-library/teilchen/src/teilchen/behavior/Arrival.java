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
import teilchen.util.Util;

public class Arrival
        implements IBehavior, Verhalten {

    static final long serialVersionUID = 6897889750581191781L;

    private PVector mSeekPosition;

    private final PVector mForce;

    private float mWeight;

    private float mBreakRadius;

    private float mBreakForce;

    private boolean mIsArriving;

    private boolean mHasArrived;

    private boolean mOverSteer;

    public Arrival() {
        mBreakRadius = 50.0f;
        mBreakForce = 50.0f;
        mForce = new PVector();
        mSeekPosition = new PVector();
        mWeight = 1;
        mIsArriving = false;
        mHasArrived = false;
        mOverSteer = true;
    }

    public boolean arriving() {
        return mIsArriving;
    }

    public boolean arrived() {
        return mHasArrived;
    }

    public boolean oversteer() {
        return mOverSteer;
    }

    public void oversteer(boolean pOverSteer) {
        mOverSteer = pOverSteer;
    }

    public PVector position() {
        return mSeekPosition;
    }

    public void setPositionRef(final PVector pPoint) {
        mSeekPosition = pPoint;
    }

    public void breakforce(float pBreakForce) {
        mBreakForce = pBreakForce;
    }

    public float breakforce() {
        return mBreakForce;
    }

    public void breakradius(float pOutterRadius) {
        mBreakRadius = pOutterRadius;
    }

    public float breakradius() {
        return mBreakRadius;
    }

    public void update(float theDeltaTime, IBehaviorParticle pParent) {
        sub(mSeekPosition, pParent.position(), mForce);
        final float myDistanceToArrivalPoint = mForce.mag();

        /* get direction */
        if (myDistanceToArrivalPoint < mBreakRadius) {
            mIsArriving = true;
            final float mSpeed = pParent.velocity().mag();
            final float MIN_ACCEPTABLE_SPEED = 10.0f;
            if (mSpeed < MIN_ACCEPTABLE_SPEED) {
                /* sleep */
                mForce.set(0, 0, 0);
                mHasArrived = true;
            } else {
                /* break */
                final boolean USE_WEIGHTED_BREAK_FORCE = true;
                if (USE_WEIGHTED_BREAK_FORCE) {
                    final float mRatio = myDistanceToArrivalPoint / mBreakRadius;

                    final PVector mBreakForceVector = Util.clone(pParent.velocity());
                    mBreakForceVector.mult(-mBreakForce);
                    mBreakForceVector.mult(1.0f - mRatio);

                    final PVector mSteerForce = Util.clone(mForce);
                    mSteerForce.mult(pParent.maximumInnerForce() / myDistanceToArrivalPoint);
                    mSteerForce.mult(mRatio);

                    add(mBreakForceVector, mSteerForce, mForce);
                } else {
                    mForce.set(pParent.velocity().x * -mBreakForce,
                               pParent.velocity().y * -mBreakForce,
                               pParent.velocity().z * -mBreakForce);
                }
                mHasArrived = false;
            }
        } else {
            /* outside of the outter radius continue 'seeking' */
            mForce.mult(pParent.maximumInnerForce() / myDistanceToArrivalPoint);
            if (mOverSteer) {
                sub(mForce, pParent.velocity(), mForce);
            }
            mIsArriving = false;
            mHasArrived = false;
        }
        mForce.mult(weight());
    }

    public PVector force() {
        return mForce;
    }

    public float weight() {
        return mWeight;
    }

    public void weight(float pWeight) {
        mWeight = pWeight;
    }
}
