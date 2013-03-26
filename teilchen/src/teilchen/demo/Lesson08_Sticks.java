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
import teilchen.constraint.Stick;
import teilchen.force.Gravity;
import teilchen.integration.Verlet;
import processing.core.PApplet;


public class Lesson08_Sticks
        extends PApplet {

    private Physics mPhysics;

    private Particle[] mParticles;

    public void setup() {
        size(640, 480, OPENGL);
        frameRate(60);
        smooth();

        mPhysics = new Physics();
        /* increase the number of iterations for contraints in each step. this can greatly relaxes tensions in the system. */
        mPhysics.contraint_iterations_per_steps = 5;

        /* add gravity for extra fun */
        mPhysics.add(new Gravity());

        /* we chose verlet integration as it integrates much more nicely with sticks ( and constraints in general ) */
        Verlet myVerlet = new Verlet();
        myVerlet.damping(0.99f);
        mPhysics.setInegratorRef(myVerlet);

        /* setup sticks to form a whip */
        mParticles = new Particle[16];
        float mSegmentLength = 20.0f;
        /* create root */
        for (int x = 0; x < mParticles.length; x++) {
            mParticles[x] = mPhysics.makeParticle(x * mSegmentLength, 0, 0, 0.1f);
            if (x > 0) {
                Stick myStick = new Stick(mParticles[x - 1],
                                          mParticles[x],
                                          mSegmentLength);
                /* damp the stick to release tensions from the system */
                myStick.damping(0.99f);
                mPhysics.add(myStick);
            }
        }

        /* fix root particle so it can stick to the mouse later */
        mParticles[0].fixed(true);
    }

    public void draw() {
        /* stick root particle to mouse */
        mParticles[0].position().set(mouseX, mouseY);

        /* update */
        mPhysics.step(1.0f / frameRate);

        /* draw sticks with descending stroke weight */
        background(255);
        stroke(0, 192);
        for (int x = 1; x < mParticles.length; x++) {
            Particle p1 = mParticles[x - 1];
            Particle p2 = mParticles[x];
            final float mStrokeWeight = 4.0f * (1.0f - (float) x / mParticles.length);
            strokeWeight(mStrokeWeight);
            line(p1.position().x, p1.position().y, p1.position().z,
                 p2.position().x, p2.position().y, p2.position().z);
        }
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{Lesson08_Sticks.class.getName()});
    }
}
