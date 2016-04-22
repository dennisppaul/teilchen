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
package teilchen.util;

import processing.core.PGraphics;
import processing.core.PVector;
import teilchen.cubicle.CubicleAtom;
import teilchen.cubicle.CubicleWorld;

public class CubicleWorldView {

    private final CubicleWorld mCubicleWorld;

    public int color_full = -1;

    public int color_empty = -8421505;

    public CubicleWorldView(CubicleWorld theWorld) {
        mCubicleWorld = theWorld;
    }

    public void draw(PGraphics theParent) {

        /* collect data */
        final CubicleAtom[][][] myData = mCubicleWorld.getDataRef();
        final TransformMatrix4f myTransform = mCubicleWorld.transform();
        final PVector myScale = mCubicleWorld.cellscale();

        /* draw world */
        theParent.pushMatrix();

        /* rotation */
        float[] f = myTransform.toArray();
        theParent.translate(f[12], f[13], f[14]);
        theParent.applyMatrix(f[0], f[1], f[2], f[3],
                              f[4], f[5], f[6], f[7],
                              f[8], f[9], f[10], f[11],
                              0, 0, 0, f[15]);

        /* scale */
        theParent.scale(myScale.x, myScale.y, myScale.z);
        for (int x = 0; x < myData.length; x++) {
            for (int y = 0; y < myData[x].length; y++) {
                for (int z = 0; z < myData[x][y].length; z++) {
                    CubicleAtom myCubicle = myData[x][y][z];
                    theParent.pushMatrix();
                    theParent.translate(x, y, z);
                    theParent.translate(mCubicleWorld.cellscale().x / 2 / myScale.x,
                                        mCubicleWorld.cellscale().y / 2 / myScale.y,
                                        mCubicleWorld.cellscale().z / 2 / myScale.z);
                    if (myCubicle.size() > 0) {
                        theParent.stroke(color_full);
                    } else {
                        theParent.stroke(color_empty);
                    }
                    theParent.box(1);
                    theParent.popMatrix();
                }
            }
        }
        theParent.popMatrix();
    }
}
