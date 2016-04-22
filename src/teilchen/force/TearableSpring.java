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
package teilchen.force;

import teilchen.Particle;
import teilchen.Physics;
import static teilchen.util.Util.*;

public class TearableSpring
        extends Spring {

    private boolean _myTornApart = false;

    private float _myTearDistance = -1;

    public TearableSpring(Particle theA, Particle theB) {
        super(theA,
              theB,
              2.0f, 0.1f,
              distance(theA.position(), theB.position()));
    }

    public TearableSpring(final Particle theA,
                          final Particle theB,
                          final float theSpringConstant,
                          final float theSpringDamping,
                          final float theRestLength,
                          final float theTearDistance) {
        super(theA,
              theB,
              theSpringConstant,
              theSpringDamping,
              theRestLength);
        _myTearDistance = theTearDistance;
    }

    public final float teardistance() {
        return _myTearDistance;
    }

    public final void teardistance(float theTearDistance) {
        _myTearDistance = theTearDistance;
    }

    public void apply(final float theDeltaTime, final Physics theParticleSystem) {
        /* check if spring will tear */
        if (_myTearDistance > 0) {
            final float myActualDistance = distance(a().position(), b().position());
            if (myActualDistance > restlength() + _myTearDistance) {
                _myTornApart = true;
            }
        }
        /* apply force if spring is ok */
        if (!_myTornApart) {
            super.apply(theDeltaTime, theParticleSystem);
        }
    }

    public boolean dead() {
        return _myTornApart || super.dead();
    }
}
