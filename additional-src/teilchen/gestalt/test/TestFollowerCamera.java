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

package teilchen.gestalt.test;


import java.util.Vector;

import gestalt.Gestalt;
import gestalt.shape.FastBitmapFont;
import gestalt.processing.GestaltPlugIn;
import gestalt.render.Drawable;
import gestalt.render.controller.FrameGrabber;
import gestalt.shape.Line;
import gestalt.shape.Plane;

import mathematik.Rotation;
import mathematik.Vector3f;

import teilchen.BehaviorParticle;
import teilchen.Physics;
import teilchen.behavior.Arrival;
import teilchen.force.ViscousDrag;
import teilchen.gestalt.util.GestaltFollowerCamera;
import teilchen.util.P5DrawLib;
import processing.core.PApplet;


public class TestFollowerCamera
    extends PApplet {

    private GestaltPlugIn gestalt;

    private Path myPathCollector;

    private Vector<MyBehaviorParticle> _myParticles;

    private Physics _myParticleSystem;

    private GestaltFollowerCamera _myCamera;

    private Line _myLine;

    private int _myCurrentFocusAgentID;

    private FrameGrabber _myFrameGrabber;

    public void setup() {
        size(640, 480, OPENGL);
        frameRate(60);
        smooth();
        hint(DISABLE_DEPTH_TEST);

        /* gestalt */
        gestalt = new GestaltPlugIn(this);

        /* create path */
        myPathCollector = new Path(100);

        /* create path view */
        _myLine = gestalt.drawablefactory().line();
        _myLine.material().color.set(0, 0.2f);
        _myLine.material().depthmask = false;
        _myLine.material().depthtest = false;
        _myLine.linewidth = 1;
        _myLine.setPrimitive(Gestalt.LINE_PRIMITIVE_TYPE_LINE_LOOP);
        _myLine.points = myPathCollector.data;
        gestalt.bin(Gestalt.BIN_3D).add(_myLine);

        _myParticleSystem = new Physics();
        _myParticleSystem.add(new ViscousDrag(0.3f));

        _myParticles = new Vector<MyBehaviorParticle> ();
        for (int i = 0; i < 50; i++) {
            MyBehaviorParticle myBehaviorParticle = new MyBehaviorParticle(i);
            myBehaviorParticle.particle = _myParticleSystem.makeParticle(BehaviorParticle.class);
            myBehaviorParticle.arrival = new Arrival();
            myBehaviorParticle.particle.behaviors().add(myBehaviorParticle.arrival);
            myBehaviorParticle.arrival.breakradius(40);
            myBehaviorParticle.arrival.breakforce(10);
            myBehaviorParticle.particle.position().set(random(width), random(height));

            _myParticles.add(myBehaviorParticle);
            gestalt.bin(Gestalt.BIN_3D).add(myBehaviorParticle.view);
        }

        /* camera */
        _myCamera = new GestaltFollowerCamera(gestalt.camera());
        _myCamera.focusarrival().setPositionRef(_myParticles.get(_myCurrentFocusAgentID).particle.position());

        /* create frame grabber */
        _myFrameGrabber = gestalt.drawablefactory().extensions().framegrabber();
        _myFrameGrabber.setImageFileFormat(Gestalt.IMAGE_FILEFORMAT_JPEG);
        _myFrameGrabber.setFileName(sketchPath + "/" + getClass().getSimpleName() + "/" + getClass().getSimpleName());
        gestalt.bin(Gestalt.BIN_ARBITRARY).add(_myFrameGrabber);
    }


    public void draw() {
        float myDeltaTime = 1.0f / 60;

        /* collect mouse position */
        myPathCollector.createEvent();

        /* update camera */
        _myCamera.loop(myDeltaTime);

        /* update particle system */
        _myParticleSystem.step(myDeltaTime);
        for (MyBehaviorParticle myParticle : _myParticles) {
            myParticle.update();
        }

        /* draw */
        background(255);
        if (!mousePressed) {
            gestalt.applyCamera(gestalt.camera());
        }

        myPathCollector.connectCollectedPoints();
        myPathCollector.drawCollectedPoints();

        if (!mousePressed) {
            markFocusedAgent(_myParticles.get(_myCurrentFocusAgentID).particle.position());
        }

        /* mark focus */
        stroke(0, 64);
        noFill();
        pushMatrix();
        translate(_myCamera.focusparticle().position().x,
                  _myCamera.focusparticle().position().y,
                  _myCamera.focusparticle().position().z);
        ellipse(0, 0, 36, 36);
        popMatrix();
        line(_myCamera.focusarrival().position().x,
             _myCamera.focusarrival().position().y,
             _myCamera.focusarrival().position().z,
             _myCamera.focusparticle().position().x,
             _myCamera.focusparticle().position().y,
             _myCamera.focusparticle().position().z);

//        if (_myRecording) {
//            saveFrame(getClass().getSimpleName() + "/" + getClass().getSimpleName() + "-####.tga");
//        }
    }


    private boolean _myRecording = false;

    public void keyPressed() {
        switch (key) {
            case ' ':
                _myRecording = !_myRecording;
                if (_myRecording) {
                    _myFrameGrabber.start();
                    System.out.println("### start recording");
                } else {
                    _myFrameGrabber.stop();
                    System.out.println("### stop recording");
                }
                break;
        }

        /* focus on next agent camera */
        if (key == 'n') {
            _myCurrentFocusAgentID++;
            _myCurrentFocusAgentID %= _myParticles.size();
            /* point camera focus to current agent */
            _myCamera.focusarrival().setPositionRef(_myParticles.get(_myCurrentFocusAgentID).particle.position());
        }
    }


    private void markFocusedAgent(Vector3f thePosition) {
        noFill();
        stroke(0, 127);
        pushMatrix();
        translate(thePosition.x, thePosition.y, thePosition.z);
        ellipse(0, 0, 32, 32);
        popMatrix();
    }


    private class MyBehaviorParticle {

        public Arrival arrival;

        public BehaviorParticle particle;

        public final Plane view;

        private final FastBitmapFont _myFont;

        private int myCurrentSeekPositionID;

        private final Rotation _myRotation;

        public MyBehaviorParticle(final int theID) {
            /* create view */
            view = gestalt.drawablefactory().plane();
            view.scale().set(20, 5);
            view.material().color.set(0, 0.25f);
            view.material().depthtest = true;
            view.material().depthmask = false;
            view.material().transparent = true;
            view.material().wireframe = true;

            _myRotation = new Rotation();

            /* create font */
            _myFont = new FastBitmapFont();
            _myFont.color.set(0, 1);
            _myFont.align = FastBitmapFont.CENTERED;
            _myFont.text = String.valueOf(theID);
            view.setChildContainer(new Vector<Drawable> ());
            view.add(_myFont);
            _myFont.position = view.transform().translation;
        }


        public void update() {
            /* handle seek positions */
            handleNextPathPoint();

            /* set arrival position */
            arrival.position().set(myPathCollector.data[myCurrentSeekPositionID]);

            /* update view position */
            view.position().set(particle.position());

            /* update view rotation */
            _myRotation.set(particle.velocity(), view.transform());
        }


        private void handleNextPathPoint() {
            if (arrival.arriving()) {
                myCurrentSeekPositionID++;
                myCurrentSeekPositionID %= myPathCollector.data.length;
            }
        }
    }


    private class Path {

        public final Vector3f[] data;

        private int myCurrentWayPointID;

        public Path(int theBufferSize) {
            /* create storage */
            data = new Vector3f[theBufferSize];
            for (int i = 0; i < data.length; i++) {
                data[i] = new Vector3f();
            }

            /* fill storage with circle value */
            for (int i = 0; i < data.length; i++) {
                final float r = radians(i * (float) 360 / data.length);
                final float x = sin(r) * 200 + width / 2;
                final float y = cos(r) * 200 + height / 2;
                data[i].set(x, y, 0);
            }
        }


        public void createEvent() {
            if (mousePressed) {
                data[myCurrentWayPointID].set(mouseX, mouseY, 0);

                myCurrentWayPointID++;
                myCurrentWayPointID %= data.length;
            }
        }


        public void connectCollectedPoints() {
            for (int i = 0; i < data.length - 1; i++) {
                /* get current id */
                int myID = i + myCurrentWayPointID;
                myID %= data.length;
                /* get next id */
                int myNextID = myID + 1;
                myNextID %= data.length;
                /* connect both points */
                stroke(0, 127);
                line(
                    data[myID].x,
                    data[myID].y,
                    data[myID].z,
                    data[myNextID].x,
                    data[myNextID].y,
                    data[myNextID].z
                    );
            }
        }


        public void drawCollectedPoints() {
            strokeWeight(2);
            for (int i = 0; i < data.length; i++) {
                P5DrawLib.drawCross(g, data[i], 2, color(0, 64));
            }
            strokeWeight(1);
        }
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {TestFollowerCamera.class.getName()});
    }
}
