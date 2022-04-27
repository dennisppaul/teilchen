package teilchen.constraint;

import processing.core.PVector;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.util.Intersection;
import teilchen.util.Intersection.IntersectionResult;
import teilchen.util.Util;
import teilchen.util.WorldAxisAlignedBoundingBox;

import static processing.core.PVector.add;
import static processing.core.PVector.sub;
import static teilchen.util.Intersection.intersectLinePlane;
import static teilchen.util.Intersection.intersectRayTriangle;
import static teilchen.util.Util.calculateNormal;
import static teilchen.util.Util.contains;
import static teilchen.util.Util.isNaN;
import static teilchen.util.Util.lengthSquared;
import static teilchen.util.Util.setVelocityAndOldPosition;
import static teilchen.util.Util.updateBoundingBox;

public class TriangleDeflector implements IConstraint {
    //public class TriangleDeflector implements IForce {

    public boolean AUTO_UPDATE = true;
    private final PVector a;
    private final PVector b;
    private final PVector c;
    private final PVector mNormal;
    private final PVector mTempReflectionVector;
    private final PVector mTempNormalComponent;
    private final PVector mTempTangentComponent;
    private final IntersectionResult mIntersectionResult;
    private final WorldAxisAlignedBoundingBox mWorldAxisAlignedBoundingBox;
    private final PVector[] mVectorCollection;
    private final long mID;

    private float mCoefficientOfRestitution;
    private boolean mGotHit = false;
    private boolean mActive;
    private boolean mDead;

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
        mIntersectionResult = new IntersectionResult();
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

    public void apply__clean__(final float pDeltaTime, final Physics pParticleSystem) {
        if (AUTO_UPDATE) {
            updateProperties();
        }

        mGotHit = false;
        for (final Particle mParticle : pParticleSystem.particles()) {
            if (!mParticle.fixed()) {
                final PVector mRay = PVector.sub(mParticle.position(), mParticle.old_position());
                final PVector mOldPosition = PVector.sub(mParticle.position(), mRay);
                /* ray-normal relation */
//                final PVector mRayNormalized = new PVector().set(mRay).normalize();
//                final float mAngleRayNormal = PVector.dot(mRayNormalized, mNormal);
//                if (mAngleRayNormal == 1.0f || mAngleRayNormal == -1.0f) {
//                    System.out.println("paralell");
//                } else if (mAngleRayNormal == 0.0f) {
//                    System.out.println("perpendicular or mag==0");
//                } else if (mAngleRayNormal > 0.0f) {
//                    System.out.println("away");
//                } else if (mAngleRayNormal < 0.0f) {
//                    System.out.println("towards");
//                }

                /* particle-triangle relation */
                final float mPointSidePlane = point_side_of_plane(mParticle.position(), a, b, c);
                final float mOldPointSidePlane = point_side_of_plane(mOldPosition, a, b, c);

                if ((mOldPointSidePlane > 0.0f && mPointSidePlane > 0.0f) ||
                    (mOldPointSidePlane < 0.0f && mPointSidePlane < 0.0f)) {
                    /* particle was and is either above or below plane */
                } else if (mOldPointSidePlane == 0.0f && mPointSidePlane == 0.0f) {
                    /* particle was and is on plane */
                } else {
                    final PVector mIntersection = intersectLinePlane(a,
                                                                     mNormal,
                                                                     mParticle.position(),
                                                                     mRay);
                    if (mIntersection != null) {
                        if (Util.isPointInsideTriangle3(a, b, c, mIntersection)) {
                            final float mIntersectLength = PVector.sub(mIntersection, mParticle.position()).mag();
                            final float mRayLength = mRay.mag();
                            /* reflect velocity */
                            final PVector mReflectedVelocity = reflectVelocity(mParticle.velocity(),
                                                                               mNormal,
                                                                               mCoefficientOfRestitution);
                            mParticle.velocity().set(mReflectedVelocity);
                            /* fix position */
                            final float mPlaneFractionBeyond = mRayLength - mIntersectLength;
                            PVector mFractionBeyond = new PVector().set(mParticle.velocity()).setMag(
                            mPlaneFractionBeyond);
                            mParticle.position().set(mIntersection);
                            mParticle.position().add(mFractionBeyond);
                        }
                    }
                }
            }
        }
    }

    private void separateComponents(Particle pParticle, PVector pNormal) {
        /* normal */
        mTempNormalComponent.set(pNormal);
        mTempNormalComponent.mult(pNormal.dot(pParticle.velocity()));
        /* tangent */
        sub(pParticle.velocity(), mTempNormalComponent, mTempTangentComponent);
        /* negate normal */
        mTempNormalComponent.mult(-mCoefficientOfRestitution);
        /* set reflection vector */
        add(mTempTangentComponent, mTempNormalComponent, mTempReflectionVector);
    }

    private static float point_side_of_plane(PVector p, PVector p0, PVector p1, PVector p2) {
        // from https://www.geeksforgeeks.org/program-to-find-equation-of-a-plane-passing-through-3-points/
        float a1 = p1.x - p0.x;
        float b1 = p1.y - p0.y;
        float c1 = p1.z - p0.z;
        float a2 = p2.x - p0.x;
        float b2 = p2.y - p0.y;
        float c2 = p2.z - p0.z;
        float a = b1 * c2 - b2 * c1;
        float b = a2 * c1 - a1 * c2;
        float c = a1 * b2 - b1 * a2;
        float d = (-a * p0.x - b * p0.y - c * p0.z);
        return a * p.x + b * p.y + c * p.z + d;
    }

    private static float distance_plane_point(PVector p, PVector p0, PVector p1, PVector p2) {
        // from https://www.geeksforgeeks.org/program-to-find-equation-of-a-plane-passing-through-3-points/
        float a1 = p1.x - p0.x;
        float b1 = p1.y - p0.y;
        float c1 = p1.z - p0.z;
        float a2 = p2.x - p0.x;
        float b2 = p2.y - p0.y;
        float c2 = p2.z - p0.z;
        float a = b1 * c2 - b2 * c1;
        float b = a2 * c1 - a1 * c2;
        float c = a1 * b2 - b1 * a2;
        float d = (-a * p0.x - b * p0.y - c * p0.z);
        // from https://www.geeksforgeeks.org/distance-between-a-point-and-a-plane-in-3-d/
        d = Math.abs(point_side_of_plane(p, p0, p1, p2));
        float e = (float) Math.sqrt(a * a + b * b + c * c);
        return d / e;
    }

    private static PVector reflectVelocity(PVector pVelocity, PVector pNormal, float pCoefficientOfRestitution) {
        /* normal */
        final PVector mTempNormalComponent = new PVector().set(pNormal);
        mTempNormalComponent.mult(pNormal.dot(pVelocity));
        /* tangent */
        final PVector mTempTangentComponent = sub(pVelocity, mTempNormalComponent);
        /* negate normal */
        mTempNormalComponent.mult(-pCoefficientOfRestitution);
        /* reflection vector */
        return PVector.add(mTempTangentComponent, mTempNormalComponent);
    }

    private void __apply(final float pDeltaTime, final Physics pParticleSystem) {
        /* TODO update triangle properties -- maybe this is better not done automatically */
        if (AUTO_UPDATE) {
            updateProperties();
        }

        mGotHit = false;
        for (final Particle mParticle : pParticleSystem.particles()) {
            if (!mParticle.fixed()) {
                final boolean IGNORE_CULLING = true;
                final boolean IGNORE_BOUNDING_BOX = true;
                final boolean DERIVE_RAY_FROM_OLD_POSITION = true;

                /* only test if in bounding box */
                if (IGNORE_BOUNDING_BOX || contains(mParticle.position(), mWorldAxisAlignedBoundingBox)) {
                    final PVector mRay;
                    if (DERIVE_RAY_FROM_OLD_POSITION) {
                        mRay = PVector.sub(mParticle.position(), mParticle.old_position());
                    } else {
                        mRay = new PVector().set(mParticle.velocity()).mult(pDeltaTime);
                    }

                    if (isNaN(mRay)) {
                        continue;
                    }

                    // TODO maybe also test if ray is parallel to triangle plane?
                    if (Util.isPointInsideTriangle3(a, b, c, mParticle.position())) {
                        System.out.println("is inside triangle");
                        markParticle(mParticle);
                    }

                    if (PVector.dot(mNormal, mRay) == 0.0f) {
                        System.out.println("paralell");
                    }

                    if (lengthSquared(mRay) == 0) {
                        mParticle.still(true);
                        continue;
                    }

                    final PVector mPointOfIntersection = new PVector();
                    final boolean mSuccess = intersectRayTriangle(mParticle.position(), mRay, a, b, c,
                                                                  mPointOfIntersection);
                    System.out.println();
                    {
                        final PVector mCenterOfMass = PVector.add(PVector.add(a(), b()), c()).mult(1.0f / 3.0f);
                        final PVector mTempPositionOld = PVector.sub(mCenterOfMass,
                                                                     mParticle.old_position()).normalize();
                        final PVector mTempPositionNew = PVector.sub(mCenterOfMass, mParticle.position()).normalize();
                        if (PVector.dot(mTempPositionOld, mNormal) > 0.0f) {
                            System.out.print("*"); // passed triangle
                        }
                        if (PVector.dot(mTempPositionNew, mNormal) > 0.0f) {
                            System.out.print("•"); // passed triangle
                        }
                    }

                    if (mSuccess) {
                        System.out.print("1");
                        // TODO do length values need to be abs'd?
                        final float mIntersectLength = PVector.sub(mPointOfIntersection, mParticle.position()).mag();
                        final float mRayLength = mRay.mag();
                        /* test if particle is passed triangle */
                        if (mRayLength > mIntersectLength) {
                            System.out.print("2");
                            final PVector mTempRay = PVector.sub(a(), mParticle.old_position()).normalize();
//                            final PVector mTempRay = new PVector().set(mRay).normalize();
                            float mDot = PVector.dot(mTempRay, mNormal);
                            final boolean mForwardFacing = mDot < 0.0f || IGNORE_CULLING;
                            if (mForwardFacing) {
                                System.out.print("3");
                                mParticle.old_position().set(mParticle.position());
                                mParticle.position().set(mPointOfIntersection);
                                separateComponents(mParticle, mNormal);
                                mParticle.velocity().set(mTempReflectionVector);
                                /* move particle by fraction, particle moved beyond plane */
                                final float mBeyondPlaneFraction = mRayLength - mIntersectLength;
                                PVector mFraction = new PVector().set(mParticle.velocity()).setMag(
                                mBeyondPlaneFraction);
                                mParticle.position().add(mFraction);
                                System.out.println(mParticle.position() + " / " + mParticle.old_position());
                                markParticle(mParticle);
                                mGotHit = true;
                            }
                        }
                    } else {
                        System.out.print("X");
                    }

                    {
                        final PVector mCenterOfMass = PVector.add(PVector.add(a(), b()), c()).mult(1.0f / 3.0f);
                        final PVector mTempPositionOld = PVector.sub(mCenterOfMass,
                                                                     mParticle.old_position()).normalize();
                        final PVector mTempPositionNew = PVector.sub(mCenterOfMass, mParticle.position()).normalize();
                        float mDot = PVector.dot(mTempPositionOld, mNormal);
                        if (mDot > 0.0f) {
                            System.out.print("+"); // passed triangle
                        }
                    }
                }
            }
        }
    }

    public void apply(final float pDeltaTime, final Physics pParticleSystem) {
        /* update triangle properties -- maybe this is better not done automatically */
        if (AUTO_UPDATE) {
            updateProperties();
        }

        mGotHit = false;
        for (final Particle mParticle : pParticleSystem.particles()) {
            if (!mParticle.fixed()) {

                final boolean IGNORE_BOUNDING_BOX = true;
                /* adjust boundingbox width to particle velocity to avoid particle shooting through the boundingbox */
                final PVector mTempBoundingBoxScale = new PVector();
                mTempBoundingBoxScale.set(mWorldAxisAlignedBoundingBox.scale);
                if (!IGNORE_BOUNDING_BOX) {
                    if (mParticle.velocity().x > mWorldAxisAlignedBoundingBox.scale.x) {
                        mWorldAxisAlignedBoundingBox.scale.x = mParticle.velocity().x;
                    }
                    if (mParticle.velocity().y > mWorldAxisAlignedBoundingBox.scale.y) {
                        mWorldAxisAlignedBoundingBox.scale.y = mParticle.velocity().y;
                    }
                    if (mParticle.velocity().z > mWorldAxisAlignedBoundingBox.scale.z) {
                        mWorldAxisAlignedBoundingBox.scale.z = mParticle.velocity().z;
                    }
                }

                /* only test if in bounding box */
                if (IGNORE_BOUNDING_BOX || contains(mParticle.position(), mWorldAxisAlignedBoundingBox)) {
                    final PVector mRay;
                    final int RAY_FROM_VELOCITY = 0;
                    final int RAY_FROM_NORMAL = 1;
                    final int RAY_FROM_OLD_POSITION = 2;
                    final int CREATE_RAY_FROM = RAY_FROM_OLD_POSITION;

                    switch (CREATE_RAY_FROM) {
                        case RAY_FROM_VELOCITY:
                            // todo why does this not work?
                            mRay = new PVector().set(mParticle.velocity());
                            break;
                        case RAY_FROM_NORMAL:
                            mRay = PVector.mult(mNormal, -mParticle.velocity().mag());
                            break;
                        case RAY_FROM_OLD_POSITION:
                            mRay = PVector.sub(mParticle.position(), mParticle.old_position());
                            break;
                        default:
                            mRay = new PVector(1, 0, 0);
                            break;
                    }

                    if (isNaN(mRay)) {
                        continue;
                    }
                    if (lengthSquared(mRay) == 0) {
                        continue;
                    }
                    mIntersectionResult.clear();
                    final boolean mSuccess = Intersection.intersectRayTriangle(mParticle.position(),
                                                                               mRay,
                                                                               a,
                                                                               b,
                                                                               c,
                                                                               mIntersectionResult,
                                                                               true);

                    /* is particle passed plane. */
                    if (mSuccess && mIntersectionResult.t <= 0) {
                        final PVector mTempPointOfIntersection = new PVector();
                        mTempPointOfIntersection.set(mRay);
                        mTempPointOfIntersection.mult(mIntersectionResult.t);
                        mTempPointOfIntersection.add(mParticle.position());
                        mParticle.position().set(mTempPointOfIntersection);
                        /* reflect velocity i.e. change direction */
                        separateComponents(mParticle, mNormal);
                        if (Util.almost(mIntersectionResult.t, -1)) {
                            /* particle is still */
                            setVelocityAndOldPosition(mParticle, mTempReflectionVector);
                            mParticle.still(true);
                        } else {
                            setVelocityAndOldPosition(mParticle, mTempReflectionVector);
                        }
                        mGotHit = true;
                        markParticle(mParticle);
                    }
                }

                /* reset boundingbox scale */
                if (!IGNORE_BOUNDING_BOX) {
                    mWorldAxisAlignedBoundingBox.scale.set(mTempBoundingBoxScale);
                }
            }
        }
    }

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
}
