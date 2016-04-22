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

import processing.core.PVector;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.force.Spring;

public class StableSpringQuad {

    public Particle a;

    public Particle b;

    public Particle c;

    public Particle d;

    public Spring ab;

    public Spring bc;

    public Spring cd;

    public Spring da;

    public Spring ac;

    public Spring bd;

    /**
     * this utility method creates a 'stable' shape from 4 positions. in this
     * case a stable shape is created by connecting the four positions' edge
     * plus two diagonals to create a stable quad.
     *
     * the positions should be in counter clockwise order.
     *
     * the positions are stored as reference which means that if you change
     * either of the vectors afterwards it will also change the position of the
     * connected particles.
     *
     * @param pParticleSystem ParticleSystem
     * @param pA
     * @param pB
     * @param pC
     * @param pD
     */
    public StableSpringQuad(final Physics pParticleSystem,
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
        final float mySpringConstant = 100;
        final float mySpringDamping = 5;
        ab = pParticleSystem.makeSpring(a, b, mySpringConstant, mySpringDamping);
        bc = pParticleSystem.makeSpring(b, c, mySpringConstant, mySpringDamping);
        cd = pParticleSystem.makeSpring(c, d, mySpringConstant, mySpringDamping);
        da = pParticleSystem.makeSpring(d, a, mySpringConstant, mySpringDamping);
        /* diagonals */
        ac = pParticleSystem.makeSpring(a, c, mySpringConstant, mySpringDamping);
        bd = pParticleSystem.makeSpring(b, d, mySpringConstant, mySpringDamping);
    }

    public StableSpringQuad(final Physics theParticleSystem,
                            final Particle pA,
                            final Particle pB,
                            final Particle pC,
                            final Particle pD) {
        a = pA;
        b = pB;
        c = pC;
        d = pD;

        /* edges */
        final float mySpringConstant = 500;
        final float mySpringDamping = 5;
        ab = theParticleSystem.makeSpring(a, b, mySpringConstant, mySpringDamping);
        bc = theParticleSystem.makeSpring(b, c, mySpringConstant, mySpringDamping);
        cd = theParticleSystem.makeSpring(c, d, mySpringConstant, mySpringDamping);
        da = theParticleSystem.makeSpring(d, a, mySpringConstant, mySpringDamping);
        /* diagonals */
        ac = theParticleSystem.makeSpring(a, c, mySpringConstant, mySpringDamping);
        bd = theParticleSystem.makeSpring(b, d, mySpringConstant, mySpringDamping);
    }
}
