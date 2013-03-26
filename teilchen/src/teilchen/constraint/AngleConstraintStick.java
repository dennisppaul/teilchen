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

import teilchen.Particle;


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
        Vector3f ab = mathematik.Util.sub(mParticleA.position(), mParticleB.position());
        Vector3f cb = mathematik.Util.sub(mParticleC.position(), mParticleB.position());
        final float mCurrentAngle = ab.angle(cb);

        if (mCurrentAngle < mMinAngle) {
            final float b = ab.length();
            final float c = cb.length();
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
