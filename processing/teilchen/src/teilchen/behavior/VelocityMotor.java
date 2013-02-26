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


package teilchen.behavior;


import mathematik.Vector3f;

import teilchen.IBehaviorParticle;


public class VelocityMotor
        implements IBehavior,
                   Verhalten {

    static final long serialVersionUID = -3781170603537671466L;

    private float mStrength = 1;

    private Vector3f mForce;

    private float _myWeight = 1;

    public VelocityMotor() {
        mForce = new Vector3f();
    }

    public float strength() {
        return mStrength;
    }

    public void strength(final float theStrength) {
        mStrength = theStrength;
    }

    public void update(float theDeltaTime, IBehaviorParticle theParent) {
        final Vector3f mDirection = new Vector3f(theParent.velocity());
        final float mSpeed = mDirection.length();
        if (mSpeed > 0) {
            mDirection.scale(1 / mSpeed);
        } else {
            mDirection.set(1, 0, 0);
        }
        mForce.scale(mStrength, mDirection);
    }

    public Vector3f force() {
        return mForce;
    }

    public float weight() {
        return _myWeight;
    }

    public void weight(float theWeight) {
        _myWeight = theWeight;
    }
}
