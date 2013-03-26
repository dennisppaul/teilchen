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
package teilchen.behavior;


import mathematik.Vector3f;

import teilchen.IBehaviorParticle;
import teilchen.Particle;

import java.util.Vector;


public class Util {

    public static class ProximityStructure {

        public final Particle particle;

        public final Vector3f distanceVec;

        public final float distance;

        public ProximityStructure(Particle pP, Vector3f pDistanceVec, float pDistance) {
            particle = pP;
            distanceVec = pDistanceVec;
            distance = pDistance;
        }

        public static Vector<ProximityStructure> findProximityEntities(IBehaviorParticle pParentEntity,
                                                                       Vector<IBehaviorParticle> pNeighborsEntity,
                                                                       float pProximity) {
            /* find neighbors in proximity */
            Vector<ProximityStructure> mCloseNeighbors = new Vector<ProximityStructure>();
            for (IBehaviorParticle p : pNeighborsEntity) {
                if (!p.equals(pParentEntity)) { /* exclude self */
                    final Vector3f mDistanceVec = mathematik.Util.sub(pParentEntity.position(), p.position());
                    final float mDistance = mDistanceVec.length();
                    if (mDistance <= pProximity) {
                        mCloseNeighbors.add(new ProximityStructure(p, mDistanceVec, mDistance));
                    }
                }
            }
            return mCloseNeighbors;
        }
    }
}
