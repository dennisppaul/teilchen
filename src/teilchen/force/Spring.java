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
package teilchen.force;

import teilchen.IConnection;
import teilchen.Particle;
import teilchen.Physics;
import static teilchen.util.Util.*;

public class Spring
        implements IForce,
                   IConnection {

    protected float mSpringConstant;

    protected float mSpringDamping;

    protected float mRestLength;

    protected Particle mA;

    protected Particle mB;

    protected boolean mOneWay;

    protected boolean mActive;

    public Spring(Particle theA, Particle theB) {
        this(theA,
             theB,
             2.0f, 0.1f,
             distance(theA.position(), theB.position()));
    }

    public Spring(Particle theA, Particle theB, float theRestLength) {
        this(theA,
             theB,
             2.0f, 0.1f,
             theRestLength);
    }

    public Spring(Particle theA,
                  Particle theB,
                  final float theSpringConstant,
                  final float theSpringDamping) {
        this(theA,
             theB,
             theSpringConstant,
             theSpringDamping,
             distance(theA.position(), theB.position()));
    }

    public Spring(final Particle theA,
                  final Particle theB,
                  final float theSpringConstant,
                  final float theSpringDamping,
                  final float theRestLength) {
        mSpringConstant = theSpringConstant;
        mSpringDamping = theSpringDamping;
        mRestLength = theRestLength;
        mA = theA;
        mB = theB;
        mOneWay = false;
        mActive = true;
    }

    public void setRestLengthByPosition() {
        mRestLength = distance(mA.position(), mB.position());
    }

    public float restlength() {
        return mRestLength;
    }

    public void restlength(float theRestLength) {
        mRestLength = theRestLength;
    }

    public final Particle a() {
        return mA;
    }

    public final Particle b() {
        return mB;
    }

    public final Particle a(Particle theA) {
        return mA = theA;
    }

    public final Particle b(Particle theB) {
        return mB = theB;
    }

    public final float currentLength() {
        return distance(mA.position(), mB.position());
    }

    /**
     * spring constant.
     *
     * @return float
     */
    public final float strength() {
        return mSpringConstant;
    }

    /**
     * spring constant.
     *
     * @param theSpringConstant float
     */
    public final void strength(float theSpringConstant) {
        mSpringConstant = theSpringConstant;
    }

    public final float damping() {
        return mSpringDamping;
    }

    public final void damping(float theSpringDamping) {
        mSpringDamping = theSpringDamping;
    }

    public void setOneWay(boolean theOneWayState) {
        mOneWay = theOneWayState;
    }

    public void apply(final float theDeltaTime, final Physics theParticleSystem) {
//        if (!mA.fixed() || !mB.fixed()) {
        float a2bX = mA.position().x - mB.position().x;
        float a2bY = mA.position().y - mB.position().y;
        float a2bZ = mA.position().z - mB.position().z;
        final float myInversDistance = fastInverseSqrt(a2bX * a2bX + a2bY * a2bY + a2bZ * a2bZ);
        final float myDistance = 1.0F / myInversDistance;

        if (myDistance == 0.0F) {
            a2bX = 0.0F;
            a2bY = 0.0F;
            a2bZ = 0.0F;
        } else {
            a2bX *= myInversDistance;
            a2bY *= myInversDistance;
            a2bZ *= myInversDistance;
        }

        final float mSpringForce = -(myDistance - mRestLength) * mSpringConstant;
        final float Va2bX = mA.velocity().x - mB.velocity().x;
        final float Va2bY = mA.velocity().y - mB.velocity().y;
        final float Va2bZ = mA.velocity().z - mB.velocity().z;
        final float mDampingForce = -mSpringDamping * (a2bX * Va2bX + a2bY * Va2bY + a2bZ * Va2bZ);
        final float r = mSpringForce + mDampingForce;
        a2bX *= r;
        a2bY *= r;
        a2bZ *= r;

        if (mOneWay) {
            if (!mB.fixed()) {
                mB.force().add(-2 * a2bX, -2 * a2bY, -2 * a2bZ);
            }
        } else {
            if (!mA.fixed()) {
                mA.force().add(a2bX, a2bY, a2bZ);
            }
            if (!mB.fixed()) {
                mB.force().add(-a2bX, -a2bY, -a2bZ);
            }
        }
//        }
    }

    protected static float fastInverseSqrt(float v) {
        final float half = 0.5f * v;
        int i = Float.floatToIntBits(v);
        i = 0x5f375a86 - (i >> 1);
        v = Float.intBitsToFloat(i);
        return v * (1.5f - half * v * v);
    }

    public boolean dead() {
        return mA.dead() || mB.dead();
    }

    public boolean active() {
        return mActive;
    }

    public void active(boolean theActiveState) {
        mActive = theActiveState;
    }
}
