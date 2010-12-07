/*
 * Particles
 *
 * Copyright (C) 2010
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


package teilchen.test.particle;


import mathematik.Random;
import mathematik.Vector3f;

import teilchen.Particle;
import teilchen.Physics;
import teilchen.constraint.Box;
import teilchen.force.Gravity;
import teilchen.force.Spring;
import teilchen.force.ViscousDrag;
import teilchen.util.CollisionManager;
import processing.core.PApplet;


public class TestCollisionManagerArbitrarySizes
    extends PApplet {

    private CollisionManager _myCollisionManager;

    private Physics _myPhysics;

    public void setup() {
        size(640, 480, OPENGL);
        smooth();
        frameRate(30);
        noFill();

        _myCollisionManager = new CollisionManager();
        _myCollisionManager.distancemode(CollisionManager.DISTANCE_MODE_RADIUS);

        _myPhysics = new Physics();
        _myPhysics.add(new ViscousDrag(0.85f));
        _myPhysics.add(new Gravity());

        Box myBox = new Box();
        myBox.min().set(50, 50, 0);
        myBox.max().set(width - 50, height - 50, 0);
        myBox.coefficientofrestitution(0.5f);
        _myPhysics.add(myBox);
    }


    public void draw() {
        if (mousePressed && mouseButton == LEFT) {
            final Particle myParticle = _myPhysics.makeParticle(new Vector3f(mouseX, mouseY, random(0, 0)), 10);
            myParticle.radius(new Random().getFloat(5.0f, 30.0f));
            myParticle.mass(myParticle.radius() / 15.0f);
            _myCollisionManager.collision().add(myParticle);
        }

        /* fix still particles */
        for (final Particle myParticle : _myPhysics.particles()) {
            if (myParticle.still()) {
                myParticle.fixed(true);
            }
        }

        /* node handler */
        _myCollisionManager.createCollisionResolvers();
        _myCollisionManager.loop(1 / 30f);
        _myPhysics.step(1 / 30f);

        /* draw */
        background(255);
        drawParticles();

        _myCollisionManager.removeCollisionResolver();
    }


    public void keyPressed() {
        switch (key) {
            case ' ':
                _myPhysics.particles().clear();
                _myCollisionManager.collision().particles().clear();
                break;
        }
    }


    private void drawParticles() {
        /* collision springs */
        stroke(0, 64);
        for (int i = 0; i < _myCollisionManager.collision().forces().size(); ++i) {
            if (_myCollisionManager.collision().forces().get(i) instanceof Spring) {
                Spring mySpring = (Spring) _myCollisionManager.collision().forces().get(i);
                line(mySpring.a().position().x, mySpring.a().position().y, mySpring.a().position().z,
                     mySpring.b().position().x, mySpring.b().position().y, mySpring.b().position().z);
            }
        }

        /* particles */
        for (int i = 0; i < _myPhysics.particles().size(); ++i) {
            Particle myParticle = _myPhysics.particles().get(i);
            pushMatrix();
            translate(myParticle.position().x, myParticle.position().y, myParticle.position().z);
            stroke(0, 127, 255, 127);
            ellipse(0, 0,
                    myParticle.radius() * 2,
                    myParticle.radius() * 2);
            stroke(200);
            popMatrix();
        }
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {TestCollisionManagerArbitrarySizes.class.getName()});
    }
}
