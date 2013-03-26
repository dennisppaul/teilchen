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
 * 'exclusion' steers to keep an object outside a bounding shape.
 */


package verhalten;


import mathematik.Vector3f;


public class Exclusion
    implements Verhalten {

    private Vector3f _myContainerPosition;

    private Vector3f _myContainerSize;

    private int _myTestingType;

    private float _myInfluence;

    public Exclusion() {
        _myContainerPosition = new Vector3f(0, 0, 0);
        _myContainerSize = new Vector3f(0, 0, 0);
        _myTestingType = EXCLUSION_ALL_COMPONENTS;
        _myInfluence = 200.0f;
    }


    public void setPosition(final Vector3f thePosition) {
        _myContainerPosition.set(thePosition);
    }


    public void setPositionRef(final Vector3f thePosition) {
        _myContainerPosition = thePosition;
    }


    public Vector3f getPosition() {
        return _myContainerPosition;
    }


    public void setSize(final Vector3f theSize) {
        _myContainerSize.set(theSize);
    }


    public void setSizeRef(final Vector3f theSize) {
        _myContainerSize = theSize;
    }


    public Vector3f getSize() {
        return _myContainerSize;
    }


    public void setInfluence(float theInfluence) {
        _myInfluence = theInfluence;
    }


    public float getInfluence() {
        return _myInfluence;
    }


    public void get(final Vector3f thePosition, Vector3f theDirection) {
        switch (_myTestingType) {
            case EXCLUSION_SINGLE_COMPONENT:

                /** @todo does this make any sense ? */
                theDirection.set(0, 0, 0);
                if (contains(thePosition.x, _myContainerPosition.x, _myContainerSize.x)) {
                    theDirection.x = thePosition.x - _myContainerPosition.x;
                } else {
                    theDirection.x = 0;
                }

                if (contains(thePosition.y, _myContainerPosition.y, _myContainerSize.y)) {
                    theDirection.y = thePosition.y - _myContainerPosition.y;
                } else {
                    theDirection.y = 0;
                }

                if (contains(thePosition.z, _myContainerPosition.z, _myContainerSize.z)) {
                    theDirection.z = thePosition.z - _myContainerPosition.z;
                } else {
                    theDirection.z = 0;
                }
                break;
            case EXCLUSION_ALL_COMPONENTS:
                if (contains(thePosition.x, _myContainerPosition.x, _myContainerSize.x) &&
                    contains(thePosition.y, _myContainerPosition.y, _myContainerSize.y) &&
                    contains(thePosition.z, _myContainerPosition.z, _myContainerSize.z)) {
                    theDirection.sub(thePosition, _myContainerPosition);
                    float theDistanceToCenter = theDirection.length();
                    if (theDistanceToCenter > 0) {
                        theDirection.scale(_myInfluence / theDistanceToCenter);
                    } else {
                        /**
                         * if _myContainerPosition and thePosition are the same
                         * normalization results in an division by zero
                         */
                        theDirection.set(_myInfluence, 0, 0);
                    }
                } else {
                    theDirection.set(0, 0, 0);
                }
                break;
            default:
                break;
        }
    }


    private boolean contains(float theTestValue, float theContainerValue, float theRange) {
        if (theTestValue > theContainerValue - theRange / 2.0f &&
            theTestValue < theContainerValue + theRange / 2.0f) {
            return true;
        } else {
            return false;
        }
    }
}
