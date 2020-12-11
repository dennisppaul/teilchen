package teilchen.examples;

import processing.core.PApplet;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.force.Gravity;

public class SketchLesson02_Particles extends PApplet {

    /*
     * this sketch demonstrates how to create and handle multiple particles and remove individual
     * particles.
     *
     * drag mouse to spawn particles.
     */

    private Physics mPhysics;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        /* create a particle system */
        mPhysics = new Physics();

        /* create a gravitational force and add it to the particle system */
        Gravity mGravity = new Gravity(0, 30, 0);
        mPhysics.add(mGravity);
    }

    public void draw() {
        if (mousePressed) {
            /* create and add a particle to the system */
            Particle mParticle = mPhysics.makeParticle();
            /* set particle to mouse position with random velocity */
            mParticle.position().set(mouseX, mouseY);
            mParticle.velocity().set(random(-20, 20), random(-50));
        }

        /* remove particles right before they hit the edge of the screen */
        for (int i = 0; i < mPhysics.particles().size(); i++) {
            Particle mParticle = mPhysics.particles(i);
            if (mParticle.position().y > height * 0.9f) {
                /* particles can be marked dead and will be removed on the next call to `Physics.step()` */
                mParticle.dead(true);
            }
        }

        /* update the particle system */
        float mDeltaTime = 1.0f / frameRate;
        mPhysics.step(mDeltaTime);

        /* draw all the particles in the system */
        background(255);
        fill(0);
        noStroke();
        for (int i = 0; i < mPhysics.particles().size(); i++) {
            Particle mParticle = mPhysics.particles(i);
            ellipse(mParticle.position().x, mParticle.position().y, 5, 5);
        }

        /* draw edge */
        stroke(0, 63);
        line(0, height * 0.9f, 20, height * 0.9f);
        line(width - 20, height * 0.9f, width, height * 0.9f);
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{SketchLesson02_Particles.class.getName()});
    }
}
