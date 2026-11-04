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


package teilchen.test;

import processing.core.PGraphics;
import processing.core.PMatrix3D;
import processing.core.PVector;
import teilchen.BasicParticle;
import teilchen.Physics;
import teilchen.constraint.LineIntersectionConstraint;
import teilchen.cubicle.CubicleParticle;
import teilchen.cubicle.CubicleWorld;
import teilchen.force.Spring;
import teilchen.util.CollisionManager;
import teilchen.util.CubicleWorldView;
import teilchen.util.Intersection;
import teilchen.util.Matrix3f;
import teilchen.util.Plane3f;
import teilchen.util.Ray3f;
import teilchen.util.TransformMatrix4f;
import teilchen.util.Util;
import teilchen.util.Vector3i;

/**
 * headless regression test for intersection math, matrices and the cubicle world. run with
 * <code>java teilchen.test.TestGeometry</code> ( processing core on classpath ). exits with status 1 if any check
 * fails.
 */
public class TestGeometry {

    private static final float HALF_PI = (float) (Math.PI / 2);
    private static final float TOLERANCE = 0.001f;
    private static int mFailures = 0;

    public static void main(String[] args) {
        testSegmentPlane();
        testCalculateNormal();
        testRayPlane();
        testLineLine2DKeepsZ();
        testLineIntersectionConstraint();
        testMatrixTransform();
        testMatrixTransformInverse();
        testWorldIndexInvertsTransform();
        testWorldViewMatchesQuery();
        testCollisionsAcrossCells();
        testFixedParticles();

        System.out.println(mFailures == 0 ? "ALL PASSED" : mFailures + " FAILED");
        System.exit(mFailures == 0 ? 0 : 1);
    }

    private static void check(String pName, boolean pCondition, String pDetail) {
        System.out.println((pCondition ? "PASS " : "FAIL ") + pName + " " + pDetail);
        if (!pCondition) {
            mFailures++;
        }
    }

    private static PVector v(float x, float y, float z) {
        return new PVector(x, y, z);
    }

    private static boolean near(PVector a, float x, float y, float z) {
        return a != null && Math.abs(a.x - x) < TOLERANCE && Math.abs(a.y - y) < TOLERANCE && Math.abs(a.z - z) < TOLERANCE;
    }

    private static void testSegmentPlane() {
        final PVector o = v(0, 0, 0);
        final PVector n = v(0, 1, 0);
        final PVector mCrossing = Intersection.intersect_line_segment_plane(v(2, 1, 0), v(2, -1, 0), o, n);
        check("segment/plane crossing", near(mCrossing, 2, 0, 0), "" + mCrossing);
        final PVector mReverse = Intersection.intersect_line_segment_plane(v(2, -1, 0), v(2, 3, 0), o, n);
        check("segment/plane crossing upward", near(mReverse, 2, 0, 0), "" + mReverse);
        final PVector mAbove = Intersection.intersect_line_segment_plane(v(0, 1, 0), v(0, 3, 0), o, n);
        check("segment/plane not reaching", mAbove == null, "" + mAbove);
    }

    private static void testCalculateNormal() {
        final PVector a = v(10, 0, 0);
        final PVector b = v(0, 10, 0);
        final PVector mNormal = new PVector();
        Util.calculateNormal(a, b, mNormal);
        check("calculateNormal result", near(mNormal, 0, 0, 1), "" + mNormal);
        check("calculateNormal keeps input", near(a, 10, 0, 0) && near(b, 0, 10, 0), a + " " + b);
        final Plane3f mPlane = new Plane3f(v(0, 0, 0), v(10, 0, 0), v(0, 10, 0));
        mPlane.updateNormal();
        check("plane updateNormal", near(mPlane.normal, 0, 0, 1) && near(mPlane.vectorB, 0, 10, 0),
              mPlane.normal + " " + mPlane.vectorB);
    }

    private static boolean hit(Plane3f pPlane, PVector pOrigin, PVector pDirection, boolean pQuad) {
        return Intersection.intersectRayPlane(new Ray3f(pOrigin, pDirection), pPlane, new PVector(), false, pQuad);
    }

    private static void testRayPlane() {
        /* intersectRayPlane expects the normal to be edge A x edge B */
        final Plane3f p = new Plane3f(v(0, 0, 0), v(10, 0, 0), v(0, 10, 0));
        p.normal = v(0, 0, 100);
        check("ray/plane hit from front", hit(p, v(2.5f, 2.5f, 5), v(0, 0, -1), false), "");
        check("ray/plane hit from back", hit(p, v(2.5f, 2.5f, -5), v(0, 0, 1), false), "");
        check("ray/plane parallel", !hit(p, v(2.5f, 2.5f, 5), v(1, 0, 0), false), "");
        final Plane3f mUnit = new Plane3f(v(0, 0, 0), v(1, 0, 0), v(0, 1, 0));
        mUnit.normal = v(0, 0, 1);
        final boolean mFront = hit(mUnit, v(.25f, .25f, 1e-6f), v(0, 0, -1e-6f), false);
        final boolean mBack = hit(mUnit, v(.25f, .25f, -1e-6f), v(0, 0, 1e-6f), false);
        check("ray/plane below epsilon symmetric", mFront == mBack, mFront + " " + mBack);
        check("ray/plane edge x=0", hit(p, v(0, 5, 5), v(0, 0, -1), false), "");
        check("ray/plane edge y=0", hit(p, v(5, 0, 5), v(0, 0, -1), false), "");
        check("ray/plane outside triangle", !hit(p, v(7.5f, 7.5f, 5), v(0, 0, -1), false), "");
        check("ray/plane inside quad", hit(p, v(7.5f, 7.5f, 5), v(0, 0, -1), true), "");
        check("ray/plane outside quad u", !hit(p, v(20, 5, 5), v(0, 0, -1), true), "");
        check("ray/plane outside quad v", !hit(p, v(5, 20, 5), v(0, 0, -1), true), "");
    }

    private static void testLineLine2DKeepsZ() {
        final PVector mResult = v(9, 9, 9);
        final int mState = Intersection.intersect_line_line(v(-1, 0, 0), v(1, 0, 10), v(0, -1, 3), v(0, 1, 3), mResult);
        check("line/line 2D z on segment a", mState == Intersection.INTERSECTING && near(mResult, 0, 0, 5),
              mState + " " + mResult);
    }

    private static PVector constrain(float pOldY, float pPadding) {
        final BasicParticle mA = new BasicParticle();
        final BasicParticle mB = new BasicParticle();
        final BasicParticle mParticle = new BasicParticle();
        mA.position().set(-10, 0, 5);
        mB.position().set(10, 0, 5);
        mParticle.old_position().set(3, pOldY, 5);
        mParticle.position().set(3, -1, 5);
        final LineIntersectionConstraint mConstraint = new LineIntersectionConstraint(mParticle);
        mConstraint.intersecting_lines().add(new Spring(mA, mB));
        mConstraint.intersection_padding(pPadding);
        mConstraint.apply(new Physics());
        return mParticle.position();
    }

    private static void testLineIntersectionConstraint() {
        PVector p = constrain(1, 0);
        check("line constraint no padding keeps z", near(p, 3, 0, 5), "" + p);
        p = constrain(0, 0.5f);
        check("line constraint old position on line", near(p, 3, 0, 5), "" + p);
        p = constrain(1, 0.5f);
        check("line constraint old position 1 away", near(p, 3, 0.5f, 5), "" + p);
        p = constrain(2, 0.5f);
        check("line constraint old position 2 away", near(p, 3, 0.5f, 5), "" + p);
    }

    private static void testMatrixTransform() {
        final Matrix3f m = new Matrix3f(Matrix3f.IDENTITY);
        m.setZRotation(HALF_PI);
        final PVector mX = v(1, 0, 0);
        m.transform(mX);
        check("matrix transform maps x to x axis", near(mX, m.getXAxis().x, m.getXAxis().y, m.getXAxis().z), "" + mX);
        check("matrix z rotation is counter-clockwise", near(mX, 0, 1, 0), "" + mX);

        final TransformMatrix4f t = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
        t.rotation.setZRotation(HALF_PI);
        final PVector mT = v(1, 2, 3);
        final PVector mM = v(1, 2, 3);
        t.transform(mT);
        m.transform(mM);
        check("matrix transform matches TransformMatrix4f", near(mM, mT.x, mT.y, mT.z), mM + " " + mT);

        final Matrix3f a = new Matrix3f(Matrix3f.IDENTITY);
        final Matrix3f b = new Matrix3f(Matrix3f.IDENTITY);
        a.setZRotation(HALF_PI);
        b.setXRotation(HALF_PI);
        final Matrix3f ab = new Matrix3f(a);
        ab.multiply(b);
        final PVector mProduct = v(1, 2, 3);
        ab.transform(mProduct);
        final PVector mSteps = v(1, 2, 3);
        b.transform(mSteps);
        a.transform(mSteps);
        check("(a*b) transform applies b then a", near(mProduct, mSteps.x, mSteps.y, mSteps.z), mProduct + " " + mSteps);
    }

    private static void testMatrixTransformInverse() {
        final Matrix3f m = new Matrix3f();
        m.xx = 2;
        m.xy = -1;
        m.xz = 3;
        m.yx = 0.5f;
        m.yy = 4;
        m.yz = -2;
        m.zx = 1;
        m.zy = 7;
        m.zz = -3;
        final PVector p = v(1, 2, 3);
        m.transform(p);
        m.transformInverse(p);
        check("matrix transformInverse round trip", near(p, 1, 2, 3), "" + p);
        final Matrix3f mInverted = new Matrix3f(m);
        mInverted.invert();
        final PVector q = v(4, 5, 6);
        final PVector r = v(4, 5, 6);
        mInverted.transform(q);
        m.transformInverse(r);
        check("matrix transformInverse matches invert", near(q, r.x, r.y, r.z), q + " " + r);
        final PVector s = v(1, 2, 3);
        new Matrix3f().transformInverse(s);
        check("matrix transformInverse singular", near(s, 1, 2, 3), "" + s);
    }

    private static void testWorldIndexInvertsTransform() {
        final CubicleWorld w = new CubicleWorld(10, 10, 10);
        w.cellscale().set(10, 10, 10);
        w.transform().rotation.setZRotation(HALF_PI / 3);
        w.transform().translation.set(100, 50, 0);
        final PVector p = v(35, 25, 5);
        w.transform().transform(p);
        final Vector3i i = w.worldposition2index(p);
        check("world index inverts rotated transform", i.x == 3 && i.y == 2 && i.z == 0, i.x + "," + i.y + "," + i.z);
    }

    /* records the first transform a view applies. drawing itself is ignored. */
    private static class RecordingGraphics extends PGraphics {
        final PMatrix3D matrix = new PMatrix3D();
        private boolean mRecording = true;

        public void pushMatrix() {
        }

        public void popMatrix() {
        }

        public void translate(float x, float y, float z) {
            if (mRecording) {
                matrix.translate(x, y, z);
            }
        }

        public void applyMatrix(float n00, float n01, float n02, float n03, float n10, float n11, float n12, float n13,
                                float n20, float n21, float n22, float n23, float n30, float n31, float n32, float n33) {
            matrix.apply(n00, n01, n02, n03, n10, n11, n12, n13, n20, n21, n22, n23, n30, n31, n32, n33);
            mRecording = false;
        }

        public void scale(float x, float y, float z) {
        }

        public void stroke(int pColor) {
        }

        public void box(float pSize) {
        }
    }

    private static void testWorldViewMatchesQuery() {
        final CubicleWorld w = new CubicleWorld(10, 10, 10);
        w.cellscale().set(10, 10, 10);
        w.transform().rotation.setZRotation(HALF_PI / 3);
        w.transform().translation.set(100, 50, 0);
        final RecordingGraphics g = new RecordingGraphics();
        new CubicleWorldView(w).draw(g);
        final PVector mDrawn = g.matrix.mult(v(35, 25, 5), null);
        final Vector3i i = w.worldposition2index(mDrawn);
        check("world view matches query", i.x == 3 && i.y == 2 && i.z == 0, i.x + "," + i.y + "," + i.z);
    }

    private static int resolvers(CollisionManager pManager, CubicleWorld pWorld, CubicleParticle... pParticles) {
        for (CubicleParticle p : pParticles) {
            pManager.collision().add(p);
            pWorld.add(p);
        }
        pWorld.update();
        pManager.createCollisionResolvers(pWorld);
        return pManager.collision_forces().size();
    }

    private static CubicleParticle particle(float x, float y, float z) {
        final CubicleParticle p = new CubicleParticle();
        p.position().set(x, y, z);
        return p;
    }

    private static CubicleWorld world(int pCells, float pScale) {
        final CubicleWorld w = new CubicleWorld(pCells, pCells, pCells);
        w.cellscale().set(pScale, pScale, pScale);
        return w;
    }

    private static CollisionManager manager() {
        final CollisionManager m = new CollisionManager();
        m.minimumDistance(20);
        return m;
    }

    private static void testCollisionsAcrossCells() {
        int n = resolvers(manager(), world(4, 100), particle(99, 50, 50), particle(101, 50, 50));
        check("collision across cell border", n == 1, "resolvers=" + n);
        n = resolvers(manager(), world(4, 100), particle(90, 50, 50), particle(95, 50, 50));
        check("collision in one cell", n == 1, "resolvers=" + n);
        n = resolvers(manager(), world(4, 100), particle(10, 50, 50), particle(50, 50, 50));
        check("no collision when too far apart", n == 0, "resolvers=" + n);
        n = resolvers(manager(), world(10, 5), particle(1, 1, 1), particle(18, 1, 1));
        check("collision over several small cells", n == 1, "resolvers=" + n);
        n = resolvers(manager(), world(4, 100), particle(99, 50, 50), particle(101, 50, 50), particle(100, 51, 50));
        check("each pair handled once", n == 3, "resolvers=" + n);

        final CollisionManager mRadius = manager();
        mRadius.distancemode(CollisionManager.DISTANCE_MODE_RADIUS);
        final CubicleParticle a = particle(99, 50, 50);
        final CubicleParticle b = particle(101, 50, 50);
        a.radius(5);
        b.radius(5);
        n = resolvers(mRadius, world(4, 100), a, b);
        check("collision across cell border radius mode", n == 1, "resolvers=" + n);

        n = resolvers(manager(), world(4, 100), particle(-5000, 0, 0), particle(5, 5, 5));
        check("far off-world particle ignored", n == 0, "resolvers=" + n);
        n = resolvers(manager(), world(4, 100), particle(-5, 5, 5), particle(5, 5, 5));
        check("collision across world border", n == 1, "resolvers=" + n);
        n = resolvers(manager(), world(4, 100), particle(-500, 5, 5), particle(-505, 5, 5));
        check("collision off-world", n == 1, "resolvers=" + n);
    }

    private static void testFixedParticles() {
        final CubicleParticle mFixed = particle(55, 50, 50);
        mFixed.fixed(true);
        final int n = resolvers(manager(), world(4, 100), particle(50, 50, 50), mFixed);
        check("collision with fixed particle", n == 1, "resolvers=" + n);
        check("fixed particle in its cell", mFixed.cubicle().x == 0 && mFixed.cubicle().y == 0, "" + mFixed.cubicle().x);

        final CubicleWorld w = world(4, 10);
        final CubicleParticle p = particle(5, 5, 5);
        w.add(p);
        w.update();
        p.fixed(true);
        p.position().set(35, 35, 35);
        w.update();
        check("moved fixed particle changes cell", p.cubicle().x == 3, "" + p.cubicle().x);
    }
}
