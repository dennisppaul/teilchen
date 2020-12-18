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

package teilchen.force;

import processing.core.PVector;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.util.Plane3f;

import static processing.core.PVector.add;
import static processing.core.PVector.sub;
import static teilchen.util.Util.isNaN;

public class PlaneDeflector implements IForce {

    private final Plane3f mPlane;
    private final PVector mTempDiff;
    private final PVector mTempReflectionVector;
    private final PVector mTempNormalComponent;
    private final PVector mTempTangentComponent;
    private float mCoefficientOfRestitution;
    private boolean mActive;
    private boolean mDead = false;
    private final long mID;

    public PlaneDeflector() {
        mID = Physics.getUniqueID();
        mPlane = new Plane3f();
        mPlane.normal = new PVector(0, 1, 0);
        mCoefficientOfRestitution = 1.0f;

        mTempDiff = new PVector();
        mTempReflectionVector = new PVector();
        mTempNormalComponent = new PVector();
        mTempTangentComponent = new PVector();
        mActive = true;
    }

    public void apply(final float pDeltaTime, final Physics pParticleSystem) {
        for (final Particle mParticle : pParticleSystem.particles()) {
            if (!mParticle.fixed()) {
                /* test if particle passed plane */
                if (testParticlePosition(mParticle, mPlane) < 0) {
                    // @todo maybe add a *distance to origin* check here?
                    /* find intersection with plane */
                    PVector mResult = new PVector();
                    /*
                     * using the normal of the plane instead of the velocity of
                     * the particle. though less correct it seems to be more
                     * stable.
                     */
                    final float mIntersectionResult = intersectLinePlane(mParticle.position(),
                                                                          mPlane.normal,
                                                                          mPlane,
                                                                          mResult);

                    /* remove particle from collision */
                    if (mIntersectionResult != Float.NEGATIVE_INFINITY && !isNaN(mResult)) {
                        mParticle.position().set(mResult);
                    }

                    /* change direction */
                    separateComponents(mParticle, mPlane);
                    mParticle.velocity().set(mTempReflectionVector);

                    /* tag particle */
                    mParticle.tag(true);
                }
            }
        }
    }

    public boolean dead() {
        return mDead;
    }

    public void dead(boolean pDead) {
        mDead = pDead;
    }

    public boolean active() {
        return mActive;
    }

    public void active(boolean pActiveState) {
        mActive = pActiveState;
    }

    public long ID() {
        return mID;
    }

    public Plane3f plane() {
        return mPlane;
    }

    public void coefficientofrestitution(float pCoefficientOfRestitution) {
        mCoefficientOfRestitution = pCoefficientOfRestitution;
    }

    public float coefficientofrestitution() {
        return mCoefficientOfRestitution;
    }

    private float testParticlePosition(Particle pParticle, Plane3f pPlane) {
        sub(pParticle.position(), pPlane.origin, mTempDiff);
        mTempDiff.normalize();
        final float mAngle = mTempDiff.dot(pPlane.normal);
        return mAngle;
    }

    private void separateComponents(Particle pParticle, Plane3f pPlane) {
        /* normal */
        mTempNormalComponent.set(pPlane.normal);
        mTempNormalComponent.mult(pPlane.normal.dot(pParticle.velocity()));
        /* tangent */
        sub(pParticle.velocity(), mTempNormalComponent, mTempTangentComponent);
        /* negate normal */
        mTempNormalComponent.mult(-mCoefficientOfRestitution);
        /* set reflection vector */
        add(mTempTangentComponent, mTempNormalComponent, mTempReflectionVector);
    }

    private float intersectLinePlane(final PVector pRayOrigin,
                                     final PVector pRayDirection,
                                     final Plane3f pPlane,
                                     final PVector pIntersectionPoint) {
        float mT;
        final float mDenominator = pPlane.normal.dot(pRayDirection);

        if (mDenominator == 0) {
            System.err.println("### ERROR @ Intersection / NEGATIVE_INFINITY");
            return Float.NEGATIVE_INFINITY;
        }

        final float n = pPlane.normal.dot(pRayOrigin);
        final float D = -pPlane.origin.dot(pPlane.normal);
        mT = -((n + D) / mDenominator);

        if (pIntersectionPoint != null) {
            pIntersectionPoint.set(pRayDirection);
            pIntersectionPoint.mult(mT);
            pIntersectionPoint.add(pRayOrigin);
        }

        return mT;
    }
}
