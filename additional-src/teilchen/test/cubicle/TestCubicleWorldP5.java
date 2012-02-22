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


package teilchen.test.cubicle;


import teilchen.cubicle.CubicleEntity;
import teilchen.cubicle.CubicleWorld;
import teilchen.util.P5CubicleWorldView;
import processing.core.PApplet;


/**
 * this version is still buggy :(
 */

public class TestCubicleWorldP5
    extends PApplet {

    private CubicleWorld _myWorld;

    private CubicleEntity[] _myEntity;

    private P5CubicleWorldView _myView;

    private int _myCurrentEntity;

    private float _myRotation;

    public void setup() {
        size(640, 480, P3D);
        noFill();

        /* setup world */
        _myWorld = new CubicleWorld(10, 10, 2);
        _myWorld.cellscale().set(20, 20, 20);
        _myWorld.transform().translation.set(width / 2, height / 2);
        _myView = new P5CubicleWorldView(_myWorld);

        /* setup entity */
        _myEntity = new CubicleEntity[3];
        for (int i = 0; i < _myEntity.length; i++) {
            _myEntity[i] = new CubicleEntity();
            _myWorld.add(_myEntity[i]);
        }
    }


    public void draw() {

        background(50);
        _myView.draw(this);

        /* handle entities */
        _myEntity[_myCurrentEntity].position().set(mouseX, mouseY);

        /* handle world */
        _myWorld.update();

        /* handle input */
        final float myDelta = 1 / 30f;
        if (keyPressed) {
            if (key == 'a') {
                _myRotation += myDelta;
                _myWorld.transform().rotation.setZRotation(_myRotation);
            }
            if (key == 'd') {
                _myRotation -= myDelta;
                _myWorld.transform().rotation.setZRotation(_myRotation);
            }
            if (key == 's') {
                _myRotation += myDelta;
                _myWorld.transform().rotation.setXRotation(_myRotation);
            }
            if (key == 'w') {
                _myRotation -= myDelta;
                _myWorld.transform().rotation.setXRotation(_myRotation);
            }
            if (key == 'A') {
                _myWorld.transform().translation.x -= myDelta * 20;
            }
            if (key == 'D') {
                _myWorld.transform().translation.x += myDelta * 20;
            }
            if (key == 'S') {
                _myWorld.transform().translation.y -= myDelta * 20;
            }
            if (key == 'W') {
                _myWorld.transform().translation.y += myDelta * 20;
            }
        }
    }


    public void mousePressed() {
        _myCurrentEntity++;
        _myCurrentEntity %= _myEntity.length;
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {TestCubicleWorldP5.class.getName()});
    }
}
