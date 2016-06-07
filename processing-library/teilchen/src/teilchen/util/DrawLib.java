/*
 * Teilchen
 *
 * Copyright (C) 2015
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
package teilchen.util;

import java.util.List;
import processing.core.PGraphics;
import processing.core.PVector;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.force.Attractor;
import teilchen.force.IForce;
import teilchen.force.Spring;
import teilchen.force.TriangleDeflector;

public class DrawLib {

    public static void drawAttractor(final PGraphics g,
                                     final List<IForce> theForces,
                                     int theColor) {
        for (final IForce myForce : theForces) {
            if (myForce instanceof Attractor) {
                draw(g, (Attractor) myForce, theColor);
            }
        }
    }

    /**
     * draw attractors.
     *
     * @param g           PGraphics
     * @param myAttractor Attractor
     * @param theColor    int
     */
    public static void draw(final PGraphics g, final Attractor myAttractor, int theColor) {
        g.sphereDetail(6);
        g.noFill();
        g.stroke(theColor);

        g.pushMatrix();
        g.translate(myAttractor.position().x,
                    myAttractor.position().y,
                    myAttractor.position().z);
        g.sphere(myAttractor.radius());
        g.popMatrix();
    }

    public static void drawParticles(final PGraphics g,
                                     final Physics theParticleSystem,
                                     float theSize,
                                     int theColor) {
        draw(g,
             theParticleSystem.particles(),
             theSize,
             theColor);
    }

    public static void drawParticles(final PGraphics g,
                                     final Physics theParticleSystem,
                                     float theSize,
                                     int theStrokeColor,
                                     int theFillColor) {
        draw(g,
             theParticleSystem.particles(),
             theSize,
             theStrokeColor,
             theFillColor);
    }

    public static void drawSprings(final PGraphics g,
                                   final Physics theParticleSystem,
                                   int theColor) {
        /* draw springs */
        g.stroke(theColor);
        for (int i = 0; i < theParticleSystem.forces().size(); i++) {
            if (theParticleSystem.forces(i) instanceof Spring) {
                Spring mySpring = (Spring) theParticleSystem.forces(i);
                g.line(mySpring.a().position().x,
                       mySpring.a().position().y,
                       mySpring.b().position().x,
                       mySpring.b().position().y);
            }
        }
    }

    /**
     * draw particles.
     *
     * @param g            PGraphics
     * @param theParticles Vector
     * @param theSize      float
     * @param theColor     int
     */
    public static void draw(final PGraphics g,
                            final List<Particle> theParticles,
                            float theSize,
                            int theColor) {
        g.stroke(theColor);
        g.noFill();
        for (Particle myParticle : theParticles) {
            g.pushMatrix();
            g.translate(myParticle.position().x,
                        myParticle.position().y,
                        myParticle.position().z);
            g.ellipse(0, 0, theSize, theSize);
            g.popMatrix();
        }
    }

    /**
     * draw particles.
     *
     * @param g              PGraphics
     * @param theParticles   Vector
     * @param theSize        float
     * @param theStrokeColor int
     * @param theFillColor
     */
    public static void draw(final PGraphics g,
                            final List<Particle> theParticles,
                            float theSize,
                            int theStrokeColor,
                            int theFillColor) {
        g.stroke(theStrokeColor);
        g.fill(theFillColor);
        for (Particle myParticle : theParticles) {
            g.pushMatrix();
            g.translate(myParticle.position().x,
                        myParticle.position().y,
                        myParticle.position().z);
            g.ellipse(0, 0, theSize, theSize);
            g.popMatrix();
        }
    }

    /**
     * draw triangle deflector with bounding box.
     *
     * @param g                    PGraphics
     * @param theTriangleDeflector TriangleDeflector
     * @param theTriangleColor     int
     * @param theBBColor           int
     * @param theNormalColor       int
     */
    public static void draw(final PGraphics g,
                            final TriangleDeflector theTriangleDeflector,
                            int theTriangleColor,
                            int theBBColor,
                            int theNormalColor) {
        /* triangle */
        int myTriangleColor = theTriangleColor;
        if (theTriangleDeflector.hit()) {
            myTriangleColor = theBBColor;
        }
        draw(g,
             theTriangleDeflector.a(), theTriangleDeflector.b(), theTriangleDeflector.c(),
             myTriangleColor,
             theNormalColor);

        /* bb */
        draw(g,
             theTriangleDeflector.boundingbox(),
             theBBColor);
    }

    /**
     * draw buunding box.
     *
     * @param g                              PGraphics
     * @param theWorldAxisAlignedBoundingBox WorldAxisAlignedBoundingBox
     * @param theColor                       int
     */
    public static void draw(final PGraphics g,
                            final WorldAxisAlignedBoundingBox theWorldAxisAlignedBoundingBox,
                            int theColor) {
        g.stroke(theColor);
        g.pushMatrix();
        g.translate(theWorldAxisAlignedBoundingBox.position.x,
                    theWorldAxisAlignedBoundingBox.position.y,
                    theWorldAxisAlignedBoundingBox.position.z);
        g.box(theWorldAxisAlignedBoundingBox.scale.x,
              theWorldAxisAlignedBoundingBox.scale.y,
              theWorldAxisAlignedBoundingBox.scale.z);
        g.popMatrix();
    }

    /**
     * draw a triangle with a normal
     *
     * @param g                PGraphics
     * @param a                PVector
     * @param b                PVector
     * @param c                PVector
     * @param theTriangleColor int
     * @param theNormalColor   int
     */
    public static void draw(final PGraphics g,
                            final PVector a, final PVector b, final PVector c,
                            int theTriangleColor, int theNormalColor) {
        g.stroke(theTriangleColor);
        g.beginShape(PGraphics.TRIANGLES);
        g.vertex(a.x, a.y, a.z);
        g.vertex(b.x, b.y, b.z);
        g.vertex(c.x, c.y, c.z);
        g.endShape();
        g.noFill();

        PVector myNormal = new PVector();
        Util.calculateNormal(a, b, c, myNormal);
        myNormal.mult(75);

        PVector myCenterOfMass = new PVector();
        myCenterOfMass.add(a);
        myCenterOfMass.add(b);
        myCenterOfMass.add(c);
        myCenterOfMass.mult(1f / 3f);

        g.stroke(theNormalColor);
        g.line(myCenterOfMass.x,
               myCenterOfMass.y,
               myCenterOfMass.z,
               myCenterOfMass.x + myNormal.x,
               myCenterOfMass.y + myNormal.y,
               myCenterOfMass.z + myNormal.z);
    }

    public static void cross2(final PGraphics g,
                              final PVector pPosition,
                              float pSize) {
        g.line(
                pPosition.x + pSize,
                pPosition.y + pSize,
                pPosition.z,
                pPosition.x - pSize,
                pPosition.y - pSize,
                pPosition.z);
        g.line(
                pPosition.x + pSize,
                pPosition.y - pSize,
                pPosition.z,
                pPosition.x - pSize,
                pPosition.y + pSize,
                pPosition.z);
    }

    public static void cross3(final PGraphics g,
                              final PVector pPosition,
                              float pSize) {
        g.line(pPosition.x - pSize,
               pPosition.y,
               pPosition.z,
               pPosition.x + pSize,
               pPosition.y,
               pPosition.z);
        g.line(pPosition.x,
               pPosition.y - pSize,
               pPosition.z,
               pPosition.x,
               pPosition.y + pSize,
               pPosition.z);
        g.line(pPosition.x,
               pPosition.y,
               pPosition.z - pSize,
               pPosition.x,
               pPosition.y,
               pPosition.z + pSize);
    }
}
