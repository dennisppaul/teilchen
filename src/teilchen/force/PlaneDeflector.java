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


package teilchen.force;


import mathematik.Plane3f;
import mathematik.Vector3f;

import teilchen.Particle;
import teilchen.Physics;


public class PlaneDeflector
    implements IForce {

    private Plane3f _myPlane;

    private float _myCoefficientOfRestitution;

    private final Vector3f _myTempDiff;

    private final Vector3f _myTempReflectionVector;

    private final Vector3f _myTempNormalComponent;

    private final Vector3f _myTempTangentComponent;

    private boolean _myActive;

    public PlaneDeflector() {
        _myPlane = new Plane3f();
        _myPlane.normal = new Vector3f(0, 1, 0);
        _myCoefficientOfRestitution = 1.0f;

        _myTempDiff = new Vector3f();
        _myTempReflectionVector = new Vector3f();
        _myTempNormalComponent = new Vector3f();
        _myTempTangentComponent = new Vector3f();
        _myActive = true;
    }


    public void apply(final float theDeltaTime, final Physics theParticleSystem) {
        for (final Particle myParticle : theParticleSystem.particles()) {
            if (!myParticle.fixed()) {
                /* test if particle passed plane */
                if (testParticlePosition(myParticle, _myPlane) < 0) {

                    /* find intersection with plane */
                    Vector3f myResult = new Vector3f();
                    /**
                     * using the normal of the plane instead of the velocity of the particle.
                     * though less correct it seems to be more stable.
                     */
                    final float myIntersectionResult = intersectLinePlane(myParticle.position(),
                                                                          _myPlane.normal,
                                                                          _myPlane,
                                                                          myResult);

                    /* remove particle from collision */
                    if (myIntersectionResult != Float.NEGATIVE_INFINITY
                        && !myResult.isNaN()) {
                        myParticle.position().set(myResult);
                    }

                    /* change direction */
                    seperateComponents(myParticle, _myPlane);
                    myParticle.velocity().set(_myTempReflectionVector);
                }
            }
        }
    }


    public Plane3f plane() {
        return _myPlane;
    }


    public void coefficientofrestitution(float theCoefficientOfRestitution) {
        _myCoefficientOfRestitution = theCoefficientOfRestitution;
    }


    public float coefficientofrestitution() {
        return _myCoefficientOfRestitution;
    }


    private final float testParticlePosition(Particle theParticle, Plane3f thePlane) {
        _myTempDiff.sub(theParticle.position(), thePlane.origin);
        _myTempDiff.normalize();
        final float myAngle = _myTempDiff.dot(thePlane.normal);
        return myAngle;
    }


    private final void seperateComponents(Particle theParticle, Plane3f thePlane) {
        /* normal */
        _myTempNormalComponent.set(thePlane.normal);
        _myTempNormalComponent.scale(thePlane.normal.dot(theParticle.velocity()));
        /* tangent */
        _myTempTangentComponent.sub(theParticle.velocity(), _myTempNormalComponent);
        /* negate normal */
        _myTempNormalComponent.scale( -_myCoefficientOfRestitution);
        /* set reflection vector */
        _myTempReflectionVector.add(_myTempTangentComponent, _myTempNormalComponent);
    }


    private final float intersectLinePlane(final Vector3f theRayOrigin,
                                           final Vector3f theRayDirection,
                                           final Plane3f thePlane,
                                           final Vector3f theIntersectionPoint) {
        float myT = 0;
        final float myDenominator = thePlane.normal.dot(theRayDirection);

        if (myDenominator == 0) {
            System.err.println("### ERROR @ Intersection / NEGATIVE_INFINITY");
            return Float.NEGATIVE_INFINITY;
        }

        final float numer = thePlane.normal.dot(theRayOrigin);
        final float D = -thePlane.origin.dot(thePlane.normal);
        myT = - ( (numer + D) / myDenominator);

        if (theIntersectionPoint != null) {
            theIntersectionPoint.set(theRayDirection);
            theIntersectionPoint.scale( (float) myT);
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
