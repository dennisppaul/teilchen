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
 * 'containment' steers to keep an object inside a bounding shape.
 */


package verhalten;


import mathematik.Vector3f;


/*
 * Contaiment is, as of now, restricted to a world-axis-aligned bounding box.
 * Future additionions are a sphere shaped container, a rotated bounding box
 * or even an arbitrary shape.
 *
 * also a containment by planes and rectangles would be cool which would then
 * be more of a deflector type.
 *
 * there is an issue with testing against all the position components at the
 * same time. it can result in a crowding around the center behavior.
 */


/**
 * @deprecated 
 */
public class Containment
    implements Verhalten {

    private Vector3f _myContainerPosition;

    private Vector3f _myContainerSize;

    private int _myTestingType;

    public Containment() {
        _myTestingType = CONTAINMENT_SINGLE_COMPONENT;
        _myContainerPosition = new Vector3f();
        _myContainerSize = new Vector3f();
    }


    public void get(final Vector3f thePosition, Vector3f theDirection) {
        switch (_myTestingType) {
            case CONTAINMENT_SINGLE_COMPONENT:
                if (thePosition.x <= (_myContainerPosition.x + _myContainerSize.x / 2)
                    && thePosition.x >= (_myContainerPosition.x - _myContainerSize.x / 2)) {
                    theDirection.x = 0;
                } else {
                    theDirection.x = _myContainerPosition.x - thePosition.x;
                }

                if (thePosition.y <= (_myContainerPosition.y + _myContainerSize.y / 2)
                    && thePosition.y >= (_myContainerPosition.y - _myContainerSize.y / 2)) {
                    theDirection.y = 0;
                } else {
                    theDirection.y = _myContainerPosition.y - thePosition.y;
                }

                if (thePosition.z <= (_myContainerPosition.z + _myContainerSize.z / 2)
                    && thePosition.z >= (_myContainerPosition.z - _myContainerSize.z / 2)) {
                    theDirection.z = 0;
                } else {
                    theDirection.z = _myContainerPosition.z - thePosition.z;
                }
                break;
            case CONTAINMENT_ALL_COMPONENTS:
                if (thePosition.x <= (_myContainerPosition.x + _myContainerSize.x / 2)
                    && thePosition.x >= (_myContainerPosition.x - _myContainerSize.x / 2)
                    && thePosition.y <= (_myContainerPosition.y + _myContainerSize.y / 2)
                    && thePosition.y >= (_myContainerPosition.y - _myContainerSize.y / 2)
                    && thePosition.z <= (_myContainerPosition.z + _myContainerSize.z / 2)
                    && thePosition.z >= (_myContainerPosition.z - _myContainerSize.z / 2)
                    ) {
                    theDirection.x = 0;
                    theDirection.y = 0;
                    theDirection.z = 0;
                } else {
                    theDirection.sub(_myContainerPosition, thePosition);
                }
                break;
            default:
                break;
        }
    }


    public void ensureContainment(final Vector3f thePosition) {
        if (thePosition.x >= (_myContainerPosition.x + _myContainerSize.x / 2)) {
            thePosition.x = _myContainerPosition.x + _myContainerSize.x / 2;
        }
        if (thePosition.x <= (_myContainerPosition.x - _myContainerSize.x / 2)) {
            thePosition.x = _myContainerPosition.x - _myContainerSize.x / 2;
        }

        if (thePosition.y >= (_myContainerPosition.y + _myContainerSize.y / 2)) {
            thePosition.y = _myContainerPosition.y + _myContainerSize.y / 2;
        }
        if (thePosition.y <= (_myContainerPosition.y - _myContainerSize.y / 2)) {
            thePosition.y = _myContainerPosition.y - _myContainerSize.y / 2;
        }

        if (thePosition.z >= (_myContainerPosition.z + _myContainerSize.z / 2)) {
            thePosition.z = _myContainerPosition.z + _myContainerSize.z / 2;
        }
        if (thePosition.z <= (_myContainerPosition.z - _myContainerSize.z / 2)) {
            thePosition.z = _myContainerPosition.z - _myContainerSize.z / 2;
        }
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
}
