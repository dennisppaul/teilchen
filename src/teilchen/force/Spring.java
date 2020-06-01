/*
 * Teilchen
 *
 * Copyright (C) 2020
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
import teilchen.IConnection;
import teilchen.Particle;
import teilchen.Physics;

import static teilchen.util.Util.distance;

public class Spring implements IForce, IConnection {

    private static final boolean USE_FAST_SQRT = true;
    protected float mSpringConstant;
    protected float mSpringDamping;
    protected float mRestLength;
    protected Particle mA;
    protected Particle mB;
    protected boolean mOneWay;
    protected boolean mActive;

    public Spring(Particle pA, Particle pB) {
        this(pA, pB, 2.0f, 0.1f, distance(pA.position(), pB.position()));
    }

    public Spring(final Particle pA,
                  final Particle pB,
                  final float pSpringConstant,
                  final float pSpringDamping,
                  final float pRestLength) {
        mSpringConstant = pSpringConstant;
        mSpringDamping = pSpringDamping;
        mRestLength = pRestLength;
        mA = pA;
        mB = pB;
        mOneWay = false;
        mActive = true;
    }

    public Spring(Particle pA, Particle pB, float pRestLength) {
        this(pA, pB, 2.0f, 0.1f, pRestLength);
    }

    public Spring(Particle pA, Particle pB, final float pSpringConstant, final float pSpringDamping) {
        this(pA, pB, pSpringConstant, pSpringDamping, distance(pA.position(), pB.position()));
    }

    public void setRestLengthByPosition() {
        mRestLength = distance(mA.position(), mB.position());
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
            // from http://paulbourke.net/miscellaneous/particle/
            final float dx = mA.position().x - mB.position().x;
            final float dy = mA.position().y - mB.position().y;
            final float dz = mA.position().z - mB.position().z;
            final float mLength = 1.0f / fastInverseSqrt(dx * dx + dy * dy + dz * dz);
            PVector f = new PVector();
            f.x = mSpringConstant * (mLength - mRestLength);
            f.x += mSpringDamping * (mA.velocity().x - mB.velocity().x) * dx / mLength;
            f.x *= -dx / mLength;
            f.y = mSpringConstant * (mLength - mRestLength);
            f.y += mSpringDamping * (mA.velocity().y - mB.velocity().y) * dy / mLength;
            f.y *= -dy / mLength;
            f.z = mSpringConstant * (mLength - mRestLength);
            f.z += mSpringDamping * (mA.velocity().z - mB.velocity().z) * dz / mLength;
            f.z *= -dz / mLength;

            if (mOneWay) {
                if (!mB.fixed()) {
                    f.mult(-2);
                    mB.force().add(f);
                }
            } else {
                if (!mA.fixed()) {
                    mA.force().add(f);
                }
                if (!mB.fixed()) {
                    mB.force().sub(f);
                }
            }

//            final PVector mAB = PVector.sub(mA.position(), mB.position());
//            final float mDistance;
//            if (USE_FAST_SQRT) {
//                mDistance = 1.0f / fastInverseSqrt(mAB.magSq());
//            } else {
//                mDistance = mAB.mag();
//            }
//            if (mDistance == 0.0f) { return; }
//
//            final float mSpringForce = -mSpringConstant * (mDistance - mRestLength); // Fspring = - k * x
//
//            PVector mABV = PVector.sub(mA.velocity(), mB.velocity());
//            Util.scale(mABV, mAB);
//            mABV.div(mDistance);
//            mABV.mult(mSpringDamping);
//            mAB.add(mABV);
//
//            mAB.div(mDistance); // normalize
//            mAB.mult(mSpringForce);
//
//            if (mOneWay) {
//                if (!mB.fixed()) {
//                    mAB.mult(-2);
//                    mB.force().add(mAB);
//                }
//            } else {
//                if (!mA.fixed()) {
//                    mA.force().add(mAB);
//                }
//                if (!mB.fixed()) {
//                    mB.force().sub(mAB);
//                }
//            }

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
        return mA.dead() || mB.dead();
    }

    public boolean active() {
        return mActive;
    }

    public void active(boolean pActiveState) {
        mActive = pActiveState;
    }

    protected static float fastInverseSqrt(float v) {
        final float half = 0.5f * v;
        int i = Float.floatToIntBits(v);
        i = 0x5f375a86 - (i >> 1);
        v = Float.intBitsToFloat(i);
        return v * (1.5f - half * v * v);
    }
}
