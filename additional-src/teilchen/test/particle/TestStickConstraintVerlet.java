/*
 * Particles
 *
 * Copyright (C) 2012
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


import teilchen.Particle;
import teilchen.Physics;
import teilchen.constraint.Stick;
import teilchen.force.Gravity;
import teilchen.integration.Verlet;
import processing.core.PApplet;


public class TestStickConstraintVerlet
        extends PApplet {

    private Physics mPhysics;

    private Particle[] mParticles;

    private final int NUMBER_OF_SEGMENTS = 16;

    private final float STICK_DAMPING = 0.99f;

    public void setup() {
        size(640, 480, OPENGL);
        frameRate(60);
        smooth();

        mPhysics = new Physics();
        mPhysics.contraint_iterations_per_steps = 5;

        Verlet myVerlet = new Verlet();
        myVerlet.damping(0.99f);
        mPhysics.setInegratorRef(myVerlet);

        mPhysics.add(new Gravity());
        /* setup string */
        mParticles = new Particle[NUMBER_OF_SEGMENTS];
        float mSegmentLength = 20.0f;
        /* create root */
        for (int x = 0; x < mParticles.length; x++) {
            mParticles[x] = mPhysics.makeParticle(x * mSegmentLength, 0, 0, 0.1f);
            if (x > 0) {
                Stick myStick = new Stick(mParticles[x - 1],
                                          mParticles[x],
                                          mSegmentLength);
                myStick.damping(STICK_DAMPING);
                mPhysics.add(myStick);
            }
        }

        mParticles[0].fixed(true);
    }

    public void draw() {
        /* update */
        mParticles[0].position().set(mouseX, mouseY);
        mPhysics.step(1.0f / frameRate);

        background(255);

        /* draw sticks */
        stroke(0, 192);
        for (int x = 1; x < mParticles.length; x++) {
            Particle p1 = mParticles[x - 1];
            Particle p2 = mParticles[x];
            final float mStrokeWeight = 4.0f * (1.0f - (float)x / mParticles.length);
            strokeWeight(mStrokeWeight);
            line(p1.position().x,
                 p1.position().y,
                 p1.position().z,
                 p2.position().x,
                 p2.position().y,
                 p2.position().z);
        }
    }

    public static void main(String[] args) {
        PApplet.main(new String[] {TestStickConstraintVerlet.class.getName()});
    }
}


