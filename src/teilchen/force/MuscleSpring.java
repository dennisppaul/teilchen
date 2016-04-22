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

import teilchen.Particle;
import teilchen.Physics;
import static teilchen.util.Util.*;

public class MuscleSpring
        extends Spring {

    private float mAmplitude = 1;

    private float mOffset = 0;

    private float mFrequency = 1;

    private float mInitialRestLength;

    private float mCurrentTime;

    private final boolean mAutomaticContraction = true;

    public MuscleSpring(Particle theA, Particle theB) {
        super(theA, theB);
        mInitialRestLength = mRestLength;
    }

    public MuscleSpring(final Particle theA,
                        final Particle theB,
                        final float theSpringConstant,
                        final float theSpringDamping,
                        final float theRestLength) {
        super(theA,
              theB,
              theSpringConstant,
              theSpringDamping,
              theRestLength);
        mInitialRestLength = mRestLength;
    }

    public void setRestLengthByPosition() {
        mInitialRestLength = distance(mA.position(), mB.position());
    }

    public float restlength() {
        return mInitialRestLength;
    }

    public void restlength(float theRestLength) {
        mInitialRestLength = theRestLength;
    }

    public void frequency(final float theFrequency) {
        mFrequency = theFrequency;
    }

    public float frequency() {
        return mFrequency;
    }

    public void amplitude(final float theAmplitude) {
        mAmplitude = theAmplitude;
    }

    public float amplitude() {
        return mAmplitude;
    }

    /**
     * set the offset of the contraction in radians.
     *
     * @param theOffset float
     */
    public void offset(final float theOffset) {
        mOffset = theOffset;
    }

    public float offset() {
        return mOffset;
    }

    public void apply(final float theDeltaTime, final Physics theParticleSystem) {

        if (mAutomaticContraction) {
            mCurrentTime += theDeltaTime;

            final float myOffset = (float) Math.sin(mCurrentTime * mFrequency + mOffset) * mAmplitude;
            mRestLength = mInitialRestLength + myOffset;
        }

        super.apply(theDeltaTime, theParticleSystem);
    }
}
