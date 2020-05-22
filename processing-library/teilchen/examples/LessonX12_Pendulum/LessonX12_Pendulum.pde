import teilchen.*; 
import teilchen.behavior.*; 
import teilchen.constraint.*; 
import teilchen.cubicle.*; 
import teilchen.force.*; 
import teilchen.integration.*; 
import teilchen.util.*; 


Physics mPhysics;
Particle mPendulumRoot;
Particle mPendulumTip;
Pulse mPulse;
void settings() {
    size(640, 480);
}
void setup() {
    mPhysics = new Physics();
    Gravity mGravity = new Gravity();
    mPhysics.add(mGravity);
    mPendulumRoot = mPhysics.makeParticle(0, 0, 0, 0.05f);
    mPendulumRoot.position().set(width / 2, 100);
    mPendulumRoot.fixed(true);
    mPendulumTip = mPhysics.makeParticle(0, 0, 0, 0.05f);
    float mSegmentLength = height / 2;
    Spring mConnection = new Spring(mPendulumRoot, mPendulumTip, mSegmentLength);
    mConnection.damping(0.0f);
    mConnection.strength(10);
    mPhysics.add(mConnection);
    mPulse = new Pulse(mPendulumTip);
    mPulse.damping(0.99f);
    mPhysics.add(mPulse);
}
void draw() {
    final int NUM_ITERATIONS = 5;
    for (int i = 0; i < NUM_ITERATIONS; i++) {
        mPhysics.step(1.0f / (frameRate * NUM_ITERATIONS));
    }
    background(255);
    Particle p1 = mPendulumRoot;
    Particle p2 = mPendulumTip;
    stroke(0, 192);
    noFill();
    line(p1.position().x, p1.position().y, p2.position().x, p2.position().y);
    stroke(255, 0, 0);
    fill(255);
    ellipse(p1.position().x, p1.position().y, 10, 10);
    ellipse(p2.position().x, p2.position().y, 20, 20);
}
void mousePressed() {
    mPulse.force().set(mPendulumTip.velocity().normalize().mult(100));
}
