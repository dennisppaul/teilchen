package teilchen.force;

import processing.core.PVector;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.util.WorldAxisAlignedBoundingBox;

import static processing.core.PApplet.abs;
import static processing.core.PVector.add;
import static processing.core.PVector.mult;
import static processing.core.PVector.sub;
import static teilchen.util.Intersection.intersect_line_segment_plane;
import static teilchen.util.Util.calculateNormal;
import static teilchen.util.Util.distance_point_plane;
import static teilchen.util.Util.isNaN;
import static teilchen.util.Util.lengthSquared;
import static teilchen.util.Util.point_in_triangle;
import static teilchen.util.Util.project_point_onto_plane;
import static teilchen.util.Util.project_vector_onto_plane;
import static teilchen.util.Util.reflect;
import static teilchen.util.Util.updateBoundingBox;

public class TriangleDeflector implements IForce {
    public boolean AUTO_UPDATE = true;
    private final PVector a;
    private final PVector b;
    private final PVector c;
    private boolean mActive;
    private float mCoefficientOfRestitution;
    private boolean mDead;
    private boolean mGotHit = false;
    private final long mID;
    //    private final IntersectionResult mIntersectionResult;
    private final PVector mNormal;
    private final PVector mTempNormalComponent;
    private final PVector mTempPointOfIntersection = new PVector();
    private final PVector mTempReflectionVector;
    private final PVector mTempTangentComponent;
    private final PVector[] mVectorCollection;
    private final WorldAxisAlignedBoundingBox mWorldAxisAlignedBoundingBox;

    public TriangleDeflector() {
        mID = Physics.getUniqueID();
        a = new PVector();
        b = new PVector();
        c = new PVector();

        /* hmmm. */
        mVectorCollection = new PVector[3];
        mVectorCollection[0] = a;
        mVectorCollection[1] = b;
        mVectorCollection[2] = c;

        mNormal = new PVector();
        mCoefficientOfRestitution = 1.0f;

        mTempReflectionVector = new PVector();
        mTempNormalComponent = new PVector();
        mTempTangentComponent = new PVector();
//        mIntersectionResult = new IntersectionResult();
        mWorldAxisAlignedBoundingBox = new WorldAxisAlignedBoundingBox();

        mActive = true;
        mDead = false;
    }

    public PVector a() {
        return a;
    }

    public PVector b() {
        return b;
    }

    public PVector c() {
        return c;
    }

    public WorldAxisAlignedBoundingBox boundingbox() {
        return mWorldAxisAlignedBoundingBox;
    }

    public void updateProperties() {
        mVectorCollection[0] = a;
        mVectorCollection[1] = b;
        mVectorCollection[2] = c;
        calculateNormal(a, b, c, mNormal);
        updateBoundingBox(mWorldAxisAlignedBoundingBox, mVectorCollection);
    }

    private boolean calculateIntersection(Particle mParticle, float delta_time) {
        if (!mParticle.fixed()) {
            if (isNaN(mParticle.velocity())) {
                return false;
            }
            if (lengthSquared(mParticle.velocity()) == 0) {
                return false;
            }

            /* proximity */
            final PVector mPrevPosition = add(mParticle.position(), mult(mParticle.velocity(), -delta_time));
            final float mDistanceToPlane = abs(distance_point_plane(mPrevPosition, a, mNormal));
            final float mDistanceEpsilon = 0.2f;
            PVector mIntersection = intersect_line_segment_plane(mParticle.position(), mPrevPosition, a, mNormal);
            if (point_in_triangle(a, b, c, mParticle.position()) && mDistanceToPlane < mDistanceEpsilon) {
                keepParticleOnPlane(mParticle, mPrevPosition);
                return true;
            }

            /* parallel */
            final float mAngle = abs(new PVector().set(mParticle.velocity()).normalize().dot(mNormal));
            final float mAngleEpsilon = 0.001f;
            if (mAngle < mAngleEpsilon && mDistanceToPlane < mDistanceEpsilon) {
                keepParticleOnPlane(mParticle, mPrevPosition);
                return true;
            }

            if (mIntersection != null && point_in_triangle(a, b, c, mIntersection)) {
                PVector mSegmentCollisionPastPlane = sub(mParticle.position(), mIntersection);
                PVector mPositionReflectionPast = reflect(mSegmentCollisionPastPlane, mNormal, false);
                mParticle.position().set(mIntersection);
                mParticle.position().add(mPositionReflectionPast);

                PVector mSegmentCollisionBeforePlane = sub(mIntersection, mPrevPosition);
                PVector mPositionReflectionBefore = reflect(mSegmentCollisionBeforePlane, mNormal, false);
                mParticle.old_position().set(mIntersection);
                mParticle.old_position().sub(mPositionReflectionBefore);

                PVector mVelocityReflection = reflect(mParticle.velocity(), mNormal, false);
                mParticle.velocity().set(mVelocityReflection).mult(mCoefficientOfRestitution);

                mParticle.tag(true);
                return true;
            }
        }
        return false;
    }

    private void keepParticleOnPlane(Particle mParticle, PVector pPrevPosition) {
        mParticle.velocity().set(project_vector_onto_plane(mParticle.velocity(), mNormal));
        mParticle.position().set(project_point_onto_plane(mParticle.position(), a, mNormal));
        mParticle.old_position().set(pPrevPosition);
        mParticle.tag(true);
    }

    public void apply(final float pDeltaTime, final Physics pParticleSystem) {
        /* update triangle properties -- maybe this is better not done automatically */
        if (AUTO_UPDATE) {
            updateProperties();
        }

        mGotHit = false;
        for (final Particle mParticle : pParticleSystem.particles()) {
            if (!mParticle.fixed()) {
                boolean mDoesIntersect = calculateIntersection(mParticle, pDeltaTime);
                if (mDoesIntersect && !mGotHit) {
                    mGotHit = true;
                }

//                final boolean IGNORE_BOUNDING_BOX = true;
//                /* adjust boundingbox width to particle velocity to avoid particle shooting through the boundingbox */
//                final PVector mTempBoundingBoxScale = new PVector();
//                mTempBoundingBoxScale.set(mWorldAxisAlignedBoundingBox.scale);
//                if (!IGNORE_BOUNDING_BOX) {
//                    if (mParticle.velocity().x > mWorldAxisAlignedBoundingBox.scale.x) {
//                        mWorldAxisAlignedBoundingBox.scale.x = mParticle.velocity().x;
//                    }
//                    if (mParticle.velocity().y > mWorldAxisAlignedBoundingBox.scale.y) {
//                        mWorldAxisAlignedBoundingBox.scale.y = mParticle.velocity().y;
//                    }
//                    if (mParticle.velocity().z > mWorldAxisAlignedBoundingBox.scale.z) {
//                        mWorldAxisAlignedBoundingBox.scale.z = mParticle.velocity().z;
//                    }
//                }
//
//                /* only test if in bounding box */
//                if (IGNORE_BOUNDING_BOX || contains(mParticle.position(), mWorldAxisAlignedBoundingBox)) {
//                    final PVector mRay;
//                    final int RAY_FROM_VELOCITY = 0;
//                    final int RAY_FROM_NORMAL = 1;
//                    final int RAY_FROM_OLD_POSITION = 2;
//                    final int CREATE_RAY_FROM = RAY_FROM_OLD_POSITION;
//
//                    switch (CREATE_RAY_FROM) {
//                        case RAY_FROM_VELOCITY:
//                            // todo why does this not work?
//                            mRay = new PVector().set(mParticle.velocity());
//                            break;
//                        case RAY_FROM_NORMAL:
//                            mRay = PVector.mult(mNormal, -mParticle.velocity().mag());
//                            break;
//                        case RAY_FROM_OLD_POSITION:
//                            mRay = PVector.sub(mParticle.position(), mParticle.old_position());
//                            break;
//                        default:
//                            mRay = new PVector(1, 0, 0);
//                            break;
//                    }
//
//                    if (isNaN(mRay)) {
//                        continue;
//                    }
//                    if (lengthSquared(mRay) == 0) {
//                        continue;
//                    }
//                    mIntersectionResult.clear();
//                    final boolean mSuccess = Intersection.intersectRayTriangle(mParticle.position(),
//                                                                               mRay,
//                                                                               a,
//                                                                               b,
//                                                                               c,
//                                                                               mIntersectionResult,
//                                                                               true);
//
//                    /* is particle past plane. */
//                    if (mSuccess && mIntersectionResult.t <= 0) {
//                        mTempPointOfIntersection.set(mRay);
//                        mTempPointOfIntersection.mult(mIntersectionResult.t);
//                        mTempPointOfIntersection.add(mParticle.position());
//                        mParticle.position().set(mTempPointOfIntersection);
//                        /* reflect velocity i.e. change direction */
//                        separateComponents(mParticle, mNormal);
//                        if (Util.almost(mIntersectionResult.t, -1)) {
//                            /* particle is still */
//                            setVelocityAndOldPosition(mParticle, mTempReflectionVector);
//                            mParticle.still(true);
//                        } else {
//                            setVelocityAndOldPosition(mParticle, mTempReflectionVector);
//                        }
//                        mGotHit = true;
//                        markParticle(mParticle);
//                    }
//                }
//
//                /* reset boundingbox scale */
//                if (!IGNORE_BOUNDING_BOX) {
//                    mWorldAxisAlignedBoundingBox.scale.set(mTempBoundingBoxScale);
//                }
            }
        }
    }

//    public void apply(final float pDeltaTime, final Physics pParticleSystem) {
//        /* update triangle properties -- maybe this is better not done automatically */
//        if (AUTO_UPDATE) {
//            updateProperties();
//        }
//
//        mGotHit = false;
//        for (final Particle mParticle : pParticleSystem.particles()) {
//            if (!mParticle.fixed()) {
//
//                final boolean IGNORE_BOUNDING_BOX = true;
//                /* adjust boundingbox width to particle velocity to avoid particle shooting through the boundingbox */
//                final PVector mTempBoundingBoxScale = new PVector();
//                mTempBoundingBoxScale.set(mWorldAxisAlignedBoundingBox.scale);
//                if (!IGNORE_BOUNDING_BOX) {
//                    if (mParticle.velocity().x > mWorldAxisAlignedBoundingBox.scale.x) {
//                        mWorldAxisAlignedBoundingBox.scale.x = mParticle.velocity().x;
//                    }
//                    if (mParticle.velocity().y > mWorldAxisAlignedBoundingBox.scale.y) {
//                        mWorldAxisAlignedBoundingBox.scale.y = mParticle.velocity().y;
//                    }
//                    if (mParticle.velocity().z > mWorldAxisAlignedBoundingBox.scale.z) {
//                        mWorldAxisAlignedBoundingBox.scale.z = mParticle.velocity().z;
//                    }
//                }
//
//                /* only test if in bounding box */
//                if (IGNORE_BOUNDING_BOX || contains(mParticle.position(), mWorldAxisAlignedBoundingBox)) {
//                    final PVector mRay;
//                    final int RAY_FROM_VELOCITY = 0;
//                    final int RAY_FROM_NORMAL = 1;
//                    final int RAY_FROM_OLD_POSITION = 2;
//                    final int CREATE_RAY_FROM = RAY_FROM_OLD_POSITION;
//
//                    switch (CREATE_RAY_FROM) {
//                        case RAY_FROM_VELOCITY:
//                            // todo why does this not work?
//                            mRay = new PVector().set(mParticle.velocity());
//                            break;
//                        case RAY_FROM_NORMAL:
//                            mRay = PVector.mult(mNormal, -mParticle.velocity().mag());
//                            break;
//                        case RAY_FROM_OLD_POSITION:
//                            mRay = PVector.sub(mParticle.position(), mParticle.old_position());
//                            break;
//                        default:
//                            mRay = new PVector(1, 0, 0);
//                            break;
//                    }
//
//                    if (isNaN(mRay)) {
//                        continue;
//                    }
//                    if (lengthSquared(mRay) == 0) {
//                        continue;
//                    }
//                    mIntersectionResult.clear();
//                    final boolean mSuccess = Intersection.intersectRayTriangle(mParticle.position(),
//                                                                               mRay,
//                                                                               a,
//                                                                               b,
//                                                                               c,
//                                                                               mIntersectionResult,
//                                                                               true);
//
//                    /* is particle past plane. */
//                    if (mSuccess && mIntersectionResult.t <= 0) {
//                        mTempPointOfIntersection.set(mRay);
//                        mTempPointOfIntersection.mult(mIntersectionResult.t);
//                        mTempPointOfIntersection.add(mParticle.position());
//                        mParticle.position().set(mTempPointOfIntersection);
//                        /* reflect velocity i.e. change direction */
//                        separateComponents(mParticle, mNormal);
//                        if (Util.almost(mIntersectionResult.t, -1)) {
//                            /* particle is still */
//                            setVelocityAndOldPosition(mParticle, mTempReflectionVector);
//                            mParticle.still(true);
//                        } else {
//                            setVelocityAndOldPosition(mParticle, mTempReflectionVector);
//                        }
//                        mGotHit = true;
//                        markParticle(mParticle);
//                    }
//                }
//
//                /* reset boundingbox scale */
//                if (!IGNORE_BOUNDING_BOX) {
//                    mWorldAxisAlignedBoundingBox.scale.set(mTempBoundingBoxScale);
//                }
//            }
//        }
//    }

    public boolean dead() {
        return mDead;
    }

    public void dead(boolean pDead) {
        mDead = pDead;
    }

    public boolean active() {
        return mActive;
    }

    public void active(boolean pActiveState) {
        mActive = pActiveState;
    }

    public long ID() {
        return mID;
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

    public PVector normal() {
        return mNormal;
    }

    protected void markParticle(Particle pParticle) {
        pParticle.tag(true);
    }

//    private void separateComponents(Particle pParticle, PVector pNormal) {
//        /* normal */
//        mTempNormalComponent.set(pNormal);
//        mTempNormalComponent.mult(pNormal.dot(pParticle.velocity()));
//        /* tangent */
//        sub(pParticle.velocity(), mTempNormalComponent, mTempTangentComponent);
//        /* negate normal */
//        mTempNormalComponent.mult(-mCoefficientOfRestitution);
//        /* set reflection vector */
//        add(mTempTangentComponent, mTempNormalComponent, mTempReflectionVector);
//    }
}
