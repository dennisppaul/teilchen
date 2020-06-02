package teilchen.examples;

import processing.core.PApplet;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.ShortLivedParticle;
import teilchen.force.Gravity;
import teilchen.force.PlaneDeflector;
import teilchen.force.ViscousDrag;

public class SketchLesson10_PlaneDeflector extends PApplet {

    /*
     * this sketch demonstrates how to create and use ``PlaneDeflector` and how to use
     * `ShortLivedParticle` a particle that only exists for a defined period of time.
     *
     * drag mouse to tilt deflector.
     */

    private Physics mPhysics;
    private PlaneDeflector mDeflector;

    public void settings() {
        size(640, 480, P3D);
    }

    public void setup() {
        /* create a particle system */
        mPhysics = new Physics();

        /* create a deflector and add it to the particle system.
         * the that defines the deflection area is defined by an
         * origin and a normal. this also means that the plane s size
         * is infinite.
         * note that there is also a triangle deflector that is constraint
         * by three points.
         */
        mDeflector = new PlaneDeflector();
        /* set plane origin into the center of the screen */
        mDeflector.plane().origin.set(width / 2.0f, height / 2.0f, 0);
        mDeflector.plane().normal.set(0, -1, 0);
        /* the coefficient of restitution defines how hard particles bounce of the deflector */
        mDeflector.coefficientofrestitution(0.7f);
        mPhysics.add(mDeflector);

        /* create gravity */
        Gravity mGravity = new Gravity();
        mGravity.force().y = 50;
        mPhysics.add(mGravity);

        /* create drag */
        ViscousDrag myViscousDrag = new ViscousDrag();
        myViscousDrag.coefficient = 0.1f;
        mPhysics.add(myViscousDrag);
    }

    public void draw() {
        /* rotate deflector plane */
        if (mousePressed) {
            final float myAngle = 2 * PI * (float) mouseX / width - PI;
            mDeflector.plane().normal.set(sin(myAngle), -cos(myAngle), 0);
        }

        /* create a special particle */
        ShortLivedParticle mNewParticle = new ShortLivedParticle();
        mNewParticle.position().set(mouseX, mouseY);
        mNewParticle.velocity().set(0, random(100) + 50);
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
            /* this special particle can tell you how much time it has to live.
             * we map this information to its transparency.
             */
            float mRatio = 1 - ((ShortLivedParticle) mParticle).ageRatio();
            noStroke();
            if (mParticle.tagged()) {
                fill(255, 127, 0, 191 * mRatio);
            } else {
                fill(0, 127 * mRatio);
            }
            ellipse(mParticle.position().x, mParticle.position().y, 5, 5);
        }

        /* draw deflector */
        stroke(0, 127);
        line(mDeflector.plane().origin.x - mDeflector.plane().normal.y * -width,
             mDeflector.plane().origin.y + mDeflector.plane().normal.x * -width,
             mDeflector.plane().origin.x - mDeflector.plane().normal.y * width,
             mDeflector.plane().origin.y + mDeflector.plane().normal.x * width);

        stroke(255, 127, 0, 127);
        line(mDeflector.plane().origin.x,
             mDeflector.plane().origin.y,
             mDeflector.plane().origin.x + mDeflector.plane().normal.x * 20,
             mDeflector.plane().origin.y + mDeflector.plane().normal.y * 20);

        /* finally remove the collision tag */
        mPhysics.removeTags();
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{SketchLesson10_PlaneDeflector.class.getName()});
    }
}
