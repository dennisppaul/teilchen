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


package teilchen.test.particle.behavior;


import java.util.Vector;

import teilchen.BehaviorParticle;
import teilchen.Physics;
import teilchen.behavior.Seek;
import teilchen.behavior.Wander;
import teilchen.force.Attractor;
import teilchen.force.Gravity;
import teilchen.force.IForce;
import teilchen.force.ViscousDrag;
import processing.core.PApplet;


public class TestBehaviorParticle
    extends PApplet {

    private Physics _myParticleSystem;

    private Vector<MyBehaviorParticle> _myParticles;

    public void setup() {
        size(640, 480, OPENGL);
        frameRate(60);
        colorMode(RGB, 1.0f);
        noFill();
        smooth();

        /* physics */
        _myParticleSystem = new Physics();

        ViscousDrag myViscousDrag = new ViscousDrag();
        myViscousDrag.coefficient = 0.1f;
        _myParticleSystem.add(myViscousDrag);

        Gravity myGravity = new Gravity();
        myGravity.force().y = 5;
        _myParticleSystem.add(myGravity);

        for (int i = 0; i < 6; i++) {
            Attractor _myForceSphere = new Attractor();
            _myForceSphere.radius(random(75, 100));
            _myForceSphere.strength( -500);
            _myForceSphere.position().set(random(width), random(height));
            _myParticleSystem.add(_myForceSphere);
        }

        /* create particles */
        _myParticles = new Vector<MyBehaviorParticle> ();
        for (int i = 0; i < 100; i++) {
            MyBehaviorParticle myBehaviorParticle = new MyBehaviorParticle();
            myBehaviorParticle.particle = _myParticleSystem.makeParticle(BehaviorParticle.class);
            _myParticles.add(myBehaviorParticle);

            myBehaviorParticle.seek = new Seek();
            myBehaviorParticle.wander = new Wander();
            myBehaviorParticle.particle.behaviors().add(myBehaviorParticle.seek);
            myBehaviorParticle.particle.behaviors().add(myBehaviorParticle.wander);
            myBehaviorParticle.seek.weight(0.75f);
            myBehaviorParticle.wander.weight(0.25f);
            myBehaviorParticle.wander.steeringstrength(20);

            myBehaviorParticle.particle.position().set(random(width), random(height));
        }
    }


    public void draw() {
        /* update particles */
        _myParticleSystem.step(1f / 60);

        /* draw particles */
        background(1);
        for (MyBehaviorParticle myParticle : _myParticles) {
            myParticle.seek.position().set(mouseX, mouseY, 0);

            float myDistance = myParticle.seek.position().distance(myParticle.particle.position());
            if (myDistance < 100) {
                myParticle.seek.weight(1 - myDistance / 100);
            } else {
                myParticle.seek.weight(0);
            }

            if (myParticle.particle.position().x < 0) {
                myParticle.particle.position().x = width;
            }
            if (myParticle.particle.position().x > width) {
                myParticle.particle.position().x = 0;
            }
            if (myParticle.particle.position().y < 0) {
                myParticle.particle.position().y = height;
            }
            if (myParticle.particle.position().y > height) {
                myParticle.particle.position().y = 0;
            }

            /* draw particle */
            stroke(0, 0.5f);
            ellipse(myParticle.particle.position().x, myParticle.particle.position().y, 20, 20);
            line(myParticle.particle.position().x,
                 myParticle.particle.position().y,
                 myParticle.particle.position().x + myParticle.particle.velocity().x * 1,
                 myParticle.particle.position().y + myParticle.particle.velocity().y * 1);
        }

        /* draw force */
        stroke(0, 0.25f);
        for (IForce myForce : _myParticleSystem.forces()) {
            if (myForce instanceof Attractor) {
                Attractor myAttractor = (Attractor) myForce;
                ellipse(myAttractor.position().x, myAttractor.position().y,
                        myAttractor.radius() * 2, myAttractor.radius() * 2);
            }
        }
        if (_myRecording) {
            saveFrame(getClass().getSimpleName() + "/" + getClass().getSimpleName() + "-####.tga");
        }
    }


    private boolean _myRecording = true;

    public void keyPressed() {
        switch (key) {
            case ' ':
                _myRecording = !_myRecording;
                if (_myRecording) {
                    System.out.println("### start recording");
                } else {
                    System.out.println("### stop recording");
                }
                break;
        }
    }


    class MyBehaviorParticle {

        Seek seek;

        Wander wander;

        BehaviorParticle particle;
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {TestBehaviorParticle.class.getName()});
    }
}
