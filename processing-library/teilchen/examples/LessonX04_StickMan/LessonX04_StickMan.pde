import teilchen.*; 
import teilchen.behavior.*; 
import teilchen.constraint.*; 
import teilchen.cubicle.*; 
import teilchen.force.*; 
import teilchen.integration.*; 
import teilchen.util.*; 

/*
 * this sketch demonstratwa some advanced use of particles, springs ( e.g `MuscleSpring` )
 * and attractors to create a group of `StickMan`.
 *
 * press mouse to grab and fling stickmen.
 */
Attractor mAttractor;
Gravity mGravity;
Physics mPhysics;
StickMan[] mStickMan;
void settings() {
    size(640, 480);
}
void setup() {
    mPhysics = new Physics();
    mPhysics.setIntegratorRef(new RungeKutta());
    mGravity = new Gravity();
    mGravity.force().y = 20;
    mPhysics.add(mGravity);
    ViscousDrag mViscousDrag = new ViscousDrag();
    mViscousDrag.coefficient = 0.85f;
    mPhysics.add(mViscousDrag);
    mAttractor = new Attractor();
    mAttractor.radius(5000);
    mAttractor.strength(0);
    mAttractor.position().set(width / 2.0f, height / 2.0f);
    mPhysics.add(mAttractor);
    mStickMan = new StickMan[20];
    for (int i = 0; i < mStickMan.length; i++) {
        mStickMan[i] = new StickMan(mPhysics, random(0, width), random(0.3f, 0.6f));
        mStickMan[i].translate(new PVector().set(0, height / 2.0f, 0));
    }
}
void draw() {
    mPhysics.step(1f / 60f);
    Overlap.resolveOverlap(mPhysics.particles());
    /* constraint particles */
    for (int i = 0; i < mPhysics.particles().size(); i++) {
        if (mPhysics.particles(i).position().y > height - 10) {
            mPhysics.particles(i).position().y = height - 10;
        }
        if (mPhysics.particles(i).position().x > width) {
            mPhysics.particles(i).position().x = width;
        }
        if (mPhysics.particles(i).position().x < 0) {
            mPhysics.particles(i).position().x = 0;
        }
    }
    /* handle particles */
    if (mousePressed) {
        mAttractor.position().set(mouseX, mouseY);
        if (mouseButton == RIGHT) {
            mAttractor.strength(-500);
            mAttractor.radius(500);
        } else {
            mAttractor.strength(500);
            mAttractor.radius(100);
        }
    } else {
        mAttractor.strength(0);
    }
    /* draw */
    background(255);
    /* draw springs */
    stroke(0, 20);
    for (int i = 0; i < mPhysics.forces().size(); i++) {
        if (mPhysics.forces(i) instanceof Spring) {
            Spring mSpring = (Spring) mPhysics.forces(i);
            line(mSpring.a().position().x,
                 mSpring.a().position().y,
                 mSpring.b().position().x,
                 mSpring.b().position().y);
        }
    }
    /* draw particles */
    fill(0);
    noStroke();
    for (int i = 0; i < mPhysics.particles().size(); i++) {
        ellipse(mPhysics.particles(i).position().x, mPhysics.particles(i).position().y, 2, 2);
    }
    /* draw man */
    noFill();
    g.stroke(0, 127);
    for (StickMan s : mStickMan) {
        s.draw(g);
    }
    /* draw attractor */
    if (mousePressed) {
        noFill();
        stroke(0, 191);
        ellipse(mAttractor.position().x, mAttractor.position().y, 50, 50);
    }
}
