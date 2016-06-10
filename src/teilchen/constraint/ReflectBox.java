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

package teilchen.constraint;

import processing.core.PVector;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.integration.Verlet;
import teilchen.util.Util;

import java.util.ArrayList;

public class ReflectBox implements IConstraint {

    private static final PVector[] _myNormals;

    static {
        _myNormals = new PVector[6];
        _myNormals[0] = new PVector(-1, 0, 0);
        _myNormals[1] = new PVector(0, -1, 0);
        _myNormals[2] = new PVector(0, 0, -1);
        _myNormals[3] = new PVector(1, 0, 0);
        _myNormals[4] = new PVector(0, 1, 0);
        _myNormals[5] = new PVector(0, 0, 1);
    }

    private final PVector _myMin;
    private final PVector _myMax;
    public boolean NEGATIVE_X = true;
    public boolean NEGATIVE_Y = true;
    public boolean NEGATIVE_Z = true;
    public boolean POSITIV_X = true;
    public boolean POSITIV_Y = true;
    public boolean POSITIV_Z = true;
    protected boolean mActive = true;
    private float _myCoefficientOfRestitution;
    private float _myEpsilon;

    public ReflectBox(final PVector theMin, final PVector theMax) {
        _myMin = theMin;
        _myMax = theMax;
        _myCoefficientOfRestitution = 1.0f;
        _myEpsilon = 0.001f;
    }

    public ReflectBox() {
        this(new PVector(), new PVector());
    }

    public void epsilon(final float theEpsilon) {
        _myEpsilon = theEpsilon;
    }

    public PVector min() {
        return _myMin;
    }

    public PVector max() {
        return _myMax;
    }

    public void coefficientofrestitution(float theCoefficientOfRestitution) {
        _myCoefficientOfRestitution = theCoefficientOfRestitution;
    }

    public float coefficientofrestitution() {
        return _myCoefficientOfRestitution;
    }

    public void apply(final Physics theParticleSystem) {
        if (!(theParticleSystem.getIntegrator() instanceof Verlet)) {
            System.out.println("### WARNING @ " + getClass().getSimpleName() + " / only works with verlet integrator.");
        }
        apply(theParticleSystem.particles());
    }

    public void apply(final ArrayList<Particle> theParticles) {
        apply(theParticles, null);
    }

    public void apply(final ArrayList<Particle> theParticles, final ArrayList<Particle> theCollisionParticles) {
        if (!mActive) {
            return;
        }

        for (final Particle myParticle : theParticles) {
            final PVector myPositionBeforeCollision = Util.clone(myParticle.position());
            final PVector p = myParticle.position();
            final PVector p_old = myParticle.old_position();
            final float r = myParticle.radius();
            /**
             * @todo we should weight the deflection normal
             */
            if (p.x + r > _myMax.x || p.y + r > _myMax.y || p.z + r > _myMax.z || p.x - r < _myMin.x || p.y - r < _myMin.y || p.z - r < _myMin.z) {
                int myNumberOfCollisions = 0;
                final PVector myDeflectionNormal = new PVector();
                if (POSITIV_X) {
                    if (p.x + r > _myMax.x) {
                        final float myBorderDiff = _myMax.x - p_old.x - r;
                        p.x = p_old.x + myBorderDiff;
                        myDeflectionNormal.add(_myNormals[0]);
                        myNumberOfCollisions++;
                    }
                }

                if (POSITIV_Y) {
                    if (p.y + r > _myMax.y) {
                        final float myBorderDiff = _myMax.y - p_old.y - r;
                        p.y = p_old.y + myBorderDiff;
                        myDeflectionNormal.add(_myNormals[1]);
                        myNumberOfCollisions++;
                    }
                }

                if (POSITIV_Z) {
                    if (p.z + r > _myMax.z) {
                        final float myBorderDiff = _myMax.z - p_old.z - r;
                        p.z = p_old.z + myBorderDiff;
                        myDeflectionNormal.add(_myNormals[2]);
                        myNumberOfCollisions++;
                    }
                }

                if (NEGATIVE_X) {
                    if (p.x - r < _myMin.x) {
                        final float myBorderDiff = _myMin.x - p_old.x + r;
                        p.x = p_old.x + myBorderDiff;
                        myDeflectionNormal.add(_myNormals[3]);
                        myNumberOfCollisions++;
                    }
                }

                if (NEGATIVE_Y) {
                    if (p.y - r < _myMin.y) {
                        final float myBorderDiff = _myMin.y - p_old.y + r;
                        p.y = p_old.y + myBorderDiff;
                        myDeflectionNormal.add(_myNormals[4]);
                        myNumberOfCollisions++;
                    }
                }

                if (NEGATIVE_Z) {
                    if (p.z - r < _myMin.z) {
                        final float myBorderDiff = _myMin.z - p_old.z + r;
                        p.z = p_old.z + myBorderDiff;
                        myDeflectionNormal.add(_myNormals[5]);
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
                    if (mySpeed > _myEpsilon) {
                        final PVector myDiffAfterCollision = PVector.sub(myPositionBeforeCollision,
                                                                         myParticle.position());
                        final PVector myDiffBeforeCollision = PVector.sub(myParticle.old_position(),
                                                                          myParticle.position());
                        myDeflectionNormal.mult(1.0f / (float) myNumberOfCollisions);
                        teilchen.util.Util.reflect(myDiffAfterCollision,
                                                   myDeflectionNormal,
                                                   _myCoefficientOfRestitution);
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

    public boolean active() {
        return mActive;
    }

    public void active(boolean theActiveState) {
        mActive = theActiveState;
    }
}
