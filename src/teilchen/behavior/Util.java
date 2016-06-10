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
package teilchen.behavior;

import java.util.ArrayList;
import processing.core.PVector;
import static processing.core.PVector.sub;
import teilchen.IBehaviorParticle;
import teilchen.Particle;

public class Util {

    public static class ProximityStructure {

        public final Particle particle;

        public final PVector distanceVec;

        public final float distance;

        public ProximityStructure(Particle pP, PVector pDistanceVec, float pDistance) {
            particle = pP;
            distanceVec = pDistanceVec;
            distance = pDistance;
        }

        public static ArrayList<ProximityStructure> findProximityEntities(IBehaviorParticle pParentEntity,
                                                                          ArrayList<IBehaviorParticle> pNeighborsEntity,
                                                                       float pProximity) {
            /* find neighbors in proximity */
            ArrayList<ProximityStructure> mCloseNeighbors = new ArrayList<>();
            for (IBehaviorParticle p : pNeighborsEntity) {
                if (!p.equals(pParentEntity)) {
                    /* exclude self */

                    final PVector mDistanceVec = sub(pParentEntity.position(), p.position());
                    final float mDistance = mDistanceVec.mag();
                    if (mDistance <= pProximity) {
                        mCloseNeighbors.add(new ProximityStructure(p, mDistanceVec, mDistance));
                    }
                }
            }
            return mCloseNeighbors;
        }
    }
}
