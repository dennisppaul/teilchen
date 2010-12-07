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


package teilchen.gestalt.test;


import gestalt.p5.GestaltPlugIn;
import gestalt.texture.Bitmaps;

import mathematik.Vector3f;

import data.Resource;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.ShortLivedParticle;
import teilchen.force.Gravity;
import teilchen.force.IForce;
import teilchen.force.ViscousDrag;
import teilchen.gestalt.util.GestaltDrawLib.GestaltParticleView;
import processing.core.PApplet;


/**
 * this sketch shows how to integrate pointsprites with particles.
 * pointsprites are extremly fast.
 * the only drag is that they don t seem to work on all graphicscards.
 * :(
 */

public class TestParticleSystemAsPointsprites
    extends PApplet {

    /* gestalt */

    private GestaltPlugIn gestalt;

    private GestaltParticleView _myView;

    /* physics */

    private Physics physics;

    public void setup() {
        size(640, 480, OPENGL);
        frameRate(120);
        noFill();

        /* gestalt */
        gestalt = new GestaltPlugIn(this);

        /* physics */
        physics = new Physics();
        physics.add(new MyMouseForce());
        Gravity myGravity = new Gravity();
        myGravity.force().y = 100;
        physics.add(myGravity);

        ViscousDrag myViscousDrag = new ViscousDrag();
        myViscousDrag.coefficient = 1f;
        physics.add(myViscousDrag);

        /* point sprites */
        _myView = new GestaltParticleView(physics, gestalt,
                                          Bitmaps.getBitmap(Resource.getStream("flower-particle.png"),
                                                            "flower"),
                                          200);
    }


    public void draw() {
        /* draw particles -- map particles to mesh */
        background(255);
        _myView.loop(1f / frameRate);

        /* create new particles */
        if (mousePressed && physics.particles().size() < _myView.getMaxParticles()) {
            ShortLivedParticle myParticle = physics.makeParticle(ShortLivedParticle.class);
            myParticle.setMaxAge(3);
            if (myParticle != null) {
                myParticle.position().set(mouseX, mouseY);
            }
        }

        physics.step(1f / frameRate);
    }


    private class MyMouseForce
        implements IForce {

        public void apply(final float theDeltaTime, final Physics theParticleSystem) {
            for (final Particle myParticle : theParticleSystem.particles()) {
                if (!myParticle.fixed()) {
                    Vector3f myMouseVector = mathematik.Util.sub(new Vector3f(mouseX, mouseY), myParticle.position());
                    myMouseVector.normalize();
                    myMouseVector.scale(500f);
                    if (!myMouseVector.isNaN()) {
                        myParticle.force().add(myMouseVector);
                    }
                }
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
        PApplet.main(new String[] {TestParticleSystemAsPointsprites.class.getName()});
    }
}
