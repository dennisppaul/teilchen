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
import teilchen.behavior.Motor;
import teilchen.behavior.Wander;
import teilchen.force.ViscousDrag;

import static processing.core.PConstants.OPENGL;
import static processing.core.PConstants.RGB;


/**
 * this sketch shows how to assign an 'wander' behavior to a particle.
 */
public class Lesson10_WanderBehavior
        extends PApplet {

    private Physics mPhysics;

    private BehaviorParticle mParticle;

    private Wander mWander;

    private Motor mMotor;

    public void setup() {
        size(640, 480, OPENGL);
        smooth();
        frameRate(120);

        /* physics */
        mPhysics = new Physics();
        mPhysics.add(new ViscousDrag());

        /* create particles */
        mParticle = mPhysics.makeParticle(BehaviorParticle.class);
        mParticle.position().set(width / 2, height / 2);
        mParticle.maximumInnerForce(100);
        mParticle.radius(10);

        /* create behavior */
        mWander = new Wander();
        mParticle.behaviors().add(mWander);

        /* a motor is required to push the particle forward - wander manipulats the direction the particle is pushed in */
        mMotor = new Motor();
        mMotor.auto_update_direction(true); /* the direction the motor pushes into is each step automatically set to the velocity */
        mMotor.strength(25);
        mParticle.behaviors().add(mMotor);
    }

    public void draw() {
        /* update particle system */
        mPhysics.step(1.0f / frameRate);

        /* draw behavior particle */
        background(255);

        fill(1);
        stroke(0, 127);
        line(mParticle.position().x,
             mParticle.position().y,
             mParticle.position().x + mParticle.velocity().x,
             mParticle.position().y + mParticle.velocity().y);
        ellipse(mParticle.position().x, mParticle.position().y,
                mParticle.radius() * 2, mParticle.radius() * 2);
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{Lesson10_WanderBehavior.class.getName()});
    }
}
