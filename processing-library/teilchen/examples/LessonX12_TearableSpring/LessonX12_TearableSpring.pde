import teilchen.*; 
import teilchen.behavior.*; 
import teilchen.constraint.*; 
import teilchen.cubicle.*; 
import teilchen.force.*; 
import teilchen.integration.*; 
import teilchen.util.*; 


/*
 * this sketch demonstrates how to use `TearableSprings` a variation of the normal spring that
 * tears if stretched beyond a certain length. drag particles to tear spring mesh.
 */
static final int GRID_WIDTH = 32;
static final int GRID_HEIGHT = 24;
final Particle[][] mParticles = new Particle[GRID_WIDTH][GRID_HEIGHT];
Physics mPhysics;
Particle mParticleSelected = null;
void settings() {
    size(640, 480, P3D);
}
void setup() {
    mPhysics = new Physics();
    /* select `RungeKutta` for integration as it can handle multiple connected springs better */
    RungeKutta mIntegrator = new RungeKutta();
    mPhysics.setIntegratorRef(mIntegrator);
    mPhysics.add(new Gravity(0, 0.981f, 0));
    /* create particles */
    final PVector mTranslate = new PVector().set(width / 4.0f, height / 4.0f);
    for (int x = 0; x < mParticles.length; x++) {
        for (int y = 0; y < mParticles[x].length; y++) {
            final Particle p = mPhysics.makeParticle();
            p.mass(0.01f);
            p.position().set(x, y);
            p.position().mult(10.0f);
            p.position().add(mTranslate);
            mParticles[x][y] = p;
        }
    }
    /* connect particles with tearable spring */
    for (int x = 0; x < GRID_WIDTH; x++) {
        for (int y = 0; y < GRID_HEIGHT; y++) {
            final Particle p0 = mParticles[x][y];
            if (x < GRID_WIDTH - 1) {
                final Particle p1 = mParticles[x + 1][y];
                createSpring(p0, p1);
            }
            if (y < GRID_HEIGHT - 1) {
                final Particle p2 = mParticles[x][y + 1];
                createSpring(p0, p2);
            }
        }
    }
    /*  fix corner particles */
    mParticles[0][0].fixed(true);
    mParticles[GRID_WIDTH - 1][0].fixed(true);
    mParticles[0][GRID_HEIGHT - 1].fixed(true);
    mParticles[GRID_WIDTH - 1][GRID_HEIGHT - 1].fixed(true);
}
void draw() {
    if (mParticleSelected != null) {
        /* not that it is better to perform all *direct* particle manipulations before `step`
        is called */
        mParticleSelected.position().set(mouseX, mouseY);
    }
    /* in systems with a lot of *tension* it is sometimes necessary to calculate steps in
    smaller fractions. in this case 5 times per frame */
    mPhysics.step(1.0f / frameRate, 5);
    /* draw */
    background(255);
    noStroke();
    fill(0);
    for (Particle p : mPhysics.particles()) {
        final float mParticleSize = 3;
        ellipse(p.position().x, p.position().y, mParticleSize, mParticleSize);
    }
    stroke(0, 127);
    noFill();
    for (IForce f : mPhysics.forces()) {
        if (f instanceof Spring) {
            Spring s = (Spring) f;
            drawSpring(s);
        }
    }
    if (mParticleSelected != null) {
        noFill();
        stroke(255, 127, 0);
        final float mParticleSize = 5 * 3;
        ellipse(mParticleSelected.position().x, mParticleSelected.position().y, mParticleSize, mParticleSize);
    }
}
void mousePressed() {
    mParticleSelected = teilchen.util.Util.findParticleByProximity(mPhysics, mouseX, mouseY, 0, 20);
}
void mouseReleased() {
    mParticleSelected = null;
}
void drawSpring(Spring s) {
    line(s.a().position().x,
         s.a().position().y,
         s.a().position().z,
         s.b().position().x,
         s.b().position().y,
         s.b().position().z);
}
void createSpring(Particle p0, Particle p1) {
    TearableSpring s = new TearableSpring(p0, p1);
    s.tear_distance(40);
    s.strength(20.0f);
    s.damping(1.0f);
    mPhysics.add(s);
}
