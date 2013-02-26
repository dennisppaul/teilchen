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


package teilchen;


import java.io.Serializable;

import mathematik.Vector3f;


public class BasicParticle
    implements Particle, Serializable {

    private boolean _myFixed;

    private float _myAge;

    private float _myMass;

    private Vector3f _myPosition;

    private Vector3f _myOldPosition;

    private final Vector3f _myVelocity;

    private final Vector3f _myForce;

    private boolean _myTagged;

    private boolean _myStill;

    private float _myRadius;

    private static final long serialVersionUID = 3737917975116369338L;

    public BasicParticle() {
        _myPosition = new Vector3f();
        _myOldPosition = new Vector3f();
        _myVelocity = new Vector3f();
        _myForce = new Vector3f();
        _myMass = 1;
        _myFixed = false;
        _myAge = 0;
        _myTagged = false;
        _myStill = false;
        _myRadius = 0;
    }


    public boolean fixed() {
        return _myFixed;
    }


    public void fixed(boolean theFixed) {
        _myFixed = theFixed;
    }


    public float age() {
        return _myAge;
    }


    public void age(float theAge) {
        _myAge = theAge;
    }


    public float mass() {
        return _myMass;
    }


    public void mass(float theMass) {
        _myMass = theMass;
    }


    public Vector3f position() {
        return _myPosition;
    }


    public Vector3f old_position() {
        return _myOldPosition;
    }


    public void setPositionRef(Vector3f thePosition) {
        _myPosition = thePosition;
    }


    public Vector3f velocity() {
        return _myVelocity;
    }


    public Vector3f force() {
        return _myForce;
    }


    public boolean dead() {
        return false;
    }


    public void accumulateInnerForce(final float theDeltaTime) {}


    public boolean tagged() {
        return _myTagged;
    }


    public void tag(boolean theTag) {
        _myTagged = theTag;
    }


    public boolean still() {
        return _myStill;
    }


    public void still(boolean theStill) {
        _myStill = theStill;
    }


    public float radius() {
        return _myRadius;
    }


    public void radius(float theRadius) {
        _myRadius = theRadius;
    }
}
