import teilchen.*; 
import teilchen.behavior.*; 
import teilchen.constraint.*; 
import teilchen.cubicle.*; 
import teilchen.force.*; 
import teilchen.integration.*; 
import teilchen.util.*; 


/*
 * this sketch demonstrates how to connect four particles and six springs to form a
 * stable quad made from springs, this a construct that allows to emulate something
 * similar to a *body*. this sketch also demonstrates how to use `StableSpringQuad`
 * to achieve the same result.
 *
 * press mouse to drag corner of stable quad.
 */
Physics mPhysics;
Particle mRoot;
void settings() {
    size(640, 480, P3D);
}
void setup() {
    mPhysics = new Physics();
    /* use `RungeKutta` as it produces more stable results in applications like these */
    mPhysics.setIntegratorRef(new RungeKutta());
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
    /* create stable quad from springs */
    /* first the edge-springs ... */
    final float mSpringConstant = 100;
    final float mSpringDamping = 5;
    mPhysics.makeSpring(a, b, mSpringConstant, mSpringDamping);
    mPhysics.makeSpring(b, c, mSpringConstant, mSpringDamping);
    mPhysics.makeSpring(c, d, mSpringConstant, mSpringDamping);
    mPhysics.makeSpring(d, a, mSpringConstant, mSpringDamping).restlength();
    /* ... then the diagonal-springs */
    mPhysics.makeSpring(a, c, mSpringConstant, mSpringDamping);
    mPhysics.makeSpring(b, d, mSpringConstant, mSpringDamping).restlength();
    /* define 'a' as root particle for mouse interaction */
    mRoot = a;
    mRoot.fixed(true);
    mRoot.radius(10);
    /* create stable quad with `StableSpringQuad` */
    new StableSpringQuad(mPhysics, d, c, mPhysics.makeParticle(100, 200), mPhysics.makeParticle(0, 200));
}
void draw() {
    /* handle particles */
    if (mousePressed) {
        mRoot.fixed(true);
        mRoot.position().set(mouseX, mouseY);
    } else {
        mRoot.fixed(false);
    }
    mPhysics.step(1.0f / frameRate);
    /* draw particles *manually* and springs using `DrawLib` */
    background(255);
    noStroke();
    fill(0);
    for (Particle p : mPhysics.particles()) {
        ellipse(p.position().x, p.position().y, 5, 5);
    }
    DrawLib.drawSprings(g, mPhysics, color(0, 63));
    /* highlight root particle */
    noStroke();
    fill(0);
    ellipse(mRoot.position().x, mRoot.position().y, 15, 15);
}
