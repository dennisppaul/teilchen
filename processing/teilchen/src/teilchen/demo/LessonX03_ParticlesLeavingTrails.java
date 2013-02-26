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


import java.util.Vector;

import teilchen.Particle;
import teilchen.Physics;
import teilchen.ShortLivedParticle;
import teilchen.force.Gravity;
import teilchen.util.ParticleTrail;
import processing.core.PApplet;


public class LessonX03_ParticlesLeavingTrails
        extends PApplet {

    private Physics mPhysics;

    private Vector<ParticleTrail> mTrails;

    public void setup() {
        size(640, 480, OPENGL);
        smooth();
        frameRate(60);

        /* create a particle system */
        mPhysics = new Physics();

        /* create a gravitational force */
        Gravity myGravity = new Gravity();
        mPhysics.add(myGravity);
        myGravity.force().y = 30;
        myGravity.force().x = 5;

        /* create trails and particles */
        mTrails = new Vector<ParticleTrail>();
        for (int i = 0; i < 500; i++) {
            Particle mParticle = mPhysics.makeParticle();
            mParticle.mass(2.0f);
            ParticleTrail myParticleTrail = new ParticleTrail(mPhysics,
                                                              mParticle,
                                                              0.2f,
                                                              random(0.5f, 1));
            myParticleTrail.mass(0.5f);
            mTrails.add(myParticleTrail);
        }
        resetParticles(width / 2, height / 2);
    }

    private void resetParticles(float x, float y) {
        for (ParticleTrail myTrails : mTrails) {
            myTrails.particle().position().set(x + random(-100, 100), y + random(-10, 10), 0);
            myTrails.particle().velocity().set(random(-10, 10), random(-50, -20), 0);
            myTrails.fragments().clear();
        }
    }

    public void draw() {
        for (ParticleTrail myTrails : mTrails) {
            myTrails.loop(1f / frameRate);
        }

        mPhysics.step(1f / frameRate);

        background(255);
        for (ParticleTrail myTrail : mTrails) {
            drawTrail(myTrail);
        }
    }

    private void drawTrail(ParticleTrail theTrail) {

        final Vector<Particle> mFragments = theTrail.fragments();
        final Particle mParticle = theTrail.particle();

        /* draw head */
        if (mFragments.size() > 1) {
            stroke(255, 0, 127);
            line(mFragments.get(mFragments.size() - 1).position().x,
                 mFragments.get(mFragments.size() - 1).position().y,
                 mFragments.get(mFragments.size() - 1).position().z,
                 mParticle.position().x,
                 mParticle.position().y,
                 mParticle.position().z);
        }

        /* draw trail */
        for (int i = 0; i < mFragments.size() - 1; i++) {
            if (mFragments.get(i) instanceof ShortLivedParticle) {
                final float mRatio = 1.0f - ((ShortLivedParticle)mFragments.get(i)).ageRatio();
                stroke(127, mRatio * 255);
            }
            line(mFragments.get(i).position().x,
                 mFragments.get(i).position().y,
                 mFragments.get(i).position().z,
                 mFragments.get(i + 1).position().x,
                 mFragments.get(i + 1).position().y,
                 mFragments.get(i + 1).position().z);
        }
    }

    public void mousePressed() {
        resetParticles(mouseX, mouseY);
    }

    public static void main(String[] args) {
        PApplet.main(new String[] {LessonX03_ParticlesLeavingTrails.class.getName()});
    }
}
