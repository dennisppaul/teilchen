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
import teilchen.ShortLivedParticle;
import teilchen.force.Gravity;
import teilchen.force.TriangleDeflector;
import teilchen.force.ViscousDrag;
import teilchen.util.P5DrawLib;
import processing.core.PApplet;


public class TestTriangleDeflection
    extends PApplet {

    private Physics _myParticleSystem;

    private TriangleDeflector _myTriangleDeflector;

    private float _myYRotation;

    public void setup() {
        size(640, 480, OPENGL);
        frameRate(120);
        colorMode(RGB, 1.0f);
        noFill();

        /* particle system */
        _myParticleSystem = new Physics();

        /* ground plane */
        _myTriangleDeflector = new TriangleDeflector();
        _myTriangleDeflector.a().set(0, height * 1.0f, 250);
        _myTriangleDeflector.c().set(width, height * 0.75f, 300);
        _myTriangleDeflector.b().set(width * 0.5f, height * 0.5f, -200);
        _myTriangleDeflector.coefficientofrestitution(0.75f);
        _myParticleSystem.add(_myTriangleDeflector);

        Gravity myGravity = new Gravity();
        myGravity.force().y = 50;
        _myParticleSystem.add(myGravity);

        ViscousDrag myViscousDrag = new ViscousDrag();
        myViscousDrag.coefficient = 0.1f;
        _myParticleSystem.add(myViscousDrag);
    }


    public void keyPressed() {
        if (key == ',') {
            _myYRotation += 0.1f;
        }
        if (key == '.') {
            _myYRotation -= 0.1f;
        }
    }


    public void draw() {
        _myParticleSystem.step(1f / frameRate);

        /* -- */
        if (mousePressed) {
            ShortLivedParticle myParticle = _myParticleSystem.makeParticle(ShortLivedParticle.class);
            myParticle.velocity().x = (mouseX - pmouseX) * 25 + random( -10, 10);
            myParticle.velocity().y = 50;
            myParticle.setMaxAge(10);
            myParticle.position().set(mouseX, mouseY, random( -200, 200));
        }

        /* draw particles */
        _myYRotation += 0.0025f;
        translate(width / 2, 0, -500);
        rotateY(_myYRotation);
        translate( -width / 2, 0, 0);
        background(1);

        for (Particle myParticle : _myParticleSystem.particles()) {
            final float myAgeRatio = 1 - ( (ShortLivedParticle) myParticle).ageRatio();
            stroke(0, 0.5f);
            pushMatrix();
            translate(myParticle.position().x, myParticle.position().y, myParticle.position().z);
            ellipse(0, 0, myAgeRatio * 15, myAgeRatio * 15);
            popMatrix();
        }

        /* draw plane */
        P5DrawLib.draw(g, _myTriangleDeflector, color(0, 0.5f), color(1, 0, 0, 0.25f), color(1, 1, 0, 0.5f));
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {TestTriangleDeflection.class.getName()});
    }
}
