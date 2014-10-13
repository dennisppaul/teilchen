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


import mathematik.Vector3f;

import teilchen.Particle;
import teilchen.Physics;
import teilchen.integration.Verlet;


public class Box
        implements IConstraint {

    protected boolean mActive = true;

    private final Vector3f _myMin;

    private final Vector3f _myMax;

    private boolean _myReflectFlag;

    private float _myCoefficientOfRestitution;

    private boolean _myTeleport;

    public Box(final Vector3f theMin, final Vector3f theMax) {
        _myMin = theMin;
        _myMax = theMax;
        _myReflectFlag = true;
        _myCoefficientOfRestitution = 1.0f;
        _myTeleport = false;
    }

    public Box() {
        this(new Vector3f(), new Vector3f());
    }

    public void telelport(boolean theTeleportState) {
        _myTeleport = theTeleportState;
    }

    public void reflect(boolean theReflectState) {
        _myReflectFlag = theReflectState;
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
                final Vector3f myPosition = new Vector3f(myParticle.position());
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
                            final Vector3f myDiff = mathematik.Util.sub(myPosition, myParticle.position());
                            teilchen.util.Util.reflect(myDiff, _myNormals[myTag], _myCoefficientOfRestitution);
//                            System.out.println("### reflect " + _myNormals[myTag]);
//                            System.out.println("myDiff " + myDiff);
                            myParticle.old_position().sub(myDiff);
                        } else {
                            teilchen.util.Util.reflectVelocity(myParticle,
                                                               _myNormals[myTag],
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
