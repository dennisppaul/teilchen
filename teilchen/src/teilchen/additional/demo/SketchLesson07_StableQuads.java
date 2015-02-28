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


import teilchen.Physics;
import teilchen.force.Gravity;
import teilchen.force.ViscousDrag;
import teilchen.util.DrawLib;
import processing.core.PApplet;
import teilchen.Particle;
import teilchen.constraint.Box;
import teilchen.integration.RungeKutta;
import teilchen.util.StableSpringQuad;


public class Lesson07_StableQuads
        extends PApplet {

    private Physics mPhysics;

    private Particle mRoot;

    public void setup() {
        size(640, 480, OPENGL);
        smooth();
        frameRate(60);

        mPhysics = new Physics();
        /* we use 'runge kutta' as it is more stable for this application */
        mPhysics.setInegratorRef(new RungeKutta());

        Gravity myGravity = new Gravity();
        myGravity.force().y = 98.1f;
        mPhysics.add(myGravity);

        /* add drag to smooth the spring interaction */
        mPhysics.add(new ViscousDrag(0.2f));

        /* add a container */
        Box myBox = new Box();
        myBox.min().set(0, 0, 0);
        myBox.max().set(width, height, 0);
        mPhysics.add(myBox);

        /* create root */
        Particle a = mPhysics.makeParticle(0, 0);
        Particle b = mPhysics.makeParticle(100, 0);
        Particle c = mPhysics.makeParticle(100, 100);
        Particle d = mPhysics.makeParticle(0, 100);

        new StableSpringQuad(mPhysics, d, c, mPhysics.makeParticle(100, 200), mPhysics.makeParticle(0, 200));

        /* create stable quad from springs */
        /* first the edge-springs ... */
        final float mySpringConstant = 100;
        final float mySpringDamping = 5;
        mPhysics.makeSpring(a, b, mySpringConstant, mySpringDamping);
        mPhysics.makeSpring(b, c, mySpringConstant, mySpringDamping);
        mPhysics.makeSpring(c, d, mySpringConstant, mySpringDamping);
        mPhysics.makeSpring(d, a, mySpringConstant, mySpringDamping).restlength();
        /* ... then the diagonal-springs */
        mPhysics.makeSpring(a, c, mySpringConstant, mySpringDamping);
        mPhysics.makeSpring(b, d, mySpringConstant, mySpringDamping).restlength();

        /* define 'a' as root particle for mouse interaction */
        mRoot = a;
        mRoot.fixed(true);
    }

    public void draw() {

        /* handle particles */
        if (mousePressed) {
            mRoot.fixed(true);
            mRoot.position().set(mouseX, mouseY);
        } else {
            mRoot.fixed(false);
        }

        mPhysics.step(1f / frameRate);

        /* draw */
        background(255);
        DrawLib.drawSprings(g, mPhysics, color(255, 0, 127, 64));
        DrawLib.drawParticles(g, mPhysics, 12, color(164), color(245));
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{Lesson07_StableQuads.class.getName()});
    }
}
