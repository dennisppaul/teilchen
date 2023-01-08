/*
 * Teilchen
 *
 * This file is part of the *teilchen* library (https://github.com/dennisppaul/teilchen).
 * Copyright (c) 2023 Dennis P Paul.
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
     * @return cubicle id
     */
    Vector3i cubicle();

    /**
     * get reference to position vector
     *
     * @return position
     */
    PVector position();

    /**
     * returns true if the new position don t match the previously stored position
     *
     * @param pX x
     * @param pY y
     * @param pZ z
     * @return returns true if the new position don t match the previously stored position
     */
    boolean leaving(int pX, int pY, int pZ);

    /**
     * entities can be temporarily removed from the process of being updated by the world.
     *
     * @return returns active state
     */
    boolean isActive();
}
