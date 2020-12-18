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

import teilchen.Particle;
import teilchen.Physics;
import static teilchen.util.Util.*;

public class TearableSpring
        extends Spring {

    private boolean mTorn = false;

    private float mTearDistance = -1;

    public TearableSpring(Particle pA, Particle pB) {
        super(pA,
              pB,
              2.0f, 0.1f,
              distance(pA.position(), pB.position()));
    }

    public TearableSpring(final Particle pA,
                          final Particle pB,
                          final float pSpringConstant,
                          final float pSpringDamping,
                          final float pRestLength,
                          final float pTearDistance) {
        super(pA,
              pB,
              pSpringConstant,
              pSpringDamping,
              pRestLength);
        mTearDistance = pTearDistance;
    }

    public final float tear_distance() {
        return mTearDistance;
    }

    public final void tear_distance(float pTearDistance) {
        mTearDistance = pTearDistance;
    }

    public void apply(final float pDeltaTime, final Physics pParticleSystem) {
        /* check if spring will tear */
        if (mTearDistance > 0) {
            final float mActualDistance = distance(a().position(), b().position());
            if (mActualDistance > restlength() + mTearDistance) {
                mTorn = true;
            }
        }
        /* apply force if spring is ok */
        if (!mTorn) {
            super.apply(pDeltaTime, pParticleSystem);
        }
    }

    public boolean dead() {
        return mTorn || super.dead();
    }
}
