import teilchen.*; 
import teilchen.behavior.*; 
import teilchen.constraint.*; 
import teilchen.cubicle.*; 
import teilchen.force.*; 
import teilchen.integration.*; 
import teilchen.util.*; 

/*
 * this sketch demonstrates how to use `ParticleTrail` to make particles leave a trail.
 *
 * press mouse to respawn particles. move mouse to change attractor position.
 */

Attractor mAttractor;

Physics mPhysics;

ArrayList<ParticleTrail> mTrails;

void settings() {
    size(640, 480);
}

void setup() {
    /* create a particle system */
    mPhysics = new Physics();
    /* create a gravitational force */
    Gravity mGravity = new Gravity();
    mPhysics.add(mGravity);
    mGravity.force().y = 20;
    /* create drag */
    ViscousDrag mViscousDrag = new ViscousDrag();
    mViscousDrag.coefficient = 0.1f;
    mPhysics.add(mViscousDrag);
    final float mBorder = 40;
    Box mBox = new Box(new PVector(mBorder, mBorder, mBorder),
                       new PVector(width - mBorder, height - mBorder, 100 - mBorder));
    mBox.reflect(true);
    mPhysics.add(mBox);
    /* create an attractor */
    mAttractor = new Attractor();
    mAttractor.radius(150);
    mAttractor.strength(-500);
    mPhysics.add(mAttractor);
    /* create trails and particles */
    mTrails = new ArrayList<ParticleTrail>();
    for (int i = 0; i < 500; i++) {
        Particle mParticle = mPhysics.makeParticle();
        mParticle.mass(random(1.5f, 3.0f));
        /* note that if `ParticleTrail` receives the same `Physics` object as the particles,
        also forces and contraints are shared. */
        ParticleTrail mParticleTrail = new ParticleTrail(mPhysics, mParticle, 0.2f, random(0.5f, 1));
        mParticleTrail.mass(0.5f);
        mTrails.add(mParticleTrail);
    }
    resetParticles(width / 2.0f, height / 2.0f);
}

void draw() {
    /* set attractor to mouse position */
    mAttractor.position().set(mouseX, mouseY);
    for (ParticleTrail mTrails : mTrails) {
        mTrails.loop(1f / frameRate);
    }
    mPhysics.step(1f / frameRate);
    background(255);
    /* draw trails */
    for (ParticleTrail mTrail : mTrails) {
        drawTrail(mTrail);
    }
    /* draw attractor */
    strokeWeight(1);
    noFill();
    stroke(0, 15);
    ellipse(mAttractor.position().x, mAttractor.position().y, mAttractor.radius() * 2, mAttractor.radius() * 2);
    stroke(0, 31);
    ellipse(mAttractor.position().x, mAttractor.position().y, mAttractor.radius() * 1, mAttractor.radius() * 1);
    stroke(0, 63);
    ellipse(mAttractor.position().x,
            mAttractor.position().y,
            mAttractor.radius() * 0.5f,
            mAttractor.radius() * 0.5f);
}

void mousePressed() {
    resetParticles(mouseX, mouseY);
}

void drawTrail(ParticleTrail pTrail) {
    final ArrayList<Particle> mFragments = pTrail.fragments();
    final Particle mParticle = pTrail.particle();
    /* draw trail */
    for (int i = 0; i < mFragments.size() - 1; i++) {
        if (mFragments.get(i) instanceof ShortLivedParticle) {
            final float mRatio = 1.0f - ((ShortLivedParticle) mFragments.get(i)).ageRatio();
            stroke(0);
            strokeWeight(mRatio * 5);
        }
        int j = (i + 1) % mFragments.size();
        line(mFragments.get(i).position().x,
             mFragments.get(i).position().y,
             mFragments.get(j).position().x,
             mFragments.get(j).position().y);
    }
    /* draw connection from trail to head */
    if (!mFragments.isEmpty()) {
        line(mFragments.get(mFragments.size() - 1).position().x,
             mFragments.get(mFragments.size() - 1).position().y,
             mParticle.position().x,
             mParticle.position().y);
    }
    /* draw head */
    if (mFragments.size() > 1) {
        fill(0);
        noStroke();
        pushMatrix();
        translate(mParticle.position().x, mParticle.position().y);
        ellipse(0, 0, 5, 5);
        popMatrix();
    }
}

void resetParticles(float x, float y) {
    for (ParticleTrail mTrails : mTrails) {
        mTrails.particle().position().set(x + random(-10, 10), y + random(-10, 10), 0);
        mTrails.particle().velocity().set(random(-10, 10), random(-10, 10), random(-10, 10));
        mTrails.fragments().clear();
    }
}
