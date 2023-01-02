import teilchen.*; 
import teilchen.behavior.*; 
import teilchen.constraint.*; 
import teilchen.cubicle.*; 
import teilchen.force.*; 
import teilchen.integration.*; 
import teilchen.util.*; 

/*
 * this sketch demonstrates how to create and use an `Attractor` and how to teleport particles.
 *
 * press mouse to toggle attractor between postive and *negative* attraction.
 */

Attractor mAttractor;

Physics mPhysics;

void settings() {
    size(640, 480);
}

void setup() {
    /* create a particle system */
    mPhysics = new Physics();
    /* create a viscous force that slows down all motion */
    ViscousDrag mDrag = new ViscousDrag();
    mDrag.coefficient = 0.75f;
    mPhysics.add(mDrag);
    /* teleport particles from one edge of the screen to the other */
    Teleporter mTeleporter = new Teleporter();
    mTeleporter.min().set(0, 0);
    mTeleporter.max().set(width, height);
    mPhysics.add(mTeleporter);
    /* create some particles */
    for (int i = 0; i < 1000; i++) {
        Particle mParticle = mPhysics.makeParticle();
        mParticle.position().set(random(width), random(height));
        mParticle.mass(random(1.0f, 5.0f));
    }
    /* create an attractor */
    mAttractor = new Attractor();
    mAttractor.radius(100);
    mAttractor.strength(150);
    mPhysics.add(mAttractor);
}

void draw() {
    /* set attractor to mouse position */
    mAttractor.position().set(mouseX, mouseY);
    /* update the particle system */
    final float mDeltaTime = 1.0f / frameRate;
    mPhysics.step(mDeltaTime);
    /* draw */
    background(255);
    /* draw all the particles in particle system */
    fill(0);
    noStroke();
    for (int i = 0; i < mPhysics.particles().size(); i++) {
        Particle mParticle = mPhysics.particles(i);
        ellipse(mParticle.position().x, mParticle.position().y, 5, 5);
    }
    /* draw attractor */
    noFill();
    stroke(0, 63);
    strokeWeight(1.0f);
    ellipse(mAttractor.position().x, mAttractor.position().y, mAttractor.radius() * 2, mAttractor.radius() * 2);
    if (mAttractor.strength() < 0) {
        noStroke();
        fill(0);
    } else {
        stroke(0);
        strokeWeight(4.0f);
    }
    ellipse(mAttractor.position().x, mAttractor.position().y, mAttractor.radius() / 2, mAttractor.radius() / 2);
}

void mousePressed() {
    /* flip the direction of the attractors strength. */
    float mInvertedStrength = -1 * mAttractor.strength();
    /* a negative strength turns the attractor into a repulsor */
    mAttractor.strength(mInvertedStrength);
}
