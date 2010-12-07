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


package teilchen.gestalt.util;


import gestalt.render.Loop;
import gestalt.render.Loopable;

import teilchen.Physics;


public class PhysicsThread
    implements Loopable, Runnable {

    private final Thread _myThread;

    private final Loop _myLoop;

    private final Physics _myPhysics;

    private boolean _myFixFramerate = false;

    public PhysicsThread() {
        _myThread = new Thread(this);
        _myLoop = new Loop(1);
        _myLoop.add(this);
        _myPhysics = new Physics();
    }


    public Physics physics() {
        return _myPhysics;
    }


    public void framerate(int theFramerate) {
        _myLoop.framerate(theFramerate);
    }


    public int framerate() {
        return _myLoop.getCurrentFramerate();
    }


    public void start() {
        _myThread.start();
    }


    public void stop() {
        _myLoop.quit();
    }


    public void fixDeltaTime(boolean theFixFramerateFlag) {
        _myFixFramerate = theFixFramerateFlag;
    }


    public void setup() {
    }


    public void update(float theDeltaTime) {
        if (_myFixFramerate) {
            _myPhysics.step(1f / _myLoop.getFramerate());
        } else {
            _myPhysics.step(theDeltaTime);
        }
    }


    public void run() {
        _myLoop.execute();
    }
}
