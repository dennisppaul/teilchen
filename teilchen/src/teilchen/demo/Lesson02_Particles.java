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


import teilchen.Particle;
import teilchen.Physics;
import teilchen.force.Gravity;
import processing.core.PApplet;


/**
 * this sketch shows how to create and handle multiple particles and remove
 * individual particles.
 */
public class Lesson02_Particles
        extends PApplet {

    private Physics mPhysics;

    public void setup() {
        size(640, 480, OPENGL);
        smooth();
        frameRate(30);

        /* create a particle system */
        mPhysics = new Physics();

        /* create a gravitational force and add it to the particle system */
        Gravity myGravity = new Gravity(0, 30, 0);
        mPhysics.add(myGravity);
    }

    public void draw() {
        if (mousePressed) {
            /* create and add a particle to the system */
            Particle mParticle = mPhysics.makeParticle();
            /* set particle to mouse position with random velocity */
            mParticle.position().set(mouseX, mouseY);
            mParticle.velocity().set(random(-20, 20), random(-50));
        }

        /* update the particle system */
        final float mDeltaTime = 1.0f / frameRate;
        mPhysics.step(mDeltaTime);

        /* remove particles right before they hit the edge of the screen */
        for (int i = 0; i < mPhysics.particles().size(); i++) {
            Particle mParticle = mPhysics.particles(i);
            if (mParticle.position().y > height * 0.9f) {
                mPhysics.particles().remove(i);
            }
        }

        /* draw all the particles in the system */
        background(255);
        stroke(0, 127);
        fill(0, 32);
        for (int i = 0; i < mPhysics.particles().size(); i++) {
            Particle mParticle = mPhysics.particles(i);
            ellipse(mParticle.position().x, mParticle.position().y, 10, 10);
        }
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{Lesson02_Particles.class.getName()});
    }
}
