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
import static teilchen.util.Util.lengthSquared;

public class DirectedAttractor
        extends Attractor {

    private final PVector mVectorA = new PVector();

    private final PVector mVectorB = new PVector();

    public DirectedAttractor() {
        super();
    }

    public void apply(float pDeltaTime, Physics pParticleSystem) {
        for (final Particle mParticle : pParticleSystem.particles()) {
            /* each particle */
            if (!mParticle.fixed()) {
                PVector.sub(mPosition, mParticle.position(), mTemp);

                final float mDistance = fastInverseSqrt(1 / lengthSquared(mTemp));
                if (mDistance < mRadius) {

                    mVectorA.set(mTemp);
                    mVectorA.mult(1.0f / mDistance);
                    mVectorB.normalize(mParticle.velocity());
                    float mAngle = mVectorA.dot(mVectorB);

                    float mFallOff = 1f - mDistance / mRadius;
                    final float mForce = mAngle * mFallOff * mFallOff * mStrength;
                    mTemp.mult(mForce / mDistance);
                    mParticle.force().add(mTemp);
                }
            }
        }
    }
}
