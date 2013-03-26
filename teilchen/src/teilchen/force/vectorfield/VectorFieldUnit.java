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
package teilchen.force.vectorfield;


import mathematik.Vector3f;


public class VectorFieldUnit {

    private Vector3f _myPosition;

    private Vector3f _myScale;

    private Vector3f _myAcceleration;

    public VectorFieldUnit(final Vector3f thePosition,
                           final Vector3f theScale,
                           final Vector3f theAcceleration) {
        _myPosition = new Vector3f(thePosition);
        _myScale = new Vector3f(theScale);
        _myAcceleration = new Vector3f(theAcceleration);
    }

    public void setAcceleration(final Vector3f theAcceleration) {
        _myAcceleration.set(theAcceleration);
    }

    public Vector3f getAcceleration() {
        return _myAcceleration;
    }

    public Vector3f getPosition() {
        return _myPosition;
    }

    public void setPosition(final Vector3f thePosition) {
        _myPosition.set(thePosition);
    }

    public Vector3f getScale() {
        return _myScale;
    }

    public void setScale(final Vector3f theScale) {
        _myScale.set(theScale);
    }

    public boolean isInside(final Vector3f thePosition) {
        if (thePosition.x >= _myPosition.x
                && thePosition.x < _myPosition.x + _myScale.x
                && thePosition.y >= _myPosition.y
                && thePosition.y < _myPosition.y + _myScale.y
                && thePosition.z >= _myPosition.z
                && thePosition.z < _myPosition.z + _myScale.z) {
            return true;
        } else {
            return false;
        }
    }
}
