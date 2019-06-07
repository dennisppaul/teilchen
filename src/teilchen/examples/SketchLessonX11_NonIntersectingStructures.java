package teilchen.examples;

import processing.core.PApplet;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.constraint.LineIntersectionConstraint;
import teilchen.constraint.Stick;
import teilchen.force.Gravity;
import teilchen.integration.Verlet;

import java.util.ArrayList;

public class SketchLessonX11_NonIntersectingStructures extends PApplet {

    private Physics mPhysics;

    private Particle[] mParticles;

    public void settings() {
        size(640, 480, P3D);
    }

    public void setup() {
        frameRate(60);
        smooth();

        mPhysics = new Physics();
        mPhysics.constrain_iterations_per_steps = 1;

        /* add gravity for extra fun */
        mPhysics.add(new Gravity());

        /* we chose verlet integration as it integrates much more nicely with sticks ( and constraints in general ) */
        Verlet myVerlet = new Verlet();
        myVerlet.damping(0.99f);
        mPhysics.setIntegratorRef(myVerlet);

        /* setup sticks to form mParticle whip */
        mParticles = new Particle[16];
        float mSegmentLength = 20.0f;

        /* create sticks */
        ArrayList<Stick> mSticks = new ArrayList<>();

        for (int i = 0; i < mParticles.length; i++) {
            mParticles[i] = mPhysics.makeParticle(i * mSegmentLength, 0, 0, 0.1f);
            if (i > 0) {
                Stick myStick = new Stick(mParticles[i - 1], mParticles[i], mSegmentLength);
                mSticks.add(myStick);
                /* damp stick to release tensions from system */
                myStick.damping(0.99f);
                mPhysics.add(myStick);
            }
        }

        /* create line intersection mechanism */
        for (Particle mParticle : mParticles) {
            LineIntersectionConstraint mLineIntersections = new LineIntersectionConstraint(mParticle);
            mLineIntersections.intersecting_lines().addAll(mSticks);
            mPhysics.add(mLineIntersections);
            mLineIntersections.DEBUG_VIEW = g;
        }

        /* fix root particle so it can stick to the mouse later */
        mParticles[0].fixed(true);
    }

    public void draw() {
        background(255);

        /* stick root particle to mouse */
        mParticles[0].position().set(mouseX, mouseY);

        /* update */
        mPhysics.step(1.0f / frameRate);

        /* draw sticks with descending stroke weight */
        stroke(0, 192);
        for (int i = 1; i < mParticles.length; i++) {
            Particle p1 = mParticles[i - 1];
            Particle p2 = mParticles[i];
            final float mStrokeWeight = 4.0f * (1.0f - (float) i / mParticles.length);
            strokeWeight(mStrokeWeight);
            line(p1.position().x, p1.position().y, p1.position().z, p2.position().x, p2.position().y, p2.position().z);
        }
    }

    public static void main(String[] args) {
        PApplet.main(SketchLessonX11_NonIntersectingStructures.class.getName());
    }
}
