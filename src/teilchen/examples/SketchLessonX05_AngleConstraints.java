package teilchen.examples;

import processing.core.PApplet;
import teilchen.IParticle;
import teilchen.Physics;
import teilchen.constraint.AngleConstraintStick;
import teilchen.constraint.Stick;
import teilchen.force.AngleConstraintSpring;
import teilchen.force.Gravity;
import teilchen.force.Spring;
import teilchen.force.ViscousDrag;
import teilchen.integration.RungeKutta;

public class SketchLessonX05_AngleConstraints extends PApplet {

    /*
     * this sketch demonstrates how to contraint the angle between two springs or two sticks.
     *
     * drag mouse to move particle.
     */

    private AngleConstraintSpring mAngleConstraintABC;
    private AngleConstraintStick mAngleConstraintBCD;
    private IParticle mParticleA;
    private IParticle mParticleB;
    private IParticle mParticleC;
    private IParticle mParticleD;
    private Physics mPhysics;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        mPhysics = new Physics();
        mPhysics.setIntegratorRef(new RungeKutta());

        ViscousDrag mViscousDrag = new ViscousDrag();
        mViscousDrag.coefficient = 1f;
        mPhysics.add(mViscousDrag);

        Gravity mGravity = new Gravity();
        mGravity.force().y = 50;
        mPhysics.add(mGravity);

        /* particles */
        mParticleA = mPhysics.makeParticle();
        mParticleB = mPhysics.makeParticle();
        mParticleC = mPhysics.makeParticle();
        mParticleD = mPhysics.makeParticle();

        mParticleA.position().set(width / 2.0f + 50, height / 3.0f);
        mParticleB.position().set(width / 2.0f, height - height / 1.75f);
        mParticleC.position().set(width / 2.0f, height - height / 4.0f);
        mParticleD.position().set(width / 2.0f, height - height / 8.0f);

        mParticleA.radius(7);
        mParticleB.radius(3);
        mParticleC.radius(10);
        mParticleD.radius(2);

        mParticleB.fixed(true);

        /* springs */
        Spring mSpringAB = new Spring(mParticleA, mParticleB);
        mSpringAB.strength(250);
        mSpringAB.damping(10);
        mPhysics.add(mSpringAB);

        Spring mSpringBC = new Spring(mParticleB, mParticleC);
        mSpringBC.strength(250);
        mSpringBC.damping(10);
        mPhysics.add(mSpringBC);

        Stick mSpringCD = new Stick(mParticleC, mParticleD);
        mSpringCD.damping(1);
        mPhysics.add(mSpringCD);

        /* angle constraint */
        mAngleConstraintABC = new AngleConstraintSpring(mParticleA, mParticleB, mParticleC);
        mAngleConstraintABC.min_angle(PI * 0.5f);
        mAngleConstraintABC.damping(1);
        mAngleConstraintABC.strength(200);
        mPhysics.add(mAngleConstraintABC);

        mAngleConstraintBCD = new AngleConstraintStick(mParticleB, mParticleC, mParticleD);
        mAngleConstraintBCD.min_angle(PI * 0.8f);
        mAngleConstraintBCD.damping(0.5f);
        mPhysics.add(mAngleConstraintBCD);
    }

    public void draw() {
        /* attach particle to mouse */
        if (mousePressed) {
            mParticleA.position().set(mouseX, mouseY);
        }

        /* apply constraints */
        mAngleConstraintABC.pre_step();
        mAngleConstraintBCD.pre_step();
        draw_physics();

        mPhysics.step(1f / frameRate);

        /* remove contraints */
        mAngleConstraintABC.post_step();
        mAngleConstraintBCD.post_step();
    }

    private void drawParticle(IParticle p) {
        ellipse(p.position().x, p.position().y, p.radius() * 2, p.radius() * 2);
    }

    private void drawParticles() {
        noStroke();
        fill(0);
        drawParticle(mParticleA);
        drawParticle(mParticleB);
        drawParticle(mParticleC);
        drawParticle(mParticleD);
    }

    private void drawSprings() {
        for (int i = 0; i < mPhysics.forces().size(); i++) {
            if (mPhysics.forces(i) instanceof Spring) {
                final Spring mSpring = (Spring) mPhysics.forces(i);
                if (mSpring instanceof AngleConstraintSpring) {
                    strokeWeight(1);
                    if (mSpring.active()) {
                        stroke(0, 191);
                    } else {
                        stroke(0, 31);
                    }
                } else {
                    strokeWeight(3);
                    stroke(0);
                }
                line(mSpring.a(), mSpring.b());
            }
        }
        strokeWeight(1);
    }

    private void drawSticks() {
        for (int i = 0; i < mPhysics.constraints().size(); i++) {
            if (mPhysics.constraints(i) instanceof Stick) {
                final Stick mStick = (Stick) mPhysics.constraints(i);
                if (mStick instanceof AngleConstraintStick) {
                    strokeWeight(1);
                    if (mStick.active()) {
                        stroke(0, 191);
                    } else {
                        stroke(0, 31);
                    }
                } else {
                    strokeWeight(2);
                    stroke(0);
                }
                line(mStick.a(), mStick.b());
            }
        }
        strokeWeight(1);
    }

    private void draw_physics() {
        background(255);

        drawSprings();
        drawSticks();
        drawParticles();
    }

    private void line(IParticle p1, IParticle p2) {
        line(p1.position().x, p1.position().y, p2.position().x, p2.position().y);
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{SketchLessonX05_AngleConstraints.class.getName()});
    }
}
