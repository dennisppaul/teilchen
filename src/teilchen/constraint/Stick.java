/*
 * Teilchen
 *
 * This file is part of the *teilchen* library (https://github.com/dennisppaul/teilchen).
 * Copyright (c) 2023 Dennis P Paul.
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
import teilchen.IConnection;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.util.Util;

import static teilchen.Physics.EPSILON;
import static teilchen.util.Util.distance;
import static teilchen.util.Util.lengthSquared;

public class Stick implements IConstraint, IConnection {

    protected final Particle mA;
    protected boolean mActive = true;
    protected final Particle mB;
    protected float mDamping;
    protected boolean mOneWay;
    protected float mRestLength;
    protected final PVector mTempDistanceVector;
    protected final PVector mTempVector;
    private boolean mDead = false;
    private final long mID;

    public Stick(Particle pA, Particle pB) {
        this(pA, pB, distance(pA.position(), pB.position()));
    }

    public Stick(final Particle pA, final Particle pB, final float pRestLength) {
        mID = Physics.getUniqueID();
        mRestLength = pRestLength;
        mA = pA;
        mB = pB;
        mTempDistanceVector = new PVector();
        mTempVector = new PVector();
        mOneWay = false;
        mDamping = 1f;
    }

    public void setRestLengthByPosition() {
        mRestLength = distance(mA.position(), mB.position());
    }

    public float damping() {
        return mDamping;
    }

    public void damping(float pDamping) {
        mDamping = pDamping;
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

    public void setOneWay(boolean pOneWayState) {
        mOneWay = pOneWayState;
    }

    public void apply(Physics pParticleSystem) {
        if (!mActive) {
            return;
        }
        if (mA.fixed() && mB.fixed()) {
            return;
        }
        PVector.sub(mA.position(), mB.position(), mTempDistanceVector);
        final float mDistanceSquared = lengthSquared(mTempDistanceVector);
        if (mDistanceSquared > 0) {
            final float mInvDistance = Util.fastInverseSqrt(mDistanceSquared);
            final float mDistance = 1.0f / mInvDistance;
            final float mDifference = mRestLength - mDistance;
            if (mDifference > EPSILON || mDifference < -EPSILON) {
                if (!mOneWay) {
                    final float mDifferenceScale = mDamping * 0.5f * mDifference / mDistance;
                    PVector.mult(mTempDistanceVector, mDifferenceScale, mTempVector);

                    if (mA.fixed()) {
                        mB.position().sub(mTempVector);
                        mB.position().sub(mTempVector);
                    } else if (mB.fixed()) {
                        mA.position().add(mTempVector);
                        mA.position().add(mTempVector);
                    } else {
                        mA.position().add(mTempVector);
                        mB.position().sub(mTempVector);
                    }
                } else {
                    final float mDifferenceScale = mDifference * mInvDistance;
                    PVector.mult(mTempDistanceVector, mDifferenceScale, mTempVector);
                    mB.position().sub(mTempVector);
                }
            }
        } else if (mA.fixed()) {
            mB.position().set(mA.position());
            mB.position().x += mRestLength;
        } else if (mB.fixed()) {
            mA.position().set(mB.position());
            mA.position().x += mRestLength;
        } else {
            mB.position().set(mA.position());
            mA.position().x -= mRestLength / 2;
            mB.position().x += mRestLength / 2;
        }
    }

    public boolean active() {
        return mActive;
    }

    public void active(boolean pActiveState) {
        mActive = pActiveState;
    }

    public boolean dead() {
        return mDead;
    }

    public void dead(boolean pDead) {
        mDead = pDead;
    }

    public long ID() {
        return mID;
    }
}
