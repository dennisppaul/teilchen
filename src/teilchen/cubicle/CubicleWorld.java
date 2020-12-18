/*
 * Teilchen
 *
 * This file is part of the *teilchen* library (https://github.com/dennisppaul/teilchen).
 * Copyright (c) 2020 Dennis P Paul.
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

    public CubicleWorld(Vector3i pNumberOfAtoms) {
        this(pNumberOfAtoms.x, pNumberOfAtoms.y, pNumberOfAtoms.z);
    }

    public CubicleWorld(int pNumberOfXAtoms, int pNumberOfYAtoms, int pNumberOfZAtoms) {
        initializeAtoms(pNumberOfXAtoms, pNumberOfYAtoms, pNumberOfZAtoms);
        mTransform = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
        mScale = new PVector(1, 1, 1);
        mEntites = new ArrayList<>();
    }

    private void initializeAtoms(int pNumberOfXAtoms, int pNumberOfYAtoms, int pNumberOfZAtoms) {
        mWorld = new CubicleAtom[pNumberOfXAtoms][pNumberOfYAtoms][pNumberOfZAtoms];
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
        Iterator<ICubicleEntity> mIterator = mEntites.iterator();
        while (mIterator.hasNext()) {
            handleEntity(mIterator.next());
        }
    }

    public void add(ICubicleEntity pEntity) {
        mEntites.add(pEntity);
        pEntity.cubicle().set(OFF_WORLD, OFF_WORLD, OFF_WORLD);
        mOffWorld.add(pEntity);
    }

    public boolean remove(ICubicleEntity pEntity) {
        return removeFromCubicle(pEntity) && mEntites.remove(pEntity);
    }

    public void handleEntity(ICubicleEntity pEntity) {
        if (pEntity.isActive()) {

            /* transform entity position into cubicle world space */
            final Vector3i mIndex = worldposition2index(pEntity.position());

            /* handle entites position in cubicle grid */
            if (checkBounds(mIndex.x, mIndex.y, mIndex.z)) {
                if (pEntity.leaving(mIndex.x, mIndex.y, mIndex.z)) {
                    /* remove from previous cubicles */
                    if (!removeFromCubicle(pEntity)) {
                        System.err.println("### ERROR @ CubicleWorld / removing entity / inworld");
                    }
                    /* add to current cubicle */
                    mWorld[mIndex.x][mIndex.y][mIndex.z].add(pEntity);
                    /* store cubicle */
                    pEntity.cubicle().set(mIndex.x, mIndex.y, mIndex.z);
                }
            } else if (pEntity.leaving(OFF_WORLD, OFF_WORLD, OFF_WORLD)) {
                /* remove from cubicles */
                if (!removeFromCubicle(pEntity)) {
                    System.err.println("### ERROR @ CubicleWorld / removing entity / offworld");
                }
                /* add to off world */
                mOffWorld.add(pEntity);

                /* store cubicle */
                pEntity.cubicle().set(OFF_WORLD, OFF_WORLD, OFF_WORLD);
            }
        }
    }

    public ArrayList<ICubicleEntity> getLocalEntities(PVector pPosition) {
        final Vector3i mIndex = worldposition2index(pPosition);
        if (checkBounds(mIndex.x, mIndex.y, mIndex.z)) {
            final CubicleAtom mCubicleAtom = getAtom(mIndex.x, mIndex.y, mIndex.z);
            return mCubicleAtom.data();
        }
        return null;
    }

    public ArrayList<ICubicleEntity> getLocalEntities(ICubicleEntity pEntity) {
        final Vector3i mIndex = pEntity.cubicle();
        return getAtom(mIndex.x, mIndex.y, mIndex.z).data();
    }

    public ArrayList<ICubicleEntity> getLocalEntities(PVector pPosition, int pExtraRadius) {
        return getLocalEntities(pPosition, pExtraRadius, pExtraRadius, pExtraRadius);
    }

    public ArrayList<ICubicleEntity> getLocalEntities(PVector pPosition,
                                                      int pXRadius,
                                                      int pYRadius,
                                                      int pZRadius) {
        final Vector3i mIndex = worldposition2index(pPosition);
        if (checkBounds(mIndex.x, mIndex.y, mIndex.z)) {
            final ArrayList<CubicleAtom> mAtoms = getAtoms(mIndex.x,
                                                           mIndex.y,
                                                           mIndex.z,
                                                           pXRadius,
                                                           pYRadius,
                                                           pZRadius);
            final ArrayList<ICubicleEntity> mEntities = new ArrayList<>();
            for (CubicleAtom a : mAtoms) {
                mEntities.addAll(a.data());
            }
            return mEntities.isEmpty() ? null : mEntities;
        } else {
            return null;
        }
    }

    public ArrayList<ICubicleEntity> getLocalEntities(ICubicleEntity pEntity,
                                                      int pXRadius,
                                                      int pYRadius,
                                                      int pZRadius) {
        final Vector3i mIndex = pEntity.cubicle();
        final ArrayList<CubicleAtom> mAtoms = getAtoms(mIndex.x,
                                                       mIndex.y,
                                                       mIndex.z,
                                                       pXRadius,
                                                       pYRadius,
                                                       pZRadius);
        final ArrayList<ICubicleEntity> mEntities = new ArrayList<>();
        for (CubicleAtom a : mAtoms) {
            mEntities.addAll(a.data());
        }
        return mEntities.isEmpty() ? null : mEntities;
    }

    public ArrayList<ICubicleEntity> entities() {
        return mEntites;
    }

    public Vector3i worldposition2index(PVector pPosition) {
        /* get position */
        final PVector mPosition = Util.clone(pPosition);

        /* translation */
        mPosition.sub(mTransform.translation);

        /* rotation */
        mTransform.rotation.transform(mPosition);

        /* scale */
        Util.divide(mPosition, mScale);

        /* round off */
        final Vector3i mIndex = new Vector3i((int) Math.floor(mPosition.x),
                                              (int) Math.floor(mPosition.y),
                                              (int) Math.floor(mPosition.z));
        return mIndex;
    }

    private boolean removeFromCubicle(ICubicleEntity pEntity) {
        if (pEntity.cubicle().x == OFF_WORLD && pEntity.cubicle().y == OFF_WORLD && pEntity.cubicle().z == OFF_WORLD) {
            /* was stored in the offworld cubicle */
            return mOffWorld.remove(pEntity);
        } else if (checkBounds(pEntity.cubicle().x, pEntity.cubicle().y, pEntity.cubicle().z)) {
            /* was stored in a cubicle */
            return mWorld[pEntity.cubicle().x][pEntity.cubicle().y][pEntity.cubicle().z].remove(pEntity);
        } else {
            /* values were invalid */
            System.out.println("### WARNING @ CubicleWorld / couldn t remove entity");
            return false;
        }
    }

    private boolean checkBounds(int pX, int pY, int pZ) {
        if (pX < mWorld.length && pX >= 0) {
            if (pY < mWorld[pX].length && pY >= 0) {
                if (pZ < mWorld[pX][pY].length && pZ >= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public CubicleAtom getAtom(int pX, int pY, int pZ) {
        if (checkBounds(pX, pY, pZ)) {
            return mWorld[pX][pY][pZ];
        } else {
            return mOffWorld;
        }
    }

    public ArrayList<CubicleAtom> getAtoms(int pX,
                                           int pY,
                                           int pZ,
                                           int pXRadius,
                                           int pYRadius,
                                           int pZRadius) {
        ArrayList<CubicleAtom> mAtoms = new ArrayList<>();
        for (int z = -pZRadius; z < pZRadius + 1; ++z) {
            for (int y = -pYRadius; y < pYRadius + 1; ++y) {
                for (int x = -pXRadius; x < pXRadius + 1; ++x) {
                    int mX = pX + x;
                    int mY = pY + y;
                    int mZ = pZ + z;
                    if (checkBounds(mX, mY, mZ) && mWorld[mX][mY][mZ].size() > 0) {
                        mAtoms.add(mWorld[mX][mY][mZ]);
                    }
                }
            }
        }
        return mAtoms;
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
