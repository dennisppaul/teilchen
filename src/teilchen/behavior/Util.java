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

package teilchen.behavior;

import processing.core.PVector;
import teilchen.IBehaviorParticle;
import teilchen.IParticle;

import java.util.ArrayList;

import static processing.core.PVector.sub;

public class Util {

    public static class ProximityStructure {

        public final float distance;
        public final PVector distanceVec;
        public final IParticle particle;

        public ProximityStructure(IParticle pP, PVector pDistanceVec, float pDistance) {
            particle = pP;
            distanceVec = pDistanceVec;
            distance = pDistance;
        }

        public static <E extends IBehaviorParticle> ArrayList<ProximityStructure> findProximityEntities(
                IBehaviorParticle pParentEntity,
                ArrayList<E> pNeighborsEntity,
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
