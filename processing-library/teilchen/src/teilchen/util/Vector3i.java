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

    public Vector3i(int pX, int pY, int pZ) {
        set(pX, pY, pZ);
    }

    public Vector3i(Vector3i pVector) {
        set(pVector);
    }

    public Vector3i(int[] pVector) {
        set(pVector);
    }

    public void set(int pX, int pY, int pZ) {
        x = pX;
        y = pY;
        z = pZ;
    }

    public void set(Vector3i pVector) {
        x = pVector.x;
        y = pVector.y;
        z = pVector.z;
    }

    public void set(int[] pVector) {
        x = pVector[0];
        y = pVector[1];
        z = pVector[2];
    }

    public final String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

    public final float lengthSquared() {
        return x * x + y * y + z * z;
    }

    public int compareTo(Vector3i pVector3i) {
        return (int) (lengthSquared() - pVector3i.lengthSquared());
    }
}
