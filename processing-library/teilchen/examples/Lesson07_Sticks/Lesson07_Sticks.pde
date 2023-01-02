import teilchen.*; 
import teilchen.behavior.*; 
import teilchen.constraint.*; 
import teilchen.cubicle.*; 
import teilchen.force.*; 
import teilchen.integration.*; 
import teilchen.util.*; 

/*
 * this sketch demonstrates how to use a stick to connect two particles. `Stick` is similar to
 * `Spring` except that it does not use forces to move particles which results in a more
 * *stiff* behavior.
 *
 * move mouse to drag sticks.
 */

Particle[] mParticles;

Physics mPhysics;

void settings() {
    size(640, 480);
}

void setup() {
    mPhysics = new Physics();
    /* add gravity for extra fun */
    mPhysics.add(new Gravity());
    /* choose verlet integration as it produces more stable results with sticks ( and constraints in general ) */
    Verlet mVerlet = new Verlet();
    mVerlet.damping(0.99f);
    mPhysics.setIntegratorRef(mVerlet);
    /* setup sticks to form a whip */
    mParticles = new Particle[16];
    float mSegmentLength = 20.0f;
    /* create root */
    for (int x = 0; x < mParticles.length; x++) {
        mParticles[x] = mPhysics.makeParticle(x * mSegmentLength, 0, 0, 0.1f);
        if (x > 0) {
            Stick mStick = new Stick(mParticles[x - 1], mParticles[x], mSegmentLength);
            /* damp the stick to release tensions from the system */
            mStick.damping(0.99f);
            mPhysics.add(mStick);
        }
    }
    /* fix root particle so it can stick to the mouse later */
    mParticles[0].fixed(true);
}

void draw() {
    /* stick root particle to mouse */
    mParticles[0].position().set(mouseX, mouseY);
    /* update */
    /* increasing the number of iterations for each step can greatly relaxes tensions and
    errors in the system. */
    mPhysics.step(1.0f / frameRate, 5);
    /* draw sticks with descending stroke weight */
    background(255);
    stroke(0, 191);
    for (int x = 1; x < mParticles.length; x++) {
        Particle p1 = mParticles[x - 1];
        Particle p2 = mParticles[x];
        final float mStrokeWeight = 4.0f * (1.0f - (float) x / mParticles.length);
        strokeWeight(mStrokeWeight);
        line(p1.position().x, p1.position().y, p2.position().x, p2.position().y);
    }
}
