/*
 * Particles
 *
 * Copyright (C) 2012
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


import gestalt.processing.G5;

import mathematik.Vector3f;

import teilchen.Particle;
import teilchen.Physics;
import teilchen.constraint.IConstraint;
import teilchen.constraint.Stick;
import teilchen.force.Attractor;
import teilchen.force.Gravity;
import teilchen.integration.Verlet;
import processing.core.PApplet;


public class TestCloth
    extends PApplet {

    private Physics _myParticleSystem;

    private Particle[][] _myParticles;

    private int[] _myGridSize = new int[] {64, 24};

    private Vector3f[][] _myNormals;

    private boolean DRAW_STICKS = false;

    private float _myCounter;

    public void setup() {
        size(1024, 768, OPENGL);
        frameRate(60);

        G5.setup(this, false);
        G5.gestalt().framesetup().depthbufferclearing = false;
        G5.gestalt().framesetup().colorbufferclearing = false;
        G5.fpscounter(true).display().color.set(1);

        _myParticleSystem = new Physics();
        _myParticleSystem.contraint_iterations_per_steps = 3;
        _myParticleSystem.HINT_OPTIMIZE_STILL = false;
        _myParticleSystem.HINT_RECOVER_NAN = false;
        _myParticleSystem.HINT_REMOVE_DEAD = false;

        Verlet myVerlet = new Verlet();
        myVerlet.damping(0.9f);
        _myParticleSystem.setInegratorRef(myVerlet);
        _myParticleSystem.add(new Gravity(new Vector3f(0, 1000f, 0)));
        _myParticleSystem.add(new MyMouseForce());

        float _myGridStepX = (float) ( (float) width / _myGridSize[0]);
        float _myGridStepY = (float) ( (height * 0.5f) / _myGridSize[1]);
        _myParticles = new Particle[_myGridSize[0]][_myGridSize[1]];

        for (int y = 0; y < _myGridSize[1]; y++) {
            for (int x = 0; x < _myGridSize[0]; x++) {
                _myParticles[x][y] = _myParticleSystem.makeParticle();
                _myParticles[x][y].position().set(x * _myGridStepX + _myGridStepX / 2,
                                                  y * _myGridStepY,
                                                  random(0, 1));
                _myParticles[x][y].old_position().set(_myParticles[x][y].position());
                _myParticles[x][y].mass(0.1f);
                final float DAMPING = 0.9f;
                if (y > 0) {
                    Stick myStick = new Stick(_myParticles[x][y - 1],
                                              _myParticles[x][y],
                                              _myGridStepY);
                    myStick.damping(DAMPING);
                    _myParticleSystem.add(myStick);
                }
                if (x > 0) {
                    Stick myStick = new Stick(_myParticles[x - 1][y],
                                              _myParticles[x][y],
                                              _myGridStepX);
                    myStick.damping(DAMPING);
                    _myParticleSystem.add(myStick);
                }
                if (x > 0 && y > 0) {
                    Stick myStick = new Stick(_myParticles[x - 1][y - 1],
                                              _myParticles[x][y],
                                              new Vector3f(_myGridStepX, _myGridStepY).length());
                    _myParticleSystem.add(myStick);
                }
            }
        }

        _myNormals = new Vector3f[_myGridSize[0]][_myGridSize[1]];
        for (int x = 0; x < _myParticles.length; x++) {
            for (int y = 0; y < _myParticles[x].length; y++) {
                _myNormals[x][y] = new Vector3f(0, 0, -1);
            }
        }

        for (int x = 0; x < _myParticles.length; x++) {
            _myParticles[x][0].fixed(true);
        }

        for (int i = 0; i < 10; i++) {
            float mySpeed = random( -50, 50);
            mySpeed += getSign(mySpeed) * 100;
            MyMovingAttractor myMovingAttractor = new MyMovingAttractor(mySpeed);
            myMovingAttractor.position().x = random(width);
            myMovingAttractor.position().y = random(height * 0.5f) + height * 0.5f;
            myMovingAttractor.position().z = random( -10, 10);
            _myParticleSystem.add(myMovingAttractor);
        }
    }


    private float getSign(float theValue) {
        return theValue / abs(theValue);
    }


    public void draw() {
        _myParticleSystem.step(1.0f / 60f);

        _myCounter += 1.0f / frameRate;
        for (int x = 0; x < _myParticles.length; x++) {
            final float myZ = (float) x / _myParticles.length;
            _myParticles[x][0].position().z = sin(myZ * 20 * PI + _myCounter) * 10;
        }

        background(50);

        /* lighting */
        directionalLight(255, 0, 0, 0, -0.8314113f, -0.5556575f);
        ambientLight(0, 0, 0);
        lightSpecular(204, 204, 204);
        pointLight(255, 255, 255,
                   mouseX, mouseY, 100);

        /* draw cloth */
        noStroke();
        fill(255);
        beginShape(TRIANGLES);
        for (int x = 0; x < _myParticles.length; x++) {
            for (int y = 0; y < _myParticles[x].length; y++) {
                if (x < _myParticles.length - 1 && y < _myParticles[x].length - 1) {
                    mathematik.Util.calculateNormal(_myParticles[x][y].position(),
                                                    _myParticles[x + 1][y].position(),
                                                    _myParticles[x + 1][y + 1].position(),
                                                    _myNormals[x][y]);
                    triangle(x, y,
                             x + 1, y,
                             x + 1, y + 1);
                    triangle(x, y,
                             x + 1, y + 1,
                             x, y + 1);
                }
            }
        }
        endShape();

        /* draw sticks */
        DRAW_STICKS = keyPressed;
        if (DRAW_STICKS) {
            stroke(255, 127);
            for (final IConstraint myIConstraint : _myParticleSystem.constraints()) {
                if (myIConstraint instanceof Stick) {
                    final Stick myStick = (Stick) myIConstraint;
                    line(myStick.a().position().x,
                         myStick.a().position().y,
                         myStick.a().position().z,
                         myStick.b().position().x,
                         myStick.b().position().y,
                         myStick.b().position().z);
                }
            }
        }
    }


    private void triangle(int ax, int ay,
                          int bx, int by,
                          int cx, int cy) {
        normal(_myNormals[ax][ay]);
        vertex(_myParticles[ax][ay].position());
        normal(_myNormals[bx][by]);
        vertex(_myParticles[bx][by].position());
        normal(_myNormals[cx][cy]);
        vertex(_myParticles[cx][cy].position());
    }


    private void normal(Vector3f n) {
        normal(n.x, n.y, n.z);
    }


    private void vertex(Vector3f v) {
        vertex(v.x, v.y, v.z);
    }


    private class MyMovingAttractor
        extends Attractor {

        private final float _mySpeed;

        public MyMovingAttractor(float theSpeed) {
            _myStrength = -10000;
            _myRadius = 100;
            _mySpeed = theSpeed;
        }


        public void apply(float theDeltaTime, Physics theParticleSystem) {
            position().x += theDeltaTime * _mySpeed;
            if (position().x > width) {
                position().x -= width;
            }
            if (position().x < 0) {
                position().x += width;
            }
            super.apply(theDeltaTime, theParticleSystem);
        }
    }


    private class MyMouseForce
        extends Attractor {

        public MyMouseForce() {
            _myStrength = -10000;
            _myRadius = 200;
        }


        public void apply(float theDeltaTime, Physics theParticleSystem) {
            position().set(mouseX, mouseY, 10);
            super.apply(theDeltaTime, theParticleSystem);
        }
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {TestCloth.class.getName()});
    }
}
