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


package teilchen.demo;


import mathematik.Vector3f;
import mathematik.Vector3i;

import processing.core.PApplet;
import teilchen.cubicle.CubicleWorld;
import teilchen.cubicle.ICubicleEntity;
import teilchen.util.P5CubicleWorldView;

import java.util.Vector;


public class LessonX07_CubicleWorld
        extends PApplet {

    private final int NUMBER_OF_PARTICLES_ADDED = 100;

    private final int WORLD_NUMBER_OF_CUBICLES = 15;

    private final float WORLD_CUBICLE_SCALE = 20;

    private boolean showCubicles = true;

    private float mRotationZ = 0.1f;

    private Vector3f mPosition = new Vector3f();

    private int numParticles = 1;

    private CubicleWorld mCubicleWorld;

    private P5CubicleWorldView mCubicleWorldView;


    public void setup() {
        size(640, 480, OPENGL);
        textFont(createFont("Courier", 11));
        hint(DISABLE_DEPTH_SORT);
        hint(DISABLE_DEPTH_TEST);

        /* setup world */
        mCubicleWorld = new CubicleWorld(WORLD_NUMBER_OF_CUBICLES, WORLD_NUMBER_OF_CUBICLES, WORLD_NUMBER_OF_CUBICLES);
        mCubicleWorld.cellscale().set(WORLD_CUBICLE_SCALE, WORLD_CUBICLE_SCALE, WORLD_CUBICLE_SCALE);
        mCubicleWorld.transform().translation.set(-WORLD_NUMBER_OF_CUBICLES * mCubicleWorld.cellscale().x / 2,
                                                  -WORLD_NUMBER_OF_CUBICLES * mCubicleWorld.cellscale().y / 2,
                                                  -WORLD_NUMBER_OF_CUBICLES * mCubicleWorld.cellscale().z / 2);

        mCubicleWorldView = new P5CubicleWorldView(mCubicleWorld);
        mCubicleWorldView.color_empty = color(0, 1);
        mCubicleWorldView.color_full = color(0, 4);

        mCubicleWorld.add(new MCubicleEntity());
    }


    public void draw() {
        /* get entities from cubicle world */
        mCubicleWorld.update();
        Vector<ICubicleEntity> mEntities = mCubicleWorld.getLocalEntities(mPosition, 1);

        background(255);
        pushMatrix();

        translate(width / 2, height / 2, 0);

        /* rotate */
        if (mousePressed) {
            mRotationZ += (mouseX * 0.01f - mRotationZ) * 0.05f;
        } else {
            mPosition.x = mouseX - width / 2;
            mPosition.y = mouseY - height / 2;
        }
        rotateX(THIRD_PI);
        rotateZ(mRotationZ);

        /* draw cubicle world */
        if (showCubicles) {
            stroke(0, 127);
            noFill();
            mCubicleWorldView.draw(this);
        }

        /* draw entities */
        int mNumberOfPointsSelected = 0;
        stroke(0, 127, 255, 127);
        noFill();
        if (mEntities != null) {
            mNumberOfPointsSelected = mEntities.size();
            for (ICubicleEntity mEntity : mEntities) {
                MCubicleEntity m = (MCubicleEntity)mEntity;
                stroke(m.color);
                drawCross(mEntity.position(), 5.0f);
            }
        }

        /* draw crosshair */
        stroke(255, 0, 0, 63);
        noFill();
        beginShape(LINES);
        vertex(mPosition.x, -WORLD_CUBICLE_SCALE * WORLD_NUMBER_OF_CUBICLES / 2, 0);
        vertex(mPosition.x, WORLD_CUBICLE_SCALE * WORLD_NUMBER_OF_CUBICLES / 2, 0);
        vertex(-WORLD_CUBICLE_SCALE * WORLD_NUMBER_OF_CUBICLES / 2, mPosition.y, 0);
        vertex(WORLD_CUBICLE_SCALE * WORLD_NUMBER_OF_CUBICLES / 2, mPosition.y, 0);
        endShape();

        /* draw selection sphere */
        stroke(255, 0, 0, 63);
        noFill();
        translate(mPosition.x, mPosition.y, 0);
        box(WORLD_CUBICLE_SCALE);
        popMatrix();

        fill(0);
        noStroke();
        text("POINTS   : " + numParticles, 10, 12);
        text("SELECTED : " + mNumberOfPointsSelected, 10, 24);
        text("FPS      : " + frameRate, 10, 36);
    }


    private void drawCross(Vector3f v, float pRadius) {
        line(v.x - pRadius, v.y, v.z, v.x + pRadius, v.y, v.z);
        line(v.x, v.y - pRadius, v.z, v.x, v.y + pRadius, v.z);
        line(v.x, v.y, v.z - pRadius, v.x, v.y, v.z + pRadius);
    }


    public void keyPressed() {
        if (key == ' ') {
            for (int i = 0; i < NUMBER_OF_PARTICLES_ADDED; i++) {
                MCubicleEntity mEntity = new MCubicleEntity();
                mEntity.position().x = random(-WORLD_CUBICLE_SCALE * WORLD_NUMBER_OF_CUBICLES / 2, WORLD_CUBICLE_SCALE * WORLD_NUMBER_OF_CUBICLES / 2);
                mEntity.position().y = random(-WORLD_CUBICLE_SCALE * WORLD_NUMBER_OF_CUBICLES / 2, WORLD_CUBICLE_SCALE * WORLD_NUMBER_OF_CUBICLES / 2);
                mEntity.position().z = random(-WORLD_CUBICLE_SCALE * WORLD_NUMBER_OF_CUBICLES / 2, WORLD_CUBICLE_SCALE * WORLD_NUMBER_OF_CUBICLES / 2);
                mCubicleWorld.add(mEntity);
            }
            numParticles += NUMBER_OF_PARTICLES_ADDED;
        } else if (key == 'o') {
            showCubicles = !showCubicles;
        } else if (key == 'c') {
            mCubicleWorld.removeAll();
            numParticles = 0;
        }
    }


    class MCubicleEntity
            implements ICubicleEntity {

        int color = color(0, 127, random(0, 255), 127);

        private Vector3i _myCubiclePosition;

        private final Vector3f _myPosition;


        public MCubicleEntity() {
            _myCubiclePosition = new Vector3i();
            _myPosition = new Vector3f();
        }


        public Vector3i cubicle() {
            return _myCubiclePosition;
        }


        public Vector3f position() {
            return _myPosition;
        }


        public boolean leaving(int theX, int theY, int theZ) {
            if (theX == cubicle().x
                    && theY == cubicle().y
                    && theZ == cubicle().z) {
                return false;
            }
            return true;
        }


        public boolean isActive() {
            return true;
        }
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {LessonX07_CubicleWorld.class.getName()});
    }
}
