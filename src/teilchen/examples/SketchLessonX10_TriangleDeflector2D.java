package teilchen.examples;

import processing.core.PApplet;
import teilchen.MortalParticle;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.force.Gravity;
import teilchen.constraint.TriangleDeflector;

public class SketchLessonX10_TriangleDeflector2D extends PApplet {

    /*
     * this sketch demonstrates how to use `TriangleDeflectors` in a 2D context to make particles
     * bounce off a triangle ( that looks like a line ). it also demonstrates how to use
     * `MortalParticle` to remove particles automatically once they leave the screen.
     *
     * press mouse to spawn particles.
     */

    private Physics mPhysics;
    private TriangleDeflector mTriangleDeflector;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        /* physics */
        mPhysics = new Physics();
        Gravity mGravity = new Gravity(0, 20, 0);
        mPhysics.add(mGravity);

        /* triangle deflector */
        final float mPadding = 50;
        mTriangleDeflector = teilchen.util.Util.createTriangleDeflector2D(mPadding,
                                                                          height - mPadding - 40,
                                                                          width - mPadding,
                                                                          height - mPadding + 40,
                                                                          1.0f);
        mPhysics.add(mTriangleDeflector);
    }

    public void draw() {
        mPhysics.step(1.0f / frameRate);

        /* draw particles */
        background(255);

        fill(0);
        noStroke();
        for (int i = 0; i < mPhysics.particles().size(); i++) {
            Particle mParticle = mPhysics.particles(i);
            final float mParticleSize;
            if (mParticle.tagged()) {
                mParticleSize = 15;
            } else {
                mParticleSize = 5;
            }
            circle(mParticle.position().x, mParticle.position().y, mParticleSize);
        }

        /* draw deflectors */
        noFill();
        stroke(0);
        if (mTriangleDeflector.hit()) {
            strokeWeight(1);
        } else {
            strokeWeight(3);
        }
        line(mTriangleDeflector.a().x, mTriangleDeflector.a().y, mTriangleDeflector.b().x, mTriangleDeflector.b().y);

        /* finally remove the collision tag */
        mPhysics.removeTags();

        /* create and add a particle to the system */
        if (mousePressed) {
            addParticle();
        }
    }

    private void addParticle() {
        MyMortalParticle mParticle = new MyMortalParticle();
        mPhysics.add(mParticle);
        /* set particle to mouse position with random velocity */
        mParticle.position().set(mouseX, mouseY);
        mParticle.velocity().set(random(-20, 20), 0);
    }

    private class MyMortalParticle extends MortalParticle {

        public boolean isDead() {
            return position().y > height || still();
        }
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{SketchLessonX10_TriangleDeflector2D.class.getName()});
    }
}
