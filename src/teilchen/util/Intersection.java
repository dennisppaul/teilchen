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

import static processing.core.PVector.add;
import static processing.core.PVector.cross;
import static processing.core.PVector.mult;
import static processing.core.PVector.sub;
import static teilchen.util.Util.lengthSquared;

/**
 * beware this is not really in good shape. i ll read my linear algebra book and fix this class. someday. hopefully.
 */
public final class Intersection implements Serializable {

    /**
     * from paul bourke ( http://local.wasp.uwa.edu.au/~pbourke/geometry/lineline2d/ )
     */
    public static final int COINCIDENT = 0;
    public static final int INTERSECTING = 2;
    public static final int NOT_INTERSECTING = 3;
    public static final int PARALLEL = 1;
    private static final float EPSILON = 0.00001f;
    private static final PVector H = new PVector();
    private static final PVector P_VEC = new PVector();
    private static final PVector Q = new PVector();
    private static final PVector Q_VEC = new PVector();
    private static final PVector S = new PVector();
    private static final PVector TMP_EDGE_1 = new PVector();
    private static final PVector TMP_EDGE_2 = new PVector();
    private static final PVector TMP_EDGE_NORMAL = new PVector();
    private static final PVector T_VEC = new PVector();
    private static final long serialVersionUID = -5392974339890719551L;

    /**
     * http://local.wasp.uwa.edu.au/~pbourke/geometry/sphereline/raysphere.c Calculate the intersection of a ray and a
     * sphere The line segment is defined from p1 to p2 The sphere is of radius r and centered at sc There are
     * potentially two points of intersection given by p = p1 + mu1 (p2 - p1) p = p1 + mu2 (p2 - p1) Return FALSE if the
     * ray doesn't intersect the sphere.
     *
     * @param pP1           P1
     * @param pP2           P2
     * @param pSphereCenter sphere center
     * @param pSphereRadius sphere radius
     * @return returns true if intersection exists
     */
    public static boolean RaySphere(PVector pP1, PVector pP2, PVector pSphereCenter, float pSphereRadius) {
        float a, b, c;
        float bb4ac;
        PVector dp = new PVector();

        dp.x = pP2.x - pP1.x;
        dp.y = pP2.y - pP1.y;
        dp.z = pP2.z - pP1.z;
        a = dp.x * dp.x + dp.y * dp.y + dp.z * dp.z;
        b = 2 * (dp.x * (pP1.x - pSphereCenter.x) + dp.y * (pP1.y - pSphereCenter.y) + dp.z * (pP1.z - pSphereCenter.z));
        c = pSphereCenter.x * pSphereCenter.x + pSphereCenter.y * pSphereCenter.y + pSphereCenter.z * pSphereCenter.z;
        c += pP1.x * pP1.x + pP1.y * pP1.y + pP1.z * pP1.z;
        c -= 2 * (pSphereCenter.x * pP1.x + pSphereCenter.y * pP1.y + pSphereCenter.z * pP1.z);
        c -= pSphereRadius * pSphereRadius;
        bb4ac = b * b - 4 * a * c;

        return !(Math.abs(a) < EPSILON || bb4ac < 0);
    }

    /**
     * intersect line with plane ( grabbed from Xith )
     *
     * @param pPlane             Plane3f
     * @param pRay               Ray3f
     * @param pIntersectionPoint PVector
     * @return float
     */
    public static float intersectLinePlane(final Ray3f pRay, final Plane3f pPlane, final PVector pIntersectionPoint) {
        /*
         * @todo not sure whether this is for ray-plane or line-plane intersection. but i think it s for the latter,
         *   hence the method name.
         */

        double time = 0;
        cross(pPlane.vectorA, pPlane.vectorB, TMP_EDGE_NORMAL);
        double denom = TMP_EDGE_NORMAL.dot(pRay.direction);

        if (denom == 0) {
            System.err.println("### ERROR @ Intersection / NEGATIVE_INFINITY");
            return Float.NEGATIVE_INFINITY;
        }

        double numer = TMP_EDGE_NORMAL.dot(pRay.origin);
        double D = -(pPlane.origin.dot(TMP_EDGE_NORMAL));
        time = -((numer + D) / denom);

        if (pIntersectionPoint != null) {
            pIntersectionPoint.set(pRay.direction);
            pIntersectionPoint.mult((float) time);
            pIntersectionPoint.add(pRay.origin);
        }

        return (float) time;
    }

    //    /*
    //     * Practical Analysis of Optimized Ray-Triangle Intersection
    //     * Tomas Moeller
    //     * Department of Computer Engineering, Chalmers University of Technology, Sweden.
    //     *
    //     * code rewritten to do tests on the sign of the determinant
    //     * the division is before the test of the sign of the det
    //     * and one CROSS has been moved out from the if-else if-else
    //     * from -- http://www.cs.lth.se/home/Tomas_Akenine_Moller/raytri/raytri.c
    //     */
    //    public static boolean intersectRayTriangle(final PVector orig,
    //                                               final PVector dir,
    //                                               final PVector vert0,
    //                                               final PVector vert1,
    //                                               final PVector vert2,
    //                                               float[] result) {
    //        final int T = 0;
    //        final int U = 1;
    //        final int V = 2;
    //        PVector edge1;
    //        PVector edge2;
    //        //        PVector tvec;
    //        //        PVector pvec;
    //        //        PVector qvec;
    //        float det;
    //        float inv_det;
    //
    //        /* find vectors for two edges sharing vert0 */
    //        edge1 = sub(vert1, vert0);
    //        edge2 = sub(vert2, vert0);
    //
    //        /* begin calculating determinant - also used to calculate U parameter */
    //        cross(dir, edge2, P_VEC);
    //
    //        /* if determinant is near zero, ray lies in plane of triangle */
    //        det = edge1.dot(P_VEC);
    //
    //        /* calculate distance from vert0 to ray origin */
    //        sub(orig, vert0, T_VEC);
    //        inv_det = 1.0f / det;
    //
    //        cross(T_VEC, edge1, Q_VEC);
    //
    //        if (det > EPSILON) {
    //            result[U] = T_VEC.dot(P_VEC);
    //            if (result[U] < 0.0f || result[U] > det) {
    //                return false;
    //            }
    //
    //            /* calculate V parameter and test bounds */
    //            result[V] = dir.dot(Q_VEC);
    //            if (result[V] < 0.0f || result[U] + result[V] > det) {
    //                return false;
    //            }
    //
    //        } else if (det < -EPSILON) {
    //            /* calculate U parameter and test bounds */
    //            result[U] = T_VEC.dot(P_VEC);
    //            if (result[U] > 0.0f || result[U] < det) {
    //                return false;
    //            }
    //
    //            /* calculate V parameter and test bounds */
    //            result[V] = dir.dot(Q_VEC);
    //            if (result[V] > 0.0f || result[U] + result[V] < det) {
    //                return false;
    //            }
    //        } else {
    //            return false;
    //            /* ray is parallell to the plane of the triangle */
    //        }
    //
    //        result[T] = edge2.dot(Q_VEC) * inv_det;
    //        result[U] *= inv_det;
    //        result[V] *= inv_det;
    //
    //        return true;
    //    }

    public static boolean intersectRayPlane(final Ray3f pRay,
                                            final Plane3f pPlane,
                                            final PVector pResult,
                                            final boolean doPlanar,
                                            final boolean quad) {
        PVector diff = PVector.sub(pRay.origin, pPlane.origin); // mathematik.IntegrationUtil.sub(theRay.origin, v0);
        PVector edge1 = pPlane.vectorA; // mathematik.IntegrationUtil.sub(v1, v0);
        PVector edge2 = pPlane.vectorB; // mathematik.IntegrationUtil.sub(v2, v0);

        PVector norm = pPlane.normal; //new PVector();

        if (pPlane.normal == null) {
            pPlane.updateNormal();
            norm = pPlane.normal;
        }

        float dirDotNorm = pRay.direction.dot(norm);
        float sign;
        if (dirDotNorm > EPSILON) {
            sign = 1;
        } else if (dirDotNorm < EPSILON) {
            sign = -1f;
            dirDotNorm = -dirDotNorm;
        } else {
            // ray and triangle are parallel
            return false;
        }

        PVector mCross = new PVector();
        cross(diff, edge2, mCross);
        float dirDotDiffxEdge2 = sign * pRay.direction.dot(mCross);
        if (dirDotDiffxEdge2 > 0.0f) {
            mCross = new PVector();
            cross(edge1, diff, mCross);
            float dirDotEdge1xDiff = sign * pRay.direction.dot(mCross);
            if (dirDotEdge1xDiff >= 0.0f) {
                if (!quad ? dirDotDiffxEdge2 + dirDotEdge1xDiff <= dirDotNorm : dirDotEdge1xDiff <= dirDotNorm) {
                    float diffDotNorm = -sign * diff.dot(norm);
                    if (diffDotNorm >= 0.0f) {
                        // ray intersects triangle
                        // if storage vector is null, just return true,
                        if (pResult == null) {
                            return true;
                        }
                        // else fill in.
                        float inv = 1f / dirDotNorm;
                        float t = diffDotNorm * inv;
                        if (!doPlanar) {
                            pResult.set(pRay.origin);
                            pResult.add(pRay.direction.x * t, pRay.direction.y * t, pRay.direction.z * t);
                        } else {
                            // these weights can be used to determine
                            // interpolated values, such as texture coord.
                            // eg. texcoord s,t at intersection point:
                            // s = w0*s0 + w1*s1 + w2*s2;
                            // t = w0*t0 + w1*t1 + w2*t2;
                            float w1 = dirDotDiffxEdge2 * inv;
                            float w2 = dirDotEdge1xDiff * inv;
                            //float w0 = 1.0f - w1 - w2;
                            pResult.set(t, w1, w2);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static PVector[] intersectRaySpherePoints(PVector pSphereCenter,
                                                     float pSphereRadius,
                                                     PVector pRayDirection,
                                                     PVector pRayOrigin) {
        // Solve quadratic equation
        float a = lengthSquared(pRayDirection);
        if (a == 0.0) {
            return null;
        }
        float b = 2.0f * (pRayOrigin.dot(pRayDirection) - pRayDirection.dot(pSphereCenter));
        PVector tempDiff = sub(pSphereCenter, pRayOrigin);
        float c = lengthSquared(tempDiff) - (pSphereRadius * pSphereRadius);
        float disc = b * b - 4 * a * c;
        if (disc < 0.0f) {
            return null;
        }
        int numIntersections;
        if (disc == 0.0f) {
            numIntersections = 1;
        } else {
            numIntersections = 2;
        }

        // Atleast one intersection
        PVector[] points = new PVector[numIntersections];
        float t0;
        float t1 = 0.0f;
        t0 = ((0.5f * (-1.0f * b + (float) Math.sqrt(disc))) / a);
        if (numIntersections == 2) {
            t1 = ((0.5f * (-1.0f * b - (float) Math.sqrt(disc))) / a);
        }
        // point 1 of intersection
        points[0] = Util.clone(pRayDirection);
        points[0].mult(t0);
        points[0].add(pRayOrigin);
        if (numIntersections == 2) {
            points[1] = Util.clone(pRayDirection);
            points[1].mult(t1);
            points[1].add(pRayOrigin);
        }
        return points;
    }

    //    public static boolean intersectRayTriangle(final PVector pRayOrigin,
    //                                               final PVector pRayDirection,
    //                                               final PVector v0,
    //                                               final PVector v1,
    //                                               final PVector v2,
    //                                               final PVector pResult,
    //                                               final boolean pCullingFlag) {
    //        final IntersectionResult mResult = new IntersectionResult();
    //        final boolean mSuccess = intersectRayTriangle(pRayOrigin, pRayDirection, v0, v1, v2, mResult,
    //        pCullingFlag);
    //        pResult.x = mResult.t;
    //        pResult.y = mResult.u;
    //        pResult.z = mResult.v;
    //        return mSuccess;
    //    }

    /*
     * *Möller–Trumbore intersection algorithm* from
     * https://en.wikipedia.org/wiki/Möller–Trumbore_intersection_algorithm
     */
    public static boolean intersectRayTriangle(final PVector pRayOrigin,
                                               final PVector pRayDirection,
                                               final PVector v0,
                                               final PVector v1,
                                               final PVector v2,
                                               final PVector pIntersectionPoint) {
        final float EPSILON = 0.0000001f;
        PVector edge1, edge2, h, s, q;
        float a, f, u, v;
        edge1 = PVector.sub(v1, v0);
        edge2 = PVector.sub(v2, v0);
        h = pRayDirection.cross(edge2);
        a = edge1.dot(h);
        if (a > -EPSILON && a < EPSILON) {
            return false;
        }
        f = 1 / a;
        s = PVector.sub(pRayOrigin, v0);
        u = f * (s.dot(h));
        if (u < 0.0 || u > 1.0) {
            return false;
        }
        q = s.cross(edge1);
        v = f * pRayDirection.dot(q);
        if (v < 0.0 || u + v > 1.0) {
            return false;
        }
        // At this stage we can compute t to find out where the intersection point is on the line.
        float t = f * edge2.dot(q);
        if (t > EPSILON) { // ray intersection
            pIntersectionPoint.set(add(pRayOrigin, mult(pRayDirection, t)));
            return true;
        } else { // This means that there is a line intersection but not a ray intersection.
            return false;
        }
    }

    /**
     * Fast, Minimum Storage Ray-Triangle Intersection by Tomas Moeller &amp; Ben Trumbore
     * http://jgt.akpeters.com/papers/MollerTrumbore97/code.html
     *
     * @param pRayOrigin    ray origin
     * @param pRayDirection ray direction
     * @param v0            triangle v0
     * @param v1            triangle v1
     * @param v2            triangle v2
     * @param pResult       interesction result container
     * @param pCullingFlag  culling
     * @return returns true if intersections exists
     */
    public static boolean intersectRayTriangle(final PVector pRayOrigin,
                                               final PVector pRayDirection,
                                               final PVector v0,
                                               final PVector v1,
                                               final PVector v2,
                                               final IntersectionResult pResult,
                                               final boolean pCullingFlag) {

        float det;
        float inv_det;
        final float M_EPSILON = 0.0000001f;

        /* find vectors for two edges sharing vert0 */
        sub(v1, v0, TMP_EDGE_1);
        sub(v2, v0, TMP_EDGE_2);

        /* begin calculating determinant - also used to calculate U parameter */
        cross(pRayDirection, TMP_EDGE_2, P_VEC);

        /* if determinant is near zero, ray lies in plane of triangle */
        det = TMP_EDGE_1.dot(P_VEC);

        if (pCullingFlag) {
            /* define TEST_CULL if culling is desired */
            if (det < M_EPSILON) {
                return false;
            }
            /* calculate distance from vert0 to ray origin */
            sub(pRayOrigin, v0, T_VEC);

            /* calculate U parameter and test bounds */
            pResult.u = T_VEC.dot(P_VEC);
            if (pResult.u < 0.0f || pResult.u > det) {
                return false;
            }

            /* prepare to test V parameter */
            cross(T_VEC, TMP_EDGE_1, Q_VEC);

            /* calculate V parameter and test bounds */
            pResult.v = pRayDirection.dot(Q_VEC);
            if (pResult.v < 0.0f || pResult.u + pResult.v > det) {
                return false;
            }

            /* calculate t, scale parameters, ray intersects triangle */
            pResult.t = TMP_EDGE_2.dot(Q_VEC);
            inv_det = 1.0f / det;
            pResult.t *= inv_det;
            pResult.u *= inv_det;
            pResult.v *= inv_det;
        } else {
            /* the non-culling branch */
            if (det > -M_EPSILON && det < M_EPSILON) {
                return false;
            }
            inv_det = 1.0f / det;

            /* calculate distance from vert0 to ray origin */
            sub(pRayOrigin, v0, T_VEC);

            /* calculate U parameter and test bounds */
            pResult.u = T_VEC.dot(P_VEC) * inv_det;
            if (pResult.u < 0.0f || pResult.u > 1.0f) {
                return false;
            }

            /* prepare to test V parameter */
            cross(T_VEC, TMP_EDGE_1, Q_VEC);

            /* calculate V parameter and test bounds */
            pResult.v = pRayDirection.dot(Q_VEC) * inv_det;
            if (pResult.v < 0.0f || pResult.u + pResult.v > 1.0f) {
                return false;
            }

            /* calculate t, ray intersects triangle */
            pResult.t = TMP_EDGE_2.dot(Q_VEC) * inv_det;
        }
        return true;
    }

    /**
     * from paul bourke ( http://local.wasp.uwa.edu.au/~pbourke/geometry/lineline3d/ )
     * <p>
     * Calculate the line segment PaPb that is the shortest route between two lines P1P2 and P3P4. Calculate also the
     * values of mua and mub where Pa = P1 + mua (P2 - P1) Pb = P3 + mub (P4 - P3) Return FALSE if no solution exists.
     *
     * @param pP1 P1
     * @param pP2 P2
     * @param pP3 P3
     * @param pP4 P4
     * @param pPa Pa
     * @param pPb Pb
     * @return sucess if solution exists
     */
    public static boolean lineLineIntersect(PVector pP1,
                                            PVector pP2,
                                            PVector pP3,
                                            PVector pP4,
                                            PVector pPa,
                                            PVector pPb) {
        //        float[] theResult) {

        final PVector p13 = sub(pP1, pP3);
        final PVector p43 = sub(pP4, pP3);
        if (Math.abs(p43.x) < EPSILON && Math.abs(p43.y) < EPSILON && Math.abs(p43.z) < EPSILON) {
            return false;
        }

        final PVector p21 = sub(pP2, pP1);
        if (Math.abs(p21.x) < EPSILON && Math.abs(p21.y) < EPSILON && Math.abs(p21.z) < EPSILON) {
            return false;
        }

        final float d1343 = p13.x * p43.x + p13.y * p43.y + p13.z * p43.z;
        final float d4321 = p43.x * p21.x + p43.y * p21.y + p43.z * p21.z;
        final float d1321 = p13.x * p21.x + p13.y * p21.y + p13.z * p21.z;
        final float d4343 = p43.x * p43.x + p43.y * p43.y + p43.z * p43.z;
        final float d2121 = p21.x * p21.x + p21.y * p21.y + p21.z * p21.z;

        final float denom = d2121 * d4343 - d4321 * d4321;
        if (Math.abs(denom) < EPSILON) {
            return false;
        }
        final float numer = d1343 * d4321 - d1321 * d4343;

        final float mua = numer / denom;
        final float mub = (d1343 + d4321 * mua) / d4343;

        if (pPa == null) {
            pPa = new PVector();
        }
        pPa.x = pP1.x + mua * p21.x;
        pPa.y = pP1.y + mua * p21.y;
        pPa.z = pP1.z + mua * p21.z;
        if (pPb == null) {
            pPb = new PVector();
        }
        pPb.x = pP3.x + mub * p43.x;
        pPb.y = pP3.y + mub * p43.y;
        pPb.z = pP3.z + mub * p43.z;

        //        if (theResult != null) {
        //            theResult[0] = mua;
        //            theResult[1] = mub;
        //        }
        return true;
    }

    public static int lineLineIntersect(PVector aBegin,
                                        PVector aEnd,
                                        PVector bBegin,
                                        PVector bEnd,
                                        PVector pIntersection) {
        float denom = ((bEnd.y - bBegin.y) * (aEnd.x - aBegin.x)) - ((bEnd.x - bBegin.x) * (aEnd.y - aBegin.y));

        float nume_a = ((bEnd.x - bBegin.x) * (aBegin.y - bBegin.y)) - ((bEnd.y - bBegin.y) * (aBegin.x - bBegin.x));

        float nume_b = ((aEnd.x - aBegin.x) * (aBegin.y - bBegin.y)) - ((aEnd.y - aBegin.y) * (aBegin.x - bBegin.x));

        if (denom == 0.0f) {
            if (nume_a == 0.0f && nume_b == 0.0f) {
                return COINCIDENT;
            }
            return PARALLEL;
        }

        float ua = nume_a / denom;
        float ub = nume_b / denom;

        if (ua >= 0.0f && ua <= 1.0f && ub >= 0.0f && ub <= 1.0f) {
            if (pIntersection != null) {
                // Get the intersection point.
                pIntersection.x = aBegin.x + ua * (aEnd.x - aBegin.x);
                pIntersection.y = aBegin.y + ua * (aEnd.y - aBegin.y);
            }
            return INTERSECTING;
        }

        return NOT_INTERSECTING;
    }

    public static class IntersectionResult {
        public float t;
        public float u;
        public float v;

        public void clear() {
            v = 0;
            u = 0;
            t = 0;
        }
    }

    public static PVector intersect_ray_plane(PVector ray_origin,
                                              PVector ray_direction,
                                              PVector plane_origin,
                                              PVector plane_normal) {
        double denominator = plane_normal.dot(ray_direction);
        if (Math.abs(denominator) < 1e-6) {
            // parallel to the plane, not intersecting
            return null;
        }

        double t = plane_normal.dot(sub(plane_origin, ray_origin)) / denominator;
        if (t < 0) {
            // intersection point is behind ray origin, not intersecting
            return null;
        }

        return add(ray_origin, mult(ray_direction, (float) t));
    }

    public static PVector intersect_line_segment_plane(PVector line_p1,
                                                       PVector line_p2,
                                                       PVector plane_origin,
                                                       PVector plane_normal) {
        final PVector d = sub(line_p2, line_p1);
        final float dot = plane_normal.dot(d);
        if (dot == 0) {
            // line is parallel to plane
            return null;
        }
        final float distance = plane_normal.dot(sub(line_p1, plane_origin)) / dot;
        if (distance < 0 || distance > 1) {
            // intersection is not within line segment
            return null;
        }
        return add(line_p1, mult(d, distance));
    }
}
