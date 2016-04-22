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

import processing.core.PVector;
import teilchen.util.Vector3i;

public class CubicleEntity
        implements ICubicleEntity {

    private final Vector3i _myCubiclePosition;

    private final PVector _myPosition;

    public CubicleEntity() {
        _myCubiclePosition = new Vector3i();
        _myPosition = new PVector();
    }

    public Vector3i cubicle() {
        return _myCubiclePosition;
    }

    public PVector position() {
        return _myPosition;
    }

    public boolean leaving(int theX, int theY, int theZ) {
        return !(theX == cubicle().x
                 && theY == cubicle().y
                 && theZ == cubicle().z);
    }

    public boolean isActive() {
        return true;
    }
}
