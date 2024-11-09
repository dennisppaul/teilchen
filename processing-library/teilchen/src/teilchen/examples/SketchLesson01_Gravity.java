package teilchen.examples;

import processing.core.PApplet;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.force.Gravity;

public class SketchLesson01_Gravity extends PApplet {

    /*
     * this sketch demonstrates how to create a particle system with a single particle in it and a
     * gravity force pulling it downward.
     *
     * drag mouse to fling particle.
     */

    private Physics mPhysics;
    private Particle mParticle;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        /* create a particle system */
        mPhysics = new Physics();

        /* create a gravitational force */
        Gravity mGravity = new Gravity();
        /* the direction of the gravity is defined by the 'force' vector */
        mGravity.force().set(0, 30);
        /* forces, like gravity or any other force, can be added to the system. they will be automatically applied to
         all particles */
        mPhysics.add(mGravity);

        /* create a particle and add it to the system */
        mParticle = mPhysics.makeParticle();
    }

    public void draw() {
        /* update the particle system. this applies the gravity to the particle */
        final float mDeltaTime = 1.0f / frameRate;
        mPhysics.step(mDeltaTime);

        /* draw particle */
        background(255);
        fill(0);
        noStroke();
        ellipse(mParticle.position().x, mParticle.position().y, 5, 5);

        /* reset particle s position and velocity */
        if (mousePressed) {
            mParticle.position().set(mouseX, mouseY);
            mParticle.velocity().set(mouseX - pmouseX, mouseY - pmouseY);
            mParticle.velocity().mult(10);
        }
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{SketchLesson01_Gravity.class.getName()});
    }
}
