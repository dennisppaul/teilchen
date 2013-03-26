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


package teilchen.test.particle.springs;

import processing.core.PApplet;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.force.AngleConstraintSpring;
import teilchen.force.Gravity;
import teilchen.force.Spring;
import teilchen.force.ViscousDrag;
import teilchen.integration.RungeKutta;


public class TestAngleConstraint
        extends PApplet {

    private Physics mPhysics;

    private Particle mParticleA;

    private Particle mParticleB;

    private Particle mParticleC;

    private AngleConstraintSpring mAngleConstraint;

    public void setup() {
        size(640, 480);
        frameRate(30);

        mPhysics = new Physics();
        mPhysics.setInegratorRef(new RungeKutta());

        ViscousDrag myViscousDrag = new ViscousDrag();
        myViscousDrag.coefficient = 1f;
        mPhysics.add(myViscousDrag);

        Gravity myGravity = new Gravity();
        myGravity.force().y = 50;
        mPhysics.add(myGravity);

        mParticleA = mPhysics.makeParticle();
        mParticleB = mPhysics.makeParticle();
        mParticleC = mPhysics.makeParticle();

        mParticleA.position().set(width / 2 + 50, height / 3);
        mParticleB.position().set(width / 2, height - height / 2);
        mParticleC.position().set(width / 2, height - height / 4);

        mParticleA.radius(7);
        mParticleB.radius(3);
        mParticleC.radius(10);

        mParticleB.fixed(true);

        Spring mSpringAB = new Spring(mParticleA, mParticleB);
        mSpringAB.strength(50);
        mSpringAB.damping(10);
        mPhysics.add(mSpringAB);

        Spring mSpringBC = new Spring(mParticleB, mParticleC);
        mSpringBC.strength(50);
        mSpringBC.damping(10);
        mPhysics.add(mSpringBC);

        mAngleConstraint = new AngleConstraintSpring(mParticleA, mParticleB, mParticleC);
        mAngleConstraint.min_angle(PI * 0.75f);
        mAngleConstraint.damping(1);
        mAngleConstraint.strength(200);
        mPhysics.add(mAngleConstraint);
    }

    public void draw() {

        /* handle particles */
        if (mousePressed) {
            mParticleA.position().set(mouseX, mouseY);
        }

        /* apply constraints */
        mAngleConstraint.pre_step();
        draw_physics();

        mPhysics.step(1f / frameRate);
        /* remove contraints */
        mAngleConstraint.post_step();
    }

    public void line(Particle p1, Particle p2) {
        line(p1.position().x, p1.position().y,
             p2.position().x, p2.position().y);
    }

    private void draw_physics() {
        /* draw */
        background(255);

        /* draw springs */
        for (int i = 0; i < mPhysics.forces().size(); i++) {
            if (mPhysics.forces(i) instanceof Spring) {
                Spring mySpring = (Spring)mPhysics.forces(i);
                if (mPhysics.forces(i).active()) {
                            stroke(0, 127);
                } else {
                            stroke(0, 16);
                }
                line(mySpring.a().position().x,
                     mySpring.a().position().y,
                     mySpring.b().position().x,
                     mySpring.b().position().y);
            }
        }

        /* draw particles */

        stroke(0);
        fill(92);
        drawParticle(mParticleA);
        fill(127);
        drawParticle(mParticleB);
        fill(192);
        drawParticle(mParticleC);
    }

    public static void main(String[] args) {
        PApplet.main(new String[] {TestAngleConstraint.class.getName()});
    }

    private void drawParticle(Particle p) {
        ellipse(p.position().x,
                p.position().y,
                p.radius() * 2, p.radius() * 2);
    }
}
