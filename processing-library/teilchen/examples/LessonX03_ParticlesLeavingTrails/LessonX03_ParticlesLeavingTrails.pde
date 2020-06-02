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
Physics mPhysics;
ArrayList<ParticleTrail> mTrails;
Attractor mAttractor;
void settings() {
    size(640, 480, P3D);
}
void setup() {
    /* create a particle system */
    mPhysics = new Physics();
    /* create a gravitational force */
    Gravity myGravity = new Gravity();
    mPhysics.add(myGravity);
    myGravity.force().y = 20;
    /* create drag */
    ViscousDrag myViscousDrag = new ViscousDrag();
    myViscousDrag.coefficient = 0.1f;
    mPhysics.add(myViscousDrag);
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
        ParticleTrail myParticleTrail = new ParticleTrail(mPhysics, mParticle, 0.2f, random(0.5f, 1));
        myParticleTrail.mass(0.5f);
        mTrails.add(myParticleTrail);
    }
    resetParticles(width / 2.0f, height / 2.0f);
}
void draw() {
    /* set attractor to mouse position */
    mAttractor.position().set(mouseX, mouseY);
    for (ParticleTrail myTrails : mTrails) {
        myTrails.loop(1f / frameRate);
    }
    mPhysics.step(1f / frameRate);
    background(255);
    /* draw trails */
    for (ParticleTrail myTrail : mTrails) {
        drawTrail(myTrail);
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
void resetParticles(float x, float y) {
    for (ParticleTrail myTrails : mTrails) {
        myTrails.particle().position().set(x + random(-10, 10), y + random(-10, 10), 0);
        myTrails.particle().velocity().set(random(-10, 10), random(-10, 10), random(-10, 10));
        myTrails.fragments().clear();
    }
}
void drawTrail(ParticleTrail theTrail) {
    final ArrayList<Particle> mFragments = theTrail.fragments();
    final Particle mParticle = theTrail.particle();
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
             mFragments.get(i).position().z,
             mFragments.get(j).position().x,
             mFragments.get(j).position().y,
             mFragments.get(j).position().z);
    }
    /* draw connection from trail to head */
    if (!mFragments.isEmpty()) {
        line(mFragments.get(mFragments.size() - 1).position().x,
             mFragments.get(mFragments.size() - 1).position().y,
             mFragments.get(mFragments.size() - 1).position().z,
             mParticle.position().x,
             mParticle.position().y,
             mParticle.position().z);
    }
    /* draw head */
    if (mFragments.size() > 1) {
        fill(0);
        noStroke();
        pushMatrix();
        translate(mParticle.position().x, mParticle.position().y, mParticle.position().z);
        ellipse(0, 0, 5, 5);
        popMatrix();
    }
}
