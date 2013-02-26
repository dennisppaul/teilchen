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


package teilchen.test.particle.behavior;


import processing.core.PApplet;
import teilchen.BehaviorParticle;
import teilchen.Physics;
import teilchen.behavior.Arrival;
import teilchen.force.Gravity;
import teilchen.force.ViscousDrag;


public class TestBehaviorArrival
    extends PApplet {

    private Physics _myParticleSystem;

    private BehaviorParticle _myParticle;

    private Arrival _myArrival;

    public void setup() {
        size(640, 480, OPENGL);
        frameRate(120);
        smooth();
        colorMode(RGB, 1.0f);
        noFill();

        /* physics */
        _myParticleSystem = new Physics();

        ViscousDrag myViscousDrag = new ViscousDrag();
        myViscousDrag.coefficient = 0.1f;
        _myParticleSystem.add(myViscousDrag);

        Gravity myGravity = new Gravity();
        myGravity.force().y = 5;
        _myParticleSystem.add(myGravity);

        /* create particles */
        _myParticle = _myParticleSystem.makeParticle(BehaviorParticle.class);
        _myParticle.position().set(random(width), random(height));
        _myParticle.maximumInnerForce(100);
        _myArrival = new Arrival();
        _myArrival.breakforce(_myParticle.maximumInnerForce() * 0.25f);
        _myArrival.breakradius(_myParticle.maximumInnerForce() * 0.25f);
        _myParticle.behaviors().add(_myArrival);
    }


    public void draw() {
        /* update particles */
        _myParticleSystem.step(1f / frameRate);

        _myArrival.position().set(mouseX, mouseY);

        if (_myParticle.position().x < 0) {
            _myParticle.position().x = width;
        }
        if (_myParticle.position().x > width) {
            _myParticle.position().x = 0;
        }
        if (_myParticle.position().y < 0) {
            _myParticle.position().y = height;
        }
        if (_myParticle.position().y > height) {
            _myParticle.position().y = 0;
        }

        /* draw particle */
        background(1);
        stroke(0, 0.5f);
        if (_myArrival.arriving()) {
            stroke(1, 0, 0, 0.5f);
        }
        if (_myArrival.arrived()) {
            stroke(0, 1, 0, 0.5f);
        }
        ellipse(_myParticle.position().x, _myParticle.position().y, 20, 20);
        line(_myParticle.position().x,
             _myParticle.position().y,
             _myParticle.position().x + _myParticle.velocity().x * 1,
             _myParticle.position().y + _myParticle.velocity().y * 1);

        stroke(0, 0.25f);
        ellipse(_myArrival.position().x,
                _myArrival.position().y,
                _myArrival.breakradius() * 2,
                _myArrival.breakradius() * 2);
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {TestBehaviorArrival.class.getName()});
    }
}
