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


package teilchen.constraint;


import mathematik.Vector3f;

import teilchen.IConnection;
import teilchen.Particle;
import teilchen.Physics;


public class Stick
    implements IConstraint, IConnection {

    protected final Particle _myA;

    protected final Particle _myB;

    protected float _myRestLength;

    protected final Vector3f _myTempDistanceVector;

    protected final Vector3f _myTempVector;

    protected boolean _myOneWay;

    protected float _myDamping;

    protected static final float EPSILON = 0.0001f;

    public Stick(Particle theA, Particle theB) {
        this(theA,
             theB,
             theA.position().distance(theB.position()));
    }


    public Stick(final Particle theA,
                 final Particle theB,
                 final float theRestLength) {
        _myRestLength = theRestLength;
        _myA = theA;
        _myB = theB;
        _myTempDistanceVector = new Vector3f();
        _myTempVector = new Vector3f();
        _myOneWay = false;
        _myDamping = 1f;
    }


    public void setRestLengthByPosition() {
        _myRestLength = _myA.position().distance(_myB.position());
    }


    public float damping() {
        return _myDamping;
    }


    public void damping(float theDamping) {
        _myDamping = theDamping;
    }


    public float restlength() {
        return _myRestLength;
    }


    public void restlength(float theRestLength) {
        _myRestLength = theRestLength;
    }


    public final Particle a() {
        return _myA;
    }


    public final Particle b() {
        return _myB;
    }


    public void setOneWay(boolean theOneWayState) {
        _myOneWay = theOneWayState;
    }


    public void apply(Physics theParticleSystem) {
        if (_myA.fixed() && _myB.fixed()) {
            return;
        }
        _myTempDistanceVector.sub(_myA.position(), _myB.position());
        final float myDistanceSquared = _myTempDistanceVector.lengthSquared();
        if (myDistanceSquared > 0) {
            final float myDistance = (float) Math.sqrt(myDistanceSquared);
            final float myDifference = _myRestLength - myDistance;
            if (myDifference > EPSILON || myDifference < -EPSILON) {
                if (!_myOneWay) {
                    final float myDifferenceScale = _myDamping * 0.5f * myDifference / myDistance;
                    _myTempVector.scale(myDifferenceScale, _myTempDistanceVector);
                    if (_myA.fixed()) {
                        _myB.position().sub(_myTempVector);
                        _myB.position().sub(_myTempVector);
                    } else if (_myB.fixed()) {
                        _myA.position().add(_myTempVector);
                        _myA.position().add(_myTempVector);
                    } else {
                        _myA.position().add(_myTempVector);
                        _myB.position().sub(_myTempVector);
                    }
                } else {
                    final float myDifferenceScale = myDifference / myDistance;
                    _myTempVector.scale(myDifferenceScale, _myTempDistanceVector);
                    _myB.position().sub(_myTempVector);
                }
            }
        } else {
            if (_myA.fixed()) {
                _myB.position().set(_myA.position());
                _myB.position().x += _myRestLength;
            } else if (_myB.fixed()) {
                _myA.position().set(_myB.position());
                _myA.position().x += _myRestLength;
            } else {
                _myB.position().set(_myA.position());
                _myA.position().x -= _myRestLength / 2;
                _myB.position().x += _myRestLength / 2;
            }
        }
    }
}
