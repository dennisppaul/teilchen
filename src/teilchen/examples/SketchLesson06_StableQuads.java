package teilchen.examples;

import processing.core.PApplet;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.constraint.Box;
import teilchen.force.Gravity;
import teilchen.force.ViscousDrag;
import teilchen.integration.RungeKutta;
import teilchen.util.DrawLib;
import teilchen.util.StableSpringQuad;

public class SketchLesson06_StableQuads extends PApplet {

    /*
     * this sketch demonstrates how to connect four particles and six springs to form a
     * `StableSpringQuad` a construct that allows to emulate something similar to a *body*.
     *
     * press mouse to drag corner.
     */

    private Physics mPhysics;
    private Particle mRoot;

    public void settings() {
        size(640, 480, P3D);
    }

    public void setup() {
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

        new StableSpringQuad(mPhysics, d, c, mPhysics.makeParticle(100, 200), mPhysics.makeParticle(0, 200));

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
    }

    public void draw() {

        /* handle particles */
        if (mousePressed) {
            mRoot.fixed(true);
            mRoot.position().set(mouseX, mouseY);
        } else {
            mRoot.fixed(false);
        }

        mPhysics.step(1.0f / frameRate);

        /* draw */
        background(255);
        DrawLib.drawSprings(g, mPhysics, color(255, 127, 0, 64));
        DrawLib.drawParticles(g, mPhysics, 12, color(164), color(245));
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{SketchLesson06_StableQuads.class.getName()});
    }
}
