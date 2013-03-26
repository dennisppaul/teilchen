/*
 * Teilchen
 *
 * Copyright (C) 2013
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * {@link http://www.gnu.org/licenses/lgpl.html}
 *
 */
package teilchen.demo;


import processing.core.PApplet;
import teilchen.BehaviorParticle;
import teilchen.Physics;
import teilchen.behavior.Arrival;
import teilchen.force.Spring;
import teilchen.force.ViscousDrag;
import teilchen.util.CollisionManager;

import java.util.Vector;


/**
 * this demo shows how to add behaviors to particles. in this example the
 * arrival behavior.
 */
public class LessonX06_Ducklings
        extends PApplet {

    private Physics mPhysics;

    private Vector<Duckling> mDucklings;

    private CollisionManager mCollision;

    public void setup() {
        size(640, 480, OPENGL);
        frameRate(60);
        smooth();
        colorMode(RGB, 1.0f);

        /* physics */
        mPhysics = new Physics();

        ViscousDrag myViscousDrag = new ViscousDrag();
        myViscousDrag.coefficient = 0.25f;
        mPhysics.add(myViscousDrag);

        mCollision = new CollisionManager();
        mCollision.minimumDistance(25);

        /* ducklings */
        mDucklings = new Vector<Duckling>();
        for (int i = 0; i < 13; i++) {
            final Duckling mDuckling = new Duckling();
            if (!mDucklings.isEmpty()) {
                mDuckling.arrival.setPositionRef(mDucklings.lastElement().particle.position());
            }
            mCollision.collision().add(mDuckling.particle);
            mDucklings.add(mDuckling);
        }
    }

    public void draw() {
        final float mDeltaTime = 1.0f / frameRate;
        background(1);

        /* update particles */
        mCollision.createCollisionResolvers();
        mCollision.loop(mDeltaTime);
        mPhysics.step(mDeltaTime);

        drawCollisionSprings();
        mCollision.removeCollisionResolver();

        mDucklings.firstElement().arrival.oversteer(!mousePressed);
        mDucklings.firstElement().arrival.position().set(mouseX, mouseY);

        /* draw */
        for (Duckling mDuckling : mDucklings) {
            drawParticle(mDuckling);
        }

        /* draw arrival */
        stroke(0, 0.25f);
        noFill();
        ellipse(mDucklings.firstElement().arrival.position().x,
                mDucklings.firstElement().arrival.position().y,
                20, 20);
    }

    private void drawParticle(Duckling pDuckling) {
        final BehaviorParticle mParticle = pDuckling.particle;
        final Arrival mArrival = pDuckling.arrival;

        /* draw particle */
        stroke(0, 0.5f);
        noFill();
        if (mArrival.arriving()) {
            stroke(1, 0, 0, 0.5f);
        }
        if (mArrival.arrived()) {
            stroke(0, 1, 0, 0.5f);
        }
        ellipse(mParticle.position().x, mParticle.position().y,
                mParticle.radius() * 2, mParticle.radius() * 2);

        /* - */
        pushMatrix();
        translate(mParticle.position().x,
                  mParticle.position().y);

        /* draw velocity */
        stroke(1, 0, 0, 0.5f);
        line(0, 0, mParticle.velocity().x, mParticle.velocity().y);

        /* draw break force */
        stroke(0, 0.5f, 1, 0.5f);
        line(0, 0, mArrival.force().x, mArrival.force().y);

        /* - */
        popMatrix();
    }

    private void drawCollisionSprings() {
        stroke(0, 1, 0, 0.25f);
        for (int i = 0; i < mCollision.collision().forces().size(); ++i) {
            if (mCollision.collision().forces().get(i) instanceof Spring) {
                Spring mySpring = (Spring) mCollision.collision_forces().get(i);
                line(mySpring.a().position().x, mySpring.a().position().y, mySpring.a().position().z,
                     mySpring.b().position().x, mySpring.b().position().y, mySpring.b().position().z);
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
        PApplet.main(new String[]{LessonX06_Ducklings.class.getName()});
    }
}
