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

import teilchen.Physics;
import teilchen.constraint.Collider;

/**
 * adapts a <code>Collider</code> ( a constraint ) to the <code>Force</code> interface. when the force is applied the
 * first time it adds its collider to the constraints of the physics. the collider follows the state of the force:
 * it is skipped while the force is inactive and removed once the force is dead or removed from the physics.
 * <p>
 * this exists for compatibility with the former <code>*Deflector</code> forces. new code should add colliders
 * directly with <code>Physics.add(Constraint)</code>.
 */
public abstract class DeflectorAdapter implements Force {

    private final long mID;
    private boolean mActive = true;
    private boolean mDead = false;
    private Physics mRegisteredPhysics = null;

    protected DeflectorAdapter() {
        mID = Physics.getUniqueID();
    }

    /**
     * @return collider that performs the actual collision handling
     */
    public abstract Collider collider();

    /**
     * called once per force pass before the collider is applied. use it to update collider geometry.
     */
    protected void sync() {
    }

    public void apply(final float pDeltaTime, final Physics pParticleSystem) {
        if (pParticleSystem == null) {
            return;
        }
        final Collider mCollider = collider();
        if (mRegisteredPhysics != pParticleSystem || mCollider.detached()) {
            mCollider.owner(this);
            mCollider.attach();
            pParticleSystem.add(mCollider);
            mRegisteredPhysics = pParticleSystem;
        }
        sync();
        mCollider.ownerApplied(pParticleSystem.stepCount());
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

    public boolean hit() {
        return collider().hit();
    }

    public void coefficientofrestitution(float pCoefficientOfRestitution) {
        collider().coefficientofrestitution(pCoefficientOfRestitution);
    }

    public float coefficientofrestitution() {
        return collider().coefficientofrestitution();
    }
}
