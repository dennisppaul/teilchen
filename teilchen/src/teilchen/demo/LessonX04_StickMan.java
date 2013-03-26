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
import teilchen.Physics;
import teilchen.force.Attractor;
import teilchen.force.Gravity;
import teilchen.force.Spring;
import teilchen.force.ViscousDrag;
import teilchen.integration.RungeKutta;
import teilchen.util.Overlap;
import teilchen.util.StickMan;


/**
 * this demo shows some advanced use of particles, springs and attractors to
 * create stickmen.
 */
public class LessonX04_StickMan
        extends PApplet {

    private Physics mPhysics;

    private Attractor mAttractor;

    private Gravity mGravity;

    private ViscousDrag mViscousDrag;

    private StickMan[] mMyStickMan;

    public void setup() {
        size(640, 480, OPENGL);
        smooth();
        frameRate(60);
        noFill();

        mPhysics = new Physics();
        mPhysics.setInegratorRef(new RungeKutta());

        mGravity = new Gravity();
        mGravity.force().y = 20;
        mPhysics.add(mGravity);

        mViscousDrag = new ViscousDrag();
        mViscousDrag.coefficient = 0.85f;
        mPhysics.add(mViscousDrag);

        mAttractor = new Attractor();
        mAttractor.radius(500);
        mAttractor.strength(0);
        mAttractor.position().set(width / 2, height / 2);
        mPhysics.add(mAttractor);

        mMyStickMan = new StickMan[20];
        for (int i = 0; i < mMyStickMan.length; i++) {
            mMyStickMan[i] = new StickMan(mPhysics, random(0, width), random(0.3f, 0.6f));
        }
    }

    public void draw() {

        mPhysics.step(1f / 60f);
        Overlap.resolveOverlap(mPhysics.particles());

        /* constraint particles */
        for (int i = 0; i < mPhysics.particles().size(); i++) {
            if (mPhysics.particles(i).position().y > height - 10) {
                mPhysics.particles(i).position().y = height - 10;
            }
            if (mPhysics.particles(i).position().x > width) {
                mPhysics.particles(i).position().x = width;
            }
            if (mPhysics.particles(i).position().x < 0) {
                mPhysics.particles(i).position().x = 0;
            }
        }

        /* handle particles */
        if (mousePressed) {
            mAttractor.position().set(mouseX, mouseY);
            if (mouseButton == RIGHT) {
                mAttractor.strength(-500);
                mAttractor.radius(500);
            } else {
                mAttractor.strength(500);
                mAttractor.radius(100);
            }
        } else {
            mAttractor.strength(0);
        }

        if (keyPressed) {
            mGravity.force().y = -10;
        } else {
            mGravity.force().y = 20;
        }

        /* draw */
        background(255);

        /* draw springs */
        stroke(0, 20);
        for (int i = 0; i < mPhysics.forces().size(); i++) {
            if (mPhysics.forces(i) instanceof Spring) {
                Spring mySpring = (Spring) mPhysics.forces(i);
                line(mySpring.a().position().x,
                     mySpring.a().position().y,
                     mySpring.b().position().x,
                     mySpring.b().position().y);
            }
        }

        /* draw particles */
        for (int i = 0; i < mPhysics.particles().size(); i++) {
            ellipse(mPhysics.particles(i).position().x,
                    mPhysics.particles(i).position().y, 5, 5);
        }

        /* draw man */
        for (int i = 0; i < mMyStickMan.length; i++) {
            mMyStickMan[i].draw(g);
        }
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{LessonX04_StickMan.class.getName()});
    }
}
