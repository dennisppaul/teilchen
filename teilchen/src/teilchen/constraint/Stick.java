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
package teilchen.constraint;


import mathematik.Vector3f;

import teilchen.IConnection;
import teilchen.Particle;
import teilchen.Physics;


public class Stick
        implements IConstraint,
                   IConnection {

    protected final Particle mA;

    protected final Particle mB;

    protected float mRestLength;

    protected final Vector3f mTempDistanceVector;

    protected final Vector3f mTempVector;

    protected boolean mOneWay;

    protected float mDamping;

    protected boolean mActive = true;

    protected static final float EPSILON = 0.0001f;

    public Stick(Particle theA, Particle theB) {
        this(theA,
             theB,
             theA.position().distance(theB.position()));
    }

    public Stick(final Particle theA,
                 final Particle theB,
                 final float theRestLength) {
        mRestLength = theRestLength;
        mA = theA;
        mB = theB;
        mTempDistanceVector = new Vector3f();
        mTempVector = new Vector3f();
        mOneWay = false;
        mDamping = 1f;
    }

    public void setRestLengthByPosition() {
        mRestLength = mA.position().distance(mB.position());
    }

    public float damping() {
        return mDamping;
    }

    public void damping(float theDamping) {
        mDamping = theDamping;
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

    public void setOneWay(boolean theOneWayState) {
        mOneWay = theOneWayState;
    }

    public void apply(Physics theParticleSystem) {
        if (!mActive) {
            return;
        }
        if (mA.fixed() && mB.fixed()) {
            return;
        }
        mTempDistanceVector.sub(mA.position(), mB.position());
        final float myDistanceSquared = mTempDistanceVector.lengthSquared();
        if (myDistanceSquared > 0) {
            final float myDistance = (float) Math.sqrt(myDistanceSquared);
            final float myDifference = mRestLength - myDistance;
            if (myDifference > EPSILON || myDifference < -EPSILON) {
                if (!mOneWay) {
                    final float myDifferenceScale = mDamping * 0.5f * myDifference / myDistance;
                    mTempVector.scale(myDifferenceScale, mTempDistanceVector);
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
                    final float myDifferenceScale = myDifference / myDistance;
                    mTempVector.scale(myDifferenceScale, mTempDistanceVector);
                    mB.position().sub(mTempVector);
                }
            }
        } else {
            if (mA.fixed()) {
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
    }

    public boolean active() {
        return mActive;
    }

    public void active(boolean theActiveState) {
        mActive = theActiveState;
    }
}
