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

package teilchen.constraint;

import processing.core.PVector;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.util.Util;

public class Teleporter implements IConstraint {

    public Teleporter() {
        this(new PVector(), new PVector());
    }
    public Teleporter(final PVector pMin, final PVector pMax) {
        mID = Physics.getUniqueID();
        mMin = Util.clone(pMin);
        mMax = Util.clone(pMax);
    }

    public PVector max() {
        return mMax;
    }

    public PVector min() {
        return mMin;
    }

    public void apply(Physics pParticleSystem) {
        if (!mActive) {
            return;
        }

        for (final Particle mParticle : pParticleSystem.particles()) {
            if (mParticle.position().x > mMax.x) {
                mParticle.position().x -= Math.abs(mMax.x - mMin.x);
            }
            if (mParticle.position().y > mMax.y) {
                mParticle.position().y -= Math.abs(mMax.y - mMin.y);
            }
            if (mParticle.position().z > mMax.z) {
                mParticle.position().z -= Math.abs(mMax.z - mMin.z);
            }
            if (mParticle.position().x < mMin.x) {
                mParticle.position().x += Math.abs(mMax.x - mMin.x);
            }
            if (mParticle.position().y < mMin.y) {
                mParticle.position().y += Math.abs(mMax.y - mMin.y);
            }
            if (mParticle.position().z < mMin.z) {
                mParticle.position().z += Math.abs(mMax.z - mMin.z);
            }
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
    private final PVector mMin;
    private final PVector mMax;
    private final long mID;
    protected boolean mActive = true;
    private boolean mDead = false;

}
