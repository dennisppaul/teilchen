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
package teilchen.behavior;

import processing.core.PVector;
import teilchen.IBehaviorParticle;

public class Motor
        implements IBehavior,
                   Verhalten {

    static final long serialVersionUID = -3781170603537691466L;

    private PVector mDirection;

    private float mStrength;

    private final PVector mForce;

    private float mWeight;

    private boolean mAutoNormalizeDirection;

    private boolean mActive;

    private boolean mAutoUpdateDirection;

    public final PVector AUTO_RECOVER_DIRECTION;

    public Motor() {
        mDirection = new PVector(1, 0, 0);
        mForce = new PVector();
        mActive = true;
        mStrength = 1;
        mWeight = 1;
        mAutoUpdateDirection = false;
        mAutoNormalizeDirection = true;
        AUTO_RECOVER_DIRECTION = new PVector();
        teilchen.util.Util.randomize(AUTO_RECOVER_DIRECTION);
        AUTO_RECOVER_DIRECTION.z = 0;
    }

    public boolean active() {
        return mActive;
    }

    public void active(boolean pActive) {
        mActive = pActive;
    }

    public float strength() {
        return mStrength;
    }

    public void strength(final float theStrength) {
        mStrength = theStrength;
    }

    public PVector direction() {
        return mDirection;
    }

    public void setDirectionRef(final PVector theDirection) {
        mDirection = theDirection;
    }

    public void auto_update_direction(boolean pAutoUpdateDirection) {
        mAutoUpdateDirection = pAutoUpdateDirection;
    }

    public void auto_normalize_direction(boolean pAutoNormalizeDirection) {
        mAutoNormalizeDirection = pAutoNormalizeDirection;
    }

    public void update(float theDeltaTime, IBehaviorParticle pParent) {
        if (mActive) {
            if (mAutoUpdateDirection) {
                if (pParent.velocity().mag() > 0.0f) {
                    mDirection.set(pParent.velocity());
                } else {
                    mDirection.set(AUTO_RECOVER_DIRECTION);
                }
            }
            if (mAutoNormalizeDirection) {
                mDirection.normalize();
            }
            PVector.mult(mDirection, mStrength, mForce);
            PVector.mult(mForce, mWeight, mForce);
        } else {
            mForce.set(0, 0, 0);
        }
    }

    public PVector force() {
        return mForce;
    }

    public float weight() {
        return mWeight;
    }

    public void weight(float theWeight) {
        mWeight = theWeight;
    }
}
