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
 * testing the wandering behavior.
 */


package verhalten.test;


import gestalt.render.AnimatorRenderer;

import mathematik.Vector3f;

import verhalten.Engine;
import verhalten.Wander;
import verhalten.view.JoglEngineView;
import verhalten.view.JoglWanderView;


public class TestWander
    extends AnimatorRenderer {

    private Engine _myWanderEngine;

    private Wander _myWander;

    public void setup() {
        framerate(120);

        /* setup an engine */
        _myWanderEngine = new Engine();
        _myWanderEngine.setMaximumForce(100.0f);
        _myWanderEngine.setMaximumSpeed(20.0f);

        /* setup behavior */
        _myWander = new Wander();
        _myWander.setRadius(100);

        /* setup view */
        JoglEngineView myView;
        myView = new JoglEngineView(_myWanderEngine);
        myView.material().color.set(1, 1, 1);
        myView.addBehavior(new JoglWanderView(_myWander));
        bin(BIN_3D).add(myView);
    }


    public void loop(float theDeltaTime) {
        /* wander */
        Vector3f myWanderDirection = new Vector3f();
        _myWander.get(_myWanderEngine, theDeltaTime, myWanderDirection);
        _myWanderEngine.apply(theDeltaTime, myWanderDirection);
        /* teleport */
        if (_myWanderEngine.position().x > displaycapabilities().width / 2 ||
            _myWanderEngine.position().x < displaycapabilities().width / -2 ||
            _myWanderEngine.position().y > displaycapabilities().height / 2 ||
            _myWanderEngine.position().y < displaycapabilities().height / -2) {
            _myWanderEngine.position().set(0, 0, 0);
        }
        /* set direction */
        if (event().mouseDown) {
            if (event().shift) {
                _myWanderEngine.velocity().set(_myWanderEngine.getMaximumSpeed(), 0, 0);
                _myWander.getTarget().set(_myWanderEngine.velocity());
            } else {
                _myWanderEngine.velocity().set( -_myWanderEngine.getMaximumSpeed(), 0, 0);
                _myWander.getTarget().set(_myWanderEngine.velocity());
            }
        }
    }


    public static void main(String[] args) {
        new TestWander().init();
    }
}
