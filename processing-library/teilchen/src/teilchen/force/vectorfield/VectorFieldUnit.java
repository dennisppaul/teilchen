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
import teilchen.util.Util;

public class VectorFieldUnit {

    private final PVector _myPosition;

    private final PVector _myScale;

    private final PVector _myAcceleration;

    public VectorFieldUnit(final PVector thePosition,
                           final PVector theScale,
                           final PVector theAcceleration) {
        _myPosition = Util.clone(thePosition);
        _myScale = Util.clone(theScale);
        _myAcceleration = Util.clone(theAcceleration);
    }

    public void setAcceleration(final PVector theAcceleration) {
        _myAcceleration.set(theAcceleration);
    }

    public PVector getAcceleration() {
        return _myAcceleration;
    }

    public PVector getPosition() {
        return _myPosition;
    }

    public void setPosition(final PVector thePosition) {
        _myPosition.set(thePosition);
    }

    public PVector getScale() {
        return _myScale;
    }

    public void setScale(final PVector theScale) {
        _myScale.set(theScale);
    }

    public boolean isInside(final PVector thePosition) {
        return thePosition.x >= _myPosition.x
               && thePosition.x < _myPosition.x + _myScale.x
               && thePosition.y >= _myPosition.y
               && thePosition.y < _myPosition.y + _myScale.y
               && thePosition.z >= _myPosition.z
               && thePosition.z < _myPosition.z + _myScale.z;
    }
}
