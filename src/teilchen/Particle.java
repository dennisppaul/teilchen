/*
 * Teilchen
 *
 * This file is part of the *teilchen* library (https://github.com/dennisppaul/teilchen).
 * Copyright (c) 2020 Dennis P Paul.
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

package teilchen;

import processing.core.PVector;
import teilchen.util.SpatialEntity;

public interface Particle extends SpatialEntity {

    boolean fixed();

    void fixed(boolean pFixed);

    float age();

    void age(float pAge);

    float mass();

    void mass(float pMass);

    PVector old_position();

    void setPositionRef(PVector pPosition);

    PVector velocity();

    PVector force();

    boolean dead();

    void dead(boolean pDead);

    boolean tagged();

    void tag(boolean pTag);

    void accumulateInnerForce(final float pDeltaTime);

    float radius();

    PVector position();

    void radius(float pRadius);

    boolean still();

    void still(boolean pStill);

    long ID();
}
