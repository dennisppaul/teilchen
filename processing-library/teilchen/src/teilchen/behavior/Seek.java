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
import static processing.core.PVector.sub;
import teilchen.IBehaviorParticle;

public class Seek
        implements IBehavior, Verhalten {

    static final long serialVersionUID = -3781170603537691477L;

    private PVector mSeekPosition;

    private final PVector mForce;

    private float mWeight = 1;

    private float mDistanceToPoint;

    private boolean mOverSteer;

    public Seek() {
        mSeekPosition = new PVector();
        mForce = new PVector();
        mOverSteer = false;
    }

    public boolean oversteer() {
        return mOverSteer;
    }

    public void oversteer(boolean pOverSteer) {
        mOverSteer = pOverSteer;
    }

    public PVector position() {
        return mSeekPosition;
    }

    public void setPositionRef(final PVector thePoint) {
        mSeekPosition = thePoint;
    }

    public float distancetopoint() {
        return mDistanceToPoint;
    }

    public void update(float theDeltaTime, IBehaviorParticle theParent) {
        sub(mSeekPosition, theParent.position(), mForce);
        mDistanceToPoint = mForce.mag();
        if (mDistanceToPoint > SMALLEST_ACCEPTABLE_DISTANCE) {
            mForce.mult(theParent.maximumInnerForce() / mDistanceToPoint);
            if (mOverSteer) {
                sub(mForce, theParent.velocity(), mForce);
            }
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

    public void weight(float theWeight) {
        mWeight = theWeight;
    }
}
