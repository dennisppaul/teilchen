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
package teilchen;


import java.util.Vector;

import teilchen.behavior.IBehavior;


public class BehaviorParticle
        extends BasicParticle
        implements IBehaviorParticle {

    private final Vector<IBehavior> mBehaviors;

    private float mMaximumInnerForce;

    private static final long serialVersionUID = 2735849326244271321L;

    public BehaviorParticle() {
        mBehaviors = new Vector<IBehavior>();
        mMaximumInnerForce = 50;
    }

    public void accumulateInnerForce(final float theDeltaTime) {
        for (final IBehavior mBehavior : mBehaviors) {
            if (mBehavior != null) {
                mBehavior.update(theDeltaTime, this);
                force().add(mBehavior.force());
            }
        }
        /* clamp to maximum force */
        if (maximumInnerForce() > 0) {
            final float mForceLength = force().length();
            if (mForceLength > maximumInnerForce()) {
                force().scale(maximumInnerForce() / mForceLength);
            }
        }
    }

    public float maximumInnerForce() {
        return mMaximumInnerForce;
    }

    public void maximumInnerForce(float theForce) {
        mMaximumInnerForce = theForce;
    }

    public Vector<IBehavior> behaviors() {
        return mBehaviors;
    }
}
