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

import processing.core.PMatrix3D;
import processing.core.PVector;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.force.TriangleDeflector;
import teilchen.force.TriangleDeflectorIndexed;

import java.util.ArrayList;

import static processing.core.PVector.add;
import static processing.core.PVector.sub;

public class Util {

    private static final float ALMOST_THRESHOLD = 0.001f;
    private static final PVector TMP_MIN = new PVector();
    private static final PVector TMP_MAX = new PVector();
    /* normal */
    private static final PVector TMP_BA = new PVector();
    private static final PVector TMP_BC = new PVector();
    private static final java.util.Random RND_GENERATOR = new java.util.Random();
    private static final PVector TMP_NORMAL = new PVector();
    private static final PVector TMP_TANGENT = new PVector();

    /* contain */
    public static boolean contains(final PVector thePosition, final WorldAxisAlignedBoundingBox theWorldAlignedBox) {
        return (contains(thePosition.x, theWorldAlignedBox.position.x, theWorldAlignedBox.scale.x) && contains(
                thePosition.y,
                theWorldAlignedBox.position.y,
                theWorldAlignedBox.scale.y) && contains(thePosition.z,
                                                        theWorldAlignedBox.position.z,
                                                        theWorldAlignedBox.scale.z));
    }

    public static boolean contains(final float theTestValue, final float theContainerValue, final float theRange) {
        return (theTestValue > theContainerValue - theRange * 0.5f && theTestValue < theContainerValue + theRange * 0.5f);
    }

    public static boolean insidePolygon(PVector thePoint, PVector[] thePolygon) {
        float x = thePoint.x;
        float y = thePoint.y;

        int c = 0;
        for (int i = 0, j = thePolygon.length - 1; i < thePolygon.length; j = i++) {
            if ((((thePolygon[i].y <= y) && (y < thePolygon[j].y)) || ((thePolygon[j].y <= y) && (y < thePolygon[i].y))) && (x < (thePolygon[j].x - thePolygon[i].x) * (y - thePolygon[i].y) / (thePolygon[j].y - thePolygon[i].y) + thePolygon[i].x)) {
                c = (c + 1) % 2;
            }
        }
        return c == 1;
    }

    public static boolean insidePolygon(final PVector thePoint, final ArrayList<PVector> thePolygon) {
        float x = thePoint.x;
        float y = thePoint.y;

        int c = 0;
        for (int i = 0, j = thePolygon.size() - 1; i < thePolygon.size(); j = i++) {
            if ((((thePolygon.get(i).y <= y) && (y < thePolygon.get(j).y)) || ((thePolygon.get(j).y <= y) && (y < thePolygon.get(
                    i).y))) && (x < (thePolygon.get(j).x - thePolygon.get(i).x) * (y - thePolygon.get(i).y) / (thePolygon.get(
                    j).y - thePolygon.get(i).y) + thePolygon.get(i).x)) {
                c = (c + 1) % 2;
            }
        }
        return c == 1;
    }

    public static boolean inside2DPolygon(final PVector thePoint, final ArrayList<PVector> thePolygon) {
        float x = thePoint.x;
        float y = thePoint.y;

        int c = 0;
        for (int i = 0, j = thePolygon.size() - 1; i < thePolygon.size(); j = i++) {
            if ((((thePolygon.get(i).y <= y) && (y < thePolygon.get(j).y)) || ((thePolygon.get(j).y <= y) && (y < thePolygon.get(
                    i).y))) && (x < (thePolygon.get(j).x - thePolygon.get(i).x) * (y - thePolygon.get(i).y) / (thePolygon.get(
                    j).y - thePolygon.get(i).y) + thePolygon.get(i).x)) {
                c = (c + 1) % 2;
            }
        }
        return c == 1;
    }

    public static void updateBoundingBox(final WorldAxisAlignedBoundingBox theWorldAxisAlignedBoundingBox,
                                         final PVector[] myVectors) {

        if (myVectors == null || myVectors.length == 0) {
            return;
        }

        /* get minimum and maximum */
        TMP_MIN.set(myVectors[0]);
        TMP_MAX.set(myVectors[0]);

        for (int i = 1; i < myVectors.length; i++) {
            /* minimum */
            if (TMP_MIN.x > myVectors[i].x) {
                TMP_MIN.x = myVectors[i].x;
            }
            if (TMP_MIN.y > myVectors[i].y) {
                TMP_MIN.y = myVectors[i].y;
            }
            if (TMP_MIN.z > myVectors[i].z) {
                TMP_MIN.z = myVectors[i].z;
            }
            /* maximum */
            if (TMP_MAX.x < myVectors[i].x) {
                TMP_MAX.x = myVectors[i].x;
            }
            if (TMP_MAX.y < myVectors[i].y) {
                TMP_MAX.y = myVectors[i].y;
            }
            if (TMP_MAX.z < myVectors[i].z) {
                TMP_MAX.z = myVectors[i].z;
            }
        }

        /* create world aligned boundingbox */
 /* bb position */
        sub(TMP_MAX, TMP_MIN, theWorldAxisAlignedBoundingBox.position);
        theWorldAxisAlignedBoundingBox.position.mult(0.5f);
        theWorldAxisAlignedBoundingBox.position.add(TMP_MIN);
        /* bb scale */
        sub(TMP_MAX, TMP_MIN, theWorldAxisAlignedBoundingBox.scale);
        theWorldAxisAlignedBoundingBox.scale.x = Math.abs(theWorldAxisAlignedBoundingBox.scale.x);
        theWorldAxisAlignedBoundingBox.scale.y = Math.abs(theWorldAxisAlignedBoundingBox.scale.y);
        theWorldAxisAlignedBoundingBox.scale.z = Math.abs(theWorldAxisAlignedBoundingBox.scale.z);
    }

    /**
     * calculate a normal from a set of three vectors.
     *
     * @param pointA
     * @param pointB
     * @param pointC
     * @param theResultNormal
     */
    public static void calculateNormal(final PVector pointA,
                                       final PVector pointB,
                                       final PVector pointC,
                                       final PVector theResultNormal) {
        sub(pointB, pointA, TMP_BA);
        sub(pointC, pointB, TMP_BC);
        PVector.cross(TMP_BA, TMP_BC, theResultNormal);
        theResultNormal.normalize();
    }

    /**
     * @param theVectorAB     PVector
     * @param theVectorBC     PVector
     * @param theResultNormal PVector
     */
    public static void calculateNormal(final PVector theVectorAB,
                                       final PVector theVectorBC,
                                       final PVector theResultNormal) {
        theResultNormal.cross(theVectorAB, theVectorBC);
        theResultNormal.normalize();
    }

    /**
     * Sets a position randomly distributed inside a sphere of unit radius
     * centered at the origin. Orientation will be random and length will range
     * between 0 and 1
     *
     * @param p
     */
    public static void randomize(PVector p) {
        p.x = RND_GENERATOR.nextFloat() * 2.0F - 1.0F;
        p.y = RND_GENERATOR.nextFloat() * 2.0F - 1.0F;
        p.z = RND_GENERATOR.nextFloat() * 2.0F - 1.0F;
        p.normalize();
    }

    /*
     * Rotate a point p by angle theta around an arbitrary line segment p1-p2
     * Return the rotated point.
     * Positive angles are anticlockwise looking down the axis towards the origin.
     * Assume right hand coordinate system.
     */
    public static PVector rotatePoint(PVector p, double theta, PVector p1, PVector p2) {
        PVector r = new PVector();
        PVector q = new PVector();
        PVector myP = new PVector();
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

    public static void divide(final PVector p, final PVector theVector) {
        p.x /= theVector.x;
        p.y /= theVector.y;
        p.z /= theVector.z;
    }

    public static void satisfyNeighborConstraints(final ArrayList<Particle> theParticles, final float theRelaxedness) {
        for (int i = 0; i < theParticles.size(); i++) {
            final Particle p1 = theParticles.get(i);
            for (int j = i + 1; j < theParticles.size(); j++) {
                final Particle p2 = theParticles.get(j);
                /* satisfy overlap */
                if (almost(p1.position(), p2.position())) {
                    p1.position().x += 0.01f;
                    p2.position().x -= 0.01f;
                    continue;
                }
                /* recover bad positions */
                if (isNaN(p1.position())) {
                    p1.position().set(p1.old_position());
                }
                if (isNaN(p2.position())) {
                    p2.position().set(p2.old_position());
                }
                final float myDistance = distance(p1.position(), p2.position());
                /* skip bad values */
                if (myDistance == 0.0f || Float.isNaN(myDistance)) {
                    continue;
                }
                final float myDesiredDistance = p1.radius() + p2.radius();
                if (myDistance < myDesiredDistance) {
                    final PVector myDiff = PVector.sub(p1.position(), p2.position());
                    myDiff.mult(1.0f / myDistance);
                    myDiff.mult(myDesiredDistance - myDistance);
                    myDiff.mult(0.5f);
                    myDiff.mult(theRelaxedness);
                    p1.position().add(myDiff);
                    p2.position().sub(myDiff);
                }
            }
        }
    }

    public static PVector clone(PVector p) {
        PVector v = new PVector();
        v.set(p);
        return v;
    }

    public static float angle(PVector p, PVector theVector) {
        float d = p.dot(theVector) / (p.mag() * theVector.mag());
        /**
         * @todo check these lines.
         */
        if (d < -1.0f) {
            d = -1.0f;
        }
        if (d > 1.0f) {
            d = 1.0f;
        }
        return (float) Math.acos(d);
    }

    public static float distance(PVector p1, PVector p2) {
        float dx = p1.x - p2.x;
        float dy = p1.y - p2.y;
        float dz = p1.z - p2.z;
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static float distanceSquared(PVector p0, PVector p1) {
        float dx = p0.x - p1.x;
        float dy = p0.y - p1.y;
        float dz = p0.z - p1.z;
        return (dx * dx + dy * dy + dz * dz);
    }

    public static boolean almost(PVector p0, PVector p1) {
        return Math.abs(p1.x - p0.x) < ALMOST_THRESHOLD && Math.abs(p1.y - p0.y) < ALMOST_THRESHOLD && Math.abs(p1.z - p0.z) < ALMOST_THRESHOLD;
    }

    public static void reflectVelocity(final Particle theParticle,
                                       final PVector theNormal,
                                       float theCoefficientOfRestitution) {
        final PVector myVelocity = theParticle.velocity();
        /* normal */
        TMP_NORMAL.set(theNormal);
        TMP_NORMAL.mult(theNormal.dot(myVelocity));
        /* tangent */
        sub(myVelocity, TMP_NORMAL, TMP_TANGENT);
        /* negate normal */
        TMP_NORMAL.mult(-theCoefficientOfRestitution);
        /* set reflection vector */
        add(TMP_TANGENT, TMP_NORMAL, myVelocity);

        /* also set old position */
        if (Physics.HINT_UPDATE_OLD_POSITION) {
            theParticle.old_position().set(theParticle.position());
        }
    }

    public static void reflect(final PVector theVector,
                               final PVector theNormal,
                               final float theCoefficientOfRestitution) {
        final PVector myNormalComponent = new PVector();
        final PVector myTangentComponent = new PVector();

        /* normal */
        myNormalComponent.set(theNormal);
        myNormalComponent.mult(theNormal.dot(theVector));
        /* tangent */
        sub(theVector, myNormalComponent, myTangentComponent);
        /* negate normal */
        myNormalComponent.mult(-theCoefficientOfRestitution);
        /* set reflection vector */
        add(myTangentComponent, myNormalComponent, theVector);
    }

    public static void reflect(final PVector theVector, final PVector theNormal) {
        /* normal */
        TMP_NORMAL.set(theNormal);
        TMP_NORMAL.mult(theNormal.dot(theVector));
        /* tangent */
        sub(theVector, TMP_NORMAL, TMP_TANGENT);
        /* negate normal */
        TMP_NORMAL.mult(-1.0f);
        /* set reflection vector */
        add(TMP_TANGENT, TMP_NORMAL, theVector);
    }

    public static ArrayList<TriangleDeflector> createTriangleDeflectors(final float[] theVertices,
                                                                        final float theCoefficientOfRestitution) {
        final ArrayList<TriangleDeflector> myDeflectors = new ArrayList<>();
        for (int i = 0; i < theVertices.length / 9; i++) {
            final TriangleDeflector myTriangleDeflector = new TriangleDeflector();
            myTriangleDeflector.a().set(theVertices[i * 9 + 0], theVertices[i * 9 + 1], theVertices[i * 9 + 2]);
            myTriangleDeflector.b().set(theVertices[i * 9 + 3], theVertices[i * 9 + 4], theVertices[i * 9 + 5]);
            myTriangleDeflector.c().set(theVertices[i * 9 + 6], theVertices[i * 9 + 7], theVertices[i * 9 + 8]);
            myTriangleDeflector.coefficientofrestitution(theCoefficientOfRestitution);
            myTriangleDeflector.updateProperties();
            myDeflectors.add(myTriangleDeflector);
        }
        return myDeflectors;
    }

    public static ArrayList<TriangleDeflector> createTriangleDeflectors(final PVector[] theVertices,
                                                                        final float theCoefficientOfRestitution) {
        final ArrayList<TriangleDeflector> myDeflectors = new ArrayList<>();
        for (int i = 0; i < theVertices.length / 3; i++) {
            final TriangleDeflector myTriangleDeflector = new TriangleDeflector();
            myTriangleDeflector.a().set(theVertices[i * 3 + 0]);
            myTriangleDeflector.b().set(theVertices[i * 3 + 1]);
            myTriangleDeflector.c().set(theVertices[i * 3 + 2]);
            myTriangleDeflector.coefficientofrestitution(theCoefficientOfRestitution);
            myTriangleDeflector.updateProperties();
            myDeflectors.add(myTriangleDeflector);
        }
        return myDeflectors;
    }

    public static ArrayList<TriangleDeflector> createTriangleDeflectorsIndexed(final float[] theVertices,
                                                                               final float theCoefficientOfRestitution) {
        final ArrayList<TriangleDeflector> myDeflectors = new ArrayList<>();
        for (int i = 0; i < theVertices.length / 9; i++) {
            final TriangleDeflectorIndexed myTriangleDeflector = new TriangleDeflectorIndexed(theVertices,
                                                                                              i * 9 + 0,
                                                                                              i * 9 + 3,
                                                                                              i * 9 + 6);
            myTriangleDeflector.coefficientofrestitution(theCoefficientOfRestitution);
            myTriangleDeflector.updateProperties();
            myDeflectors.add(myTriangleDeflector);
        }
        return myDeflectors;
    }

    public static PVector cross(PVector p1, PVector p2) {
        final PVector v = new PVector();
        return PVector.cross(p1, p2, v);
    }

    public static void pointAt(final PMatrix3D pResult,
                               final PVector pPosition,
                               final PVector pUpVector, /* should be normalized */
                               final PVector pPointAtPosition) {

        /* forward */
        final PVector mForwardVector = PVector.sub(pPosition, pPointAtPosition);
        mForwardVector.normalize();

        /* side */
        final PVector mSideVector = cross(pUpVector, mForwardVector);
        mSideVector.normalize();

        /* up */
        final PVector mUpVector = cross(mForwardVector, mSideVector);
        mUpVector.normalize();

        if (!isNaN(mSideVector) && !isNaN(mUpVector) && !isNaN(mForwardVector)) {
            /* x */
            pResult.m00 = mSideVector.x;
            pResult.m10 = mSideVector.y;
            pResult.m20 = mSideVector.z;
            /* y */
            pResult.m01 = mUpVector.x;
            pResult.m11 = mUpVector.y;
            pResult.m21 = mUpVector.z;
            /* z */
            pResult.m02 = mForwardVector.x;
            pResult.m12 = mForwardVector.y;
            pResult.m22 = mForwardVector.z;
        }
    }

    public static boolean isNaN(PVector p) {
        return Float.isNaN(p.x) || Float.isNaN(p.y) || Float.isNaN(p.z);
    }

    public static float lengthSquared(PVector p) {
        return p.x * p.x + p.y * p.y + p.z * p.z;
    }

    public static PVector mult(PVector v1, PVector v2) {
        return mult(v1, v2, null);
    }

    public static PVector mult(PVector v1, PVector v2, PVector target) {
        if (target == null) {
            target = new PVector(v1.x * v2.x, v1.y * v2.y, v1.z * v2.z);
        } else {
            target.set(v1.x * v2.x, v1.y * v2.y, v1.z * v2.z);
        }
        return target;
    }

    public static boolean isPointInTriangle(final PVector v0,
                                            final PVector v1,
                                            final PVector v2,
                                            final PVector thePoint) {
        //    // Compute vectors
        //    v0 = C - A
        //    v1 = B - A
        //    v2 = P - A

        PVector v00 = new PVector().set(v2).sub(v0);
        PVector v01 = new PVector().set(v1).sub(v0);
        PVector v02 = new PVector().set(thePoint).sub(v0);

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
}
