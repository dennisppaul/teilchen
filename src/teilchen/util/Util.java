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
    private static final java.util.Random RND_GENERATOR = new java.util.Random();
    /* normal */
    private static final PVector TMP_BA = new PVector();
    private static final PVector TMP_BC = new PVector();
    private static final PVector TMP_MAX = new PVector();
    private static final PVector TMP_MIN = new PVector();
    private static final PVector TMP_NORMAL = new PVector();
    private static final PVector TMP_TANGENT = new PVector();

    public static boolean almost(PVector p0, PVector p1) {
        return Math.abs(p1.x - p0.x) < ALMOST_THRESHOLD && Math.abs(p1.y - p0.y) < ALMOST_THRESHOLD && Math.abs(p1.z - p0.z) < ALMOST_THRESHOLD;
    }

    public static boolean almost(float a, float b) {
        final float mDelta = Math.abs(b - a);
        return mDelta < ALMOST_THRESHOLD;
    }

    public static float angle(PVector p, PVector pVector) {
        float d = p.dot(pVector) / (p.mag() * pVector.mag());
        /*
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

    /**
     * calculate a normal from a set of three vectors.
     *
     * @param pointA        point A
     * @param pointB        point B
     * @param pointC        point C
     * @param pResultNormal normal
     */
    public static void calculateNormal(final PVector pointA,
                                       final PVector pointB,
                                       final PVector pointC,
                                       final PVector pResultNormal) {
        sub(pointB, pointA, TMP_BA);
        sub(pointC, pointB, TMP_BC);
        PVector.cross(TMP_BA, TMP_BC, pResultNormal);
        pResultNormal.normalize();
    }

    /**
     * @param pVectorAB     vector AB
     * @param pVectorBC     vector BC
     * @param pResultNormal normal
     */
    public static void calculateNormal(final PVector pVectorAB, final PVector pVectorBC, final PVector pResultNormal) {
        pResultNormal.cross(pVectorAB, pVectorBC);
        pResultNormal.normalize();
    }

    public static PVector calculateReflectionVector(Particle pParticle, PVector pNormal) {
        PVector mTempNormalComponent = new PVector();
        /* normal */
        mTempNormalComponent.set(pNormal);
        mTempNormalComponent.mult(pNormal.dot(pParticle.velocity()));
        /* tangent */
        PVector mTempTangentComponent = PVector.sub(pParticle.velocity(), mTempNormalComponent);
        /* negate normal */
        mTempNormalComponent.mult(-1.0f);
        /* set reflection vector */
        return PVector.add(mTempTangentComponent, mTempNormalComponent);
    }


    public static PVector clone(PVector p) {
        PVector v = new PVector();
        v.set(p);
        return v;
    }

    /* contain */
    public static boolean contains(final PVector pPosition, final WorldAxisAlignedBoundingBox pWorldAlignedBox) {
        return (contains(pPosition.x, pWorldAlignedBox.position.x, pWorldAlignedBox.scale.x) && contains(pPosition.y,
                                                                                                         pWorldAlignedBox.position.y,
                                                                                                         pWorldAlignedBox.scale.y) && contains(
                pPosition.z,
                pWorldAlignedBox.position.z,
                pWorldAlignedBox.scale.z));
    }

    public static boolean contains(final float pTestValue, final float pContainerValue, final float pRange) {
        return (pTestValue > pContainerValue - pRange * 0.5f && pTestValue < pContainerValue + pRange * 0.5f);
    }

    public static TriangleDeflector createTriangleDeflector2D(float x1,
                                                              float y1,
                                                              float x2,
                                                              float y2,
                                                              float mCoefficientOfRestitution) {
        final float mZOffset = 1.0f;
        final TriangleDeflector mTriangleDeflector = new TriangleDeflector();
        mTriangleDeflector.a().set(new PVector(x1, y1, 0));
        mTriangleDeflector.b().set(new PVector(x2, y2, 0));
        mTriangleDeflector.c().set(new PVector(x2, y2, mZOffset));
        mTriangleDeflector.coefficientofrestitution(mCoefficientOfRestitution);
        mTriangleDeflector.updateProperties();
        return mTriangleDeflector;
    }

    public static ArrayList<TriangleDeflector> createTriangleDeflectors(final float[] pVertices,
                                                                        final float pCoefficientOfRestitution) {
        final ArrayList<TriangleDeflector> mDeflectors = new ArrayList<>();
        for (int i = 0; i < pVertices.length / 9; i++) {
            final TriangleDeflector mTriangleDeflector = new TriangleDeflector();
            mTriangleDeflector.a().set(pVertices[i * 9 + 0], pVertices[i * 9 + 1], pVertices[i * 9 + 2]);
            mTriangleDeflector.b().set(pVertices[i * 9 + 3], pVertices[i * 9 + 4], pVertices[i * 9 + 5]);
            mTriangleDeflector.c().set(pVertices[i * 9 + 6], pVertices[i * 9 + 7], pVertices[i * 9 + 8]);
            mTriangleDeflector.coefficientofrestitution(pCoefficientOfRestitution);
            mTriangleDeflector.updateProperties();
            mDeflectors.add(mTriangleDeflector);
        }
        return mDeflectors;
    }

    public static ArrayList<TriangleDeflector> createTriangleDeflectors(final PVector[] pVertices,
                                                                        final float pCoefficientOfRestitution) {
        final ArrayList<TriangleDeflector> mDeflectors = new ArrayList<>();
        for (int i = 0; i < pVertices.length / 3; i++) {
            final TriangleDeflector mTriangleDeflector = new TriangleDeflector();
            mTriangleDeflector.a().set(pVertices[i * 3 + 0]);
            mTriangleDeflector.b().set(pVertices[i * 3 + 1]);
            mTriangleDeflector.c().set(pVertices[i * 3 + 2]);
            mTriangleDeflector.coefficientofrestitution(pCoefficientOfRestitution);
            mTriangleDeflector.updateProperties();
            mDeflectors.add(mTriangleDeflector);
        }
        return mDeflectors;
    }

    public static ArrayList<TriangleDeflector> createTriangleDeflectorsIndexed(final float[] pVertices,
                                                                               final float pCoefficientOfRestitution) {
        final ArrayList<TriangleDeflector> mDeflectors = new ArrayList<>();
        for (int i = 0; i < pVertices.length / 9; i++) {
            final TriangleDeflectorIndexed mTriangleDeflector = new TriangleDeflectorIndexed(pVertices,
                                                                                             i * 9 + 0,
                                                                                             i * 9 + 3,
                                                                                             i * 9 + 6);
            mTriangleDeflector.coefficientofrestitution(pCoefficientOfRestitution);
            mTriangleDeflector.updateProperties();
            mDeflectors.add(mTriangleDeflector);
        }
        return mDeflectors;
    }

    public static PVector cross(PVector p1, PVector p2) {
        final PVector v = new PVector();
        return PVector.cross(p1, p2, v);
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

    public static void divide(final PVector p, final PVector pVector) {
        p.x /= pVector.x;
        p.y /= pVector.y;
        p.z /= pVector.z;
    }

    public static float fastInverseSqrt(float v) {
        final float half = 0.5f * v;
        int i = Float.floatToIntBits(v);
        i = 0x5f375a86 - (i >> 1);
        v = Float.intBitsToFloat(i);
        return v * (1.5f - half * v * v);
    }

    public static Particle findParticleByProximity(Physics pPhysics,
                                                   float x,
                                                   float y,
                                                   float z,
                                                   float pSelectionRadius) {
        return findParticleByProximity(pPhysics.particles(), new PVector().set(x, y, z), pSelectionRadius);
    }

    public static Particle findParticleByProximity(Physics pPhysics, PVector pPosition, float pSelectionRadius) {
        return findParticleByProximity(pPhysics.particles(), pPosition, pSelectionRadius);
    }

    public static Particle findParticleByProximity(ArrayList<Particle> pParticles,
                                                   float x,
                                                   float y,
                                                   float z,
                                                   float pSelectionRadius) {
        return findParticleByProximity(pParticles, new PVector().set(x, y, z), pSelectionRadius);
    }

    public static Particle findParticleByProximity(ArrayList<Particle> pParticles,
                                                   PVector pPosition,
                                                   float pSelectionRadius) {
        final ArrayList<Particle> mCloseParticles = new ArrayList<>();
        for (Particle p : pParticles) {
            if (PVector.dist(pPosition, p.position()) < pSelectionRadius) {
                mCloseParticles.add(p);
            }
        }
        if (mCloseParticles.isEmpty()) {
            return null;
        }
        Particle mClosestParticle = mCloseParticles.get(0);
        float mClosestDistance = PVector.dist(pPosition, mClosestParticle.position());
        for (int i = 1; i < mCloseParticles.size(); i++) {
            final float mDistance = PVector.dist(pPosition, mCloseParticles.get(i).position());
            if (mDistance < mClosestDistance) {
                mClosestDistance = mDistance;
                mClosestParticle = mCloseParticles.get(i);
            }
        }
        return mClosestParticle;
    }

    public static boolean inside2DPolygon(final PVector pPoint, final ArrayList<PVector> pPolygon) {
        float x = pPoint.x;
        float y = pPoint.y;

        int c = 0;
        for (int i = 0, j = pPolygon.size() - 1; i < pPolygon.size(); j = i++) {
            if ((((pPolygon.get(i).y <= y) && (y < pPolygon.get(j).y)) || ((pPolygon.get(j).y <= y) && (y < pPolygon.get(
                    i).y))) && (x < (pPolygon.get(j).x - pPolygon.get(i).x) * (y - pPolygon.get(i).y) / (pPolygon.get(j).y - pPolygon.get(
                    i).y) + pPolygon.get(i).x)) {
                c = (c + 1) % 2;
            }
        }
        return c == 1;
    }

    public static boolean insidePolygon(PVector pPoint, PVector[] pPolygon) {
        float x = pPoint.x;
        float y = pPoint.y;

        int c = 0;
        for (int i = 0, j = pPolygon.length - 1; i < pPolygon.length; j = i++) {
            if ((((pPolygon[i].y <= y) && (y < pPolygon[j].y)) || ((pPolygon[j].y <= y) && (y < pPolygon[i].y))) && (x < (pPolygon[j].x - pPolygon[i].x) * (y - pPolygon[i].y) / (pPolygon[j].y - pPolygon[i].y) + pPolygon[i].x)) {
                c = (c + 1) % 2;
            }
        }
        return c == 1;
    }

    public static boolean insidePolygon(final PVector pPoint, final ArrayList<PVector> pPolygon) {
        float x = pPoint.x;
        float y = pPoint.y;

        int c = 0;
        for (int i = 0, j = pPolygon.size() - 1; i < pPolygon.size(); j = i++) {
            if ((((pPolygon.get(i).y <= y) && (y < pPolygon.get(j).y)) || ((pPolygon.get(j).y <= y) && (y < pPolygon.get(
                    i).y))) && (x < (pPolygon.get(j).x - pPolygon.get(i).x) * (y - pPolygon.get(i).y) / (pPolygon.get(j).y - pPolygon.get(
                    i).y) + pPolygon.get(i).x)) {
                c = (c + 1) % 2;
            }
        }
        return c == 1;
    }

    public static boolean isNaN(PVector p) {
        return Float.isNaN(p.x) || Float.isNaN(p.y) || Float.isNaN(p.z);
    }

    public static boolean isPointInTriangle(final PVector v0,
                                            final PVector v1,
                                            final PVector v2,
                                            final PVector pPoint) {
        //    // Compute vectors
        //    v0 = C - A
        //    v1 = B - A
        //    v2 = P - A

        PVector v00 = new PVector().set(v2).sub(v0);
        PVector v01 = new PVector().set(v1).sub(v0);
        PVector v02 = new PVector().set(pPoint).sub(v0);

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

    /**
     * @param v line segment start
     * @param w line segment end
     * @param p point
     * @return resulting point on line segment
     */
    public static PVector projectPointOnLineSegment(PVector v, PVector w, PVector p) {
        // Return minimum distance between line segment vw and point p
        final float l2 = PVector.sub(v, w).magSq();  // i.e. |w-v|^2 -  avoid a sqrt
        if (l2 == 0.0) {
            return p.copy();
            //            return distance(p, v);   // v == w case
        }
        // Consider the line extending the segment, parameterized as v + t (w - v).
        // We find projection of point p onto the line.
        // It falls where t = [(p-v) . (w-v)] / |w-v|^2
        // We clamp t from [0,1] to handle points outside the segment vw.
        final float t = Math.max(0, Math.min(1, PVector.dot(PVector.sub(p, v), PVector.sub(w, v)) / l2));
        return PVector.add(v, PVector.mult(PVector.sub(w, v), t));
        //        return distance(p, projection);
    }

    /**
     * Sets a position randomly distributed inside a sphere of unit radius centered at the origin. Orientation will be
     * random and length will range between 0 and 1
     *
     * @param p randomized vector
     */
    public static void randomize(PVector p) {
        p.x = RND_GENERATOR.nextFloat() * 2.0F - 1.0F;
        p.y = RND_GENERATOR.nextFloat() * 2.0F - 1.0F;
        p.z = RND_GENERATOR.nextFloat() * 2.0F - 1.0F;
        p.normalize();
    }

    public static void randomize2D(PVector p) {
        p.x = RND_GENERATOR.nextFloat() * 2.0F - 1.0F;
        p.y = RND_GENERATOR.nextFloat() * 2.0F - 1.0F;
        p.z = 0.0f;
        p.normalize();
    }

    public static void reflect(final PVector direction, final PVector normal, final float coefficient_of_restitution) {
        final PVector mNormalComponent = new PVector();
        final PVector mTangentComponent = new PVector();

        /* normal */
        mNormalComponent.set(normal);
        mNormalComponent.mult(normal.dot(direction));
        /* tangent */
        sub(direction, mNormalComponent, mTangentComponent);
        /* negate normal */
        mNormalComponent.mult(-coefficient_of_restitution);
        /* set reflection vector */
        add(mTangentComponent, mNormalComponent, direction);
    }

    /**
     * @param direction        direction vector to be reflected
     * @param normal           normal to reflect at
     * @param normalize_normal if true, normal will be normalized
     * @return reflected vector
     */
    public static PVector reflect(PVector direction, PVector normal, boolean normalize_normal) {
        // r = e - 2 (e.n) n :: ( | n | = 1 )
        // with e :: direction
        //      r :: reflection
        //      n :: normal

        final PVector n = new PVector().set(normal);
        if (normalize_normal) {
            n.normalize();
        }
        final PVector e = new PVector().set(direction);
        final float d = PVector.dot(e, n); // d > 0 = frontface, d < 0 = backface
        n.mult(2 * d);
        final PVector r = PVector.sub(e, n);
        return r;
    }

    public static void reflectVelocity(final Particle pParticle,
                                       final PVector pNormal,
                                       float pCoefficientOfRestitution) {
        final PVector mVelocity = pParticle.velocity();
        /* normal */
        TMP_NORMAL.set(pNormal);
        TMP_NORMAL.mult(pNormal.dot(mVelocity));
        /* tangent */
        sub(mVelocity, TMP_NORMAL, TMP_TANGENT);
        /* negate normal */
        TMP_NORMAL.mult(-pCoefficientOfRestitution);
        /* set reflection vector */
        add(TMP_TANGENT, TMP_NORMAL, mVelocity);

        /* also set old position */
        if (Physics.HINT_UPDATE_OLD_POSITION) {
            pParticle.old_position().set(pParticle.position());
        }
    }

    /*
     * Rotate a point p by angle theta around an arbitrary line segment p1-p2
     * Return the rotated point.
     * Positive angles are anticlockwise looking down the axis towards the origin.
     * Assume right hand coordinate system.
     */
    public static PVector rotatePoint(PVector p, double pta, PVector p1, PVector p2) {
        PVector r = new PVector();
        PVector q = new PVector();
        PVector mP = new PVector();
        double cospta, sinpta;

        mP.set(p);

        r.x = p2.x - p1.x;
        r.y = p2.y - p1.y;
        r.z = p2.z - p1.z;
        mP.x -= p1.x;
        mP.y -= p1.y;
        mP.z -= p1.z;

        r.normalize();

        cospta = Math.cos(pta);
        sinpta = Math.sin(pta);

        q.x += (cospta + (1 - cospta) * r.x * r.x) * mP.x;
        q.x += ((1 - cospta) * r.x * r.y - r.z * sinpta) * mP.y;
        q.x += ((1 - cospta) * r.x * r.z + r.y * sinpta) * mP.z;

        q.y += ((1 - cospta) * r.x * r.y + r.z * sinpta) * mP.x;
        q.y += (cospta + (1 - cospta) * r.y * r.y) * mP.y;
        q.y += ((1 - cospta) * r.y * r.z - r.x * sinpta) * mP.z;

        q.z += ((1 - cospta) * r.x * r.z - r.y * sinpta) * mP.x;
        q.z += ((1 - cospta) * r.y * r.z + r.x * sinpta) * mP.y;
        q.z += (cospta + (1 - cospta) * r.z * r.z) * mP.z;

        q.x += p1.x;
        q.y += p1.y;
        q.z += p1.z;

        return q;
    }

    public static void satisfyNeighborConstraints(final ArrayList<Particle> pParticles, final float pRelaxedness) {
        for (int i = 0; i < pParticles.size(); i++) {
            final Particle p1 = pParticles.get(i);
            for (int j = i + 1; j < pParticles.size(); j++) {
                final Particle p2 = pParticles.get(j);
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
                final float mDistance = distance(p1.position(), p2.position());
                /* skip bad values */
                if (mDistance == 0.0f || Float.isNaN(mDistance)) {
                    continue;
                }
                final float mDesiredDistance = p1.radius() + p2.radius();
                if (mDistance < mDesiredDistance) {
                    final PVector mDiff = PVector.sub(p1.position(), p2.position());
                    mDiff.mult(1.0f / mDistance);
                    mDiff.mult(mDesiredDistance - mDistance);
                    mDiff.mult(0.5f);
                    mDiff.mult(pRelaxedness);
                    p1.position().add(mDiff);
                    p2.position().sub(mDiff);
                }
            }
        }
    }

    public static void scale(PVector v1, PVector v2) {
        v1.x *= v2.x;
        v1.y *= v2.y;
        v1.z *= v2.z;
    }

    public static void setVelocityAndOldPosition(Particle pParticle, PVector pNewVelocity) {
        pParticle.velocity().set(pNewVelocity);
        final PVector mOldPosition = sub(pParticle.position(), pParticle.velocity());
        pParticle.old_position().set(mOldPosition);
    }

    public static void updateBoundingBox(final WorldAxisAlignedBoundingBox pWorldAxisAlignedBoundingBox,
                                         final PVector[] mVectors) {

        if (mVectors == null || mVectors.length == 0) {
            return;
        }

        /* get minimum and maximum */
        TMP_MIN.set(mVectors[0]);
        TMP_MAX.set(mVectors[0]);

        for (int i = 1; i < mVectors.length; i++) {
            /* minimum */
            if (TMP_MIN.x > mVectors[i].x) {
                TMP_MIN.x = mVectors[i].x;
            }
            if (TMP_MIN.y > mVectors[i].y) {
                TMP_MIN.y = mVectors[i].y;
            }
            if (TMP_MIN.z > mVectors[i].z) {
                TMP_MIN.z = mVectors[i].z;
            }
            /* maximum */
            if (TMP_MAX.x < mVectors[i].x) {
                TMP_MAX.x = mVectors[i].x;
            }
            if (TMP_MAX.y < mVectors[i].y) {
                TMP_MAX.y = mVectors[i].y;
            }
            if (TMP_MAX.z < mVectors[i].z) {
                TMP_MAX.z = mVectors[i].z;
            }
        }

        /* create world aligned boundingbox */
        /* bb position */
        sub(TMP_MAX, TMP_MIN, pWorldAxisAlignedBoundingBox.position);
        pWorldAxisAlignedBoundingBox.position.mult(0.5f);
        pWorldAxisAlignedBoundingBox.position.add(TMP_MIN);
        /* bb scale */
        sub(TMP_MAX, TMP_MIN, pWorldAxisAlignedBoundingBox.scale);
        pWorldAxisAlignedBoundingBox.scale.x = Math.abs(pWorldAxisAlignedBoundingBox.scale.x);
        pWorldAxisAlignedBoundingBox.scale.y = Math.abs(pWorldAxisAlignedBoundingBox.scale.y);
        pWorldAxisAlignedBoundingBox.scale.z = Math.abs(pWorldAxisAlignedBoundingBox.scale.z);
    }

    /* --- NEW --- */

    public static float circumcenter_triangle(PVector a, PVector b, PVector c, PVector result) {
        // from https://gamedev.stackexchange.com/questions/60630/how-do-i-find-the-circumcenter-of-a-triangle-in-3d
        final PVector ac = PVector.sub(c, a);
        final PVector ab = PVector.sub(b, a);
        final PVector abXac = ab.cross(ac);

        final PVector p0 = PVector.mult(abXac.cross(ab), ac.magSq());
        final PVector p1 = PVector.mult(ac.cross(abXac), ab.magSq());
        final PVector p2 = PVector.add(p0, p1);
        final PVector toCircumsphereCenter = PVector.mult(p2, 1.0f / (2.0f * abXac.magSq()));
        final float circumsphereRadius = toCircumsphereCenter.mag();

        final PVector ccs = PVector.add(a, toCircumsphereCenter); // ccs
        result.set(ccs);

        return circumsphereRadius;
    }

    public static boolean point_in_triangle(PVector a, PVector b, PVector c, PVector point) {
        // compute the barycentric coordinates of the point with respect to the triangle
        PVector v0 = PVector.sub(c, a);
        PVector v1 = PVector.sub(b, a);
        PVector v2 = PVector.sub(point, a);
        double d00 = v0.dot(v0);
        double d01 = v0.dot(v1);
        double d02 = v0.dot(v2);
        double d11 = v1.dot(v1);
        double d12 = v1.dot(v2);
        double denom = d00 * d11 - d01 * d01;
        double u = (d11 * d02 - d01 * d12) / denom;
        double v = (d00 * d12 - d01 * d02) / denom;
        return u >= 0 && v >= 0 && u + v <= 1;
    }

    public static float distance_point_plane(PVector point, PVector plane_origin, PVector plane_normal) {
        final PVector d = sub(plane_origin, point);
        final float dot = plane_normal.dot(d);
        final float magnitude = plane_normal.mag();
        return dot / magnitude;
    }

    public static PVector project_vector_onto_plane(PVector vector, PVector plane_normal) {
        float dot = vector.x * plane_normal.x + vector.y * plane_normal.y + vector.z * plane_normal.z;
        float x = vector.x - dot * plane_normal.x;
        float y = vector.y - dot * plane_normal.y;
        float z = vector.z - dot * plane_normal.z;
        return new PVector(x, y, z);
    }

    public static PVector project_point_onto_plane(PVector point, PVector plane_origin, PVector plane_normal) {
        PVector v = PVector.sub(point, plane_origin);
        float dot = plane_normal.dot(v);
        float magnitude = plane_normal.mag();
        PVector projection = new PVector((v.x - dot * plane_normal.x) / magnitude,
                                         (v.y - dot * plane_normal.y) / magnitude,
                                         (v.z - dot * plane_normal.z) / magnitude);
        return new PVector(plane_origin.x + projection.x, plane_origin.y + projection.y, plane_origin.z + projection.z);
    }

    public static PVector project_point_onto_line(PVector point, PVector line_point_a, PVector line_point_b) {
        PVector lineDirection = PVector.sub(line_point_b, line_point_a);
        lineDirection.normalize();
        float projection = PVector.dot(lineDirection, PVector.sub(point, line_point_a));
        return PVector.add(line_point_a, PVector.mult(lineDirection, projection));
    }

    public static PVector project_point_onto_line_segment(PVector point, PVector p1, PVector p2) {
        final PVector lineDirection = PVector.sub(p2, p1);
        float lineLength = lineDirection.mag();
        lineDirection.normalize();

        float projection = PVector.dot(lineDirection, PVector.sub(point, p1));

        if (projection < 0) {
            return p1;
        } else if (projection > lineLength) {
            return p2;
        } else {
            PVector projectedPoint = PVector.add(p1, PVector.mult(lineDirection, projection));
            return projectedPoint;
        }
    }

    public static boolean is_parallel(PVector vector, PVector normal) {
        return PVector.dot(vector, normal) == 0;
    }
}
