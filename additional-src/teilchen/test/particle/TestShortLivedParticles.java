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


import mathematik.Vector3f;

import teilchen.Particle;
import teilchen.Physics;
import teilchen.ShortLivedParticle;
import teilchen.force.IForce;
import teilchen.force.ViscousDrag;
import processing.core.PApplet;


public class TestShortLivedParticles
    extends PApplet {

    private Physics _myParticleSystem;

    public void setup() {
        size(1024, 768, OPENGL);
        frameRate(60);
        colorMode(RGB, 1.0f);
        noFill();
        smooth();

        _myParticleSystem = new Physics();
        _myParticleSystem.add(new MyMouseForce());

        ViscousDrag myViscousDrag = new ViscousDrag();
        myViscousDrag.coefficient = 1f;
        _myParticleSystem.add(myViscousDrag);
    }


    public void draw() {
        /* draw particles */
        background(1);
        Particle myPreviousParticle = null;
        Vector3f myPreviousMidPoint = null;
        Vector3f myPreviousCross = null;
        for (Particle myParticle : _myParticleSystem.particles()) {
            final float myAgeRatio = 1 - ( (ShortLivedParticle) myParticle).ageRatio();

            noStroke();
            fill(0, myAgeRatio * 1.0f);
            ellipse(myParticle.position().x, myParticle.position().y, myAgeRatio * 5, myAgeRatio * 5);

            if (myPreviousParticle != null) {
                Vector3f myCross = mathematik.Util.sub(myParticle.position(), myPreviousParticle.position());

                final float myDistance = min(30, myCross.length());

                myCross.set(myCross.y, -myCross.x, 0);
                myCross.normalize();
                myCross.scale(myAgeRatio * myDistance * 2);

                Vector3f myMidPoint = mathematik.Util.add(myPreviousParticle.position(), myParticle.position());
                myMidPoint.scale(0.5f);

                stroke(0, myAgeRatio * 0.2f);
                line(myMidPoint.x + myCross.x,
                     myMidPoint.y + myCross.y,
                     myMidPoint.x - myCross.x,
                     myMidPoint.y - myCross.y);

                if (myPreviousMidPoint != null && myPreviousCross != null) {
                    stroke(0, myAgeRatio * 0.8f);
                    line(myMidPoint.x + myCross.x,
                         myMidPoint.y + myCross.y,
                         myPreviousMidPoint.x + myPreviousCross.x,
                         myPreviousMidPoint.y + myPreviousCross.y);
                    line(myMidPoint.x - myCross.x,
                         myMidPoint.y - myCross.y,
                         myPreviousMidPoint.x - myPreviousCross.x,
                         myPreviousMidPoint.y - myPreviousCross.y);
                }
                myPreviousMidPoint = myMidPoint;
                myPreviousCross = myCross;
            }
            myPreviousParticle = myParticle;
        }

        if (mousePressed) {
            Vector3f myPosition = new Vector3f(mouseX, mouseY, 0);
            if (_myParticleSystem.particles().isEmpty() ||
                _myParticleSystem.particles().lastElement().position().distanceSquared(myPosition) > 0) {
                ShortLivedParticle myParticle = _myParticleSystem.makeParticle(ShortLivedParticle.class);
                myParticle.setMaxAge(3);
                myParticle.position().set(myPosition);
            }
        }

        _myParticleSystem.step(1f / 60);

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


    private class MyMouseForce
        implements IForce {

        public void apply(final float theDeltaTime, final Physics theParticleSystem) {
            for (final Particle myParticle : theParticleSystem.particles()) {
                if (!myParticle.fixed()) {
                    Vector3f myMouseVector = mathematik.Util.sub(new Vector3f(mouseX, mouseY), myParticle.position());
                    myMouseVector.normalize();
                    myMouseVector.scale(200f);
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
        PApplet.main(new String[] {TestShortLivedParticles.class.getName()});
    }
}
