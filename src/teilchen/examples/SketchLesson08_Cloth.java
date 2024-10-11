package teilchen.examples;

import processing.core.PApplet;
import processing.core.PVector;
import teilchen.IParticle;
import teilchen.Physics;
import teilchen.constraint.IConstraint;
import teilchen.constraint.Stick;
import teilchen.force.Attractor;
import teilchen.force.Gravity;
import teilchen.integration.Verlet;

public class SketchLesson08_Cloth extends PApplet {

    /*
     * this sketch demonstrate how to use particles and sticks to emulate a piece of cloth.
     * note that a similar effect can also be achieved with springs, however the result is
     * slightly more *bouncy*.
     *
     * press mouse to wrinkle cloth.
     */

    private static final int GRID_HEIGHT = 32;
    private static final int GRID_WIDTH = 32;
    private Attractor mAttractor;
    private final float mAttractorStrength = 15000;
    private Physics mPhysics;

    public void settings() {
        size(640, 480, P3D);
    }

    public void setup() {
        mPhysics = new Physics();

        Verlet mVerlet = new Verlet();
        mVerlet.damping(0.9f);
        mPhysics.setIntegratorRef(mVerlet);
        mPhysics.add(new Gravity(new PVector(0, 1000f, 0)));

        mAttractor = new Attractor();
        mAttractor.strength(-mAttractorStrength);
        mAttractor.radius(300);
        mPhysics.add(mAttractor);

        IParticle[][] mParticles = new IParticle[GRID_WIDTH][GRID_HEIGHT];

        /* setup cloth */
        float mGridStepX = ((float) width / GRID_WIDTH);
        float mGridStepY = ((float) height / GRID_HEIGHT);
        /* setup particles */
        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                mParticles[x][y] = mPhysics.makeParticle();
                mParticles[x][y].position().set((x + 0.5f) * mGridStepX, y * mGridStepY, random(0, 1));
                mParticles[x][y].old_position().set(mParticles[x][y].position());
                mParticles[x][y].mass(0.1f);
            }
        }
        /* setup connections */
        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                final float DAMPING = 0.9f;
                if (y > 0) {
                    Stick mStick = new Stick(mParticles[x][y - 1], mParticles[x][y], mGridStepY);
                    mStick.damping(DAMPING);
                    mPhysics.add(mStick);
                }
                if (x > 0) {
                    Stick mStick = new Stick(mParticles[x - 1][y], mParticles[x][y], mGridStepX);
                    mStick.damping(DAMPING);
                    mPhysics.add(mStick);
                }
                if (x > 0 && y > 0) {
                    Stick mStickA = new Stick(mParticles[x - 1][y - 1],
                                              mParticles[x][y],
                                              new PVector(mGridStepX, mGridStepY).mag());
                    mPhysics.add(mStickA);
                    Stick mStickB = new Stick(mParticles[x][y - 1],
                                              mParticles[x - 1][y],
                                              new PVector(mGridStepX, mGridStepY).mag());
                    mPhysics.add(mStickB);
                }
            }
        }

        /* fix first row */
        for (IParticle[] mParticle : mParticles) {
            mParticle[0].fixed(true);
        }
    }

    public void draw() {
        /* update */
        if (mousePressed) {
            mAttractor.strength(mAttractorStrength);
        } else {
            mAttractor.strength(-mAttractorStrength);
        }
        mAttractor.position().set(mouseX, mouseY, 50);
        mPhysics.step(1.0f / frameRate, 5);

        background(255);

        /* draw sticks */
        stroke(0, 191);
        for (final IConstraint mIConstraint : mPhysics.constraints()) {
            if (mIConstraint instanceof Stick) {
                final Stick mStick = (Stick) mIConstraint;
                line(mStick.a().position().x,
                     mStick.a().position().y,
                     mStick.a().position().z,
                     mStick.b().position().x,
                     mStick.b().position().y,
                     mStick.b().position().z);
            }
        }
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{SketchLesson08_Cloth.class.getName()});
    }
}
