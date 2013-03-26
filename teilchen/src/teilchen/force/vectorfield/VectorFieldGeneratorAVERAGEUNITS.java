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
package teilchen.force.vectorfield;


import mathematik.Vector3f;


public class VectorFieldGeneratorAVERAGEUNITS
        implements VectorFieldGenerator {

    private VectorFieldUnit[][][] _myField;

    public VectorFieldGeneratorAVERAGEUNITS(int theSizeX,
                                            int theSizeY,
                                            int theSizeZ,
                                            Vector3f[] theCoordinats,
                                            Vector3f[] theAccelerations) {
        /* populate field */
        _myField = new VectorFieldUnit[theSizeX][theSizeY][theSizeZ];
        for (int x = 0; x < theSizeX; x++) {
            for (int y = 0; y < theSizeY; y++) {
                for (int z = 0; z < theSizeZ; z++) {
                    Vector3f myPosition = new Vector3f(x
                            * (1f / (float) theSizeX),
                                                       y * (1f / (float) theSizeY),
                                                       z * (1f / (float) theSizeZ));
                    Vector3f myScale = new Vector3f(1f
                            / (float) theSizeX,
                                                    1f / (float) theSizeY,
                                                    1f / (float) theSizeZ);
                    Vector3f myAcceleration = new Vector3f(1f, 1f, 1f);
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
                    Vector3f myAcceleration = new Vector3f();
                    VectorFieldUnit myUnit = _myField[x][y][z];
                    for (int i = 0; i < theCoordinats.length; i++) {
                        Vector3f myDistance = new Vector3f();
                        VectorFieldUnit myOtherUnit = _myField[ (int) theCoordinats[i].x][ (int) theCoordinats[i].y][ (int) theCoordinats[i].z];
                        myDistance.set(myOtherUnit.getPosition());
                        myDistance.sub(myUnit.getPosition());
                        float myRatio = myMaxDistance - myDistance.length();
                        Vector3f myTempAcceleration = new Vector3f();
                        myTempAcceleration.set(theAccelerations[i]);
                        myTempAcceleration.scale(myRatio);
                        myAcceleration.add(myTempAcceleration);
                    }
                    myAcceleration.scale(1f / myMaxDistance);
                    myUnit.setAcceleration(myAcceleration);
                }
            }
        }
    }

    public VectorFieldUnit[][][] data() {
        return _myField;
    }
}
