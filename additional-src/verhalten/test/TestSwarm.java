/*
 * Verhalten
 * a collection of simple behaviors
 *
 * Copyright (C) 2005 Dennis Paul
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

/*
 * test the cohesion, alignment and seperation behavior.
 */


package verhalten.test;


import gestalt.render.AnimatorRenderer;
import gestalt.shape.Cube;
import gestalt.util.CameraMover;

import mathematik.Vector3f;

import verhalten.Alignment;
import verhalten.Cohesion;
import verhalten.Containment;
import verhalten.Engine;
//import verhalten.OverlapRemover;
import verhalten.Rotation;
import verhalten.Separation;


public class TestSwarm
    extends AnimatorRenderer {

    private SwarmEntity[] _mySwarmEntities;

    public void setup() {
        /* setup entities */
        _mySwarmEntities = new SwarmEntity[50];
        for (int i = 0; i < _mySwarmEntities.length; i++) {
            _mySwarmEntities[i] = new SwarmEntity();
            _mySwarmEntities[i].position().set( (float) Math.random() * 100,
                                               (float) Math.random() * 100,
                                               (float) Math.random() * 100);
        }

        /* setup renderer */
        framerate(UNDEFINED);
        camera().fovy *= 2f;
        camera().setMode(CAMERA_MODE_LOOK_AT);
        camera().nearclipping = 3;
    }


    public void loop(float theDeltaTime) {
        for (int i = 0; i < _mySwarmEntities.length; i++) {
            _mySwarmEntities[i].loop(theDeltaTime);
        }
        /* move camera */
        // camera().position().set(_mySwarmEntities[0].position());
        // camera().lookat.set(_mySwarmEntities[1].position());
        CameraMover.handleKeyEvent(camera(), event(), theDeltaTime);
    }


    public static void main(String[] arg) {
        new TestSwarm().init();
    }


    private class SwarmEntity
        extends Engine {

        private static final long serialVersionUID = -6043927846991252641L;

        private Cube _myShape;

        private Rotation _myRotation;

        private Cohesion _myCohesion;

        private Containment _myContainment;

        private Separation _mySeparation;

        private Alignment _myAlignment;

//        private OverlapRemover _myOverlapRemover;

        private Vector3f[] _myDirections;

        private float[] _myDirectionsWeight;

        public SwarmEntity() {
            /* setup a cube */
            _myShape = drawablefactory().cube();
            _myShape.material().color.a = 0.125f;
            _myShape.material().wireframe = true;
            _myShape.setTransformRef(transform());
            _myShape.scale().set(15, 5, 5);
            _myShape.origin(SHAPE_ORIGIN_CENTERED);
            bin(BIN_3D).add(_myShape);

            /* setup behavior */
            setSpeed(100.0f);
            setMaximumForce(50.0f);
            setMaximumSpeed( (float) Math.random() * 20.0f + 30.0f);

            _myRotation = new Rotation(10);
            _myRotation.setUpVector(new Vector3f(0, 1, 0));

            _myContainment = new Containment();
            _myContainment.setSize(new Vector3f(600, 400, 30));

            _mySeparation = new Separation();
            _mySeparation.setPrivacyRadius(20);

            _myCohesion = new Cohesion();
            _myAlignment = new Alignment();

            _myDirectionsWeight = new float[] {0.2f, 0.3f, 0.25f, 0.25f};
            _myDirections = new Vector3f[_myDirectionsWeight.length];
            for (int i = 0; i < _myDirections.length; i++) {
                _myDirections[i] = new Vector3f();
            }

            /* remove overlapping vehicles */
//            _myOverlapRemover = new OverlapRemover();
            setBoundingRadius(10f);
        }


        public void loop(float theDeltaTime) {
            /* calculate new directions */
            _mySeparation.get(_mySwarmEntities, position(), _myDirections[1]);

            if (_myDirections[1].lengthSquared() == 0) {
                _myCohesion.get(_mySwarmEntities, this, _myDirections[0]);
                _myAlignment.get(_mySwarmEntities, _myDirections[2]);
                _myContainment.get(position(), _myDirections[3]);
                apply(theDeltaTime, _myDirections, _myDirectionsWeight);
            } else {
                apply(theDeltaTime, _myDirections[1]);
            }

            _myRotation.setRotationMatrix(velocity(), transform());

            /* remove overlap */
//            _myOverlapRemover.remove(this, _mySwarmEntities);
        }
    }
}
