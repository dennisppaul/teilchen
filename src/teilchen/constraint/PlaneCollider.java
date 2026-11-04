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

package teilchen.constraint;

import processing.core.PVector;
import teilchen.Particle;
import teilchen.util.Plane3f;

/**
 * keeps particles on the front side of an infinite plane defined by an origin and a normal. particles behind the
 * plane are pushed to the front side. particle radius is taken into account.
 */
public class PlaneCollider extends Collider {

    private final Plane3f mPlane;
    private float nX, nY, nZ, mD;

    public PlaneCollider() {
        this(new Plane3f());
        mPlane.normal = new PVector(0, 1, 0);
    }

    /**
     * @param pPlane plane with <code>origin</code> and <code>normal</code>. plane is referenced.
     */
    public PlaneCollider(Plane3f pPlane) {
        mPlane = pPlane;
    }

    public PlaneCollider(PVector pOrigin, PVector pNormal) {
        this(new Plane3f());
        mPlane.origin.set(pOrigin);
        mPlane.normal = new PVector().set(pNormal);
    }

    public Plane3f plane() {
        return mPlane;
    }

    protected void prepare() {
        final PVector n = mPlane.normal;
        final float mLength = n == null ? 0 : n.mag();
        if (mLength == 0) {
            nX = nY = nZ = 0;
            return;
        }
        nX = n.x / mLength;
        nY = n.y / mLength;
        nZ = n.z / mLength;
        mD = nX * mPlane.origin.x + nY * mPlane.origin.y + nZ * mPlane.origin.z;
    }

    protected boolean collide(Particle pParticle) {
        if (nX == 0 && nY == 0 && nZ == 0) {
            return false;
        }
        final PVector p = pParticle.position();
        final float d = nX * p.x + nY * p.y + nZ * p.z - mD - pParticle.radius();
        if (d >= 0) {
            return false;
        }
        resolve(pParticle, nX, nY, nZ, -d);
        return true;
    }
}
