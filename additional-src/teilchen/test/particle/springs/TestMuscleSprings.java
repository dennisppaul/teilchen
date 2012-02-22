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


package teilchen.test.particle.springs;


import teilchen.Particle;
import teilchen.Physics;
import teilchen.force.MuscleSpring;
import teilchen.force.Spring;
import teilchen.force.ViscousDrag;
import teilchen.integration.RungeKutta;
import processing.core.PApplet;


public class TestMuscleSprings
    extends PApplet {

    private Physics _myParticleSystem;

    private Particle _myParticle;

    public void setup() {
        size(640, 480);
        smooth();
        frameRate(120);

        _myParticleSystem = new Physics();
        _myParticleSystem.setInegratorRef(new RungeKutta());

        ViscousDrag myViscousDrag = new ViscousDrag();
        myViscousDrag.coefficient = 1f;
        _myParticleSystem.add(myViscousDrag);

        /* create particles */
        Particle[] myParticles = new Particle[10];
        for (int i = 0; i < myParticles.length; i++) {
            final float myRadiant = PI * 2.0f * (i / (float) myParticles.length);
            myParticles[i] = _myParticleSystem.makeParticle();
            myParticles[i].position().set(sin(myRadiant) * 100, cos(myRadiant) * 100);
        }

        _myParticle = _myParticleSystem.makeParticle();
        _myParticle.fixed(true);

        /* create springs */
        for (int i = 0; i < myParticles.length; i++) {
            final float myRadiant = PI * 2.0f * (i / (float) myParticles.length);
            MuscleSpring mySpring = new MuscleSpring(myParticles[i], _myParticle);
            mySpring.offset(myRadiant);
            mySpring.amplitude(50);
            mySpring.frequency(0.5f);
            mySpring.strength(200);
            mySpring.damping(20);
            _myParticleSystem.add(mySpring);
        }

        for (int i = 0; i < myParticles.length; i++) {
            final int myNextID = (i + 1) % myParticles.length;
            Spring mySpring = new Spring(myParticles[i], myParticles[myNextID]);
            mySpring.strength(1000);
            mySpring.damping(10);
            mySpring.restlength(mySpring.restlength() * 0.9f);
            _myParticleSystem.add(mySpring);
        }
    }


    public void draw() {

        /* handle particles */
        _myParticle.position().set(mouseX, mouseY);
        _myParticleSystem.step(1f / frameRate);

        /* draw */
        background(255);

        /* draw muscle springs */
        fill(0, 20);
        noStroke();
        beginShape(POLYGON);
        for (int i = 0; i < _myParticleSystem.forces().size(); i++) {
            if (_myParticleSystem.forces(i) instanceof MuscleSpring) {
                MuscleSpring mySpring = (MuscleSpring) _myParticleSystem.forces(i);
                vertex(mySpring.a().position().x, mySpring.a().position().y);
            }
        }
        endShape(CLOSE);

        /* draw springs */
        stroke(0, 30);
        for (int i = 0; i < _myParticleSystem.forces().size(); i++) {
            if (_myParticleSystem.forces(i) instanceof Spring) {
                Spring mySpring = (Spring) _myParticleSystem.forces(i);
                line(mySpring.a().position().x,
                     mySpring.a().position().y,
                     mySpring.b().position().x,
                     mySpring.b().position().y);
            }
        }

        /* draw particles */
        stroke(0, 50);
        noFill();
        for (int i = 0; i < _myParticleSystem.particles().size(); i++) {
            ellipse(_myParticleSystem.particles(i).position().x,
                    _myParticleSystem.particles(i).position().y, 5, 5);
        }

        if (_myRecording) {
            saveFrame(getClass().getSimpleName() + "/" + getClass().getSimpleName() + "-####.tga");
        }
    }


    private boolean _myRecording = false;

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


    public static void main(String[] args) {
        PApplet.main(new String[] {TestMuscleSprings.class.getName()});
    }
}
