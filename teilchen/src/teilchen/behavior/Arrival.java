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


public class Arrival
        implements IBehavior, Verhalten {

    static final long serialVersionUID = 6897889750581191781L;

    private Vector3f mSeekPosition;

    private final Vector3f mForce;

    private float mWeight;

    private float mBreakRadius;

    private float mBreakForce;

    private boolean mIsArriving;

    private boolean mHasArrived;

    private boolean mOverSteer;

    public Arrival() {
        mBreakRadius = 50.0f;
        mBreakForce = 50.0f;
        mForce = new Vector3f();
        mSeekPosition = new Vector3f();
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

    public Vector3f position() {
        return mSeekPosition;
    }

    public void setPositionRef(final Vector3f pPoint) {
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
        mForce.sub(mSeekPosition, pParent.position());
        final float myDistanceToArrivalPoint = mForce.length();

        /* get direction */
        if (myDistanceToArrivalPoint < mBreakRadius) {
            mIsArriving = true;
            final float mSpeed = pParent.velocity().length();
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

                    final Vector3f mBreakForceVector = new Vector3f(pParent.velocity());
                    mBreakForceVector.scale(-mBreakForce);
                    mBreakForceVector.scale(1.0f - mRatio);

                    final Vector3f mSteerForce = new Vector3f(mForce);
                    mSteerForce.scale(pParent.maximumInnerForce() / myDistanceToArrivalPoint);
                    mSteerForce.scale(mRatio);

                    mForce.add(mBreakForceVector, mSteerForce);
                } else {
                    mForce.set(pParent.velocity().x * -mBreakForce,
                               pParent.velocity().y * -mBreakForce,
                               pParent.velocity().z * -mBreakForce);
                }
                mHasArrived = false;
            }
        } else {
            /* outside of the outter radius continue 'seeking' */
            mForce.scale(pParent.maximumInnerForce() / myDistanceToArrivalPoint);
            if (mOverSteer) {
                mForce.sub(mForce, pParent.velocity());
            }
            mIsArriving = false;
            mHasArrived = false;
        }
        mForce.scale(weight());
    }

    public Vector3f force() {
        return mForce;
    }

    public float weight() {
        return mWeight;
    }

    public void weight(float pWeight) {
        mWeight = pWeight;
    }
}
