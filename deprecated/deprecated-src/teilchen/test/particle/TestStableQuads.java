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


import mathematik.Vector3f;

import teilchen.Physics;
import teilchen.force.Gravity;
import teilchen.force.ViscousDrag;
import teilchen.integration.RungeKutta;
import teilchen.util.DrawLib;
import teilchen.util.StableSpringQuad;
import processing.core.PApplet;


public class TestStableQuads
    extends PApplet {

    private Physics _myParticleSystem;

    private StableSpringQuad _myQuad;

    public void setup() {
        size(640, 480, OPENGL);
        smooth();
        frameRate(120);

        _myParticleSystem = new Physics();
        _myParticleSystem.setInegratorRef(new RungeKutta());

        Gravity myGravity = new Gravity();
        myGravity.force().y = 10;
        _myParticleSystem.add(myGravity);

        ViscousDrag myViscousDrag = new ViscousDrag();
        myViscousDrag.coefficient = 1f;
        _myParticleSystem.add(myViscousDrag);

        _myQuad = new StableSpringQuad(_myParticleSystem,
                                       new Vector3f(width * 0.33f, height * 0.33f),
                                       new Vector3f(width * 0.66f, height * 0.33f),
                                       new Vector3f(width * 0.66f, height * 0.66f),
                                       new Vector3f(width * 0.33f, height * 0.66f));
        _myQuad.a.fixed(true);
    }


    public void draw() {

        /* handle particles */
        _myQuad.a.position().set(mouseX, mouseY);
        _myParticleSystem.step(1f / 120);

        /* draw */
        background(255);

        /* draw springs */
        DrawLib.drawSprings(g, _myParticleSystem, color(0, 64));

        /* draw particles */
        DrawLib.drawParticles(g, _myParticleSystem, 5, color(0, 127));
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {TestStableQuads.class.getName()});
    }
}
