/*
 * Teilchen
 *
 * Copyright (C) 2020
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

    public static <E extends SpatialEntity> void resolveOverlap(E pEntityA,
                                                                E pEntityB) {
        if (pEntityB == pEntityA) {
            return;
        }

        if (pEntityA.radius() == 0 || pEntityB.radius() == 0) {
            return;
        }

        final PVector mAB = PVector.sub(pEntityA.position(), pEntityB.position());
        final float mDistance = mAB.mag();

        if (mDistance > 0) {
            float mOverlap = pEntityB.radius() + pEntityA.radius() - mDistance;

            if (mOverlap > 0) {
                mAB.mult(0.5f * mOverlap / mDistance);
                pEntityA.position().add(mAB);
                pEntityB.position().sub(mAB);
            }
        } else if (RESOLVE_SAME_PLACE != null) {
            final PVector mOffset = Util.clone(RESOLVE_SAME_PLACE);
            mOffset.mult(pEntityB.radius() + pEntityA.radius());
            mOffset.mult(0.5f);
            pEntityA.position().add(mOffset);
            pEntityB.position().sub(mOffset);
        }
    }

    public static <E extends SpatialEntity> void resolveOverlap(E pEntity,
                                                                E[] pEntities) {
        if (pEntities == null || pEntities.length < 1) {
            return;
        }

        for (E pEntitie : pEntities) {
            resolveOverlap(pEntitie, pEntity);
        }
    }

    public static <E extends SpatialEntity> void resolveOverlap(E pEntity,
                                                                List<E> pEntities) {
        if (pEntities == null || pEntities.size() < 1) {
            return;
        }

        for (int i = 0; i < pEntities.size(); i++) {
            resolveOverlap(pEntities.get(i), pEntity);
        }
    }

    public static <E extends SpatialEntity> void resolveOverlap(List<E> pEntities) {
        if (pEntities == null || pEntities.isEmpty()) {
            return;
        }

        /**
         * @todo room for improvement. there is some redundant testing going on
         * here.
         */
        for (int i = 0; i < pEntities.size(); i++) {
            for (int j = 0; j < pEntities.size(); j++) {
                if (i == j) {
                    continue;
                }
//                final SpatialEntity mOtherEntity = theEntities.get(j);
                resolveOverlap(pEntities.get(i), pEntities.get(j));
            }
        }
    }
}
