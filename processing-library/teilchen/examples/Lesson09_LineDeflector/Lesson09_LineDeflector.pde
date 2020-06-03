import teilchen.*; 
import teilchen.behavior.*; 
import teilchen.constraint.*; 
import teilchen.cubicle.*; 
import teilchen.force.*; 
import teilchen.integration.*; 
import teilchen.util.*; 


/*
 * this sketch demonstrates how to create and use `LineDeflector2D` and how to use
 * `ShortLivedParticle` a particle that only exists for a defined period of time.
 *
 * press mouse to position deflector.
 */
Physics mPhysics;
LineDeflector2D mDeflector;
void settings() {
    size(640, 480, P3D);
}
void setup() {
    /* create a particle system */
    mPhysics = new Physics();
    mDeflector = new LineDeflector2D();
    mDeflector.a().set(50, height / 2.0f);
    mDeflector.b().set(width - 50, height / 2.0f - 100);
    mPhysics.add(mDeflector);
    /* create gravity */
    Gravity myGravity = new Gravity();
    myGravity.force().y = 50;
    mPhysics.add(myGravity);
    /* create drag */
    ViscousDrag myViscousDrag = new ViscousDrag();
    myViscousDrag.coefficient = 0.1f;
    mPhysics.add(myViscousDrag);
}
void draw() {
    /* rotate deflector plane */
    if (mousePressed) {
        mDeflector.a().set(mouseX, mouseY);
    }
    /* create a special particle */
    ShortLivedParticle myNewParticle = new ShortLivedParticle();
    myNewParticle.position().set(mouseX, mouseY);
    myNewParticle.velocity().set(0, random(100) + 50);
    /* this particle is removed after a specific interval */
    myNewParticle.setMaxAge(4);
    myNewParticle.radius(2.5f);
    /* add particle manually to the particle system */
    mPhysics.add(myNewParticle);
    /* update physics */
    final float mDeltaTime = 1.0f / frameRate;
    mPhysics.step(mDeltaTime);
    /* draw all the particles in the particle system */
    background(255);
    for (int i = 0; i < mPhysics.particles().size(); i++) {
        Particle mParticle = mPhysics.particles(i);
        /* this special particle has a limited life time. in this case this information is
        mapped to its transparency. */
        float mRatio = 1 - ((ShortLivedParticle) mParticle).ageRatio();
        stroke(0, 127);
        stroke(0, 64 * mRatio);
        if (mParticle.tagged()) {
            fill(255, 127, 0, 255 * mRatio);
        } else {
            fill(0, 255 * mRatio);
        }
        ellipse(mParticle.position().x, mParticle.position().y, mParticle.radius() * 2, mParticle.radius() * 2);
    }
    /* draw deflector */
    mDeflector.draw(g);
    /* finally remove the collision tag */
    mPhysics.removeTags();
}
