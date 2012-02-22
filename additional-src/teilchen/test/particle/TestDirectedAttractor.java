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
import teilchen.ShortLivedParticle;
import teilchen.force.DirectedAttractor;
import teilchen.force.IForce;
import teilchen.force.ViscousDrag;
import processing.core.PApplet;


public class TestDirectedAttractor
    extends PApplet {

    private Physics _myParticleSystem;

    public void setup() {
        size(640, 480, OPENGL);
        frameRate(120);

        _myParticleSystem = new Physics();

        /* forces */
        ViscousDrag myViscousDrag = new ViscousDrag();
        myViscousDrag.coefficient = 0.15f;
        _myParticleSystem.add(myViscousDrag);
    }


    public synchronized void draw() {
        /* physics */
        _myParticleSystem.step(1f / frameRate);

        if (keyPressed) {
            {
                for (int i = 0; i < _myParticleSystem.forces().size(); i++) {
                    if (_myParticleSystem.forces().get(i) instanceof DirectedAttractor) {
                        _myParticleSystem.forces().remove(i);
                    }
                }
            }
        }

        /* draw */
        background(255);

        noStroke();
        fill(0, 255, 127, 64);
        for (IForce myForce : _myParticleSystem.forces()) {
            if (myForce instanceof DirectedAttractor) {
                DirectedAttractor myAttractor = (DirectedAttractor) myForce;
                ellipse(myAttractor.position().x, myAttractor.position().y,
                        myAttractor.radius() * 2, myAttractor.radius() * 2);
            }
        }

        stroke(0, 64);
        noFill();
        for (Particle myParticle : _myParticleSystem.particles()) {
            ellipse(myParticle.position().x, myParticle.position().y, 10, 10);
        }
    }


    public void mouseMoved() {
        /* create particles */
        ShortLivedParticle myParticle = _myParticleSystem.makeParticle(ShortLivedParticle.class);
        myParticle.setMaxAge(10);
        myParticle.position().set(mouseX, mouseY);
        myParticle.velocity().x = (mouseX - pmouseX) * 20;
        myParticle.velocity().y = (mouseY - pmouseY) * 20;
    }


    public void mousePressed() {
        /* create sphere */
        DirectedAttractor _myForceSphere = new DirectedAttractor();
        _myForceSphere.radius(100);
        _myForceSphere.strength(500);
        _myForceSphere.position().set(mouseX, mouseY);
        _myParticleSystem.add(_myForceSphere);
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {TestDirectedAttractor.class.getName()});
    }
}
