package teilchen.force;

import processing.core.PGraphics;
import processing.core.PVector;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.util.Util;

public class LineDeflector2D implements IForce {

    private PVector a = new PVector();
    private PVector b = new PVector();
    private boolean mActive = true;
    private float mCoefficientOfRestitution = 1.0f;
    private boolean mDead = false;

    public PVector normal() {
        PVector mNormal = PVector.sub(b, a);
        mNormal.normalize();
        /* proper 3D version */
        //            mNormal = mNormal.cross(new PVector(0, 0, 1));
        /* 2D optimized version */
        float y = -mNormal.x;
        mNormal.x = mNormal.y;
        mNormal.y = y;
        return mNormal;
    }

    public PVector mid() {
        return PVector.sub(b, a).mult(0.5f).add(a);
    }

    public void calculateIntersection(Particle pParticle, float pDeltaTime) {
        /* calc intersection from velocity */
        //            PVector mForward = PVector.mult(pParticle.velocity(), pDeltaTime);
        //            mForward.add(PVector.mult(pParticle.velocity(), pParticle.radius() / pParticle.velocity().mag()));
        //            PVector mFuturePosition = PVector.add(pParticle.position(), mForward);
        //            PVector mPointOfIntersection = new PVector();
        //            int mIntersectionResult = Intersection.lineLineIntersect(pParticle.position(), mFuturePosition,
        //            a, b,
        // mPointOfIntersection);
        //            if (mIntersectionResult == Intersection.INTERESECTING) {
        //                PVector mReflection = Util.calculateReflectionVector(pParticle, normal());
        //                pParticle.velocity().set(mReflection);
        //                pParticle.tag(true);
        //            }

        /* calc intersection by closest distance to line */
        PVector mProjectedPointOnLine = Util.projectPointOnLineSegment(a, b, pParticle.position());
        float mDistanceToLine = PVector.sub(mProjectedPointOnLine, pParticle.position()).mag();
        if (mDistanceToLine < pParticle.radius()) {
            /* resolve collision */
            PVector mPosition = PVector.sub(pParticle.position(), mProjectedPointOnLine);
            mPosition.mult(pParticle.radius() / mPosition.mag());
            mPosition.add(mProjectedPointOnLine);
            pParticle.position().set(mPosition);
            /* reflect velocity */
            PVector mReflection = Util.calculateReflectionVector(pParticle, normal()).mult(mCoefficientOfRestitution);
            pParticle.velocity().set(mReflection);
            pParticle.tag(true);
        }
    }

    public void draw(PGraphics g) {
        PVector mMid = mid();
        PVector mNormal = PVector.add(mMid, PVector.mult(normal(), 10));
        g.stroke(255, 0, 0);
        g.line(mMid.x, mMid.y, mNormal.x, mNormal.y);
        g.stroke(0);
        g.line(a.x, a.y, b.x, b.y);
    }

    @Override
    public void apply(float pDeltaTime, Physics pParticleSystem) {
        for (final Particle mParticle : pParticleSystem.particles()) {
            if (!mParticle.fixed()) {
                calculateIntersection(mParticle, pDeltaTime);
            }
        }
    }

    @Override
    public boolean dead() {
        return mDead;
    }

    @Override
    public void dead(boolean pDead) {
        mDead = pDead;
    }

    @Override
    public boolean active() {
        return mActive;
    }

    @Override
    public void active(boolean pActiveState) {
        mActive = pActiveState;
    }

    public void coefficientofrestitution(float pCoefficientOfRestitution) {
        mCoefficientOfRestitution = pCoefficientOfRestitution;
    }

    public PVector a() {
        return a;
    }

    public PVector b() {
        return b;
    }
}