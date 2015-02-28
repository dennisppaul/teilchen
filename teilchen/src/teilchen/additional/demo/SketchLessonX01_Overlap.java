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


import mathematik.Vector3f;

import processing.core.PApplet;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.force.Gravity;
import teilchen.force.Spring;
import teilchen.force.ViscousDrag;
import teilchen.util.Overlap;
import teilchen.util.Packing;


/**
 * this sketch is exactly like Lesson06_Springs, except that it also shows how
 * to resolveOverlap overlaps.
 */
public class LessonX01_Overlap
        extends PApplet {

    private Physics mPhysics;

    private Particle mRoot;

    private static final float PARTICLE_RADIUS = 13;

    public void setup() {
        size(640, 480, OPENGL);
        smooth();
        frameRate(30);

        mPhysics = new Physics();

        /* create drag */
        mPhysics.add(new ViscousDrag());
        mPhysics.add(new Gravity(new Vector3f(0, 100f, 0)));


        mRoot = mPhysics.makeParticle(width / 2, height / 2, 0.0f);
        mRoot.mass(30);
        mRoot.fixed(true);
        mRoot.radius(PARTICLE_RADIUS);
    }

    public void draw() {
        if (mousePressed) {
            Particle mParticle = mPhysics.makeParticle(mouseX, mouseY, 0);
            mPhysics.makeSpring(mRoot, mParticle);

            /*
             * we define a radius for the particle so the particle has
             * dimensions
             */
            mParticle.radius(random(PARTICLE_RADIUS / 2) + PARTICLE_RADIUS);
        }


        /* move overlapping particles away from each other */
        for (int i = 0; i < 10; i++) {
            mRoot.position().set(width / 2, height / 2, 0.0f); // a bit of a 'hack'
            Overlap.resolveOverlap(mPhysics.particles());
        }

        /* update the particle system */
        final float mDeltaTime = 1.0f / frameRate;
        mPhysics.step(mDeltaTime);

        /* draw particles and connecting line */
        background(255);

        /* draw springs */
        noFill();
        stroke(255, 0, 127, 64);
        for (int i = 0; i < mPhysics.forces().size(); i++) {
            if (mPhysics.forces().get(i) instanceof Spring) {
                Spring mSSpring = (Spring) mPhysics.forces().get(i);
                line(mSSpring.a().position().x, mSSpring.a().position().y,
                     mSSpring.b().position().x, mSSpring.b().position().y);
            }
        }
        /* draw particles */
        fill(255, 127);
        stroke(164);
        for (int i = 0; i < mPhysics.particles().size(); i++) {
            ellipse(mPhysics.particles().get(i).position().x,
                    mPhysics.particles().get(i).position().y,
                    mPhysics.particles().get(i).radius() * 2,
                    mPhysics.particles().get(i).radius() * 2);
        }
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{LessonX01_Overlap.class.getName()});
    }
}
