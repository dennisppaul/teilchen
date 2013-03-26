/*
 * Teilchen
 *
 * Copyright (C) 2013
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


import mathematik.Vector3f;

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
     * plus two diagonals to create a stable quad.<br/>
     * the positions should be in counter clockwise order.<br/>
     * the positions are stored as reference which means that if you change
     * either of the vectors afterwards it will also change the position of the
     * connected particles.
     *
     * @param theParticleSystem ParticleSystem
     * @param a Vector3f
     * @param b Vector3f
     * @param c Vector3f
     * @param d Vector3f
     */
    public StableSpringQuad(final Physics theParticleSystem,
                            final Vector3f theA,
                            final Vector3f theB,
                            final Vector3f theC,
                            final Vector3f theD) {
        a = theParticleSystem.makeParticle();
        b = theParticleSystem.makeParticle();
        c = theParticleSystem.makeParticle();
        d = theParticleSystem.makeParticle();

        a.setPositionRef(theA);
        b.setPositionRef(theB);
        c.setPositionRef(theC);
        d.setPositionRef(theD);

        /* edges */
        final float mySpringConstant = 100;
        final float mySpringDamping = 5;
        ab = theParticleSystem.makeSpring(a, b, mySpringConstant, mySpringDamping);
        bc = theParticleSystem.makeSpring(b, c, mySpringConstant, mySpringDamping);
        cd = theParticleSystem.makeSpring(c, d, mySpringConstant, mySpringDamping);
        da = theParticleSystem.makeSpring(d, a, mySpringConstant, mySpringDamping);
        /* diagonals */
        ac = theParticleSystem.makeSpring(a, c, mySpringConstant, mySpringDamping);
        bd = theParticleSystem.makeSpring(b, d, mySpringConstant, mySpringDamping);
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
