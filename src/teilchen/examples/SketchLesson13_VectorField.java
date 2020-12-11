package teilchen.examples;

import processing.core.PApplet;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.force.VectorField;
import teilchen.force.ViscousDrag;

public class SketchLesson13_VectorField extends PApplet {

    /*
     * this sketch demonstrates how to use `VectorField`. a vector field is a set of regions
     * that apply a force to all particles with the regions.
     *
     * press `SPACE` to reset particles.
     * press `G` to toggle field view
     * press `L` to toggle line/point view
     */

    /* drawing particles as lines looks more intriguing but less explicatory */
    private boolean mDrawParticlesAsLines = false;
    private boolean mDrawGrid = false;
    private Physics mPhysics;
    private VectorField mVectorField;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        mPhysics = new Physics();

        mVectorField = new VectorField(56, 40);
        mVectorField.hint(VectorField.ENABLE_IGNORE_3D);
        mVectorField.cell_size().set(10, 10);
        mVectorField.position().set(40, 40);
        mPhysics.add(mVectorField);

        ViscousDrag mDrag = new ViscousDrag(0.75f);
        mPhysics.add(mDrag);

        mVectorField.randomize_forces(40);
        spawnParticles();
    }

    public void draw() {
        mVectorField.smooth_forces(true);
        mVectorField.set_force_strength(40);

        mPhysics.step(1.0f / frameRate);

        background(255);

        /* draw vectro field */
        noFill();
        stroke(0, 63);
        if (mDrawGrid) { VectorField.draw(g, mVectorField, 0.15f); }

        /* draw particles - as point or as lines */
        if (!mDrawParticlesAsLines) {
            /* draw border */
            noStroke();
            fill(0);
            rect(0, 0, width, 40);
            rect(0, height - 40, width, 40);
            rect(0, 40, 40, height - 80);
            rect(width - 40, 40, 40, height - 80);
            /* particles inside the vector field are colored black and outside white */
            for (Particle p : mPhysics.particles()) {
                if (mVectorField.inside(p.position())) {
                    stroke(0, 127);
                } else {
                    stroke(255, 127);
                }
                point(p.position().x, p.position().y);
            }
        } else {
            stroke(0, 12);
            beginShape(LINES);
            for (Particle p : mPhysics.particles()) {
                vertex(p.position().x, p.position().y);
            }
            endShape();
        }
    }

    public void keyPressed() {
        switch (key) {
            case 'l':
            case 'L':
                mDrawParticlesAsLines = !mDrawParticlesAsLines;
            case ' ':
                spawnParticles();
                mVectorField.randomize_forces(40);
                break;
            case 'g':
            case 'G':
                mDrawGrid = !mDrawGrid;
                break;
        }
    }

    private void spawnParticles() {
        mPhysics.particles().clear();
        final int mNumOfParticles = (int) random(4000, 40000);
        for (int i = 0; i < mNumOfParticles; i++) {
            final float mMass = random(0.5f, 2.0f);
            /* spawn particles - distributed on a grid or randomly */
            if (mDrawParticlesAsLines) {
                final float mStep = (width - 80) * (height - 80) / (float) mNumOfParticles;
                int mX = (int) (i * mStep) % (width - 80) + 40;
                int mY = (int) (i * mStep) / (width - 80) + 40;
                mPhysics.makeParticle(mX, mY).mass(mMass);
            } else {
                mPhysics.makeParticle(random(40, width - 40), random(40, height - 40)).mass(mMass);
            }
        }
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{SketchLesson13_VectorField.class.getName()});
    }
}

