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

package teilchen.force;

import processing.core.PVector;
import teilchen.Particle;
import teilchen.constraint.Collider;
import teilchen.constraint.LineCollider2D;

/**
 * @deprecated use {@link teilchen.constraint.LineCollider2D} and add it with <code>Physics.add(Constraint)</code>.
 * this class wraps a <code>LineCollider2D</code> for compatibility.
 */
@Deprecated
public class LineDeflector2D extends DeflectorAdapter {

    private final LineCollider2D mCollider = new LineCollider2D();

    public Collider collider() {
        return mCollider;
    }

    public PVector normal() {
        return mCollider.normal();
    }

    public PVector mid() {
        return PVector.sub(b(), a()).mult(0.5f).add(a());
    }

    public void calculateIntersection(Particle pParticle, float pDeltaTime) {
        mCollider.apply(pParticle);
    }

    public PVector a() {
        return mCollider.a();
    }

    public PVector b() {
        return mCollider.b();
    }

    public void set_edges(PVector a, PVector b) {
        mCollider.set_edges(a, b);
    }
}
