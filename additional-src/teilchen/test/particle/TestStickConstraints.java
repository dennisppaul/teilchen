/*
 * Particles
 *
 * Copyright (C) 2010
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


package teilchen.test.particle;


import mathematik.Vector3f;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.constraint.Stick;
import teilchen.force.Gravity;
import teilchen.force.ViscousDrag;
import teilchen.integration.Euler;
import teilchen.integration.Midpoint;
import teilchen.integration.RungeKutta;
import processing.core.PApplet;


public class TestStickConstraints
        extends PApplet {

    private Physics mParticleSystem;

    private Particle mRoot;

    public void setup() {
        size(640, 480, OPENGL);
        frameRate(30);
        smooth();

        mParticleSystem = new Physics();
        mParticleSystem.contraint_iterations_per_steps = 10;
        mParticleSystem.add(new ViscousDrag(0.99f));
        mParticleSystem.add(new Gravity(0, 200, 0));

        mRoot = mParticleSystem.makeParticle(new Vector3f(0, 0, 0));
        mRoot.fixed(true);

        Particle myLastParticle = mRoot;
        for (int i = 0; i < 20; i++) {
            Particle myParticle = mParticleSystem.makeParticle();
            myParticle.position().set(0, (i + 1) * 10, 0);
            myParticle.mass(0.05f);
            Stick myStick = new Stick(myLastParticle, myParticle);
            myStick.restlength(10);
            myStick.damping(0.99f);
            mParticleSystem.add(myStick);
            myLastParticle = myParticle;
        }
    }

    public void draw() {
        mParticleSystem.step(1 / frameRate);
        mRoot.position().set(mouseX, mouseY);

        for (int i = 0; i < mParticleSystem.constraints().size(); i++) {
            final Stick myStick = (Stick)mParticleSystem.constraints().get(i);
            myStick.setOneWay(mousePressed);
        }

        /* draw particles */
        background(255);

        stroke(0, 0, 0);

        for (int i = 0; i < mParticleSystem.particles().size() - 1; i++) {
            ellipse(mParticleSystem.particles().get(i).position().x,
                    mParticleSystem.particles().get(i).position().y,
                    5, 5);
            line(mParticleSystem.particles().get(i + 1).position().x,
                 mParticleSystem.particles().get(i + 1).position().y,
                 mParticleSystem.particles().get(i).position().x,
                 mParticleSystem.particles().get(i).position().y);

        }

        if (keyPressed) {
            if (key=='1') {
                    mParticleSystem.setInegratorRef(new RungeKutta());
            }
            if (key=='2') {
                    mParticleSystem.setInegratorRef(new Euler());
            }
            if (key=='2') {
                    mParticleSystem.setInegratorRef(new Midpoint());
            }
        }
    }

    public static void main(String[] args) {
        PApplet.main(new String[] {TestStickConstraints.class.getName()});
    }
}
