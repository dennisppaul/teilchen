import teilchen.*; 
import teilchen.behavior.*; 
import teilchen.constraint.*; 
import teilchen.cubicle.*; 
import teilchen.force.*; 
import teilchen.integration.*; 
import teilchen.util.*; 


/*
 * this sketch demonstrates how to use `VectorField`. a vector field is a set of regions
 * that apply a force to all particles with the regions.
 */
Physics mPhysics;
VectorField mVectorField;
final boolean mDrawParticlesAsLines = false; /* drawing particles as lines looks more intriguing but less explicatory */
void settings() {
    size(640, 480, P2D);
}
void setup() {
    mPhysics = new Physics();
    mVectorField = new VectorField(28, 20);
    mVectorField.hint(VectorField.ENABLE_IGNORE_3D);
    mVectorField.scale().set(20, 20);
    mVectorField.position().set(40, 40);
    mVectorField.randomize_forces(40);
    mPhysics.add(mVectorField);
    ViscousDrag myDrag = new ViscousDrag(0.75f);
    mPhysics.add(myDrag);
    spawnParticles();
}
void draw() {
    mVectorField.smooth_forces(true);
    mVectorField.set_force_strength(40);
    mPhysics.step(1.0f / frameRate);
    background(255);
    /* draw vectro field */
    noFill();
    stroke(0, 63);
    if (mousePressed) { VectorField.draw(g, mVectorField, 0.3f); }
    /* draw particles */
    if (!mDrawParticlesAsLines) {
        stroke(0, 127);
        for (Particle p : mPhysics.particles()) {
            point(p.position().x, p.position().y);
        }
    } else {
        stroke(0, 12);
        beginShape(LINES);
        for (Particle p : mPhysics.particles()) {
            vertex(p.position().x, p.position().y);
        }
        endShape();
    }
}
void keyPressed() {
    spawnParticles();
    mVectorField.randomize_forces(40);
}
void spawnParticles() {
    mPhysics.particles().clear();
    for (int i = 0; i < 20000; i++) {
        mPhysics.makeParticle(random(width), random(height)).mass(random(0.5f, 2.0f));
    }
}
