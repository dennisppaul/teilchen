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
package teilchen.integration;

import teilchen.Particle;

import java.util.List;

public final class Util {

    public static void calculateDerivatives(final List<Particle> pParticles, final List<Derivate3f> pDerivates) {
        for (int i = 0; i < pParticles.size(); i++) {
            pDerivates.get(i).px = pParticles.get(i).velocity().x;
            pDerivates.get(i).py = pParticles.get(i).velocity().y;
            pDerivates.get(i).pz = pParticles.get(i).velocity().z;
            pDerivates.get(i).vx = pParticles.get(i).force().x / pParticles.get(i).mass();
            pDerivates.get(i).vy = pParticles.get(i).force().y / pParticles.get(i).mass();
            pDerivates.get(i).vz = pParticles.get(i).force().z / pParticles.get(i).mass();
        }
    }

    public static <T> void checkContainerSize(final int pSize, final List<T> pContainer, Class<T> pClass) {
        final int mDiff = pSize - pContainer.size();
        if (mDiff > 0) {
            for (int i = 0; i < mDiff; i++) {
                try {
                    pContainer.add(pClass.newInstance());
                } catch (Exception ex) {
                    System.err.println(ex);
                }
            }
        } else if (mDiff < 0) {
            for (int i = 0; i < mDiff; i++) {
                pContainer.remove(mDiff + pSize);
            }
        }
    }
}
