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

import teilchen.constraint.Collider;
import teilchen.constraint.PlaneCollider;
import teilchen.util.Plane3f;

/**
 * @deprecated use {@link teilchen.constraint.PlaneCollider} and add it with <code>Physics.add(Constraint)</code>.
 * this class wraps a <code>PlaneCollider</code> for compatibility.
 */
@Deprecated
public class PlaneDeflector extends DeflectorAdapter {

    private final PlaneCollider mCollider = new PlaneCollider();

    public Collider collider() {
        return mCollider;
    }

    public Plane3f plane() {
        return mCollider.plane();
    }
}
