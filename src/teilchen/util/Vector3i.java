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
package teilchen.util;

import java.io.Serializable;

public class Vector3i
        implements Serializable, Comparable<Vector3i> {

    private static final long serialVersionUID = -1207335169644019377L;

    public int x;

    public int y;

    public int z;

    public Vector3i() {
        x = 0;
        y = 0;
        z = 0;
    }

    public Vector3i(int theX, int theY, int theZ) {
        set(theX, theY, theZ);
    }

    public Vector3i(Vector3i theVector) {
        set(theVector);
    }

    public Vector3i(int[] theVector) {
        set(theVector);
    }

    public void set(int theX, int theY, int theZ) {
        x = theX;
        y = theY;
        z = theZ;
    }

    public void set(Vector3i theVector) {
        x = theVector.x;
        y = theVector.y;
        z = theVector.z;
    }

    public void set(int[] theVector) {
        x = theVector[0];
        y = theVector[1];
        z = theVector[2];
    }

    public final String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

    public final float lengthSquared() {
        return x * x + y * y + z * z;
    }

    public int compareTo(Vector3i theVector3i) {
        return (int) (lengthSquared() - theVector3i.lengthSquared());
    }
}
