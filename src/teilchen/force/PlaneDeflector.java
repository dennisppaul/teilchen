/*
 * Teilchen
 *
 * Copyright (C) 2015
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

    public PlaneDeflector() {
        mPlane = new Plane3f();
        mPlane.normal = new PVector(0, 1, 0);
        mCoefficientOfRestitution = 1.0f;

        mTempDiff = new PVector();
        mTempReflectionVector = new PVector();
        mTempNormalComponent = new PVector();
        mTempTangentComponent = new PVector();
        mActive = true;
    }

    public void apply(final float theDeltaTime, final Physics theParticleSystem) {
        for (final Particle mParticle : theParticleSystem.particles()) {
            if (!mParticle.fixed()) {
                /* test if particle passed plane */
                if (testParticlePosition(mParticle, mPlane) < 0) {
                    // @todo maybe add a *distance to origin* check here?
                    /* find intersection with plane */
                    PVector myResult = new PVector();
                    /*
                     * using the normal of the plane instead of the velocity of
                     * the particle. though less correct it seems to be more
                     * stable.
                     */
                    final float myIntersectionResult = intersectLinePlane(mParticle.position(), mPlane.normal, mPlane, myResult);

                    /* remove particle from collision */
                    if (myIntersectionResult != Float.NEGATIVE_INFINITY && !isNaN(myResult)) {
                        mParticle.position().set(myResult);
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
        return false;
    }

    public boolean active() {
        return mActive;
    }

    public void active(boolean theActiveState) {
        mActive = theActiveState;
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

    private float testParticlePosition(Particle theParticle, Plane3f thePlane) {
        sub(theParticle.position(), thePlane.origin, mTempDiff);
        mTempDiff.normalize();
        final float mAngle = mTempDiff.dot(thePlane.normal);
        return mAngle;
    }

    private void separateComponents(Particle theParticle, Plane3f thePlane) {
        /* normal */
        mTempNormalComponent.set(thePlane.normal);
        mTempNormalComponent.mult(thePlane.normal.dot(theParticle.velocity()));
        /* tangent */
        sub(theParticle.velocity(), mTempNormalComponent, mTempTangentComponent);
        /* negate normal */
        mTempNormalComponent.mult(-mCoefficientOfRestitution);
        /* set reflection vector */
        add(mTempTangentComponent, mTempNormalComponent, mTempReflectionVector);
    }

    private float intersectLinePlane(final PVector theRayOrigin,
                                     final PVector theRayDirection,
                                     final Plane3f thePlane,
                                     final PVector theIntersectionPoint) {
        float myT;
        final float myDenominator = thePlane.normal.dot(theRayDirection);

        if (myDenominator == 0) {
            System.err.println("### ERROR @ Intersection / NEGATIVE_INFINITY");
            return Float.NEGATIVE_INFINITY;
        }

        final float n = thePlane.normal.dot(theRayOrigin);
        final float D = -thePlane.origin.dot(thePlane.normal);
        myT = -((n + D) / myDenominator);

        if (theIntersectionPoint != null) {
            theIntersectionPoint.set(theRayDirection);
            theIntersectionPoint.mult(myT);
            theIntersectionPoint.add(theRayOrigin);
        }

        return myT;
    }
}
