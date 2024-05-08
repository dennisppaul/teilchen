/*
 * Teilchen
 *
 * This file is part of the *teilchen* library (https://github.com/dennisppaul/teilchen).
 * Copyright (c) 2024 Dennis P Paul.
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

package teilchen.constraint;

import processing.core.PGraphics;
import processing.core.PVector;
import teilchen.IConnection;
import teilchen.Particle;
import teilchen.Physics;

import java.util.ArrayList;

import static teilchen.util.Intersection.INTERSECTING;
import static teilchen.util.Intersection.intersect_line_line;

public class LineIntersectionConstraint implements IConstraint {

    public PGraphics DEBUG_VIEW = null;
    private boolean mDead = false;
    private final long mID;
    private final Particle mParticle;
    private ArrayList<IConnection> mPotentialLineIntersections;
    private float mProxScale = 1.0f;

    public LineIntersectionConstraint(Particle pParticle) {
        mID = Physics.getUniqueID();
        mParticle = pParticle;
        mPotentialLineIntersections = new ArrayList<>();
    }

    public Particle particle() {
        return mParticle;
    }

    public void apply(Physics pParticleSystem) {
        final PVector mIntersection = new PVector();
        final PVector mA = mParticle.position();
        final PVector mB = mParticle.old_position();
        float mMinDistanceToIntersection = Float.MAX_VALUE;
        for (IConnection mPotentialLineIntersection : mPotentialLineIntersections) {
            final PVector mAA = mPotentialLineIntersection.a().position();
            final PVector mBB = mPotentialLineIntersection.b().position();
            if (mA == mAA || mA == mBB) {
                /* ignore test if points are shared */
                continue;
            }
            final int mResult = intersect_line_line(mA, mB, mAA, mBB, mIntersection);
            if (mResult == INTERSECTING) {
                /* reset current position to intersection */
                final PVector mIntersectProx = PVector.sub(mB, mIntersection);
                final float mDistanceToIntersection = mIntersectProx.mag();
                if (mDistanceToIntersection < mMinDistanceToIntersection) {
                    mMinDistanceToIntersection = mDistanceToIntersection;
                    if (mProxScale > 0.0f) {
                        /* add paddding instead of setting the particle to intersection */
                        if (mDistanceToIntersection != 0 && mDistanceToIntersection != 1) {
                            mIntersectProx.mult(mProxScale / mDistanceToIntersection);
                            mIntersectProx.add(mIntersection);
                        }
                        mParticle.position().set(mIntersectProx);
                    } else {
                        mParticle.position().set(mIntersection);
                    }
                }
                if (DEBUG_VIEW != null) {
                    DEBUG_VIEW.strokeWeight(1);
                    DEBUG_VIEW.stroke(255, 0, 0, 192);
                    DEBUG_VIEW.ellipse(mIntersection.x, mIntersection.y, 20, 20);
                    DEBUG_VIEW.stroke(0, 255, 0, 192);
                    DEBUG_VIEW.ellipse(mIntersectProx.x, mIntersectProx.y, 10, 10);
                }
            }
            if (DEBUG_VIEW != null) {
                DEBUG_VIEW.stroke(255, 0, 0, 192);
                DEBUG_VIEW.strokeWeight(1);
                DEBUG_VIEW.line(mA.x, mA.y, mB.x, mB.y);
            }
        }
    }

    public boolean active() {
        return true;
    }

    public void active(boolean pActiveState) {
    }

    public boolean dead() {
        return mDead;
    }

    public void dead(boolean pDead) {
        mDead = pDead;
    }

    public long ID() {
        return mID;
    }

    public ArrayList<IConnection> intersecting_lines() {
        return mPotentialLineIntersections;
    }

    public void setIntersectingLinesRef(ArrayList<IConnection> pIntersectingLinesRef) {
        mPotentialLineIntersections = pIntersectingLinesRef;
    }

    public void intersection_padding(float pIntersectionPadding) {
        mProxScale = pIntersectionPadding;
    }
}