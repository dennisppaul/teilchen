/*
 * Teilchen
 *
 * This file is part of the *teilchen* library (https://github.com/dennisppaul/teilchen).
 * Copyright (c) 2023 Dennis P Paul.
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

    public int color_empty = -8421505;
    public int color_full = -1;
    private final CubicleWorld mCubicleWorld;

    public CubicleWorldView(CubicleWorld pWorld) {
        mCubicleWorld = pWorld;
    }

    public void draw(PGraphics pParent) {

        /* collect data */
        final CubicleAtom[][][] mData = mCubicleWorld.getDataRef();
        final TransformMatrix4f mTransform = mCubicleWorld.transform();
        final PVector mScale = mCubicleWorld.cellscale();

        /* draw world */
        pParent.pushMatrix();

        /* rotation */
        float[] f = mTransform.toArray();
        pParent.translate(f[12], f[13], f[14]);
        pParent.applyMatrix(f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8], f[9], f[10], f[11], 0, 0, 0, f[15]);

        /* scale */
        pParent.scale(mScale.x, mScale.y, mScale.z);
        for (int x = 0; x < mData.length; x++) {
            for (int y = 0; y < mData[x].length; y++) {
                for (int z = 0; z < mData[x][y].length; z++) {
                    CubicleAtom mCubicle = mData[x][y][z];
                    pParent.pushMatrix();
                    pParent.translate(x, y, z);
                    pParent.translate(mCubicleWorld.cellscale().x / 2 / mScale.x,
                                      mCubicleWorld.cellscale().y / 2 / mScale.y,
                                      mCubicleWorld.cellscale().z / 2 / mScale.z);
                    if (mCubicle.size() > 0) {
                        pParent.stroke(color_full);
                    } else {
                        pParent.stroke(color_empty);
                    }
                    pParent.box(1);
                    pParent.popMatrix();
                }
            }
        }
        pParent.popMatrix();
    }
}
