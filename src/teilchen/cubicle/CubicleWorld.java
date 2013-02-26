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


package teilchen.cubicle;


import java.util.Iterator;
import java.util.Vector;

import mathematik.TransformMatrix4f;
import mathematik.Vector3f;
import mathematik.Vector3i;


/*
 * cubicle world handles entities and queries about a cubicles state.
 */


public class CubicleWorld {

    public static final int OFF_WORLD = -1;

    private CubicleAtom[][][] _myWorld;

    private CubicleAtom _myOffWorld;

    private TransformMatrix4f _myTransform;

    private Vector3f _myScale;

    private Vector<ICubicleEntity> _myEntites;

    public CubicleWorld(Vector3i theNumberOfAtoms) {
        this(theNumberOfAtoms.x, theNumberOfAtoms.y, theNumberOfAtoms.z);
    }


    public CubicleWorld(int theNumberOfXAtoms,
                        int theNumberOfYAtoms,
                        int theNumberOfZAtoms) {
        initializeAtoms(theNumberOfXAtoms, theNumberOfYAtoms, theNumberOfZAtoms);
        _myTransform = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
        _myScale = new Vector3f(1, 1, 1);
        _myEntites = new Vector<ICubicleEntity> ();
    }


    private void initializeAtoms(int theNumberOfXAtoms,
                                 int theNumberOfYAtoms,
                                 int theNumberOfZAtoms) {
        _myWorld = new CubicleAtom[theNumberOfXAtoms][theNumberOfYAtoms][theNumberOfZAtoms];
        for (int x = 0; x < _myWorld.length; x++) {
            for (int y = 0; y < _myWorld[x].length; y++) {
                for (int z = 0; z < _myWorld[x][y].length; z++) {
                    _myWorld[x][y][z] = new CubicleAtom(x, y, z);
                }
            }
        }
        _myOffWorld = new CubicleAtom(OFF_WORLD, OFF_WORLD, OFF_WORLD);
    }


    public void update() {
        Iterator<ICubicleEntity> myIterator = _myEntites.iterator();
        while (myIterator.hasNext()) {
            handleEntity(myIterator.next());
        }
    }


    public void add(ICubicleEntity theEntity) {
        _myEntites.add(theEntity);
        theEntity.cubicle().set(OFF_WORLD, OFF_WORLD, OFF_WORLD);
        _myOffWorld.add(theEntity);
    }


    public boolean remove(ICubicleEntity theEntity) {
        return removeFromCubicle(theEntity) && _myEntites.remove(theEntity);
    }


    public void handleEntity(ICubicleEntity theEntity) {
        if (theEntity.isActive()) {

            /* transform entity position into cubicle world space */

            final Vector3i myIndex = worldposition2index(theEntity.position());

            /* handle entites position in cubicle grid */
            if (checkBounds(myIndex.x, myIndex.y, myIndex.z)) {
                if (theEntity.leaving(myIndex.x, myIndex.y, myIndex.z)) {
                    /* remove from previous cubicles */
                    if (!removeFromCubicle(theEntity)) {
                        System.err.println("### ERROR @ CubicleWorld / removing entity / inworld");
                    }
                    /* add to current cubicle */
                    _myWorld[myIndex.x][myIndex.y][myIndex.z].add(theEntity);
                    /* store cubicle */
                    theEntity.cubicle().set(myIndex.x, myIndex.y, myIndex.z);
                }
            } else {
                if (theEntity.leaving(OFF_WORLD, OFF_WORLD, OFF_WORLD)) {
                    /* remove from cubicles */
                    if (!removeFromCubicle(theEntity)) {
                        System.err.println("### ERROR @ CubicleWorld / removing entity / offworld");
                    }
                    /* add to off world */
                    _myOffWorld.add(theEntity);

                    /* store cubicle */
                    theEntity.cubicle().set(OFF_WORLD, OFF_WORLD, OFF_WORLD);
                }
            }
        }
    }


    public Vector<ICubicleEntity> getLocalEntities(Vector3f thePosition) {
        final Vector3i myIndex = worldposition2index(thePosition);
        if (checkBounds(myIndex.x, myIndex.y, myIndex.z)) {
            final CubicleAtom myCubicleAtom = getAtom(myIndex.x, myIndex.y, myIndex.z);
            myCubicleAtom.data();
        }
        return null;
    }


    public Vector<ICubicleEntity> getLocalEntities(ICubicleEntity theEntity) {
        final Vector3i myIndex = theEntity.cubicle();
        return getAtom(myIndex.x, myIndex.y, myIndex.z).data();
    }


    public Vector3i worldposition2index(Vector3f thePosition) {
        /* get position */
        final Vector3f myPosition = new Vector3f(thePosition);

        /* translation */
        myPosition.sub(_myTransform.translation);

        /* rotation */
        _myTransform.rotation.transform(myPosition);

        /* scale */
        myPosition.divide(_myScale);

        /* round off */
        final Vector3i myIndex = new Vector3i( (int) Math.floor(myPosition.x),
                                              (int) Math.floor(myPosition.y),
                                              (int) Math.floor(myPosition.z));
        return myIndex;
    }


    private boolean removeFromCubicle(ICubicleEntity theEntity) {
        if (theEntity.cubicle().x == OFF_WORLD &&
            theEntity.cubicle().y == OFF_WORLD &&
            theEntity.cubicle().z == OFF_WORLD) {
            /* was stored in the offworld cubicle */
            return _myOffWorld.remove(theEntity);
        } else {
            if (checkBounds(theEntity.cubicle().x, theEntity.cubicle().y, theEntity.cubicle().z)) {
                /* was stored in a cubicle */
                return _myWorld[theEntity.cubicle().x][theEntity.cubicle().y][theEntity.cubicle().z].remove(theEntity);
            } else {
                /* values were invalid */
                System.out.println("### WARNING @ CubicleWorld / couldn t remove entity");
                return false;
            }
        }
    }


    private boolean checkBounds(int theX,
                                int theY,
                                int theZ) {
        if (theX < _myWorld.length && theX >= 0) {
            if (theY < _myWorld[theX].length && theY >= 0) {
                if (theZ < _myWorld[theX][theY].length && theZ >= 0) {
                    return true;
                }
            }
        }
        return false;
    }


    public CubicleAtom getAtom(int theX,
                               int theY,
                               int theZ) {
        if (checkBounds(theX, theY, theZ)) {
            return _myWorld[theX][theY][theZ];
        } else {
            return _myOffWorld;
        }
    }


    public Vector<CubicleAtom> getAtoms(int theX,
                                        int theY,
                                        int theZ,
                                        int theXRadius,
                                        int theYRadius,
                                        int theZRadius) {
        Vector<CubicleAtom> myAtoms = new Vector<CubicleAtom> ();
        for (int z = -theZRadius; z < theZRadius + 1; ++z) {
            for (int y = -theYRadius; y < theYRadius + 1; ++y) {
                for (int x = -theXRadius; x < theXRadius + 1; ++x) {
                    int myX = theX + x;
                    int myY = theY + y;
                    int myZ = theZ + z;
                    if (checkBounds(myX, myY, myZ) && _myWorld[myX][myY][myZ].size() > 0) {
                        myAtoms.add(_myWorld[myX][myY][myZ]);
                    }
                }
            }
        }
        return myAtoms;
    }


    public Vector3f cellscale() {
        return _myScale;
    }


    public TransformMatrix4f transform() {
        return _myTransform;
    }


    public CubicleAtom[][][] getDataRef() {
        return _myWorld;
    }


    public Vector<ICubicleEntity> getEntities() {
        return _myEntites;
    }


    public CubicleAtom getOffWorldAtom() {
        return _myOffWorld;
    }
}
