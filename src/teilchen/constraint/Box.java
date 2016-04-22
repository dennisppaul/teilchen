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
import static processing.core.PVector.sub;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.integration.Verlet;
import teilchen.util.Util;

public class Box
        implements IConstraint {

    protected boolean mActive = true;

    private final PVector _myMin;

    private final PVector _myMax;

    private boolean _myReflectFlag;

    private float _myCoefficientOfRestitution;

    private boolean _myTeleport;

    public Box(final PVector theMin, final PVector theMax) {
        _myMin = theMin;
        _myMax = theMax;
        _myReflectFlag = true;
        _myCoefficientOfRestitution = 1.0f;
        _myTeleport = false;
    }

    public Box() {
        this(new PVector(), new PVector());
    }

    public void telelport(boolean theTeleportState) {
        _myTeleport = theTeleportState;
    }

    public void reflect(boolean theReflectState) {
        _myReflectFlag = theReflectState;
    }

    public PVector min() {
        return _myMin;
    }

    public PVector max() {
        return _myMax;
    }
    private static final PVector[] NORMALS;

    static {
        NORMALS = new PVector[6];
        NORMALS[0] = new PVector(-1, 0, 0);
        NORMALS[1] = new PVector(0, -1, 0);
        NORMALS[2] = new PVector(0, 0, -1);
        NORMALS[3] = new PVector(1, 0, 0);
        NORMALS[4] = new PVector(0, 1, 0);
        NORMALS[5] = new PVector(0, 0, 1);
    }

    public void coefficientofrestitution(float theCoefficientOfRestitution) {
        _myCoefficientOfRestitution = theCoefficientOfRestitution;
    }

    public float coefficientofrestitution() {
        return _myCoefficientOfRestitution;
    }

    public void apply(final Physics theParticleSystem) {

        if (!mActive) {
            return;
        }

        for (final Particle myParticle : theParticleSystem.particles()) {
            if (_myTeleport) {
                if (myParticle.position().x > _myMax.x) {
                    myParticle.position().x = _myMin.x;
                }
                if (myParticle.position().y > _myMax.y) {
                    myParticle.position().y = _myMin.y;
                }
                if (myParticle.position().z > _myMax.z) {
                    myParticle.position().z = _myMin.z;
                }
                if (myParticle.position().x < _myMin.x) {
                    myParticle.position().x = _myMax.x;
                }
                if (myParticle.position().y < _myMin.y) {
                    myParticle.position().y = _myMax.y;
                }
                if (myParticle.position().z < _myMin.z) {
                    myParticle.position().z = _myMax.z;
                }
            } else {
                /**
                 * @todo to do this properly we would need to add the normals
                 * and normalize them. maybe later.
                 */
                int myTag = -1;
                final PVector myPosition = Util.clone(myParticle.position());
                if (myParticle.position().x > _myMax.x) {
                    myParticle.position().x = _myMax.x;
                    myTag = 0;
                }
                if (myParticle.position().y > _myMax.y) {
                    myParticle.position().y = _myMax.y;
                    myTag = 1;
                }
                if (myParticle.position().z > _myMax.z) {
                    myParticle.position().z = _myMax.z;
                    myTag = 2;
                }
                if (myParticle.position().x < _myMin.x) {
                    myParticle.position().x = _myMin.x;
                    myTag = 3;
                }
                if (myParticle.position().y < _myMin.y) {
                    myParticle.position().y = _myMin.y;
                    myTag = 4;
                }
                if (myParticle.position().z < _myMin.z) {
                    myParticle.position().z = _myMin.z;
                    myTag = 5;
                }
                if (myTag >= 0) {
                    if (_myReflectFlag) {
                        if (theParticleSystem.getIntegrator() instanceof Verlet) {
                            final PVector myDiff = sub(myPosition, myParticle.position());
                            teilchen.util.Util.reflect(myDiff, NORMALS[myTag], _myCoefficientOfRestitution);
//                            System.out.println("### reflect " + _myNormals[myTag]);
//                            System.out.println("myDiff " + myDiff);
                            myParticle.old_position().sub(myDiff);
                        } else {
                            teilchen.util.Util.reflectVelocity(myParticle,
                                                               NORMALS[myTag],
                                                               _myCoefficientOfRestitution);
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

    public void active(boolean theActiveState) {
        mActive = theActiveState;
    }
}
