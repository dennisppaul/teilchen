import teilchen.*; 
import teilchen.behavior.*; 
import teilchen.constraint.*; 
import teilchen.cubicle.*; 
import teilchen.force.*; 
import teilchen.integration.*; 
import teilchen.util.*; 

/*
 * this sketch demonstrates how to create and use ``PlaneDeflector` and how to use
 * `ShortLivedParticle` a particle that only exists for a defined period of time.
 *
 * drag mouse to tilt deflector.
 */
PlaneDeflector mDeflector;
Physics mPhysics;
void settings() {
    size(640, 480);
}
void setup() {
    /* create a particle system */
    mPhysics = new Physics();
    /* create a deflector and add it to the particle system.
     * the that defines the deflection area is defined by an
     * origin and a normal. this also means that the plane s size
     * is infinite.
     * note that there is also a triangle deflector that is constraint
     * by three points.
     */
    mDeflector = new PlaneDeflector();
    /* set plane origin into the center of the screen */
    mDeflector.plane().origin.set(width / 2.0f, height / 2.0f, 0);
    mDeflector.plane().normal.set(0, -1, 0);
    /* the coefficient of restitution defines how hard particles bounce of the deflector */
    mDeflector.coefficientofrestitution(0.7f);
    mPhysics.add(mDeflector);
    /* create gravity */
    Gravity mGravity = new Gravity();
    mGravity.force().y = 50;
    mPhysics.add(mGravity);
    /* create drag */
    ViscousDrag mViscousDrag = new ViscousDrag();
    mViscousDrag.coefficient = 0.1f;
    mPhysics.add(mViscousDrag);
}
void draw() {
    /* rotate deflector plane */
    if (mousePressed) {
        final float mAngle = 2 * PI * (float) mouseX / width - PI;
        mDeflector.plane().normal.set(sin(mAngle), -cos(mAngle), 0);
    }
    /* create a special particle */
    ShortLivedParticle mNewParticle = new ShortLivedParticle();
    mNewParticle.position().set(mouseX, mouseY);
    mNewParticle.velocity().set(0, random(100) + 50);
    /* this particle is removed after a specific interval */
    mNewParticle.setMaxAge(4);
    /* add particle manually to the particle system */
    mPhysics.add(mNewParticle);
    /* update physics */
    final float mDeltaTime = 1.0f / frameRate;
    mPhysics.step(mDeltaTime);
    /* draw all the particles in the particle system */
    background(255);
    for (int i = 0; i < mPhysics.particles().size(); i++) {
        Particle mParticle = mPhysics.particles(i);
        /* `ShortLivedParticle` keeps track of much it has to *live*.
         * in this case the live time is mapped to its transparency.
         */
        float mRatio = 1 - ((ShortLivedParticle) mParticle).ageRatio();
        fill(0, 255 * mRatio);
        noStroke();
        float mDiameter;
        if (mParticle.tagged()) {
            mDiameter = 10;
        } else {
            mDiameter = 5;
        }
        ellipse(mParticle.position().x, mParticle.position().y, mDiameter, mDiameter);
    }
    /* draw deflector */
    stroke(0);
    strokeWeight(3.0f);
    line(mDeflector.plane().origin.x - mDeflector.plane().normal.y * -width,
         mDeflector.plane().origin.y + mDeflector.plane().normal.x * -width,
         mDeflector.plane().origin.x - mDeflector.plane().normal.y * width,
         mDeflector.plane().origin.y + mDeflector.plane().normal.x * width);
    strokeWeight(1.0f);
    line(mDeflector.plane().origin.x,
         mDeflector.plane().origin.y,
         mDeflector.plane().origin.x + mDeflector.plane().normal.x * 20,
         mDeflector.plane().origin.y + mDeflector.plane().normal.y * 20);
    /* finally remove the collision tag */
    mPhysics.removeTags();
}
