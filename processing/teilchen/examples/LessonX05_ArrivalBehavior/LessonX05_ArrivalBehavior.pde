import processing.opengl.*;

import teilchen.BehaviorParticle;
import teilchen.Physics;
import teilchen.behavior.Arrival;
import teilchen.force.ViscousDrag;

/**
 * this demo shows how to add behaviors to particles. in this example the
 * arrival behavior.
 */

Physics mPhysics;

BehaviorParticle mParticle;

Arrival mArrival;

void setup() {
  size(640, 480, OPENGL);
  frameRate(120);
  smooth();
  colorMode(RGB, 1.0f);

  /* physics */
  mPhysics = new Physics();

  ViscousDrag myViscousDrag = new ViscousDrag();
  myViscousDrag.coefficient = 0.1f;
  mPhysics.add(myViscousDrag);

  /* create particles */
  mParticle = mPhysics.makeParticle(BehaviorParticle.class);
  mParticle.position().set(random(width), random(height));
  mParticle.maximumInnerForce(250);
  mParticle.radius(10);

  mArrival = new Arrival();
  mArrival.breakforce(5);
  mArrival.breakradius(15);

  mParticle.behaviors().add(mArrival);
}

void draw() {
  /* update particles */
  mPhysics.step(1f / frameRate);

  mArrival.oversteer(!mousePressed);
  mArrival.position().set(mouseX, mouseY);

  /* draw */
  background(1);
  drawParticle(mParticle, mArrival);
}

void drawParticle(BehaviorParticle pParticle, Arrival pArrival) {
  /* draw particle */
  stroke(0, 0.5f);
  noFill();
  if (pArrival.arriving()) {
    stroke(1, 0, 0, 0.5f);
  }
  if (pArrival.arrived()) {
    stroke(0, 1, 0, 0.5f);
  }
  ellipse(pParticle.position().x, pParticle.position().y,
  pParticle.radius() * 2, pParticle.radius() * 2);

  /* - */
  pushMatrix();
  translate(pParticle.position().x,
  pParticle.position().y);

  /* draw velocity */
  stroke(1, 0, 0, 0.5f);
  line(0, 0, pParticle.velocity().x, pParticle.velocity().y);

  /* draw break force */
  stroke(0, 0.5f, 1, 0.5f);
  line(0, 0, pArrival.force().x, pArrival.force().y);

  /* - */
  popMatrix();

  /* draw arrival */
  stroke(0, 0.25f);
  noFill();
  ellipse(pArrival.position().x,
  pArrival.position().y,
  pArrival.breakradius() * 2,
  pArrival.breakradius() * 2);
}

