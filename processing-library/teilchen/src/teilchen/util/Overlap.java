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

import java.util.List;
import processing.core.PVector;

public class Overlap {

    public static PVector RESOLVE_SAME_PLACE = new PVector(1, 0, 0);

    public static <E extends SpatialEntity> void resolveOverlap(E theEntityA,
                                                                E theEntityB) {
        if (theEntityB == theEntityA) {
            return;
        }

        if (theEntityA.radius() == 0 || theEntityB.radius() == 0) {
            return;
        }

        final PVector mAB = PVector.sub(theEntityA.position(), theEntityB.position());
        final float myDistance = mAB.mag();

        if (myDistance > 0) {
            float myOverlap = theEntityB.radius() + theEntityA.radius() - myDistance;

            if (myOverlap > 0) {
                mAB.mult(0.5f * myOverlap / myDistance);
                theEntityA.position().add(mAB);
                theEntityB.position().sub(mAB);
            }
        } else if (RESOLVE_SAME_PLACE != null) {
            final PVector myOffset = Util.clone(RESOLVE_SAME_PLACE);
            myOffset.mult(theEntityB.radius() + theEntityA.radius());
            myOffset.mult(0.5f);
            theEntityA.position().add(myOffset);
            theEntityB.position().sub(myOffset);
        }
    }

    public static <E extends SpatialEntity> void resolveOverlap(E theEntity,
                                                                E[] theEntities) {
        if (theEntities == null || theEntities.length < 1) {
            return;
        }

        for (E theEntitie : theEntities) {
            resolveOverlap(theEntitie, theEntity);
        }
    }

    public static <E extends SpatialEntity> void resolveOverlap(E theEntity,
                                                                List<E> theEntities) {
        if (theEntities == null || theEntities.size() < 1) {
            return;
        }

        for (int i = 0; i < theEntities.size(); i++) {
            resolveOverlap(theEntities.get(i), theEntity);
        }
    }

    public static <E extends SpatialEntity> void resolveOverlap(List<E> theEntities) {
        if (theEntities == null || theEntities.isEmpty()) {
            return;
        }

        /**
         * @todo room for improvement. there is some redundant testing going on
         * here.
         */
        for (int i = 0; i < theEntities.size(); i++) {
            for (int j = 0; j < theEntities.size(); j++) {
                if (i == j) {
                    continue;
                }
//                final SpatialEntity myOtherEntity = theEntities.get(j);
                resolveOverlap(theEntities.get(i), theEntities.get(j));
            }
        }
    }
}
