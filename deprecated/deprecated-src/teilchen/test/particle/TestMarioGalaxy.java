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


package teilchen.test.particle;


import java.util.Vector;

import gestalt.model.ModelData;
import gestalt.model.ModelLoaderOBJ;

import mathematik.Matrix3f;
import mathematik.Vector3f;

import data.Resource;
import teilchen.BasicParticle;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.force.Attractor;
import teilchen.force.TriangleDeflector;
import teilchen.force.ViscousDrag;
import teilchen.util.ParticleTrail;
import processing.core.PApplet;
import processing.core.PGraphics;
import teilchen.force.IForce;
import teilchen.util.Util;


public class TestMarioGalaxy
    extends PApplet {

    private Physics _myParticleSystem;

    private float _myRotation;

    private ModelData myModelData;

    private Attractor myAttractor;

    private Traveller myTraveller;

    private ParticleTrail myTrail;

    public void setup() {
        size(1024, 768, OPENGL);
        frameRate(120);
        smooth();
        noFill();

        /* particle system */
        _myParticleSystem = new Physics();

        /* load model */
        myModelData = ModelLoaderOBJ.getModelData(Resource.getStream("weirdobject.obj"));
        System.out.println("### INFO / creating " + (myModelData.vertices.length / 9) + " triangles.");

        /* create model deflectors */
        myModelData.scale(new Vector3f(2, 2, 2));
        final Vector3f myYOffset = new Vector3f(500, 500, 0);
        myModelData.translate(myYOffset);

        final float[] myVertices = myModelData.vertices;
        final Vector<IForce> myDeflectors = createTriangleDeflectors(myVertices);
        _myParticleSystem.addForces(myDeflectors);

        /* forces */
        myAttractor = new Attractor();
        myAttractor.radius(1000);
        myAttractor.strength(500);
        myAttractor.position().set(myYOffset);
        _myParticleSystem.add(myAttractor);

        ViscousDrag myViscousDrag = new ViscousDrag();
        myViscousDrag.coefficient = 0.1f;
        _myParticleSystem.add(myViscousDrag);

        myTraveller = new Traveller();
        _myParticleSystem.add(myTraveller);
        myTraveller.position().set(width / 2,
                                   0,
                                   random( -100, 100));

        myTrail = new ParticleTrail(new Physics(),
                                    myTraveller,
                                    0.2f,
                                    random(0.5f, 1));
        myTrail.setParticleClass(BasicParticle.class);

    }


    private Vector<IForce> createTriangleDeflectors(float[] myVertices) {
        final Vector<IForce> myDeflectors = new Vector<IForce> ();
        for (int i = 0; i < myVertices.length / 9; i++) {
            MyTriangleDeflector myTriangleDeflector = new MyTriangleDeflector();
            myTriangleDeflector.a().set(myVertices[i * 9 + 0],
                                        myVertices[i * 9 + 1],
                                        myVertices[i * 9 + 2]);
            myTriangleDeflector.b().set(myVertices[i * 9 + 3],
                                        myVertices[i * 9 + 4],
                                        myVertices[i * 9 + 5]);
            myTriangleDeflector.c().set(myVertices[i * 9 + 6],
                                        myVertices[i * 9 + 7],
                                        myVertices[i * 9 + 8]);
            myTriangleDeflector.coefficientofrestitution(0.0f);
            myDeflectors.add(myTriangleDeflector);
        }
        return myDeflectors;
    }


    public void keyPressed() {
        if (key == ',') {
            _myRotation += 0.1f;
        }
        if (key == '.') {
            _myRotation -= 0.1f;
        }
        if (key == ' ') {
            _myParticleSystem.particles().clear();
        }
    }


    public void draw() {
        _myParticleSystem.step(1f / frameRate);
        myTrail.loop(1f / frameRate);
        _myRotation += 1f / frameRate * 0.01f;

        /* draw particles */
        translate(width / 2, 0, -width);
        rotateY(_myRotation);
        translate( -width / 2, 0, 0);
        background(255);

        stroke(255, 0, 0, 127);
        for (Particle myParticle : _myParticleSystem.particles()) {
            if (myParticle instanceof Traveller) {
                Traveller myTraveller = (Traveller) myParticle;
                myTraveller.draw(g);
            }
        }

        stroke(0, 50);
        pushMatrix();
        translate(myAttractor.position().x, myAttractor.position().y, myAttractor.position().z);
        ellipse(0, 0, myAttractor.radius() * 2, myAttractor.radius() * 2);
        pushMatrix();
        rotateX(PI / 2);
        ellipse(0, 0, myAttractor.radius() * 2, myAttractor.radius() * 2);
        popMatrix();
        pushMatrix();
        rotateY(PI / 2);
        ellipse(0, 0, myAttractor.radius() * 2, myAttractor.radius() * 2);
        popMatrix();
        popMatrix();

        stroke(255, 127, 0, 127);
        drawTrail(myTrail);

        /* draw trianlge reflectors */
        stroke(0, 20);
        drawModel(g, myModelData);
    }


    private void drawModel(final PGraphics pg,
                           final ModelData myModelData) {
        pg.beginShape(PGraphics.TRIANGLES);
        for (int i = 0; i < myModelData.vertices.length; i += 3) {
            pg.vertex(myModelData.vertices[i + 0],
                      myModelData.vertices[i + 1],
                      myModelData.vertices[i + 2]);
        }
        pg.endShape();
    }


    private class MyTriangleDeflector
        extends TriangleDeflector {

        protected void markParticle(Particle theParticle) {
            if (theParticle instanceof Traveller) {
                final Traveller myTraveller = (Traveller) theParticle;
                myTraveller.setNormal(normal());
            }
        }
    }


    private class Traveller
        extends BasicParticle {

        private Matrix3f _myMatrix = new Matrix3f(Matrix3f.IDENTITY);

        private static final float SPEED = 100;

        public void accumulateInnerForce(final float theDeltaTime) {
//            force().add(mathematik.Util.scale(_myMatrix.getXAxis(), SPEED));
        }


        public void setNormal(Vector3f theNormal) {
            Vector3f myUp = new Vector3f(theNormal);
            myUp.normalize();
            Vector3f myForward = _myMatrix.getXAxis();
            Vector3f mySide = mathematik.Util.cross(myUp, myForward);
            mySide.normalize();
            myForward = mathematik.Util.cross(mySide, myUp);
            myForward.normalize();

            _myMatrix.setXAxis(myForward);
            _myMatrix.setYAxis(myUp);
            _myMatrix.setZAxis(mySide);

            velocity().set(mathematik.Util.scale(_myMatrix.getXAxis(), SPEED));
        }


        public void draw(PGraphics pg) {
            stroke(255, 0, 0);
            line(position(), mathematik.Util.add(position(), mathematik.Util.scale(_myMatrix.getXAxis(), 50)));
            stroke(0, 255, 0);
            line(position(), mathematik.Util.add(position(), mathematik.Util.scale(_myMatrix.getYAxis(), 50)));
            stroke(0, 0, 255);
            line(position(), mathematik.Util.add(position(), mathematik.Util.scale(_myMatrix.getZAxis(), 50)));
            stroke(0, 127);
            line(position(), mathematik.Util.add(position(), velocity()));
        }
    }


    private void drawTrail(ParticleTrail theTrail) {

        final Vector<Particle> _myFragments = theTrail.fragments();
        final Particle _myParticle = theTrail.particle();

        /* this is temporary */
        for (int i = 0; i < _myFragments.size() - 1; i++) {
            line(_myFragments.get(i).position().x,
                 _myFragments.get(i).position().y,
                 _myFragments.get(i).position().z,
                 _myFragments.get(i + 1).position().x,
                 _myFragments.get(i + 1).position().y,
                 _myFragments.get(i + 1).position().z);
        }

        if (_myFragments.size() > 1) {
            stroke(255, 255);
            line(_myFragments.get(_myFragments.size() - 1).position().x,
                 _myFragments.get(_myFragments.size() - 1).position().y,
                 _myFragments.get(_myFragments.size() - 1).position().z,
                 _myParticle.position().x,
                 _myParticle.position().y,
                 _myParticle.position().z);
        }
    }


    private void line(Vector3f a, Vector3f b) {
        line(a.x, a.y, a.z, b.x, b.y, b.z);
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {TestMarioGalaxy.class.getName()});
    }
}
