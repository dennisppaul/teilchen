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
 * 'cohesion' steers towards the center of mass of a group of objects.
 */


package verhalten;


import java.util.Vector;

import mathematik.Vector3f;


/**
 * @deprecated 
 */
public class Cohesion
    implements IVerhaltenBehavior {

    private Seek _mySeek;

    private Vector3f _myDebugResult;

    public Cohesion() {
        _mySeek = new Seek();
        _myDebugResult = new Vector3f();
    }


    public void get(final IVerhaltenEntity[] theNeighbors,
                    final Engine theEngine,
                    final Vector3f theDirection) {
        theDirection.set(0, 0, 0);
        if (theNeighbors.length != 0) {
            for (int i = 0; i < theNeighbors.length; i++) {
                theDirection.add(theNeighbors[i].position());
            }
            theDirection.scale(1.0f / (float) theNeighbors.length);
            _mySeek.setPoint(theDirection);
            _mySeek.get(theEngine, theDirection);
        }
    }


    public<E extends IVerhaltenEntity>void get(final Vector<E> theNeighbors,
                                               final Engine theEngine,
                                               final Vector3f theDirection) {
        get(theNeighbors,
            theNeighbors.size(),
            theEngine,
            theDirection);
    }


    public<E extends IVerhaltenEntity>void get(final Vector<E> theNeighbors,
                                               final int theNumberOfEntities,
                                               final Engine theEngine,
                                               final Vector3f theDirection) {

        final int myNumberOfEntities;
        if (theNumberOfEntities < theNeighbors.size()) {
            myNumberOfEntities = theNumberOfEntities;
        } else {
            myNumberOfEntities = theNeighbors.size();
        }

        theDirection.set(0, 0, 0);
        if (!theNeighbors.isEmpty()) {
            for (int i = 0; i < myNumberOfEntities; i++) {
                theDirection.add(theNeighbors.get(i).position());
            }
            theDirection.scale(1.0f / (float) myNumberOfEntities);
            _mySeek.setPoint(theDirection);
            _mySeek.get(theEngine, theDirection);
        }
    }


    public Vector3f direction() {
        return _myDebugResult;
    }
}
