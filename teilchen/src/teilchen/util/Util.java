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
package teilchen.util;


import mathematik.Vector3f;

import processing.core.PMatrix3D;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.force.IForce;
import teilchen.force.TriangleDeflector;
import teilchen.force.TriangleDeflectorIndexed;

import java.util.Vector;
import teilchen.constraint.IConstraint;


public class Util {

    public final static void satisfyNeighborConstraints(final Vector<Particle> theParticles, final float theRelaxedness) {
        for (int i = 0; i < theParticles.size(); i++) {
            final Particle p1 = theParticles.get(i);
            for (int j = i + 1; j < theParticles.size(); j++) {
                final Particle p2 = theParticles.get(j);
                /* satisfy overlap */
                if (p1.position().almost(p2.position())) {
                    p1.position().x += 0.01f;
                    p2.position().x -= 0.01f;
                    continue;
                }
                /* recover bad positions */
                if (p1.position().isNaN()) {
                    p1.position().set(p1.old_position());
                }
                if (p2.position().isNaN()) {
                    p2.position().set(p2.old_position());
                }
                final float myDistance = p1.position().distance(p2.position());
                /* skip bad values */
                if (myDistance == 0.0f || Float.isNaN(myDistance)) {
                    continue;
                }
                final float myDesiredDistance = p1.radius() + p2.radius();
                if (myDistance < myDesiredDistance) {
                    final Vector3f myDiff = mathematik.Util.sub(p1.position(), p2.position());
                    myDiff.scale(1.0f / myDistance);
                    myDiff.scale(myDesiredDistance - myDistance);
                    myDiff.scale(0.5f);
                    myDiff.scale(theRelaxedness);
                    p1.position().add(myDiff);
                    p2.position().sub(myDiff);
                }
            }
        }
    }
    private static final Vector3f TMP_NORMAL = new Vector3f();

    private static final Vector3f TMP_TANGENT = new Vector3f();

    public final static void reflectVelocity(final Particle theParticle,
                                             final Vector3f theNormal,
                                             float theCoefficientOfRestitution) {
        final Vector3f myVelocity = theParticle.velocity();
        /* normal */
        TMP_NORMAL.set(theNormal);
        TMP_NORMAL.scale(theNormal.dot(myVelocity));
        /* tangent */
        TMP_TANGENT.sub(myVelocity, TMP_NORMAL);
        /* negate normal */
        TMP_NORMAL.scale(-theCoefficientOfRestitution);
        /* set reflection vector */
        myVelocity.add(TMP_TANGENT, TMP_NORMAL);

        /* also set old position */
        if (Physics.HINT_UPDATE_OLD_POSITION) {
            theParticle.old_position().set(theParticle.position());
        }
    }

    public final static void reflect(final Vector3f theVector, final Vector3f theNormal, final float theCoefficientOfRestitution) {
        final Vector3f myNormalComponent = new Vector3f();
        final Vector3f myTangentComponent = new Vector3f();

        /* normal */
        myNormalComponent.set(theNormal);
        myNormalComponent.scale(theNormal.dot(theVector));
        /* tangent */
        myTangentComponent.sub(theVector, myNormalComponent);
        /* negate normal */
        myNormalComponent.scale(-theCoefficientOfRestitution);
        /* set reflection vector */
        theVector.add(myTangentComponent, myNormalComponent);
    }

    public final static void reflect(final Vector3f theVector, final Vector3f theNormal) {
        /* normal */
        TMP_NORMAL.set(theNormal);
        TMP_NORMAL.scale(theNormal.dot(theVector));
        /* tangent */
        TMP_TANGENT.sub(theVector, TMP_NORMAL);
        /* negate normal */
        TMP_NORMAL.scale(-1.0f);
        /* set reflection vector */
        theVector.add(TMP_TANGENT, TMP_NORMAL);
    }

    public static final Vector<IConstraint> createTriangleDeflectors(final float[] theVertices,
                                                                final float theCoefficientOfRestitution) {
        final Vector<IConstraint> myDeflectors = new Vector<IConstraint>();
        for (int i = 0; i < theVertices.length / 9; i++) {
            final TriangleDeflector myTriangleDeflector = new TriangleDeflector();
            myTriangleDeflector.a().set(theVertices[i * 9 + 0],
                                        theVertices[i * 9 + 1],
                                        theVertices[i * 9 + 2]);
            myTriangleDeflector.b().set(theVertices[i * 9 + 3],
                                        theVertices[i * 9 + 4],
                                        theVertices[i * 9 + 5]);
            myTriangleDeflector.c().set(theVertices[i * 9 + 6],
                                        theVertices[i * 9 + 7],
                                        theVertices[i * 9 + 8]);
            myTriangleDeflector.coefficientofrestitution(theCoefficientOfRestitution);
            myTriangleDeflector.updateProperties();
            myDeflectors.add(myTriangleDeflector);
        }
        return myDeflectors;
    }

    public static final Vector<IConstraint> createTriangleDeflectorsIndexed(final float[] theVertices,
                                                                       final float theCoefficientOfRestitution) {
        final Vector<IConstraint> myDeflectors = new Vector<IConstraint>();
        for (int i = 0; i < theVertices.length / 9; i++) {
            final TriangleDeflectorIndexed myTriangleDeflector = new TriangleDeflectorIndexed(
                    theVertices,
                    i * 9 + 0,
                    i * 9 + 3,
                    i * 9 + 6);
            myTriangleDeflector.coefficientofrestitution(theCoefficientOfRestitution);
            myTriangleDeflector.updateProperties();
            myDeflectors.add(myTriangleDeflector);
        }
        return myDeflectors;
    }

    public static void pointAt(final PMatrix3D pResult,
                               final Vector3f pPosition,
                               final Vector3f pUpVector, /* should be normalized */
                               final Vector3f pPointAtPosition) {

        /* forward */
        final Vector3f mForwardVector = mathematik.Util.sub(pPosition, pPointAtPosition);
        mForwardVector.normalize();

        /* side */
        final Vector3f mSideVector = mathematik.Util.cross(pUpVector, mForwardVector);
        mSideVector.normalize();

        /* up */
        final Vector3f mUpVector = mathematik.Util.cross(mForwardVector, mSideVector);
        mUpVector.normalize();

        if (!mSideVector.isNaN()
                && !mUpVector.isNaN()
                && !mForwardVector.isNaN()) {
            /* x */
            pResult.m00 = mSideVector.x;
            pResult.m10 = mSideVector.y;
            pResult.m20 = mSideVector.z;
            /* y */
            pResult.m01 = mUpVector.x;
            pResult.m11 = mUpVector.y;
            pResult.m21 = mUpVector.z;
            /* z */
            pResult.m02 = mForwardVector.x;
            pResult.m12 = mForwardVector.y;
            pResult.m22 = mForwardVector.z;
        }
    }
}
