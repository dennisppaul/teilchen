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


package teilchen.behavior;


import mathematik.Vector3f;

import teilchen.IBehaviorParticle;


public class Motor
    implements IBehavior, Verhalten {

    static final long serialVersionUID = -3781170603537691466L;

    private Vector3f _myDirection;

    private float _myStrength = 1;

    private Vector3f _myForce;

    private float _myWeight = 1;

    public Motor() {
        _myDirection = new Vector3f();
        _myForce = new Vector3f();
    }


    public float strength() {
        return _myStrength;
    }


    public void strength(final float theStrength) {
        _myStrength = theStrength;
    }


    public Vector3f direction() {
        return _myDirection;
    }


    public void setDirectionRef(final Vector3f theDirection) {
        _myDirection = theDirection;
    }


    public void update(float theDeltaTime, IBehaviorParticle theParent) {
        _myForce.scale(_myStrength, _myDirection);
    }


    public Vector3f force() {
        return _myForce;
    }


    public float weight() {
        return _myWeight;
    }


    public void weight(float theWeight) {
        _myWeight = theWeight;
    }
}
