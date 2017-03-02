package teilchen.examples;

import processing.core.PApplet;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.force.Gravity;
import teilchen.force.TriangleDeflector;

public class SketchLessonX10_TriangleDeflector2D extends PApplet {

    private Physics mPhysics;

    private TriangleDeflector mTriangleDeflector;

    public void settings() {
        size(640, 480, P3D);
    }

    public void setup() {
        frameRate(60);

        /* physics */
        mPhysics = new Physics();
        Gravity myGravity = new Gravity(0, 20, 0);
        mPhysics.add(myGravity);

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

        final float mDeltaTime = 1.0f / frameRate;
        mPhysics.step(mDeltaTime);

        /* remove particles  */
        for (int i = 0; i < mPhysics.particles().size(); i++) {
            Particle mParticle = mPhysics.particles(i);
            if (mParticle.still()) {
                //                mPhysics.particles().remove(i);
            }
            if (mParticle.position().y > height) {
                mPhysics.particles().remove(i);
            }
        }

        /* draw particles */
        background(255);

        for (int i = 0; i < mPhysics.particles().size(); i++) {
            Particle mParticle = mPhysics.particles(i);
            stroke(0, 127);
            if (mParticle.tagged()) {
                fill(255, 127, 64);
            } else {
                fill(0, 32);
            }
            pushMatrix();
            translate(mParticle.position().x, mParticle.position().y);
            ellipse(0, 0, 10, 10);
            popMatrix();
        }

        /* draw deflectors */
        noFill();
        stroke(0);
        line(mTriangleDeflector.a().x, mTriangleDeflector.a().y, mTriangleDeflector.b().x, mTriangleDeflector.b().y);

        /* finally remove the collision tag */
        mPhysics.removeTags();
    }

    public void mousePressed() {
        /* create and add a particle to the system */
        Particle mParticle = mPhysics.makeParticle();
        /* set particle to mouse position with random velocity */
        mParticle.position().set(mouseX, mouseY);
        mParticle.velocity().set(random(-20, 20), 0);
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{SketchLessonX10_TriangleDeflector2D.class.getName()});
    }
}
