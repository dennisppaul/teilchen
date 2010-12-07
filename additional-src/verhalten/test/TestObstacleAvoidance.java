/*
 * Verhalten
 *
 * Copyright (C) 2005 Patrick Kochlik + Dennis Paul
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
 * testing the obstacle avoidance behavior.
 */


package verhalten.test;


import gestalt.render.AnimatorRenderer;
import gestalt.util.CameraMover;

import mathematik.TransformMatrix4f;
import mathematik.Vector3f;

import verhalten.Engine;
import verhalten.IVerhaltenEntity;
import verhalten.ObstacleAvoidance;
//import verhalten.OverlapRemover;
import verhalten.Rotation;
import verhalten.Wander;
import verhalten.view.JoglEngineView;
import verhalten.view.JoglEntityView;
import verhalten.view.JoglObstacleAvoidanceView;
import gestalt.context.DisplayCapabilities;


public class TestObstacleAvoidance
    extends AnimatorRenderer {

    private AnObstacle[] _myObstacles;

    private Engine[] _myEngine;

    private Rotation[] _myRotation;

    private Wander[] _myWander;

    private ObstacleAvoidance[] _myObstacleAvoidance;

    public void setup() {
        /* setup renderer */
        framerate(60);
        displaycapabilities().backgroundcolor.set(1);
        camera().setMode(CAMERA_MODE_LOOK_AT);
        camera().position().set(10, -510, 405);

        /* setup obstacles */
        _myObstacles = new AnObstacle[20];
        for (int i = 0; i < _myObstacles.length; i++) {
            _myObstacles[i] = new AnObstacle();
            JoglEntityView myJoglEntityView = new JoglEntityView(_myObstacles[i]);
            myJoglEntityView.material().color.set(0);
            bin(BIN_3D).add(myJoglEntityView);
        }

        /* remove obstacle overlap */
//        OverlapRemover myOverlapRemover = new OverlapRemover();
        for (int i = 0; i < _myObstacles.length; i++) {
//            myOverlapRemover.remove(_myObstacles[i], _myObstacles);
        }

        /* setup engine */
        _myEngine = new Engine[3];
        _myRotation = new Rotation[_myEngine.length];
        _myWander = new Wander[_myEngine.length];
        _myObstacleAvoidance = new ObstacleAvoidance[_myEngine.length];

        for (int i = 0; i < _myEngine.length; i++) {
            /* setup engine */
            _myEngine[i] = new Engine();
            _myEngine[i].setMaximumForce(100.0f);
            _myEngine[i].setMaximumSpeed(100.0f);
            _myEngine[i].setBoundingRadius(20.0f);
            /* setup rotation */
            _myRotation[i] = new Rotation(10);
            /* setup behavior */
            _myObstacleAvoidance[i] = new ObstacleAvoidance();
            _myWander[i] = new Wander();
            _myWander[i].setRadius(20);
            /* setup view */
            JoglEngineView myView;
            myView = new JoglEngineView(_myEngine[i]);
            myView.addBehavior(new JoglObstacleAvoidanceView(_myObstacleAvoidance[i]));
            myView.material().color.set(0);
            bin(BIN_3D).add(myView);
        }
    }


    public void loop(float theDeltaTime) {
        if (event().keyPressed && event().key == ' ') {
            System.out.println(camera());
        }

        CameraMover.handleKeyEvent(camera(), event(), theDeltaTime);

        for (int i = 0; i < _myEngine.length; i++) {
            /* obstacle avoid */
            Vector3f myAvoidDirection = new Vector3f();
            _myObstacleAvoidance[i].get(_myEngine[i], _myObstacles, myAvoidDirection);

            /* wander */
            if (myAvoidDirection.lengthSquared() == 0) {
                Vector3f myWanderDirection = new Vector3f();
                _myWander[i].get(_myEngine[i], theDeltaTime, myWanderDirection);
                myAvoidDirection.add(myWanderDirection);
            }

            /* constant speed */
            Vector3f myForwardDirection = new Vector3f(_myEngine[i].velocity());
            myForwardDirection.normalize();
            myForwardDirection.scale(10);
            myAvoidDirection.add(myForwardDirection);

            /* apply */
            _myEngine[i].apply(theDeltaTime, myAvoidDirection);

            /* rotation */
            _myRotation[i].setRotationMatrix(_myEngine[i].velocity(), _myEngine[i].transform());

            /* teleport */
            if (_myEngine[i].position().x > displaycapabilities().width / 2) {
                _myEngine[i].position().x -= displaycapabilities().width;
            }
            if (_myEngine[i].position().x < displaycapabilities().width / -2) {
                _myEngine[i].position().x += displaycapabilities().width;
            }
            if (_myEngine[i].position().y > displaycapabilities().height / 2) {
                _myEngine[i].position().y -= displaycapabilities().height;
            }
            if (_myEngine[i].position().y < displaycapabilities().height / -2) {
                _myEngine[i].position().y += displaycapabilities().height;
            }
        }
    }


    private class AnObstacle
        implements IVerhaltenEntity {

        private TransformMatrix4f _myTransform;

        private float _myRadius;

        private boolean _myIsTagged;

        public AnObstacle() {
            _myTransform = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
            _myRadius = 20 + (float) Math.random() * 20;
            _myTransform.translation.set(displaycapabilities().width * Math.random() -
                                         displaycapabilities().width * 0.5f,
                                         displaycapabilities().height * Math.random() -
                                         displaycapabilities().height * 0.5f);
            _myIsTagged = false;
        }


        public Vector3f position() {
            return _myTransform.translation;
        }


        public Vector3f velocity() {
            return null;
        }


        public TransformMatrix4f transform() {
            return _myTransform;
        }


        public float getBoundingRadius() {
            return _myRadius;
        }


        public boolean isTagged() {
            return _myIsTagged;
        }


        public void setTag(boolean theTag) {
            _myIsTagged = theTag;
        }

    }

    public DisplayCapabilities createDisplayCapabilities() {
        DisplayCapabilities myDisplayCapabilities = new DisplayCapabilities();
        myDisplayCapabilities.antialiasinglevel = 4;
        return myDisplayCapabilities;
    }

    public static void main(String[] args) {
        new TestObstacleAvoidance().init();
    }
}
