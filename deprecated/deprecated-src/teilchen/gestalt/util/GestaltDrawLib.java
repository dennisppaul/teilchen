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


package teilchen.gestalt.util;


import java.util.Vector;
import javax.media.opengl.GL;

import gestalt.Gestalt;
import gestalt.material.PointSprite;
import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.material.Material;
import gestalt.render.BasicRenderer;
import gestalt.shape.AbstractShape;
import gestalt.material.Color;
import gestalt.material.Material;
import gestalt.shape.Mesh;
import gestalt.material.texture.Bitmap;

import mathematik.Vector3f;

import teilchen.Particle;
import teilchen.Physics;
import teilchen.force.IForce;
import teilchen.force.Spring;
import teilchen.force.TriangleDeflector;


public class GestaltDrawLib {

    public static abstract class GestaltAbstactParticleView
        extends AbstractShape {

        public GestaltAbstactParticleView() {
            material = new Material();
        }


        public void draw(GLContext theRenderContext) {

            GL gl = (  theRenderContext).gl;

            /* begin material */
            material.begin(theRenderContext);

            /* draw particles */
            draw(gl);

            /* end material */
            material.end(theRenderContext);
        }


        public abstract void draw(GL gl);

    }


    /**
     * draw particles as diagonal crosses on the xy-plane.
     */
    public static class GestaltParticleViewLine
        extends GestaltAbstactParticleView {

        private Vector<Particle> _myParticles;

        private float _myRadius = 2;

        public GestaltParticleViewLine(Vector<Particle> theParticles) {
            _myParticles = theParticles;
        }


        public void draw(GL gl) {
            gl.glBegin(GL.GL_LINES);
            for (Particle myParticle : _myParticles) {
                gl.glVertex3f(myParticle.position().x - _myRadius,
                              myParticle.position().y - _myRadius,
                              myParticle.position().z);
                gl.glVertex3f(myParticle.position().x + _myRadius,
                              myParticle.position().y + _myRadius,
                              myParticle.position().z);
                gl.glVertex3f(myParticle.position().x + _myRadius,
                              myParticle.position().y - _myRadius,
                              myParticle.position().z);
                gl.glVertex3f(myParticle.position().x - _myRadius,
                              myParticle.position().y + _myRadius,
                              myParticle.position().z);
            }
            gl.glEnd();
        }
    }


    /**
     * draw forces as gestalt object.
     *
     * the following forces are drawn:
     *
     * TriangleDeflector
     * Attractor
     */
    public static class GestaltForceView
        extends AbstractShape {

        private final Physics _myParticleSystem;

        public final Color trianglecolor;

        public final Color normalcolor;

        public final Color boundingboxcolor;

        public float normalscale = 50;

        public boolean smooth = false;

        public boolean markhits = true;

        public GestaltForceView(final Physics theParticleSystem) {
            _myParticleSystem = theParticleSystem;
            trianglecolor = new Color(1, 0, 0, 1);
            normalcolor = new Color(1, 1, 0, 1);
            boundingboxcolor = new Color(1, 0.5f, 0, 1);
            material = new Material();
            material.wireframe = true;
        }


        public void draw(GLContext theRenderContext) {

            GL gl = (  theRenderContext).gl;

            /* begin material */
            material.begin(theRenderContext);

            /* smooth */
            if (smooth) {
                gl.glEnable(GL.GL_LINE_SMOOTH);
            }

            /* draw forces */
            for (IForce myForce : _myParticleSystem.forces()) {
                if (myForce instanceof TriangleDeflector) {
                    final TriangleDeflector myTriangleDeflector = (TriangleDeflector) myForce;

                    /* draw triangle */
                    if (myTriangleDeflector.hit() && markhits) {
                        gl.glColor4f(boundingboxcolor.r,
                                     boundingboxcolor.g,
                                     boundingboxcolor.b,
                                     boundingboxcolor.a);
                    } else {
                        gl.glColor4f(trianglecolor.r,
                                     trianglecolor.g,
                                     trianglecolor.b,
                                     trianglecolor.a);
                    }

                    gl.glBegin(GL.GL_TRIANGLES);
                    gl.glVertex3f(myTriangleDeflector.a().x, myTriangleDeflector.a().y, myTriangleDeflector.a().z);
                    gl.glVertex3f(myTriangleDeflector.b().x, myTriangleDeflector.b().y, myTriangleDeflector.b().z);
                    gl.glVertex3f(myTriangleDeflector.c().x, myTriangleDeflector.c().y, myTriangleDeflector.c().z);
                    gl.glEnd();

                    /* draw normal */
                    if (normalcolor.a != 0) {
                        Vector3f myNormal = new Vector3f();
                        mathematik.Util.calculateNormal(myTriangleDeflector.a(),
                                                        myTriangleDeflector.b(),
                                                        myTriangleDeflector.c(),
                                                        myNormal);
                        myNormal.scale(normalscale);

                        Vector3f myCenterOfMass = new Vector3f();
                        myCenterOfMass.add(myTriangleDeflector.a());
                        myCenterOfMass.add(myTriangleDeflector.b());
                        myCenterOfMass.add(myTriangleDeflector.c());
                        myCenterOfMass.scale(0.33f);

                        gl.glColor4f(normalcolor.r,
                                     normalcolor.g,
                                     normalcolor.b,
                                     normalcolor.a);
                        gl.glBegin(GL.GL_LINES);
                        gl.glVertex3f(myCenterOfMass.x,
                                      myCenterOfMass.y,
                                      myCenterOfMass.z);
                        gl.glVertex3f(myCenterOfMass.x + myNormal.x,
                                      myCenterOfMass.y + myNormal.y,
                                      myCenterOfMass.z + myNormal.z);
                        gl.glEnd();
                    }

                } else if (myForce instanceof Spring) {
                    final Spring mySpring = (Spring) myForce;

                    gl.glColor4f(trianglecolor.r,
                                 trianglecolor.g,
                                 trianglecolor.b,
                                 trianglecolor.a);
                    gl.glBegin(GL.GL_LINES);
                    gl.glVertex3f(mySpring.a().position().x,
                                  mySpring.a().position().y,
                                  mySpring.a().position().z);
                    gl.glVertex3f(mySpring.b().position().x,
                                  mySpring.b().position().y,
                                  mySpring.b().position().z);
                    gl.glEnd();
                }
            }

            /* smooth */
            if (smooth) {
                gl.glDisable(GL.GL_LINE_SMOOTH);
            }

            /* end material */
            material.end(theRenderContext);
        }
    }


    /**
     * draw particles as gestalt pointsprites.
     */
    public static class GestaltParticleView {

        private final int _myMaxParticles;

        private final Mesh _myMesh;

        private final PointSprite _myPointSprites;

        private final BasicRenderer _myGestalt;

        private final Physics _myParticleSystem;

        public GestaltParticleView(final Physics theParticleSystem,
                                   final BasicRenderer theGestalt,
                                   final Bitmap theBitmap,
                                   final int theMaxParticles) {
            _myParticleSystem = theParticleSystem;
            _myGestalt = theGestalt;
            _myMaxParticles = theMaxParticles;

            /* create mesh */
            _myMesh = _myGestalt.drawablefactory().mesh(false,
                                                        new float[_myMaxParticles * 3], 3,
                                                        null, 0,
                                                        null, 0,
                                                        null,
                                                        Gestalt.MESH_POINTS);

            /* create texture */
            _myPointSprites = new PointSprite();
            _myPointSprites.load(theBitmap);
            _myPointSprites.quadric = new float[] {10, 0.2f, 0.001f};
            _myPointSprites.pointsize = 300;
            _myPointSprites.minpointsize = 4;
            _myPointSprites.maxpointsize = 256;

            _myMesh.material().addPlugin(_myPointSprites);
            _myMesh.material().blendmode = Gestalt.MATERIAL_BLEND_MULTIPLY;
            _myMesh.material().depthtest = false;
            _myMesh.material().transparent = true;

            /* add to renderer */
            _myGestalt.bin(Gestalt.BIN_3D).add(_myMesh);
        }


        public Material material() {
            return _myMesh.material();
        }


        public Mesh mesh() {
            return _myMesh;
        }


        public PointSprite pointsprite() {
            return _myPointSprites;
        }


        public void loop(final float theDeltaTime) {
            final int myLength = Math.min(_myParticleSystem.particles().size(), _myMesh.vertices().length / 3);
            /* draw particles -- map particles to mesh */
            for (int i = 0; i < myLength; i++) {
                final Vector3f myPosition = _myParticleSystem.particles().get(i).position();
                _myMesh.vertices()[i * 3 + 0] = myPosition.x;
                _myMesh.vertices()[i * 3 + 1] = myPosition.y;
                _myMesh.vertices()[i * 3 + 2] = myPosition.z;
            }
            _myMesh.drawlength(myLength);
        }


        public int getMaxParticles() {
            return _myMaxParticles;
        }
    }
}
