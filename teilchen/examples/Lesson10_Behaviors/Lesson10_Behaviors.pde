import mathematik.*;

import teilchen.BehaviorParticle;
import teilchen.Physics;
import teilchen.behavior.Arrival;


/**
 * this sketch shows how to assign an 'arrival' behavior to a particle.
 */
Physics mPhysics;

BehaviorParticle mParticle;

Arrival mArrival;

void setup() {
  size(640, 480, OPENGL);
  smooth();
  frameRate(120);
  colorMode(RGB, 1.0f);
  noFill();

  /* physics */
  mPhysics = new Physics();

  /* create particles */
  mParticle = mPhysics.makeParticle(BehaviorParticle.class);
  mParticle.maximumInnerForce(100);

  /* create arrival behavior */
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
  stroke(0, 0.5f);
  if (mArrival.arriving()) {
    /* color particle red while it is arriving */
    stroke(1, 0, 0, 0.5f);
  }
  if (mArrival.arrived()) {
    /* color particle green when it has arrived */
    stroke(0, 1, 0, 0.5f);
  }

  line(mParticle.position().x, 
  mParticle.position().y, 
  mParticle.position().x + mParticle.velocity().x, 
  mParticle.position().y + mParticle.velocity().y);
  fill(1);
  ellipse(mParticle.position().x, mParticle.position().y, 12, 12);

  /* draw arrival */
  stroke(0, 0.25f);
  noFill();
  ellipse(mArrival.position().x, 
  mArrival.position().y, 
  mArrival.breakradius() * 2, 
  mArrival.breakradius() * 2);
}

