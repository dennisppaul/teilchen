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
 * cached triangle geometry with a swept particle test. shared by <code>TriangleCollider</code> and
 * <code>TriangleMeshCollider</code>.
 * <p>
 * the path of a particle is clipped against the prism above and below the triangle. the side the particle comes
 * from is decided where the path enters the prism, i.e directly above or below the triangle. this makes the test
 * robust for particles moving from one triangle of a mesh to the next, also across folds.
 */
final class ColliderTriangle {

    /*
     * tolerance of prism edges relative to <code>Collider.SKIN</code>. makes neighboring triangles overlap slightly
     * ( no gaps at seams ). it must be small compared to SKIN, otherwise a particle resting SKIN above one triangle
     * could lie below the plane of a folded neighbor within the overlap and be pushed to the wrong side.
     */
    private static final float EDGE_TOLERANCE = 0.05f;

    float aX, aY, aZ;
    float nX, nY, nZ;
    float minX, minY, minZ, maxX, maxY, maxZ;
    boolean valid;
    /* inward unit normals of prism sides ( edge planes ), vertices on edges and tolerances */
    private final float[] mEdgeNormal = new float[9];
    private final float[] mEdgeVertex = new float[9];

    void set(float ax, float ay, float az, float bx, float by, float bz, float cx, float cy, float cz) {
        aX = ax;
        aY = ay;
        aZ = az;
        /* normal = ( b - a ) x ( c - a ) */
        final float e1X = bx - ax, e1Y = by - ay, e1Z = bz - az;
        final float e0X = cx - ax, e0Y = cy - ay, e0Z = cz - az;
        final float x = e1Y * e0Z - e1Z * e0Y;
        final float y = e1Z * e0X - e1X * e0Z;
        final float z = e1X * e0Y - e1Y * e0X;
        final float mLength = (float) Math.sqrt(x * x + y * y + z * z);
        valid = mLength > 0;
        if (valid) {
            nX = x / mLength;
            nY = y / mLength;
            nZ = z / mLength;
            setEdge(0, ax, ay, az, bx, by, bz);
            setEdge(1, bx, by, bz, cx, cy, cz);
            setEdge(2, cx, cy, cz, ax, ay, az);
        } else {
            nX = nY = nZ = 0;
        }
        minX = Math.min(ax, Math.min(bx, cx));
        minY = Math.min(ay, Math.min(by, cy));
        minZ = Math.min(az, Math.min(bz, cz));
        maxX = Math.max(ax, Math.max(bx, cx));
        maxY = Math.max(ay, Math.max(by, cy));
        maxZ = Math.max(az, Math.max(bz, cz));
    }

    private void setEdge(int i, float px, float py, float pz, float qx, float qy, float qz) {
        final float dX = qx - px, dY = qy - py, dZ = qz - pz;
        /* inward normal = n x edge */
        float mX = nY * dZ - nZ * dY;
        float mY = nZ * dX - nX * dZ;
        float mZ = nX * dY - nY * dX;
        final float l = (float) Math.sqrt(mX * mX + mY * mY + mZ * mZ);
        mEdgeNormal[i * 3] = mX / l;
        mEdgeNormal[i * 3 + 1] = mY / l;
        mEdgeNormal[i * 3 + 2] = mZ / l;
        mEdgeVertex[i * 3] = px;
        mEdgeVertex[i * 3 + 1] = py;
        mEdgeVertex[i * 3 + 2] = pz;
    }

    /* result of last successful <code>test</code> */
    float hitT;
    private float mHitSide, mHitPenetration, mHitX, mHitY, mHitZ;

    /**
     * tests the path of particle against triangle ( two-sided, offset by particle radius ) and resolves collision.
     */
    boolean collide(Particle pParticle, Collider pCollider) {
        if (test(pParticle, pCollider)) {
            resolve(pParticle, pCollider);
            return true;
        }
        return false;
    }

    /**
     * resolves collision found by last successful <code>test</code>. particle must not have changed since.
     */
    void resolve(Particle pParticle, Collider pCollider) {
        pCollider.resolve(pParticle, nX * mHitSide, nY * mHitSide, nZ * mHitSide, mHitPenetration, mHitX, mHitY, mHitZ);
    }

    /**
     * tests the path of particle against triangle without resolving. on success <code>hitT</code> holds the path
     * parameter ( 0 .. 1 ) of the contact.
     */
    boolean test(Particle pParticle, Collider pCollider) {
        if (!valid) {
            return false;
        }
        final float r = pParticle.radius();
        final PVector p0 = pCollider.pathStart(pParticle);
        final PVector p1 = pParticle.position();
        final float p0x = p0.x, p0y = p0.y, p0z = p0.z;
        final float p1x = p1.x, p1y = p1.y, p1z = p1.z;

        /* bounding box rejection */
        final float b = r + pCollider.SKIN;
        if (Math.max(p0x, p1x) < minX - b || Math.min(p0x, p1x) > maxX + b ||
            Math.max(p0y, p1y) < minY - b || Math.min(p0y, p1y) > maxY + b ||
            Math.max(p0z, p1z) < minZ - b || Math.min(p0z, p1z) > maxZ + b) {
            return false;
        }

        /* clip path against prism of triangle */
        final float mEdgeTolerance = EDGE_TOLERANCE * pCollider.SKIN;
        float tIn = 0;
        float tOut = 1;
        for (int i = 0; i < 3; i++) {
            final int j = i * 3;
            final float f0 = mEdgeNormal[j] * (p0x - mEdgeVertex[j]) +
                             mEdgeNormal[j + 1] * (p0y - mEdgeVertex[j + 1]) +
                             mEdgeNormal[j + 2] * (p0z - mEdgeVertex[j + 2]) + mEdgeTolerance;
            final float f1 = mEdgeNormal[j] * (p1x - mEdgeVertex[j]) +
                             mEdgeNormal[j + 1] * (p1y - mEdgeVertex[j + 1]) +
                             mEdgeNormal[j + 2] * (p1z - mEdgeVertex[j + 2]) + mEdgeTolerance;
            if (f0 < 0 && f1 < 0) {
                return false;
            }
            if (f0 < 0) {
                tIn = Math.max(tIn, f0 / (f0 - f1));
            } else if (f1 < 0) {
                tOut = Math.min(tOut, f0 / (f0 - f1));
            }
        }
        if (tIn > tOut) {
            return false;
        }

        /* signed distances to plane where path enters and leaves prism */
        final float s0 = nX * (p0x - aX) + nY * (p0y - aY) + nZ * (p0z - aZ);
        final float s1 = nX * (p1x - aX) + nY * (p1y - aY) + nZ * (p1z - aZ);
        final float sIn = s0 + (s1 - s0) * tIn;
        final float sOut = s0 + (s1 - s0) * tOut;

        /*
         * side the particle comes from, decided above or below the triangle itself. a particle ( practically ) on
         * the surface counts as being on the front side. this keeps the side stable at folds between triangles,
         * where tiny numerical differences would otherwise flip it.
         */
        final float mSide = sIn < -0.5f * pCollider.SKIN ? -1 : 1;

        /* collision if path ends behind offset surface within prism */
        if (mSide * sOut - r >= 0) {
            return false;
        }
        final float mPenetration = r - mSide * s1;
        if (mPenetration <= 0) {
            return false;
        }
        /* point where path reaches offset surface ( or enters prism already penetrating ) */
        final float dIn = mSide * sIn - r;
        final float dOut = mSide * sOut - r;
        final float t = dIn > 0 ? tIn + (tOut - tIn) * dIn / (dIn - dOut) : tIn;
        hitT = t;
        mHitSide = mSide;
        mHitPenetration = mPenetration;
        mHitX = p0x + (p1x - p0x) * t;
        mHitY = p0y + (p1y - p0y) * t;
        mHitZ = p0z + (p1z - p0z) * t;
        return true;
    }
}
