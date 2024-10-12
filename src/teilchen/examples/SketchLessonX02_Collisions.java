package teilchen.examples;

import processing.core.PApplet;
import processing.core.PVector;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.constraint.Box;
import teilchen.force.Gravity;
import teilchen.force.Spring;
import teilchen.force.ViscousDrag;
import teilchen.util.CollisionManager;

public class SketchLessonX02_Collisions extends PApplet {

    /*
     * this sketch demonstrates how to use `CollisionManager` to resolve particle collisions by
     * applying temporary springs pushing 2 colliding particles appart.
     *
     * press mouse to create particles.
     */

    private static final float PARTICLE_SIZE = 5;
    private CollisionManager mCollision;
    private Physics mPhysics;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        noFill();
        ellipseMode(CENTER);

        mCollision = new CollisionManager();
        mCollision.distancemode(CollisionManager.DISTANCE_MODE_FIXED);
        mCollision.minimumDistance(50);

        mPhysics = new Physics();
        mPhysics.add(new ViscousDrag(0.85f));
        mPhysics.add(new Gravity());

        Box mBox = new Box();
        mBox.min().set(50, 50, 0);
        mBox.max().set(width - 50, height - 50, 0);
        mBox.coefficientofrestitution(0.7f);
        mBox.reflect(true);
        mPhysics.add(mBox);
    }

    public void draw() {
        /* create particles */
        if (mousePressed) {
            final Particle mParticle = mPhysics.makeParticle(new PVector(mouseX, mouseY, 0), 10);
            mCollision.collision().add(mParticle);
        }

        /* collision handler */
        final float mDeltaTime = 1.0f / frameRate;
        mCollision.createCollisionResolvers();
        mCollision.loop(mDeltaTime);
        mPhysics.step(mDeltaTime);

        /* draw */
        background(255);
        drawThings();

        mCollision.removeCollisionResolver();
    }

    private void drawThings() {
        /* collision springs */
        noFill();
        stroke(0, 63);
        for (int i = 0; i < mCollision.collision().forces().size(); ++i) {
            if (mCollision.collision().forces().get(i) instanceof Spring) {
                Spring mSpring = (Spring) mCollision.collision_forces().get(i);
                line(mSpring.a().position().x,
                     mSpring.a().position().y,
                     mSpring.b().position().x,
                     mSpring.b().position().y);
            }
        }

        /* particles */
        noStroke();
        fill(0);
        for (int i = 0; i < mPhysics.particles().size(); ++i) {
            Particle mParticle = mPhysics.particles().get(i);
            final float mCollisionScale = mParticle.tagged() ? 1.0f : 2.0f;
            mParticle.tag(false);
            pushMatrix();
            translate(mParticle.position().x, mParticle.position().y);
            ellipse(0, 0, PARTICLE_SIZE * mCollisionScale, PARTICLE_SIZE * mCollisionScale);
            popMatrix();
        }
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{SketchLessonX02_Collisions.class.getName()});
    }
}
