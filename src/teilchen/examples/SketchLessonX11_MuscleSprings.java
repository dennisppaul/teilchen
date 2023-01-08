package teilchen.examples;

import processing.core.PApplet;
import processing.core.PVector;
import teilchen.BasicParticle;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.constraint.Box;
import teilchen.force.Gravity;
import teilchen.force.IForce;
import teilchen.force.MuscleSpring;

public class SketchLessonX11_MuscleSprings extends PApplet {

    /*
     * this sketch demonstrates how to use `MuscleSpring`, a spring that contracts and relaxes over
     * time; like a muscle. this sketch is also a tip of the hat to the famouse
     * [Soda Constructor](https://en.wikipedia.org/wiki/Soda_Constructor).
     *
     * press mouse to create particle.
     * hover over particle and drag mouse to create spring and connect particles.
     * hover over particle and press `X` to delete particle and its connected springs.
     * press `D`, hover over particle and drag mouse to drag particle.
     * press `SPACE` to toggle simulation on/off.
     */

    private static final float MUSCLE_CONTRACTION_LENGTH = 0.3f;
    private static final float MUSCLE_CONTRACTION_SPEED = 1.5f;
    private static final float MUSCLE_PHASE_SHIFT_SCALE = PI;
    private static final float PARTICAL_MASS = 0.25f;
    private static final float SELECTION_RADIUS = 10;
    private static final float SPRING_STRENGTH = 1.0f;
    private Gravity mGravity;
    private boolean mPauseSimulation;
    private Physics mPhysics;
    private Particle mTemporaryParticle;
    private MuscleSpring mTemporarySpring;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        mPhysics = new Physics();

        mGravity = new Gravity();
        mPhysics.add(mGravity);

        float mPadding = 5;
        Box mBox = new Box();
        mBox.min().set(mPadding, mPadding, 0);
        mBox.max().set(width - mPadding, height - mPadding, 0);
        mBox.coefficientofrestitution(0.7f);
        mBox.reflect(true);
        mPhysics.add(mBox);

        mPauseSimulation = true;
    }

    public void draw() {
        /* move selected particle */
        if (mTemporaryParticle != null) {
            mTemporaryParticle.position().set(mouseX, mouseY);
        }

        /* advance simulation */
        if (!mPauseSimulation) {
            mPhysics.step(1.0f / frameRate);
        }

        /* draw */
        background(255);
        drawSimulationState();
        drawParticlesSprings();
        drawTemporaryParticleSpring();
        drawHighlightParticleNearby();
    }

    public void mousePressed() {
        beginCreateParticleSpring();
    }

    public void mouseReleased() {
        endCreateParticleSpring();
    }

    public void keyPressed() {
        switch (key) {
            case ' ':
                mPauseSimulation = !mPauseSimulation;
                break;
            case 'x':
            case 'X':
                deleteParticleAndSpring();
                break;
            case 'f':
            case 'F':
                toggleParticleFixed();
                break;
            case 'g':
            case 'G':
                invertGravity();
                break;
        }
    }

    private void beginCreateParticleSpring() {
        Particle mSelectedParticle = getParticleCloseToMouse();
        if (mSelectedParticle != null) {
            if (keyPressed && (key == 'd' || key == 'D')) {
                mTemporaryParticle = mSelectedParticle;
            } else {
                mTemporaryParticle = createParticle();
            }
            mTemporarySpring = createSpring(mSelectedParticle, mTemporaryParticle);
        } else {
            mTemporaryParticle = createParticle();
        }
    }

    private BasicParticle createParticle() {
        BasicParticle p = new BasicParticle();
        p.mass(PARTICAL_MASS);
        return p;
    }

    private MuscleSpring createSpring(Particle a, Particle b) {
        MuscleSpring s = new MuscleSpring(a, b);
        s.strength(SPRING_STRENGTH);
        return s;
    }

    private void deleteParticleAndSpring() {
        Particle mSelectedParticle = getParticleCloseToMouse();
        /*  mark particle for deletion */
        if (mSelectedParticle != null) {
            mSelectedParticle.dead(true);
        }
        /* find connected spring and mark for deletion */
        for (IForce f : mPhysics.forces()) {
            if (f instanceof MuscleSpring) {
                MuscleSpring s = (MuscleSpring) f;
                if (s.a() == mSelectedParticle || s.b() == mSelectedParticle) {
                    s.dead(true);
                }
            }
        }
        /* if simulation is paused deletion must be triggered manually */
        if (mPauseSimulation) {
            mPhysics.purge();
        }
    }

    private void drawHighlightParticleNearby() {
        Particle mSelectedParticle = getParticleCloseToMouse();
        if (mSelectedParticle != null) {
            stroke(0);
            noFill();
            ellipse(mSelectedParticle.position().x,
                    mSelectedParticle.position().y,
                    SELECTION_RADIUS * 2,
                    SELECTION_RADIUS * 2);
        }
    }

    private void drawParticlesSprings() {
        /* draw connecting springs */
        stroke(0);
        noFill();
        for (IForce f : mPhysics.forces()) {
            if (f instanceof MuscleSpring) {
                MuscleSpring s = (MuscleSpring) f;
                line(s.a().position().x, s.a().position().y, s.b().position().x, s.b().position().y);
            }
        }
        /* draw particles */
        noStroke();
        fill(0);
        for (Particle p : mPhysics.particles()) {
            if (p.fixed()) {
                rect(p.position().x - 5f, p.position().y - 5f, 10, 10);
            } else {
                ellipse(p.position().x, p.position().y, 5, 5);
            }
        }
    }

    private void drawSimulationState() {
        noStroke();
        fill(0);
        if (mPauseSimulation) {
            rect(10, 10, 30, 30);
        } else {
            ellipse(25, 25, 30, 30);
        }
    }

    private void drawTemporaryParticleSpring() {
        /* draw temporary particle */
        if (mTemporaryParticle != null) {
            noStroke();
            fill(0);
            ellipse(mTemporaryParticle.position().x, mTemporaryParticle.position().y, 10, 10);
        }

        /* draw temporary spring */
        if (mTemporarySpring != null) {
            stroke(0);
            noFill();
            strokeWeight(3.0f);
            line(mTemporarySpring.a().position().x,
                 mTemporarySpring.a().position().y,
                 mTemporarySpring.b().position().x,
                 mTemporarySpring.b().position().y);
            strokeWeight(1.0f);
        }
    }

    private void endCreateParticleSpring() {
        Particle mSelectedParticle = getParticleCloseToMouse();
        /* add temporary particle */
        if (mTemporaryParticle != null) {
            if (mSelectedParticle == null) {
                mPhysics.add(mTemporaryParticle, true);
            } else {
                if (mTemporarySpring != null) {
                    mTemporarySpring.b(mSelectedParticle);
                }
            }
            updateConnectedSprings(mTemporaryParticle);
            mTemporaryParticle = null;
        }
        /* add temporary spring */
        if (mTemporarySpring != null) {
            if (mTemporarySpring.a() != mTemporarySpring.b()) {
                mPhysics.add(mTemporarySpring, true);
            }
            mTemporarySpring.restlength(PVector.dist(mTemporarySpring.a().position(), mTemporarySpring.b().position()));
            /* set contraction length */
            mTemporarySpring.amplitude(mTemporarySpring.restlength() * MUSCLE_CONTRACTION_LENGTH);
            mTemporarySpring.frequency(MUSCLE_CONTRACTION_SPEED);
            mTemporarySpring = null;
        }
        /* update muscle phase */
        for (int i = 0; i < mPhysics.forces().size(); i++) {
            IForce f = mPhysics.forces().get(i);
            if (f instanceof MuscleSpring) {
                MuscleSpring s = (MuscleSpring) f;
                float mPhaseShift = (float) i / (mPhysics.forces().size() - 1);
                s.phaseshift(mPhaseShift * MUSCLE_PHASE_SHIFT_SCALE);
            }
        }
    }

    private Particle getParticleCloseToMouse() {
        return teilchen.util.Util.findParticleByProximity(mPhysics.particles(), mouseX, mouseY, 0, SELECTION_RADIUS);
    }

    private void invertGravity() {
        mGravity.force().y *= -1;
    }

    private void toggleParticleFixed() {
        Particle mSelectedParticle = getParticleCloseToMouse();
        /*  toggle particle fixed */
        if (mSelectedParticle != null) {
            mSelectedParticle.fixed(!mSelectedParticle.fixed());
        }
    }

    private void updateConnectedSprings(Particle mTemporaryParticle) {
        // @TODO("update length of connected springs")
        for (IForce f : mPhysics.forces()) {
            if (f instanceof MuscleSpring) {
                MuscleSpring s = (MuscleSpring) f;
                if (s.a() == mTemporaryParticle || s.b() == mTemporaryParticle) {
                    s.setRestLengthByPosition();
                }
            }
        }
    }

    public static void main(String[] args) {
        PApplet.main(SketchLessonX11_MuscleSprings.class.getName());
    }
}
