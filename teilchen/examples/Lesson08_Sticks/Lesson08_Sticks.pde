import processing.opengl.*;

import mathematik.*;

import teilchen.Particle;
import teilchen.Physics;
import teilchen.constraint.Stick;
import teilchen.force.Gravity;
import teilchen.integration.Verlet;

Physics mPhysics;

Particle[] mParticles;

void setup() {
  size(640, 480, OPENGL);
  frameRate(60);
  smooth();

  mPhysics = new Physics();
  /* increase the number of iterations for contraints in each step. this can greatly relaxes tensions in the system. */
  mPhysics.contraint_iterations_per_steps = 5;

  /* add gravity for extra fun */
  mPhysics.add(new Gravity());

  /* we chose verlet integration as it integrates much more nicely with sticks ( and constraints in general ) */
  Verlet myVerlet = new Verlet();
  myVerlet.damping(0.99f);
  mPhysics.setInegratorRef(myVerlet);

  /* setup sticks to form a whip */
  mParticles = new Particle[16];
  float mSegmentLength = 20.0;
  /* create root */
  for (int x = 0; x < mParticles.length; x++) {
    mParticles[x] = mPhysics.makeParticle(x * mSegmentLength, 0, 0, 0.1f);
    if (x > 0) {
      Stick myStick = new Stick(mParticles[x - 1],
      mParticles[x],
      mSegmentLength);
      /* damp the stick to release tensions from the system */
      myStick.damping(0.99f);
      mPhysics.add(myStick);
    }
  }

  /* fix root particle so it can stick to the mouse later */
  mParticles[0].fixed(true);
}

void draw() {
  /* stick root particle to mouse */
  mParticles[0].position().set(mouseX, mouseY);

  /* update */
  mPhysics.step(1.0 / frameRate);

  /* draw sticks with descending stroke weight */
  background(255);
  stroke(0, 192);
  for (int x = 1; x < mParticles.length; x++) {
    Particle p1 = mParticles[x - 1];
    Particle p2 = mParticles[x];
    final float mStrokeWeight = 4.0 * (1.0 - (float)x / mParticles.length);
    strokeWeight(mStrokeWeight);
    line(p1.position().x, p1.position().y, p1.position().z,
    p2.position().x, p2.position().y, p2.position().z);
  }
}

