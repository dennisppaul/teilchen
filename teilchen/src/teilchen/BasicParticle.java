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

    private boolean mFixed;

    private float mAge;

    private float mMass;

    private Vector3f mPosition;

    private final Vector3f mOldPosition;

    private final Vector3f mVelocity;

    private final Vector3f mForce;

    private boolean mTagged;

    private boolean mStill;

    private float mRadius;

    private static final long serialVersionUID = 3737917975116369338L;

    public BasicParticle() {
        mPosition = new Vector3f();
        mOldPosition = new Vector3f();
        mVelocity = new Vector3f();
        mForce = new Vector3f();
        mMass = 1;
        mFixed = false;
        mAge = 0;
        mTagged = false;
        mStill = false;
        mRadius = 0;
    }

    public boolean fixed() {
        return mFixed;
    }

    public void fixed(boolean theFixed) {
        mFixed = theFixed;
    }

    public float age() {
        return mAge;
    }

    public void age(float theAge) {
        mAge = theAge;
    }

    public float mass() {
        return mMass;
    }

    public void mass(float theMass) {
        mMass = theMass;
    }

    public Vector3f position() {
        return mPosition;
    }

    public Vector3f old_position() {
        return mOldPosition;
    }

    public void setPositionRef(Vector3f thePosition) {
        mPosition = thePosition;
    }

    public Vector3f velocity() {
        return mVelocity;
    }

    public Vector3f force() {
        return mForce;
    }

    public boolean dead() {
        return false;
    }

    public void accumulateInnerForce(final float theDeltaTime) {
    }

    public boolean tagged() {
        return mTagged;
    }

    public void tag(boolean theTag) {
        mTagged = theTag;
    }

    public boolean still() {
        return mStill;
    }

    public void still(boolean theStill) {
        mStill = theStill;
    }

    public float radius() {
        return mRadius;
    }

    public void radius(float theRadius) {
        mRadius = theRadius;
    }
}
