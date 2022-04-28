/*
 * Teilchen
 *
 * This file is part of the *teilchen* library (https://github.com/dennisppaul/teilchen).
 * Copyright (c) 2020 Dennis P Paul.
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

import processing.core.PGraphics;
import processing.core.PVector;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.force.Attractor;
import teilchen.force.IForce;
import teilchen.force.Spring;
import teilchen.force.TriangleDeflector;

import java.util.List;

public class DrawLib {

    public static void drawAttractor(final PGraphics g,
                                     final List<IForce> pForces,
                                     int pColor) {
        for (final IForce mForce : pForces) {
            if (mForce instanceof Attractor) {
                draw(g, (Attractor) mForce, pColor);
            }
        }
    }

    /**
     * draw attractors.
     *
     * @param g          PGraphics
     * @param mAttractor Attractor
     * @param pColor     int
     */
    public static void draw(final PGraphics g, final Attractor mAttractor, int pColor) {
        g.sphereDetail(6);
        g.noFill();
        g.stroke(pColor);

        g.pushMatrix();
        g.translate(mAttractor.position().x,
                    mAttractor.position().y,
                    mAttractor.position().z);
        g.sphere(mAttractor.radius());
        g.popMatrix();
    }

    public static void drawParticles(final PGraphics g,
                                     final Physics pParticleSystem,
                                     float pSize,
                                     int pColor) {
        draw(g,
             pParticleSystem.particles(),
             pSize,
             pColor);
    }

    public static void drawParticles(final PGraphics g,
                                     final Physics pParticleSystem,
                                     float pSize,
                                     int pStrokeColor,
                                     int pFillColor) {
        draw(g,
             pParticleSystem.particles(),
             pSize,
             pStrokeColor,
             pFillColor);
    }

    public static void drawSprings(final PGraphics g,
                                   final Physics pParticleSystem,
                                   int pColor) {
        /* draw springs */
        g.stroke(pColor);
        for (int i = 0; i < pParticleSystem.forces().size(); i++) {
            if (pParticleSystem.forces(i) instanceof Spring) {
                Spring mSpring = (Spring) pParticleSystem.forces(i);
                g.line(mSpring.a().position().x,
                       mSpring.a().position().y,
                       mSpring.b().position().x,
                       mSpring.b().position().y);
            }
        }
    }

    /**
     * draw particles.
     *
     * @param g          PGraphics
     * @param pParticles particles
     * @param pSize      radius
     * @param pColor     stroke color
     */
    public static void draw(final PGraphics g,
                            final List<Particle> pParticles,
                            float pSize,
                            int pColor) {
        g.stroke(pColor);
        g.noFill();
        for (Particle mParticle : pParticles) {
            g.pushMatrix();
            g.translate(mParticle.position().x,
                        mParticle.position().y,
                        mParticle.position().z);
            g.ellipse(0, 0, pSize, pSize);
            g.popMatrix();
        }
    }

    /**
     * draw particles.
     *
     * @param g            PGraphics
     * @param pParticles   particles
     * @param pSize        radius
     * @param pStrokeColor stroke color
     * @param pFillColor   fill color
     */
    public static void draw(final PGraphics g,
                            final List<Particle> pParticles,
                            float pSize,
                            int pStrokeColor,
                            int pFillColor) {
        g.stroke(pStrokeColor);
        g.fill(pFillColor);
        for (Particle mParticle : pParticles) {
            g.pushMatrix();
            g.translate(mParticle.position().x,
                        mParticle.position().y,
                        mParticle.position().z);
            g.ellipse(0, 0, pSize, pSize);
            g.popMatrix();
        }
    }

    /**
     * draw triangle deflector with bounding box.
     *
     * @param g                  PGraphics
     * @param pTriangleDeflector triangle deflector
     * @param pTriangleColor     triangle color
     * @param pBBColor           bounding box color
     * @param pNormalColor       normal color
     */
    public static void draw(final PGraphics g,
                            final TriangleDeflector pTriangleDeflector,
                            int pTriangleColor,
                            int pBBColor,
                            int pNormalColor) {
        /* triangle */
        int mTriangleColor = pTriangleColor;
        if (pTriangleDeflector.hit()) {
            mTriangleColor = pBBColor;
        }
        draw(g,
             pTriangleDeflector.a(), pTriangleDeflector.b(), pTriangleDeflector.c(),
             mTriangleColor,
             pNormalColor);

        /* bb */
        draw(g,
             pTriangleDeflector.boundingbox(),
             pBBColor);
    }

    /**
     * draw buunding box.
     *
     * @param g                            PGraphics
     * @param pWorldAxisAlignedBoundingBox WorldAxisAlignedBoundingBox
     * @param pColor                       int
     */
    public static void draw(final PGraphics g,
                            final WorldAxisAlignedBoundingBox pWorldAxisAlignedBoundingBox,
                            int pColor) {
        g.stroke(pColor);
        g.pushMatrix();
        g.translate(pWorldAxisAlignedBoundingBox.position.x,
                    pWorldAxisAlignedBoundingBox.position.y,
                    pWorldAxisAlignedBoundingBox.position.z);
        g.box(pWorldAxisAlignedBoundingBox.scale.x,
              pWorldAxisAlignedBoundingBox.scale.y,
              pWorldAxisAlignedBoundingBox.scale.z);
        g.popMatrix();
    }

    /**
     * draw a triangle with a normal
     *
     * @param g              PGraphics
     * @param a              PVector
     * @param b              PVector
     * @param c              PVector
     * @param pTriangleColor int
     * @param pNormalColor   int
     */
    public static void draw(final PGraphics g,
                            final PVector a, final PVector b, final PVector c,
                            int pTriangleColor, int pNormalColor) {
        g.stroke(pTriangleColor);
        g.beginShape(PGraphics.TRIANGLES);
        g.vertex(a.x, a.y, a.z);
        g.vertex(b.x, b.y, b.z);
        g.vertex(c.x, c.y, c.z);
        g.endShape();
        g.noFill();

        PVector mNormal = new PVector();
        Util.calculateNormal(a, b, c, mNormal);
        mNormal.mult(75);

        PVector mCenterOfMass = new PVector();
        mCenterOfMass.add(a);
        mCenterOfMass.add(b);
        mCenterOfMass.add(c);
        mCenterOfMass.mult(1f / 3f);

        g.stroke(pNormalColor);
        g.line(mCenterOfMass.x,
               mCenterOfMass.y,
               mCenterOfMass.z,
               mCenterOfMass.x + mNormal.x,
               mCenterOfMass.y + mNormal.y,
               mCenterOfMass.z + mNormal.z);
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
