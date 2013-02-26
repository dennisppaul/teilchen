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

import java.util.Iterator;

import gestalt.candidates.JoglTerrain;

import mathematik.Matrix3f;
import mathematik.TransformMatrix4f;
import mathematik.Vector3f;

import teilchen.Particle;
import teilchen.Physics;
import teilchen.constraint.IConstraint;


public class TerrainCollider
        implements IConstraint {

    private TransformMatrix4f _myTransform;

    private Vector3f _myScale;

    public final int width;

    public final int height;

    public final float[][] heightfield; /* this is essentially the mesh */


    private final Matrix3f _myInversRotation;

    private float _myCoefficientOfRestitution = 0.5f;


    public TerrainCollider(int theWidth, int theHeight) {
        _myTransform = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
        _myScale = new Vector3f(1, 1, 1);
        width = theWidth;
        height = theHeight;
        _myInversRotation = new Matrix3f(Matrix3f.IDENTITY);
        heightfield = new float[width + 1][height + 1];
    }


    public TerrainCollider(JoglTerrain theTerrain) {
        width = theTerrain.getQuadsX();
        height = theTerrain.getQuadsY();
        heightfield = new float[width + 1][height + 1];

        _myInversRotation = new Matrix3f(Matrix3f.IDENTITY);
        _myTransform = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
        _myScale = new Vector3f(1, 1, 1);

        for (int x = 0; x < theTerrain.vertices().length; x++) {
            for (int y = 0; y < theTerrain.vertices()[x].length; y++) {
                Vector3f myVertex = new Vector3f(theTerrain.vertices()[x][y]);
                heightfield[x][y] = myVertex.z;
            }
        }
    }


    public void updateTerrain(JoglTerrain theTerrain) {
        for (int x = 0; x < theTerrain.vertices().length; x++) {
            for (int y = 0; y < theTerrain.vertices()[x].length; y++) {
                Vector3f myVertex = new Vector3f(theTerrain.vertices()[x][y]);
                heightfield[x][y] = myVertex.z;
            }
        }
    }


    public void apply(final Physics theParticleSystem) {
        /* cache invers rotation matrix */
        _myInversRotation.set(_myTransform.rotation);
        _myInversRotation.invert();

        /* constraint particles */
        final Iterator<Particle> iter = theParticleSystem.particles().iterator();
        while (iter.hasNext()) {
            handleEntity(iter.next());
        }
    }


    private void handleEntity(Particle theEntity) {
        /* transform position to terrain space */
        final Vector3f myPosition = new Vector3f(theEntity.position());
        toLocal(myPosition);

        /* round off */
        final int x = (int) Math.floor(myPosition.x);
        final int y = (int) Math.floor(myPosition.y);
        final float myRatioX = myPosition.x - x;
        final float myRatioY = myPosition.y - y;

        /* handle entites position in grid */
        if (checkBounds(x, y)) {
            final float myZ;
            final Vector3f myNormal = new Vector3f();
            if (myRatioX + myRatioY < 1) {
                /* first triangle (a b d) */
                final float a = heightfield[x][y];
                final float b = heightfield[x + 1][y];
                final float d = heightfield[x][y + 1];
                myZ = (b - a) * myRatioX + (d - a) * myRatioY + a;

                mathematik.Util.calculateNormal(new Vector3f(x, y, heightfield[x][y]),
                                                new Vector3f(x + 1, y, heightfield[x + 1][y]),
                                                new Vector3f(x, y + 1, heightfield[x][y + 1]),
                                                myNormal);
                _myTransform.rotation.transform(myNormal);
            } else {
                /* second triangle (b d c) */
                final float b = heightfield[x + 1][y];
                final float c = heightfield[x + 1][y + 1];
                final float d = heightfield[x][y + 1];
                myZ = (d - c) * (1 - myRatioX) + (b - c) * (1 - myRatioY) + c;

                mathematik.Util.calculateNormal(new Vector3f(x + 1, y, heightfield[x + 1][y]),
                                                new Vector3f(x + 1, y + 1, heightfield[x + 1][y + 1]),
                                                new Vector3f(x, y + 1, heightfield[x][y + 1]),
                                                myNormal);
                _myTransform.rotation.transform(myNormal);
            }
            if (myPosition.z < myZ) {
                myPosition.z = myZ;
                toGlobal(myPosition);
                theEntity.position().z = myPosition.z;

                teilchen.util.Util.reflectVelocity(theEntity,
                                                    myNormal,
                                                    _myCoefficientOfRestitution);
            }
        }
    }


    public void coefficientofrestitution(float theCoefficientOfRestitution) {
        _myCoefficientOfRestitution = theCoefficientOfRestitution;
    }


    public float coefficientofrestitution() {
        return _myCoefficientOfRestitution;
    }


    private void toLocal(final Vector3f thePosition) {
        /* translation */
        thePosition.sub(_myTransform.translation);
        /* rotation */
        _myTransform.rotation.transform(thePosition);
        /* scale */
        Vector3f myScale = new Vector3f(_myScale);
        myScale.x /= width;
        myScale.y /= height;
        thePosition.divide(myScale);
    }


    private void toGlobal(final Vector3f thePosition) {
        /* scale */
        Vector3f myScale = new Vector3f(_myScale);
        myScale.x /= width;
        myScale.y /= height;
        thePosition.scale(myScale);
        /* rotation */
        _myInversRotation.transform(thePosition);
        /* translation */
        thePosition.add(_myTransform.translation);
    }


    private boolean checkBounds(int theX, int theY) {
        if (theX < width && theX >= 0) {
            if (theY < height && theY >= 0) {
                return true;
            }
        }
        return false;
    }


    public boolean isInCollider(Vector3f thePosition) {
        final Vector3f myPosition = new Vector3f(thePosition);
        toLocal(myPosition);

        /* round off */
        final int x = (int) Math.floor(myPosition.x);
        final int y = (int) Math.floor(myPosition.y);
        if (x < width && x >= 0) {
            if (y < height && y >= 0) {
                return true;
            }
        }
        return false;
    }


    public Vector3f scale() {
        return _myScale;
    }


    public TransformMatrix4f transform() {
        return _myTransform;
    }
}
