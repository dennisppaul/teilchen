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

package teilchen.cubicle;

import teilchen.util.Vector3i;

import java.util.ArrayList;

/*
 * container class for ICubicleEntity representing one cube in the world.
 */
public class CubicleAtom {

    private final ArrayList<ICubicleEntity> mContainer;

    private final Vector3i mPosition;

    public CubicleAtom(int x, int y, int z) {
        mContainer = new ArrayList<>();
        mPosition = new Vector3i(x, y, z);
    }

    public Vector3i position() {
        return mPosition;
    }

    public void add(ICubicleEntity theEntity) {
        mContainer.add(theEntity);
    }

    public boolean remove(ICubicleEntity theEntity) {
        return mContainer.remove(theEntity);
    }

    public void clear() {
        mContainer.clear();
    }

    public int size() {
        return mContainer.size();
    }

    public ArrayList<ICubicleEntity> data() {
        return mContainer;
    }
}
