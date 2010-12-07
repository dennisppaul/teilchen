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

import mathematik.Vector3f;

import data.Resource;
import teilchen.ConditionalParticle;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.force.Gravity;
import teilchen.force.ViscousDrag;
import teilchen.gestalt.util.GestaltDrawLib;
import teilchen.gestalt.util.GestaltDrawLib.GestaltParticleViewLine;


public class TestParticlesDeformingModel
    extends AnimatorRenderer {

    private Physics _myParticleSystem;

    private Model _myModel;

    private final float _myDripInterval = 1.0f;

    private float _myDripCounter;

    public void setup() {
        framerate(UNDEFINED);
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
        _myModel = G.model(Resource.getStream("rotated-cube.obj"));
        _myModel.mesh().material().lit = true;
        _myModel.mesh().material().color.set(0.25f, 0.75f, 1.0f);

        System.out.println("### INFO / creating " + (_myModel.mesh().vertices().length / 9) + " deflectors.");
        _myParticleSystem.addForces(teilchen.util.Util.createTriangleDeflectorsIndexed(_myModel.mesh().vertices(),
                                                                                        0.0f));

        /* forces */
        final Gravity myGravity = new Gravity();
        myGravity.force().y = -10;
        _myParticleSystem.add(myGravity);

        final ViscousDrag myViscousDrag = new ViscousDrag();
        myViscousDrag.coefficient = 0.1f;
        _myParticleSystem.add(myViscousDrag);
    }


    public void loop(final float theDeltaTime) {
        _myParticleSystem.step(theDeltaTime);

        _myDripCounter += theDeltaTime;
        if (_myDripCounter > _myDripInterval) {
            _myDripCounter = 0;
            spawnParticle();
            mathematik.Util.createNormals(_myModel.mesh().vertices(), _myModel.mesh().normals());
        }

        /* deform vertices within radius */
        final float myDeformStrength = 5;
        for (Particle myParticle : _myParticleSystem.particles()) {
            for (int i = 0; i < _myModel.mesh().vertices().length; i += 3) {
                final Vector3f myVertex = new Vector3f(_myModel.mesh().vertices()[i + 0],
                                                       _myModel.mesh().vertices()[i + 1],
                                                       _myModel.mesh().vertices()[i + 2]);
                final float myDistanceSquared = myParticle.position().distanceSquared(myVertex);
                if (myDistanceSquared < myParticle.radius() * myParticle.radius()) {
                    final float myDistance = (float) Math.sqrt(myDistanceSquared);
                    myVertex.y -= theDeltaTime * (myDistance / myParticle.radius()) * myDeformStrength;
                    _myModel.mesh().vertices()[i + 1] = myVertex.y;
                }
            }
        }
    }


    private void spawnParticle() {
        final ConditionalParticle myParticle = new ConditionalParticle() {
            public boolean condition() {
                return position().y > 0;
            }
        };
        myParticle.radius(5.0f);
        _myParticleSystem.add(myParticle);
        myParticle.position().set(event().mouseX,
                                  height / 2,
                                  event().mouseY * -1);
    }


    public void keyPressed(char theKey, int theKeyCode) {
        if (theKey == 'n') {
            mathematik.Util.createNormals(_myModel.mesh().vertices(), _myModel.mesh().normals());
        }
    }


    public static void main(String[] args) {
        G.init(TestParticlesDeformingModel.class); ;
    }
}
