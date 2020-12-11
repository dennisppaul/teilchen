package teilchen.examples;

import processing.core.PApplet;
import teilchen.BehaviorParticle;
import teilchen.Physics;
import teilchen.behavior.Arrival;
import teilchen.force.Spring;
import teilchen.force.ViscousDrag;
import teilchen.util.CollisionManager;

import java.util.ArrayList;

public class SketchLessonX06_Ducklings extends PApplet {

    /*
     * this sketch demonstrates how to use `Arrival` behaviors with particles to create a group of
     * ducklings.
     *
     * move mouse to change target position. press mouse to set to *over steering*.
     */

    private Physics mPhysics;
    private ArrayList<Duckling> mDucklings;
    private CollisionManager mCollision;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        colorMode(RGB, 1.0f);

        /* physics */
        mPhysics = new Physics();

        ViscousDrag mViscousDrag = new ViscousDrag();
        mViscousDrag.coefficient = 0.25f;
        mPhysics.add(mViscousDrag);

        mCollision = new CollisionManager();
        mCollision.minimumDistance(25);

        /* ducklings */
        mDucklings = new ArrayList<Duckling>();
        for (int i = 0; i < 13; i++) {
            final Duckling mDuckling = new Duckling();
            if (!mDucklings.isEmpty()) {
                mDuckling.arrival.setPositionRef(mDucklings.get(mDucklings.size() - 1).particle.position());
            }
            mCollision.collision().add(mDuckling.particle);
            mDucklings.add(mDuckling);
        }
    }

    public void draw() {
        final float mDeltaTime = 1.0f / frameRate;

        /* update particles */
        mCollision.createCollisionResolvers();
        mCollision.loop(mDeltaTime);
        mPhysics.step(mDeltaTime);

        mDucklings.get(0).arrival.oversteer(!mousePressed);
        mDucklings.get(0).arrival.position().set(mouseX, mouseY);

        /* draw */
        background(1);

        drawCollisionSprings();
        for (Duckling mDuckling : mDucklings) {
            drawParticle(mDuckling);
        }

        /* draw arrival */
        stroke(0, 0.75f);
        noFill();
        ellipse(mDucklings.get(0).arrival.position().x,
                mDucklings.get(0).arrival.position().y,
                50,
                50);

        /* clean up */
        mCollision.removeCollisionResolver();
    }

    private void drawParticle(Duckling pDuckling) {
        final BehaviorParticle mParticle = pDuckling.particle;
        final Arrival mArrival = pDuckling.arrival;

        /* draw particle */
        stroke(0, 0.75f);
        noFill();
        if (mArrival.arriving()) {
            ellipse(mParticle.position().x, mParticle.position().y, mParticle.radius() * 4, mParticle.radius() * 4);
        }
        if (mArrival.arrived()) {
            ellipse(mParticle.position().x, mParticle.position().y, mParticle.radius() * 3, mParticle.radius() * 3);
        }
        fill(0);
        noStroke();
        ellipse(mParticle.position().x, mParticle.position().y, mParticle.radius() * 2, mParticle.radius() * 2);

        /* - */
        pushMatrix();
        translate(mParticle.position().x, mParticle.position().y);

        /* draw velocity */
        stroke(0, 0.75f);
        line(0, 0, mParticle.velocity().x, mParticle.velocity().y);

        /* draw break force */
        stroke(0, 0.5f);
        line(0, 0, mArrival.force().x, mArrival.force().y);

        /* - */
        popMatrix();
    }

    private void drawCollisionSprings() {
        stroke(0, 0.5f);
        for (int i = 0; i < mCollision.collision().forces().size(); ++i) {
            if (mCollision.collision().forces().get(i) instanceof Spring) {
                Spring mSpring = (Spring) mCollision.collision_forces().get(i);
                line(mSpring.a().position().x,
                     mSpring.a().position().y,
                     mSpring.b().position().x,
                     mSpring.b().position().y);
            }
        }
    }

    class Duckling {

        BehaviorParticle particle;

        Arrival arrival;

        Duckling() {
            /* create particles */
            particle = mPhysics.makeParticle(BehaviorParticle.class);
            particle.position().set(random(width), random(height));
            particle.maximumInnerForce(random(50, 150));
            particle.radius(random(6, 10));

            arrival = new Arrival();
            arrival.breakforce(random(12, 28));
            arrival.breakradius(random(45, 55));

            particle.behaviors().add(arrival);
        }
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{SketchLessonX06_Ducklings.class.getName()});
    }
}
