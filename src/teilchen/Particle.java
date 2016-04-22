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
package teilchen;

import processing.core.PVector;
import teilchen.util.SpatialEntity;

public interface Particle
        extends SpatialEntity {

    boolean fixed();

    void fixed(boolean theFixed);

    float age();

    void age(float theAge);

    float mass();

    void mass(float theMass);

    PVector position();

    PVector old_position();

    void setPositionRef(PVector thePosition);

    PVector velocity();

    PVector force();

    boolean dead();

    boolean tagged();

    void tag(boolean theTag);

    void accumulateInnerForce(final float theDeltaTime);

    float radius();

    void radius(float theRadius);

    boolean still();

    void still(boolean theStill);
}
