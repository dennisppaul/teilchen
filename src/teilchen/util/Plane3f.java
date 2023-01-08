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
package teilchen.util;

import processing.core.PVector;

import java.io.Serializable;

public class Plane3f implements Serializable {

    private static final long serialVersionUID = 2390391570305327484L;
    public float d = Float.NaN;
    /**
     * these fields are not used by default and left uninitialized 'null'
     */
    public PVector normal;
    public PVector origin;
    public PVector vectorA;
    public PVector vectorB;

    public Plane3f() {
        origin = new PVector();
        vectorA = new PVector();
        vectorB = new PVector();
    }

    public Plane3f(PVector pOrigin, PVector pVectorA, PVector pVectorB) {
        origin = pOrigin;
        vectorA = pVectorA;
        vectorB = pVectorB;
    }

    public void updateNormal() {
        if (normal == null) {
            normal = new PVector();
        }
        Util.calculateNormal(vectorA, vectorB, normal);
    }

    public void updateD() {
        if (normal != null) {
            d = -normal.dot(origin);
        }
    }
//    private float intersection(PVector a, PVector b) {
//        /*
//         * updateNormal();
//         * updateD();
//         */
//
//        float u = normal.x * a.x +
//                  normal.y * a.y +
//                  normal.z * a.z +
//                  d;
//        u /= normal.x * (a.x - b.x) +
//            normal.y * (a.y - b.y) +
//            normal.z * (a.z - b.z);
//        return u;
//    }
}
