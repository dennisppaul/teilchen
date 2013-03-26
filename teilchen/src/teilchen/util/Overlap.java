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

import java.util.List;


public class Overlap {

    public static Vector3f RESOLVE_SAME_PLACE = new Vector3f(1, 0, 0);

    public static <E extends SpatialEntity> void resolveOverlap(E theEntityA,
                                                                E theEntityB) {
        if (theEntityB == theEntityA) {
            return;
        }

        if (theEntityA.radius() == 0 || theEntityB.radius() == 0) {
            return;
        }

        final Vector3f mAB = mathematik.Util.sub(theEntityA.position(), theEntityB.position());
        final float myDistance = mAB.length();

        if (myDistance > 0) {
            float myOverlap = theEntityB.radius() + theEntityA.radius() - myDistance;

            if (myOverlap > 0) {
                mAB.scale(0.5f * myOverlap / myDistance);
                theEntityA.position().add(mAB);
                theEntityB.position().sub(mAB);
            }
        } else {
            if (RESOLVE_SAME_PLACE != null) {
                final Vector3f myOffset = new Vector3f(RESOLVE_SAME_PLACE);
                myOffset.scale(theEntityB.radius() + theEntityA.radius());
                myOffset.scale(0.5f);
                theEntityA.position().add(myOffset);
                theEntityB.position().sub(myOffset);
            }
        }
    }

    public static <E extends SpatialEntity> void resolveOverlap(E theEntity,
                                                                E[] theEntities) {
        if (theEntities == null || theEntities.length < 1) {
            return;
        }

        for (int i = 0; i < theEntities.length; i++) {
            resolveOverlap(theEntities[i], theEntity);
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
