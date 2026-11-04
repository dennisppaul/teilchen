/*
 * Teilchen
 *
 * This file is part of the *teilchen* library (https://github.com/dennisppaul/teilchen).
 * Copyright (c) 2024 Dennis P Paul.
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

package teilchen.integration;

import processing.core.PVector;
import teilchen.Particle;

import java.util.List;

public final class IntegrationUtil {

    public static void calculateDerivatives(final List<Particle> pParticles, final List<Derivate3f> pDerivates) {
        synchronized (pParticles) {
            final int mSize = Math.min(pParticles.size(), pDerivates.size());
            for (int i = 0; i < mSize; i++) {
                final Particle    p = pParticles.get(i);
                final Derivate3f d = pDerivates.get(i);
                d.px = p.velocity().x;
                d.py = p.velocity().y;
                d.pz = p.velocity().z;
                d.vx = p.force().x / p.mass();
                d.vy = p.force().y / p.mass();
                d.vz = p.force().z / p.mass();
            }
        }
    }

    /**
     * resizes container to exactly <code>pSize</code> elements, creating new elements with the default constructor
     * of <code>pClass</code>.
     */
    public static <T> void checkContainerSize(final int pSize, final List<T> pContainer, Class<T> pClass) {
        while (pContainer.size() < pSize) {
            try {
                pContainer.add(pClass.getDeclaredConstructor().newInstance());
            } catch (Exception ex) {
                throw new RuntimeException("could not create instance of " + pClass.getName(), ex);
            }
        }
        while (pContainer.size() > pSize) {
            pContainer.remove(pContainer.size() - 1);
        }
    }

    /**
     * stores position, velocity and accumulated force of each particle.
     */
    public static void saveState(final List<Particle> pParticles,
                                 final List<PVector> pPositions,
                                 final List<PVector> pVelocities,
                                 final List<PVector> pForces) {
        for (int i = 0; i < pParticles.size(); i++) {
            final Particle p = pParticles.get(i);
            pPositions.get(i).set(p.position());
            pVelocities.get(i).set(p.velocity());
            pForces.get(i).set(p.force());
        }
    }

    /**
     * resets the force of each particle to the stored value. used by multi-stage integrators so that forces are not
     * accumulated across stages.
     */
    public static void restoreForces(final List<Particle> pParticles, final List<PVector> pForces) {
        for (int i = 0; i < pParticles.size(); i++) {
            pParticles.get(i).force().set(pForces.get(i));
        }
    }
}
