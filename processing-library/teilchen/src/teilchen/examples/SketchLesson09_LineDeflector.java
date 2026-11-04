package teilchen.examples;

import processing.core.PApplet;
import processing.core.PVector;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.ShortLivedParticle;
import teilchen.constraint.LineCollider2D;
import teilchen.force.Gravity;
import teilchen.force.ViscousDrag;

public class SketchLesson09_LineDeflector extends PApplet {

    /*
     * this sketch demonstrates how to create and use `LineCollider2D` and how to use
     * `ShortLivedParticle` a particle that only exists for a defined period of time.
     *
     * press mouse to position collider.
     */

    private LineCollider2D mDeflector;
    private Physics mPhysics;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        /* create a particle system */
        mPhysics = new Physics();

        /* colliders are constraints. they keep particles from passing through geometry */
        mDeflector = new LineCollider2D();
        mDeflector.a().set(50, height / 2.0f);
        mDeflector.b().set(width - 50, height / 2.0f - 100);
        mPhysics.add(mDeflector);

        /* create gravity */
        Gravity mGravity = new Gravity();
        mGravity.force().y = 175;
        mPhysics.add(mGravity);

        /* create drag */
        ViscousDrag mViscousDrag = new ViscousDrag();
        mViscousDrag.coefficient = 0.2f;
        mPhysics.add(mViscousDrag);
    }

    public void draw() {
        /* rotate deflector plane */
        if (mousePressed) {
            mDeflector.a().set(mouseX, mouseY);
        }

        /* create a special particle */
        ShortLivedParticle mNewParticle = new ShortLivedParticle();
        mNewParticle.position().set(mouseX, mouseY);
        mNewParticle.velocity().set(0, random(100) + 50);
        mNewParticle.radius(5);
        /* this particle is removed after a specific interval */
        mNewParticle.setMaxAge(4);
        /* add particle manually to the particle system */
        mPhysics.add(mNewParticle);

        /* update physics */
        final float mDeltaTime = 1.0f / frameRate;
        mPhysics.step(mDeltaTime);

        /* draw all the particles in the particle system */
        background(255);
        for (int i = 0; i < mPhysics.particles().size(); i++) {
            Particle mParticle = mPhysics.particles(i);
            /* this special particle has a limited lifetime. in this case this information is
            mapped to its transparency. */
            float mRatio = 1 - ((ShortLivedParticle) mParticle).ageRatio();
            fill(0, 127 * mRatio);
            noStroke();
            float mDiameter;
            if (mParticle.tagged()) {
                mDiameter = mNewParticle.radius() * 2;
            } else {
                mDiameter = mNewParticle.radius();
            }
            ellipse(mParticle.position().x, mParticle.position().y, mDiameter, mDiameter);
        }

        /* draw deflector */
        drawDeflector(mDeflector);

        /* finally remove the collision tag */
        mPhysics.removeTags();
    }

    private void drawDeflector(LineCollider2D mDeflector) {
        PVector mMid = PVector.add(mDeflector.a(), mDeflector.b()).mult(0.5f);
        PVector mNormal = PVector.add(mMid, PVector.mult(mDeflector.normal(), 10));
        stroke(0);
        strokeWeight(3);
        line(mDeflector.a().x, mDeflector.a().y, mDeflector.b().x, mDeflector.b().y);
        strokeWeight(1);
        line(mMid.x, mMid.y, mNormal.x, mNormal.y);
    }

    public static void main(String[] args) {
        PApplet.main(SketchLesson09_LineDeflector.class.getName());
    }
}
