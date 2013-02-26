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
 * testing the separation behavior.
 */


package verhalten.test;


import gestalt.render.AnimatorRenderer;

import mathematik.Vector3f;

import verhalten.Engine;
import verhalten.IVerhaltenEntity;
import verhalten.Seek;
import verhalten.Separation;
import verhalten.Wander;
import verhalten.view.JoglEngineView;
import verhalten.view.JoglSeekView;
import verhalten.view.JoglSeparationView;


public class TestSeparation
    extends AnimatorRenderer {

    private Engine _mySeparatorEngine;

    private Engine _mySeekerEngine;

    private Separation _mySeparation;

    private Seek _mySeek;

    private Wander _myWander;

    public void setup() {
        framerate(60);

        /* setup engine */
        _mySeparatorEngine = new Engine();
        _mySeparatorEngine.setMaximumForce(250.0f);
        _mySeparatorEngine.setMaximumSpeed(70.0f);
        _mySeparatorEngine.velocity().set(Math.random() - 0.5f,
                                          Math.random() - 0.5f);
        _mySeparatorEngine.position().x = displaycapabilities().width * 0.66f;

        _mySeekerEngine = new Engine();
        _mySeekerEngine.setMaximumForce(120.0f);
        _mySeekerEngine.setMaximumSpeed(110.0f);
        _mySeekerEngine.velocity().set(Math.random() - 0.5f,
                                       Math.random() - 0.5f);
        _mySeekerEngine.position().x = displaycapabilities().width * 0.33f;

        /* setup behavior */
        _mySeparation = new Separation();
        _mySeparation.setPrivacyRadius(200);
        _myWander = new Wander();
        _myWander.setRadius(10);

        _mySeek = new Seek();

        /* setup view */
        {
            JoglEngineView myView;
            myView = new JoglEngineView(_mySeparatorEngine);
            myView.material().color4f().set(1, 1, 1);
            myView.addBehavior(new JoglSeparationView(_mySeparation));
            bin(BIN_3D).add(myView);
        }
        {
            JoglEngineView myView;
            myView = new JoglEngineView(_mySeekerEngine);
            myView.material().color4f().set(0, 0.5f, 1);
            myView.addBehavior(new JoglSeekView(_mySeek));
            bin(BIN_3D).add(myView);
        }
    }


    public void loop(float theDeltaTime) {

        /* separate */
        {
            Vector3f mySeparation = new Vector3f();
            Vector3f myWanderDirection = new Vector3f();
            _mySeparation.get(new IVerhaltenEntity[] {_mySeekerEngine},
                              _mySeparatorEngine.position(),
                              mySeparation);
            if (mySeparation.lengthSquared() == 0) {
                _myWander.get(_mySeparatorEngine, theDeltaTime, myWanderDirection);
            }

            mySeparation.add(myWanderDirection);

            _mySeparatorEngine.apply(theDeltaTime, mySeparation);
            teleport(_mySeparatorEngine);
        }
        /* seek */
        {
            Vector3f mySeparation = new Vector3f();
            _mySeek.setPoint(_mySeparatorEngine.position());
            _mySeek.get(_mySeekerEngine,
                        mySeparation);

            _mySeekerEngine.apply(theDeltaTime, mySeparation);
            teleport(_mySeekerEngine);
        }

        /* tagged */
        if (_mySeekerEngine.position().distance(_mySeparatorEngine.position()) <
            _mySeekerEngine.getBoundingRadius() + _mySeparatorEngine.getBoundingRadius()) {
            _mySeparatorEngine.position().set(0, 0, 0);
        }
    }


    private void teleport(Engine theEngine) {
        /* teleport */
        if (theEngine.position().x > displaycapabilities().width / 2) {
            theEngine.position().x -= displaycapabilities().width;
        }
        if (theEngine.position().x < displaycapabilities().width / -2) {
            theEngine.position().x += displaycapabilities().width;
        }
        if (theEngine.position().y > displaycapabilities().height / 2) {
            theEngine.position().y -= displaycapabilities().height;
        }
        if (theEngine.position().y < displaycapabilities().height / -2) {
            theEngine.position().y += displaycapabilities().height;
        }
    }


    public static void main(String[] args) {
        new TestSeparation().init();
    }
}
