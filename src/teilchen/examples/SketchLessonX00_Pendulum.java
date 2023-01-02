package teilchen.examples;

import processing.core.PApplet;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.force.Gravity;
import teilchen.force.Pulse;
import teilchen.force.Spring;

public class SketchLessonX00_Pendulum extends PApplet {

    /*
     * this sketch demonstrates how to create a pendulum from two particles, a spring and a pulse
     * force.
     *
     * press mouse top push pendulum.
     */

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        mPhysics = new Physics();
        mPhysics.add(new Gravity());

        mPendulumRoot = mPhysics.makeParticle(0, 0, 0, 0.05f);
        mPendulumRoot.position().set(width / 2.0f, 100);
        mPendulumRoot.fixed(true);

        mPendulumTip = mPhysics.makeParticle(0, 0, 0, 0.05f);

        float mSegmentLength = height / 2.0f;
        Spring mConnection = new Spring(mPendulumRoot, mPendulumTip, mSegmentLength);
        mConnection.damping(0.0f);
        mConnection.strength(10);
        mPhysics.add(mConnection);

        mPulse = new Pulse(mPendulumTip);
        mPulse.damping(0.99f);
        mPhysics.add(mPulse);
    }

    public void draw() {
        mPhysics.step(1.0f / frameRate, 5);

        background(255);
        Particle p1 = mPendulumRoot;
        Particle p2 = mPendulumTip;

        stroke(0, 191);
        noFill();
        line(p1.position().x, p1.position().y, p2.position().x, p2.position().y);

        fill(0);
        noStroke();
        ellipse(p1.position().x, p1.position().y, 10, 10);
        ellipse(p2.position().x, p2.position().y, 20, 20);
    }

    public void mousePressed() {
        mPulse.force().set(mPendulumTip.velocity().normalize().mult(100));
    }

    public static void main(String[] args) {
        PApplet.main(SketchLessonX00_Pendulum.class.getName());
    }
    private Physics mPhysics;
    private Particle mPendulumRoot;
    private Particle mPendulumTip;
    private Pulse mPulse;
}
