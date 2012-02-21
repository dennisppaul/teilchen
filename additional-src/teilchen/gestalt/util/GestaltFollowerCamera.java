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


package teilchen.gestalt.util;


import gestalt.Gestalt;
import gestalt.context.GLContext;
import gestalt.processing.GestaltPlugIn;
import gestalt.render.controller.Camera;

import mathematik.Vector3f;

import teilchen.BehaviorParticle;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.behavior.Arrival;
import teilchen.force.Spring;
import teilchen.force.ViscousDrag;
import processing.core.PGraphics;


public class GestaltFollowerCamera {

    private final Vector3f _myOffset;

    private final Physics _myParticleSystem;

    private final Particle _myCameraParticle;

    private final Camera _myCamera;

    private final Arrival _myArrivalBehavior;

    private final BehaviorParticle _myFocusParticle;

    private final Spring _mySpring;

    private final ViscousDrag _myViscousDrag;

    public GestaltFollowerCamera(final Camera theCamera) {
        _myCamera = theCamera;

        /* create particle system */
        _myParticleSystem = new Physics();

        /* create some drag */
        _myViscousDrag = new ViscousDrag(0.3f);
        _myParticleSystem.add(_myViscousDrag);

        /* create focus particle -- the camera will look at this particle */
        _myFocusParticle = _myParticleSystem.makeParticle(BehaviorParticle.class);
        _myFocusParticle.mass(2);
        _myFocusParticle.maximumInnerForce(100);

        /* create arrival behavior */
        _myArrivalBehavior = new Arrival();
        _myArrivalBehavior.breakradius(30);
        _myArrivalBehavior.breakforce(30);
        _myFocusParticle.behaviors().add(_myArrivalBehavior);

        /* create camera particle -- this will be the position of the camera plus the defined offset */
        _myCameraParticle = _myParticleSystem.makeParticle();

        /* put a spring between camera and follow particle */
        _mySpring = new Spring(_myFocusParticle,
                               _myCameraParticle,
                               0.05f,
                               0.9f,
                               100);
        _myParticleSystem.add(_mySpring);
        _mySpring.setOneWay(true);

        /* setup default distance */
        _myOffset = new Vector3f(0, 0, 120);

        /* set camera properties once to create some reasonable default state */
        _myCamera.setLookAtRef(_myFocusParticle.position());
        _myCamera.upvector().set(0, 0, 1);
        _myCamera.setMode(Gestalt.CAMERA_MODE_LOOK_AT);
    }


    public void loop(float theDeltatime) {
        _myParticleSystem.step(theDeltatime);
        _myCamera.position().set(_myCameraParticle.position());
        _myCamera.position().add(_myOffset);
    }


    public Vector3f cameraoffset() {
        return _myOffset;
    }


    public Spring cameraspring() {
        return _mySpring;
    }


    public Camera camera() {
        return _myCamera;
    }


    public ViscousDrag drag() {
        return _myViscousDrag;
    }


    public Arrival focusarrival() {
        return _myArrivalBehavior;
    }


    public BehaviorParticle focusparticle() {
        return _myFocusParticle;
    }


    public Particle cameraparticle() {
        return _myCameraParticle;
    }


    public Physics particlesystem() {
        return _myParticleSystem;
    }


    public static CameraStub createCamera(PGraphics theContext) {
        return new CameraStub(theContext);
    }


    public static class CameraStub
        extends Camera {

        private final PGraphics g;

        public CameraStub(PGraphics theContext) {
            g = theContext;
        }


        public void draw(GLContext theGLContext) {
            GestaltPlugIn.applyCamera(g, this);
        }
    }
}
