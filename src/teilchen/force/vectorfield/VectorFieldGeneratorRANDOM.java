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

public class VectorFieldGeneratorRANDOM
        implements VectorFieldGenerator {

    private final VectorFieldUnit[][][] _myField;

    public VectorFieldGeneratorRANDOM(int theSizeX,
                                      int theSizeY,
                                      int theSizeZ) {
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
                    PVector myAcceleration = new PVector((float) Math.
                            random() - 0.5f,
                                                         (float) Math.random() - 0.5f,
                                                         (float) Math.random() - 0.5f);
                    myAcceleration.normalize();
                    VectorFieldUnit myUnit = new VectorFieldUnit(myPosition,
                                                                 myScale,
                                                                 myAcceleration);
                    _myField[x][y][z] = myUnit;
                }
            }
        }
    }

    public VectorFieldUnit[][][] data() {
        return _myField;
    }
}
