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
 * write the current rotation into the result transformation matrix.
 * if the velocity sampler is enabled the velocity will averaged
 * to avoid jerky turning.
 */


package verhalten;


import mathematik.TransformMatrix4f;
import mathematik.Vector3f;


/**
 * @deprecated PORTED TO MATHEMATIK
 */

public class Rotation {

    private Vector3f[] _myVelocitySampler;

    private final Vector3f _myUpVector;

    private final Vector3f _myCurrentAverageVelocity;

    private final Vector3f _myAverageVelocity;

    private int _myVelocitySamplerPosition;

    private final Vector3f _myTempUpVector;

    private final Vector3f _myTempSideVector;

    private final Vector3f _myTempForwardVector;

    public Rotation() {
        this(1);
    }


    public Rotation(final int theVelocitySamplerSize) {
        _myVelocitySamplerPosition = 0;
        setVelocitySamplerSize(theVelocitySamplerSize);
        _myUpVector = new Vector3f(0, 0, 1);
        _myAverageVelocity = new Vector3f();
        _myCurrentAverageVelocity = new Vector3f();
        _myTempUpVector = new Vector3f();
        _myTempSideVector = new Vector3f();
        _myTempForwardVector = new Vector3f();
    }


    public void setVelocitySamplerSize(final int theVelocitySamplerSize) {
        _myVelocitySampler = new Vector3f[theVelocitySamplerSize];
        for (int i = 0; i < _myVelocitySampler.length; ++i) {
            _myVelocitySampler[i] = new Vector3f();
        }
    }


    public void flattenVelocitySampler(final Vector3f theVelocity) {
        for (int i = 0; i < _myVelocitySampler.length; i++) {
            addEntry(theVelocity);
        }
    }


    private void addEntry(final Vector3f theVelocity) {
        _myVelocitySamplerPosition++;
        _myVelocitySamplerPosition %= _myVelocitySampler.length;
        _myAverageVelocity.sub(_myVelocitySampler[_myVelocitySamplerPosition]);
        _myVelocitySampler[_myVelocitySamplerPosition].set(theVelocity);
        _myAverageVelocity.add(theVelocity);
        _myCurrentAverageVelocity.set(_myAverageVelocity);
        _myCurrentAverageVelocity.scale(1.0f / (float) _myVelocitySampler.length);
    }


    public void setRotationMatrix(final Vector3f theVelocity,
                                  final TransformMatrix4f theResult) {
        if (_myVelocitySampler.length == 1) {
            pointAt(theVelocity, theResult);
        } else {
            addEntry(theVelocity);
            pointAt(_myCurrentAverageVelocity, theResult);
        }
    }


    public Vector3f getAverageVelocity() {
        return _myCurrentAverageVelocity;
    }


    public void setUpVector(final Vector3f theUpVector) {
        _myUpVector.set(theUpVector);
    }


    public void setUpVectorByAngle(final float roll) {
        // this is not thought out...
        _myUpVector.x = (float) Math.sin(roll);
        _myUpVector.z = - (float) Math.cos(roll);
        _myUpVector.y = 0;
    }


    private void pointAt(final Vector3f theForwardVector,
                         TransformMatrix4f theResult) {
        // get sideVector
        _myTempSideVector.cross(_myUpVector, theForwardVector);
        _myTempSideVector.normalize();
        // get 'real' upVector
        _myTempUpVector.cross(_myTempSideVector, theForwardVector);
        _myTempUpVector.normalize();
        // get forwardVector
        _myTempForwardVector.set(theForwardVector);
        _myTempForwardVector.normalize();
        // fill transformation matrix
        theResult.rotation.setXAxis(_myTempForwardVector);
        theResult.rotation.setYAxis(_myTempSideVector);
        theResult.rotation.setZAxis(_myTempUpVector);
    }
}
