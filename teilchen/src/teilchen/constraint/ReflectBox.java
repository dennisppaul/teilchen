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
package teilchen.constraint;


import java.util.Vector;

import mathematik.Vector3f;

import teilchen.Particle;
import teilchen.Physics;
import teilchen.integration.Verlet;


public class ReflectBox
        implements IConstraint {

    protected boolean mActive = true;

    private final Vector3f _myMin;

    private final Vector3f _myMax;

    private float _myCoefficientOfRestitution;

    private float _myEpsilon;

    public boolean NEGATIVE_X = true;

    public boolean NEGATIVE_Y = true;

    public boolean NEGATIVE_Z = true;

    public boolean POSITIV_X = true;

    public boolean POSITIV_Y = true;

    public boolean POSITIV_Z = true;

    public ReflectBox(final Vector3f theMin, final Vector3f theMax) {
        _myMin = theMin;
        _myMax = theMax;
        _myCoefficientOfRestitution = 1.0f;
        _myEpsilon = 0.001f;
    }

    public ReflectBox() {
        this(new Vector3f(), new Vector3f());
    }

    public void epsilon(final float theEpsilon) {
        _myEpsilon = theEpsilon;
    }

    public Vector3f min() {
        return _myMin;
    }

    public Vector3f max() {
        return _myMax;
    }
    private static final Vector3f[] _myNormals;

    static {
        _myNormals = new Vector3f[6];
        _myNormals[0] = new Vector3f(-1, 0, 0);
        _myNormals[1] = new Vector3f(0, -1, 0);
        _myNormals[2] = new Vector3f(0, 0, -1);
        _myNormals[3] = new Vector3f(1, 0, 0);
        _myNormals[4] = new Vector3f(0, 1, 0);
        _myNormals[5] = new Vector3f(0, 0, 1);
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

    public void apply(final Vector<Particle> theParticles) {
        apply(theParticles, null);
    }

    public void apply(final Vector<Particle> theParticles, final Vector<Particle> theCollisionParticles) {
        if (!mActive) {
            return;
        }

        for (final Particle myParticle : theParticles) {
            final Vector3f myPositionBeforeCollision = new Vector3f(myParticle.position());
            final Vector3f p = myParticle.position();
            final Vector3f p_old = myParticle.old_position();
            final float r = myParticle.radius();
            /**
             * @todo we should weight the deflection normal
             */
            if (p.x + r > _myMax.x
                    || p.y + r > _myMax.y
                    || p.z + r > _myMax.z
                    || p.x - r < _myMin.x
                    || p.y - r < _myMin.y
                    || p.z - r < _myMin.z) {
                int myNumberOfCollisions = 0;
                final Vector3f myDeflectionNormal = new Vector3f();
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
                    final float mySpeed = myPositionBeforeCollision.distanceSquared(myParticle.old_position());
                    if (mySpeed > _myEpsilon) {
                        final Vector3f myDiffAfterCollision = mathematik.Util.sub(myPositionBeforeCollision,
                                                                                  myParticle.position());
                        final Vector3f myDiffBeforeCollision = mathematik.Util.sub(myParticle.old_position(),
                                                                                   myParticle.position());
                        myDeflectionNormal.scale(1.0f / (float) myNumberOfCollisions);
                        teilchen.util.Util.reflect(myDiffAfterCollision, myDeflectionNormal,
                                                   _myCoefficientOfRestitution);
                        teilchen.util.Util.reflect(myDiffBeforeCollision, myDeflectionNormal, 1);

                        if (!myParticle.old_position().isNaN() && !myParticle.position().isNaN()) {
                            myParticle.old_position().add(myParticle.position(), myDiffBeforeCollision);
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
