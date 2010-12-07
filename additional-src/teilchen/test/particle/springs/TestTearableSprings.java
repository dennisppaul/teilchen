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


package teilchen.test.particle.springs;


import teilchen.Particle;
import teilchen.Physics;
import teilchen.force.Gravity;
import teilchen.force.IForce;
import teilchen.force.Spring;
import teilchen.force.TearableSpring;
import teilchen.force.ViscousDrag;
import teilchen.integration.RungeKutta;
import processing.core.PApplet;


public class TestTearableSprings
    extends PApplet {

    private Physics _myParticleSystem;

    private Particle myA;

    public void setup() {
        size(640, 480);
        frameRate(60);

        _myParticleSystem = new Physics();
        _myParticleSystem.setInegratorRef(new RungeKutta());

        ViscousDrag myViscousDrag = new ViscousDrag();
        myViscousDrag.coefficient = 0.3f;
        _myParticleSystem.add(myViscousDrag);

        Gravity myGravity = new Gravity();
        myGravity.force().y = 75;
        _myParticleSystem.add(myGravity);

        myA = _myParticleSystem.makeParticle();
        Particle myB = _myParticleSystem.makeParticle();
        Particle myC = _myParticleSystem.makeParticle();

        myA.position().set(width * 0.5f, height / 2);
        myB.position().set(width * 0.4f, height - height / 3);
        myC.position().set(width * 0.6f, height - height / 4);

        TearableSpring mySpringAB = new TearableSpring(myA, myB);
        mySpringAB.teardistance(100);
        mySpringAB.strength(140);
        mySpringAB.damping(0.9f);
        _myParticleSystem.add(mySpringAB);

        Spring mySpringBC = new Spring(myB, myC);
        mySpringBC.strength(20);
        _myParticleSystem.add(mySpringBC);

        TearableSpring mySpringAC = new TearableSpring(myA, myC);
        mySpringAC.teardistance(150);
        mySpringAC.strength(140);
        mySpringAC.damping(0.9f);
        _myParticleSystem.add(mySpringAC);

        myA.fixed(true);
    }


    public void draw() {

        /* handle particles */
        if (mousePressed) {
            myA.position().set(mouseX, mouseY);
        }
        _myParticleSystem.step(1f / frameRate);

        /* draw */
        background(255);

        /* draw springs */
        stroke(0, 64);
        for (IForce myForce : _myParticleSystem.forces()) {
            if (myForce instanceof Spring) {
                Spring mySpring = (Spring) myForce;
                line(mySpring.a().position().x,
                     mySpring.a().position().y,
                     mySpring.b().position().x,
                     mySpring.b().position().y);
            }
        }

        /* draw particles */
        stroke(0, 127);
        for (Particle myParticle : _myParticleSystem.particles()) {
            ellipse(myParticle.position().x, myParticle.position().y, 10, 10);
        }
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {TestTearableSprings.class.getName()});
    }
}
