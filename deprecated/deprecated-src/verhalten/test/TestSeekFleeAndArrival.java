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
 * testing the seek, flee and arrival behavior.
 */


package verhalten.test;


import gestalt.render.AnimatorRenderer;

import mathematik.Vector3f;

import verhalten.Arrival;
import verhalten.Engine;
import verhalten.Flee;
import verhalten.Seek;
import verhalten.Verhalten;
import verhalten.view.JoglArrivalView;
import verhalten.view.JoglEngineView;
import verhalten.view.JoglFleeView;
import verhalten.view.JoglSeekView;


public class TestSeekFleeAndArrival
    extends AnimatorRenderer {

    private Engine _mySeekEngine;

    private Engine _myFleeEngine;

    private Engine _myArriveEngine;

    private Seek _mySeek;

    private Flee _myFlee;

    private Arrival _myArrive;

    public void setup() {
        framerate(120);

        /* setup engine */
        _mySeekEngine = new Engine();
        _mySeekEngine.setMaximumForce(100.0f);
        _mySeekEngine.setMaximumSpeed(100.0f);
        _myFleeEngine = new Engine();
        _myFleeEngine.setMaximumForce(100.0f);
        _myFleeEngine.setMaximumSpeed(100.0f);
        _myArriveEngine = new Engine();
        _myArriveEngine.setMaximumForce(Verhalten.UNDEFINED);
        _myArriveEngine.setMaximumSpeed(300.0f);

        /* setup behavior */
        _mySeek = new Seek();
        _myFlee = new Flee();
        _myArrive = new Arrival();
        _myArrive.setBreakSpeed(1.0f);
        _myArrive.setOutterRadius(100);

        /* setup view */
        {
            JoglEngineView myView;
            myView = new JoglEngineView(_mySeekEngine);
            myView.material().color4f().set(1, 1, 1);
            myView.addBehavior(new JoglSeekView(_mySeek));
            bin(BIN_3D).add(myView);
        }
        {
            JoglEngineView myView;
            myView = new JoglEngineView(_myFleeEngine);
            myView.material().color4f().set(0.75f, 0.75f, 0.75f);
            myView.addBehavior(new JoglFleeView(_myFlee));
            bin(BIN_3D).add(myView);
        }
        {
            JoglEngineView myView;
            myView = new JoglEngineView(_myArriveEngine);
            myView.material().color4f().set(0.5f, 0.5f, 0.5f);
            myView.addBehavior(new JoglArrivalView(_myArrive));
            bin(BIN_3D).add(myView);
        }
    }


    public void loop(float theDeltaTime) {
        /* seek */
        {
            Vector3f mySeekDirection = new Vector3f();
            _mySeek.setPoint(new Vector3f(event().mouseX, event().mouseY));
            _mySeek.get(_mySeekEngine, mySeekDirection);
            _mySeekEngine.apply(theDeltaTime, mySeekDirection);
        }
        /* evade */
        {
            Vector3f myFleeDirection = new Vector3f();
            _myFlee.setPoint(new Vector3f(event().mouseX, event().mouseY));
            _myFlee.get(_myFleeEngine, myFleeDirection);
            _myFleeEngine.apply(theDeltaTime, myFleeDirection);
            if (_myFleeEngine.position().x > displaycapabilities().width / 2 ||
                _myFleeEngine.position().x < displaycapabilities().width / -2 ||
                _myFleeEngine.position().y > displaycapabilities().height / 2 ||
                _myFleeEngine.position().y < displaycapabilities().height / -2) {
                _myFleeEngine.position().set(0, 0, 0);
            }
        }
        /* arrive */
        {
            Vector3f myArriveDirection = new Vector3f();
            _myArrive.setPosition(new Vector3f(event().mouseX, event().mouseY));
            _myArrive.get(_myArriveEngine, myArriveDirection);
            _myArriveEngine.apply(theDeltaTime, myArriveDirection);
        }
    }


    public static void main(String[] args) {
        new TestSeekFleeAndArrival().init();
    }
}
