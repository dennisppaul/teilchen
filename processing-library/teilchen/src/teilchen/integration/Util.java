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
package teilchen.integration;

import java.util.List;
import teilchen.Particle;

public final class Util {

    public static <T> void checkContainerSize(final int theSize,
                                              final List<T> theContainer,
                                              Class<T> theClass) {
        final int myDiff = theSize - theContainer.size();
        if (myDiff > 0) {
            for (int i = 0; i < myDiff; i++) {
                try {
                    theContainer.add(theClass.newInstance());
                } catch (Exception ex) {
                    System.err.println(ex);
                }
            }
        } else if (myDiff < 0) {
            for (int i = 0; i < myDiff; i++) {
                theContainer.remove(myDiff + theSize);
            }
        }
    }

    public static void calculateDerivatives(final List<Particle> theParticles,
                                            final List<Derivate3f> theDerivates) {
        for (int i = 0; i < theParticles.size(); i++) {
            theDerivates.get(i).px = theParticles.get(i).velocity().x;
            theDerivates.get(i).py = theParticles.get(i).velocity().y;
            theDerivates.get(i).pz = theParticles.get(i).velocity().z;
            theDerivates.get(i).vx = theParticles.get(i).force().x / theParticles.get(i).mass();
            theDerivates.get(i).vy = theParticles.get(i).force().y / theParticles.get(i).mass();
            theDerivates.get(i).vz = theParticles.get(i).force().z / theParticles.get(i).mass();
        }
    }
}
