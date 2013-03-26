import processing.opengl.*;

import mathematik.Vector3f;

import teilchen.Particle;
import teilchen.Physics;
import teilchen.constraint.IConstraint;
import teilchen.constraint.Stick;
import teilchen.force.Attractor;
import teilchen.force.Gravity;
import teilchen.integration.Verlet;


Physics mPhysics;

Particle[][] mParticles;

final int GRID_WIDTH = 32;

final int GRID_HEIGHT = 16;

Attractor mAttractor;

void setup() {
  size(640, 480, OPENGL);
  frameRate(60);

  mPhysics = new Physics();
  mPhysics.contraint_iterations_per_steps = 5;

  Verlet myVerlet = new Verlet();
  myVerlet.damping(0.9f);
  mPhysics.setInegratorRef(myVerlet);
  mPhysics.add(new Gravity(new Vector3f(0, 1000f, 0)));

  mAttractor = new Attractor();
  mAttractor.strength(-15000);
  mAttractor.radius(300);
  mPhysics.add(mAttractor);

  mParticles = new Particle[GRID_WIDTH][GRID_HEIGHT];

  /* setup cloth */
  float mGridStepX = ((float)width / GRID_WIDTH);
  float mGridStepY = (((float)height * 0.5f) / GRID_HEIGHT);
  for (int y = 0; y < GRID_HEIGHT; y++) {
    for (int x = 0; x < GRID_WIDTH; x++) {
      mParticles[x][y] = mPhysics.makeParticle();
      mParticles[x][y].position().set((x + 0.5f) * mGridStepX,
      y * mGridStepY,
      random(0, 1));
      mParticles[x][y].old_position().set(mParticles[x][y].position());
      mParticles[x][y].mass(0.1f);

      final float DAMPING = 0.9f;
      if (y > 0) {
        Stick myStick = new Stick(mParticles[x][y - 1],
        mParticles[x][y],
        mGridStepY);
        myStick.damping(DAMPING);
        mPhysics.add(myStick);
      }
      if (x > 0) {
        Stick myStick = new Stick(mParticles[x - 1][y],
        mParticles[x][y],
        mGridStepX);
        myStick.damping(DAMPING);
        mPhysics.add(myStick);
      }
      if (x > 0 && y > 0) {
        Stick myStick1 = new Stick(mParticles[x - 1][y - 1],
        mParticles[x][y],
        new Vector3f(mGridStepX, mGridStepY).length());
        mPhysics.add(myStick1);
        Stick myStick2 = new Stick(mParticles[x][y - 1],
        mParticles[x - 1][y],
        new Vector3f(mGridStepX, mGridStepY).length());
        mPhysics.add(myStick2);
      }
    }
  }

  /* fix first row */
  for (int x = 0; x < mParticles.length; x++) {
    mParticles[x][0].fixed(true);
  }
}

void draw() {

  /* update */
  mAttractor.position().set(mouseX, mouseY, 50);
  mPhysics.step(1.0 / frameRate);

  background(255);

  /* draw sticks */
  stroke(0, 127);

  for (int i = 0; i < mPhysics.constraints().size(); i++) {
    IConstraint mConstraint = mPhysics.constraints().get(i);
    if (mConstraint instanceof Stick) {
      final Stick myStick = (Stick)mConstraint;
      line(myStick.a().position().x,
      myStick.a().position().y,
      myStick.a().position().z,
      myStick.b().position().x,
      myStick.b().position().y,
      myStick.b().position().z);
    }
  }
}

