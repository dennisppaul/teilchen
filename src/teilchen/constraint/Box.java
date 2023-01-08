/*
 * Teilchen
 *
 * This file is part of the *teilchen* library (https://github.com/dennisppaul/teilchen).
 * Copyright (c) 2023 Dennis P Paul.
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

import processing.core.PVector;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.integration.Verlet;
import teilchen.util.Util;

import static processing.core.PVector.sub;

public class Box implements IConstraint {

    private static final PVector[] NORMALS;
    protected boolean mActive = true;
    private float mCoefficientOfRestitution;
    private boolean mDead = false;
    private final long mID;
    private final PVector mMax;
    private final PVector mMin;
    private boolean mReflectFlag;
    private boolean mTeleport;

    static {
        NORMALS = new PVector[6];
        NORMALS[0] = new PVector(-1, 0, 0);
        NORMALS[1] = new PVector(0, -1, 0);
        NORMALS[2] = new PVector(0, 0, -1);
        NORMALS[3] = new PVector(1, 0, 0);
        NORMALS[4] = new PVector(0, 1, 0);
        NORMALS[5] = new PVector(0, 0, 1);
    }

    public Box(final PVector pMin, final PVector pMax) {
        mID = Physics.getUniqueID();
        mMin = pMin;
        mMax = pMax;
        mReflectFlag = true;
        mCoefficientOfRestitution = 1.0f;
        mTeleport = false;
    }

    public Box() {
        this(new PVector(), new PVector());
    }

    public void telelport(boolean pTeleportState) {
        mTeleport = pTeleportState;
    }

    public void reflect(boolean pReflectState) {
        mReflectFlag = pReflectState;
    }

    public PVector min() {
        return mMin;
    }

    public PVector max() {
        return mMax;
    }

    public void coefficientofrestitution(float pCoefficientOfRestitution) {
        mCoefficientOfRestitution = pCoefficientOfRestitution;
    }

    public float coefficientofrestitution() {
        return mCoefficientOfRestitution;
    }

    public void apply(final Physics pParticleSystem) {

        if (!mActive) {
            return;
        }

        for (final Particle myParticle : pParticleSystem.particles()) {
            if (mTeleport) {
                if (myParticle.position().x > mMax.x) {
                    myParticle.position().x = mMin.x;
                }
                if (myParticle.position().y > mMax.y) {
                    myParticle.position().y = mMin.y;
                }
                if (myParticle.position().z > mMax.z) {
                    myParticle.position().z = mMin.z;
                }
                if (myParticle.position().x < mMin.x) {
                    myParticle.position().x = mMax.x;
                }
                if (myParticle.position().y < mMin.y) {
                    myParticle.position().y = mMax.y;
                }
                if (myParticle.position().z < mMin.z) {
                    myParticle.position().z = mMax.z;
                }
            } else {
                /**
                 * @todo to do this properly we would need to add the normals
                 * and normalize them. maybe later.
                 */
                int myTag = -1;
                final PVector myPosition = Util.clone(myParticle.position());
                if (myParticle.position().x > mMax.x) {
                    myParticle.position().x = mMax.x;
                    myTag = 0;
                }
                if (myParticle.position().y > mMax.y) {
                    myParticle.position().y = mMax.y;
                    myTag = 1;
                }
                if (myParticle.position().z > mMax.z) {
                    myParticle.position().z = mMax.z;
                    myTag = 2;
                }
                if (myParticle.position().x < mMin.x) {
                    myParticle.position().x = mMin.x;
                    myTag = 3;
                }
                if (myParticle.position().y < mMin.y) {
                    myParticle.position().y = mMin.y;
                    myTag = 4;
                }
                if (myParticle.position().z < mMin.z) {
                    myParticle.position().z = mMin.z;
                    myTag = 5;
                }
                if (myTag >= 0) {
                    if (mReflectFlag) {
                        if (pParticleSystem.getIntegrator() instanceof Verlet) {
                            final PVector myDiff = sub(myPosition, myParticle.position());
                            teilchen.util.Util.reflect(myDiff, NORMALS[myTag], mCoefficientOfRestitution);
//                            System.out.println("### reflect " + _myNormals[myTag]);
//                            System.out.println("myDiff " + myDiff);
                            myParticle.old_position().sub(myDiff);
                        } else {
                            teilchen.util.Util.reflectVelocity(myParticle, NORMALS[myTag], mCoefficientOfRestitution);
                        }
                    } else {
                        myParticle.velocity().set(0, 0, 0);
                    }
                }
            }
        }
    }

    public boolean active() {
        return mActive;
    }

    public void active(boolean pActiveState) {
        mActive = pActiveState;
    }

    public boolean dead() {
        return mDead;
    }

    public void dead(boolean pDead) {
        mDead = pDead;
    }

    public long ID() {
        return mID;
    }
}
