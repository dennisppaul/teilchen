package teilchen.examples;

import processing.core.PApplet;
import processing.core.PVector;
import teilchen.MortalParticle;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.force.Gravity;
import teilchen.force.TriangleDeflector;

import java.util.ArrayList;

public class SketchLessonX09_TriangleDeflector extends PApplet {

    /*
     * this sketch demonstrates how to use `TriangleDeflectors` to make particles bounce off two
     * triangles. it also demonstrates how to use `MortalParticle` to remove particles
     * automatically once they leave the screen.
     *
     * press mouse to create particles. move mouse to rotate view.
     */

    public void settings() {
        size(640, 480, P3D);
    }

    public void setup() {
        /* physics */
        mPhysics = new Physics();
        Gravity mGravity = new Gravity(0, -3, -30);
        mPhysics.add(mGravity);

        /* triangle deflectors */
        final PVector[] mVertices = new PVector[]{new PVector(0, 0, 0), new PVector(width, height, 0),
                                                  new PVector(0, height, 0), new PVector(0, 0, 0),
                                                  new PVector(width, 0, 0), new PVector(width, height, 0),};
        mTriangleDeflectors = teilchen.util.Util.createTriangleDeflectors(mVertices, 1.0f);
        mPhysics.addForces(mTriangleDeflectors);
    }

    public void draw() {
        if (mousePressed) {
            spawnParticle();
        }

        final float mDeltaTime = 1.0f / frameRate;
        mPhysics.step(mDeltaTime);

        /* draw particles */
        background(255);
        camera(2 * mouseX - width / 2.0f,
               mouseY + height,
               height * 1.3f - mouseY,
               width / 2.0f,
               height / 2.0f,
               0,
               0,
               1,
               0);

        noStroke();
        sphereDetail(10);
        for (int i = 0; i < mPhysics.particles().size(); i++) {
            Particle mParticle = mPhysics.particles(i);
            pushMatrix();
            translate(mParticle.position().x, mParticle.position().y, mParticle.position().z);
            fill(0);
            if (mParticle.tagged()) {
                sphere(10);
            } else {
                sphere(5);
            }
            popMatrix();
        }

        /* draw deflectors */
        for (TriangleDeflector t : mTriangleDeflectors) {
            if (t.hit()) {
                fill(0);
                stroke(255);
            } else {
                fill(255);
                stroke(0);
            }
            beginShape();
            vertex(t.a());
            vertex(t.b());
            vertex(t.c());
            endShape(CLOSE);
        }

        /* finally remove the collision tag */
        mPhysics.removeTags();
    }

    private void spawnParticle() {
        /* create and add a particle to the system */
        MyMortalParticle mParticle = new MyMortalParticle();
        mPhysics.add(mParticle);
        /* set particle to mouse position with random velocity */
        mParticle.position().set(random(width), random(height), height / 2.0f);
        mParticle.velocity().set(random(-20, 20), 0, random(20));
    }

    private void vertex(PVector a) {
        vertex(a.x, a.y, a.z);
    }

    private class MyMortalParticle extends MortalParticle {

        public boolean isDead() {
            return position().z < -height;
        }
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{SketchLessonX09_TriangleDeflector.class.getName()});
    }
    private Physics mPhysics;
    private ArrayList<TriangleDeflector> mTriangleDeflectors;
}
