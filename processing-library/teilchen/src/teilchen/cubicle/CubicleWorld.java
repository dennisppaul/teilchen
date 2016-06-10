/*
 * Teilchen
 *
 * Copyright (C) 2015
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

import processing.core.PVector;
import teilchen.util.TransformMatrix4f;
import teilchen.util.Util;
import teilchen.util.Vector3i;

import java.util.ArrayList;
import java.util.Iterator;

/*
 * cubicle world handles entities and queries about a cubicles state.
 */
public class CubicleWorld {

    public static final int OFF_WORLD = -1;

    private CubicleAtom[][][] mWorld;

    private CubicleAtom mOffWorld;

    private TransformMatrix4f mTransform;

    private PVector mScale;

    private ArrayList<ICubicleEntity> mEntites;

    public CubicleWorld(Vector3i theNumberOfAtoms) {
        this(theNumberOfAtoms.x, theNumberOfAtoms.y, theNumberOfAtoms.z);
    }

    public CubicleWorld(int theNumberOfXAtoms, int theNumberOfYAtoms, int theNumberOfZAtoms) {
        initializeAtoms(theNumberOfXAtoms, theNumberOfYAtoms, theNumberOfZAtoms);
        mTransform = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
        mScale = new PVector(1, 1, 1);
        mEntites = new ArrayList<>();
    }

    private void initializeAtoms(int theNumberOfXAtoms, int theNumberOfYAtoms, int theNumberOfZAtoms) {
        mWorld = new CubicleAtom[theNumberOfXAtoms][theNumberOfYAtoms][theNumberOfZAtoms];
        for (int x = 0; x < mWorld.length; x++) {
            for (int y = 0; y < mWorld[x].length; y++) {
                for (int z = 0; z < mWorld[x][y].length; z++) {
                    mWorld[x][y][z] = new CubicleAtom(x, y, z);
                }
            }
        }
        mOffWorld = new CubicleAtom(OFF_WORLD, OFF_WORLD, OFF_WORLD);
    }

    public void update() {
        Iterator<ICubicleEntity> myIterator = mEntites.iterator();
        while (myIterator.hasNext()) {
            handleEntity(myIterator.next());
        }
    }

    public void add(ICubicleEntity theEntity) {
        mEntites.add(theEntity);
        theEntity.cubicle().set(OFF_WORLD, OFF_WORLD, OFF_WORLD);
        mOffWorld.add(theEntity);
    }

    public boolean remove(ICubicleEntity theEntity) {
        return removeFromCubicle(theEntity) && mEntites.remove(theEntity);
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
                    mWorld[myIndex.x][myIndex.y][myIndex.z].add(theEntity);
                    /* store cubicle */
                    theEntity.cubicle().set(myIndex.x, myIndex.y, myIndex.z);
                }
            } else if (theEntity.leaving(OFF_WORLD, OFF_WORLD, OFF_WORLD)) {
                /* remove from cubicles */
                if (!removeFromCubicle(theEntity)) {
                    System.err.println("### ERROR @ CubicleWorld / removing entity / offworld");
                }
                /* add to off world */
                mOffWorld.add(theEntity);

                /* store cubicle */
                theEntity.cubicle().set(OFF_WORLD, OFF_WORLD, OFF_WORLD);
            }
        }
    }

    public ArrayList<ICubicleEntity> getLocalEntities(PVector thePosition) {
        final Vector3i myIndex = worldposition2index(thePosition);
        if (checkBounds(myIndex.x, myIndex.y, myIndex.z)) {
            final CubicleAtom myCubicleAtom = getAtom(myIndex.x, myIndex.y, myIndex.z);
            return myCubicleAtom.data();
        }
        return null;
    }

    public ArrayList<ICubicleEntity> getLocalEntities(ICubicleEntity theEntity) {
        final Vector3i myIndex = theEntity.cubicle();
        return getAtom(myIndex.x, myIndex.y, myIndex.z).data();
    }

    public ArrayList<ICubicleEntity> getLocalEntities(PVector thePosition, int pExtraRadius) {
        return getLocalEntities(thePosition, pExtraRadius, pExtraRadius, pExtraRadius);
    }

    public ArrayList<ICubicleEntity> getLocalEntities(PVector thePosition,
                                                      int theXRadius,
                                                      int theYRadius,
                                                      int theZRadius) {
        final Vector3i myIndex = worldposition2index(thePosition);
        if (checkBounds(myIndex.x, myIndex.y, myIndex.z)) {
            final ArrayList<CubicleAtom> mAtoms = getAtoms(myIndex.x,
                                                           myIndex.y,
                                                           myIndex.z,
                                                           theXRadius,
                                                           theYRadius,
                                                           theZRadius);
            final ArrayList<ICubicleEntity> mEntities = new ArrayList<>();
            for (CubicleAtom a : mAtoms) {
                mEntities.addAll(a.data());
            }
            return mEntities.isEmpty() ? null : mEntities;
        } else {
            return null;
        }
    }

    public ArrayList<ICubicleEntity> getLocalEntities(ICubicleEntity theEntity,
                                                      int theXRadius,
                                                      int theYRadius,
                                                      int theZRadius) {
        final Vector3i myIndex = theEntity.cubicle();
        final ArrayList<CubicleAtom> mAtoms = getAtoms(myIndex.x,
                                                       myIndex.y,
                                                       myIndex.z,
                                                       theXRadius,
                                                       theYRadius,
                                                       theZRadius);
        final ArrayList<ICubicleEntity> mEntities = new ArrayList<>();
        for (CubicleAtom a : mAtoms) {
            mEntities.addAll(a.data());
        }
        return mEntities.isEmpty() ? null : mEntities;
    }

    public ArrayList<ICubicleEntity> entities() {
        return mEntites;
    }

    public Vector3i worldposition2index(PVector thePosition) {
        /* get position */
        final PVector myPosition = Util.clone(thePosition);

        /* translation */
        myPosition.sub(mTransform.translation);

        /* rotation */
        mTransform.rotation.transform(myPosition);

        /* scale */
        Util.divide(myPosition, mScale);

        /* round off */
        final Vector3i myIndex = new Vector3i((int) Math.floor(myPosition.x),
                                              (int) Math.floor(myPosition.y),
                                              (int) Math.floor(myPosition.z));
        return myIndex;
    }

    private boolean removeFromCubicle(ICubicleEntity theEntity) {
        if (theEntity.cubicle().x == OFF_WORLD && theEntity.cubicle().y == OFF_WORLD && theEntity.cubicle().z == OFF_WORLD) {
            /* was stored in the offworld cubicle */
            return mOffWorld.remove(theEntity);
        } else if (checkBounds(theEntity.cubicle().x, theEntity.cubicle().y, theEntity.cubicle().z)) {
            /* was stored in a cubicle */
            return mWorld[theEntity.cubicle().x][theEntity.cubicle().y][theEntity.cubicle().z].remove(theEntity);
        } else {
            /* values were invalid */
            System.out.println("### WARNING @ CubicleWorld / couldn t remove entity");
            return false;
        }
    }

    private boolean checkBounds(int theX, int theY, int theZ) {
        if (theX < mWorld.length && theX >= 0) {
            if (theY < mWorld[theX].length && theY >= 0) {
                if (theZ < mWorld[theX][theY].length && theZ >= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public CubicleAtom getAtom(int theX, int theY, int theZ) {
        if (checkBounds(theX, theY, theZ)) {
            return mWorld[theX][theY][theZ];
        } else {
            return mOffWorld;
        }
    }

    public ArrayList<CubicleAtom> getAtoms(int theX,
                                           int theY,
                                           int theZ,
                                           int theXRadius,
                                           int theYRadius,
                                           int theZRadius) {
        ArrayList<CubicleAtom> myAtoms = new ArrayList<>();
        for (int z = -theZRadius; z < theZRadius + 1; ++z) {
            for (int y = -theYRadius; y < theYRadius + 1; ++y) {
                for (int x = -theXRadius; x < theXRadius + 1; ++x) {
                    int myX = theX + x;
                    int myY = theY + y;
                    int myZ = theZ + z;
                    if (checkBounds(myX, myY, myZ) && mWorld[myX][myY][myZ].size() > 0) {
                        myAtoms.add(mWorld[myX][myY][myZ]);
                    }
                }
            }
        }
        return myAtoms;
    }

    public PVector cellscale() {
        return mScale;
    }

    public TransformMatrix4f transform() {
        return mTransform;
    }

    public CubicleAtom[][][] getDataRef() {
        return mWorld;
    }

    public ArrayList<ICubicleEntity> getEntities() {
        return mEntites;
    }

    public CubicleAtom getOffWorldAtom() {
        return mOffWorld;
    }

    public void removeAll() {
        final Iterator<ICubicleEntity> iter = mEntites.iterator();
        while (iter.hasNext()) {
            final ICubicleEntity c = iter.next();
            removeFromCubicle(c);
            iter.remove();
        }
    }
}
