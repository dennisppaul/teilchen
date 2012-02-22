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


package teilchen.test.particle.springs;


import teilchen.Particle;
import teilchen.Physics;
import teilchen.force.Gravity;
import teilchen.force.Spring;
import teilchen.force.ViscousDrag;
import teilchen.integration.RungeKutta;
import processing.core.PApplet;


public class TestSprings
    extends PApplet {

    private Physics _myParticleSystem;

    private Particle _myParticle;

    public void setup() {
        size(640, 480);
        frameRate(120);

        _myParticleSystem = new Physics();
        _myParticleSystem.setInegratorRef(new RungeKutta());

        Gravity myGravity = new Gravity();
        myGravity.force().y = 900;
        _myParticleSystem.add(myGravity);

        ViscousDrag myViscousDrag = new ViscousDrag();
        myViscousDrag.coefficient = 1f;
        _myParticleSystem.add(myViscousDrag);

        _myParticle = _myParticleSystem.makeParticle();
        Particle myB = _myParticleSystem.makeParticle();
        Particle myC = _myParticleSystem.makeParticle();

        _myParticle.position().set(width / 2, height / 2);
        myB.position().set(width / 2, height - height / 3);
        myC.position().set(width / 2, height - height / 4);

        Spring mySpringAB = new Spring(_myParticle, myB);
        mySpringAB.strength(25);
        _myParticleSystem.add(mySpringAB);

        Spring mySpringBC = new Spring(myB, myC);
        mySpringBC.damping(0.01f);
        mySpringBC.strength(25);
        _myParticleSystem.add(mySpringBC);

        Spring mySpringAC = new Spring(_myParticle, myC);
        mySpringAC.damping(10f);
        mySpringAC.strength(25);
        mySpringAC.restlength(50);
        _myParticleSystem.add(mySpringAC);

        _myParticle.fixed(true);
    }


    public void draw() {

        /* handle particles */
        if (mousePressed) {
            _myParticle.position().set(mouseX, mouseY);
        }
        _myParticleSystem.step(1f / frameRate);

        /* draw */
        background(255);

        /* draw springs */
        stroke(0, 64);
        for (int i = 0; i < _myParticleSystem.forces().size(); i++) {
            if (_myParticleSystem.forces(i) instanceof Spring) {
                Spring mySpring = (Spring) _myParticleSystem.forces(i);
                line(mySpring.a().position().x,
                     mySpring.a().position().y,
                     mySpring.b().position().x,
                     mySpring.b().position().y);
            }
        }

        /* draw particles */
        stroke(0, 127);
        for (int i = 0; i < _myParticleSystem.particles().size(); i++) {
            ellipse(_myParticleSystem.particles(i).position().x,
                    _myParticleSystem.particles(i).position().y, 10, 10);
        }
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {TestSprings.class.getName()});
    }
}
