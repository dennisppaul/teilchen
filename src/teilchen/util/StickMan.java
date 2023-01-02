package teilchen.util;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import teilchen.BasicParticle;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.force.MuscleSpring;

public class StickMan {

    private final BasicParticle mLeftFoot;
    private final BasicParticle mLeftHand;
    private final StableSpringQuad mQuad;
    private final BasicParticle mRightFoot;
    private final BasicParticle mRightHand;
    private final float mScale;

    public StickMan(Physics pParticleSystem, float pOffset, float pScale) {
        mScale = pScale;

        /* body */
        mQuad = new StableSpringQuad(pParticleSystem,
                                     new PVector(10 * pScale + pOffset, 50 * pScale),
                                     new PVector(40 * pScale + pOffset, 50 * pScale),
                                     new PVector(50 * pScale + pOffset, 0),
                                     new PVector(0 + pOffset, 0));
        /* legs */
        mLeftFoot = pParticleSystem.makeParticle(new PVector(10 * pScale + pOffset, 100 * pScale), 0.5f);
        pParticleSystem.makeSpring(mQuad.a, mLeftFoot, 100, 0.1f);
        pParticleSystem.makeSpring(mQuad.b, mLeftFoot, 100, 0.1f);
        pParticleSystem.makeSpring(mQuad.d, mLeftFoot, 2, 1f);

        mRightFoot = pParticleSystem.makeParticle(new PVector(40 * pScale + pOffset, 100 * pScale), 0.5f);
        pParticleSystem.makeSpring(mQuad.a, mRightFoot, 100, 0.1f);
        pParticleSystem.makeSpring(mQuad.b, mRightFoot, 100, 0.1f);
        pParticleSystem.makeSpring(mQuad.c, mRightFoot, 2, 1f);

        /* arms */
        mRightHand = pParticleSystem.makeParticle(new PVector(70 * pScale + pOffset, 0), 0.1f);
        pParticleSystem.makeSpring(mQuad.c, mRightHand, 100, 0.1f);
        pParticleSystem.makeSpring(mQuad.b, mRightHand, 10, 0.1f);

        mLeftHand = pParticleSystem.makeParticle(new PVector(-20 * pScale + pOffset, 0), 0.1f);
        pParticleSystem.makeSpring(mQuad.d, mLeftHand, 100, 0.1f);
        pParticleSystem.makeSpring(mQuad.a, mLeftHand, 10, 0.1f);

        pParticleSystem.makeSpring(mLeftHand, mRightHand, 10, 0.1f);

        mLeftFoot.radius(7 * pScale);
        mRightFoot.radius(7 * pScale);
        mLeftHand.radius(20 * pScale);
        mRightHand.radius(20 * pScale);
        mQuad.a.radius(7 * pScale);
        mQuad.a.radius(7 * pScale);
        mQuad.a.radius(7 * pScale);
        mQuad.a.radius(7 * pScale);

        final float mMass = 2f;
        mLeftFoot.mass(mMass * pScale);
        mRightFoot.mass(mMass * pScale);
        mLeftHand.mass(mMass * pScale);
        mRightHand.mass(mMass * pScale);
        mQuad.a.mass(mMass * pScale);
        mQuad.b.mass(mMass * pScale);
        mQuad.c.mass(mMass * pScale);
        mQuad.d.mass(mMass * pScale);

        /* make legs move */
        MuscleSpring mMuscleSpring = new MuscleSpring(mLeftFoot, mRightFoot);
        mMuscleSpring.amplitude(20 * pScale);
        mMuscleSpring.strength(100);
        mMuscleSpring.phaseshift((float) Math.random() * 2 * PApplet.PI);
        mMuscleSpring.frequency(1);
        pParticleSystem.add(mMuscleSpring);
    }

    public void translate(PVector p) {
        mLeftFoot.position().add(p);
        mRightFoot.position().add(p);
        mLeftHand.position().add(p);
        mRightHand.position().add(p);
        mQuad.a.position().add(p);
        mQuad.b.position().add(p);
        mQuad.c.position().add(p);
        mQuad.d.position().add(p);
    }

    public void draw(PGraphics g) {
        /* draw arms */
        g.line(mRightHand.position().x, mRightHand.position().y, mQuad.c.position().x, mQuad.c.position().y);
        g.line(mLeftHand.position().x, mLeftHand.position().y, mQuad.d.position().x, mQuad.d.position().y);

        /* draw legs */
        g.line(mRightFoot.position().x, mRightFoot.position().y, mQuad.b.position().x, mQuad.b.position().y);
        g.line(mLeftFoot.position().x, mLeftFoot.position().y, mQuad.a.position().x, mQuad.a.position().y);

        /* draw body */
        g.line(mQuad.a.position().x, mQuad.a.position().y, mQuad.b.position().x, mQuad.b.position().y);
        g.line(mQuad.b.position().x, mQuad.b.position().y, mQuad.c.position().x, mQuad.c.position().y);
        g.line(mQuad.c.position().x, mQuad.c.position().y, mQuad.d.position().x, mQuad.d.position().y);
        g.line(mQuad.d.position().x, mQuad.d.position().y, mQuad.a.position().x, mQuad.a.position().y);

        /* draw head */
        g.ellipse((mQuad.c.position().x + mQuad.d.position().x) * 0.5f,
                  (mQuad.c.position().y + mQuad.d.position().y) * 0.5f,
                  40 * mScale,
                  40 * mScale);
    }

    public Particle anchor() {
        return mQuad.a;
    }
}
