package teilchen.examples;

import processing.core.PApplet;
import processing.core.PVector;
import teilchen.MortalParticle;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.force.Gravity;
import teilchen.force.TriangleDeflector;
import teilchen.util.DrawLib;

import java.util.ArrayList;

public class SketchLessonX09_TriangleDeflector extends PApplet {

    /*
     * this sketch demonstrates how to use `TriangleDeflectors` to make particles bounce off two
     * triangles. it also demonstrates how to use `MortalParticle` to remove particles
     * automatically once they leave the screen.
     */

    private Physics mPhysics;
    private ArrayList<TriangleDeflector> mTriangleDeflectors;

    public void settings() {
        size(640, 480, P3D);
    }

    public void setup() {
        rectMode(CENTER);
        hint(DISABLE_DEPTH_TEST);

        /* physics */
        mPhysics = new Physics();
        Gravity myGravity = new Gravity(0, 0, -30);
        mPhysics.add(myGravity);

        /* triangle deflectors */
        final PVector[] mVertices = new PVector[]{new PVector(0, 0, 0),
                                                  new PVector(width, height, 0),
                                                  new PVector(0, height, 0),
                                                  new PVector(0, 0, 0),
                                                  new PVector(width, 0, 0),
                                                  new PVector(width, height, 0),};
        mTriangleDeflectors = teilchen.util.Util.createTriangleDeflectors(mVertices, 1.0f);
        mPhysics.addForces(mTriangleDeflectors);
    }

    public void draw() {
        if (mousePressed) {
            /* create and add a particle to the system */
            MyMortalParticle mParticle = new MyMortalParticle();
            mPhysics.add(mParticle);
            /* set particle to mouse position with random velocity */
            mParticle.position().set(mouseX, random(height), height / 2.0f);
            mParticle.velocity().set(random(-20, 20), 0, random(20));
        }

        final float mDeltaTime = 1.0f / frameRate;
        mPhysics.step(mDeltaTime);

        /* draw particles */
        background(255);
        camera(width / 2.0f, mouseY + height, height * 1.3f - mouseY, width / 2.0f, height / 2.0f, 0, 0, 1, 0);

        noStroke();
        sphereDetail(10);
        for (int i = 0; i < mPhysics.particles().size(); i++) {
            Particle mParticle = mPhysics.particles(i);
            if (mParticle.tagged()) {
                fill(255, 127, 64);
            } else {
                fill(0);
            }
            pushMatrix();
            translate(mParticle.position().x, mParticle.position().y, mParticle.position().z);
            sphere(5);
            popMatrix();
        }

        /* draw deflectors */
        noFill();
        for (TriangleDeflector mTriangleDeflector : mTriangleDeflectors) {
            DrawLib.draw(g, mTriangleDeflector, color(0), color(255, 0, 0), color(0, 255, 0));
        }

        /* finally remove the collision tag */
        mPhysics.removeTags();
    }

    private class MyMortalParticle extends MortalParticle {

        public boolean isDead() {
            return position().z < -height;
        }
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{SketchLessonX09_TriangleDeflector.class.getName()});
    }
}
