import teilchen.*; 
import teilchen.behavior.*; 
import teilchen.constraint.*; 
import teilchen.cubicle.*; 
import teilchen.force.*; 
import teilchen.integration.*; 
import teilchen.util.*; 


Physics mPhysics;
ArrayList<ParticleTrail> mTrails;
Attractor mAttractor;
void settings() {
    size(640, 480, P3D);
}
void setup() {
    frameRate(60);
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
    mAttractor.radius(200);
    mAttractor.strength(-300);
    mPhysics.add(mAttractor);
    /* create trails and particles */
    mTrails = new ArrayList<ParticleTrail>();
    for (int i = 0; i < 500; i++) {
        Particle mParticle = mPhysics.makeParticle();
        mParticle.mass(2.0f);
        ParticleTrail myParticleTrail = new ParticleTrail(mPhysics, mParticle, 0.2f, random(0.5f, 1));
        myParticleTrail.mass(0.5f);
        mTrails.add(myParticleTrail);
    }
    resetParticles(width / 2, height / 2);
}
void resetParticles(float x, float y) {
    for (ParticleTrail myTrails : mTrails) {
        myTrails.particle().position().set(x + random(-10, 10), y + random(-10, 10), 0);
        myTrails.particle().velocity().set(random(-10, 10), random(-10, 10), random(-10, 10));
        myTrails.fragments().clear();
    }
}
void draw() {
    /* set attractor to mouse position */
    mAttractor.position().set(mouseX, mouseY);
    for (ParticleTrail myTrails : mTrails) {
        myTrails.loop(1f / frameRate);
    }
    mPhysics.step(1f / frameRate);
    background(255);
    for (ParticleTrail myTrail : mTrails) {
        drawTrail(myTrail);
    }
}
void drawTrail(ParticleTrail theTrail) {
    final ArrayList<Particle> mFragments = theTrail.fragments();
    final Particle mParticle = theTrail.particle();
    /* draw head */
    if (mFragments.size() > 1) {
        fill(255, 0, 127);
        noStroke();
        pushMatrix();
        translate(mParticle.position().x, mParticle.position().y, mParticle.position().z);
        sphereDetail(4);
        sphere(3);
        popMatrix();
    }
    /* draw trail */
    for (int i = 0; i < mFragments.size() - 1; i++) {
        if (mFragments.get(i) instanceof ShortLivedParticle) {
            final float mRatio = 1.0f - ((ShortLivedParticle) mFragments.get(i)).ageRatio();
            stroke(127, mRatio * 255);
            strokeWeight(mRatio * 3);
        }
        int j = (i + 1) % mFragments.size();
        line(mFragments.get(i).position().x,
             mFragments.get(i).position().y,
             mFragments.get(i).position().z,
             mFragments.get(j).position().x,
             mFragments.get(j).position().y,
             mFragments.get(j).position().z);
    }
    if (!mFragments.isEmpty()) {
        line(mFragments.get(mFragments.size() - 1).position().x,
             mFragments.get(mFragments.size() - 1).position().y,
             mFragments.get(mFragments.size() - 1).position().z,
             mParticle.position().x,
             mParticle.position().y,
             mParticle.position().z);
    }
}
void mousePressed() {
    resetParticles(mouseX, mouseY);
}
