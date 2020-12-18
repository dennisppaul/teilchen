/*
 * Teilchen
 *
 * This file is part of the *teilchen* library (https://github.com/dennisppaul/teilchen).
 * Copyright (c) 2020 Dennis P Paul.
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

import processing.core.PVector;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.constraint.Stick;

public class StableStickQuad {

    public Particle a;

    public Particle b;

    public Particle c;

    public Particle d;

    public Stick ab;

    public Stick bc;

    public Stick cd;

    public Stick da;

    public Stick ac;

    public Stick bd;

    public StableStickQuad(final Physics pParticleSystem,
                           final PVector pA,
                           final PVector pB,
                           final PVector pC,
                           final PVector pD) {
        a = pParticleSystem.makeParticle();
        b = pParticleSystem.makeParticle();
        c = pParticleSystem.makeParticle();
        d = pParticleSystem.makeParticle();

        a.setPositionRef(pA);
        b.setPositionRef(pB);
        c.setPositionRef(pC);
        d.setPositionRef(pD);

        /* edges */
        ab = new Stick(a, b);
        bc = new Stick(b, c);
        cd = new Stick(c, d);
        da = new Stick(d, a);
        pParticleSystem.add(ab);
        pParticleSystem.add(bc);
        pParticleSystem.add(cd);
        pParticleSystem.add(da);

        /* diagonals */
        ac = new Stick(a, c);
        bd = new Stick(b, d);
        pParticleSystem.add(ac);
        pParticleSystem.add(bd);
    }
}
