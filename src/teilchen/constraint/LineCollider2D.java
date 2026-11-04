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
 * keeps particles from passing through a line segment in the XY plane ( z is ignored ). the segment is two-sided,
 * particles are deflected on the side they come from. particle radius is taken into account.
 */
public class LineCollider2D extends Collider {

    private PVector a = new PVector();
    private PVector b = new PVector();

    public LineCollider2D() {
    }

    public LineCollider2D(PVector pA, PVector pB) {
        a.set(pA);
        b.set(pB);
    }

    public PVector a() {
        return a;
    }

    public PVector b() {
        return b;
    }

    /**
     * sets ( references ) end points of line segment.
     */
    public void set_edges(PVector pA, PVector pB) {
        a = pA;
        b = pB;
    }

    /**
     * @return unit normal of line segment
     */
    public PVector normal() {
        final PVector n = new PVector(b.y - a.y, a.x - b.x);
        n.normalize();
        return n;
    }

    protected boolean collide(Particle pParticle) {
        final float lX = b.x - a.x, lY = b.y - a.y;
        final float mLengthSquared = lX * lX + lY * lY;
        if (mLengthSquared == 0) {
            return false;
        }
        final float mLength = (float) Math.sqrt(mLengthSquared);
        final float r = pParticle.radius();
        final PVector p0 = pathStart(pParticle);
        final PVector p1 = pParticle.position();

        /* line normal and signed distances */
        final float nX = lY / mLength, nY = -lX / mLength;
        final float s0 = nX * (p0.x - a.x) + nY * (p0.y - a.y);
        final float s1 = nX * (p1.x - a.x) + nY * (p1.y - a.y);

        /* 1. clip path to slab beside segment, decide side where path enters it, test crossing */
        final float u0 = ((p0.x - a.x) * lX + (p0.y - a.y) * lY) / mLengthSquared;
        final float u1 = ((p1.x - a.x) * lX + (p1.y - a.y) * lY) / mLengthSquared;
        float tIn = 0;
        float tOut = 1;
        boolean mInSlab = true;
        for (int i = 0; i < 2 && mInSlab; i++) {
            /* f >= 0 inside: u >= 0 and 1 - u >= 0 */
            final float f0 = i == 0 ? u0 : 1 - u0;
            final float f1 = i == 0 ? u1 : 1 - u1;
            if (f0 < 0 && f1 < 0) {
                mInSlab = false;
            } else if (f0 < 0) {
                tIn = Math.max(tIn, f0 / (f0 - f1));
            } else if (f1 < 0) {
                tOut = Math.min(tOut, f0 / (f0 - f1));
            }
        }
        if (mInSlab && tIn <= tOut) {
            final float sIn = s0 + (s1 - s0) * tIn;
            final float sOut = s0 + (s1 - s0) * tOut;
            /* a particle ( practically ) on the line counts as being on the front side */
            final float mSide = sIn < -0.5f * SKIN ? -1 : 1;
            final float mPenetration = r - mSide * s1;
            if (mSide * sOut - r < 0 && mPenetration > 0) {
                final float dIn = mSide * sIn - r;
                final float dOut = mSide * sOut - r;
                final float t = dIn > 0 ? tIn + (tOut - tIn) * dIn / (dIn - dOut) : tIn;
                resolve(pParticle, nX * mSide, nY * mSide, 0, mPenetration,
                        p0.x + (p1.x - p0.x) * t, p0.y + (p1.y - p0.y) * t, p0.z + (p1.z - p0.z) * t);
                return true;
            }
        }

        /* 2. end position overlaps segment ( incl. rounded end caps ) */
        if (r > 0) {
            float u = ((p1.x - a.x) * lX + (p1.y - a.y) * lY) / mLengthSquared;
            u = Math.max(0, Math.min(1, u));
            final float dX = p1.x - (a.x + lX * u);
            final float dY = p1.y - (a.y + lY * u);
            final float mDistance = (float) Math.sqrt(dX * dX + dY * dY);
            if (mDistance < r) {
                if (mDistance > 0) {
                    resolve(pParticle, dX / mDistance, dY / mDistance, 0, r - mDistance);
                } else {
                    final float mFallbackSide = s0 >= 0 ? 1 : -1;
                    resolve(pParticle, nX * mFallbackSide, nY * mFallbackSide, 0, r);
                }
                return true;
            }
        }
        return false;
    }
}
