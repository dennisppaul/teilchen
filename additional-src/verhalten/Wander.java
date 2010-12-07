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
 * 'wander' steers smoothly to generate a random motion.
 */


package verhalten;


import java.io.Serializable;

import mathematik.Vector3f;


/**
 * @deprecated PORTED TO PARTICLES
 */

public class Wander
    implements IVerhaltenBehavior, Serializable {

    private static final long serialVersionUID = 8877586686692875211L;

    private Vector3f _myDebugResult;

    private float _mySteeringStrength;

    private float _myWanderRadius;

    private Vector3f _myWanderTarget;

    private Vector3f _myForwardSteering;

    private Vector3f _myWanderDirection;

    public Wander() {
        _myDebugResult = new Vector3f();
        _mySteeringStrength = 700.0f;
        _myWanderRadius = 30.0f;

        _myForwardSteering = new Vector3f();
        _myWanderTarget = new Vector3f();
        _myWanderDirection = new Vector3f(1, 1, 0);
    }


    public void get(final Engine theEngine,
                    final float theDeltaTime,
                    Vector3f theDirection) {

        /* get target offset */
        theDirection.set(_myWanderDirection.x * Math.random() - _myWanderDirection.x * 0.5f,
                         _myWanderDirection.y * Math.random() - _myWanderDirection.y * 0.5f,
                         _myWanderDirection.z * Math.random() - _myWanderDirection.z * 0.5f);
        theDirection.scale(_mySteeringStrength);
        theDirection.scale(theDeltaTime);

        /* change target position */
        _myWanderTarget.add(theDirection);
        _myWanderTarget.normalize();
        _myWanderTarget.scale(_myWanderRadius);

        /* add a forward motion */
        _myForwardSteering.normalize(theEngine.velocity());
        _myForwardSteering.scale(theEngine.getMaximumSpeed());
        theDirection.add(_myForwardSteering, _myWanderTarget);

        /* --- */
        _myDebugResult.set(theDirection);
    }


    public void setSteeringStrength(final float theSteeringStrength) {
        _mySteeringStrength = theSteeringStrength;
    }


    public float getSteeringStrength() {
        return _mySteeringStrength;
    }


    public float getRadius() {
        return _myWanderRadius;
    }


    public void setRadius(float theRadius) {
        _myWanderRadius = theRadius;
    }


    public Vector3f possibleDirections() {
        return _myWanderDirection;
    }


    public Vector3f direction() {
        return _myDebugResult;
    }


    /* used for debugging */

    public Vector3f getTarget() {
        return _myWanderTarget;
    }


    public Vector3f getForward() {
        return _myForwardSteering;
    }

}
