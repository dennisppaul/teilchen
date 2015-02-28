package teilchen.force;

import mathematik.Intersection;
import mathematik.Intersection.IntersectionResult;
import mathematik.Vector3f;
import mathematik.WorldAxisAlignedBoundingBox;

import teilchen.Particle;
import teilchen.Physics;
import teilchen.constraint.IConstraint;

public class TriangleDeflector
        implements IConstraint {

    private final Vector3f a;

    private final Vector3f b;

    private final Vector3f c;

    private final Vector3f mNormal;

    private float mCoefficientOfRestitution;

    private final Vector3f mTempReflectionVector;

    private final Vector3f mTempNormalComponent;

    private final Vector3f mTempTangentComponent;

    private final IntersectionResult mIntersectionResult;

    private final Vector3f mTempPointOfIntersection = new Vector3f();

    private final WorldAxisAlignedBoundingBox mWorldAxisAlignedBoundingBox;

    private final Vector3f[] mVectorCollection;

    public boolean AUTO_UPDATE = true;

    private boolean mGotHit = false;

    private boolean mActive;

    public TriangleDeflector() {
        a = new Vector3f();
        b = new Vector3f();
        c = new Vector3f();

        /* hmmm. */
        mVectorCollection = new Vector3f[3];
        mVectorCollection[0] = a;
        mVectorCollection[1] = b;
        mVectorCollection[2] = c;

        mNormal = new Vector3f();
        mCoefficientOfRestitution = 1.0f;

        mTempReflectionVector = new Vector3f();
        mTempNormalComponent = new Vector3f();
        mTempTangentComponent = new Vector3f();
        mIntersectionResult = new IntersectionResult();
        mWorldAxisAlignedBoundingBox = new WorldAxisAlignedBoundingBox();

        mActive = true;
    }

    public Vector3f a() {
        return a;
    }

    public Vector3f b() {
        return b;
    }

    public Vector3f c() {
        return c;
    }

    public WorldAxisAlignedBoundingBox boundingbox() {
        return mWorldAxisAlignedBoundingBox;
    }

    public void updateProperties() {
        mVectorCollection[0] = a;
        mVectorCollection[1] = b;
        mVectorCollection[2] = c;
        mathematik.Util.calculateNormal(a, b, c, mNormal);
        mathematik.Util.updateBoundingBox(mWorldAxisAlignedBoundingBox, mVectorCollection);
    }

    private float mPreviousT = -1.0f;

    public void apply(Physics pParticleSystem) {
//    public void apply(final float pDeltaTime, final Physics pParticleSystem) {

        /* update triangle properties -- maybe this is better not done automatically */
        if (AUTO_UPDATE) {
            updateProperties();
        }

        mGotHit = false;
        for (final Particle myParticle : pParticleSystem.particles()) {
            if (!myParticle.fixed()) {
                final boolean IGNORE_BOUNDING_BOX = true;

                /* adjust boundingbox width to particle velocity to avoid particle shooting through the boundingbox */
                final Vector3f myTempBoundingBoxScale = new Vector3f(mWorldAxisAlignedBoundingBox.scale);
                if (!IGNORE_BOUNDING_BOX) {
                    if (myParticle.velocity().x > mWorldAxisAlignedBoundingBox.scale.x) {
                        mWorldAxisAlignedBoundingBox.scale.x = myParticle.velocity().x;
                    }
                    if (myParticle.velocity().y > mWorldAxisAlignedBoundingBox.scale.y) {
                        mWorldAxisAlignedBoundingBox.scale.y = myParticle.velocity().y;
                    }
                    if (myParticle.velocity().z > mWorldAxisAlignedBoundingBox.scale.z) {
                        mWorldAxisAlignedBoundingBox.scale.z = myParticle.velocity().z;
                    }
                }

                /* only test if in bounding box */
                if (IGNORE_BOUNDING_BOX || mathematik.Util.contains(myParticle.position(), mWorldAxisAlignedBoundingBox)) {
                    final Vector3f mRay;
                    final int RAY_FROM_VELOCITY = 0;
                    final int RAY_FROM_NORMAL = 1;
                    final int RAY_FROM_OLD_POSITION = 2;
                    final int CREATE_RAY_FROM = RAY_FROM_OLD_POSITION;

                    switch (CREATE_RAY_FROM) {
                        case RAY_FROM_VELOCITY:
                            mRay = myParticle.velocity();
                            break;
                        case RAY_FROM_NORMAL:
                            mRay = mathematik.Util.scale(mNormal, -myParticle.velocity().length());
                            break;
                        case RAY_FROM_OLD_POSITION:
                            mRay = mathematik.Util.sub(myParticle.position(), myParticle.old_position());
                            break;
                        default:
                            mRay = new Vector3f(1, 0, 0);
                            break;
                    }

                    if (mRay.isNaN()) {
                        break;
                    }
                    if (mRay.lengthSquared() == 0) {
                        break;
                    }
                    final boolean mSuccess = Intersection.intersectRayTriangle(myParticle.position(),
                                                                               mRay,
                                                                               a, b, c,
                                                                               mIntersectionResult,
                                                                               true);
                    /* is particle past plane. */
                    if (mSuccess && mIntersectionResult.t <= 0 && mPreviousT > 0) {
                        mTempPointOfIntersection.set(mRay);
                        mTempPointOfIntersection.scale(mIntersectionResult.t);
                        mTempPointOfIntersection.add(myParticle.position());
                        myParticle.position().set(mTempPointOfIntersection);

                        /* reflect velocity i.e. change direction */
                        seperateComponents(myParticle, mNormal);
                        myParticle.velocity().set(mTempReflectionVector);

                        mGotHit = true;
                        myParticle.tag(true);
                        markParticle(myParticle);
//                        mPreviousT = 0.0f; /* ??? */

                    }
                    if (mSuccess) {
                        mPreviousT = mIntersectionResult.t;
                    } else {
                        mPreviousT = 0.0f;
                    }

                }

                /* reset boundingbox scale */
                if (!IGNORE_BOUNDING_BOX) {
                    mWorldAxisAlignedBoundingBox.scale.set(myTempBoundingBoxScale);
                }
            }
        }
    }

    protected void markParticle(Particle pParticle) {
    }

    public boolean hit() {
        return mGotHit;
    }

    public void coefficientofrestitution(float pCoefficientOfRestitution) {
        mCoefficientOfRestitution = pCoefficientOfRestitution;
    }

    public float coefficientofrestitution() {
        return mCoefficientOfRestitution;
    }

    private void seperateComponents(Particle pParticle, Vector3f pNormal) {
        /* normal */
        mTempNormalComponent.set(pNormal);
        mTempNormalComponent.scale(pNormal.dot(pParticle.velocity()));
        /* tangent */
        mTempTangentComponent.sub(pParticle.velocity(), mTempNormalComponent);
        /* negate normal */
        mTempNormalComponent.scale(-mCoefficientOfRestitution);
        /* set reflection vector */
        mTempReflectionVector.add(mTempTangentComponent, mTempNormalComponent);
    }

    public Vector3f normal() {
        return mNormal;
    }

    public boolean dead() {
        return false;
    }

    public boolean active() {
        return mActive;
    }

    public void active(boolean pActiveState) {
        mActive = pActiveState;
    }
}
