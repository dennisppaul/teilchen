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


import teilchen.Physics;
import teilchen.ShortLivedParticle;
import teilchen.force.Attractor;
import teilchen.force.Gravity;
import teilchen.force.ViscousDrag;
import teilchen.util.P5DrawLib;
import processing.core.PApplet;


public class TestAttractors
    extends PApplet {

    private Physics _myParticleSystem;

    public void setup() {
        size(640, 480, OPENGL);
        frameRate(120);

        _myParticleSystem = new Physics();

        /* forces */
        ViscousDrag myViscousDrag = new ViscousDrag();
        myViscousDrag.coefficient = 0.5f;
        _myParticleSystem.add(myViscousDrag);

        Gravity myGravity = new Gravity();
        myGravity.force().y = 20;
        _myParticleSystem.add(myGravity);
    }


    public void draw() {
        /* physics */
        _myParticleSystem.step(1f / frameRate);

        /* create particles */
        {
            ShortLivedParticle myParticle = _myParticleSystem.makeParticle(ShortLivedParticle.class);
            myParticle.setMaxAge(10);
            myParticle.position().set(mouseX + random( -50, 50), mouseY, random( -50, 50));
            myParticle.velocity().y = 20;
        }

        /* draw */
        background(255);
        P5DrawLib.drawAttractor(g, _myParticleSystem.forces(), color(0, 255, 0, 64));
        P5DrawLib.draw(g, _myParticleSystem.particles(), 10, color(0, 127));
    }


    public void mousePressed() {
        /* create sphere */
        Attractor _myForceSphere = new Attractor();
        _myForceSphere.radius(random(50) + 50);
        _myForceSphere.strength(random(40 + 25));
        _myForceSphere.position().set(mouseX, mouseY);
        _myParticleSystem.add(_myForceSphere);
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {TestAttractors.class.getName()});
    }

}
