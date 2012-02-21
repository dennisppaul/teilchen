/*
 * Particles
 *
 * Copyright (C) 2010
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


package teilchen.gestalt.util;


import gestalt.material.Color;
import gestalt.material.texture.Bitmap;

import mathematik.Vector3f;
import teilchen.force.vectorfield.VectorFieldGenerator;
import teilchen.force.vectorfield.VectorFieldUnit;


public class VectorFieldGeneratorBITMAP
    implements VectorFieldGenerator {

    private VectorFieldUnit[][][] _myField;

    public VectorFieldGeneratorBITMAP(int theSizeZ,
                                      Bitmap[] theBitmaps,
                                      int[] theBitmapsZ) {
        int mySizeX = theBitmaps[0].getWidth();
        int mySizeY = theBitmaps[0].getHeight();
        int mySizeZ = theSizeZ;

        /* populate field */
        _myField = new VectorFieldUnit[mySizeX][mySizeY][mySizeZ];

        for (int x = 0; x < mySizeX; x++) {
            for (int y = 0; y < mySizeY; y++) {
                for (int z = 0; z < mySizeZ; z++) {
                    /* get color depending on z position*/
                    Color myColor = new Color();
                    int myColorY = mySizeY - 1 - y;
                    getColor(x, myColorY, z, theBitmaps, theBitmapsZ, myColor);

                    /* generate unit */
                    Vector3f myPosition = new Vector3f(x *
                                                       (1f / (float) mySizeX),
                                                       y * (1f / (float) mySizeY),
                                                       z * (1f / (float) mySizeZ));
                    Vector3f myScale = new Vector3f(1f /
                                                    (float) mySizeX,
                                                    1f / (float) mySizeY,
                                                    1f / (float) mySizeZ);
                    Vector3f myAcceleration = new Vector3f(myColor.r,
                                                           myColor.g,
                                                           myColor.b);
                    myAcceleration.normalize();
                    VectorFieldUnit myUnit = new VectorFieldUnit(myPosition,
                                                                 myScale,
                                                                 myAcceleration);
                    _myField[x][y][z] = myUnit;
                }
            }
        }
    }


    private void getColor(int theX,
                          int theY,
                          int theZ,
                          Bitmap[] theBitmaps,
                          int[] theBitmapsZ,
                          Color theColor) {
        for (int i = 0; i < theBitmapsZ.length; i++) {
            if (i < theBitmapsZ.length - 1) {
                if (theZ >= theBitmapsZ[i] && theZ < theBitmapsZ[i + 1]) {
                    Bitmap myBitmap01 = theBitmaps[i];
                    Bitmap myBitmap02 = theBitmaps[i + 1];
                    float myRatio = (float) (theZ - theBitmapsZ[i]) / (float) (theBitmapsZ[i + 1] - theBitmapsZ[i]);
                    Color myColor01 = new Color();
                    Color myColor02 = new Color();
                    myBitmap01.getPixel(theX, theY, myColor01);
                    myBitmap02.getPixel(theX, theY, myColor02);
                    theColor.r = myColor01.r * (1f - myRatio) + myColor02.r * myRatio;
                    theColor.g = myColor01.g * (1f - myRatio) + myColor02.g * myRatio;
                    theColor.b = myColor01.b * (1f - myRatio) + myColor02.b * myRatio;
                    break;
                }
            } else {
                theBitmaps[theBitmaps.length - 1].getPixel(theX, theY, theColor);
            }
        }
    }


    public VectorFieldUnit[][][] data() {
        return _myField;
    }
}
