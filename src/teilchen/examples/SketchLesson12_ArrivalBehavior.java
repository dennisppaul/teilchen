package teilchen.examples;

import processing.core.PApplet;
import teilchen.BehaviorParticle;
import teilchen.Physics;
import teilchen.behavior.Arrival;

public class SketchLesson12_ArrivalBehavior extends PApplet {

    /*
     * this sketch demonstrates how to use behaviors.  it appliies the `Arrival` behavior to make a
     * `BehaviorParticle` arrive at a certain location.
     *
     * press mouse to position arrival destination.
     */

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        /* physics */
        mPhysics = new Physics();

        /* create particles */
        mParticle = mPhysics.makeParticle(BehaviorParticle.class);
        mParticle.maximumInnerForce(100);
        mParticle.radius(10);

        /* create behavior */
        mArrival = new Arrival();
        mArrival.breakforce(mParticle.maximumInnerForce() * 0.25f);
        mArrival.breakradius(mParticle.maximumInnerForce() * 0.25f);
        mParticle.behaviors().add(mArrival);
    }

    public void draw() {
        /* set the arrival position to mouse position */
        mArrival.position().set(mouseX, mouseY);

        /* update particle system */
        mPhysics.step(1.0f / frameRate);

        /* draw behavior particle */
        background(255);
        noFill();
        stroke(0, 191);
        if (mArrival.arriving()) {
            ellipse(mParticle.position().x, mParticle.position().y, mParticle.radius() * 4, mParticle.radius() * 4);
        }
        if (mArrival.arrived()) {
            ellipse(mParticle.position().x, mParticle.position().y, mParticle.radius() * 3, mParticle.radius() * 3);
            // @TODO("particle drifts after arriving.")
        }
        fill(0);
        noStroke();
        ellipse(mParticle.position().x, mParticle.position().y, mParticle.radius() * 2, mParticle.radius() * 2);

        /* draw velocity */
        stroke(0, 191);
        line(mParticle.position().x,
             mParticle.position().y,
             mParticle.position().x + mParticle.velocity().x,
             mParticle.position().y + mParticle.velocity().y);

        /* draw arrival destination */
        stroke(0, 191);
        noFill();
        ellipse(mArrival.position().x, mArrival.position().y, mArrival.breakradius() * 2, mArrival.breakradius() * 2);
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{SketchLesson12_ArrivalBehavior.class.getName()});
    }
    private Physics mPhysics;
    private BehaviorParticle mParticle;
    private Arrival mArrival;
}
