/*
 * Teilchen
 *
 * This file is part of the *teilchen* library (https://github.com/dennisppaul/teilchen).
 * Copyright (c) 2020 Dennis P Paul.
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

package teilchen.constraint;

import processing.core.PVector;
import teilchen.Particle;
import teilchen.Physics;

import static processing.core.PVector.add;
import static processing.core.PVector.sub;
import static teilchen.Physics.EPSILON;
import static teilchen.util.Util.isNaN;
import static teilchen.util.Util.rotatePoint;

public class Angular implements IConstraint {

    /*
     * @todo it probably pays off to check if we deal with a 2D or 3D constraint.
     * just checking components once and then saving a lot of time.
     */

    private static final double STRENGTH = 1;
    public boolean OK;
    private final Particle mA;
    private final Particle mB;
    private final Particle mC;
    private final PVector mTempA = new PVector();
    private final PVector mTempB = new PVector();
    private final PVector mTempNormal;
    private final long mID;
    protected boolean mActive = true;
    private boolean mDead = false;
    private float mMinimumAngle;
    private float mMaximumAngle;

    public Angular(Particle pA, Particle pB, Particle pC,
                   float pMinimumAngle, float pMaximumAngle) {
        mID = Physics.getUniqueID();
        mA = pA;
        mB = pB;
        mC = pC;
        mTempNormal = new PVector();
        range(pMinimumAngle, pMaximumAngle);
    }

    public Angular(Particle pA, Particle pB, Particle pC) {
        this(pA,
             pB,
             pC,
             0, 0);
    }

    public void range(float pMinimumAngle, float pMaximumAngle) {
        mMinimumAngle = pMinimumAngle;
        mMaximumAngle = pMaximumAngle;
        sortAngles();
    }

    public float minimumAngle() {
        return mMinimumAngle;
    }

    public float maximumAngle() {
        return mMaximumAngle;
    }

    public void apply(float pDeltaTime, Physics pParticleSystem) {

        if (!mActive) {
            return;
        }

        /*
         * @todo test for special case: a and c are in the same place.
         */
        sub(mB.position(), mA.position(), mTempA);
        sub(mB.position(), mC.position(), mTempB);

        mTempA.normalize();
        mTempB.normalize();

        /*
         * @todo check for special cases! like angle being 0 etc.
         */
        /*
         * @todo check if the range exceeds PI.
         */
        if (mMinimumAngle < Math.PI && mMaximumAngle > Math.PI) {
            System.out.println("### WARNING split range and check twice.");
        }

        float myCosinusAngle = mTempA.dot(mTempB);
        if (myCosinusAngle > 1) {
            System.out.println("### WARNING myCosinusAngle > 1: " + myCosinusAngle);
            myCosinusAngle = 1;
        }

        final float myTempCosMaximumAngle = (float) Math.cos(mMaximumAngle);
        final float myTempCosMinimumAngle = (float) Math.cos(mMinimumAngle);
        final float myCosMaximumAngle = Math.max(myTempCosMinimumAngle, myTempCosMaximumAngle);
        final float myCosMinimumAngle = Math.min(myTempCosMinimumAngle, myTempCosMaximumAngle);

        calculateNormal(mTempA, mTempB);
        final boolean myLeftSide = checkForHemisphere(mTempA, mTempB);
        double myCurrentAngle = 0;

        /*
         * @todo until i the split is implemented agular constraints only work
         * for one side.
         */
        OK = myLeftSide;

        if (myLeftSide) {
            if (myCosinusAngle < myCosMinimumAngle || myCosinusAngle > myCosMaximumAngle) {
                myCurrentAngle = Math.acos(myCosinusAngle);
                OK = false;
            } else {
                OK = true;
            }
        } else {
            myCurrentAngle = 2 * Math.PI - Math.acos(myCosinusAngle);
        }

        if (!OK) {
            final double myTheta;
            if (myCurrentAngle > mMaximumAngle) {
                myTheta = mMaximumAngle - myCurrentAngle;
            } else if (myCosinusAngle < mMinimumAngle) {
                myTheta = -1 * (myCurrentAngle - mMinimumAngle);
            } else {
                System.out.println("### WARNING puzzled.");
                myTheta = 0;
            }

            correctAngle(myTheta);
        }
    }

    public boolean active() {
        return mActive;
    }

    public void active(boolean pActiveState) {
        mActive = pActiveState;
    }

    public boolean dead() { return mDead; }

    public void dead(boolean pDead) { mDead = pDead; }

    @Override
    public long ID() {
        return mID;
    }

    private void sortAngles() {
        final float mTempMaximumAngle = mMaximumAngle;
        final float mTempMinimumAngle = mMinimumAngle;
        mMaximumAngle = Math.max(mTempMaximumAngle, mTempMinimumAngle);
        mMinimumAngle = Math.min(mTempMaximumAngle, mTempMinimumAngle);
    }

    private void calculateNormal(PVector myVectorA, PVector myVectorB) {
        mTempNormal.cross(myVectorA, myVectorB);
        mTempNormal.normalize();
        if (isNaN(mTempNormal)) {
            mTempNormal.set(0, 0, 1);
            System.out.println("### WARNING can t find normal.");
        }
    }

    private void correctAngle(double pTheta) {
        if (pTheta < -EPSILON || pTheta > EPSILON) {

            PVector myOprPointOnAxis = add(mB.position(), mTempNormal);

            PVector myRotatedPointA = rotatePoint(mA.position(), pTheta * -0.5 * STRENGTH,
                                                  mB.position(),
                                                  myOprPointOnAxis);
            mA.position().set(myRotatedPointA);

            PVector myRotatedPointB = rotatePoint(mC.position(), pTheta * 0.5 * STRENGTH,
                                                  mB.position(),
                                                  myOprPointOnAxis);
            mC.position().set(myRotatedPointB);

            System.out.println("correct " + Math.toDegrees(pTheta) + " / " + mTempNormal);
        }
    }

    private boolean checkForHemisphere(PVector myVectorA, PVector myVectorB) {
        /* special case thus easy to find the direction */
        if (myVectorA.z == 0 && myVectorB.z == 0) {
            return mTempNormal.z > 0;
        } else {
            /*
             * @todo do it the hard way and create a matrix from the two vectors
             * and transform the cross vector into local space
             */
            System.out.println("### WARNING calculate for 3D plane / not implemented.");
            return true;
        }
    }
}
