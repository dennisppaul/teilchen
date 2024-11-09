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
package teilchen.behavior;

import processing.core.PVector;
import teilchen.BehaviorParticle;

import static processing.core.PVector.sub;

public class Flee implements IBehavior, Verhalten {

    static final long serialVersionUID = -6530887943347815188L;
    private PVector mFleePosition;
    private final PVector mForce;
    private float mWeight = 1;

    public Flee() {
        mFleePosition = new PVector();
        mForce = new PVector();
    }

    public PVector position() {
        return mFleePosition;
    }

    public void setPositionRef(final PVector pPoint) {
        mFleePosition = pPoint;
    }

    public void update(float pDeltaTime, BehaviorParticle pParent) {
        sub(pParent.position(), mFleePosition, mForce);
        final float mDistanceToPoint = mForce.mag();
        if (mDistanceToPoint > SMALLEST_ACCEPTABLE_DISTANCE) {
            mForce.mult(pParent.maximumInnerForce() / mDistanceToPoint);
            sub(mForce, pParent.velocity(), mForce);
            mForce.mult(weight());
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

    public void weight(float pWeight) {
        mWeight = pWeight;
    }
}
