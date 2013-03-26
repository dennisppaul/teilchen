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


package teilchen.test.cubicle;


import gestalt.render.AnimatorRenderer;
import gestalt.shape.AbstractShape;

import mathematik.Vector3f;
import mathematik.Vector3i;

import teilchen.cubicle.CubicleWorld;
import teilchen.cubicle.ICubicleEntity;
import teilchen.gestalt.util.JoglCubicleWorldView;


/*
 * test the cubicle world concept
 */

public class TestCubicleWorld
    extends AnimatorRenderer {

    private CubicleWorld _myWorld;

    private MyEntity[] _myEntity;

    private int _myCurrentEntity;

    private float _myRotation;

    public void setup() {
        /* setup world */
        _myWorld = new CubicleWorld(10, 10, 2);
        _myWorld.cellscale().set(20, 20, 20);
        bin(BIN_3D).add(new JoglCubicleWorldView(_myWorld));

        /* setup entity */
        _myEntity = new MyEntity[3];
        for (int i = 0; i < _myEntity.length; i++) {
            _myEntity[i] = new MyEntity(drawablefactory().plane());
            bin(BIN_3D).add(_myEntity[i].getView());
            _myWorld.add(_myEntity[i]);
        }

        /* setup renderer */
        framerate(10);
    }


    public void loop(float theDeltaTime) {
        /* handle entities */
        if (event().mouseClicked) {
            _myCurrentEntity++;
            _myCurrentEntity %= _myEntity.length;
        }
        _myEntity[_myCurrentEntity].position().set(event().mouseX, event().mouseY);

        /* handle world */
        _myWorld.update();
        _myWorld.getEntities();

        /* handle neighbors */
        final int myNumberOfNeighbors = _myWorld.getLocalEntities(_myEntity[_myCurrentEntity]).size();
        if (myNumberOfNeighbors > 1) {
            System.out.println("neighbors (including self): " + myNumberOfNeighbors);
        }

        /* handle input */
        if (event().keyDown) {
            if (event().shift) {
                if (event().keyCode == KEYCODE_LEFT) {
                    _myWorld.transform().translation.x -= theDeltaTime * 20;
                }
                if (event().keyCode == KEYCODE_RIGHT) {
                    _myWorld.transform().translation.x += theDeltaTime * 20;
                }
                if (event().keyCode == KEYCODE_DOWN) {
                    _myWorld.transform().translation.y -= theDeltaTime * 20;
                }
                if (event().keyCode == KEYCODE_UP) {
                    _myWorld.transform().translation.y += theDeltaTime * 20;
                }
            } else {
                if (event().keyCode == KEYCODE_LEFT) {
                    _myRotation += theDeltaTime;
                    _myWorld.transform().rotation.setZRotation(_myRotation);
                }
                if (event().keyCode == KEYCODE_RIGHT) {
                    _myRotation -= theDeltaTime;
                    _myWorld.transform().rotation.setZRotation(_myRotation);
                }
                if (event().keyCode == KEYCODE_UP) {
                    _myRotation += theDeltaTime;
                    _myWorld.transform().rotation.setXRotation(_myRotation);
                }
                if (event().keyCode == KEYCODE_DOWN) {
                    _myRotation -= theDeltaTime;
                    _myWorld.transform().rotation.setXRotation(_myRotation);
                }
            }
        }
    }


    public static void main(String[] args) {
        new TestCubicleWorld().init();
    }


    private class MyEntity
        implements ICubicleEntity {

        private AbstractShape _myView;

        private Vector3i _myCubiclePosition;

        public MyEntity(AbstractShape theView) {
            _myCubiclePosition = new Vector3i();
            _myView = theView;
            _myView.scale().set(20, 20, 20);
            _myView.material().wireframe = true;
        }


        public AbstractShape getView() {
            return _myView;
        }


        public Vector3i cubicle() {
            return _myCubiclePosition;
        }


        public Vector3f position() {
            return _myView.position();
        }


        public boolean leaving(int theX, int theY, int theZ) {
            if (theX == cubicle().x &&
                theY == cubicle().y &&
                theZ == cubicle().z) {
                return false;
            }
            return true;
        }


        public boolean isActive() {
            return true;
        }
    }
}
