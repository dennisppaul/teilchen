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
 * 'math' is a collection of useful math functions.
 */


package verhalten;


import mathematik.Vector3f;


public class Util
    implements Verhalten {

    /* intersection */

    private static Vector3f h = new Vector3f();

    private static Vector3f s = new Vector3f();

    private static Vector3f q = new Vector3f();

    private static Vector3f myPlaneEdgeA = new Vector3f();

    private static Vector3f myPlaneEdgeB = new Vector3f();

    public static float intersectRayTriangle(final Vector3f theRayOrigin,
                                             final Vector3f theRayDirection,
                                             final Vector3f thePlanePointA,
                                             final Vector3f thePlanePointB,
                                             final Vector3f thePlanePointC,
                                             final Vector3f theResult,
                                             final boolean theCullingFlag) {
        float a;
        float f;
        float u;
        float v;
        float t;

        myPlaneEdgeA.sub(thePlanePointB, thePlanePointA);
        myPlaneEdgeB.sub(thePlanePointC, thePlanePointA);
        h.cross(theRayDirection, myPlaneEdgeB);

        a = myPlaneEdgeA.dot(h);
        if (a > -0.00001 && a < 0.00001) {
            return Float.NaN;
        }
        if (theCullingFlag) {
            // u
            s.sub(theRayOrigin, thePlanePointA);
            u = s.dot(h);
            if (u < 0.0f || u > a) {
                return Float.NaN;
            }
            // v
            v = theRayDirection.dot(q);
            if (v < 0.0f || u + v > a) {
                return Float.NaN;
            }
            // t
            q.cross(s, myPlaneEdgeA);
            t = myPlaneEdgeB.dot(q);
            // invert
            f = 1.0f / a;
            u *= f;
            v *= f;
            t *= f;
        } else {
            f = 1f / a;
            // u
            s.sub(theRayOrigin, thePlanePointA);
            u = f * s.dot(h);
            if (u < 0.0f || u > 1.0f) {
                return Float.NaN;
            }
            // v
            q.cross(s, myPlaneEdgeA);
            v = f * theRayDirection.dot(q);
            if (v < 0.0 || u + v > 1.0) {
                return Float.NaN;
            }
            // t
            t = myPlaneEdgeB.dot(q) * f;
        }
        // result
        theResult.scale(t, theRayDirection);
        theResult.add(theRayOrigin);

        return t;
    }


    public static boolean intersectRayPlane(final Vector3f theRayOrigin,
                                            final Vector3f theRayDirection,
                                            final Vector3f thePlanePointA,
                                            final Vector3f thePlanePointB,
                                            final Vector3f thePlanePointC,
                                            final Vector3f theResult) {
        float a;
        float f;
        float u;
        float v;
        float t;

        myPlaneEdgeA.sub(thePlanePointB, thePlanePointA);
        myPlaneEdgeB.sub(thePlanePointC, thePlanePointA);
        h.cross(theRayDirection, myPlaneEdgeB);

        a = myPlaneEdgeA.dot(h);
        if (a > -SMALLEST_ACCEPTABLE_DISTANCE && a < SMALLEST_ACCEPTABLE_DISTANCE) {
            return false; // parallel
        }
        // u
        s.sub(theRayOrigin, thePlanePointA);
        u = s.dot(h);
        // v
        v = theRayDirection.dot(q);
        // t
        q.cross(s, myPlaneEdgeA);
        t = myPlaneEdgeB.dot(q);
        // invert
        f = 1.0f / a;
        u *= f;
        v *= f;
        t *= f;

        // result
        theResult.scale(t, theRayDirection);
        theResult.add(theRayOrigin);

        return true;
    }


    public static void main(String[] args) {
        Vector3f myResult = new Vector3f();
        float myRatio = intersectRayTriangle(new Vector3f(0.5f, 0.5f, 10),
                                             new Vector3f(0, 0, -1),
                                             new Vector3f( -1, -1, 0),
                                             new Vector3f(1, -1, 0),
                                             new Vector3f(1, 1, 0),
                                             myResult,
                                             true);
        System.out.println(myRatio);
        System.out.println(myResult);
        System.out.println(intersectRayPlane(new Vector3f(3.5f, 0.5f, 2),
                                             new Vector3f(1, 0, -1),
                                             new Vector3f( -1, -1, 0),
                                             new Vector3f(1, -1, 0),
                                             new Vector3f(1, 1, 0),
                                             myResult));
        System.out.println(myResult);
    }
}
