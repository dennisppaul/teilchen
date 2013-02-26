/*
 * Verhalten
 *
 * Copyright (C) 2005 Patrick Kochlik + Dennis Paul
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

/*
 * engine takes the collected directions and integrates them
 * into the current velocity and position().
 */


package verhalten;


import java.io.Serializable;

import mathematik.TransformMatrix4f;
import mathematik.Vector3f;


/**
 * @deprecated PORTED TO PARTICLES
 */

public class Engine
    implements IVerhaltenEntity, Serializable {

    private static final long serialVersionUID = 4263004041053399993L;

    public static final boolean MORE_CAREFUL = true;

    private float _myRadius;

    private final TransformMatrix4f _myTransform;

    private final Vector3f _myVelocity;

    private final Vector3f _myNormalizedVelocity;

    private float _myMass;

    private float _myMaximumSpeed;

    private float _myMaximumForce;

    private float _mySpeed;

    private final Vector3f _mySteeringForce;

    private final Vector3f _myAcceleration;

    private final Vector3f _myResult;

    private final Vector3f _myTempDirection;

    private final Vector3f _myTempWeightDirection;

    private final Vector3f _myTempVelocity;

    public Engine() {
        _myRadius = 10;
        _myMaximumSpeed = 100.0f;
        _myMass = 1.0f;
        _myMaximumForce = 50.0f;
        _mySpeed = 10.0f;
        _mySteeringForce = new Vector3f();
        _myAcceleration = new Vector3f();
        _myTransform = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
        _myVelocity = new Vector3f(1, 0, 0);
        _myNormalizedVelocity = new Vector3f(1, 0, 0);
        /* allocate once for better performance */
        _myResult = new Vector3f();
        _myTempDirection = new Vector3f();
        _myTempWeightDirection = new Vector3f();
        _myTempVelocity = new Vector3f();
    }


    public Vector3f position() {
        return _myTransform.translation;
    }


    public Vector3f velocity() {
        return _myVelocity;
    }


    public TransformMatrix4f transform() {
        return _myTransform;
    }


    public Vector3f normalizedVelocity() {
        return _myNormalizedVelocity;
    }


    public float getMass() {
        return _myMass;
    }


    public void setMass(final float theMass) {
        _myMass = theMass;
    }


    public float getMaximumSpeed() {
        return _myMaximumSpeed;
    }


    public void setMaximumSpeed(final float theMaximumSpeed) {
        _myMaximumSpeed = theMaximumSpeed;
    }


    public float getSpeed() {
        return _mySpeed;
    }


    public void setSpeed(final float theSpeed) {
        if (theSpeed > 0.0f) {
            if (_mySpeed > 0.0f) {
                _myTempVelocity.normalize(_myVelocity);
                _myVelocity.scale(theSpeed, _myTempVelocity);
            } else {
                _myVelocity.set(theSpeed,
                                0,
                                0);
            }
        } else {
            _myVelocity.set(0,
                            0,
                            0);
        }
        _mySpeed = theSpeed;
    }


    public void clampToSpeed(final float theMinSpeed,
                             final float theMaxSpeed) {
        if (_mySpeed < theMinSpeed) {
            setSpeed(theMinSpeed);
        } else if (_mySpeed > theMaxSpeed) {
            setSpeed(theMaxSpeed);
        }
    }


    public float getMaximumForce() {
        return _myMaximumForce;
    }


    public void setMaximumForce(final float theMaximumForce) {
        _myMaximumForce = theMaximumForce;
    }


    public void apply(final float theDeltaTime,
                      final Vector3f[] theDirections,
                      final float[] theDirectionsWeight) {
        _myTempDirection.set(0, 0, 0);
        if (theDirections.length == theDirectionsWeight.length) {
            for (int i = 0; i < theDirections.length; i++) {
                _myTempWeightDirection.scale(theDirectionsWeight[i],
                                             theDirections[i]);
                _myTempDirection.add(_myTempWeightDirection);
            }
            _myTempWeightDirection.scale(1.0f / (float) theDirections.length);
        } else {
            System.out.println("### WARNING @ " + this.getClass() +
                               " / direction and weight arrays are not aligned");
        }
        apply(theDeltaTime,
              _myTempDirection);
    }


    public void apply(final float theDeltaTime,
                      final Vector3f theDirection) {

        if (MORE_CAREFUL) {
            if (theDeltaTime <= 0) {
                System.err.println("### WARNING @ Engine / delta time is zero. " +
                                   theDeltaTime);
                return;
            }
            if (theDirection.isNaN()) {
                System.err.println("### WARNING @ Engine / direction is NaN. " +
                                   theDirection);
            }
        }

        // cr ... adjusted steering force -> limit it to an angle against the
        // theVelocity
        // cr ... smoothed accleration

        /**
         * @todo i am not at all sure about this.
         * but visually it makes a lot of sense.
         * especially in the 'arrival' behavior.
         */
        theDirection.scale(1 / theDeltaTime);
        /* steering force */
        _mySteeringForce.set(theDirection);

        /* if the direction is (0, 0, 0) continue just integrationg the velocity */
        float mySteeringForceLength = _mySteeringForce.length();
        if (mySteeringForceLength > 0) {
            /* clamp to maximum force */
            if (_myMaximumForce != Verhalten.UNDEFINED) {
                if (mySteeringForceLength > _myMaximumForce) {
                    _mySteeringForce.scale(_myMaximumForce / mySteeringForceLength);
                }
            }

            /* acceleration */
            _myAcceleration.scale(1.0f / _myMass, _mySteeringForce);

            /* velocity */
            _myResult.scale(theDeltaTime, _myAcceleration);
            _myVelocity.add(_myResult);

            if (MORE_CAREFUL && _myVelocity.isNaN()) {
                System.err.println("### WARNING @ Engine / velocity is NaN." + _myVelocity);
                _myVelocity.set(0, 0, 0);
                _myAcceleration.set(0, 0, 0);
            }
        }

        /* speed */
        _mySpeed = _myVelocity.length();
        if (_mySpeed == 0) {
            return;
        }
        _myNormalizedVelocity.scale(1 / _mySpeed, _myVelocity);
        if (_mySpeed > _myMaximumSpeed) {
            _myVelocity.scale(_myMaximumSpeed,
                              _myNormalizedVelocity);
        }

        /* position */
        _myResult.scale(theDeltaTime,
                        _myVelocity);
        if (MORE_CAREFUL && _myVelocity.isNaN()) {
            System.err.println("### WARNING @ Engine / result is NaN." +
                               _myResult);
            _myResult.set(0, 0, 0);
        }

        _myTransform.translation.add(_myResult);
    }


    public float getBoundingRadius() {
        return _myRadius;
    }


    public void setBoundingRadius(float theBoundingRadius) {
        _myRadius = theBoundingRadius;
    }


    public boolean isTagged() {
        return false;
    }


    public void setTag(boolean theTag) {
    }
}
