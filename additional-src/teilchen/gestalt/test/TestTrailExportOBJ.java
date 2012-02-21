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


import java.util.Vector;

import gestalt.Gestalt;
import gestalt.extension.quadline.QuadLine;
import gestalt.processing.GestaltPlugIn;
import gestalt.material.Material;
import gestalt.util.scenewriter.SceneWriter;

import mathematik.Vector3f;

import teilchen.BehaviorParticle;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.behavior.Wander;
import teilchen.force.Attractor;
import teilchen.force.IForce;
import teilchen.force.ViscousDrag;
import teilchen.util.ParticleTrail;
import processing.core.PApplet;


public class TestTrailExportOBJ
    extends PApplet {

    private Physics _myParticleSystem;

    private Vector<MyBehaviorParticle> _myParticles;

    private GestaltPlugIn gestalt;

    public void setup() {
        size(640, 480, OPENGL);
        frameRate(30);
        colorMode(RGB, 1.0f);
        noFill();
        smooth();

        /* gestalt */
        gestalt = new GestaltPlugIn(this);

        /* physics */
        _myParticleSystem = new Physics();

        ViscousDrag myViscousDrag = new ViscousDrag();
        myViscousDrag.coefficient = 0.1f;
        _myParticleSystem.add(myViscousDrag);

        for (int i = 0; i < 100; i++) {
            Attractor _myForceSphere = new Attractor();
            _myForceSphere.radius(random(75, 100));
            _myForceSphere.strength( -500);
            _myForceSphere.position().set(random(width), random(height), random( -100, 100));
            _myParticleSystem.add(_myForceSphere);
        }

        /* create particles */
        _myParticles = new Vector<MyBehaviorParticle> ();
        Material myMaterial = gestalt.drawablefactory().material();
        for (int i = 0; i < 500; i++) {
            MyBehaviorParticle myBehaviorParticle = new MyBehaviorParticle();
            myBehaviorParticle.particle = _myParticleSystem.makeParticle(BehaviorParticle.class);
            _myParticles.add(myBehaviorParticle);

            myBehaviorParticle.wander = new Wander();
            myBehaviorParticle.particle.behaviors().add(myBehaviorParticle.wander);
            myBehaviorParticle.wander.weight(0.5f);
            myBehaviorParticle.wander.steeringstrength(50);

            myBehaviorParticle.particle.position().set(random(width), random(height));

            /* create trails */
            myBehaviorParticle.trail = new ParticleTrail(_myParticleSystem,
                                                         myBehaviorParticle.particle,
                                                         random(0.25f, 0.3f),
                                                         15);
            myBehaviorParticle.trail.fix(true);

            /* create quadline */
            myBehaviorParticle.line = gestalt.drawablefactory().extensions().quadline();
            myBehaviorParticle.line.setMaterialRef(myMaterial);
            gestalt.bin(Gestalt.BIN_3D).add(myBehaviorParticle.line);
        }
    }


    public void draw() {
        /* draw particles */
        background(0);
        for (MyBehaviorParticle myParticle : _myParticles) {
            /* trail */
            myParticle.trail.loop(1f / 30);

            /* transfer fragment position to quadline */
            updateQuadline(myParticle);
        }

        /* draw force */
        stroke(1, 0.25f);
        for (IForce myForce : _myParticleSystem.forces()) {
            if (myForce instanceof Attractor) {
                Attractor myAttractor = (Attractor) myForce;
                ellipse(myAttractor.position().x, myAttractor.position().y,
                        myAttractor.radius() * 1, myAttractor.radius() * 1);
            }
        }

        _myParticleSystem.step(1f / 30);
    }


    private void updateQuadline(MyBehaviorParticle theParticle) {
        final Vector<Particle> myFragments = theParticle.trail.fragments();

        if (myFragments.size() < 2) {
            return;
        }

        if (myFragments.size() > 1) {
            theParticle.line.points = new Vector3f[myFragments.size() + 1];
            int myLast = theParticle.line.points.length - 1;
            theParticle.line.points[myLast] = theParticle.trail.particle().position();
            for (int i = 0; i < theParticle.line.points.length - 1; i++) {
                theParticle.line.points[i] = myFragments.get(i).position();
            }
        } else {
            theParticle.line.points = new Vector3f[myFragments.size()];
            for (int i = 0; i < myFragments.size(); i++) {
                theParticle.line.points[i] = myFragments.get(i).position();
            }
        }

        theParticle.line.update();
    }


    public void mousePressed() {
        for (MyBehaviorParticle myParticle : _myParticles) {
            myParticle.particle.position().set(mouseX, mouseY, 0);
            myParticle.particle.velocity().set(random(1), random(1), random( -1, 1));
            myParticle.trail.clear();
            myParticle.trail.set();
        }
    }


    public void keyPressed() {
        if (key == 's') {
            new SceneWriter("../TestTrailExportOBJ.obj", gestalt.bin(Gestalt.BIN_3D));
        }
    }


    class MyBehaviorParticle {

        Wander wander;

        BehaviorParticle particle;

        ParticleTrail trail;

        QuadLine line;
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {TestTrailExportOBJ.class.getName()});
    }
}
