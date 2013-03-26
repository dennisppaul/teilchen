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
import teilchen.util.CubicleWorldView;
import teilchen.util.DrawLib;

import java.util.Vector;


public class LessonX07_CubicleWorld
        extends PApplet {

    private final int WORLD_NUMBER_OF_CUBICLES = 15;

    private final float WORLD_CUBICLE_SCALE = 20;

    private final float WORLD_SCALE = WORLD_NUMBER_OF_CUBICLES * WORLD_CUBICLE_SCALE;

    private boolean showCubicles = true;

    private float mRotationZ = 0.1f;

    private Vector3f mPosition = new Vector3f();

    private CubicleWorld mCubicleWorld;

    private CubicleWorldView mCubicleWorldView;

    public void setup() {
        size(640, 480, OPENGL);
        textFont(createFont("Courier", 11));
        hint(DISABLE_DEPTH_SORT);
        hint(DISABLE_DEPTH_TEST);

        /* setup world */
        mCubicleWorld = new CubicleWorld(WORLD_NUMBER_OF_CUBICLES, WORLD_NUMBER_OF_CUBICLES, WORLD_NUMBER_OF_CUBICLES);
        mCubicleWorld.cellscale().set(WORLD_CUBICLE_SCALE, WORLD_CUBICLE_SCALE, WORLD_CUBICLE_SCALE);
        mCubicleWorld.transform().translation.set(-WORLD_SCALE / 2, -WORLD_SCALE / 2, -WORLD_SCALE / 2);

        mCubicleWorldView = new CubicleWorldView(mCubicleWorld);
        mCubicleWorldView.color_empty = color(0, 1);
        mCubicleWorldView.color_full = color(0, 4);

        mCubicleWorld.add(new MCubicleEntity());
    }

    public void draw() {
        /* handle entities */
        if (frameRate > 30) {
            addRandomEntities(2);
        }

        mCubicleWorld.update();
        Vector<ICubicleEntity> mEntities = mCubicleWorld.getLocalEntities(mPosition, 1);

        /* draw things */
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
            mCubicleWorldView.draw(g);
        }

        /* draw entities */
        int mNumberOfPointsSelected = 0;
        stroke(0, 127, 255, 127);
        noFill();
        if (mEntities != null) {
            mNumberOfPointsSelected = mEntities.size();
            for (ICubicleEntity mEntity : mEntities) {
                MCubicleEntity m = (MCubicleEntity) mEntity;
                stroke(m.color);
                DrawLib.cross3(g, mEntity.position(), 5.0f);
            }
        }

        /* draw crosshair */
        stroke(255, 0, 0, 63);
        noFill();
        beginShape(LINES);
        vertex(mPosition.x, -WORLD_SCALE / 2, 0);
        vertex(mPosition.x, WORLD_SCALE / 2, 0);
        vertex(-WORLD_SCALE / 2, mPosition.y, 0);
        vertex(WORLD_SCALE / 2, mPosition.y, 0);
        endShape();

        /* draw selection sphere */
        stroke(255, 0, 0, 63);
        noFill();
        translate(mPosition.x, mPosition.y, 0);
        box(WORLD_CUBICLE_SCALE);
        popMatrix();

        fill(0);
        noStroke();
        text("POINTS   : " + mCubicleWorld.entities().size(), 10, 12);
        text("SELECTED : " + mNumberOfPointsSelected, 10, 24);
        text("FPS      : " + frameRate, 10, 36);
    }

    private void addRandomEntities(int pNumberParticles) {
        for (int i = 0; i < pNumberParticles; i++) {
            MCubicleEntity mEntity = new MCubicleEntity();
            mEntity.position().x = random(-WORLD_SCALE / 2, WORLD_SCALE / 2);
            mEntity.position().y = random(-WORLD_SCALE / 2, WORLD_SCALE / 2);
            mEntity.position().z = random(-WORLD_SCALE / 2, WORLD_SCALE / 2);
            mCubicleWorld.add(mEntity);
        }
    }

    class MCubicleEntity
            implements ICubicleEntity {

        int color = color(0, 127, random(0, 255), 127);

        private Vector3i mCubiclePosition;

        private final Vector3f mPosition;

        public MCubicleEntity() {
            mCubiclePosition = new Vector3i();
            mPosition = new Vector3f();
        }

        public Vector3i cubicle() {
            return mCubiclePosition;
        }

        public Vector3f position() {
            return mPosition;
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
        PApplet.main(new String[]{LessonX07_CubicleWorld.class.getName()});
    }
}
