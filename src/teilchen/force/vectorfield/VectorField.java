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
package teilchen.force.vectorfield;

import processing.core.PVector;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.VectorfieldParticle;
import teilchen.force.IForce;
import teilchen.util.Util;
import teilchen.util.Vector3i;

public class VectorField
        implements IForce {

    private final VectorFieldUnit[][][] _myField;

    public float strength;

    private final PVector _myPosition;

    private final PVector _myScale;

    private boolean _myActive;

    public VectorField(VectorFieldGenerator theGenerator) {
        /* init fields */
        strength = 1;
        _myPosition = new PVector();
        _myScale = new PVector(1f, 1f, 1f);
        _myField = theGenerator.data();
        _myActive = true;
    }

    public VectorFieldUnit[][][] data() {
        return _myField;
    }

    public PVector getPosition() {
        return _myPosition;
    }

    public void setPosition(final PVector thePosition) {
        _myPosition.set(thePosition);
        for (int x = 0; x < _myField.length; x++) {
            for (int y = 0; y < _myField[x].length; y++) {
                for (int z = 0; z < _myField[x][y].length; z++) {
                    PVector myPosition = new PVector(x
                                                     * (_myScale.x / (float) _myField.length),
                                                     y * (_myScale.y / (float) _myField[x].length),
                                                     z * (_myScale.z / (float) _myField[x][y].length));
                    _myField[x][y][z].getPosition().set(myPosition);
                    _myField[x][y][z].getPosition().add(thePosition);
                }
            }
        }
    }

    public final PVector getScale() {
        return _myScale;
    }

    public void setScale(final PVector theScale) {
        _myScale.set(theScale);
        for (int x = 0; x < _myField.length; x++) {
            for (int y = 0; y < _myField[x].length; y++) {
                for (int z = 0; z < _myField[x][y].length; z++) {
                    PVector myUnitScale = new PVector(_myScale.x
                                                      / (float) _myField.length,
                                                      _myScale.y / (float) _myField[x].length,
                                                      _myScale.z / (float) _myField[x][y].length);
                    _myField[x][y][z].setScale(myUnitScale);
                    PVector myPosition = new PVector(x
                                                     * (_myScale.x / (float) _myField.length),
                                                     y * (_myScale.y / (float) _myField[x].length),
                                                     z * (_myScale.z / (float) _myField[x][y].length));
                    myPosition.add(_myPosition);
                    _myField[x][y][z].setPosition(myPosition);
                }
            }
        }
    }

//    public void setScale(final PVector theScale) {
//    _myScale.set(theScale);
//    for (int x = 0; x < _myField.length; x++) {
//        for (int y = 0; y < _myField[x].length; y++) {
//            for (int z = 0; z < _myField[x][y].length; z++) {
//                PVector myUnitScale = new PVector(_myScale.x,
//                        _myScale.y,
//                        _myScale.z);
//                _myField[x][y][z].setScale(myUnitScale);
//                PVector myPosition = new PVector(x *
//                        (_myScale.x),
//                        y * (_myScale.y),
//                        z * (_myScale.z));
//                myPosition.add(_myPosition);
//                _myField[x][y][z].setPosition(myPosition);
//            }
//        }
//    }
//}
    /* IForce */
    private PVector force(float theDeltaTime, VectorfieldParticle theParticle) {
        if (isInBoundingBox(theParticle)) {
            Vector3i myUnit = checkIfIsInside(theParticle, 1);
            if (myUnit != null) {
                PVector myAcceleration = Util.clone(_myField[myUnit.x][myUnit.y][myUnit.z].getAcceleration());
                myAcceleration.mult(strength);
                theParticle.setLastUnit(myUnit);
                return myAcceleration;
            }
            for (int x = 0; x < _myField.length; x++) {
                for (int y = 0; y < _myField[x].length; y++) {
                    for (int z = 0; z < _myField[x][y].length; z++) {
                        if (_myField[x][y][z].isInside(theParticle.position())) {
                            PVector myAcceleration = Util.clone(_myField[x][y][z].getAcceleration());
                            myAcceleration.mult(strength);
                            theParticle.setLastUnit(new Vector3i(x, y, z));
                            return myAcceleration;
                        }
                    }
                }
            }
        }
        return new PVector();
    }

    private boolean isInBoundingBox(VectorfieldParticle theParticle) {
        return theParticle.position().x >= _myPosition.x
               && theParticle.position().x <= _myPosition.x + _myScale.x
               && theParticle.position().y >= _myPosition.y
               && theParticle.position().y <= _myPosition.y + _myScale.y
               && theParticle.position().z >= _myPosition.z
               && theParticle.position().z <= _myPosition.z + _myScale.z;
    }

    private Vector3i checkIfIsInside(VectorfieldParticle theParticle,
                                     int theRadius) {
        Vector3i myUnit = new Vector3i(theParticle.getLastUnit());
        for (int x = myUnit.x - theRadius; x < myUnit.x + theRadius; x++) {
            for (int y = myUnit.y - theRadius; y < myUnit.y + theRadius; y++) {
                for (int z = myUnit.z - theRadius; z < myUnit.z + theRadius; z++) {
                    if (x >= 0 && x < _myField.length
                        && y >= 0 && y < _myField[x].length
                        && z >= 0 && z < _myField[x][y].length) {
                        if (_myField[x][y][z].isInside(theParticle.position())) {
                            return new Vector3i(x, y, z);
                        }
                    }
                }
            }
        }
        return null;
    }

    public void apply(final float theDeltaTime,
                      final Physics theParticleSystem) {
        /**
         * @todo clean up force method
         */
        for (final Particle myParticle : theParticleSystem.particles()) {
            if (!myParticle.fixed()) {
                if (myParticle instanceof VectorfieldParticle) {
                    myParticle.force().add(force(theDeltaTime,
                                                 (VectorfieldParticle) myParticle));
                }
            }
        }
    }

    public boolean dead() {
        return false;
    }

    public boolean active() {
        return _myActive;
    }

    public void active(boolean theActiveState) {
        _myActive = theActiveState;
    }
}
