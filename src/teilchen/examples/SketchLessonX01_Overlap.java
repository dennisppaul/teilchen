package teilchen.examples;

import processing.core.PApplet;
import processing.core.PVector;
import teilchen.IParticle;
import teilchen.Physics;
import teilchen.force.Gravity;
import teilchen.force.Spring;
import teilchen.force.ViscousDrag;
import teilchen.util.Overlap;

public class SketchLessonX01_Overlap extends PApplet {

    /*
     * this sketch is exactly like `Lesson06_Springs` except that it also shows how to resolve
     * overlaps of particles by moving particles apart manipulating their position directly.
     *
     * press mouse to create new particles.
     */

    private static final float PARTICLE_RADIUS = 13;
    private Physics mPhysics;
    private IParticle mRoot;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        hint(ENABLE_DEPTH_SORT);
        mPhysics = new Physics();

        /* create drag */
        mPhysics.add(new ViscousDrag());
        mPhysics.add(new Gravity(new PVector(0, 100f, 0)));

        mRoot = mPhysics.makeParticle(width / 2.0f, height / 2.0f, 0.0f);
        mRoot.mass(30);
        mRoot.fixed(true);
        mRoot.radius(PARTICLE_RADIUS);
    }

    public void draw() {
        if (mousePressed) {
            IParticle mParticle = mPhysics.makeParticle(mouseX, mouseY, 0);
            mPhysics.makeSpring(mRoot, mParticle);

            /* define a radius for the particle so it has dimensions */
            mParticle.radius(random(PARTICLE_RADIUS / 2) + PARTICLE_RADIUS);
        }


        /* move overlapping particles away from each other */
        for (int i = 0; i < 10; i++) {
            mRoot.position().set(width / 2.0f, height / 2.0f, 0.0f); // a bit of a 'hack'
            Overlap.resolveOverlap(mPhysics.particles());
        }

        /* update the particle system */
        mPhysics.step(1.0f / frameRate);

        /* draw particles and connecting line */
        background(255);

        /* draw springs */
        noFill();
        stroke(0, 31);
        for (int i = 0; i < mPhysics.forces().size(); i++) {
            if (mPhysics.forces().get(i) instanceof Spring) {
                Spring mSSpring = (Spring) mPhysics.forces().get(i);
                line(mSSpring.a().position().x,
                     mSSpring.a().position().y,
                     mSSpring.b().position().x,
                     mSSpring.b().position().y);
            }
        }
        /* draw particles */
        fill(255);
        for (int i = 0; i < mPhysics.particles().size(); i++) {
            IParticle p = mPhysics.particles().get(i);
            stroke(0, 191);
            ellipse(p.position().x, p.position().y, p.radius() * 2, p.radius() * 2);
        }
    }

    public static void main(String[] args) {
        PApplet.main(SketchLessonX01_Overlap.class.getName());
    }
}
