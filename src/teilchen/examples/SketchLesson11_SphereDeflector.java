package teilchen.examples;

import processing.core.PApplet;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.ShortLivedParticle;
import teilchen.force.Gravity;
import teilchen.force.SphereDeflector;
import teilchen.force.ViscousDrag;

import java.util.ArrayList;

public class SketchLesson11_SphereDeflector extends PApplet {

    /*
     * this sketch demonstrates how to create and use `SphereDeflector` and how to use
     * `ShortLivedParticle` a particle that only exists for a defined period of time.
     *
     * move mouse to spawn particles anywhere on the screen.
     */

    private final ArrayList<SphereDeflector> mDeflectors = new ArrayList<>();
    private Physics mPhysics;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        /* create a particle system */
        mPhysics = new Physics();

        for (int i = 0; i < 32; i++) {
            SphereDeflector d = new SphereDeflector();
            d.radius(random(20, 60));
            d.coefficientofrestitution(0.9f);
            d.position().set(random(width), random(height));
            mDeflectors.add(d);
            mPhysics.add(d);
        }

        /* create gravity */
        Gravity mGravity = new Gravity();
        mGravity.force().y = 50;
        mPhysics.add(mGravity);

        /* create drag */
        ViscousDrag mViscousDrag = new ViscousDrag();
        mViscousDrag.coefficient = 0.1f;
        mPhysics.add(mViscousDrag);
    }

    public void draw() {
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
            noStroke();
            fill(0, 127 * mRatio);
            circle(mParticle.position().x, mParticle.position().y, mParticle.radius() * 2);
            if (mParticle.tagged()) {
                noFill();
                stroke(0, 127 * mRatio);
                circle(mParticle.position().x, mParticle.position().y, mParticle.radius() * 3);
            }
        }

        /* draw deflector */
        for (SphereDeflector d : mDeflectors) {
            drawDeflector(d);
        }

        /* finally remove the collision tag */
        mPhysics.removeTags();
    }

    private void drawDeflector(SphereDeflector mDeflector) {
        noStroke();
        fill(0);
        circle(mDeflector.position().x, mDeflector.position().y, mDeflector.radius() * 2);
    }

    public static void main(String[] args) {
        PApplet.main(SketchLesson11_SphereDeflector.class.getName());
    }
}

