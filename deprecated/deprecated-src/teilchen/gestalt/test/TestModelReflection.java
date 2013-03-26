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


package teilchen.gestalt.test;


import gestalt.model.ModelData;
import gestalt.model.ModelLoaderOBJ;

import mathematik.Vector3f;

import data.Resource;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.ShortLivedParticle;
import teilchen.force.Gravity;
import teilchen.force.IForce;
import teilchen.force.PlaneDeflector;
import teilchen.force.TriangleDeflector;
import teilchen.force.ViscousDrag;
import teilchen.util.DrawLib;
import processing.core.PApplet;


public class TestModelReflection
        extends PApplet {

    private Physics _myParticleSystem;

    private float _myYRotation;

    private PlaneDeflector _myGroudPlaneDeflector;


    public void setup() {
        size(640, 480, OPENGL);
        frameRate(120);
        colorMode(RGB, 1.0f);
        noFill();

        /* particle system */
        _myParticleSystem = new Physics();

        /* load model */
        ModelData myModelData = ModelLoaderOBJ.getModelData(Resource.getStream("kugel.obj"));
        System.out.println("### INFO / creating " + (myModelData.vertices.length / 9) + " triangles.");

        /* create model deflectors */
        myModelData.scale(new Vector3f(100, 100, 100));
        final Vector3f myYOffset = new Vector3f(300, 500, 0);

        for (int i = 0; i < myModelData.vertices.length / 9; i++) {
            TriangleDeflector myTriangleDeflector = new TriangleDeflector();
            myTriangleDeflector.a().set(myModelData.vertices[i * 9 + 0],
                                        myModelData.vertices[i * 9 + 1],
                                        myModelData.vertices[i * 9 + 2]);
            myTriangleDeflector.b().set(myModelData.vertices[i * 9 + 3],
                                        myModelData.vertices[i * 9 + 4],
                                        myModelData.vertices[i * 9 + 5]);
            myTriangleDeflector.c().set(myModelData.vertices[i * 9 + 6],
                                        myModelData.vertices[i * 9 + 7],
                                        myModelData.vertices[i * 9 + 8]);
            myTriangleDeflector.a().add(myYOffset);
            myTriangleDeflector.b().add(myYOffset);
            myTriangleDeflector.c().add(myYOffset);
            myTriangleDeflector.coefficientofrestitution(0.25f);
            _myParticleSystem.add(myTriangleDeflector);
        }

        /* create ground reflectors */
        _myGroudPlaneDeflector = new PlaneDeflector();
        _myGroudPlaneDeflector.plane().origin.set(width / 2, height, 0);
        _myGroudPlaneDeflector.plane().normal.set(0, -1, 0);
        _myGroudPlaneDeflector.coefficientofrestitution(0.75f);
        _myParticleSystem.add(_myGroudPlaneDeflector);

        /* forces */
        Gravity myGravity = new Gravity();
        myGravity.force().y = 50;
        _myParticleSystem.add(myGravity);

        ViscousDrag myViscousDrag = new ViscousDrag();
        myViscousDrag.coefficient = 0.1f;
        _myParticleSystem.add(myViscousDrag);
    }


    public void keyPressed() {
        if (key == ',') {
            _myYRotation += 0.1f;
        }
        if (key == '.') {
            _myYRotation -= 0.1f;
        }
    }


    public void draw() {
        System.out.println(frameRate);
        System.out.println("particles: " + _myParticleSystem.particles().size());

        _myParticleSystem.step(1f / frameRate);

        /* -- */
        if (mousePressed) {
            spawnParticle();
            spawnParticle();
        }

        /* draw particles */
        _myYRotation += 0.1f / frameRate;
        translate(width / 2, 0, -width);
        rotateY(_myYRotation);
        translate(-width / 2, 0, 0);
        background(1);

        stroke(0, 0.5f);
        for (Particle myParticle : _myParticleSystem.particles()) {
            final float myAgeRatio = 1 - ((ShortLivedParticle)myParticle).ageRatio();
            pushMatrix();
            translate(myParticle.position().x, myParticle.position().y, myParticle.position().z);
            ellipse(0, 0, myAgeRatio * 15, myAgeRatio * 15);
            popMatrix();
        }

        /* draw trianlge reflectors */
        if (keyPressed) {
            for (IForce myForce : _myParticleSystem.forces()) {
                if (myForce instanceof TriangleDeflector) {
                    DrawLib.draw(g,
                                 (TriangleDeflector)myForce,
                                 color(0, 0.25f),
                                 color(1, 0, 0, 0.05f),
                                 color(1, 0.5f, 0, 0.75f));
                }
            }
        }
    }


    private void spawnParticle() {
        ShortLivedParticle myParticle = _myParticleSystem.makeParticle(ShortLivedParticle.class);
        myParticle.velocity().x = (mouseX - pmouseX) * 25 + random(-10, 10);
        myParticle.velocity().y = (mouseY - pmouseY) * 25 + random(-10, 10);
        myParticle.setMaxAge(10);
        myParticle.position().set(random(width * 0.25f, width * 0.75f),
                                  mouseY,
                                  random(-width / 4, width / 4));
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {TestModelReflection.class.getName()});
    }
}
