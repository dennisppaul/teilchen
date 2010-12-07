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


package teilchen.test.particle;


import teilchen.Particle;
import teilchen.Physics;
import teilchen.constraint.Angular;
import teilchen.constraint.Box;
import teilchen.constraint.Stick;
import teilchen.force.ViscousDrag;
import processing.core.PApplet;


public class TestContraints
    extends PApplet {

    private Physics _myParticleSystem;

    private Particle _myA;

    private Particle _myB;

    private Particle _myC;

    private Angular _myAngular;

    public void setup() {
        size(640, 480, OPENGL);
        frameRate(120);
        smooth();

        _myParticleSystem = new Physics();
        _myParticleSystem.add(new ViscousDrag(0.1f));

        _myA = _myParticleSystem.makeParticle();
        _myB = _myParticleSystem.makeParticle();
        _myC = _myParticleSystem.makeParticle();

        Stick myStickA = new Stick(_myA, _myB);
        Stick myStickB = new Stick(_myB, _myC);
        myStickA.restlength(100);
        myStickB.restlength(100);
        _myParticleSystem.add(myStickA);
        _myParticleSystem.add(myStickB);

        Box myBox = new Box();
        myBox.min().set(0, 0);
        myBox.max().set(width, height);
        _myParticleSystem.add(myBox);

        _myAngular = new Angular(_myA, _myB, _myC);
        _myAngular.range(2.0f, PI - 0.01f);
        _myParticleSystem.add(_myAngular);

        _myA.position().set(random(width / 2 - 40, width / 2 + 40), random(height / 2 - 40, height / 2 + 40));
        _myB.position().set(random(width / 2 - 40, width / 2 + 40), random(height / 2 - 40, height / 2 + 40));
        _myC.position().set(random(width / 2 - 40, width / 2 + 40), random(height / 2 - 40, height / 2 + 40));
    }


    public void draw() {
        _myParticleSystem.step(1 / 120f);

        if (mousePressed) {
            mouseTest();
        }

        /* draw particles */
        background(255);

        if (_myAngular.OK) {
            stroke(0);
        } else {
            stroke(255, 0, 0);
        }

        line(_myA.position().x, _myA.position().y,
             _myB.position().x, _myB.position().y);
        line(_myC.position().x, _myC.position().y,
             _myB.position().x, _myB.position().y);

        ellipse(_myC.position().x, _myC.position().y, 5, 5);
        ellipse(_myB.position().x, _myB.position().y, 10, 10);
        ellipse(_myA.position().x, _myA.position().y, 20, 20);
    }


    private void mouseTest() {
        final int myRadius = 20;
        for (Particle myParticle : _myParticleSystem.particles()) {
            if (mouseX > myParticle.position().x - myRadius &&
                mouseX < myParticle.position().x + myRadius &&
                mouseY > myParticle.position().y - myRadius &&
                mouseY < myParticle.position().y + myRadius) {
                myParticle.position().set(mouseX, mouseY);
                return;
            }
        }
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {TestContraints.class.getName()});
    }

}
