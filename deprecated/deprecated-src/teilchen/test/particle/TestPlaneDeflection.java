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


package teilchen.test.particle;


import teilchen.Particle;
import teilchen.Physics;
import teilchen.ShortLivedParticle;
import teilchen.force.Gravity;
import teilchen.force.PlaneDeflector;
import teilchen.force.ViscousDrag;
import processing.core.PApplet;


/** @todo how can we define handle planes that are not infinite in scale? */

public class TestPlaneDeflection
    extends PApplet {

    private Physics _myParticleSystem;

    private PlaneDeflector _myGroudPlaneDeflector;

    private PlaneDeflector _myPlaneDeflector;

    public void setup() {
        size(640, 480, OPENGL);
        frameRate(120);
        colorMode(RGB, 1.0f);
        noFill();

        /* particle system */
        _myParticleSystem = new Physics();

        /* ground plane */
        _myPlaneDeflector = new PlaneDeflector();
        _myPlaneDeflector.plane().origin.set(width / 2, height * 0.75f, 0);
        _myPlaneDeflector.plane().normal.set(0, -1, 0);
        _myPlaneDeflector.coefficientofrestitution(0.75f);
        _myParticleSystem.add(_myPlaneDeflector);

        _myGroudPlaneDeflector = new PlaneDeflector();
        _myGroudPlaneDeflector.plane().origin.set(width / 2, height, 0);
        _myGroudPlaneDeflector.plane().normal.set(0, -1, 0);
        _myGroudPlaneDeflector.coefficientofrestitution(0.75f);
        _myParticleSystem.add(_myGroudPlaneDeflector);

        Gravity myGravity = new Gravity();
        myGravity.force().y = 300;
        _myParticleSystem.add(myGravity);

        ViscousDrag myViscousDrag = new ViscousDrag();
        myViscousDrag.coefficient = 0.1f;
        _myParticleSystem.add(myViscousDrag);
    }


    public void draw() {

        _myParticleSystem.step(1f / frameRate);

        /* -- */
        if (mousePressed) {
            final float myAngle = 2 * PI * (float) mouseX / width - PI;
            _myPlaneDeflector.plane().normal.set(sin(myAngle), -cos(myAngle), 0);
        }
        {
            ShortLivedParticle myParticle = _myParticleSystem.makeParticle(ShortLivedParticle.class);
            myParticle.velocity().x = (mouseX - pmouseX) * 25 + random( -10, 10);
            myParticle.velocity().y = 100;
            myParticle.setMaxAge(4);
            myParticle.position().set(mouseX, mouseY);
        }

        /* draw particles */
        background(1);
        for (Particle myParticle : _myParticleSystem.particles()) {
            final float myAgeRatio = 1 - ( (ShortLivedParticle) myParticle).ageRatio();
            stroke(0, 0.5f);
            ellipse(myParticle.position().x, myParticle.position().y,
                    myAgeRatio * 15, myAgeRatio * 15);
        }

        /* draw plane */
        stroke(0);
        line(_myPlaneDeflector.plane().origin.x - _myPlaneDeflector.plane().normal.y * -200,
             _myPlaneDeflector.plane().origin.y + _myPlaneDeflector.plane().normal.x * -200,
             _myPlaneDeflector.plane().origin.x - _myPlaneDeflector.plane().normal.y * 200,
             _myPlaneDeflector.plane().origin.y + _myPlaneDeflector.plane().normal.x * 200);

        stroke(1, 0, 0);
        line(_myPlaneDeflector.plane().origin.x,
             _myPlaneDeflector.plane().origin.y,
             _myPlaneDeflector.plane().origin.x + _myPlaneDeflector.plane().normal.x * 20,
             _myPlaneDeflector.plane().origin.y + _myPlaneDeflector.plane().normal.y * 20);
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {TestPlaneDeflection.class.getName()});
    }
}
