/*
 * Teilchen
 *
 * Copyright (C) 2020
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

public class Gravity implements IForce {

    private final PVector mForce;
    private boolean mActive;
    private boolean mDead = false;
    private final long mID;

    public Gravity() {
        this(new PVector(0, 9.81f, 0));
    }

    public Gravity(final PVector pForce) {
        mID = Physics.getUniqueID();
        mActive = true;
        mForce = pForce;
    }

    public Gravity(float pForceX, float pForceY, float pForceZ) {
        this(new PVector(pForceX, pForceY, pForceZ));
    }

    public PVector force() {
        return mForce;
    }

    public void apply(final float pDeltaTime, final Physics pParticleSystem) {
        for (final Particle mParticle : pParticleSystem.particles()) {
            if (!mParticle.fixed()) {
                mParticle.force().add(mForce);
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
}
