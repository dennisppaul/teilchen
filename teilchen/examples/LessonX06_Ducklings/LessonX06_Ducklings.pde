import mathematik.*;

import processing.opengl.*;

import teilchen.BehaviorParticle;
import teilchen.Physics;
import teilchen.behavior.Arrival;
import teilchen.force.Spring;
import teilchen.force.ViscousDrag;
import teilchen.util.CollisionManager;

/**
 * this demo shows how to add behaviors to particles. in this example the
 * arrival behavior.
 */

Physics mPhysics;

ArrayList<Duckling> mDucklings;

CollisionManager mCollision;

void setup() {
  size(640, 480, OPENGL);
  frameRate(60);
  smooth();
  colorMode(RGB, 1.0f);

  /* physics */
  mPhysics = new Physics();

  ViscousDrag myViscousDrag = new ViscousDrag();
  myViscousDrag.coefficient = 0.25f;
  mPhysics.add(myViscousDrag);

  mCollision = new CollisionManager();
  mCollision.minimumDistance(25);

  /* ducklings */
  mDucklings = new ArrayList<Duckling>();
  for (int i = 0; i < 13; i++) {
    final Duckling mDuckling = new Duckling();
    if (!mDucklings.isEmpty()) {
      mDuckling.arrival.setPositionRef(mDucklings.get(mDucklings.size()-1).particle.position());
    }
    mCollision.collision().add(mDuckling.particle);
    mDucklings.add(mDuckling);
  }
}


void draw() {
  final float mDeltaTime = 1.0f / frameRate;
  background(1);

  /* update particles */
  mCollision.createCollisionResolvers();
  mCollision.loop(mDeltaTime);
  mPhysics.step(mDeltaTime);

  drawCollisionSprings();
  mCollision.removeCollisionResolver();

  mDucklings.get(0).arrival.oversteer(!mousePressed);
  mDucklings.get(0).arrival.position().set(mouseX, mouseY);

  /* draw */
  for (int i=0; i < mDucklings.size(); i++) {
    Duckling mDuckling = mDucklings.get(i);
    drawParticle(mDuckling);
  }

  /* draw arrival */
  stroke(0, 0.25f);
  noFill();
  ellipse(mDucklings.get(0).arrival.position().x,
  mDucklings.get(0).arrival.position().y,
  20, 20);
}

void drawParticle(Duckling pDuckling) {
  final BehaviorParticle mParticle = pDuckling.particle;
  final Arrival mArrival = pDuckling.arrival;

  /* draw particle */
  stroke(0, 0.5f);
  noFill();
  if (mArrival.arriving()) {
    stroke(1, 0, 0, 0.5f);
  }
  if (mArrival.arrived()) {
    stroke(0, 1, 0, 0.5f);
  }
  ellipse(mParticle.position().x, mParticle.position().y,
  mParticle.radius() * 2, mParticle.radius() * 2);

  /* - */
  pushMatrix();
  translate(mParticle.position().x,
  mParticle.position().y);

  /* draw velocity */
  stroke(1, 0, 0, 0.5f);
  line(0, 0, mParticle.velocity().x, mParticle.velocity().y);

  /* draw break force */
  stroke(0, 0.5f, 1, 0.5f);
  line(0, 0, mArrival.force().x, mArrival.force().y);

  /* - */
  popMatrix();
}

void drawCollisionSprings() {
  stroke(0, 1, 0, 0.25f);
  for (int i = 0; i < mCollision.collision().forces().size(); ++i) {
    if (mCollision.collision().forces().get(i) instanceof Spring) {
      Spring mySpring = (Spring) mCollision.collision_forces().get(i);
      line(mySpring.a().position().x, mySpring.a().position().y, mySpring.a().position().z,
      mySpring.b().position().x, mySpring.b().position().y, mySpring.b().position().z);
    }
  }
}

class Duckling {

  BehaviorParticle particle;

  Arrival arrival;

  Duckling() {
    /* create particles */
    particle = mPhysics.makeParticle(BehaviorParticle.class);
    particle.position().set(random(width), random(height));
    particle.maximumInnerForce(random(50, 150));
    particle.radius(random(6, 10));

    arrival = new Arrival();
    arrival.breakforce(random(12, 28));
    arrival.breakradius(random(45, 55));

    particle.behaviors().add(arrival);
  }
}

