/*
 * Teilchen
 *
 * This file is part of the *teilchen* library (https://github.com/dennisppaul/teilchen).
 * Copyright (c) 2024 Dennis P Paul.
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
import teilchen.IParticle;
import teilchen.Physics;
import teilchen.integration.Verlet;
import teilchen.util.Util;

import java.util.ArrayList;

public class ReflectBox implements IConstraint {

    private static final PVector[] mNormals;
    public boolean NEGATIVE_X = true;
    public boolean NEGATIVE_Y = true;
    public boolean NEGATIVE_Z = true;
    public boolean POSITIV_X = true;
    public boolean POSITIV_Y = true;
    public boolean POSITIV_Z = true;
    protected boolean mActive = true;
    private float mCoefficientOfRestitution;
    private boolean mDead = false;
    private float mEpsilon;
    private final long mID;
    private final PVector mMax;
    private final PVector mMin;

    static {
        mNormals = new PVector[6];
        mNormals[0] = new PVector(-1, 0, 0);
        mNormals[1] = new PVector(0, -1, 0);
        mNormals[2] = new PVector(0, 0, -1);
        mNormals[3] = new PVector(1, 0, 0);
        mNormals[4] = new PVector(0, 1, 0);
        mNormals[5] = new PVector(0, 0, 1);
    }

    public ReflectBox(final PVector pMin, final PVector pMax) {
        mID = Physics.getUniqueID();
        mMin = pMin;
        mMax = pMax;
        mCoefficientOfRestitution = 1.0f;
        mEpsilon = 0.001f;
    }

    public ReflectBox() {
        this(new PVector(), new PVector());
    }

    public void epsilon(final float pEpsilon) {
        mEpsilon = pEpsilon;
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
        if (!(pParticleSystem.getIntegrator() instanceof Verlet)) {
            System.out.println("### WARNING @ " + getClass().getSimpleName() + " / only works with verlet integrator.");
        }
        apply(pParticleSystem.particles());
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

    public void apply(final ArrayList<IParticle> pParticles) {
        apply(pParticles, null);
    }

    public void apply(final ArrayList<IParticle> pParticles, final ArrayList<IParticle> pCollisionParticles) {
        if (!mActive) {
            return;
        }

        for (final IParticle mParticle : pParticles) {
            final PVector mPositionBeforeCollision = Util.clone(mParticle.position());
            final PVector p = mParticle.position();
            final PVector p_old = mParticle.old_position();
            final float r = mParticle.radius();
            /**
             * @todo we should weight the deflection normal
             */
            if (p.x + r > mMax.x || p.y + r > mMax.y || p.z + r > mMax.z || p.x - r < mMin.x || p.y - r < mMin.y || p.z - r < mMin.z) {
                int mNumberOfCollisions = 0;
                final PVector mDeflectionNormal = new PVector();
                if (POSITIV_X) {
                    if (p.x + r > mMax.x) {
                        final float mBorderDiff = mMax.x - p_old.x - r;
                        p.x = p_old.x + mBorderDiff;
                        mDeflectionNormal.add(mNormals[0]);
                        mNumberOfCollisions++;
                    }
                }

                if (POSITIV_Y) {
                    if (p.y + r > mMax.y) {
                        final float mBorderDiff = mMax.y - p_old.y - r;
                        p.y = p_old.y + mBorderDiff;
                        mDeflectionNormal.add(mNormals[1]);
                        mNumberOfCollisions++;
                    }
                }

                if (POSITIV_Z) {
                    if (p.z + r > mMax.z) {
                        final float mBorderDiff = mMax.z - p_old.z - r;
                        p.z = p_old.z + mBorderDiff;
                        mDeflectionNormal.add(mNormals[2]);
                        mNumberOfCollisions++;
                    }
                }

                if (NEGATIVE_X) {
                    if (p.x - r < mMin.x) {
                        final float mBorderDiff = mMin.x - p_old.x + r;
                        p.x = p_old.x + mBorderDiff;
                        mDeflectionNormal.add(mNormals[3]);
                        mNumberOfCollisions++;
                    }
                }

                if (NEGATIVE_Y) {
                    if (p.y - r < mMin.y) {
                        final float mBorderDiff = mMin.y - p_old.y + r;
                        p.y = p_old.y + mBorderDiff;
                        mDeflectionNormal.add(mNormals[4]);
                        mNumberOfCollisions++;
                    }
                }

                if (NEGATIVE_Z) {
                    if (p.z - r < mMin.z) {
                        final float mBorderDiff = mMin.z - p_old.z + r;
                        p.z = p_old.z + mBorderDiff;
                        mDeflectionNormal.add(mNormals[5]);
                        mNumberOfCollisions++;
                    }
                }

                if (mNumberOfCollisions > 0) {
                    /* remember collided particles */
                    if (pCollisionParticles != null) {
                        pCollisionParticles.add(mParticle);
                    }
                    /* room for optimization / we don t need to reflect twice. */
                    final float mSpeed = Util.distanceSquared(mPositionBeforeCollision, mParticle.old_position());
                    if (mSpeed > mEpsilon) {
                        final PVector mDiffAfterCollision = PVector.sub(mPositionBeforeCollision, mParticle.position());
                        final PVector mDiffBeforeCollision = PVector.sub(mParticle.old_position(),
                                                                         mParticle.position());
                        mDeflectionNormal.mult(1.0f / (float) mNumberOfCollisions);
                        teilchen.util.Util.reflect(mDiffAfterCollision, mDeflectionNormal, mCoefficientOfRestitution);
                        teilchen.util.Util.reflect(mDiffBeforeCollision, mDeflectionNormal, 1);

                        if (!Util.isNaN(mParticle.old_position()) && !Util.isNaN(mParticle.position())) {
                            PVector.add(mParticle.position(), mDiffBeforeCollision, mParticle.old_position());
                            mParticle.position().add(mDiffAfterCollision);
                        }
                    }
                }
            }
        }
    }
}
