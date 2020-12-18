/*
 * Teilchen
 *
 * This file is part of the *teilchen* library (https://github.com/dennisppaul/teilchen).
 * Copyright (c) 2020 Dennis P Paul.
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

import processing.core.PVector;

import java.io.Serializable;

public class BasicParticle implements Particle, Serializable {

    private static final long serialVersionUID = 3737917975116369338L;
    private final PVector mOldPosition;
    private final PVector mVelocity;
    private final PVector mForce;
    private final long mID;
    private boolean mFixed;
    private float mAge;
    private float mMass;
    private PVector mPosition;
    private boolean mTagged;
    private boolean mStill;
    private boolean mDead;
    private float mRadius;

    public BasicParticle() {
        mID = Physics.getUniqueID();
        mPosition = new PVector();
        mOldPosition = new PVector();
        mVelocity = new PVector();
        mForce = new PVector();
        mMass = 1;
        mFixed = false;
        mAge = 0;
        mTagged = false;
        mStill = false;
        mRadius = 0;
        mDead = false;
    }

    public boolean fixed() {
        return mFixed;
    }

    public void fixed(boolean pFixed) {
        mFixed = pFixed;
    }

    public float age() {
        return mAge;
    }

    public void age(float pAge) {
        mAge = pAge;
    }

    public float mass() {
        return mMass;
    }

    public void mass(float pMass) {
        mMass = pMass;
    }

    public PVector old_position() {
        return mOldPosition;
    }

    public void setPositionRef(PVector pPosition) {
        mPosition = pPosition;
    }

    public PVector velocity() {
        return mVelocity;
    }

    public PVector force() {
        return mForce;
    }

    public boolean dead() {
        return mDead;
    }

    public void dead(boolean pDead) {
        mDead = pDead;
    }

    public boolean tagged() {
        return mTagged;
    }

    public void tag(boolean pTag) {
        mTagged = pTag;
    }

    public void accumulateInnerForce(final float pDeltaTime) {
    }

    public float radius() {
        return mRadius;
    }

    public PVector position() {
        return mPosition;
    }

    public void radius(float pRadius) {
        mRadius = pRadius;
    }

    public boolean still() {
        return mStill;
    }

    public void still(boolean pStill) {
        mStill = pStill;
    }

    public long ID() {
        return mID;
    }
}
