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


import java.util.Vector;

import gestalt.Gestalt;


/**
 *
 * a loose collection of linear algebra methods that are not
 * connected to a specific class.
 *
 */
public class Util {

    public static final Vector3f AXIS_X = new Vector3f(1, 0, 0);

    public static final Vector3f AXIS_Y = new Vector3f(0, 1, 0);

    public static final Vector3f AXIS_Z = new Vector3f(0, 0, 1);

    public static final float areaTriangle(final Vector3f v0,
                                           final Vector3f v1,
                                           final Vector3f v2) {
        final Vector3f myAB = sub(v1, v0);
        final Vector3f myAC = sub(v2, v0);
        final Vector3f myCross = cross(myAB, myAC);
        return 0.5f * myCross.magnitude();
    }

    public float length(Vector3f theVector3f) {
        return theVector3f.length();
    }

    public static final boolean isPointInTriangle(final Vector3f v0,
                                                  final Vector3f v1,
                                                  final Vector3f v2,
                                                  final Vector3f thePoint) {
//    // Compute vectors
//    v0 = C - A
//    v1 = B - A
//    v2 = P - A

        Vector3f v00 = new Vector3f(v2);
        v00.sub(v0);

        Vector3f v01 = new Vector3f(v1);
        v01.sub(v0);

        Vector3f v02 = new Vector3f(thePoint);
        v02.sub(v0);

        // Compute dot products

        float dot00 = v00.dot(v00);
        float dot01 = v00.dot(v01);
        float dot02 = v00.dot(v02);
        float dot11 = v01.dot(v01);
        float dot12 = v01.dot(v02);

        // Compute barycentric coordinates
        float invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
        float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
        float v = (dot00 * dot12 - dot01 * dot02) * invDenom;

        // Check if point is in triangle
        return (u > 0) && (v > 0) && (u + v < 1);
    }


    /* contain */
    public static final boolean contains(final Vector3f thePosition,
                                         final WorldAxisAlignedBoundingBox theWorldAlignedBox) {
        return (contains(thePosition.x, theWorldAlignedBox.position.x, theWorldAlignedBox.scale.x)
                && contains(thePosition.y, theWorldAlignedBox.position.y, theWorldAlignedBox.scale.y)
                && contains(thePosition.z, theWorldAlignedBox.position.z, theWorldAlignedBox.scale.z));
    }

    public static final boolean contains(final float theTestValue,
                                         final float theContainerValue,
                                         final float theRange) {
        return (theTestValue > theContainerValue - theRange * 0.5f
                && theTestValue < theContainerValue + theRange * 0.5f);
    }

    public static boolean insidePolygon(Vector2f thePoint, Vector2f[] thePolygon) {
        float x = thePoint.x;
        float y = thePoint.y;

        int c = 0;
        for (int i = 0, j = thePolygon.length - 1; i < thePolygon.length; j = i++) {
            if ((((thePolygon[i].y <= y) && (y < thePolygon[j].y))
                    || ((thePolygon[j].y <= y) && (y < thePolygon[i].y)))
                    && (x < (thePolygon[j].x - thePolygon[i].x) * (y - thePolygon[i].y)
                    / (thePolygon[j].y - thePolygon[i].y) + thePolygon[i].x)) {
                c = (c + 1) % 2;
            }
        }
        return c == 1;
    }

    public static final boolean insidePolygon(final Vector2f thePoint, final Vector<Vector2f> thePolygon) {
        float x = thePoint.x;
        float y = thePoint.y;

        int c = 0;
        for (int i = 0, j = thePolygon.size() - 1; i < thePolygon.size(); j = i++) {
            if ((((thePolygon.get(i).y <= y) && (y < thePolygon.get(j).y))
                    || ((thePolygon.get(j).y <= y) && (y < thePolygon.get(i).y)))
                    && (x < (thePolygon.get(j).x - thePolygon.get(i).x) * (y - thePolygon.get(i).y)
                    / (thePolygon.get(j).y - thePolygon.get(i).y) + thePolygon.get(i).x)) {
                c = (c + 1) % 2;
            }
        }
        return c == 1;
    }

    public static final boolean inside2DPolygon(final Vector3f thePoint, final Vector<Vector3f> thePolygon) {
        float x = thePoint.x;
        float y = thePoint.y;

        int c = 0;
        for (int i = 0, j = thePolygon.size() - 1; i < thePolygon.size(); j = i++) {
            if ((((thePolygon.get(i).y <= y) && (y < thePolygon.get(j).y))
                    || ((thePolygon.get(j).y <= y) && (y < thePolygon.get(i).y)))
                    && (x < (thePolygon.get(j).x - thePolygon.get(i).x) * (y - thePolygon.get(i).y)
                    / (thePolygon.get(j).y - thePolygon.get(i).y) + thePolygon.get(i).x)) {
                c = (c + 1) % 2;
            }
        }
        return c == 1;
    }

    private static final Vector3f _myTempMin = new Vector3f();

    private static final Vector3f _myTempMax = new Vector3f();

    public static void updateBoundingBox(final WorldAxisAlignedBoundingBox theWorldAxisAlignedBoundingBox,
                                         final Vector3f[] myVectors) {

        if (myVectors == null || myVectors.length == 0) {
            return;
        }

        /* get minimum and maximum */
        _myTempMin.set(myVectors[0]);
        _myTempMax.set(myVectors[0]);

        for (int i = 1; i < myVectors.length; i++) {
            /* minimum */
            if (_myTempMin.x > myVectors[i].x) {
                _myTempMin.x = myVectors[i].x;
            }
            if (_myTempMin.y > myVectors[i].y) {
                _myTempMin.y = myVectors[i].y;
            }
            if (_myTempMin.z > myVectors[i].z) {
                _myTempMin.z = myVectors[i].z;
            }
            /* maximum */
            if (_myTempMax.x < myVectors[i].x) {
                _myTempMax.x = myVectors[i].x;
            }
            if (_myTempMax.y < myVectors[i].y) {
                _myTempMax.y = myVectors[i].y;
            }
            if (_myTempMax.z < myVectors[i].z) {
                _myTempMax.z = myVectors[i].z;
            }
        }

        /* create world aligned boundingbox */
        /* bb position */
        theWorldAxisAlignedBoundingBox.position.sub(_myTempMax, _myTempMin);
        theWorldAxisAlignedBoundingBox.position.scale(0.5f);
        theWorldAxisAlignedBoundingBox.position.add(_myTempMin);
        /* bb scale */
        theWorldAxisAlignedBoundingBox.scale.sub(_myTempMax, _myTempMin);
        theWorldAxisAlignedBoundingBox.scale.x = Math.abs(theWorldAxisAlignedBoundingBox.scale.x);
        theWorldAxisAlignedBoundingBox.scale.y = Math.abs(theWorldAxisAlignedBoundingBox.scale.y);
        theWorldAxisAlignedBoundingBox.scale.z = Math.abs(theWorldAxisAlignedBoundingBox.scale.z);
    }


    /* transforms */
    private static final Vector3f _myTempForwardVector = new Vector3f();

    private static final Vector3f _myTempSideVector = new Vector3f();

    private static final Vector3f _myTempUpVector = new Vector3f();

    public static void pointAt(final TransformMatrix4f theResult,
                               final Vector3f theUpVector,
                               final Vector3f thePointAtPosition) {

        pointAt(theResult,
                theResult.translation,
                theUpVector,
                thePointAtPosition);

//        /* forward */
//        _myTempForwardVector.sub(theResult.translation, thePointAtPosition);
//        _myTempForwardVector.normalize();
//
//        /* side */
//        _myTempSideVector.cross(theUpVector, _myTempForwardVector);
//        _myTempSideVector.normalize();
//
//        /* up */
//        _myTempUpVector.cross(_myTempForwardVector, _myTempSideVector);
//        _myTempUpVector.normalize();
//
//        if (!_myTempSideVector.isNaN() &&
//                !_myTempUpVector.isNaN() &&
//                !_myTempForwardVector.isNaN()) {
//            theResult.rotation.setXAxis(_myTempSideVector);
//            theResult.rotation.setYAxis(_myTempUpVector);
//            theResult.rotation.setZAxis(_myTempForwardVector);
//        }
    }

    public static void pointAt(final TransformMatrix4f theResult,
                               final Vector3f thePosition,
                               final Vector3f theUpVector,
                               final Vector3f thePointAtPosition) {
        /* forward */
        _myTempForwardVector.sub(thePosition, thePointAtPosition);
        _myTempForwardVector.normalize();

        /* side */
        _myTempSideVector.cross(theUpVector, _myTempForwardVector);
        _myTempSideVector.normalize();

        /* up */
        _myTempUpVector.cross(_myTempForwardVector, _myTempSideVector);
        _myTempUpVector.normalize();

        if (!_myTempSideVector.isNaN()
                && !_myTempUpVector.isNaN()
                && !_myTempForwardVector.isNaN()) {
            theResult.rotation.setXAxis(_myTempSideVector);
            theResult.rotation.setYAxis(_myTempUpVector);
            theResult.rotation.setZAxis(_myTempForwardVector);
        }
    }

    public static void pointAlong(final TransformMatrix4f theResult,
                                  final Vector3f theForwardVector,
                                  final Vector3f theUpVector) {
        /* forward */
        _myTempForwardVector.set(theForwardVector);
        _myTempForwardVector.normalize();

        /* side */
        _myTempSideVector.cross(theUpVector, _myTempForwardVector);
        _myTempSideVector.normalize();

        /* up */
        _myTempUpVector.cross(_myTempForwardVector, _myTempSideVector);
        _myTempUpVector.normalize();

        if (!_myTempSideVector.isNaN()
                && !_myTempUpVector.isNaN()
                && !_myTempForwardVector.isNaN()) {
            theResult.rotation.setXAxis(_myTempSideVector);
            theResult.rotation.setYAxis(_myTempUpVector);
            theResult.rotation.setZAxis(_myTempForwardVector);
        }
    }

    public static final void toLocalSpace(TransformMatrix4f theLocalSpace, Vector3f theLocalResult) {
        theLocalResult.sub(theLocalSpace.translation);
        theLocalSpace.rotation.transform(theLocalResult);
    }

    public static final float bilinearInterp(final float x, final float y,
                                             final float q00,
                                             final float q10,
                                             final float q01,
                                             final float q11) {
        return q00 * (1 - x) * (1 - y)
                + q10 * x * (1 - y)
                + q01 * (1 - x) * y
                + q11 * x * y;
    }


    /* normal */
    private static final Vector3f TMP_BA = new Vector3f();

    private static final Vector3f TMP_BC = new Vector3f();

    /**
     * calculate a normal from a set of three vectors.
     *
     * @param pointA
     * @param pointB
     * @param pointC
     * @param theResultNormal
     */
    public static final void calculateNormal(final Vector3f pointA,
                                             final Vector3f pointB,
                                             final Vector3f pointC,
                                             final Vector3f theResultNormal) {
        TMP_BA.sub(pointB, pointA);
        TMP_BC.sub(pointC, pointB);

        theResultNormal.cross(TMP_BA, TMP_BC);
        theResultNormal.normalize();
    }

    /**
     *
     * @param theVectorAB Vector3f
     * @param theVectorBC Vector3f
     * @param theResultNormal Vector3f
     */
    public static final void calculateNormal(final Vector3f theVectorAB,
                                             final Vector3f theVectorBC,
                                             final Vector3f theResultNormal) {
        theResultNormal.cross(theVectorAB, theVectorBC);
        theResultNormal.normalize();
    }

    /**
     *
     * @param pointA Vector3f
     * @param pointB Vector3f
     * @param pointC Vector3f
     * @return Vector3f
     */
    public static final Vector3f createNormal(final Vector3f pointA,
                                              final Vector3f pointB,
                                              final Vector3f pointC) {
        final Vector3f myResultNormal = new Vector3f();
        calculateNormal(pointA,
                        pointB,
                        pointC,
                        myResultNormal);
        return myResultNormal;
    }

    /**
     *
     * @param theVectorAB Vector3f
     * @param theVectorBC Vector3f
     * @return Vector3f
     */
    public static final Vector3f createNormal(final Vector3f theVectorAB,
                                              final Vector3f theVectorBC) {
        final Vector3f myResultNormal = new Vector3f();
        calculateNormal(theVectorAB,
                        theVectorBC,
                        myResultNormal);
        return myResultNormal;
    }

    public static void createNormals(float[] theVertices, float[] theNormals) {
        final int NUMBER_OF_VERTEX_COMPONENTS = 3;
        final int myNumberOfPoints = 3;
        for (int i = 0; i < theVertices.length;
                i += (myNumberOfPoints * NUMBER_OF_VERTEX_COMPONENTS)) {
            Vector3f a = new Vector3f(theVertices[i + 0], theVertices[i + 1], theVertices[i + 2]);
            Vector3f b = new Vector3f(theVertices[i + 3], theVertices[i + 4], theVertices[i + 5]);
            Vector3f c = new Vector3f(theVertices[i + 6], theVertices[i + 7], theVertices[i + 8]);
            Vector3f myNormal = new Vector3f();
            mathematik.Util.calculateNormal(a, b, c, myNormal);

            theNormals[i + 0] = myNormal.x;
            theNormals[i + 1] = myNormal.y;
            theNormals[i + 2] = myNormal.z;

            theNormals[i + 3] = myNormal.x;
            theNormals[i + 4] = myNormal.y;
            theNormals[i + 5] = myNormal.z;

            theNormals[i + 6] = myNormal.x;
            theNormals[i + 7] = myNormal.y;
            theNormals[i + 8] = myNormal.z;
        }
    }


    /* distance */
    private static final Vector3f _myTempVector = new Vector3f();

    /**
     * return the distance between to points defined by two vectors.
     *
     * @param theVectorA
     *            Vector3f
     * @param theVectorB
     *            Vector3f
     * @return float
     */
    public static final float distance(Vector3f theVectorA, Vector3f theVectorB) {
        _myTempVector.set(theVectorA);
        return _myTempVector.distance(theVectorB);
    }


    /* add */
    /**
     * add two vectors and return the result in a new instance.
     *
     * @param theVectorA
     *            Vector4f
     * @param theVectorB
     *            Vector4f
     * @return Vector4f
     */
    public static final Vector4f add(Vector4f theVectorA, Vector4f theVectorB) {
        return new Vector4f(theVectorA.w + theVectorB.w,
                            theVectorA.x + theVectorB.x,
                            theVectorA.y + theVectorB.y,
                            theVectorA.z + theVectorB.z);
    }

    /**
     * add two vectors and return the result in a new instance.
     *
     * @param theVectorA
     *            Vector3f
     * @param theVectorB
     *            Vector3f
     * @return Vector3f
     */
    public static final Vector3f add(Vector3f theVectorA, Vector3f theVectorB) {
        return new Vector3f(theVectorA.x + theVectorB.x, theVectorA.y + theVectorB.y, theVectorA.z + theVectorB.z);
    }

    /**
     * add two vectors and return the result in a new instance.
     *
     * @param theVectorA
     *            Vector2f
     * @param theVectorB
     *            Vector2f
     * @return Vector2f
     */
    public static final Vector2f add(Vector2f theVectorA, Vector2f theVectorB) {
        return new Vector2f(theVectorA.x + theVectorB.x,
                            theVectorA.y + theVectorB.y);
    }

    /**
     * add two vectors and return the result in a new instance.
     *
     * @param theVectorA
     *            Vector3i
     * @param theVectorB
     *            Vector3i
     * @return Vector3i
     */
    public static final Vector3i add(Vector3i theVectorA, Vector3i theVectorB) {
        return new Vector3i(theVectorA.x + theVectorB.x, theVectorA.y + theVectorB.y, theVectorA.z + theVectorB.z);
    }

    /**
     * add two vectors and return the result in a new instance.
     *
     * @param theVectorA
     *            Vector2i
     * @param theVectorB
     *            Vector2i
     * @return Vector2i
     */
    public static final Vector2i add(Vector2i theVectorA, Vector2i theVectorB) {
        return new Vector2i(theVectorA.x + theVectorB.x, theVectorA.y + theVectorB.y);
    }


    /* sub */
    /**
     * subtract two vectors and return the result in a new instance.
     *
     * @param theVectorA
     *            Vector4f
     * @param theVectorB
     *            Vector4f
     * @return Vector4f
     */
    public static final Vector4f sub(Vector4f theVectorA, Vector4f theVectorB) {
        return new Vector4f(theVectorA.w - theVectorB.w, theVectorA.x - theVectorB.x, theVectorA.y - theVectorB.y,
                            theVectorA.z - theVectorB.z);
    }

    /**
     * subtract two vectors and return the result in a new instance.
     *
     * @param theVectorA
     *            Vector3f
     * @param theVectorB
     *            Vector3f
     * @return Vector3f
     */
    public static final Vector3f sub(Vector3f theVectorA, Vector3f theVectorB) {
        return new Vector3f(theVectorA.x - theVectorB.x, theVectorA.y - theVectorB.y, theVectorA.z - theVectorB.z);
    }

    /**
     * subtract two vectors and return the result in a new instance.
     *
     * @param theVectorA
     *            Vector2f
     * @param theVectorB
     *            Vector2f
     * @return Vector2f
     */
    public static final Vector2f sub(Vector2f theVectorA, Vector2f theVectorB) {
        return new Vector2f(theVectorA.x - theVectorB.x, theVectorA.y - theVectorB.y);
    }

    /**
     * subtract two vectors and return the result in a new instance.
     *
     * @param theVectorA
     *            Vector3i
     * @param theVectorB
     *            Vector3i
     * @return Vector3i
     */
    public static final Vector3i sub(Vector3i theVectorA, Vector3i theVectorB) {
        return new Vector3i(theVectorA.x - theVectorB.x,
                            theVectorA.y - theVectorB.y,
                            theVectorA.z - theVectorB.z);
    }

    /**
     * subtract two vectors and return the result in a new instance.
     *
     * @param theVectorA Vector2i
     * @param theVectorB Vector2i
     * @return Vector2i
     */
    public static final Vector2i sub(Vector2i theVectorA, Vector2i theVectorB) {
        return new Vector2i(theVectorA.x - theVectorB.x,
                            theVectorA.y - theVectorB.y);
    }

    /**
     * return the cross between two vectors in a new vector.
     *
     * @param theVectorA Vector3f
     * @param theVectorB Vector3f
     * @return Vector3f
     */
    public static final Vector3f cross(Vector3f theVectorA, Vector3f theVectorB) {
        final Vector3f myVector = new Vector3f();
        myVector.cross(theVectorA, theVectorB);
        return myVector;
    }

    /**
     * return a normalized vector in a new vector.
     * @param theVector Vector3f
     * @return Vector3f
     */
    public static final Vector3f normalized(Vector3f theVector) {
        final Vector3f myVector = new Vector3f(theVector);
        myVector.normalize();
        return myVector;
    }

    /**
     *
     * @param theVectorA Vector2f
     * @param theValue float
     * @return Vector2f
     */
    public static final Vector2f scale(Vector2f theVectorA, float theValue) {
        return new Vector2f(theVectorA.x * theValue,
                            theVectorA.y * theValue);
    }

    public static final Vector3f scale(Vector3f theVectorA, float theValue) {
        return new Vector3f(theVectorA.x * theValue,
                            theVectorA.y * theValue,
                            theVectorA.z * theValue);
    }

    public static final Vector3f scale(Vector3f theVectorA, Vector3f theValue) {
        return new Vector3f(theVectorA.x * theValue.x,
                            theVectorA.y * theValue.y,
                            theVectorA.z * theValue.z);
    }

    public static final Vector4f scale(Vector4f theVectorA, float theValue) {
        return new Vector4f(theVectorA.x * theValue,
                            theVectorA.y * theValue,
                            theVectorA.z * theValue,
                            theVectorA.w * theValue);
    }

    public static final float clamp(float theValue, float theMin, float theMax) {
        if (theValue > theMax) {
            theValue = theMax;
        }
        if (theValue < theMin) {
            theValue = theMin;
        }
        return theValue;
    }

    public static final float normalize(float theValue, float theStart, float theEnd) {
        return (theValue - theStart) / (theEnd - theStart);
    }

    public static final float map(float theValue, float theInStart, float theInEnd, float theOutStart, float theOutEnd) {
        return theOutStart + (theOutEnd - theOutStart) * ((theValue - theInStart) / (theInEnd - theInStart));
    }

    public static Vector2f parseVector2f(String theString) {
        String splitter;
        if (contains(theString, ",")) {
            splitter = ",";
        } else {
            throw new IllegalArgumentException();
        }
        String[] coords = theString.split(splitter);
        return new Vector2f(Float.parseFloat(coords[0]),
                            Float.parseFloat(coords[1]));
    }

    public static Vector3f parseVector3f(String theString) {
        String splitter;
        if (contains(theString, ",")) {
            splitter = ",";
        } else {
            throw new IllegalArgumentException();
        }
        String[] coords = theString.split(splitter);
        return new Vector3f(Float.parseFloat(coords[0]),
                            Float.parseFloat(coords[1]),
                            Float.parseFloat(coords[2]));
    }

    public static boolean contains(String theString, CharSequence theContainedString) {
        return theString.indexOf(theContainedString.toString()) > -1;
    }

    /**
     * the following point rotation methods are from the famous paul bourke.
     * read more on his website at
     *
     *    http://local.wasp.uwa.edu.au/~pbourke/geometry/rotate/
     *
     */
    /**
     * Rotate a point p by angle theta around an arbitrary axis r
     * Return the rotated point.
     * Positive angles are anticlockwise looking down the axis towards the origin.
     * Assume right hand coordinate system.
     *
     * @param p Vector3f
     * @param theta double
     * @param theAxis Vector3f
     * @return Vector3f
     */
    public static Vector3f rotatePoint(Vector3f p,
                                       double theta,
                                       Vector3f theAxis) {
        Vector3f myR = new Vector3f();
        Vector3f q = new Vector3f();
        double costheta, sintheta;

        myR.normalize(theAxis);

        costheta = Math.cos(theta);
        sintheta = Math.sin(theta);

        q.x += (costheta + (1 - costheta) * myR.x * myR.x) * p.x;
        q.x += ((1 - costheta) * myR.x * myR.y - myR.z * sintheta) * p.y;
        q.x += ((1 - costheta) * myR.x * myR.z + myR.y * sintheta) * p.z;

        q.y += ((1 - costheta) * myR.x * myR.y + myR.z * sintheta) * p.x;
        q.y += (costheta + (1 - costheta) * myR.y * myR.y) * p.y;
        q.y += ((1 - costheta) * myR.y * myR.z - myR.x * sintheta) * p.z;

        q.z += ((1 - costheta) * myR.x * myR.z - myR.y * sintheta) * p.x;
        q.z += ((1 - costheta) * myR.y * myR.z + myR.x * sintheta) * p.y;
        q.z += (costheta + (1 - costheta) * myR.z * myR.z) * p.z;

        return q;
    }


    /*
     * Rotate a point p by angle theta around an arbitrary line segment p1-p2
     * Return the rotated point.
     * Positive angles are anticlockwise looking down the axis towards the origin.
     * Assume right hand coordinate system.
     */
    public static Vector3f rotatePoint(Vector3f p,
                                       double theta,
                                       Vector3f p1,
                                       Vector3f p2) {
        Vector3f r = new Vector3f();
        Vector3f q = new Vector3f();
        Vector3f myP = new Vector3f();
        double costheta, sintheta;

        myP.set(p);

        r.x = p2.x - p1.x;
        r.y = p2.y - p1.y;
        r.z = p2.z - p1.z;
        myP.x -= p1.x;
        myP.y -= p1.y;
        myP.z -= p1.z;

        r.normalize();

        costheta = Math.cos(theta);
        sintheta = Math.sin(theta);

        q.x += (costheta + (1 - costheta) * r.x * r.x) * myP.x;
        q.x += ((1 - costheta) * r.x * r.y - r.z * sintheta) * myP.y;
        q.x += ((1 - costheta) * r.x * r.z + r.y * sintheta) * myP.z;

        q.y += ((1 - costheta) * r.x * r.y + r.z * sintheta) * myP.x;
        q.y += (costheta + (1 - costheta) * r.y * r.y) * myP.y;
        q.y += ((1 - costheta) * r.y * r.z - r.x * sintheta) * myP.z;

        q.z += ((1 - costheta) * r.x * r.z - r.y * sintheta) * myP.x;
        q.z += ((1 - costheta) * r.y * r.z + r.x * sintheta) * myP.y;
        q.z += (costheta + (1 - costheta) * r.z * r.z) * myP.z;

        q.x += p1.x;
        q.y += p1.y;
        q.z += p1.z;

        return q;
    }

    /**
     *
     * DistancePointLine Unit Test
     * Copyright (c) 2002, All rights reserved
     *
     * Damian Coventry
     * Tuesday, 16 July 2002
     *
     * Implementation of theory by Paul Bourke
     *
     * @param thePoint Vector3f
     * @param theLineStart Vector3f
     * @param theLineEnd Vector3f
     * @return float
     */
    public static float distancePointLineSegment(final Vector3f thePoint,
                                                 final Vector3f theLineStart,
                                                 final Vector3f theLineEnd) {
        final float u = distancePointLineU(thePoint, theLineStart, theLineEnd);

        if (u < 0.0f || u > 1.0f) {
            return -1; // closest point does not fall within the line segment
        }

        final Vector3f myIntersection = new Vector3f();
        myIntersection.x = theLineStart.x + u * (theLineEnd.x - theLineStart.x);
        myIntersection.y = theLineStart.y + u * (theLineEnd.y - theLineStart.y);
        myIntersection.z = theLineStart.z + u * (theLineEnd.z - theLineStart.z);

        return thePoint.distance(myIntersection);
    }

    public static float distancePointLine(final Vector3f thePoint,
                                          final Vector3f theLineStart,
                                          final Vector3f theLineEnd) {
        final float u = distancePointLineU(thePoint, theLineStart, theLineEnd);
        final Vector3f myIntersection = new Vector3f();
        myIntersection.x = theLineStart.x + u * (theLineEnd.x - theLineStart.x);
        myIntersection.y = theLineStart.y + u * (theLineEnd.y - theLineStart.y);
        myIntersection.z = theLineStart.z + u * (theLineEnd.z - theLineStart.z);

        return thePoint.distance(myIntersection);
    }

    public static float distancePointLineU(final Vector3f thePoint,
                                           final Vector3f theLineStart,
                                           final Vector3f theLineEnd) {
        final float myLineMagnitude = theLineStart.distance(theLineEnd);
        final float u = (((thePoint.x - theLineStart.x) * (theLineEnd.x - theLineStart.x))
                + ((thePoint.y - theLineStart.y) * (theLineEnd.y - theLineStart.y))
                + ((thePoint.z - theLineStart.z) * (theLineEnd.z - theLineStart.z)))
                / (myLineMagnitude * myLineMagnitude);

        return u;
    }

    public static float random(float theStart,
                               float theEnd) {
        final float myDiff = theEnd - theStart;
        final float myRandomValue = (float)Math.random() * myDiff;
        return myRandomValue + theStart;
    }

    public static TransformMatrix4f getTranslateRotationTransform(final int theTransformMode,
                                                                  final TransformMatrix4f theTransform,
                                                                  final Vector3f theRotation,
                                                                  final Vector3f theScale) {

        final TransformMatrix4f myMatrix = new TransformMatrix4f(TransformMatrix4f.IDENTITY);

        if (theTransformMode == Gestalt.SHAPE_TRANSFORM_MATRIX
                || theTransformMode == Gestalt.SHAPE_TRANSFORM_MATRIX_AND_ROTATION) {
            myMatrix.multiply(theTransform);
        }

        if (theTransformMode == Gestalt.SHAPE_TRANSFORM_POSITION_AND_ROTATION) {
            final TransformMatrix4f myTranslationMatrix = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
            myTranslationMatrix.translation.set(theTransform.translation);
            myMatrix.multiply(myTranslationMatrix);
        }

        if (theTransformMode == Gestalt.SHAPE_TRANSFORM_POSITION_AND_ROTATION
                || theTransformMode == Gestalt.SHAPE_TRANSFORM_MATRIX_AND_ROTATION) {
            if (theRotation.x != 0.0f) {
                final TransformMatrix4f myRotationMatrix = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
                myRotationMatrix.rotation.setXRotation(theRotation.x);
                myMatrix.multiply(myRotationMatrix);
            }
            if (theRotation.y != 0.0f) {
                final TransformMatrix4f myRotationMatrix = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
                myRotationMatrix.rotation.setYRotation(theRotation.y);
                myMatrix.multiply(myRotationMatrix);
            }
            if (theRotation.z != 0.0f) {
                final TransformMatrix4f myRotationMatrix = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
                myRotationMatrix.rotation.setZRotation(theRotation.z);
                myMatrix.multiply(myRotationMatrix);
            }
        }

        /* finally scale the shape */
        final TransformMatrix4f myScaleMatrix = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
        myScaleMatrix.rotation.xx = theScale.x;
        myScaleMatrix.rotation.yy = theScale.y;
        myScaleMatrix.rotation.zz = theScale.z;
        myMatrix.multiply(myScaleMatrix);

        return myMatrix;
    }

    public static TransformMatrix4f getRotationTransform(final int theTransformMode,
                                                         final TransformMatrix4f theTransform,
                                                         final Vector3f theRotation,
                                                         final Vector3f theScale) {

        final TransformMatrix4f myMatrix = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
        final TransformMatrix4f myTransform = new TransformMatrix4f(theTransform);
        myTransform.translation.set(0, 0, 0);

        if (theTransformMode == Gestalt.SHAPE_TRANSFORM_MATRIX
                || theTransformMode == Gestalt.SHAPE_TRANSFORM_MATRIX_AND_ROTATION) {
            myMatrix.multiply(myTransform);
        }

        if (theTransformMode == Gestalt.SHAPE_TRANSFORM_POSITION_AND_ROTATION
                || theTransformMode == Gestalt.SHAPE_TRANSFORM_MATRIX_AND_ROTATION) {
            if (theRotation.x != 0.0f) {
                final TransformMatrix4f myRotationMatrix = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
                myRotationMatrix.rotation.setXRotation(theRotation.x);
                myMatrix.multiply(myRotationMatrix);
            }
            if (theRotation.y != 0.0f) {
                final TransformMatrix4f myRotationMatrix = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
                myRotationMatrix.rotation.setYRotation(theRotation.y);
                myMatrix.multiply(myRotationMatrix);
            }
            if (theRotation.z != 0.0f) {
                final TransformMatrix4f myRotationMatrix = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
                myRotationMatrix.rotation.setZRotation(theRotation.z);
                myMatrix.multiply(myRotationMatrix);
            }
        }

        /* finally scale the shape */
        final TransformMatrix4f myScaleMatrix = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
        myScaleMatrix.rotation.xx = theScale.x;
        myScaleMatrix.rotation.yy = theScale.y;
        myScaleMatrix.rotation.zz = theScale.z;
        myMatrix.multiply(myScaleMatrix);

        return myMatrix;
    }

    public static final int CLOCKWISE = 1;

    public static final int COUNTERCLOCKWISE = -1;

    /**
     * adapted from genius paul bourke
     * http://debian.fmi.uni-sofia.bg/~sergei/cgsr/docs/clockwise.htm
     *
    Return the clockwise status of a curve, clockwise or counterclockwise
    n vertices making up curve p
    return 0 for incomputables eg: colinear points
    CLOCKWISE == 1
    COUNTERCLOCKWISE == -1
    It is assumed that
    - the polygon is closed
    - the last point is not repeated.
    - the polygon is simple (does not intersect itself or have holes)
     */
    public static int isClockWise(final Vector<Vector2f> mPoints) {

        if (mPoints.size() < 3) {
            return (0);
        }

        int mCount = 0;
        for (int i = 0; i < mPoints.size(); i++) {
            final Vector2f p1 = mPoints.get(i);
            final Vector2f p2 = mPoints.get((i + 1) % mPoints.size());
            final Vector2f p3 = mPoints.get((i + 2) % mPoints.size());
            float z;
            z = (p2.x - p1.x) * (p3.y - p2.y);
            z -= (p2.y - p1.y) * (p3.x - p2.x);
            if (z < 0) {
                mCount--;
            } else if (z > 0) {
                mCount++;
            }
        }
        if (mCount > 0) {
            return (COUNTERCLOCKWISE);
        } else if (mCount < 0) {
            return (CLOCKWISE);
        } else {
            return (0);
        }
    }

    public static int isClockWise2D(final Vector<Vector3f> mPoints) {

        if (mPoints.size() < 3) {
            return (0);
        }

        int mCount = 0;
        for (int i = 0; i < mPoints.size(); i++) {
            final Vector3f p1 = mPoints.get(i);
            final Vector3f p2 = mPoints.get((i + 1) % mPoints.size());
            final Vector3f p3 = mPoints.get((i + 2) % mPoints.size());
            float z;
            z = (p2.x - p1.x) * (p3.y - p2.y);
            z -= (p2.y - p1.y) * (p3.x - p2.x);
            if (z < 0) {
                mCount--;
            } else if (z > 0) {
                mCount++;
            }
        }
        if (mCount > 0) {
            return (COUNTERCLOCKWISE);
        } else if (mCount < 0) {
            return (CLOCKWISE);
        } else {
            return (0);
        }
    }

    public static final int CONVEX = 1;

    public static final int CONCAVE = -1;

    /**
     * adapted from genius paul bourke
     * http://debian.fmi.uni-sofia.bg/~sergei/cgsr/docs/clockwise.htm
     *
    Return whether a polygon in 2D is concave or convex
    return 0 for incomputables eg: colinear points
    CONVEX == 1
    CONCAVE == -1
    It is assumed that the polygon is simple
    (does not intersect itself or have holes)
     */
    public static int isConvex(final Vector<Vector2f> mPoints) {
        int flag = 0;
        double z;

        if (mPoints.size() < 3) {
            return (0);
        }

        for (int i = 0; i < mPoints.size(); i++) {
            final Vector2f p1 = mPoints.get(i);
            final Vector2f p2 = mPoints.get((i + 1) % mPoints.size());
            final Vector2f p3 = mPoints.get((i + 2) % mPoints.size());
            z = (p2.x - p1.x) * (p3.y - p2.y);
            z -= (p2.y - p1.y) * (p3.x - p2.x);
            if (z < 0) {
                flag |= 1;
            } else if (z > 0) {
                flag |= 2;
            }
            if (flag == 3) {
                return (CONCAVE);
            }
        }
        if (flag != 0) {
            return (CONVEX);
        } else {
            return (0);
        }
    }

    public static int isConvex2D(final Vector<Vector3f> mPoints) {
        int flag = 0;
        double z;

        if (mPoints.size() < 3) {
            return (0);
        }

        for (int i = 0; i < mPoints.size(); i++) {
            final Vector3f p1 = mPoints.get(i);
            final Vector3f p2 = mPoints.get((i + 1) % mPoints.size());
            final Vector3f p3 = mPoints.get((i + 2) % mPoints.size());
            z = (p2.x - p1.x) * (p3.y - p2.y);
            z -= (p2.y - p1.y) * (p3.x - p2.x);
            if (z < 0) {
                flag |= 1;
            } else if (z > 0) {
                flag |= 2;
            }
            if (flag == 3) {
                return (CONCAVE);
            }
        }
        if (flag != 0) {
            return (CONVEX);
        } else {
            return (0);
        }
    }
}
