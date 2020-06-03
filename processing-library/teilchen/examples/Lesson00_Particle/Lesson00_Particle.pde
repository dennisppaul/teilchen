import teilchen.*; 
import teilchen.behavior.*; 
import teilchen.constraint.*; 
import teilchen.cubicle.*; 
import teilchen.force.*; 
import teilchen.integration.*; 
import teilchen.util.*; 


/*
 * this sketch demonstrates how to create a particle system with a single particle in it.
 *
 * drag mouse to fling particle.
 */
Physics mPhysics;
Particle mParticle;
void settings() {
    size(640, 480, P3D);
}
void setup() {
    /* create a particle system. */
    mPhysics = new Physics();
    /* create a particle. note that the particle is automatically added to particle system */
    mParticle = mPhysics.makeParticle();
}
void draw() {
    /* update the particle system to the next step. usually the time step is the duration of the las frame */
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
