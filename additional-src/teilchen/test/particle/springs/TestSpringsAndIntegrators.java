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
import teilchen.force.ViscousDrag;
import teilchen.integration.RungeKutta;
import processing.core.PApplet;


public class TestSpringsAndIntegrators
    extends PApplet {

    private Physics _myParticleSystemEuler;

    private Particle myStickyParticleEuler;

    private Physics _myParticleSystemRungeKutta;

    private Particle myStickyParticleRungeKutta;

    public void setup() {
        size(640, 480);
        frameRate(120);

        /* euler */
        {
            _myParticleSystemEuler = new Physics();

            Gravity myGravity = new Gravity();
            myGravity.force().y = 900;
            _myParticleSystemEuler.add(myGravity);

            ViscousDrag myViscousDrag = new ViscousDrag();
            myViscousDrag.coefficient = 1f;
            _myParticleSystemEuler.add(myViscousDrag);

            myStickyParticleEuler = _myParticleSystemEuler.makeParticle();
            Particle myB = _myParticleSystemEuler.makeParticle();
            Particle myC = _myParticleSystemEuler.makeParticle();

            myStickyParticleEuler.position().set(width / 2, height / 2);
            myB.position().set(width / 2, height - height / 3);
            myC.position().set(width / 2, height - height / 4);

            Spring mySpringAB = new Spring(myStickyParticleEuler, myB);
            mySpringAB.strength(25);
            _myParticleSystemEuler.add(mySpringAB);

            Spring mySpringBC = new Spring(myB, myC);
            mySpringBC.damping(0.01f);
            mySpringBC.strength(25);
            _myParticleSystemEuler.add(mySpringBC);

            Spring mySpringAC = new Spring(myStickyParticleEuler, myC);
            mySpringAC.damping(10f);
            mySpringAC.strength(25);
            mySpringAC.restlength(50);
            _myParticleSystemEuler.add(mySpringAC);

            myStickyParticleEuler.fixed(true);
        }

        /* rungekutta */
        {
            _myParticleSystemRungeKutta = new Physics();
            _myParticleSystemRungeKutta.setInegratorRef(new RungeKutta());

            Gravity myGravity = new Gravity();
            myGravity.force().y = 900;
            _myParticleSystemRungeKutta.add(myGravity);

            ViscousDrag myViscousDrag = new ViscousDrag();
            myViscousDrag.coefficient = 1f;
            _myParticleSystemRungeKutta.add(myViscousDrag);

            myStickyParticleRungeKutta = _myParticleSystemRungeKutta.makeParticle();
            Particle myB = _myParticleSystemRungeKutta.makeParticle();
            Particle myC = _myParticleSystemRungeKutta.makeParticle();

            myStickyParticleRungeKutta.position().set(width / 2, height / 2);
            myB.position().set(width / 2, height - height / 3);
            myC.position().set(width / 2, height - height / 4);

            Spring mySpringAB = new Spring(myStickyParticleRungeKutta, myB);
            mySpringAB.strength(25);
            _myParticleSystemRungeKutta.add(mySpringAB);

            Spring mySpringBC = new Spring(myB, myC);
            mySpringBC.damping(0.01f);
            mySpringBC.strength(25);
            _myParticleSystemRungeKutta.add(mySpringBC);

            Spring mySpringAC = new Spring(myStickyParticleRungeKutta, myC);
            mySpringAC.damping(10f);
            mySpringAC.strength(25);
            mySpringAC.restlength(50);
            _myParticleSystemRungeKutta.add(mySpringAC);

            myStickyParticleRungeKutta.fixed(true);
        }
    }


    public void draw() {

        /* handle particles */
        if (mousePressed) {
            myStickyParticleEuler.position().set(mouseX, mouseY);
            myStickyParticleRungeKutta.position().set(mouseX, mouseY);
        }
        _myParticleSystemEuler.step(1f / frameRate);
        _myParticleSystemRungeKutta.step(1f / frameRate);

        /* draw */
        background(255);

        /* euler */
        {
            /* draw springs */
            stroke(0, 64);
            for (IForce myForce : _myParticleSystemEuler.forces()) {
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
            for (Particle myParticle : _myParticleSystemEuler.particles()) {
                ellipse(myParticle.position().x, myParticle.position().y, 10, 10);
            }
        }

        /* rungekutta */
        {
            /* draw springs */
            stroke(255, 0, 0, 64);
            for (IForce myForce : _myParticleSystemRungeKutta.forces()) {
                if (myForce instanceof Spring) {
                    Spring mySpring = (Spring) myForce;
                    line(mySpring.a().position().x,
                         mySpring.a().position().y,
                         mySpring.b().position().x,
                         mySpring.b().position().y);
                }
            }

            /* draw particles */
            stroke(255, 0, 0, 127);
            for (Particle myParticle : _myParticleSystemRungeKutta.particles()) {
                ellipse(myParticle.position().x, myParticle.position().y, 10, 10);
            }
        }
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {TestSpringsAndIntegrators.class.getName()});
    }
}
