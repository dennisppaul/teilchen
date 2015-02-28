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
import teilchen.force.Spring;
import teilchen.force.ViscousDrag;


/**
 * this sketch shows 1 how to create a viscous drag to slow motion eventually
 * down. 2 how to create a spring that connects two particles.
 */
public class Lesson05_Spring
        extends PApplet {

    private Physics mPhysics;

    private Spring mSpring;

    public void setup() {
        size(640, 480, OPENGL);
        smooth();
        frameRate(30);

        /* create a particle system */
        mPhysics = new Physics();

        /* create a viscous force that slows down all motion; 0 means no slowing down. */
        ViscousDrag myDrag = new ViscousDrag(0.25f);
        mPhysics.add(myDrag);

        /* create two particles that we can connect with a spring */
        Particle myA = mPhysics.makeParticle();
        myA.position().set(width / 2 - 50, height / 2);

        Particle myB = mPhysics.makeParticle();
        myB.position().set(width / 2 + 50, height / 2);

        /* create a spring force that connects two particles.
         * note that there is more than one way to create a spring.
         * in our case the restlength of the spring is defined by the
         * particles current position.
         */
        mSpring = mPhysics.makeSpring(myA, myB);
    }

    public void draw() {
        /* set first particle to mouse position */
        if (mousePressed) {
            mSpring.a().position().set(mouseX, mouseY);
        }

        /* update the particle system */
        final float mDeltaTime = 1.0f / frameRate;
        mPhysics.step(mDeltaTime);

        /* draw particles and connecting line */
        background(255);
        noFill();
        stroke(255, 0, 127, 64);
        line(mSpring.a().position().x, mSpring.a().position().y,
             mSpring.b().position().x, mSpring.b().position().y);
        fill(245);
        stroke(164);
        ellipse(mSpring.a().position().x, mSpring.a().position().y, 12, 12);
        ellipse(mSpring.b().position().x, mSpring.b().position().y, 12, 12);
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{Lesson05_Spring.class.getName()});
    }
}
