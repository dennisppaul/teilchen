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
 * testing the wall avoidance behavior.
 */


package verhalten.test;


import java.util.Vector;

import gestalt.render.AnimatorRenderer;
import gestalt.util.CameraMover;

import mathematik.Util;
import mathematik.Vector3f;

import verhalten.Engine;
import verhalten.IVerhaltenWall;
import verhalten.Rotation;
import verhalten.WallAvoidance;
import verhalten.Wander;
import verhalten.view.JoglEngineView;
import verhalten.view.JoglWallAvoidanceView;
import verhalten.view.JoglWallView;
import verhalten.view.JoglWanderView;


public class TestWallAvoidance
    extends AnimatorRenderer {

    private Vector<IVerhaltenWall> _myWalls;

    private Engine[] _myEngine;

    private Rotation[] _myRotation;

    private Wander[] _myWander;

    private WallAvoidance[] _myWallAvoidance;

    public void setup() {
        /* setup renderer */
        framerate(30);
        camera().setMode(CAMERA_MODE_LOOK_AT);

        /* setup obstacles */
        _myWalls = new Vector<IVerhaltenWall> ();

        AWall myWall;
        /* wall 1 */
        myWall = new AWall();
        myWall.pointA().set(200, 320, -50);
        myWall.pointB().set(200, -320, -50);
        myWall.pointC().set(200, -320, 50);
        Util.calculateNormal(myWall.pointA(), myWall.pointB(), myWall.pointC(), myWall.normal());
        _myWalls.add(myWall);
        bin(BIN_3D).add(new JoglWallView(myWall));

        /* wall 1 */
        myWall = new AWall();
        myWall.pointA().set(200, -320, 50);
        myWall.pointB().set(200, 320, 50);
        myWall.pointC().set(200, 320, -50);
        Util.calculateNormal(myWall.pointA(), myWall.pointB(), myWall.pointC(), myWall.normal());
        _myWalls.add(myWall);
        bin(BIN_3D).add(new JoglWallView(myWall));

        /* setup engine */
        _myEngine = new Engine[10];
        _myRotation = new Rotation[_myEngine.length];
        _myWander = new Wander[_myEngine.length];
        _myWallAvoidance = new WallAvoidance[_myEngine.length];

        for (int i = 0; i < _myEngine.length; i++) {
            /* setup engine */
            _myEngine[i] = new Engine();
            _myEngine[i].setMaximumForce(100.0f);
            _myEngine[i].setMaximumSpeed(100.0f);
            _myEngine[i].setBoundingRadius(20.0f);
            /* setup rotation */
            _myRotation[i] = new Rotation(10);
            /* setup behavior */
            _myWallAvoidance[i] = new WallAvoidance();
            _myWander[i] = new Wander();
            _myWander[i].setRadius(50);
            _myWander[i].setSteeringStrength(500);
            /* setup view */
            JoglEngineView myView;
            myView = new JoglEngineView(_myEngine[i]);
            myView.addBehavior(new JoglWallAvoidanceView(_myWallAvoidance[i]));
            myView.addBehavior(new JoglWanderView(_myWander[i]));
            myView.material().color.set(1, 1, 1);
            bin(BIN_3D).add(myView);
        }
    }


    public void loop(final float theDeltaTime) {
        if (event().keyPressed && event().key == ' ') {
            System.out.println(camera());
        }

        CameraMover.handleKeyEvent(camera(), event(), theDeltaTime);

        for (int i = 0; i < _myEngine.length; i++) {
            /* obstacle avoid */
            Vector3f myAvoidDirection = new Vector3f();
            boolean myIsAvoiding = _myWallAvoidance[i].get(_myEngine[i], _myWalls, myAvoidDirection);

            /* wander */
            if (!myIsAvoiding) {
                Vector3f myWanderDirection = new Vector3f();
                _myWander[i].get(_myEngine[i], theDeltaTime, myWanderDirection);
                myAvoidDirection.add(myWanderDirection);
            }

            /* apply */
            _myEngine[i].apply(theDeltaTime, myAvoidDirection);
            _myEngine[i].velocity().z = 0;
            _myEngine[i].position().z = 0;

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


    private class AWall
        implements IVerhaltenWall {

        private Vector3f[] _myPoints;

        public AWall() {
            _myPoints = new Vector3f[4];
            for (int i = 0; i < _myPoints.length; i++) {
                _myPoints[i] = new Vector3f();
            }
        }


        public Vector3f pointA() {
            return _myPoints[0];
        }


        public Vector3f pointB() {
            return _myPoints[1];
        }


        public Vector3f pointC() {
            return _myPoints[2];
        }


        public Vector3f normal() {
            return _myPoints[3];
        }
    }


    public static void main(String[] args) {
        new TestWallAvoidance().init();
    }
}
