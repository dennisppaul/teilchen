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

package teilchen.force;

public class TriangleDeflectorIndexed extends TriangleDeflector {

    private final int a_index;
    private final int b_index;
    private final int c_index;
    private final float[] mVertices;

    public TriangleDeflectorIndexed(float[] pVertices, int pA, int pB, int pC) {
        super();
        mVertices = pVertices;
        a_index = pA;
        b_index = pB;
        c_index = pC;
        updateProperties();
    }

    public void updateProperties() {
        updateVertices();
        super.updateProperties();
    }

    private void updateVertices() {
        a().set(mVertices[a_index + 0], mVertices[a_index + 1], mVertices[a_index + 2]);
        b().set(mVertices[b_index + 0], mVertices[b_index + 1], mVertices[b_index + 2]);
        c().set(mVertices[c_index + 0], mVertices[c_index + 1], mVertices[c_index + 2]);
    }
}
