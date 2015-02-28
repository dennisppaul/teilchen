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


import mathematik.Vector3f;
import processing.core.PApplet;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.constraint.Box;
import teilchen.force.Gravity;
import teilchen.force.Spring;
import teilchen.force.ViscousDrag;
import teilchen.util.CollisionManager;


public class LessonX02_Collisions
        extends PApplet {

    private static final float PARTICLE_SIZE = 12;

    private CollisionManager mCollision;

    private Physics mPhysics;

    public void setup() {
        size(640, 480, OPENGL);
        smooth();
        frameRate(30);
        noFill();
        ellipseMode(CENTER);

        mCollision = new CollisionManager();
        mCollision.distancemode(CollisionManager.DISTANCE_MODE_FIXED);
        mCollision.minimumDistance(50);

        mPhysics = new Physics();
        mPhysics.add(new ViscousDrag(0.85f));
        mPhysics.add(new Gravity());

        Box myBox = new Box();
        myBox.min().set(50, 50, 0);
        myBox.max().set(width - 50, height - 50, 0);
        myBox.coefficientofrestitution(0.7f);
        myBox.reflect(true);
        mPhysics.add(myBox);

        /* create a first particle */
        final Particle myParticle = mPhysics.makeParticle(new Vector3f(mouseX, mouseY, 0), 10);
        mCollision.collision().add(myParticle);
    }

    public void draw() {
        /* create particles */
        if (mousePressed) {
            final Particle myParticle = mPhysics.makeParticle(new Vector3f(mouseX, mouseY, 0), 10);
            mCollision.collision().add(myParticle);
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
        stroke(255, 0, 127, 64);
        for (int i = 0; i < mCollision.collision().forces().size(); ++i) {
            if (mCollision.collision().forces().get(i) instanceof Spring) {
                Spring mySpring = (Spring) mCollision.collision_forces().get(i);
                line(mySpring.a().position().x, mySpring.a().position().y, mySpring.a().position().z,
                     mySpring.b().position().x, mySpring.b().position().y, mySpring.b().position().z);
            }
        }

        /* particles */
        fill(245);
        stroke(164);
        for (int i = 0; i < mPhysics.particles().size(); ++i) {
            Particle myParticle = mPhysics.particles().get(i);
            pushMatrix();
            translate(myParticle.position().x, myParticle.position().y, myParticle.position().z);
            ellipse(0, 0,
                    PARTICLE_SIZE,
                    PARTICLE_SIZE);
            popMatrix();
        }
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{LessonX02_Collisions.class.getName()});
    }
}
