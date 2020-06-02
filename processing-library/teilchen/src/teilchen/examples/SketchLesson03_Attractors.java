package teilchen.examples;

import processing.core.PApplet;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.constraint.Teleporter;
import teilchen.force.Attractor;
import teilchen.force.ViscousDrag;

public class SketchLesson03_Attractors extends PApplet {

    /*
     * this sketch demonstrates how to create and use an `Attractor` and how to teleport particles.
     *
     * press mouse to toggle attractor between postive and *negative* attraction.
     */

    private Physics mPhysics;
    private Attractor mAttractor;

    public void settings() {
        size(640, 480, P3D);
    }

    public void setup() {
        /* create a particle system */
        mPhysics = new Physics();

        /* create a viscous force that slows down all motion */
        ViscousDrag myDrag = new ViscousDrag();
        myDrag.coefficient = 0.75f;
        mPhysics.add(myDrag);

        /* teleport particles from one edge of the screen to the other */
        Teleporter mTeleporter = new Teleporter();
        mTeleporter.min().set(0, 0);
        mTeleporter.max().set(width, height);
        mPhysics.add(mTeleporter);

        /* create some particles */
        for (int i = 0; i < 1000; i++) {
            Particle mParticle = mPhysics.makeParticle();
            mParticle.position().set(random(width), random(height));
            mParticle.mass(random(1.0f, 5.0f));
        }

        /* create an attractor */
        mAttractor = new Attractor();
        mAttractor.radius(100);
        mAttractor.strength(150);
        mPhysics.add(mAttractor);
    }

    public void draw() {
        /* set attractor to mouse position */
        mAttractor.position().set(mouseX, mouseY);

        /* update the particle system */
        final float mDeltaTime = 1.0f / frameRate;
        mPhysics.step(mDeltaTime);

        /* draw */
        background(255);

        /* draw all the particles in particle system */
        fill(0);
        noStroke();
        for (int i = 0; i < mPhysics.particles().size(); i++) {
            Particle myParticle = mPhysics.particles(i);
            ellipse(myParticle.position().x, myParticle.position().y, 5, 5);
        }

        /* draw attractor. green if it is attracting and red if it is repelling */
        noStroke();
        if (mAttractor.strength() < 0) {
            fill(255, 127, 0, 50);
        } else {
            fill(0, 127, 255, 50);
        }
        ellipse(mAttractor.position().x, mAttractor.position().y,
                mAttractor.radius(), mAttractor.radius());
    }

    public void mousePressed() {
        /* flip the direction of the attractors strength. */
        float myInvertedStrength = -1 * mAttractor.strength();
        /* a negative strength turns the attractor into a repulsor */
        mAttractor.strength(myInvertedStrength);
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{SketchLesson03_Attractors.class.getName()});
    }
}

