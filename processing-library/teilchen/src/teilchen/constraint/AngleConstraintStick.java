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
package teilchen.constraint;

import processing.core.PVector;
import static processing.core.PVector.sub;
import teilchen.Particle;
import static teilchen.util.Util.angle;

public class AngleConstraintStick
        extends Stick {

    private final Particle mParticleA;

    private final Particle mParticleB;

    private final Particle mParticleC;

    private float mMinAngle;

    /**
     *
     * particles are connected like this: A -- B -- C
     *
     * @param pParticleA
     * @param pParticleB
     * @param pParticleC
     */
    public AngleConstraintStick(Particle pParticleA, Particle pParticleB, Particle pParticleC) {
        super(pParticleA, pParticleC);
        mParticleA = pParticleA;
        mParticleB = pParticleB;
        mParticleC = pParticleC;
        mMinAngle = Float.MAX_VALUE;
    }

    public void min_angle(float pAngle) {
        mMinAngle = pAngle;
    }

    public void pre_step() {
        PVector ab = sub(mParticleA.position(), mParticleB.position());
        PVector cb = sub(mParticleC.position(), mParticleB.position());
        final float mCurrentAngle = angle(ab, cb);

        if (mCurrentAngle < mMinAngle) {
            final float b = ab.mag();
            final float c = cb.mag();
            // a = sqrt ( b*b + c*c - 2bc*cosA )
            final float mDistance = (float) Math.sqrt(b * b + c * c - 2 * b * c * (float) Math.cos(mMinAngle));
            restlength(mDistance);
            active(true);
        }
    }

    public void post_step() {
        active(false);
    }
}
