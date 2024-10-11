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
import teilchen.IParticle;
import teilchen.Physics;

import static processing.core.PConstants.TWO_PI;
import static teilchen.util.Util.distance;

public class MuscleSpring extends Spring {

    private float mAmplitude = 1.0f;
    private float mCurrentTime;
    private float mFrequency = TWO_PI;
    private float mInitialRestLength;
    private final boolean mPaused;
    private float mPhaseShift = 0.0f;

    public MuscleSpring(IParticle pA, IParticle pB) {
        super(pA, pB);
        mInitialRestLength = mRestLength;
        mPaused = false;
    }

    public MuscleSpring(final IParticle pA,
                        final IParticle pB,
                        final float pSpringConstant,
                        final float pSpringDamping,
                        final float pRestLength) {
        super(pA, pB, pSpringConstant, pSpringDamping, pRestLength);
        mInitialRestLength = mRestLength;
        mPaused = false;
    }

    public void setRestLengthByPosition() {
        PVector.dist(mA.position(), mB.position());
        mInitialRestLength = distance(mA.position(), mB.position());
    }

    public float restlength() {
        return mInitialRestLength;
    }

    public void restlength(float pRestLength) {
        mInitialRestLength = pRestLength;
    }

    public void apply(final float pDeltaTime, final Physics pParticleSystem) {
        if (!mPaused) {
            mCurrentTime += pDeltaTime;

            final float mOffset = (float) Math.sin(mCurrentTime * mFrequency + mPhaseShift) * mAmplitude;
            mRestLength = mInitialRestLength + mOffset;
        }
        super.apply(pDeltaTime, pParticleSystem);
    }

    public void frequency(final float pFrequency) {
        mFrequency = pFrequency;
    }

    public float frequency() {
        return mFrequency;
    }

    public void amplitude(final float pAmplitude) {
        mAmplitude = pAmplitude;
    }

    public float amplitude() {
        return mAmplitude;
    }

    public void phaseshift(final float pPhaseShift) {
        mPhaseShift = pPhaseShift;
    }

    public float phaseshift() {
        return mPhaseShift;
    }
}
