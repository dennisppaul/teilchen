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
import static processing.core.PVector.*;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.util.Plane3f;
import static teilchen.util.Util.*;

public class PlaneDeflector
        implements IForce {

    private final Plane3f mPlane;

    private float mCoefficientOfRestitution;

    private final PVector _myTempDiff;

    private final PVector mTempReflectionVector;

    private final PVector mTempNormalComponent;

    private final PVector mTempTangentComponent;

    private boolean _myActive;

    public PlaneDeflector() {
        mPlane = new Plane3f();
        mPlane.normal = new PVector(0, 1, 0);
        mCoefficientOfRestitution = 1.0f;

        _myTempDiff = new PVector();
        mTempReflectionVector = new PVector();
        mTempNormalComponent = new PVector();
        mTempTangentComponent = new PVector();
        _myActive = true;
    }

    public void apply(final float theDeltaTime, final Physics theParticleSystem) {
        for (final Particle myParticle : theParticleSystem.particles()) {
            if (!myParticle.fixed()) {
                /* test if particle passed plane */
                if (testParticlePosition(myParticle, mPlane) < 0) {

                    /* find intersection with plane */
                    PVector myResult = new PVector();
                    /*
                     * using the normal of the plane instead of the velocity of
                     * the particle. though less correct it seems to be more
                     * stable.
                     */
                    final float myIntersectionResult = intersectLinePlane(myParticle.position(),
                                                                          mPlane.normal,
                                                                          mPlane,
                                                                          myResult);

                    /* remove particle from collision */
                    if (myIntersectionResult != Float.NEGATIVE_INFINITY
                        && !isNaN(myResult)) {
                        myParticle.position().set(myResult);
                    }

                    /* change direction */
                    seperateComponents(myParticle, mPlane);
                    myParticle.velocity().set(mTempReflectionVector);
                }
            }
        }
    }

    public Plane3f plane() {
        return mPlane;
    }

    public void coefficientofrestitution(float theCoefficientOfRestitution) {
        mCoefficientOfRestitution = theCoefficientOfRestitution;
    }

    public float coefficientofrestitution() {
        return mCoefficientOfRestitution;
    }

    private float testParticlePosition(Particle theParticle, Plane3f thePlane) {
        sub(theParticle.position(), thePlane.origin, _myTempDiff);
        _myTempDiff.normalize();
        final float mAngle = _myTempDiff.dot(thePlane.normal);
        return mAngle;
    }

    private void seperateComponents(Particle theParticle, Plane3f thePlane) {
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

        final float numer = thePlane.normal.dot(theRayOrigin);
        final float D = -thePlane.origin.dot(thePlane.normal);
        myT = -((numer + D) / myDenominator);

        if (theIntersectionPoint != null) {
            theIntersectionPoint.set(theRayDirection);
            theIntersectionPoint.mult(myT);
            theIntersectionPoint.add(theRayOrigin);
        }

        return myT;
    }

    public boolean dead() {
        return false;
    }

    public boolean active() {
        return _myActive;
    }

    public void active(boolean theActiveState) {
        _myActive = theActiveState;
    }
}
