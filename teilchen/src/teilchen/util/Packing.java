package teilchen.util;


import mathematik.Vector3f;

import java.util.List;
import java.util.Vector;


public class Packing {

    public static <E extends SpatialEntity> void pack(Vector<E> mShapes, Vector3f pCenter, float pDamping) {
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
                    final Vector3f mAB = mathematik.Util.sub(mB.position(), mA.position());
                    final float mDistance = mAB.length();
                    if (mDistance > 0.0f) {
                        if (mDistance < mIdealDistance) {
                            mAB.normalize();
                            mAB.scale((mIdealDistance - mDistance) * 0.5f);
                            mB.position().add(mAB);
                            mA.position().sub(mAB);
                        }
                    }
                }
            }
        }
    }

    public static <E extends SpatialEntity> void contract(Vector<E> mEntities, Vector3f pCenter, float pDamping) {
        for (int i = 0; i < mEntities.size(); i++) {
            final SpatialEntity mEntity = mEntities.get(i);
            final Vector3f v = mathematik.Util.sub(mEntity.position(), pCenter);
            v.scale(pDamping);
            mEntity.position().sub(v);
        }
    }

    public static class PackingEntity
            implements SpatialEntity {

        private Vector3f mPosition;

        private float mRadius;

        public PackingEntity() {
            mPosition = new Vector3f();
            mRadius = 1;
        }

        public float radius() {
            return mRadius;
        }

        public Vector3f position() {
            return mPosition;
        }

        public void radius(float pRadius) {
            mRadius = pRadius;
        }
    }
}
