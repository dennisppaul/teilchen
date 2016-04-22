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

public class VectorFieldGeneratorAVERAGEUNITS
        implements VectorFieldGenerator {

    private final VectorFieldUnit[][][] _myField;

    public VectorFieldGeneratorAVERAGEUNITS(int theSizeX,
                                            int theSizeY,
                                            int theSizeZ,
                                            PVector[] theCoordinats,
                                            PVector[] theAccelerations) {
        /* populate field */
        _myField = new VectorFieldUnit[theSizeX][theSizeY][theSizeZ];
        for (int x = 0; x < theSizeX; x++) {
            for (int y = 0; y < theSizeY; y++) {
                for (int z = 0; z < theSizeZ; z++) {
                    PVector myPosition = new PVector(x
                                                     * (1f / (float) theSizeX),
                                                     y * (1f / (float) theSizeY),
                                                     z * (1f / (float) theSizeZ));
                    PVector myScale = new PVector(1f
                                                  / (float) theSizeX,
                                                  1f / (float) theSizeY,
                                                  1f / (float) theSizeZ);
                    PVector myAcceleration = new PVector(1f, 1f, 1f);
                    VectorFieldUnit myUnit = new VectorFieldUnit(myPosition,
                                                                 myScale,
                                                                 myAcceleration);
                    _myField[x][y][z] = myUnit;
                }
            }
        }

        float myMaxDistance = (float) Math.sqrt(2);
        for (int x = 0; x < theSizeX; x++) {
            for (int y = 0; y < theSizeY; y++) {
                for (int z = 0; z < theSizeZ; z++) {
                    PVector myAcceleration = new PVector();
                    VectorFieldUnit myUnit = _myField[x][y][z];
                    for (int i = 0; i < theCoordinats.length; i++) {
                        PVector myDistance = new PVector();
                        VectorFieldUnit myOtherUnit = _myField[(int) theCoordinats[i].x][(int) theCoordinats[i].y][(int) theCoordinats[i].z];
                        myDistance.set(myOtherUnit.getPosition());
                        myDistance.sub(myUnit.getPosition());
                        float myRatio = myMaxDistance - myDistance.mag();
                        PVector myTempAcceleration = new PVector();
                        myTempAcceleration.set(theAccelerations[i]);
                        myTempAcceleration.mult(myRatio);
                        myAcceleration.add(myTempAcceleration);
                    }
                    myAcceleration.mult(1f / myMaxDistance);
                    myUnit.setAcceleration(myAcceleration);
                }
            }
        }
    }

    public VectorFieldUnit[][][] data() {
        return _myField;
    }
}
