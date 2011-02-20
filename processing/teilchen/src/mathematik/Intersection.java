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


/**
 * beware this is not really in good shape.
 * i ll read my linear algebra book and fix this class.
 * someday. hopefully.
 */

public final class Intersection
    implements Serializable, Mathematik {

    private static final long serialVersionUID = -5392974339890719551L;

    private static final Vector3f h = new Vector3f();

    private static final Vector3f s = new Vector3f();

    private static final Vector3f q = new Vector3f();

    private static final Vector3f _myTempEdge1 = new Vector3f();

    private static final Vector3f _myTempEdge2 = new Vector3f();

    private static final Vector3f _myTempNormal = new Vector3f();

    private static final Vector3f pvec = new Vector3f();

    private static final Vector3f tvec = new Vector3f();

    private static final Vector3f qvec = new Vector3f();

    public static boolean intersectRayPlane(final Ray3f theRay,
                                            final Plane3f thePlane,
                                            final Vector3f theResult,
                                            final boolean doPlanar,
                                            final boolean quad) {
        Vector3f diff = mathematik.Util.sub(theRay.origin, thePlane.origin); // mathematik.Util.sub(theRay.origin, v0);
        Vector3f edge1 = thePlane.vectorA; // mathematik.Util.sub(v1, v0);
        Vector3f edge2 = thePlane.vectorB; // mathematik.Util.sub(v2, v0);

        Vector3f norm = thePlane.normal; //new Vector3f();

        if(thePlane.normal == null){
            thePlane.updateNormal();
            norm = thePlane.normal;
        }
        
        float dirDotNorm = theRay.direction.dot(norm);
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

        Vector3f myCross;
        myCross = new Vector3f();
        myCross.cross(diff, edge2);
        float dirDotDiffxEdge2 = sign * theRay.direction.dot(myCross);
        if (dirDotDiffxEdge2 > 0.0f) {
            myCross = new Vector3f();
            myCross.cross(edge1, diff);
            float dirDotEdge1xDiff = sign * theRay.direction.dot(myCross);
            if (dirDotEdge1xDiff >= 0.0f) {
                if (!quad ? dirDotDiffxEdge2 + dirDotEdge1xDiff <= dirDotNorm : dirDotEdge1xDiff <= dirDotNorm) {
                    float diffDotNorm = -sign * diff.dot(norm);
                    if (diffDotNorm >= 0.0f) {
                        // ray intersects triangle
                        // if storage vector is null, just return true,
                        if (theResult == null) {
                            return true;
                        }
                        // else fill in.
                        float inv = 1f / dirDotNorm;
                        float t = diffDotNorm * inv;
                        if (!doPlanar) {
                            theResult.set(theRay.origin);
                            theResult.add(theRay.direction.x * t,
                                      theRay.direction.y * t,
                                      theRay.direction.z * t);
                        } else {
                            // these weights can be used to determine
                            // interpolated values, such as texture coord.
                            // eg. texcoord s,t at intersection point:
                            // s = w0*s0 + w1*s1 + w2*s2;
                            // t = w0*t0 + w1*t1 + w2*t2;
                            float w1 = dirDotDiffxEdge2 * inv;
                            float w2 = dirDotEdge1xDiff * inv;
                            //float w0 = 1.0f - w1 - w2;
                            theResult.set(t, w1, w2);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }


//    /**
//     * @deprecated not in good state.
//     * @param v0 first point of the triangle.
//     * @param v1 second point of the triangle.
//     * @param v2 third point of the triangle.
//     * @param store storage vector - if null, no intersection is calc'd
//     * @param doPlanar true if we are calcing planar results.
//     * @param quad
//     * @return true if ray intersects triangle
//     */
//    public static boolean intersectRayTriangle(final Ray3f theRay,
//                                               final Vector3f v0,
//                                               final Vector3f v1,
//                                               final Vector3f v2,
//                                               final Vector3f store,
//                                               final boolean doPlanar,
//                                               final boolean quad) {
//        Vector3f diff = mathematik.Util.sub(theRay.origin, v0);
//        Vector3f edge1 = mathematik.Util.sub(v1, v0);
//        Vector3f edge2 = mathematik.Util.sub(v2, v0);
//        Vector3f norm = new Vector3f();
//        norm.cross(edge1, edge2);
//
//        float dirDotNorm = theRay.direction.dot(norm);
//        float sign;
//        if (dirDotNorm > EPSILON) {
//            sign = 1;
//        } else if (dirDotNorm < EPSILON) {
//            sign = -1f;
//            dirDotNorm = -dirDotNorm;
//        } else {
//            // ray and triangle are parallel
//            return false;
//        }
//
//        Vector3f myCross;
//        myCross = new Vector3f();
//        myCross.cross(diff, edge2);
//        float dirDotDiffxEdge2 = sign * theRay.direction.dot(myCross);
//        if (dirDotDiffxEdge2 > 0.0f) {
//            myCross = new Vector3f();
//            myCross.cross(edge1, diff);
//            float dirDotEdge1xDiff = sign * theRay.direction.dot(myCross);
//            if (dirDotEdge1xDiff >= 0.0f) {
//                if (!quad ? dirDotDiffxEdge2 + dirDotEdge1xDiff <= dirDotNorm : dirDotEdge1xDiff <= dirDotNorm) {
//                    float diffDotNorm = -sign * diff.dot(norm);
//                    if (diffDotNorm >= 0.0f) {
//                        // ray intersects triangle
//                        // if storage vector is null, just return true,
//                        if (store == null) {
//                            return true;
//                        }
//                        // else fill in.
//                        float inv = 1f / dirDotNorm;
//                        float t = diffDotNorm * inv;
//                        if (!doPlanar) {
//                            store.set(theRay.origin);
//                            store.add(theRay.direction.x * t,
//                                      theRay.direction.y * t,
//                                      theRay.direction.z * t);
//                        } else {
//                            // these weights can be used to determine
//                            // interpolated values, such as texture coord.
//                            // eg. texcoord s,t at intersection point:
//                            // s = w0*s0 + w1*s1 + w2*s2;
//                            // t = w0*t0 + w1*t1 + w2*t2;
//                            float w1 = dirDotDiffxEdge2 * inv;
//                            float w2 = dirDotEdge1xDiff * inv;
//                            //float w0 = 1.0f - w1 - w2;
//                            store.set(t, w1, w2);
//                        }
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
//    }


    /*
     * Practical Analysis of Optimized Ray-Triangle Intersection
     * Tomas Moeller
     * Department of Computer Engineering, Chalmers University of Technology, Sweden.
     *
     * code rewritten to do tests on the sign of the determinant
     * the division is before the test of the sign of the det
     * and one CROSS has been moved out from the if-else if-else
     * from -- http://www.cs.lth.se/home/Tomas_Akenine_Moller/raytri/raytri.c
     */
    public static boolean intersectRayTriangle(final Vector3f orig,
                                               final Vector3f dir,
                                               final Vector3f vert0,
                                               final Vector3f vert1,
                                               final Vector3f vert2,
                                               float[] result) {
        final int T = 0;
        final int U = 1;
        final int V = 2;
        Vector3f edge1;
        Vector3f edge2;
        Vector3f tvec;
        Vector3f pvec;
        Vector3f qvec;
        float det;
        float inv_det;

        /* find vectors for two edges sharing vert0 */
        edge1 = mathematik.Util.sub(vert1, vert0);
        edge2 = mathematik.Util.sub(vert2, vert0);

        /* begin calculating determinant - also used to calculate U parameter */
        pvec = mathematik.Util.cross(dir, edge2);

        /* if determinant is near zero, ray lies in plane of triangle */
        det = edge1.dot(pvec);

        /* calculate distance from vert0 to ray origin */
        tvec = mathematik.Util.sub(orig, vert0);
        inv_det = 1.0f / det;

        qvec = mathematik.Util.cross(tvec, edge1);

        if (det > EPSILON) {
            result[U] = tvec.dot(pvec);
            if (result[U] < 0.0f || result[U] > det) {
                return false;
            }

            /* calculate V parameter and test bounds */
            result[V] = dir.dot(qvec);
            if (result[V] < 0.0f || result[U] + result[V] > det) {
                return false;
            }

        } else if (det < -EPSILON) {
            /* calculate U parameter and test bounds */
            result[U] = tvec.dot(pvec);
            if (result[U] > 0.0f || result[U] < det) {
                return false;
            }

            /* calculate V parameter and test bounds */
            result[V] = dir.dot(qvec);
            if (result[V] > 0.0f || result[U] + result[V] < det) {
                return false;
            }
        } else {
            return false; /* ray is parallell to the plane of the triangle */
        }

        result[T] = edge2.dot(qvec) * inv_det;
        result[U] *= inv_det;
        result[V] *= inv_det;

        return true;
    }


    /**
     * grabbed from Xith
     * @todo not sure whether this is for ray-plane or
     * line-plane intersection. but i think it s for latter,
     * hence the method name.
     * @param thePlane Plane3f
     * @param theRay Ray3f
     * @param theIntersectionPoint Vector3f
     * @return float
     */
    public static float intersectLinePlane(final Ray3f theRay,
                                           final Plane3f thePlane,
                                           final Vector3f theIntersectionPoint) {
        double time = 0;
        _myTempNormal.cross(thePlane.vectorA, thePlane.vectorB);
        double denom = _myTempNormal.dot(theRay.direction);

        if (denom == 0) {
            System.err.println("### ERROR @ Intersection / NEGATIVE_INFINITY");
            return Float.NEGATIVE_INFINITY;
        }

        double numer = _myTempNormal.dot(theRay.origin);
        double D = - (thePlane.origin.dot(_myTempNormal));
        time = - ( (numer + D) / denom);

        if (theIntersectionPoint != null) {
            theIntersectionPoint.set(theRay.direction);
            theIntersectionPoint.scale( (float) time);
            theIntersectionPoint.add(theRay.origin);
        }

        return (float) time;
    }


//    /**
//     * @deprecated this method might contain errors.
//     * @param theRay Ray3f
//     * @param thePlane Plane3f
//     * @param theResult Vector3f
//     * @param theCullingFlag boolean
//     * @return float
//     */
//    public static float intersectRayTriangle(final Ray3f theRay,
//                                             final Plane3f thePlane,
//                                             final Vector3f theResult,
//                                             final boolean theCullingFlag) {
//        float a;
//        float f;
//        float u;
//        float v;
//        float t;
//
//        _myTempEdge1.set(thePlane.vectorA);
//        _myTempEdge2.set(thePlane.vectorB);
//
//        h.cross(theRay.direction, _myTempEdge2);
//
//        a = _myTempEdge1.dot(h);
//        if (a > -EPSILON && a < EPSILON) {
//            return Float.NaN;
//        }
//        if (theCullingFlag) {
//            // u
//            s.sub(theRay.origin,
//                  thePlane.origin);
//            u = s.dot(h);
//            if (u < 0.0f || u > a) {
//                return Float.NaN;
//            }
//            // v
//            v = theRay.direction.dot(q);
//            if (v < 0.0f || u + v > a) {
//                return Float.NaN;
//            }
//            // t
//            q.cross(s,
//                    _myTempEdge1);
//            t = _myTempEdge2.dot(q);
//            // invert
//            f = 1.0f / a;
//            u *= f;
//            v *= f;
//            t *= f;
//        } else {
//            f = 1f / a;
//            // u
//            s.sub(theRay.origin,
//                  thePlane.origin);
//            u = f * s.dot(h);
//            if (u < 0.0f || u > 1.0f) {
//                return Float.NaN;
//            }
//            // v
//            q.cross(s,
//                    _myTempEdge1);
//            v = f * theRay.direction.dot(q);
//            if (v < 0.0 || u + v > 1.0) {
//                return Float.NaN;
//            }
//            // t
//            t = _myTempEdge2.dot(q) * f;
//        }
//        // result
//        theResult.scale(t,
//                        theRay.direction);
//        theResult.add(theRay.origin);
//
//        return t;
//    }


    /**
     * @deprecated this method might contain errors.
     * @param theRayOrigin Vector3f
     * @param theRayDirection Vector3f
     * @param thePlanePointA Vector3f
     * @param thePlanePointB Vector3f
     * @param thePlanePointC Vector3f
     * @param theResult Vector3f
     * @return boolean
     */
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

        _myTempEdge1.sub(thePlanePointB,
                         thePlanePointA);
        _myTempEdge2.sub(thePlanePointC,
                         thePlanePointA);
        h.cross(theRayDirection,
                _myTempEdge2);

        a = _myTempEdge1.dot(h);
        if (a > -EPSILON && a < EPSILON) {
            return false; // parallel
        }
        // u
        s.sub(theRayOrigin,
              thePlanePointA);
        u = s.dot(h);
        // v
        v = theRayDirection.dot(q);
        // t
        q.cross(s,
                _myTempEdge1);
        t = _myTempEdge2.dot(q);
        // invert
        f = 1.0f / a;
        u *= f;
        v *= f;
        t *= f;

        // result
        theResult.scale(t,
                        theRayDirection);
        theResult.add(theRayOrigin);

        return true;
    }


    /**
     * @deprecated this method might contain errors.
     * @param theRay Ray3f
     * @param thePlane Plane3f
     * @param theResult Vector3f
     * @return boolean
     */
    public static boolean intersectRayPlane(final Ray3f theRay,
                                            final Plane3f thePlane,
                                            final Vector3f theResult) {
        float a;
        float f;
        float u;
        float v;
        float t;

        _myTempEdge1.set(thePlane.vectorA);
        _myTempEdge2.set(thePlane.vectorB);
        h.cross(theRay.direction,
                _myTempEdge2);

        a = _myTempEdge1.dot(h);
        if (a > -EPSILON && a < EPSILON) {
            return false; // parallel
        }
        // u
        s.sub(theRay.origin,
              thePlane.origin);
        u = s.dot(h);
        // v
        v = theRay.direction.dot(q);
        // t
        q.cross(s,
                _myTempEdge1);
        t = _myTempEdge2.dot(q);
        // invert
        f = 1.0f / a;
        u *= f;
        v *= f;
        t *= f;

        // result
        theResult.scale(t,
                        theRay.direction);
        theResult.add(theRay.origin);

        return true;
    }


    /**
     * @deprecated this method might contain errors.
     * @param theRay Ray3f
     * @param thePlane Plane3f
     * @return float
     */
    public static float intersectRayPlane(final Ray3f theRay,
                                          final Plane3f thePlane) {
        float a;
        float f;
        float u;
        float v;
        float t;

        _myTempEdge1.set(thePlane.vectorA);
        _myTempEdge2.set(thePlane.vectorB);
        h.cross(theRay.direction, _myTempEdge2);

        a = _myTempEdge1.dot(h);
        if (a > -EPSILON && a < EPSILON) {
            return Float.NaN; // parallel
        }
        // u
        s.sub(theRay.origin,
              thePlane.origin);
        u = s.dot(h);
        // v
        v = theRay.direction.dot(q);
        // t
        q.cross(s,
                _myTempEdge1);
        t = _myTempEdge2.dot(q);
        // invert
        f = 1.0f / a;
        u *= f;
        v *= f;
        t *= f;

        return t;
    }


    /**
     * Fast, Minimum Storage Ray-Triangle Intersection
     * by Tomas Moeller & Ben Trumbore
     * http://jgt.akpeters.com/papers/MollerTrumbore97/code.html
     *
     * @param theRayOrigin Vector3f
     * @param theRayDirection Vector3f
     * @param theVertex0 Vector3f
     * @param theVertex1 Vector3f
     * @param theVertex2 Vector3f
     * @param theResult Result
     * @param theCullingFlag boolean
     * @return boolean
     */
    public static boolean intersectRayTriangle(final Vector3f theRayOrigin,
                                               final Vector3f theRayDirection,
                                               final Vector3f theVertex0,
                                               final Vector3f theVertex1,
                                               final Vector3f theVertex2,
                                               final Result theResult,
                                               final boolean theCullingFlag) {

        float det;
        float inv_det;

        /* find vectors for two edges sharing vert0 */
        _myTempEdge1.sub(theVertex1, theVertex0);
        _myTempEdge2.sub(theVertex2, theVertex0);

        /* begin calculating determinant - also used to calculate U parameter */
        pvec.cross(theRayDirection, _myTempEdge2);

        /* if determinant is near zero, ray lies in plane of triangle */
        det = _myTempEdge1.dot(pvec);

        if (theCullingFlag) { /* define TEST_CULL if culling is desired */
            if (det < EPSILON) {
                return false;
            }
            /* calculate distance from vert0 to ray origin */
            tvec.sub(theRayOrigin, theVertex0);

            /* calculate U parameter and test bounds */
            theResult.u = tvec.dot(pvec);
            if (theResult.u < 0.0f || theResult.u > det) {
                return false;
            }

            /* prepare to test V parameter */
            qvec.cross(tvec, _myTempEdge1);

            /* calculate V parameter and test bounds */
            theResult.v = theRayDirection.dot(qvec);
            if (theResult.v < 0.0f || theResult.u + theResult.v > det) {
                return false;
            }

            /* calculate t, scale parameters, ray intersects triangle */
            theResult.t = _myTempEdge2.dot(qvec);
            inv_det = 1.0f / det;
            theResult.t *= inv_det;
            theResult.u *= inv_det;
            theResult.v *= inv_det;
        } else { /* the non-culling branch */
            if (det > -EPSILON && det < EPSILON) {
                return false;
            }
            inv_det = 1.0f / det;

            /* calculate distance from vert0 to ray origin */
            tvec.sub(theRayOrigin, theVertex0);

            /* calculate U parameter and test bounds */
            theResult.u = tvec.dot(pvec) * inv_det;
            if (theResult.u < 0.0f || theResult.u > 1.0f) {
                return false;
            }

            /* prepare to test V parameter */
            qvec.cross(tvec, _myTempEdge1);

            /* calculate V parameter and test bounds */
            theResult.v = theRayDirection.dot(qvec) * inv_det;
            if (theResult.v < 0.0f || theResult.u + theResult.v > 1.0f) {
                return false;
            }

            /* calculate t, ray intersects triangle */
            theResult.t = _myTempEdge2.dot(qvec) * inv_det;
        }
        return true;
    }


    public static class Result {
        public float t;

        public float u;

        public float v;
    }


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

        _myTempEdge1.sub(thePlanePointB,
                         thePlanePointA);
        _myTempEdge2.sub(thePlanePointC,
                         thePlanePointA);

        h.cross(theRayDirection,
                _myTempEdge2);

        a = _myTempEdge1.dot(h);
        if (a > -EPSILON && a < EPSILON) {
            return Float.NaN;
        }
        if (theCullingFlag) {
            // u
            s.sub(theRayOrigin,
                  thePlanePointA);
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
            q.cross(s,
                    _myTempEdge1);
            t = _myTempEdge2.dot(q);
            // invert
            f = 1.0f / a;
            u *= f;
            v *= f;
            t *= f;
        } else {
            f = 1f / a;
            // u
            s.sub(theRayOrigin,
                  thePlanePointA);
            u = f * s.dot(h);
            if (u < 0.0f || u > 1.0f) {
                return Float.NaN;
            }
            // v
            q.cross(s,
                    _myTempEdge1);
            v = f * theRayDirection.dot(q);
            if (v < 0.0 || u + v > 1.0) {
                return Float.NaN;
            }
            // t
            t = _myTempEdge2.dot(q) * f;
        }
        // result
        theResult.scale(t,
                        theRayDirection);
        theResult.add(theRayOrigin);

        return t;
    }


    /**
     * http://local.wasp.uwa.edu.au/~pbourke/geometry/sphereline/raysphere.c
       Calculate the intersection of a ray and a sphere
       The line segment is defined from p1 to p2
       The sphere is of radius r and centered at sc
       There are potentially two points of intersection given by
       p = p1 + mu1 (p2 - p1)
       p = p1 + mu2 (p2 - p1)
       Return FALSE if the ray doesn't intersect the sphere.
     */
    public static boolean RaySphere(Vector3f p1,
                                    Vector3f p2,
                                    Vector3f sc,
                                    float r) {
        float a, b, c;
        float bb4ac;
        Vector3f dp = new Vector3f();

        dp.x = p2.x - p1.x;
        dp.y = p2.y - p1.y;
        dp.z = p2.z - p1.z;
        a = dp.x * dp.x + dp.y * dp.y + dp.z * dp.z;
        b = 2 * (dp.x * (p1.x - sc.x) + dp.y * (p1.y - sc.y) + dp.z * (p1.z - sc.z));
        c = sc.x * sc.x + sc.y * sc.y + sc.z * sc.z;
        c += p1.x * p1.x + p1.y * p1.y + p1.z * p1.z;
        c -= 2 * (sc.x * p1.x + sc.y * p1.y + sc.z * p1.z);
        c -= r * r;
        bb4ac = b * b - 4 * a * c;
        if (Math.abs(a) < EPSILON || bb4ac < 0) {
            return false;
        }

//        float mu1 = ( -b + (float) Math.sqrt(bb4ac)) / (2 * a);
//        float mu2 = ( -b - (float) Math.sqrt(bb4ac)) / (2 * a);
//
//        Vector3f myP1 = new Vector3f(dp);
//        myP1.scale(mu1);
//        myP1.add(p1);
//
//        Vector3f myP2 = new Vector3f(dp);
//        myP2.scale(mu2);
//        myP2.add(p1);

        return true;
    }


    /**
     * from paul bourke ( http://local.wasp.uwa.edu.au/~pbourke/geometry/lineline2d/ )
     *
     */

    public static final int COINCIDENT = 0;

    public static final int PARALLEL = 1;

    public static final int INTERESECTING = 2;

    public static final int NOT_INTERESECTING = 3;

    public static int lineLineIntersect(Vector2f aBegin, Vector2f aEnd,
                                        Vector2f bBegin, Vector2f bEnd,
                                        Vector2f theIntersection) {
        float denom = ( (bEnd.y - bBegin.y) * (aEnd.x - aBegin.x)) -
                      ( (bEnd.x - bBegin.x) * (aEnd.y - aBegin.y));

        float nume_a = ( (bEnd.x - bBegin.x) * (aBegin.y - bBegin.y)) -
                       ( (bEnd.y - bBegin.y) * (aBegin.x - bBegin.x));

        float nume_b = ( (aEnd.x - aBegin.x) * (aBegin.y - bBegin.y)) -
                       ( (aEnd.y - aBegin.y) * (aBegin.x - bBegin.x));

        if (denom == 0.0f) {
            if (nume_a == 0.0f && nume_b == 0.0f) {
                return COINCIDENT;
            }
            return PARALLEL;
        }

        float ua = nume_a / denom;
        float ub = nume_b / denom;

        if (ua >= 0.0f && ua <= 1.0f && ub >= 0.0f && ub <= 1.0f) {
            if (theIntersection != null) {
                // Get the intersection point.
                theIntersection.x = aBegin.x + ua * (aEnd.x - aBegin.x);
                theIntersection.y = aBegin.y + ua * (aEnd.y - aBegin.y);
            }
            return INTERESECTING;
        }

        return NOT_INTERESECTING;
    }


    /**
     * from paul bourke ( http://local.wasp.uwa.edu.au/~pbourke/geometry/lineline3d/ )
     *
     * Calculate the line segment PaPb that is the shortest route between
     * two lines P1P2 and P3P4. Calculate also the values of mua and mub where
     * Pa = P1 + mua (P2 - P1)
     * Pb = P3 + mub (P4 - P3)
     * Return FALSE if no solution exists.
     *
     */
    public static boolean lineLineIntersect(Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4,
                                            Vector3f pa, Vector3f pb,
                                            float[] theResult) {

        final Vector3f p13 = mathematik.Util.sub(p1, p3);
        final Vector3f p43 = mathematik.Util.sub(p4, p3);
        if (Math.abs(p43.x) < EPSILON && Math.abs(p43.y) < EPSILON && Math.abs(p43.z) < EPSILON) {
            return false;
        }

        final Vector3f p21 = mathematik.Util.sub(p2, p1);
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

        pa.x = p1.x + mua * p21.x;
        pa.y = p1.y + mua * p21.y;
        pa.z = p1.z + mua * p21.z;
        pb.x = p3.x + mub * p43.x;
        pb.y = p3.y + mub * p43.y;
        pb.z = p3.z + mub * p43.z;

        if (theResult != null) {
            theResult[0] = mua;
            theResult[1] = mub;
        }
        return true;
    }


    public static void main(String[] args) {
        Vector3f myP1 = new Vector3f();
        Vector3f myP2 = new Vector3f(10, 10, 10);
        Vector3f myP3 = new Vector3f(10, 0, 0);
        Vector3f myP4 = new Vector3f(0, 10, 10);
        Vector3f myPA = new Vector3f();
        Vector3f myPB = new Vector3f();
        lineLineIntersect(myP1, myP2, myP3, myP4, myPA, myPB, null);
        System.out.println(myPA);
        System.out.println(myPB);
    }
}
