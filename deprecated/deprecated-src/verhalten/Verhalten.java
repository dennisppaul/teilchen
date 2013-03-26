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
 * global constants and properties for verhalten
 */


package verhalten;


import mathematik.Vector3f;

/**
 * @deprecated 
 */
public interface Verhalten {

    int UNDEFINED = -1;

    float SMALLEST_ACCEPTABLE_DISTANCE = 0.0001f;

    /* separation */

    int SEEK_CONSIDER_CLOSEST_NEIGHBOR = 0;

    int SEEK_CONSIDER_ALL_NEIGHBORS = 1;

    /* containment */

    int CONTAINMENT_SINGLE_COMPONENT = 0;

    int CONTAINMENT_ALL_COMPONENTS = 1;

    /* exclusion */

    int EXCLUSION_SINGLE_COMPONENT = 0;

    int EXCLUSION_ALL_COMPONENTS = 1;

    /* debug view */

    Vector3f DEBUG_COLOR_SEEK = new Vector3f(1, 0, 0);

    Vector3f DEBUG_COLOR_FLEE = new Vector3f(0, 1, 0);

    Vector3f DEBUG_COLOR_ARRIVAL = new Vector3f(0, 0, 1);

    Vector3f DEBUG_COLOR_WANDER = new Vector3f(1, 0.5f, 0);

    Vector3f DEBUG_COLOR_OBSTACLEAVOIDANCE = new Vector3f(1, 0, 1);

    Vector3f DEBUG_COLOR_WALLAVOIDANCE = new Vector3f(1, 0.5f, 1);

}
