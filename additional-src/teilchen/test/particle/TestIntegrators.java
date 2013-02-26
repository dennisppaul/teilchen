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
import teilchen.force.IForce;
import teilchen.force.ViscousDrag;
import teilchen.integration.Euler;
import teilchen.integration.RungeKutta;
import processing.core.PApplet;


public class TestIntegrators
    extends PApplet {

    private Physics _myParticleSystemEuler;

    private Physics _myParticleSystemRungeKutta;

    public void setup() {
        size(640, 480);
        frameRate(120);

        /* euler */
        _myParticleSystemEuler = new Physics();
        _myParticleSystemEuler.add(new ViscousDrag(1));
        _myParticleSystemEuler.add(new MyMouseForce());
        _myParticleSystemEuler.setInegratorRef(new Euler());

        for (int i = 0; i < 100; i++) {
            Particle myParticle = _myParticleSystemEuler.makeParticle();
            myParticle.position().set(random(height), random(width), 0);
        }

        /* runge kutta */
        _myParticleSystemRungeKutta = new Physics();
        _myParticleSystemRungeKutta.add(new ViscousDrag(1));
        _myParticleSystemRungeKutta.add(new MyMouseForce());
        _myParticleSystemRungeKutta.setInegratorRef(new RungeKutta());

        for (int i = 0; i < 100; i++) {
            Particle myParticle = _myParticleSystemRungeKutta.makeParticle();
            myParticle.position().set(_myParticleSystemEuler.particles().get(i).position());
        }
    }


    public void draw() {
        /* draw particles */
        background(255);
        stroke(0);
        for (Particle myParticle : _myParticleSystemEuler.particles()) {
            point(myParticle.position().x, myParticle.position().y);
        }

        stroke(255, 0, 0);
        for (Particle myParticle : _myParticleSystemRungeKutta.particles()) {
            point(myParticle.position().x, myParticle.position().y);
        }

        _myParticleSystemEuler.step(1 / 120f);
        _myParticleSystemRungeKutta.step(1 / 120f);
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
        PApplet.main(new String[] {TestIntegrators.class.getName()});
    }
}
