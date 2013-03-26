/*
 * Verhalten
 *
 * Copyright (C) 2005 Patrick Kochlik + Dennis Paul
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


/*
 * this interface describes the most simple entity in a verhalten world.
 * all entities should implement this interface to allow basic communication.
 */

package verhalten;


import mathematik.TransformMatrix4f;
import mathematik.Vector3f;


public interface IVerhaltenEntity {

    /**
     * get the position of the entity.
     * @return Vector3f
     */
    Vector3f position();


    /**
     * get the velocity of the entity.
     * this method can return 'null' if the entity
     * will never move.
     * @return Vector3f
     */
    Vector3f velocity();


    /**
     * get transformation matrix.
     * the use of position and transform should be redundent.
     * if you use transform you shouldn t be using position().
     * @return Matrix4f
     */
    TransformMatrix4f transform();


    /**
     * get the radius of the bounding sphere
     * @return float
     */
    float getBoundingRadius();
}
