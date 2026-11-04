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
 * keeps particles from passing through a triangle. the triangle is two-sided, particles are deflected on the side
 * they come from. particle radius is taken into account.
 * <p>
 * for many triangles use <code>TriangleMeshCollider</code>, which is considerably faster.
 */
public class TriangleCollider extends Collider {

    /**
     * if true triangle geometry is updated from <code>a()</code>, <code>b()</code>, <code>c()</code> every step. set
     * to false for static triangles and call <code>update()</code> after changing vertices.
     */
    public boolean AUTO_UPDATE = true;

    private final PVector a = new PVector();
    private final PVector b = new PVector();
    private final PVector c = new PVector();
    private final PVector mNormal = new PVector();
    private final ColliderTriangle mTriangle = new ColliderTriangle();

    public TriangleCollider() {
    }

    public TriangleCollider(PVector pA, PVector pB, PVector pC) {
        a.set(pA);
        b.set(pB);
        c.set(pC);
        update();
    }

    public PVector a() {
        return a;
    }

    public PVector b() {
        return b;
    }

    public PVector c() {
        return c;
    }

    /**
     * @return unit normal ( b - a ) x ( c - a ). valid after <code>update()</code>.
     */
    public PVector normal() {
        return mNormal;
    }

    /**
     * updates cached geometry from vertices.
     */
    public void update() {
        mTriangle.set(a.x, a.y, a.z, b.x, b.y, b.z, c.x, c.y, c.z);
        mNormal.set(mTriangle.nX, mTriangle.nY, mTriangle.nZ);
    }

    protected void prepare() {
        if (AUTO_UPDATE) {
            update();
        }
    }

    protected boolean collide(Particle pParticle) {
        return mTriangle.collide(pParticle, this);
    }
}
