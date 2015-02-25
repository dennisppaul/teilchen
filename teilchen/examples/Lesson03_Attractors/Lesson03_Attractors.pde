import processing.opengl.*;

import mathematik.*;

import teilchen.Particle;
import teilchen.Physics;
import teilchen.constraint.Teleporter;
import teilchen.force.Attractor;
import teilchen.force.ViscousDrag;

/**
 * this sketch shows how to create and use attractors.
 */

Physics mPhysics;

Attractor mAttractor;

void setup() {
  size(640, 480, OPENGL);
  smooth();
  frameRate(30);

  /* create a particle system */
  mPhysics = new Physics();

  /* create a viscous force that slows down all motion */
  ViscousDrag myDrag = new ViscousDrag();
  myDrag.coefficient = 0.75f;
  mPhysics.add(myDrag);

  /* teleport particles from one edge of the screen to the other */
  Teleporter mTeleporter = new Teleporter();
  mTeleporter.min().set(0, 0);
  mTeleporter.max().set(width, height);
  mPhysics.add(mTeleporter);

  /* create some particles */
  for (int i = 0; i < 100; i++) {
    Particle myParticle = mPhysics.makeParticle();
    myParticle.position().set(random(width), random(height));
  }

  /* create an attractor */
  mAttractor = new Attractor();
  mAttractor.radius(100);
  mAttractor.strength(150);
  mPhysics.add(mAttractor);
}

void mousePressed() {
  /* flip the direction of the attractors strength. */
  float myInvertedStrength = -1 * mAttractor.strength();
  /* a negative strength turns the attractor into a repulsor */
  mAttractor.strength(myInvertedStrength);
}

void draw() {
  /* set attractor to mouse position */
  mAttractor.position().set(mouseX, mouseY);

  /* update the particle system */
  final float mDeltaTime = 1.0 / frameRate;
  mPhysics.step(mDeltaTime);

  /* draw */
  background(255);

  /* draw all the particles in particle system */
  fill(245);
  stroke(164);
  for (int i = 0; i < mPhysics.particles().size(); i++) {
    Particle myParticle = mPhysics.particles(i);
    ellipse(myParticle.position().x, myParticle.position().y, 12, 12);
  }

  /* draw attractor. green if it is attracting and red if it is repelling */
  noStroke();
  if (mAttractor.strength() < 0) {
    fill(255, 0, 0, 50);
  } 
  else {
    fill(0, 255, 0, 50);
  }
  ellipse(mAttractor.position().x, mAttractor.position().y,
  mAttractor.radius(), mAttractor.radius());
}

