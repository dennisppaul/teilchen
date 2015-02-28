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
 * this sketch show how to create a particle system with a single particle in
 * it.
 */
public class Lesson01_Gravity
        extends PApplet {

    private Physics mPhysics;

    private Particle mParticle;

    public void setup() {
        size(640, 480, OPENGL);
        smooth();
        frameRate(30);

        /* create a particle system */
        mPhysics = new Physics();

        /* create a gravitational force */
        Gravity mGravity = new Gravity();
        /* the direction of the gravity is defined by the 'force' vector */
        mGravity.force().set(0, 30, 0);
        /* forces, like gravity or any other force, can be added to the system. they will be automatically applied to all particles */
        mPhysics.add(mGravity);

        /* create a particle and add it to the system */
        mParticle = mPhysics.makeParticle();
    }

    public void draw() {
        /* update the particle system. this applies the gravity to the particle */
        final float mDeltaTime = 1.0f / frameRate;
        mPhysics.step(mDeltaTime);

        /* draw particle */
        background(255);
        stroke(0, 127);
        fill(0, 32);
        ellipse(mParticle.position().x, mParticle.position().y, 12, 12);

        /* reset particle s position and velocity */
        if (mousePressed) {
            mParticle.position().set(mouseX, mouseY);
            mParticle.velocity().set(mouseX - pmouseX, mouseY - pmouseY);
            mParticle.velocity().scale(10);
        }
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{Lesson01_Gravity.class.getName()});
    }
}
