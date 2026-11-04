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
        if (mA.fixed() && mB.fixed()) {
            return;
        }
        /* see http://paulbourke.net/miscellaneous/particle/ */
        float nX = mA.position().x - mB.position().x;
        float nY = mA.position().y - mB.position().y;
        float nZ = mA.position().z - mB.position().z;
        final float mDistanceSquared = nX * nX + nY * nY + nZ * nZ;
        if (mDistanceSquared == 0.0f) {
            return; /* prevent division by zero */
        }
        final float mInvDistance = USE_FAST_SQRT ? Util.fastInverseSqrt(mDistanceSquared) :
                                   (float) (1.0 / Math.sqrt(mDistanceSquared));
        final float mDistance = mDistanceSquared * mInvDistance;
        /* unit vector from B to A */
        nX *= mInvDistance;
        nY *= mInvDistance;
        nZ *= mInvDistance;

        /* Fspring = -k * x */
        final float mSpringForce = -mSpringConstant * (mDistance - mRestLength);
        /* Fdamping = -c * ( relative velocity projected onto spring axis ) */
        final float vX = mA.velocity().x - mB.velocity().x;
        final float vY = mA.velocity().y - mB.velocity().y;
        final float vZ = mA.velocity().z - mB.velocity().z;
        final float mDampingForce = -mSpringDamping * (vX * nX + vY * nY + vZ * nZ);
        final float r = mSpringForce + mDampingForce;
        final float fX = nX * r;
        final float fY = nY * r;
        final float fZ = nZ * r;

        if (mOneWay) {
            if (!mB.fixed()) {
                mB.force().add(-2 * fX, -2 * fY, -2 * fZ);
            }
        } else {
            if (!mA.fixed()) {
                mA.force().add(fX, fY, fZ);
            }
            if (!mB.fixed()) {
                mB.force().add(-fX, -fY, -fZ);
            }
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
