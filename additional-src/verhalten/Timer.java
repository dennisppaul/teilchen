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


/* 'timer' can measure the time between the two times loop is called. */

/**
 * @todo
 * it doesn t reflect the possible dangers of using timers.
 * MAX and MIN values for timesteps.
 */
package verhalten;


public class Timer {

    private double _myTime = System.currentTimeMillis();

    private double _myDeltaTime = 0;

    public float getDeltaTime() {
        return (float) _myDeltaTime;
    }


    public void loop() {
        _myDeltaTime = (System.currentTimeMillis() - _myTime) / 1000.0d;
        _myTime = System.currentTimeMillis();
    }
}
