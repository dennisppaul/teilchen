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

package teilchen.force;

import processing.core.PVector;
import teilchen.IParticle;
import teilchen.Physics;
import teilchen.util.Util;

public class LineDeflector2D implements IForce {

    private PVector a = new PVector();
    private PVector b = new PVector();
    private boolean mActive = true;
    private float mCoefficientOfRestitution = 1.0f;
    private boolean mDead = false;
    private final long mID;

    public LineDeflector2D() {
        mID = Physics.getUniqueID();
    }

    public PVector normal() {
        PVector mNormal = PVector.sub(b, a);
        mNormal.normalize();
        /* proper 3D version */
        //            mNormal = mNormal.cross(new PVector(0, 0, 1));
        /* 2D optimized version */
        float y = -mNormal.x;
        mNormal.x = mNormal.y;
        mNormal.y = y;
        return mNormal;
    }

    public PVector mid() {
        return PVector.sub(b, a).mult(0.5f).add(a);
    }

    public void calculateIntersection(IParticle pParticle, float pDeltaTime) {
        /* calc intersection from velocity */
        //            PVector mForward = PVector.mult(pParticle.velocity(), pDeltaTime);
        //            mForward.add(PVector.mult(pParticle.velocity(), pParticle.radius() / pParticle.velocity().mag()));
        //            PVector mFuturePosition = PVector.add(pParticle.position(), mForward);
        //            PVector mPointOfIntersection = new PVector();
        //            int mIntersectionResult = Intersection.lineLineIntersect(pParticle.position(), mFuturePosition,
        //            a, b,
        // mPointOfIntersection);
        //            if (mIntersectionResult == Intersection.INTERESECTING) {
        //                PVector mReflection = Util.calculateReflectionVector(pParticle, normal());
        //                pParticle.velocity().set(mReflection);
        //                pParticle.tag(true);
        //            }

        /* calc intersection by closest distance to line */
        PVector mProjectedPointOnLine = Util.projectPointOnLineSegment(a, b, pParticle.position());
        float mDistanceToLine = PVector.sub(mProjectedPointOnLine, pParticle.position()).mag();
        if (mDistanceToLine < pParticle.radius()) {
            /* resolve collision */
            PVector mPosition = PVector.sub(pParticle.position(), mProjectedPointOnLine);
            mPosition.mult(pParticle.radius() / mPosition.mag());
            mPosition.add(mProjectedPointOnLine);
            pParticle.position().set(mPosition);
            /* reflect velocity */
            PVector mReflection = Util.calculateReflectionVector(pParticle, normal()).mult(mCoefficientOfRestitution);
            pParticle.velocity().set(mReflection);
            // TODO need to handle `old_position`
            pParticle.tag(true);
        }
    }

    @Override
    public void apply(float pDeltaTime, Physics pParticleSystem) {
        for (final IParticle mParticle : pParticleSystem.particles()) {
            if (!mParticle.fixed()) {
                calculateIntersection(mParticle, pDeltaTime);
            }
        }
    }

    @Override
    public boolean dead() {
        return mDead;
    }

    @Override
    public void dead(boolean pDead) {
        mDead = pDead;
    }

    @Override
    public boolean active() {
        return mActive;
    }

    @Override
    public void active(boolean pActiveState) {
        mActive = pActiveState;
    }

    public long ID() {
        return mID;
    }

    public void coefficientofrestitution(float pCoefficientOfRestitution) {
        mCoefficientOfRestitution = pCoefficientOfRestitution;
    }

    public PVector a() {
        return a;
    }

    public PVector b() {
        return b;
    }

    public void set_edges(PVector a, PVector b) {
        this.a = a;
        this.b = b;
    }
}