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


import gestalt.G;
import gestalt.model.Model;
import gestalt.render.AnimatorRenderer;

import mathematik.Random;

import data.Resource;
import teilchen.ConditionalParticle;
import teilchen.Physics;
import teilchen.force.Gravity;
import teilchen.force.ViscousDrag;
import teilchen.gestalt.util.GestaltDrawLib;
import teilchen.gestalt.util.GestaltDrawLib.GestaltParticleViewLine;


public class TestDynamicModelReflection
    extends AnimatorRenderer {

    private Physics _myParticleSystem;

    private Model _myModel;

    public void setup() {
        fpscounter(true);
        cameramover(true);
        camera().setMode(CAMERA_MODE_LOOK_AT);
        camera().position().set( -533.14844, 331.7907, 506.69937);

        displaycapabilities().backgroundcolor.set(0.2f);
        light().enable = true;
        light().setPositionRef(camera().position());

        /* particle system */
        _myParticleSystem = new Physics();

        /* point sprites */
        final GestaltParticleViewLine myView = new GestaltParticleViewLine(_myParticleSystem.particles());
        G.add(myView);

        /* load model */
        _myModel = G.model(Resource.getStream("landscape.obj"));
        _myModel.mesh().material().lit = true;

        System.out.println("### INFO / creating " + (_myModel.mesh().vertices().length / 9) + " deflectors.");
        _myParticleSystem.addForces(teilchen.util.Util.createTriangleDeflectorsIndexed(_myModel.mesh().vertices(),
                                                                                        0.0f));

        /* forces */
        final Gravity myGravity = new Gravity();
        myGravity.force().y = -50;
        _myParticleSystem.add(myGravity);

        final ViscousDrag myViscousDrag = new ViscousDrag();
        myViscousDrag.coefficient = 0.1f;
        _myParticleSystem.add(myViscousDrag);
    }


    public void loop(final float theDeltaTime) {
        _myParticleSystem.step(theDeltaTime);

        final float myOffset = theDeltaTime * 10;
        for (int i = 0; i < _myModel.mesh().vertices().length / 4; i++) {
            _myModel.mesh().vertices()[i] += new Random().getFloat( -myOffset, myOffset);
        }

        /* -- */
        if (event().mouseDown) {
            spawnParticle();
        }
    }


    private void spawnParticle() {
        final ConditionalParticle myParticle = new ConditionalParticle() {
            public boolean condition() {
                return position().y > 0;
            }
        };
        _myParticleSystem.add(myParticle);
        myParticle.position().set(event().mouseX,
                                  height / 2,
                                  event().mouseY * -1);
    }


    public static void main(String[] args) {
        G.init(TestDynamicModelReflection.class); ;
    }
}
