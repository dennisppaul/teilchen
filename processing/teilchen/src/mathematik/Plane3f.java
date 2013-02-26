/*
 * Mathematik
 *
 * Copyright (C) 2009 Patrick Kochlik + Dennis Paul
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


package mathematik;


import java.io.Serializable;


public class Plane3f
    implements Serializable {

    private static final long serialVersionUID = 2390391570305327484L;

    public Vector3f origin;

    public Vector3f vectorA;

    public Vector3f vectorB;

    /**
     * these fields are not used by default and left uninitialized 'null'
     */
    public Vector3f normal;

    public float d = Float.NaN;

    public Plane3f() {
        origin = new Vector3f();
        vectorA = new Vector3f();
        vectorB = new Vector3f();
    }


    public Plane3f(Vector3f theOrigin,
                   Vector3f theVectorA,
                   Vector3f theVectorB) {
        origin = theOrigin;
        vectorA = theVectorA;
        vectorB = theVectorB;
    }


    public void updateNormal() {
        if (normal == null) {
            normal = new Vector3f();
        }
        Util.calculateNormal(vectorA, vectorB, normal);
    }


    public void updateD() {
        if (normal != null) {
            d = -normal.dot(origin);
        }
    }

//    private float intersection(Vector3f a, Vector3f b) {
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
