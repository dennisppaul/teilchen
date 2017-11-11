package teilchen.examples;

import processing.core.PApplet;
import teilchen.Particle;
import teilchen.Physics;

/**
 this sketch show how to create a particle system with a single particle in it.
 */
public class SketchLesson00_Particle extends PApplet {

    private Physics mPhysics;

    private Particle mParticle;

    public void settings() {
        size(640, 480, P3D);
    }

    public void setup() {
        smooth();
        frameRate(30);

        /* create a particle system. */
        mPhysics = new Physics();

        /* create a particle. note that the particle is automatically added to particle system */
        mParticle = mPhysics.makeParticle();
    }

    public void draw() {
        /* update the particle system to the next step. usually the time step is the duration of the las frame */
        final float mDeltaTime = 1.0f / frameRate;
        mPhysics.step(mDeltaTime);

        /* draw particle */
        background(255);
        stroke(0, 127);
        fill(0, 32);
        ellipse(mParticle.position().x, mParticle.position().y, 12, 12);

        /* reset particle s position and velocity */
        if (mousePressed) {
            mParticle.position().set(mouseX, mouseY);
            mParticle.velocity().set(mouseX - pmouseX, mouseY - pmouseY);
            mParticle.velocity().mult(10);
        }
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{SketchLesson00_Particle.class.getName()});
    }
}
