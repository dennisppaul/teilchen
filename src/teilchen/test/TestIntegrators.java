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
import teilchen.Physics;
import teilchen.constraint.Stick;
import teilchen.constraint.Teleporter;
import teilchen.force.Gravity;
import teilchen.force.Spring;
import teilchen.integration.Euler;
import teilchen.integration.Integrator;
import teilchen.integration.Midpoint;
import teilchen.integration.RungeKutta;
import teilchen.integration.Verlet;

/**
 * headless regression test for integrators and core physics. run with <code>java teilchen.test.TestIntegrators</code>
 * ( processing core on classpath ). exits with status 1 if any check fails.
 */
public class TestIntegrators {

    private static int mFailures = 0;

    public static void main(String[] args) {
        /* constant acceleration: x(1) = (1, -5), v(1) = (1, -10) */
        testGravity("Euler", new Euler(), 0.06f);
        testGravity("Midpoint", new Midpoint(), 0.001f);
        testGravity("RungeKutta", new RungeKutta(), 0.001f);
        testGravity("Verlet", new Verlet(), 0.06f);

        testAddWithoutDuplicateCheck();
        testSpringDampingIsAxial();
        testInnerForceEvaluatedOncePerStep();
        testTeleporterPreservesVerletVelocity();
        testStickRemovesAxialVelocity();
        testFixedParticleVelocity();
        testConcurrentAdd();

        System.out.println(mFailures == 0 ? "ALL PASSED" : mFailures + " FAILED");
        System.exit(mFailures == 0 ? 0 : 1);
    }

    private static void check(String pName, boolean pCondition, String pDetail) {
        System.out.println((pCondition ? "PASS " : "FAIL ") + pName + " " + pDetail);
        if (!pCondition) {
            mFailures++;
        }
    }

    private static void testGravity(String pName, Integrator pIntegrator, float pTolerance) {
        final Physics mPhysics = new Physics();
        mPhysics.setIntegratorRef(pIntegrator);
        mPhysics.add(new Gravity(new PVector(0, -10, 0)));
        final float mDeltaTime = 0.01f;
        final BasicParticle p = mPhysics.makeParticle(0, 0, 0);
        p.velocity().set(1, 0, 0);
        /* verlet derives velocity from previous position */
        p.old_position().set(-1 * mDeltaTime, 0, 0);
        for (int i = 0; i < 100; i++) {
            mPhysics.step(mDeltaTime);
        }
        final boolean mOK = Math.abs(p.position().x - 1) < pTolerance && Math.abs(p.position().y + 5) < pTolerance * 10;
        check("gravity/" + pName, mOK, String.format("pos=(%.4f, %.4f) expected (1, -5)", p.position().x,
                                                     p.position().y));
    }

    private static void testAddWithoutDuplicateCheck() {
        final Physics mPhysics = new Physics();
        final BasicParticle p = new BasicParticle();
        mPhysics.add(p, false);
        mPhysics.add(p, true);
        check("add(particle, false)", mPhysics.particles().size() == 1, "size=" + mPhysics.particles().size());
        final Spring s = new Spring(mPhysics.makeParticle(0, 0, 0), mPhysics.makeParticle(1, 0, 0));
        mPhysics.add(s, false);
        mPhysics.add(s, true);
        check("add(spring, false)", mPhysics.forces().size() == 1, "size=" + mPhysics.forces().size());
    }

    private static void testSpringDampingIsAxial() {
        /* spring along x at rest length. relative velocity perpendicular to axis must not be damped. */
        final BasicParticle a = new BasicParticle();
        final BasicParticle b = new BasicParticle();
        a.position().set(0, 0, 0);
        b.position().set(1, 1, 0);
        a.velocity().set(0, 0, 0);
        b.velocity().set(1, -1, 0); /* perpendicular to (1, 1, 0) */
        final Spring s = new Spring(a, b, 1.0f, 10.0f, (float) Math.sqrt(2));
        s.apply(0.01f, null);
        check("spring damping axial", a.force().mag() < 0.01f, "force=" + a.force());
    }

    private static void testInnerForceEvaluatedOncePerStep() {
        final Physics mPhysics = new Physics();
        mPhysics.setIntegratorRef(new RungeKutta());
        final int[] mCount = {0};
        mPhysics.add(new BasicParticle() {
            public void accumulateInnerForce(float pDeltaTime) {
                mCount[0]++;
            }
        });
        mPhysics.step(0.01f);
        check("inner force once per step", mCount[0] == 1, "calls=" + mCount[0]);
    }

    private static void testTeleporterPreservesVerletVelocity() {
        final Physics mPhysics = new Physics();
        mPhysics.setIntegratorRef(new Verlet());
        mPhysics.add(new Teleporter(new PVector(0, 0, 0), new PVector(10, 10, 10)));
        final BasicParticle p = mPhysics.makeParticle(9.95f, 5, 5);
        p.old_position().set(9.85f, 5, 5); /* moves +0.1 per step */
        for (int i = 0; i < 3; i++) {
            mPhysics.step(0.01f);
        }
        final float mStep = p.position().x - p.old_position().x;
        check("teleporter keeps verlet velocity", Math.abs(mStep - 0.1f) < 0.001f && p.position().x < 1,
              String.format("x=%.3f step=%.3f", p.position().x, mStep));
    }

    private static void testStickRemovesAxialVelocity() {
        /* pendulum on a stick with explicit-velocity integrator: velocity must stay tangential */
        final Physics mPhysics = new Physics();
        mPhysics.add(new Gravity(new PVector(0, 10, 0)));
        final BasicParticle mAnchor = mPhysics.makeParticle(0, 0, 0);
        mAnchor.fixed(true);
        final BasicParticle p = mPhysics.makeParticle(10, 0, 0);
        mPhysics.add(new Stick(mAnchor, p, 10));
        for (int i = 0; i < 100; i++) {
            mPhysics.step(0.01f);
        }
        final PVector mAxis = PVector.sub(p.position(), mAnchor.position());
        final float mLength = mAxis.mag();
        final float mRadialVelocity = Math.abs(mAxis.normalize().dot(p.velocity()));
        check("stick removes axial velocity", mRadialVelocity < 0.01f && Math.abs(mLength - 10) < 0.01f,
              String.format("radial velocity=%.4f speed=%.3f", mRadialVelocity, p.velocity().mag()));
    }

    private static void testFixedParticleVelocity() {
        final Physics mPhysics = new Physics();
        final BasicParticle p = mPhysics.makeParticle(0, 0, 0);
        p.fixed(true);
        mPhysics.step(0.1f);
        p.position().set(1, 0, 0); /* moved 1 unit during a step of 0.1s */
        mPhysics.step(0.1f);
        check("fixed particle velocity from movement", Math.abs(p.velocity().x - 10) < 0.001f,
              "velocity=" + p.velocity());

        /* particle created with `makeParticle()` and positioned afterwards ( stale old position ). first step must
        not produce a velocity spike ( see `SketchLessonX12_TearableSpring` ). */
        final BasicParticle q = mPhysics.makeParticle();
        q.fixed(true);
        q.position().set(160, 120, 0);
        mPhysics.step(1.0f / 300.0f);
        check("fixed particle no spike on first step", q.velocity().mag() < 0.001f, "velocity=" + q.velocity());
    }

    private static void testConcurrentAdd() {
        final Physics mPhysics = new Physics();
        mPhysics.add(new Gravity());
        final Exception[] mError = {null};
        final Thread mAdder = new Thread(() -> {
            try {
                for (int i = 0; i < 2000; i++) {
                    mPhysics.makeParticle(i, 0, 0);
                    if (i % 3 == 0) {
                        mPhysics.remove(mPhysics.particles(0));
                    }
                    Thread.yield();
                }
            } catch (Exception ex) {
                mError[0] = ex;
            }
        });
        mAdder.start();
        try {
            for (int i = 0; i < 500; i++) {
                mPhysics.step(0.01f);
                Thread.yield();
            }
            mAdder.join(10000);
        } catch (Exception ex) {
            mError[0] = ex;
        }
        check("concurrent add/remove during step", mError[0] == null && !mAdder.isAlive(), "error=" + mError[0]);
    }
}
