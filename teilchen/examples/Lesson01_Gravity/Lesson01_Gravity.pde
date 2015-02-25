import processing.opengl.*;

import mathematik.*;

import teilchen.Particle;
import teilchen.Physics;
import teilchen.force.Gravity;

/**
 * this sketch show how to create a particle system with a single particle in it.
 */

Physics mPhysics;

Particle mParticle;

void setup() {
  size(640, 480, OPENGL);
  smooth();
  frameRate(30);

  /* create a particle system */
  mPhysics = new Physics();

  /* create a gravitational force */
  Gravity mGravity = new Gravity();
  /* the direction of the gravity is defined by the 'force' vector */
  mGravity.force().set(0, 30, 0);
  /* forces, like gravity or any other force, can be added to the system. they will be automatically applied to all particles */
  mPhysics.add(mGravity);

  /* create a particle and add it to the system */
  mParticle = mPhysics.makeParticle();
}

void draw() {
  /* update the particle system. this applies the gravity to the particle */
  final float mDeltaTime = 1.0 / frameRate;
  mPhysics.step(mDeltaTime);

  /* draw particle */
  background(255);
  stroke(0, 127);
  fill(0, 32);
  ellipse(mParticle.position().x, mParticle.position().y, 12, 12);

  /* reset particle s position and velocity */
  if (mousePressed) {
    mParticle.position().set(mouseX, mouseY);
    mParticle.velocity().set(mouseX - pmouseX, mouseY - pmouseY);
    mParticle.velocity().scale(10);
  }
}

