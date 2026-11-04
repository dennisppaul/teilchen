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

import processing.core.PVector;
import teilchen.BasicParticle;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.constraint.Collider;
import teilchen.constraint.LineCollider2D;
import teilchen.constraint.PlaneCollider;
import teilchen.constraint.SphereCollider;
import teilchen.constraint.TriangleCollider;
import teilchen.constraint.TriangleMeshCollider;
import teilchen.force.Gravity;
import teilchen.force.TriangleDeflector;
import teilchen.integration.Euler;
import teilchen.integration.Integrator;
import teilchen.integration.Midpoint;
import teilchen.integration.RungeKutta;
import teilchen.integration.Verlet;

import java.util.ArrayList;
import java.util.Random;

/**
 * headless regression test for colliders. run with <code>java teilchen.test.TestColliders</code> ( processing core on
 * classpath ). exits with status 1 if any check fails.
 */
@SuppressWarnings("deprecation")
public class TestColliders {

    private static final float W = 640;
    private static final float H = 480;
    private static int mFailures = 0;

    public static void main(String[] args) {
        for (Integrator i : new Integrator[]{new Euler(), new Midpoint(), new RungeKutta(), new Verlet()}) {
            testBouncingOnFloor(i, false);
            testBouncingOnFloor(i, true);
        }
        testFastParticleDoesNotTunnel();
        testSeamBetweenTriangles();
        testRestitutionZeroSlides();
        testRadiusIsRespected();
        testSphere();
        testLine2D();
        testPlaneIsOneSided();
        testDeflectorWrapper();
        testMeshMatchesTriangles();
        testMeshTerrain();
        testSteepTerrain();
        testMeshPerformance();

        System.out.println(mFailures == 0 ? "ALL PASSED" : mFailures + " FAILED");
        System.exit(mFailures == 0 ? 0 : 1);
    }

    private static void check(String pName, boolean pCondition, String pDetail) {
        System.out.println((pCondition ? "PASS " : "FAIL ") + pName + " " + pDetail);
        if (!pCondition) {
            mFailures++;
        }
    }

    private static float[] floor() {
        return new float[]{0, 0, 0, W, H, 0, 0, H, 0, 0, 0, 0, W, 0, 0, W, H, 0};
    }

    private static Physics physics(Integrator pIntegrator) {
        final Physics mPhysics = new Physics();
        mPhysics.setIntegratorRef(pIntegrator);
        return mPhysics;
    }

    private static BasicParticle particle(Physics pPhysics, float x, float y, float z, float vx, float vy, float vz,
                                          float dt) {
        final BasicParticle p = pPhysics.makeParticle(x, y, z);
        p.velocity().set(vx, vy, vz);
        p.old_position().set(x - vx * dt, y - vy * dt, z - vz * dt); /* for verlet */
        return p;
    }

    /* scenario of `SketchLessonX09_TriangleDeflector` */
    private static void testBouncingOnFloor(Integrator pIntegrator, boolean pMesh) {
        final Physics mPhysics = physics(pIntegrator);
        mPhysics.add(new Gravity(0, 0, -30));
        if (pMesh) {
            mPhysics.add(new TriangleMeshCollider(floor()));
        } else {
            for (TriangleCollider c : teilchen.util.Util.createTriangleColliders(floor(), 1.0f)) {
                mPhysics.add(c);
            }
        }
        final float dt = 1 / 60f;
        final Random r = new Random(1);
        final ArrayList<Particle> mParticles = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            mParticles.add(particle(mPhysics, 20 + r.nextFloat() * (W - 40), 20 + r.nextFloat() * (H - 40), H / 2f,
                                    r.nextFloat() * 10 - 5, r.nextFloat() * 10 - 5, r.nextFloat() * 20, dt));
        }
        for (int f = 0; f < 600; f++) {
            mPhysics.step(dt);
        }
        int mTunneled = 0;
        float mMaxZ = 0;
        for (Particle p : mParticles) {
            final boolean mInside = p.position().x > 0 && p.position().x < W && p.position().y > 0 && p.position().y < H;
            if (mInside && p.position().z < 0) {
                mTunneled++;
            }
            mMaxZ = Math.max(mMaxZ, p.position().z);
        }
        check("floor/" + pIntegrator.getClass().getSimpleName() + (pMesh ? "/mesh" : "/triangles"),
              mTunneled == 0 && mMaxZ > 200,
              String.format("tunneled=%d maxZ=%.1f ( start %.0f )", mTunneled, mMaxZ, H / 2f));
    }

    private static void testFastParticleDoesNotTunnel() {
        final Physics mPhysics = physics(new Euler());
        mPhysics.add(new TriangleMeshCollider(floor()));
        final float dt = 1 / 60f;
        final BasicParticle p = particle(mPhysics, 100, 100, 50, 0, 0, -100000, dt);
        mPhysics.step(dt);
        check("fast particle no tunneling", p.position().z > 0 && p.velocity().z > 0,
              "z=" + p.position().z + " vz=" + p.velocity().z);
    }

    private static void testSeamBetweenTriangles() {
        /* hit exactly on shared diagonal ( 0,0 ) - ( W,H ) */
        final Physics mPhysics = physics(new Euler());
        for (TriangleCollider c : teilchen.util.Util.createTriangleColliders(floor(), 1.0f)) {
            mPhysics.add(c);
        }
        final float dt = 1 / 60f;
        int mTunneled = 0;
        for (int i = 1; i < 100; i++) {
            final float t = i / 100f;
            final BasicParticle p = particle(mPhysics, W * t, H * t, 1, 0, 0, -120, dt);
            mPhysics.step(dt);
            if (p.position().z < 0) {
                mTunneled++;
            }
        }
        check("seam between triangles", mTunneled == 0, "tunneled=" + mTunneled);
    }

    private static void testRestitutionZeroSlides() {
        final Physics mPhysics = physics(new Midpoint());
        mPhysics.add(new Gravity(0, 0, -30));
        final TriangleMeshCollider c = new TriangleMeshCollider(floor());
        c.coefficientofrestitution(0);
        mPhysics.add(c);
        final float dt = 1 / 60f;
        final BasicParticle p = particle(mPhysics, 100, 100, 5, 20, 0, 0, dt);
        for (int f = 0; f < 60; f++) {
            mPhysics.step(dt);
        }
        check("restitution 0 slides on surface",
              Math.abs(p.position().z) < 0.01f && Math.abs(p.velocity().x - 20) < 0.01f &&
              Math.abs(p.velocity().z) < 1.0f,
              "pos=" + p.position() + " vel=" + p.velocity());
    }

    private static void testRadiusIsRespected() {
        final Physics mPhysics = physics(new Midpoint());
        mPhysics.add(new Gravity(0, 0, -30));
        final TriangleMeshCollider c = new TriangleMeshCollider(floor());
        c.coefficientofrestitution(0);
        mPhysics.add(c);
        final BasicParticle p = particle(mPhysics, 100, 100, 50, 0, 0, 0, 1 / 60f);
        p.radius(10);
        for (int f = 0; f < 300; f++) {
            mPhysics.step(1 / 60f);
        }
        check("particle radius respected", Math.abs(p.position().z - 10) < 0.05f, "z=" + p.position().z);
    }

    private static void testSphere() {
        final Physics mPhysics = physics(new Euler());
        final SphereCollider s = new SphereCollider(new PVector(0, 0, 0), 10);
        mPhysics.add(s);
        final float dt = 1 / 60f;
        /* fast particle passing through center within one step */
        final BasicParticle p = particle(mPhysics, -20, 0, 0, 3000, 0, 0, dt);
        mPhysics.step(dt);
        final boolean mFast = p.position().x < -10 && p.velocity().x < 0;
        /* slow particle with radius approaching */
        final BasicParticle q = particle(mPhysics, 0, 20, 0, 0, -60, 0, dt);
        q.radius(5);
        for (int f = 0; f < 60; f++) {
            mPhysics.step(dt);
        }
        final float mDistance = q.position().mag();
        check("sphere collider", mFast && mDistance >= 15 - 0.01f,
              "fast x=" + p.position().x + " slow distance=" + mDistance);
    }

    private static void testLine2D() {
        final Physics mPhysics = physics(new Euler());
        final LineCollider2D l = new LineCollider2D(new PVector(0, 0), new PVector(100, 0));
        mPhysics.add(l);
        final float dt = 1 / 60f;
        /* radius 0 particle crossing line fast ( did not collide with `LineDeflector2D` ) */
        final BasicParticle p = particle(mPhysics, 50, 10, 0, 0, -6000, 0, dt);
        mPhysics.step(dt);
        final boolean mCrossing = p.position().y > 0 && p.velocity().y > 0;
        /* particle with radius next to end cap */
        final BasicParticle q = particle(mPhysics, 105, 2, 0, 0, 0, 0, dt);
        q.radius(10);
        mPhysics.step(dt);
        final float mDistanceToCap = PVector.dist(q.position(), new PVector(100, 0));
        /* particle outside segment passes */
        final BasicParticle o = particle(mPhysics, 150, 10, 0, 0, -600, 0, dt);
        mPhysics.step(dt);
        check("line collider 2D", mCrossing && mDistanceToCap >= 10 - 0.01f && o.position().y < 0,
              "crossing y=" + p.position().y + " cap distance=" + mDistanceToCap + " outside y=" + o.position().y);
    }

    private static void testPlaneIsOneSided() {
        final Physics mPhysics = physics(new Euler());
        final PlaneCollider c = new PlaneCollider(new PVector(0, 0, 0), new PVector(0, 1, 0));
        mPhysics.add(c);
        final BasicParticle p = particle(mPhysics, 0, -50, 0, 0, 0, 0, 1 / 60f);
        mPhysics.step(1 / 60f);
        check("plane pushes particle to front side", p.position().y >= 0, "y=" + p.position().y);
    }

    private static void testDeflectorWrapper() {
        /* deprecated deflectors keep working ( as forces ) with any integrator */
        final Physics mPhysics = physics(new Midpoint());
        mPhysics.add(new Gravity(0, 0, -30));
        final ArrayList<TriangleDeflector> mDeflectors = teilchen.util.Util.createTriangleDeflectors(floor(), 1);
        mPhysics.addForces(mDeflectors);
        final float dt = 1 / 60f;
        final BasicParticle p = particle(mPhysics, 100, 100, 100, 0, 0, 0, dt);
        boolean mHit = false;
        for (int f = 0; f < 300; f++) {
            mPhysics.step(dt);
            mHit |= mDeflectors.get(0).hit() || mDeflectors.get(1).hit();
        }
        final boolean mBounces = p.position().z > 0 && mHit;
        final int mConstraints = mPhysics.constraints().size();

        /* removing a deflector removes its collider */
        mPhysics.remove(mDeflectors.get(0));
        mPhysics.remove(mDeflectors.get(1));
        mPhysics.step(dt);
        mPhysics.step(dt);
        final int mConstraintsAfterRemove = mPhysics.constraints().size();
        check("deflector wrapper", mBounces && mConstraints == 2 && mConstraintsAfterRemove == 0,
              "z=" + p.position().z + " hit=" + mHit + " constraints " + mConstraints + " -> " +
              mConstraintsAfterRemove);
    }

    private static float[] mesh(int G) {
        return mesh(G, 20);
    }

    private static float[] mesh(int G, float pHeight) {
        final float s = W / G;
        final float[] v = new float[G * G * 18];
        int k = 0;
        final Random r = new Random(3);
        final float[][] z = new float[G + 1][G + 1];
        for (int i = 0; i <= G; i++) {
            for (int j = 0; j <= G; j++) {
                z[i][j] = r.nextFloat() * pHeight;
            }
        }
        for (int i = 0; i < G; i++) {
            for (int j = 0; j < G; j++) {
                final float[] q = {i * s, j * s, z[i][j], (i + 1) * s, j * s, z[i + 1][j],
                                   (i + 1) * s, (j + 1) * s, z[i + 1][j + 1], i * s, (j + 1) * s, z[i][j + 1]};
                final int[] o = {0, 1, 2, 0, 2, 3};
                for (int t : o) {
                    v[k++] = q[t * 3];
                    v[k++] = q[t * 3 + 1];
                    v[k++] = q[t * 3 + 2];
                }
            }
        }
        return v;
    }

    private static Physics meshScene(boolean pMesh, float[] pVertices) {
        final Physics mPhysics = physics(new Euler());
        mPhysics.add(new Gravity(0, 0, -30));
        if (pMesh) {
            mPhysics.add(new TriangleMeshCollider(pVertices));
        } else {
            for (Collider c : teilchen.util.Util.createTriangleColliders(pVertices, 1.0f)) {
                mPhysics.add(c);
            }
        }
        final Random r = new Random(2);
        for (int i = 0; i < 1000; i++) {
            particle(mPhysics, r.nextFloat() * W, r.nextFloat() * W, 100 + r.nextFloat() * 100,
                     r.nextFloat() * 20 - 10, r.nextFloat() * 20 - 10, 0, 1 / 60f);
        }
        return mPhysics;
    }

    private static void testMeshMatchesTriangles() {
        /* flat mesh of many triangles: no concave corners, so both must give identical results */
        final float[] v = mesh(10, 0);
        final Physics a = meshScene(false, v);
        final Physics b = meshScene(true, v);
        float mMaxDifference = 0;
        for (int f = 0; f < 200; f++) {
            a.step(1 / 60f);
            b.step(1 / 60f);
        }
        int mBelow = 0;
        for (int i = 0; i < a.particles().size(); i++) {
            mMaxDifference = Math.max(mMaxDifference, PVector.dist(a.particles(i).position(),
                                                                   b.particles(i).position()));
            final PVector p = b.particles(i).position();
            if (p.x > 1 && p.x < W - 1 && p.y > 1 && p.y < W - 1 && p.z < -0.01f) {
                mBelow++;
            }
        }
        check("mesh matches individual triangles", mMaxDifference < 0.01f && mBelow == 0,
              "max difference=" + mMaxDifference + " below mesh=" + mBelow);
    }

    private static void testMeshTerrain() {
        /* random height terrain with concave valleys */
        final Physics b = meshScene(true, mesh(10));
        for (int f = 0; f < 300; f++) {
            b.step(1 / 60f);
        }
        int mBelow = 0;
        for (Particle q : b.particles()) {
            final PVector p = q.position();
            if (p.x > 1 && p.x < W - 1 && p.y > 1 && p.y < W - 1 && p.z < -0.01f) {
                mBelow++;
            }
        }
        check("mesh terrain no tunneling", mBelow == 0, "below terrain=" + mBelow);
    }

    /* steep terrain, fast sliding particles, all integrators, mesh and separate triangles */
    private static void testSteepTerrain() {
        final int G = 10;
        final float s = W / G;
        final Random r = new Random(3);
        final float[][] z = new float[G + 1][G + 1];
        for (int i = 0; i <= G; i++) {
            for (int j = 0; j <= G; j++) {
                z[i][j] = r.nextFloat() * 60;
            }
        }
        final float[] v = new float[G * G * 18];
        int k = 0;
        for (int i = 0; i < G; i++) {
            for (int j = 0; j < G; j++) {
                final float[] q = {i * s, j * s, z[i][j], (i + 1) * s, j * s, z[i + 1][j],
                                   (i + 1) * s, (j + 1) * s, z[i + 1][j + 1], i * s, (j + 1) * s, z[i][j + 1]};
                for (int t : new int[]{0, 1, 2, 0, 2, 3}) {
                    v[k++] = q[t * 3];
                    v[k++] = q[t * 3 + 1];
                    v[k++] = q[t * 3 + 2];
                }
            }
        }
        int mBelow = 0;
        for (boolean mMesh : new boolean[]{true, false}) {
            for (Integrator mIntegrator : new Integrator[]{new Euler(), new Midpoint(), new RungeKutta(), new Verlet()}) {
                for (float e : new float[]{0, 0.5f, 1}) {
                    final Physics mPhysics = physics(mIntegrator);
                    mPhysics.add(new Gravity(0, 0, -100));
                    if (mMesh) {
                        final TriangleMeshCollider c = new TriangleMeshCollider(v);
                        c.coefficientofrestitution(e);
                        mPhysics.add(c);
                    } else {
                        mPhysics.addConstraints(teilchen.util.Util.createTriangleColliders(v, e));
                    }
                    final Random r2 = new Random(2);
                    for (int i = 0; i < 300; i++) {
                        particle(mPhysics, r2.nextFloat() * W, r2.nextFloat() * W, 100 + r2.nextFloat() * 100,
                                 r2.nextFloat() * 200 - 100, r2.nextFloat() * 200 - 100, 0, 1 / 60f);
                    }
                    for (int f = 0; f < 300; f++) {
                        mPhysics.step(1 / 60f);
                    }
                    for (Particle q : mPhysics.particles()) {
                        final PVector p = q.position();
                        final int i = (int) Math.floor(p.x / s), j = (int) Math.floor(p.y / s);
                        if (i < 0 || j < 0 || i >= G || j >= G) {
                            continue;
                        }
                        final float fx = p.x / s - i, fy = p.y / s - j;
                        final float h = fx >= fy ?
                                        z[i][j] + fx * (z[i + 1][j] - z[i][j]) + fy * (z[i + 1][j + 1] - z[i + 1][j]) :
                                        z[i][j] + fy * (z[i][j + 1] - z[i][j]) + fx * (z[i + 1][j + 1] - z[i][j + 1]);
                        if (p.z < h - 0.01f) {
                            mBelow++;
                        }
                    }
                }
            }
        }
        check("steep terrain ( 24 configurations )", mBelow == 0, "below terrain=" + mBelow);
    }

    private static void testMeshPerformance() {
        final float[] v = mesh(20); /* 800 triangles */
        final double mTriangles = time(meshScene(false, v));
        final double mMesh = time(meshScene(true, v));
        check("mesh faster than individual triangles", mMesh < mTriangles,
              String.format("1000 particles x 800 triangles: triangles %.2f ms/step, mesh %.2f ms/step",
                            mTriangles, mMesh));
    }

    private static double time(Physics pPhysics) {
        for (int f = 0; f < 30; f++) {
            pPhysics.step(1 / 60f);
        }
        final long t0 = System.nanoTime();
        for (int f = 0; f < 60; f++) {
            pPhysics.step(1 / 60f);
        }
        return (System.nanoTime() - t0) / 1.0e6 / 60;
    }
}
