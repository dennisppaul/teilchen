import teilchen.*; 
import teilchen.behavior.*; 
import teilchen.constraint.*; 
import teilchen.cubicle.*; 
import teilchen.force.*; 
import teilchen.integration.*; 
import teilchen.util.*; 


/*
 * this sketch demonstrates how to use behaviors.  it appliies the `Arrival` behavior to make a
 * `BehaviorParticle` arrive at a certain location.
 *
 * press mouse to position arrival destination.
 */
Physics mPhysics;
BehaviorParticle mParticle;
Arrival mArrival;
void settings() {
    size(640, 480, P3D);
}
void setup() {
    colorMode(RGB, 1.0f);
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
void draw() {
    /* set the arrival position to the mouse position */
    mArrival.position().set(mouseX, mouseY);
    /* update particle system */
    mPhysics.step(1.0f / frameRate);
    /* draw behavior particle */
    background(1);
    fill(0);
    if (mArrival.arriving()) {
        /* color particle red while it is arriving */
        fill(1, 0.5f, 0);
    }
    if (mArrival.arrived()) {
        /* color particle green when it has arrived */
        fill(0, 0.5f, 1);
        // @TODO("particle drifts after arriving.")
    }
    noStroke();
    ellipse(mParticle.position().x, mParticle.position().y, mParticle.radius() * 2, mParticle.radius() * 2);
    stroke(0);
    line(mParticle.position().x,
         mParticle.position().y,
         mParticle.position().x + mParticle.velocity().x,
         mParticle.position().y + mParticle.velocity().y);
    /* draw arrival destination */
    stroke(0);
    noFill();
    ellipse(mArrival.position().x,
            mArrival.position().y,
            mArrival.breakradius() * 2,
            mArrival.breakradius() * 2);
}
