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
package teilchen.cubicle;

import processing.core.PVector;
import teilchen.util.Vector3i;

public interface ICubicleEntity {

    /**
     * get reference to the cubicle id
     *
     * @return Vector3i
     */
    Vector3i cubicle();

    /**
     * get reference to position vector
     *
     * @return PVector
     */
    PVector position();

    /**
     * returns true if the new position don t match the previously stored
     * position
     *
     * @param theX
     * @param theY
     * @param theZ
     *
     * @return boolean
     */
    boolean leaving(int theX, int theY, int theZ);

    /**
     * entities can be temporarily removed from the process of being updated by
     * the world.
     *
     * @return
     */
    boolean isActive();
}
