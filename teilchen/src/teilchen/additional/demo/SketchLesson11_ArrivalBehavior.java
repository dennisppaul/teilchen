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


/**
 * this sketch shows how to assign an 'arrival' behavior to a particle.
 */
public class Lesson11_ArrivalBehavior
        extends PApplet {

    private Physics mPhysics;

    private BehaviorParticle mParticle;

    private Arrival mArrival;

    public void setup() {
        size(640, 480, OPENGL);
        smooth();
        frameRate(120);
        colorMode(RGB, 1.0f);
        noFill();

        /* physics */
        mPhysics = new Physics();

        /* create particles */
        mParticle = mPhysics.makeParticle(BehaviorParticle.class);
        mParticle.maximumInnerForce(100);

        /* create behavior */
        mArrival = new Arrival();
        mArrival.breakforce(mParticle.maximumInnerForce() * 0.25f);
        mArrival.breakradius(mParticle.maximumInnerForce() * 0.25f);
        mParticle.behaviors().add(mArrival);
    }

    public void draw() {

        /* set the arrival position to the mouse position */
        mArrival.position().set(mouseX, mouseY);

        /* update particle system */
        mPhysics.step(1.0f / frameRate);

        /* draw behavior particle */
        background(1);
        stroke(0, 0.5f);
        if (mArrival.arriving()) {
            /* color particle red while it is arriving */
            stroke(1, 0, 0, 0.5f);
        }
        if (mArrival.arrived()) {
            /* color particle green when it has arrived */
            stroke(0, 1, 0, 0.5f);
        }

        line(mParticle.position().x,
             mParticle.position().y,
             mParticle.position().x + mParticle.velocity().x,
             mParticle.position().y + mParticle.velocity().y);
        fill(1);
        ellipse(mParticle.position().x, mParticle.position().y, 12, 12);

        /* draw arrival */
        stroke(0, 0.25f);
        noFill();
        ellipse(mArrival.position().x,
                mArrival.position().y,
                mArrival.breakradius() * 2,
                mArrival.breakradius() * 2);
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{Lesson11_ArrivalBehavior.class.getName()});
    }
}
