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

package teilchen.force;

import processing.core.PVector;
import teilchen.Particle;
import teilchen.Physics;

import static processing.core.PVector.sub;
import static teilchen.util.Util.lengthSquared;

public class Attractor
        implements IForce {

    protected final PVector mTemp = new PVector();
    protected PVector mPosition;
    protected float mStrength;
    protected float mRadius;
    private boolean mActive;
    private boolean mDead = false;
    private final long mID;

    public Attractor() {
        mID = Physics.getUniqueID();
        mPosition = new PVector();
        mRadius = 100;
        mStrength = 1;
        mActive = true;
    }

    public PVector position() {
        return mPosition;
    }

    public void setPositionRef(PVector pPosition) {
        mPosition = pPosition;
    }

    public float strength() {
        return mStrength;
    }

    public void strength(float pStrength) {
        mStrength = pStrength;
    }

    public float radius() {
        return mRadius;
    }

    public void radius(float pRadius) {
        mRadius = pRadius;
    }

    public void apply(float pDeltaTime, Physics pParticleSystem) {
        if (mStrength != 0) {
            for (final Particle mParticle : pParticleSystem.particles()) {
                /* each particle */
                if (!mParticle.fixed()) {
                    sub(mPosition, mParticle.position(), mTemp);
                    final float mDistance = fastInverseSqrt(1 / lengthSquared(mTemp));
                    if (mDistance < mRadius) {
                        float mFallOff = 1f - mDistance / mRadius;
                        final float mForce = mFallOff * mFallOff * mStrength;
                        mTemp.mult(mForce / mDistance);
                        if (!mParticle.fixed()) {
                            mParticle.force().add(mTemp);
                        }
                    }
                }
            }
        }
    }

    public boolean dead() {
        return mDead;
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

    protected static float fastInverseSqrt(float v) {
        final float half = 0.5f * v;
        int i = Float.floatToIntBits(v);
        i = 0x5f375a86 - (i >> 1);
        v = Float.intBitsToFloat(i);
        return v * (1.5f - half * v * v);
    }
}
