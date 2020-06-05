/*
 * Teilchen
 *
 * Copyright (C) 2020
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

import java.util.ArrayList;

public class ReflectBox implements IConstraint {

    private static final PVector[] mNormals;

    static {
        mNormals = new PVector[6];
        mNormals[0] = new PVector(-1, 0, 0);
        mNormals[1] = new PVector(0, -1, 0);
        mNormals[2] = new PVector(0, 0, -1);
        mNormals[3] = new PVector(1, 0, 0);
        mNormals[4] = new PVector(0, 1, 0);
        mNormals[5] = new PVector(0, 0, 1);
    }

    public boolean NEGATIVE_X = true;
    public boolean NEGATIVE_Y = true;
    public boolean NEGATIVE_Z = true;
    public boolean POSITIV_X = true;
    public boolean POSITIV_Y = true;
    public boolean POSITIV_Z = true;
    private final PVector mMin;
    private final PVector mMax;
    protected boolean mActive = true;
    private boolean mDead = false;
    private float mCoefficientOfRestitution;
    private float mEpsilon;

    public ReflectBox(final PVector pMin, final PVector pMax) {
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

    public boolean dead() { return mDead; }

    public void dead(boolean pDead) { mDead = pDead; }

    public void apply(final ArrayList<Particle> pParticles) {
        apply(pParticles, null);
    }

    public void apply(final ArrayList<Particle> pParticles, final ArrayList<Particle> theCollisionParticles) {
        if (!mActive) {
            return;
        }

        for (final Particle myParticle : pParticles) {
            final PVector myPositionBeforeCollision = Util.clone(myParticle.position());
            final PVector p = myParticle.position();
            final PVector p_old = myParticle.old_position();
            final float r = myParticle.radius();
            /**
             * @todo we should weight the deflection normal
             */
            if (p.x + r > mMax.x || p.y + r > mMax.y || p.z + r > mMax.z || p.x - r < mMin.x || p.y - r < mMin.y || p.z - r < mMin.z) {
                int myNumberOfCollisions = 0;
                final PVector myDeflectionNormal = new PVector();
                if (POSITIV_X) {
                    if (p.x + r > mMax.x) {
                        final float myBorderDiff = mMax.x - p_old.x - r;
                        p.x = p_old.x + myBorderDiff;
                        myDeflectionNormal.add(mNormals[0]);
                        myNumberOfCollisions++;
                    }
                }

                if (POSITIV_Y) {
                    if (p.y + r > mMax.y) {
                        final float myBorderDiff = mMax.y - p_old.y - r;
                        p.y = p_old.y + myBorderDiff;
                        myDeflectionNormal.add(mNormals[1]);
                        myNumberOfCollisions++;
                    }
                }

                if (POSITIV_Z) {
                    if (p.z + r > mMax.z) {
                        final float myBorderDiff = mMax.z - p_old.z - r;
                        p.z = p_old.z + myBorderDiff;
                        myDeflectionNormal.add(mNormals[2]);
                        myNumberOfCollisions++;
                    }
                }

                if (NEGATIVE_X) {
                    if (p.x - r < mMin.x) {
                        final float myBorderDiff = mMin.x - p_old.x + r;
                        p.x = p_old.x + myBorderDiff;
                        myDeflectionNormal.add(mNormals[3]);
                        myNumberOfCollisions++;
                    }
                }

                if (NEGATIVE_Y) {
                    if (p.y - r < mMin.y) {
                        final float myBorderDiff = mMin.y - p_old.y + r;
                        p.y = p_old.y + myBorderDiff;
                        myDeflectionNormal.add(mNormals[4]);
                        myNumberOfCollisions++;
                    }
                }

                if (NEGATIVE_Z) {
                    if (p.z - r < mMin.z) {
                        final float myBorderDiff = mMin.z - p_old.z + r;
                        p.z = p_old.z + myBorderDiff;
                        myDeflectionNormal.add(mNormals[5]);
                        myNumberOfCollisions++;
                    }
                }

                if (myNumberOfCollisions > 0) {
                    /* remember collided particles */
                    if (theCollisionParticles != null) {
                        theCollisionParticles.add(myParticle);
                    }
                    /* room for optimization / we don t need to reflect twice. */
                    final float mySpeed = Util.distanceSquared(myPositionBeforeCollision, myParticle.old_position());
                    if (mySpeed > mEpsilon) {
                        final PVector myDiffAfterCollision = PVector.sub(myPositionBeforeCollision,
                                                                         myParticle.position());
                        final PVector myDiffBeforeCollision = PVector.sub(myParticle.old_position(),
                                                                          myParticle.position());
                        myDeflectionNormal.mult(1.0f / (float) myNumberOfCollisions);
                        teilchen.util.Util.reflect(myDiffAfterCollision,
                                                   myDeflectionNormal,
                                                   mCoefficientOfRestitution);
                        teilchen.util.Util.reflect(myDiffBeforeCollision, myDeflectionNormal, 1);

                        if (!Util.isNaN(myParticle.old_position()) && !Util.isNaN(myParticle.position())) {
                            PVector.add(myParticle.position(), myDiffBeforeCollision, myParticle.old_position());
                            myParticle.position().add(myDiffAfterCollision);
                        }
                    }
                }
            }
        }
    }
}
