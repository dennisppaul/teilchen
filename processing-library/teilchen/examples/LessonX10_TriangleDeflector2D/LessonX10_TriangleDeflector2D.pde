import teilchen.*; 
import teilchen.behavior.*; 
import teilchen.constraint.*; 
import teilchen.cubicle.*; 
import teilchen.force.*; 
import teilchen.integration.*; 
import teilchen.util.*; 

/*
 * this sketch demonstrates how to use `TriangleDeflectors` in a 2D context to make particles
 * bounce off a triangle ( that looks like a line ). it also demonstrates how to use
 * `MortalParticle` to remove particles automatically once they leave the screen.
 *
 * press mouse to spawn particles.
 */

Physics mPhysics;

TriangleDeflector mTriangleDeflector;

void settings() {
    size(640, 480);
}

void setup() {
    /* physics */
    mPhysics = new Physics();
    Gravity mGravity = new Gravity(0, 20, 0);
    mPhysics.add(mGravity);
    /* triangle deflector */
    final float mPadding = 50;
    mTriangleDeflector = teilchen.util.Util.createTriangleDeflector2D(mPadding,
                                                                      height - mPadding - 40,
                                                                      width - mPadding,
                                                                      height - mPadding + 40,
                                                                      1.0f);
    mPhysics.add(mTriangleDeflector);
}

void draw() {
    mPhysics.step(1.0f / frameRate);
    /* draw particles */
    background(255);
    fill(0);
    strokeWeight(3);
    for (int i = 0; i < mPhysics.particles().size(); i++) {
        Particle mParticle = mPhysics.particles(i);
        if (mParticle.tagged()) {
            stroke(0);
        } else {
            noStroke();
        }
        ellipse(mParticle.position().x, mParticle.position().y, 5, 5);
    }
    /* draw deflectors */
    noFill();
    stroke(0);
    strokeWeight(3);
    line(mTriangleDeflector.a().x, mTriangleDeflector.a().y, mTriangleDeflector.b().x, mTriangleDeflector.b().y);
    /* finally remove the collision tag */
    mPhysics.removeTags();
    /* create and add a particle to the system */
    if (mousePressed) {
        MyMortalParticle mParticle = new MyMortalParticle();
        mPhysics.add(mParticle);
        /* set particle to mouse position with random velocity */
        mParticle.position().set(mouseX, mouseY);
        mParticle.velocity().set(random(-20, 20), 0);
    }
}

class MyMortalParticle extends MortalParticle {
    
boolean isDead() {
        return position().y > height || still();
    }
}
