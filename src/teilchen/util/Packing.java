package teilchen.util;

import java.util.List;
import java.util.ArrayList;
import processing.core.PVector;

public class Packing {

    public static <E extends SpatialEntity> void pack(ArrayList<E> mShapes, PVector pCenter, float pDamping) {
        resolveOverlap(mShapes);
        contract(mShapes, pCenter, pDamping);
    }

    public static <E extends SpatialEntity> void resolveOverlap(List<E> mEntities) {
        /* @TODO this is somewhat redundant to 'AntiOverlap' */
        for (int i = 0; i < mEntities.size(); i++) {
            final SpatialEntity mA = mEntities.get(i);
            for (int j = i + 1; j < mEntities.size(); j++) {
                if (i != j) {
                    final SpatialEntity mB = mEntities.get(j);
                    final float mIdealDistance = mA.radius() + mB.radius();
                    final PVector mAB = PVector.sub(mB.position(), mA.position());
                    final float mDistance = mAB.mag();
                    if (mDistance > 0.0f) {
                        if (mDistance < mIdealDistance) {
                            mAB.normalize();
                            mAB.mult((mIdealDistance - mDistance) * 0.5f);
                            mB.position().add(mAB);
                            mA.position().sub(mAB);
                        }
                    }
                }
            }
        }
    }

    public static <E extends SpatialEntity> void contract(ArrayList<E> mEntities, PVector pCenter, float pDamping) {
        for (int i = 0; i < mEntities.size(); i++) {
            final SpatialEntity mEntity = mEntities.get(i);
            final PVector v = PVector.sub(mEntity.position(), pCenter);
            v.mult(pDamping);
            mEntity.position().sub(v);
        }
    }

    public static class PackingEntity
            implements SpatialEntity {

        private final PVector mPosition;

        private float mRadius;

        public PackingEntity() {
            mPosition = new PVector();
            mRadius = 1;
        }

        public float radius() {
            return mRadius;
        }

        public PVector position() {
            return mPosition;
        }

        public void radius(float pRadius) {
            mRadius = pRadius;
        }
    }
}
