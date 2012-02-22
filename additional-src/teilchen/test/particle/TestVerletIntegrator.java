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


import mathematik.Vector3f;

import teilchen.Particle;
import teilchen.Physics;
import teilchen.constraint.Box;
import teilchen.force.IForce;
import teilchen.integration.Verlet;
import processing.core.PApplet;


public class TestVerletIntegrator
    extends PApplet {

    private Physics _myParticleSystem;

    public void setup() {
        size(640, 480);
        frameRate(60);

        /* euler */
        _myParticleSystem = new Physics();
        _myParticleSystem.add(new MyMouseForce());

        Box myBox = new Box();
        myBox.min().set(50, 50);
        myBox.max().set(width - 100, height - 100);
        _myParticleSystem.add(myBox);

        Verlet myVerlet = new Verlet();
        myVerlet.damping(0.85f); /* viscous drag doesn t work in verlet */
        _myParticleSystem.setInegratorRef(new Verlet());

        for (int i = 0; i < 100; i++) {
            Particle myParticle = _myParticleSystem.makeParticle();
            myParticle.mass(random(1f, 2));
            myParticle.position().set(random(height), random(width), 0);
        }
    }


    public void draw() {

        /* draw particles */
        background(255);

        stroke(0);
        line(pmouseX, pmouseY, mouseX, mouseY);

        for (Particle myParticle : _myParticleSystem.particles()) {
            stroke(0);
            point(myParticle.position().x, myParticle.position().y);
            stroke(255, 0, 0, 50);
            line(mouseX, mouseY, myParticle.position().x, myParticle.position().y);
        }

        _myParticleSystem.step(1 / frameRate);
    }


    private class MyMouseForce
        implements IForce {

        public void apply(final float theDeltaTime, final Physics theParticleSystem) {
            for (final Particle myParticle : theParticleSystem.particles()) {
                Vector3f myMouseVector = mathematik.Util.sub(new Vector3f(mouseX, mouseY), myParticle.position());
                myMouseVector.normalize();
                myMouseVector.scale(200f);
                myParticle.force().add(myMouseVector);
            }
        }


        public boolean dead() {
            return false;
        }


        public boolean active() {
            return true;
        }


        public void active(boolean theActiveState) {
        }
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {TestVerletIntegrator.class.getName()});
    }
}
