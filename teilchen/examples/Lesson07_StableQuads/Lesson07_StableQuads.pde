import processing.opengl.*;

import mathematik.*;

import teilchen.Physics;
import teilchen.force.Gravity;
import teilchen.force.ViscousDrag;
import teilchen.util.DrawLib;
import teilchen.Particle;
import teilchen.constraint.Box;
import teilchen.integration.RungeKutta;
import teilchen.util.StableSpringQuad;

Physics mPhysics;

Particle mRoot;

void setup() {
  size(640, 480, OPENGL);
  smooth();
  frameRate(60);

  mPhysics = new Physics();
  /* we use 'runge kutta' as it is more stable for this application */
  mPhysics.setInegratorRef(new RungeKutta());

  Gravity myGravity = new Gravity();
  myGravity.force().y = 98.1f;
  mPhysics.add(myGravity);

  /* add drag to smooth the spring interaction */
  mPhysics.add(new ViscousDrag(0.2f));

  /* add a container */
  Box myBox = new Box();
  myBox.min().set(0, 0, 0);
  myBox.max().set(width, height, 0);
  mPhysics.add(myBox);

  /* create root */
  Particle a = mPhysics.makeParticle(0, 0);
  Particle b = mPhysics.makeParticle(100, 0);
  Particle c = mPhysics.makeParticle(100, 100);
  Particle d = mPhysics.makeParticle(0, 100);

  new StableSpringQuad(mPhysics, d, c, mPhysics.makeParticle(100, 200), mPhysics.makeParticle(0, 200));

  /* create stable quad from springs */
  /* first the edge-springs ... */
  final float mySpringConstant = 100;
  final float mySpringDamping = 5;
  mPhysics.makeSpring(a, b, mySpringConstant, mySpringDamping);
  mPhysics.makeSpring(b, c, mySpringConstant, mySpringDamping);
  mPhysics.makeSpring(c, d, mySpringConstant, mySpringDamping);
  mPhysics.makeSpring(d, a, mySpringConstant, mySpringDamping).restlength();
  /* ... then the diagonal-springs */
  mPhysics.makeSpring(a, c, mySpringConstant, mySpringDamping);
  mPhysics.makeSpring(b, d, mySpringConstant, mySpringDamping).restlength();

  /* define 'a' as root particle for mouse interaction */
  mRoot = a;
  mRoot.fixed(true);
}

void draw() {

  /* handle particles */
  if (mousePressed) {
    mRoot.fixed(true);
    mRoot.position().set(mouseX, mouseY);
  } 
  else {
    mRoot.fixed(false);
  }

  mPhysics.step(1f / frameRate);

  /* draw */
  background(255);
  DrawLib.drawSprings(g, mPhysics, color(255, 0, 127, 64));
  DrawLib.drawParticles(g, mPhysics, 12, color(164), color(245));
}

