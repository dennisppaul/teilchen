import teilchen.*; 
import teilchen.behavior.*; 
import teilchen.constraint.*; 
import teilchen.cubicle.*; 
import teilchen.force.*; 
import teilchen.integration.*; 
import teilchen.util.*; 


/*
 * this sketch demonstrates how to create and handle multiple particles and remove individual
 * particles.
 */
Physics mPhysics;
void settings() {
    size(640, 480, P3D);
}
void setup() {
    smooth();
    frameRate(30);
    /* create a particle system */
    mPhysics = new Physics();
    /* create a gravitational force and add it to the particle system */
    Gravity myGravity = new Gravity(0, 30, 0);
    mPhysics.add(myGravity);
}
void draw() {
    if (mousePressed) {
        /* create and add a particle to the system */
        Particle mParticle = mPhysics.makeParticle();
        /* set particle to mouse position with random velocity */
        mParticle.position().set(mouseX, mouseY);
        mParticle.velocity().set(random(-20, 20), random(-50));
    }
    /* update the particle system */
    final float mDeltaTime = 1.0f / frameRate;
    mPhysics.step(mDeltaTime);
    /* remove particles right before they hit the edge of the screen */
    for (int i = 0; i < mPhysics.particles().size(); i++) {
        Particle mParticle = mPhysics.particles(i);
        if (mParticle.position().y > height * 0.9f) {
            mPhysics.particles().remove(i); // @TODO(check if this potentially creates an exception)
        }
    }
    /* draw all the particles in the system */
    background(255);
    stroke(0, 127);
    fill(0, 32);
    for (int i = 0; i < mPhysics.particles().size(); i++) {
        Particle mParticle = mPhysics.particles(i);
        ellipse(mParticle.position().x, mParticle.position().y, 10, 10);
    }
}
