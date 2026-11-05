/*
 * Teilchen
 *
 * This file is part of the *teilchen* library (https://github.com/dennisppaul/teilchen).
 * Copyright (c) 2024 Dennis P Paul.
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
    /* cosine of the angle between a direction and a surface normal below which the two count as perpendicular */
    private static final float PARALLEL_EPSILON = 0.000001f;
    private static final long serialVersionUID = -5392974339890719551L;

    /**
     * tests a ray against a sphere. the ray starts at P1 and continues through P2 without an end. a sphere entirely
     * behind P1 is not hit. a ray starting inside the sphere is a hit.
     * <p>
     * after http://local.wasp.uwa.edu.au/~pbourke/geometry/sphereline/raysphere.c
     *
     * @param pP1           ray origin
     * @param pP2           point on the ray
     * @param pSphereCenter sphere center
     * @param pSphereRadius sphere radius
     * @return returns true if intersection exists
     */
    public static boolean intersect_ray_sphere(PVector pP1, PVector pP2, PVector pSphereCenter, float pSphereRadius) {
        final PVector d = sub(pP2, pP1);
        final PVector o = sub(pP1, pSphereCenter);
        final float a = d.dot(d);
        if (a == 0.0f) {
            return false;
        }
        final float b = 2 * d.dot(o);
        final float c = o.dot(o) - pSphereRadius * pSphereRadius;
        final float bb4ac = b * b - 4 * a * c;
        if (bb4ac < 0) {
            return false;
        }
        /* the larger root is negative only if the whole sphere is behind the origin */
        return -b + (float) Math.sqrt(bb4ac) >= 0;
    }

    private static float parallelThreshold(final PVector pDirection, final PVector pEdgeA, final PVector pEdgeB) {
        return PARALLEL_EPSILON * pDirection.mag() * pEdgeA.cross(pEdgeB).mag();
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
        final PVector TMP_EDGE_NORMAL = new PVector();
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

    /**
     * tests a ray against the triangle ( or the parallelogram if <code>quad</code> is set ) spanned by the origin and
     * the vectors A and B of the plane. despite the name, this does not test against the infinite plane. the normal of
     * the plane is not used.
     *
     * @param pRay     ray
     * @param pPlane   plane providing origin, vector A and vector B
     * @param pResult  intersection point, or ( t, w1, w2 ) if <code>doPlanar</code> is set ( may be null )
     * @param doPlanar store ray parameter and weights instead of the intersection point
     * @param quad     test parallelogram instead of triangle
     * @return true if the ray hits
     */
    public static boolean intersectRayPlane(final Ray3f pRay,
                                            final Plane3f pPlane,
                                            final PVector pResult,
                                            final boolean doPlanar,
                                            final boolean quad) {
        PVector diff = PVector.sub(pRay.origin, pPlane.origin); // mathematik.IntegrationUtil.sub(theRay.origin, v0);
        PVector edge1 = pPlane.vectorA; // mathematik.IntegrationUtil.sub(v1, v0);
        PVector edge2 = pPlane.vectorB; // mathematik.IntegrationUtil.sub(v2, v0);

        /* the bounds below rely on the length of this normal, so it can not be the unit normal of the plane */
        PVector norm = new PVector();
        cross(edge1, edge2, norm);

        final float mParallel = PARALLEL_EPSILON * pRay.direction.mag() * norm.mag();
        float dirDotNorm = pRay.direction.dot(norm);
        float sign;
        if (dirDotNorm > mParallel) {
            sign = 1;
        } else if (dirDotNorm < -mParallel) {
            sign = -1f;
            dirDotNorm = -dirDotNorm;
        } else {
            // ray and triangle are parallel
            return false;
        }

        PVector mCross = new PVector();
        cross(diff, edge2, mCross);
        float dirDotDiffxEdge2 = sign * pRay.direction.dot(mCross);
        if (dirDotDiffxEdge2 >= 0.0f) {
            mCross = new PVector();
            cross(edge1, diff, mCross);
            float dirDotEdge1xDiff = sign * pRay.direction.dot(mCross);
            if (dirDotEdge1xDiff >= 0.0f) {
                if (!quad ?
                    dirDotDiffxEdge2 + dirDotEdge1xDiff <= dirDotNorm :
                    dirDotDiffxEdge2 <= dirDotNorm && dirDotEdge1xDiff <= dirDotNorm) {
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

    /**
     * calculates the points where a ray enters and leaves a sphere. points behind the ray origin are left out.
     *
     * @param pSphereCenter sphere center
     * @param pSphereRadius sphere radius
     * @param pRayDirection ray direction
     * @param pRayOrigin    ray origin
     * @return one or two points, the farther point first. null if the ray does not hit the sphere
     */
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
        final float t0 = (0.5f * (-b + (float) Math.sqrt(disc))) / a;
        final float t1 = (0.5f * (-b - (float) Math.sqrt(disc))) / a;
        if (t0 < 0.0f) {
            return null;
        }
        final int numIntersections = (disc == 0.0f || t1 < 0.0f) ? 1 : 2;
        PVector[] points = new PVector[numIntersections];
        points[0] = add(pRayOrigin, mult(pRayDirection, t0));
        if (numIntersections == 2) {
            points[1] = add(pRayOrigin, mult(pRayDirection, t1));
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
        if (Math.abs(a) <= parallelThreshold(pRayDirection, edge1, edge2)) {
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
     * <p>
     * a triangle behind the ray origin is not hit. <code>pResult</code> is only valid if the method returns true.
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
        final PVector P_VEC = new PVector();
        final PVector Q_VEC = new PVector();
        final PVector TMP_EDGE_1 = new PVector();
        final PVector TMP_EDGE_2 = new PVector();
        final PVector T_VEC = new PVector();

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
        final float mParallel = parallelThreshold(pRayDirection, TMP_EDGE_1, TMP_EDGE_2);

        if (pCullingFlag) {
            /* define TEST_CULL if culling is desired */
            if (det <= mParallel) {
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
            if (Math.abs(det) <= mParallel) {
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
        return pResult.t > M_EPSILON;
    }

    /**
     * Calculate the line segment PaPb that is the shortest route between two lines P1P2 and P3P4. Calculate also the
     * values of mua and mub where Pa = P1 + mua (P2 - P1) Pb = P3 + mub (P4 - P3) Return FALSE if no solution exists.
     * <p>
     * from paul bourke ( http://local.wasp.uwa.edu.au/~pbourke/geometry/lineline3d/ )
     *
     * @param pP1 P1
     * @param pP2 P2
     * @param pP3 P3
     * @param pP4 P4
     * @param pPa Pa
     * @param pPb Pb
     * @return true if solution exists
     */
    public static boolean intersect_line_line(PVector pP1,
                                              PVector pP2,
                                              PVector pP3,
                                              PVector pP4,
                                              PVector pPa,
                                              PVector pPb) {
        //        float[] theResult) {

        final PVector p13 = sub(pP1, pP3);
        final PVector p43 = sub(pP4, pP3);
        final PVector p21 = sub(pP2, pP1);

        final float d1343 = p13.x * p43.x + p13.y * p43.y + p13.z * p43.z;
        final float d4321 = p43.x * p21.x + p43.y * p21.y + p43.z * p21.z;
        final float d1321 = p13.x * p21.x + p13.y * p21.y + p13.z * p21.z;
        final float d4343 = p43.x * p43.x + p43.y * p43.y + p43.z * p43.z;
        final float d2121 = p21.x * p21.x + p21.y * p21.y + p21.z * p21.z;

        if (d2121 == 0.0f || d4343 == 0.0f) {
            return false;
        }

        /* denom / ( d2121 * d4343 ) is the squared sine of the angle between the lines */
        final float denom = d2121 * d4343 - d4321 * d4321;
        if (Math.abs(denom) <= EPSILON * d2121 * d4343) {
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

    /**
     * intersects two line segments in the xy-plane. z is ignored by the test. the z value of the result is taken from
     * segment a at the intersection.
     *
     * @param aBegin        begin of segment a
     * @param aEnd          end of segment a
     * @param bBegin        begin of segment b
     * @param bEnd          end of segment b
     * @param pIntersection intersection point ( may be null )
     * @return COINCIDENT, PARALLEL, INTERSECTING or NOT_INTERSECTING
     */
    public static int intersect_line_line(PVector aBegin,
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
                pIntersection.z = aBegin.z + ua * (aEnd.z - aBegin.z);
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
        if (Math.abs(denominator) <= PARALLEL_EPSILON * plane_normal.mag() * ray_direction.mag()) {
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
        final float distance = plane_normal.dot(sub(plane_origin, line_p1)) / dot;
        if (distance < 0 || distance > 1) {
            // intersection is not within line segment
            return null;
        }
        return add(line_p1, mult(d, distance));
    }
}
