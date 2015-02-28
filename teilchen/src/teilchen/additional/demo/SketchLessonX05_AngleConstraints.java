/*
 * Teilchen
 *
 * Copyright (C) 2013
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
package teilchen.demo;


import processing.core.PApplet;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.constraint.AngleConstraintStick;
import teilchen.constraint.Stick;
import teilchen.force.AngleConstraintSpring;
import teilchen.force.Gravity;
import teilchen.force.Spring;
import teilchen.force.ViscousDrag;
import teilchen.integration.RungeKutta;


public class LessonX05_AngleConstraints
        extends PApplet {

    private Physics mPhysics;

    private Particle mParticleA;

    private Particle mParticleB;

    private Particle mParticleC;

    private Particle mParticleD;

    private AngleConstraintSpring mAngleConstraintABC;

    private AngleConstraintStick mAngleConstraintBCD;

    public void setup() {
        size(640, 480);
        frameRate(30);
        smooth();

        mPhysics = new Physics();
        mPhysics.setInegratorRef(new RungeKutta());

        ViscousDrag myViscousDrag = new ViscousDrag();
        myViscousDrag.coefficient = 1f;
        mPhysics.add(myViscousDrag);

        Gravity myGravity = new Gravity();
        myGravity.force().y = 50;
        mPhysics.add(myGravity);

        /* particles */
        mParticleA = mPhysics.makeParticle();
        mParticleB = mPhysics.makeParticle();
        mParticleC = mPhysics.makeParticle();
        mParticleD = mPhysics.makeParticle();

        mParticleA.position().set(width / 2 + 50, height / 3);
        mParticleB.position().set(width / 2, height - height / 1.75f);
        mParticleC.position().set(width / 2, height - height / 4);
        mParticleD.position().set(width / 2, height - height / 8);

        mParticleA.radius(7);
        mParticleB.radius(3);
        mParticleC.radius(10);
        mParticleD.radius(2);

        mParticleB.fixed(true);

        /* springs */
        Spring mSpringAB = new Spring(mParticleA, mParticleB);
        mSpringAB.strength(250);
        mSpringAB.damping(10);
        mPhysics.add(mSpringAB);

        Spring mSpringBC = new Spring(mParticleB, mParticleC);
        mSpringBC.strength(250);
        mSpringBC.damping(10);
        mPhysics.add(mSpringBC);

        Stick mSpringCD = new Stick(mParticleC, mParticleD);
        mSpringCD.damping(1);
        mPhysics.add(mSpringCD);

        /* angle constraint */
        mAngleConstraintABC = new AngleConstraintSpring(mParticleA, mParticleB, mParticleC);
        mAngleConstraintABC.min_angle(PI * 0.5f);
        mAngleConstraintABC.damping(1);
        mAngleConstraintABC.strength(200);
        mPhysics.add(mAngleConstraintABC);

        mAngleConstraintBCD = new AngleConstraintStick(mParticleB, mParticleC, mParticleD);
        mAngleConstraintBCD.min_angle(PI * 0.8f);
        mAngleConstraintBCD.damping(0.5f);
        mPhysics.add(mAngleConstraintBCD);
    }

    public void draw() {
        /* attach particle to mouse */
        if (mousePressed) {
            mParticleA.position().set(mouseX, mouseY);
        }

        /* apply constraints */
        mAngleConstraintABC.pre_step();
        mAngleConstraintBCD.pre_step();
        draw_physics();

        mPhysics.step(1f / frameRate);

        /* remove contraints */
        mAngleConstraintABC.post_step();
        mAngleConstraintBCD.post_step();
    }

    private void draw_physics() {
        background(255);

        drawSprings();
        drawSticks();
        drawParticles();
    }

    private void drawSprings() {
        for (int i = 0; i < mPhysics.forces().size(); i++) {
            if (mPhysics.forces(i) instanceof Spring) {
                final Spring mSpring = (Spring) mPhysics.forces(i);
                if (mSpring instanceof AngleConstraintSpring) {
                    strokeWeight(1);
                    if (mSpring.active()) {
                        stroke(255, 0, 0, 64);
                    } else {
                        stroke(255, 0, 0, 16);
                    }
                } else {
                    strokeWeight(3);
                    stroke(0, 128);
                }
                line(mSpring.a(), mSpring.b());
            }
        }
        strokeWeight(1);
    }

    private void drawSticks() {
        for (int i = 0; i < mPhysics.constraints().size(); i++) {
            if (mPhysics.constraints(i) instanceof Stick) {
                final Stick mStick = (Stick) mPhysics.constraints(i);
                if (mStick instanceof AngleConstraintStick) {
                    strokeWeight(1);
                    if (mStick.active()) {
                        stroke(0, 127, 255, 64);
                    } else {
                        stroke(0, 127, 255, 16);
                    }
                } else {
                    strokeWeight(3);
                    stroke(0, 128);
                }
                line(mStick.a(), mStick.b());
            }
        }
        strokeWeight(1);
    }

    private void drawParticles() {
        stroke(0);
        fill(92);
        drawParticle(mParticleA);
        fill(127);
        drawParticle(mParticleB);
        fill(192);
        drawParticle(mParticleC);
        fill(64);
        drawParticle(mParticleD);
    }

    private void drawParticle(Particle p) {
        ellipse(p.position().x,
                p.position().y,
                p.radius() * 2, p.radius() * 2);
    }

    private void line(Particle p1, Particle p2) {
        line(p1.position().x, p1.position().y,
             p2.position().x, p2.position().y);
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{LessonX05_AngleConstraints.class.getName()});
    }
}
