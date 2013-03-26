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
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PMatrix3D;
import teilchen.BehaviorParticle;
import teilchen.Physics;
import teilchen.behavior.Alignment;
import teilchen.behavior.Cohesion;
import teilchen.behavior.Motor;
import teilchen.behavior.Separation;
import teilchen.behavior.Wander;
import teilchen.constraint.Teleporter;
import teilchen.force.ViscousDrag;
import teilchen.util.Util;

import java.util.Vector;


public class LessonX08_Schwarm
        extends PApplet {

    private Physics mPhysics;

    private Vector<SwarmEntity> mSwarmEntities;

    public void setup() {
        size(640, 480, OPENGL);
        frameRate(60);
        smooth();
        rectMode(CENTER);
        hint(DISABLE_DEPTH_TEST);

        /* physics */
        mPhysics = new Physics();

        Teleporter mTeleporter = new Teleporter();
        mTeleporter.min().set(0, 0, height / -2);
        mTeleporter.max().set(width, height, height / 2);
        mPhysics.add(mTeleporter);

        ViscousDrag myViscousDrag = new ViscousDrag();
        mPhysics.add(myViscousDrag);

        /* setup entities */
        mSwarmEntities = new Vector<SwarmEntity>();
        for (int i = 0; i < 60; i++) {
            SwarmEntity mSwarmEntity = new SwarmEntity();
            mSwarmEntity.position().set(random(mTeleporter.min().x, mTeleporter.max().x),
                                        random(mTeleporter.min().y, mTeleporter.max().y),
                                        random(mTeleporter.min().z, mTeleporter.max().z));
            mSwarmEntities.add(mSwarmEntity);
            mPhysics.add(mSwarmEntity);
        }
    }

    public void draw() {
        final float mDeltaTime = 1.0f / frameRate;

        /* physics */
        mPhysics.step(mDeltaTime);

        /* entities */
        for (SwarmEntity s : mSwarmEntities) {
            s.update(mDeltaTime);
        }

        /* draw */
        background(255);
        for (SwarmEntity s : mSwarmEntities) {
            s.draw(g);
        }
    }

    private class SwarmEntity
            extends BehaviorParticle {

        private Separation separation;

        private Alignment alignment;

        private Cohesion cohesion;

        private Wander wander;

        private Motor motor;

        public SwarmEntity() {
            maximumInnerForce(random(100.0f, 1000.0f));
            radius(10f);

            separation = new Separation();
            separation.proximity(20);
            separation.weight(50.0f);
            behaviors().add(separation);

            alignment = new Alignment();
            alignment.proximity(30);
            alignment.weight(30.0f);
            behaviors().add(alignment);

            cohesion = new Cohesion();
            cohesion.proximity(100);
            cohesion.weight(5.0f);
            behaviors().add(cohesion);

            wander = new Wander();
            behaviors().add(wander);

            motor = new Motor();
            motor.auto_update_direction(true);
            motor.strength(20.0f);
            behaviors().add(motor);
        }

        public void update(float theDeltaTime) {
            separation.neighbors(mSwarmEntities);
            alignment.neighbors(mSwarmEntities);
            cohesion.neighbors(mSwarmEntities);
        }

        private void draw(PGraphics g) {
            pushMatrix();

            translate(position().x, position().y, position().z);

            pushMatrix();

            PMatrix3D p = new PMatrix3D();
            Util.pointAt(p, position(), new Vector3f(0, 1, 0), mathematik.Util.add(position(), velocity()));
            applyMatrix(p);

            noStroke();
            fill(0);
            scale(1, 0.25f, 3);
            box(6);

            popMatrix();

            /* velocity */
            stroke(0, 31);
            line(0, 0, 0, velocity().x, velocity().y, velocity().z);

            popMatrix();
        }
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{LessonX08_Schwarm.class.getName()});
    }
}
