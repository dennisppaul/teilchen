package teilchen.force;

import processing.core.PVector;
import static processing.core.PVector.add;
import static processing.core.PVector.sub;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.constraint.IConstraint;
import teilchen.util.Intersection;
import teilchen.util.Intersection.IntersectionResult;
import static teilchen.util.Util.*;
import teilchen.util.WorldAxisAlignedBoundingBox;

public class TriangleDeflector
        implements IConstraint {

    private final PVector a;

    private final PVector b;

    private final PVector c;

    private final PVector mNormal;

    private float mCoefficientOfRestitution;

    private final PVector mTempReflectionVector;

    private final PVector mTempNormalComponent;

    private final PVector mTempTangentComponent;

    private final IntersectionResult mIntersectionResult;

    private final PVector mTempPointOfIntersection = new PVector();

    private final WorldAxisAlignedBoundingBox mWorldAxisAlignedBoundingBox;

    private final PVector[] mVectorCollection;

    public boolean AUTO_UPDATE = true;

    private boolean mGotHit = false;

    private boolean mActive;

    public TriangleDeflector() {
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
                final PVector myTempBoundingBoxScale = new PVector();
                myTempBoundingBoxScale.set(mWorldAxisAlignedBoundingBox.scale);
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
                if (IGNORE_BOUNDING_BOX || contains(myParticle.position(), mWorldAxisAlignedBoundingBox)) {
                    final PVector mRay;
                    final int RAY_FROM_VELOCITY = 0;
                    final int RAY_FROM_NORMAL = 1;
                    final int RAY_FROM_OLD_POSITION = 2;
                    final int CREATE_RAY_FROM = RAY_FROM_OLD_POSITION;

                    switch (CREATE_RAY_FROM) {
                        case RAY_FROM_VELOCITY:
                            mRay = myParticle.velocity();
                            break;
                        case RAY_FROM_NORMAL:
                            mRay = PVector.mult(mNormal, -myParticle.velocity().mag());
                            break;
                        case RAY_FROM_OLD_POSITION:
                            mRay = PVector.sub(myParticle.position(), myParticle.old_position());
                            break;
                        default:
                            mRay = new PVector(1, 0, 0);
                            break;
                    }

                    if (isNaN(mRay)) {
                        break;
                    }
                    if (lengthSquared(mRay) == 0) {
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
                        mTempPointOfIntersection.mult(mIntersectionResult.t);
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

    private void seperateComponents(Particle pParticle, PVector pNormal) {
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

    public PVector normal() {
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
