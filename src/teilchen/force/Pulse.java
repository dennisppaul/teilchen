/*
 * Teilchen
 *
 * Copyright (C) 2019
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

public class Pulse implements IForce {

    private boolean mActive;
    private Particle mParticle;
    private PVector mForce;
    private float mDamping;

    public Pulse(final Particle pParticle) {
        mActive = true;
        mParticle = pParticle;
        mForce = new PVector();
        mDamping = 0;
    }

    public PVector force() {
        return mForce;
    }

    @Override
    public void apply(float pDeltaTime, Physics pParticleSystem) {
        if (!mParticle.fixed()) {
            mParticle.force().add(mForce);
            if (mDamping == 0.0) {
                mForce.set(0, 0, 0);
            } else {
                mForce.mult(mDamping);
            }
        }
    }

    @Override
    public boolean dead() {
        return false;
    }

    @Override
    public boolean active() {
        return mActive;
    }

    @Override
    public void active(boolean pActiveState) {
        mActive = pActiveState;
    }

    public void damping(float pDamping) {
        mDamping = pDamping;
    }

    public float damping() {
        return mDamping;
    }
}
