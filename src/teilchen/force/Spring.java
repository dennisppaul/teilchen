/*
 * Teilchen
 *
 * This file is part of the *teilchen* library (https://github.com/dennisppaul/teilchen).
 * Copyright (c) 2024 Dennis P Paul.
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

package teilchen.force;

import processing.core.PVector;
import teilchen.Connection;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.util.Util;

import static teilchen.util.Util.distance;

public class Spring implements Force, Connection {

    private static final boolean USE_FAST_SQRT = true;
    protected Particle mA;
    protected boolean mActive;
    protected Particle mB;
    protected boolean mDead;
    protected boolean mOneWay;
    protected float mRestLength;
    protected float mSpringConstant;
    protected float mSpringDamping;
    private final long mID;

    public Spring(Particle pA, Particle pB) {
        this(pA, pB, 2.0f, 0.1f, distance(pA.position(), pB.position()));
    }

    public Spring(final Particle pA,
                  final Particle pB,
                  final float pSpringConstant,
                  final float pSpringDamping,
                  final float pRestLength) {
        mID = Physics.getUniqueID();
        mSpringConstant = pSpringConstant;
        mSpringDamping = pSpringDamping;
        mRestLength = pRestLength;
        mA = pA;
        mB = pB;
        mOneWay = false;
        mActive = true;
        mDead = false;
    }

    public Spring(Particle pA, Particle pB, float pRestLength) {
        this(pA, pB, 2.0f, 0.1f, pRestLength);
    }

    public Spring(Particle pA, Particle pB, final float pSpringConstant, final float pSpringDamping) {
        this(pA, pB, pSpringConstant, pSpringDamping, distance(pA.position(), pB.position()));
    }

    public void setRestLengthByPosition() {
        restlength(distance(mA.position(), mB.position()));
    }

    public float restlength() {
        return mRestLength;
    }

    public void restlength(float pRestLength) {
        mRestLength = pRestLength;
    }

    public final Particle a() {
        return mA;
    }

    public final Particle b() {
        return mB;
    }

    public final Particle a(Particle pA) {
        return mA = pA;
    }

    public final Particle b(Particle pB) {
        return mB = pB;
    }

    public final float currentLength() {
        return distance(mA.position(), mB.position());
    }

    public final float strength() {
        return mSpringConstant;
    }

    public final void strength(float pSpringConstant) {
        mSpringConstant = pSpringConstant;
    }

    public final float damping() {
        return mSpringDamping;
    }

    public final void damping(float pSpringDamping) {
        mSpringDamping = pSpringDamping;
    }

    public void setOneWay(boolean pOneWayState) {
        mOneWay = pOneWayState;
    }

    public void apply(final float pDeltaTime, final Physics pParticleSystem) {
        if (!(mA.fixed() && mB.fixed())) {
            final PVector mAB = PVector.sub(mA.position(), mB.position());
            final float mInvDistance;
            final float mDistance;
            if (USE_FAST_SQRT) {
                final float mInvDistanceSquared = mAB.magSq();
                if (mInvDistanceSquared == 0.0f) {
                    return; /* prevent division by zero */
                }
                mInvDistance = Util.fastInverseSqrt(mInvDistanceSquared);
                mDistance = 1.0f / mInvDistance;
            } else {
                mDistance = mAB.mag();
                if (mDistance == 0.0f) {
                    return; /* prevent division by zero */
                }
                mInvDistance = 1.0f / mDistance;
            }
            final float mSpringForce = -mSpringConstant * (mDistance - mRestLength); // Fspring = - k * x
            final PVector mABV = PVector.sub(mA.velocity(), mB.velocity());
            final PVector mForce = new PVector().set(-mSpringForce, -mSpringForce, -mSpringForce);
            Util.scale(mABV, mAB);
            mABV.mult(mInvDistance);
            mABV.mult(mSpringDamping);
            mForce.add(mABV);
            mAB.mult(-mInvDistance);
            Util.scale(mForce, mAB);

            if (mOneWay) {
                if (!mB.fixed()) {
                    mForce.mult(-2);
                    mB.force().add(mForce);
                }
            } else {
                if (!mA.fixed()) {
                    mA.force().add(mForce);
                }
                if (!mB.fixed()) {
                    mB.force().sub(mForce);
                }
            }
            // see http://paulbourke.net/miscellaneous/particle/

//            float a2bX = mA.position().x - mB.position().x;
//            float a2bY = mA.position().y - mB.position().y;
//            float a2bZ = mA.position().z - mB.position().z;
//            final float mInversDistance = fastInverseSqrt(a2bX * a2bX + a2bY * a2bY + a2bZ * a2bZ);
//            final float mDistance = 1.0f / mInversDistance;
//
//            if (mDistance == 0.0f) {
//                a2bX = 0.0f;
//                a2bY = 0.0f;
//                a2bZ = 0.0f;
//            } else {
//                a2bX *= mInversDistance;
//                a2bY *= mInversDistance;
//                a2bZ *= mInversDistance;
//            }
//
//            final float mSpringForce = -(mDistance - mRestLength) * mSpringConstant;
//            final float Va2bX = mA.velocity().x - mB.velocity().x;
//            final float Va2bY = mA.velocity().y - mB.velocity().y;
//            final float Va2bZ = mA.velocity().z - mB.velocity().z;
//            final float mDampingForce = -mSpringDamping * (a2bX * Va2bX + a2bY * Va2bY + a2bZ * Va2bZ);
//            final float r = mSpringForce + mDampingForce;
//            a2bX *= r;
//            a2bY *= r;
//            a2bZ *= r;
//
//            if (mOneWay) {
//                if (!mB.fixed()) {
//                    mB.force().add(-2 * a2bX, -2 * a2bY, -2 * a2bZ);
//                }
//            } else {
//                if (!mA.fixed()) {
//                    mA.force().add(a2bX, a2bY, a2bZ);
//                }
//                if (!mB.fixed()) {
//                    mB.force().add(-a2bX, -a2bY, -a2bZ);
//                }
//            }
        }
    }

    public boolean dead() {
        return mA.dead() || mB.dead() || mDead;
    }

    public void dead(boolean pDead) {
        mDead = pDead;
    }

    public boolean active() {
        return mActive;
    }

    public void active(boolean pActiveState) {
        mActive = pActiveState;
    }

    public long ID() {
        return mID;
    }
}
