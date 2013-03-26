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


package teilchen.test.particle;


import mathematik.Vector3f;

import teilchen.Particle;
import teilchen.Physics;
import teilchen.constraint.Box;
import teilchen.constraint.Stick;
import teilchen.force.Gravity;
import teilchen.force.Spring;
import teilchen.force.ViscousDrag;
import teilchen.util.CollisionManager;
import teilchen.util.CollisionManager.ResolverType;
import processing.core.PApplet;


public class TestCollisionManager
        extends PApplet {

    private static final float PARTICLE_SIZE = 10;

    private Particle _mySpringParticleA;

    private Particle _mySpringParticleB;

    private CollisionManager _myCollisionManager;

    private Physics _myPhysics;

    public void setup() {
        size(640, 480, OPENGL);
        smooth();
        frameRate(30);
        noFill();
        ellipseMode(CENTER);

        _myCollisionManager = new CollisionManager();
        _myCollisionManager.distancemode(CollisionManager.DISTANCE_MODE_FIXED);
        _myCollisionManager.minimumDistance(50);

        _myPhysics = new Physics();
        _myPhysics.add(new ViscousDrag(0.9f));
        _myPhysics.add(new Gravity());

        Box myBox = new Box();
        myBox.min().set(50, 50, 0);
        myBox.max().set(width - 50, height - 50, 0);
        myBox.coefficientofrestitution(0.7f);
        myBox.reflect(true);
        _myPhysics.add(myBox);
    }

    public void draw() {
        if (mousePressed) {
            if (mouseButton == LEFT) {
                final Particle myParticle = _myPhysics.makeParticle(new Vector3f(mouseX, mouseY, 0), 10);
                _myCollisionManager.collision().add(myParticle);
                _mySpringParticleA = null;
                _mySpringParticleB = null;
            }
            if (mouseButton == RIGHT) {
                Particle myCurrentParticle = withinParticle(mouseX, mouseY);
                if (myCurrentParticle != null) {
                    if (_mySpringParticleA == null) {
                        _mySpringParticleA = myCurrentParticle;
                    } else if (_mySpringParticleB == null && myCurrentParticle != _mySpringParticleA) {
                        _mySpringParticleB = myCurrentParticle;
                    }
                    if (_mySpringParticleA != null && _mySpringParticleB != null) {
                        Spring mySpring = _myPhysics.makeSpring(_mySpringParticleA,
                                                                _mySpringParticleB,
                                                                50, 50);
                        mySpring.restlength(70);
                        _mySpringParticleA = null;
                        _mySpringParticleB = null;
                    }
                }
            }
        }

        if (keyPressed) {
            if (key == ' ') {
                _myPhysics.particles().clear();
                _myCollisionManager.collision().particles().clear();
            }
            if (key == 's') {
                _myCollisionManager.setResolverType(ResolverType.SPRING);
            }
            if (key == 't') {
                _myCollisionManager.setResolverType(ResolverType.STICK);
            }
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

    private Particle withinParticle(int theX, int theY) {
        for (int i = 0; i < _myPhysics.particles().size(); ++i) {
            Particle myParticle = _myPhysics.particles().get(i);
            if (myParticle.position().x + PARTICLE_SIZE > theX
                    && myParticle.position().x - PARTICLE_SIZE < theX
                    && myParticle.position().y + PARTICLE_SIZE > theY
                    && myParticle.position().y - PARTICLE_SIZE < theY) {
                return myParticle;
            }
        }
        return null;
    }

    private void drawParticles() {
        /* springs */
        noFill();
        stroke(255, 0, 0, 191);

        for (int i = 0; i < _myPhysics.forces().size(); ++i) {
            if (_myPhysics.forces().get(i) instanceof Spring) {
                Spring mySpring = (Spring)_myPhysics.forces().get(i);
                line(mySpring.a().position().x, mySpring.a().position().y, mySpring.a().position().z,
                     mySpring.b().position().x, mySpring.b().position().y, mySpring.b().position().z);
            }
        }

        /* collision springs */
        noFill();
        stroke(0, 0, 0, 100);
        for (int i = 0; i < _myCollisionManager.collision().forces().size(); ++i) {
            if (_myCollisionManager.collision().forces().get(i) instanceof Spring) {
                Spring mySpring = (Spring)_myCollisionManager.collision().forces().get(i);
                line(mySpring.a().position().x, mySpring.a().position().y, mySpring.a().position().z,
                     mySpring.b().position().x, mySpring.b().position().y, mySpring.b().position().z);
            }
        }
        for (int i = 0; i < _myCollisionManager.collision().constraints().size(); ++i) {
            if (_myCollisionManager.collision().constraints().get(i) instanceof Stick) {
                Stick mySpring = (Stick)_myCollisionManager.collision().constraints().get(i);
                line(mySpring.a().position().x, mySpring.a().position().y, mySpring.a().position().z,
                     mySpring.b().position().x, mySpring.b().position().y, mySpring.b().position().z);
            }
        }

        /* particles */
        for (int i = 0; i < _myPhysics.particles().size(); ++i) {
            Particle myParticle = _myPhysics.particles().get(i);
            fill(255);
            pushMatrix();
            translate(myParticle.position().x, myParticle.position().y, myParticle.position().z);
            stroke(127);
            ellipse(0, 0,
                    PARTICLE_SIZE,
                    PARTICLE_SIZE);
            stroke(200);
            noFill();
            ellipse(0, 0,
                    PARTICLE_SIZE / 2,
                    PARTICLE_SIZE / 2);
            popMatrix();
        }

        /* selected */
        fill(255, 0, 0);
        noStroke();
        if (_mySpringParticleA != null) {
            pushMatrix();
            translate(_mySpringParticleA.position().x, _mySpringParticleA.position().y, _mySpringParticleA.position().z);
            ellipse(0, 0,
                    PARTICLE_SIZE,
                    PARTICLE_SIZE);
            popMatrix();
        }
        if (_mySpringParticleB != null) {
            pushMatrix();
            translate(_mySpringParticleB.position().x, _mySpringParticleB.position().y, _mySpringParticleB.position().z);
            ellipse(0, 0,
                    PARTICLE_SIZE,
                    PARTICLE_SIZE);
            popMatrix();
        }
    }

    public static void main(String[] args) {
        PApplet.main(new String[] {TestCollisionManager.class.getName()});
    }
}
