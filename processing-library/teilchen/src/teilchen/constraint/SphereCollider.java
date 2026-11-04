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

/**
 * keeps particles outside of a sphere. the path of a particle is tested, so fast particles do not pass through the
 * sphere. particle radius is taken into account.
 */
public class SphereCollider extends Collider {

    private final PVector mPosition = new PVector();
    private float mRadius;

    public SphereCollider() {
    }

    public SphereCollider(PVector pPosition, float pRadius) {
        mPosition.set(pPosition);
        mRadius = pRadius;
    }

    public PVector position() {
        return mPosition;
    }

    public float radius() {
        return mRadius;
    }

    public void radius(float pRadius) {
        mRadius = pRadius;
    }

    protected boolean collide(Particle pParticle) {
        final float R = mRadius + pParticle.radius();
        final PVector c = mPosition;
        final PVector p0 = pathStart(pParticle);
        final PVector p1 = pParticle.position();
        final float qX = p1.x - c.x, qY = p1.y - c.y, qZ = p1.z - c.z;
        final float mDistanceSquared1 = qX * qX + qY * qY + qZ * qZ;

        final float oX = p0.x - c.x, oY = p0.y - c.y, oZ = p0.z - c.z;
        final float mDistanceSquared0 = oX * oX + oY * oY + oZ * oZ;

        /* contact normal */
        float nX, nY, nZ;
        float mContactT = -1;
        final float mPenetration;
        if (mDistanceSquared0 > R * R) {
            /* started outside: find entry point of path ( ray-sphere ) */
            final float dX = p1.x - p0.x, dY = p1.y - p0.y, dZ = p1.z - p0.z;
            final float A = dX * dX + dY * dY + dZ * dZ;
            if (A == 0) {
                return false;
            }
            final float B = 2 * (oX * dX + oY * dY + oZ * dZ);
            final float C = mDistanceSquared0 - R * R;
            final float mDiscriminant = B * B - 4 * A * C;
            if (mDiscriminant < 0) {
                return false;
            }
            final float t = (float) ((-B - Math.sqrt(mDiscriminant)) / (2 * A));
            if (t < 0 || t > 1) {
                return false;
            }
            nX = (oX + dX * t) / R;
            nY = (oY + dY * t) / R;
            nZ = (oZ + dZ * t) / R;
            mContactT = t;
            /* penetration of end point below tangent plane at entry point */
            mPenetration = R - (nX * qX + nY * qY + nZ * qZ);
        } else {
            /* started inside or on surface: push out along radius */
            if (mDistanceSquared1 >= R * R) {
                return false;
            }
            final float mDistance = (float) Math.sqrt(mDistanceSquared1);
            if (mDistance > 0) {
                nX = qX / mDistance;
                nY = qY / mDistance;
                nZ = qZ / mDistance;
            } else {
                nX = 0;
                nY = -1;
                nZ = 0;
            }
            mPenetration = R - mDistance;
        }
        if (mPenetration <= 0) {
            return false;
        }
        if (mContactT >= 0) {
            resolve(pParticle, nX, nY, nZ, mPenetration, p0.x + (p1.x - p0.x) * mContactT,
                    p0.y + (p1.y - p0.y) * mContactT, p0.z + (p1.z - p0.z) * mContactT);
        } else {
            resolve(pParticle, nX, nY, nZ, mPenetration);
        }
        return true;
    }
}
