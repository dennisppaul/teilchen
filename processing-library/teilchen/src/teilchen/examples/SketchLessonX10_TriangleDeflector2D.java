package teilchen.examples;

import processing.core.PApplet;
import teilchen.MortalParticle;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.constraint.TriangleMeshCollider;
import teilchen.force.Gravity;

public class SketchLessonX10_TriangleDeflector2D extends PApplet {

    /*
     * this sketch demonstrates how to use `TriangleMeshCollider` in a 2D context to make particles
     * bounce off a wall made from two triangles ( that looks like a line ). note that for 2D
     * walls `LineCollider2D` is simpler. it also demonstrates how to use
     * `MortalParticle` to remove particles automatically once they leave the screen.
     *
     * press mouse to spawn particles.
     */

    private Physics mPhysics;
    private TriangleMeshCollider mTriangleDeflector;
    private float mX1, mY1, mX2, mY2;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        /* physics */
        mPhysics = new Physics();
        Gravity mGravity = new Gravity(0, 70, 0);
        mPhysics.add(mGravity);

        /* triangle deflector */
        final float mPadding = 50;
        mX1 = mPadding;
        mY1 = height - mPadding - 40;
        mX2 = width - mPadding;
        mY2 = height - mPadding + 40;
        /* wall is a quad perpendicular to the XY plane, made from two triangles ( 9 floats each ) */
        final float z = 10;
        mTriangleDeflector = new TriangleMeshCollider(new float[]{mX1, mY1, -z, mX2, mY2, -z, mX2, mY2, z,
                                                                  mX1, mY1, -z, mX2, mY2, z, mX1, mY1, z});
        mPhysics.add(mTriangleDeflector);
    }

    public void draw() {
        mPhysics.step(1.0f / frameRate);

        /* draw particles */
        background(255);

        fill(0);
        strokeWeight(3);
        for (int i = 0; i < mPhysics.particles().size(); i++) {
            Particle mParticle = mPhysics.particles(i);
            if (mParticle.tagged()) {
                stroke(0);
            } else {
                noStroke();
            }
            ellipse(mParticle.position().x, mParticle.position().y, 5, 5);
        }

        /* draw deflectors */
        noFill();
        stroke(0);
        strokeWeight(3);
        line(mX1, mY1, mX2, mY2);

        /* finally remove the collision tag */
        mPhysics.removeTags();

        /* create and add a particle to the system */
        if (mousePressed) {
            MyMortalParticle mParticle = new MyMortalParticle();
            mPhysics.add(mParticle);
            /* set particle to mouse position with random velocity */
            mParticle.position().set(mouseX, mouseY);
            mParticle.velocity().set(random(-20, 20), 0);
        }
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
