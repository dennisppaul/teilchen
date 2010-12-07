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
 * 'seperation' steers away from a group of objects.
 */

/** @todo implement the overlap remover */


package verhalten;


import java.io.Serializable;
import java.util.Vector;

import mathematik.Vector3f;


public class Separation
    implements IVerhaltenBehavior, Verhalten, Serializable {

    private static final long serialVersionUID = -4953599448151741585L;

    private float _myPrivacyRadius;

    private int _myMode;

    private Vector3f _myDirectionToNeighbor;

    private Vector3f _myDebugDirection;

    public Separation() {
        _myPrivacyRadius = 100.0f;
        _myMode = SEEK_CONSIDER_CLOSEST_NEIGHBOR;
        _myDirectionToNeighbor = new Vector3f();
        _myDebugDirection = new Vector3f();
    }


    /** @todo make a more general iterator concept for the get() method. */

    public void get(final IVerhaltenEntity[] theNeighbors,
                    final Vector3f thePosition,
                    Vector3f theDirection) {

        float[] myDistanceToNeighbors = new float[theNeighbors.length];
        float minDistanceToNeighbors = _myPrivacyRadius;
        int myIndexOfClosestNeighbor = -1;

        for (int i = 0; i < theNeighbors.length; ++i) {
            _myDirectionToNeighbor.sub(thePosition,
                                       theNeighbors[i].position());
            switch (_myMode) {
                case SEEK_CONSIDER_CLOSEST_NEIGHBOR:
                    myDistanceToNeighbors[i] = _myDirectionToNeighbor.length();

                    /**
                     * @todo getting length squared *could* make things faster.
                     *       myDistanceToNeighbors[i] =
                     *       myDistanceToNeighbor.lengthSquared();
                     */
                    if (myDistanceToNeighbors[i] != 0 && minDistanceToNeighbors > myDistanceToNeighbors[i]) {
                        minDistanceToNeighbors = myDistanceToNeighbors[i];
                        myIndexOfClosestNeighbor = i;
                    }
                    break;
                case SEEK_CONSIDER_ALL_NEIGHBORS:
                    myDistanceToNeighbors[i] = _myDirectionToNeighbor.length();
                    break;
            }
        }

        switch (_myMode) {
            case SEEK_CONSIDER_CLOSEST_NEIGHBOR:
                if (myIndexOfClosestNeighbor != -1
                    && myDistanceToNeighbors[myIndexOfClosestNeighbor] < _myPrivacyRadius) {
                    theDirection.sub(thePosition,
                                     theNeighbors[myIndexOfClosestNeighbor].position());
                    theDirection.scale(1.0f / myDistanceToNeighbors[myIndexOfClosestNeighbor]);
                    theDirection.scale(_myPrivacyRadius - myDistanceToNeighbors[myIndexOfClosestNeighbor]);
                } else {
                    theDirection.set(0,
                                     0,
                                     0);
                }
                break;
            case SEEK_CONSIDER_ALL_NEIGHBORS:
                theDirection.set(0,
                                 0,
                                 0);
                for (int i = 0; i < theNeighbors.length; ++i) {
                    if (myDistanceToNeighbors[i] > 0.0f && myDistanceToNeighbors[i] < _myPrivacyRadius) {
                        theDirection.sub(thePosition,
                                         theNeighbors[i].position());
                        theDirection.scale(1.0f - myDistanceToNeighbors[i] / _myPrivacyRadius);
                        theDirection.add(theDirection);
                    }
                }
                theDirection.scale(1.0f / (float) theNeighbors.length);
                break;
        }
        _myDebugDirection.set(theDirection);
    }


    public<E extends IVerhaltenEntity>void get(final Vector<E> theNeighbors,
                                               final Vector3f thePosition,
                                               Vector3f theDirection) {
        get(theNeighbors,
            theNeighbors.size(),
            thePosition,
            theDirection);
    }


    public<E extends IVerhaltenEntity>void get(final Vector<E> theNeighbors,
                                               final int theNumberOfEntities,
                                               final Vector3f thePosition,
                                               final Vector3f theDirection) {

        final int myNumberOfEntities;
        if (theNumberOfEntities < theNeighbors.size()) {
            myNumberOfEntities = theNumberOfEntities;
        } else {
            myNumberOfEntities = theNeighbors.size();
        }

        final float[] myDistanceToNeighbors = new float[myNumberOfEntities];
        float minDistanceToNeighbors = _myPrivacyRadius;
        int myIndexOfClosestNeighbor = -1;

        for (int i = 0; i < myNumberOfEntities; ++i) {
            _myDirectionToNeighbor.sub(thePosition,
                                       theNeighbors.get(i).position());
            switch (_myMode) {
                case SEEK_CONSIDER_CLOSEST_NEIGHBOR:
                    myDistanceToNeighbors[i] = _myDirectionToNeighbor.length();

                    /**
                     * @todo getting length squared *could* make things faster.
                     *       myDistanceToNeighbors[i] =
                     *       myDistanceToNeighbor.lengthSquared();
                     */
                    if (myDistanceToNeighbors[i] != 0 && minDistanceToNeighbors > myDistanceToNeighbors[i]) {
                        minDistanceToNeighbors = myDistanceToNeighbors[i];
                        myIndexOfClosestNeighbor = i;
                    }
                    break;
                case SEEK_CONSIDER_ALL_NEIGHBORS:
                    myDistanceToNeighbors[i] = _myDirectionToNeighbor.length();
                    break;
            }
        }

        switch (_myMode) {
            case SEEK_CONSIDER_CLOSEST_NEIGHBOR:
                if (myIndexOfClosestNeighbor != -1
                    && myDistanceToNeighbors[myIndexOfClosestNeighbor] < _myPrivacyRadius) {
                    theDirection.sub(thePosition,
                                     theNeighbors.get(myIndexOfClosestNeighbor).position());
                    theDirection.scale(1.0f / myDistanceToNeighbors[myIndexOfClosestNeighbor]);
                    theDirection.scale(_myPrivacyRadius - myDistanceToNeighbors[myIndexOfClosestNeighbor]);
                } else {
                    theDirection.set(0,
                                     0,
                                     0);
                }
                break;
            case SEEK_CONSIDER_ALL_NEIGHBORS:
                theDirection.set(0,
                                 0,
                                 0);
                for (int i = 0; i < myNumberOfEntities; ++i) {
                    if (myDistanceToNeighbors[i] > 0.0f && myDistanceToNeighbors[i] < _myPrivacyRadius) {
                        theDirection.sub(thePosition,
                                         theNeighbors.get(i).position());
                        theDirection.scale(1.0f - myDistanceToNeighbors[i] / _myPrivacyRadius);
                        theDirection.add(theDirection);
                    }
                }
                theDirection.scale(1.0f / (float) myNumberOfEntities);
                break;
        }

        _myDebugDirection.set(theDirection);
    }


    /** @todo
     * this can be very useful but not as flexible as the method above
     *
     *    public void get(final Vector3f[] theNeighborsPositions,
     *                    final Vector3f thePosition,
     *                    Vector3f theDirection);
     */

    public float getPrivacyRadius() {
        return _myPrivacyRadius;
    }


    public void setPrivacyRadius(float thePrivacyRadius) {
        _myPrivacyRadius = thePrivacyRadius;
    }


    public Vector3f direction() {
        return _myDebugDirection;
    }

}
