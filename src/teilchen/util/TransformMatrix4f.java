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

import java.io.Serializable;
import processing.core.PVector;

/**
 *
 * special form of a 4x4 matrix. first of all the way we represent the matrix in
 * this class is similar to the way opengl handles the row column issue.
 *
 * transform matrix -- r = rotation, t = translation
 *
 * rxx rxy rxz 0 ryx ryy ryz 0 rzx rzy rzz 0 tx ty tz 1
 *
 * the transform matrix is special in the way that is a 3x3 matrix for rotation
 * and scale and a translation vector. the remaining four values a constants.
 *
 * this is the way opengl specifies a 4x4 matrix and also the way 'toArray'
 * returns an array.
 *
 * m[0] m[4] m[8] m[12]
 *
 * m[1] m[5] m[9] m[13]
 *
 * M=( )
 *
 * m[2] m[6] m[10] m[14]
 *
 * m[3] m[7] m[11] m[15]
 *
 *
 * here is an excerpt from the glMultMatrix man page.
 *
 * "In many computer languages 4x4 arrays are represented in row-major order.
 * The transformations just described represent these matrices in column-major
 * order. The order of the multiplication is important. For example, if the
 * current transformation is a rotation, and glMultMatrix is called with a
 * translation matrix, the translation is done directly on the coordinates to be
 * transformed, while the rotation is done on the results of that translation."
 *
 * also read 'The Matrix and Quaternions FAQ' at
 * http://www.flipcode.com/documents/matrfaq.html
 */
public class TransformMatrix4f
        implements Serializable {

    public static int IDENTITY = 1;

    public Matrix3f rotation;

    public PVector translation;

    private static final long serialVersionUID = 2946060493174800199L;

    private static final float ZERO = 0;

    private static final float ONE = 1;

    private final float[] _myArrayRepresentation = new float[16];

    public TransformMatrix4f() {
        translation = new PVector();
        rotation = new Matrix3f();
    }

    public TransformMatrix4f(int theType) {
        translation = new PVector();
        rotation = new Matrix3f(theType);
        toArray();
    }

    public TransformMatrix4f(TransformMatrix4f theMatrix4f) {
        this();
        set(theMatrix4f);
        toArray();
    }

    public TransformMatrix4f(float[] theMatrixArray) {
        this();
        set(theMatrixArray);
        toArray();
    }

    public final void setIdentity() {
        translation.set(0,
                        0,
                        0);
        rotation.setIdentity();
    }

    public final void setRotationIdentity() {
        rotation.setIdentity();
    }

    public final void setZero() {
        translation.set(0,
                        0,
                        0);
        rotation.setZero();
    }

    public final float determinant() {
        float d = rotation.xx
                  * ((rotation.yy * rotation.zz * ONE + rotation.yz * translation.z * ZERO
                      + translation.y * rotation.zy * ZERO)
                     - translation.y * rotation.zz * ZERO - rotation.yy * translation.z * ZERO - rotation.yz
                                                                                                 * rotation.zy * ONE);
        d -= rotation.xy
             * ((rotation.yx * rotation.zz * ONE + rotation.yz * translation.z * ZERO
                 + translation.y * rotation.zx * ZERO)
                - translation.y * rotation.zz * ZERO - rotation.yx * translation.z * ZERO - rotation.yz
                                                                                            * rotation.zx * ONE);
        d += rotation.xz
             * ((rotation.yx * rotation.zy * ONE + rotation.yy * translation.z * ZERO
                 + translation.y * rotation.zx * ZERO)
                - translation.y * rotation.zy * ZERO - rotation.yx * translation.z * ZERO - rotation.yy
                                                                                            * rotation.zx * ONE);
        d -= translation.x
             * ((rotation.yx * rotation.zy * ZERO + rotation.yy * rotation.zz * ZERO + rotation.yz * rotation.zx * ZERO)
                - rotation.yz * rotation.zy * ZERO - rotation.yx * rotation.zz * ZERO - rotation.yy
                                                                                        * rotation.zx * ZERO);
        return d;
    }

    public final void set(TransformMatrix4f mat) {
        rotation.set(mat.rotation);
        translation.set(mat.translation);
    }

    public final void set(float[] theArrayRepresentation) {
        rotation.xx = theArrayRepresentation[0];
        rotation.yx = theArrayRepresentation[1];
        rotation.zx = theArrayRepresentation[2];
        /* 3 */
        rotation.xy = theArrayRepresentation[4];
        rotation.yy = theArrayRepresentation[5];
        rotation.zy = theArrayRepresentation[6];
        /* 7 */
        rotation.xz = theArrayRepresentation[8];
        rotation.yz = theArrayRepresentation[9];
        rotation.zz = theArrayRepresentation[10];
        /* 11 */
        translation.x = theArrayRepresentation[12];
        translation.y = theArrayRepresentation[13];
        translation.z = theArrayRepresentation[14];
        /* 15 */
    }

    public final void multiply(float theValue) {
        rotation.xx *= theValue;
        rotation.xy *= theValue;
        rotation.xz *= theValue;
        translation.x *= theValue;
        rotation.yx *= theValue;
        rotation.yy *= theValue;
        rotation.yz *= theValue;
        translation.y *= theValue;
        rotation.zx *= theValue;
        rotation.zy *= theValue;
        rotation.zz *= theValue;
        translation.z *= theValue;
    }

    public final void multiply(float theValue,
                               TransformMatrix4f theMatrix4f) {
        rotation.xx = theMatrix4f.rotation.xx * theValue;
        rotation.xy = theMatrix4f.rotation.xy * theValue;
        rotation.xz = theMatrix4f.rotation.xz * theValue;
        translation.x = theMatrix4f.translation.x * theValue;
        rotation.yx = theMatrix4f.rotation.yx * theValue;
        rotation.yy = theMatrix4f.rotation.yy * theValue;
        rotation.yz = theMatrix4f.rotation.yz * theValue;
        translation.y = theMatrix4f.translation.y * theValue;
        rotation.zx = theMatrix4f.rotation.zx * theValue;
        rotation.zy = theMatrix4f.rotation.zy * theValue;
        rotation.zz = theMatrix4f.rotation.zz * theValue;
        translation.z = theMatrix4f.translation.z * theValue;
    }

    public final void multiply(TransformMatrix4f theMatrix4f) {
        float tmp1 = rotation.xx * theMatrix4f.rotation.xx + rotation.xy * theMatrix4f.rotation.yx
                     + rotation.xz * theMatrix4f.rotation.zx + translation.x * TransformMatrix4f.ZERO;
        float tmp2 = rotation.xx * theMatrix4f.rotation.xy + rotation.xy * theMatrix4f.rotation.yy
                     + rotation.xz * theMatrix4f.rotation.zy + translation.x * TransformMatrix4f.ZERO;
        float tmp3 = rotation.xx * theMatrix4f.rotation.xz + rotation.xy * theMatrix4f.rotation.yz
                     + rotation.xz * theMatrix4f.rotation.zz + translation.x * TransformMatrix4f.ZERO;
        float tmp4 = rotation.xx * theMatrix4f.translation.x + rotation.xy * theMatrix4f.translation.y
                     + rotation.xz * theMatrix4f.translation.z + translation.x * TransformMatrix4f.ONE;

        float tmp5 = rotation.yx * theMatrix4f.rotation.xx + rotation.yy * theMatrix4f.rotation.yx
                     + rotation.yz * theMatrix4f.rotation.zx + translation.y * TransformMatrix4f.ZERO;
        float tmp6 = rotation.yx * theMatrix4f.rotation.xy + rotation.yy * theMatrix4f.rotation.yy
                     + rotation.yz * theMatrix4f.rotation.zy + translation.y * TransformMatrix4f.ZERO;
        float tmp7 = rotation.yx * theMatrix4f.rotation.xz + rotation.yy * theMatrix4f.rotation.yz
                     + rotation.yz * theMatrix4f.rotation.zz + translation.y * TransformMatrix4f.ZERO;
        float tmp8 = rotation.yx * theMatrix4f.translation.x + rotation.yy * theMatrix4f.translation.y
                     + rotation.yz * theMatrix4f.translation.z + translation.y * TransformMatrix4f.ONE;

        float tmp9 = rotation.zx * theMatrix4f.rotation.xx + rotation.zy * theMatrix4f.rotation.yx
                     + rotation.zz * theMatrix4f.rotation.zx + translation.z * TransformMatrix4f.ZERO;
        float tmp10 = rotation.zx * theMatrix4f.rotation.xy + rotation.zy * theMatrix4f.rotation.yy
                      + rotation.zz * theMatrix4f.rotation.zy + translation.z * TransformMatrix4f.ZERO;
        float tmp11 = rotation.zx * theMatrix4f.rotation.xz + rotation.zy * theMatrix4f.rotation.yz
                      + rotation.zz * theMatrix4f.rotation.zz + translation.z * TransformMatrix4f.ZERO;
        float tmp12 = rotation.zx * theMatrix4f.translation.x + rotation.zy * theMatrix4f.translation.y
                      + rotation.zz * theMatrix4f.translation.z + translation.z * TransformMatrix4f.ONE;

        /*
         float temp13 = m30 * in2.m00 +
         m31 * in2.m10 +
         m32 * in2.m20 +
         m33 * in2.m30;
         float temp14 = m30 * in2.m01 +
         m31 * in2.m11 +
         m32 * in2.m21 +
         m33 * in2.m31;
         float temp15 = m30 * in2.m02 +
         m31 * in2.m12 +
         m32 * in2.m22 +
         m33 * in2.m32;
         float temp16 = m30 * in2.m03 +
         m31 * in2.m13 +
         m32 * in2.m23 +
         m33 * in2.m33;
         */
        rotation.xx = tmp1;
        rotation.xy = tmp2;
        rotation.xz = tmp3;
        translation.x = tmp4;
        rotation.yx = tmp5;
        rotation.yy = tmp6;
        rotation.yz = tmp7;
        translation.y = tmp8;
        rotation.zx = tmp9;
        rotation.zy = tmp10;
        rotation.zz = tmp11;
        translation.z = tmp12;
    }

    public final void transform(PVector theResult) {
        /**
         * @todo check if this is right...
         */
        theResult.set(rotation.xx * theResult.x
                      + rotation.xy * theResult.y
                      + rotation.xz * theResult.z
                      + translation.x,
                      rotation.yx * theResult.x
                      + rotation.yy * theResult.y
                      + rotation.yz * theResult.z
                      + translation.y,
                      rotation.zx * theResult.x
                      + rotation.zy * theResult.y
                      + rotation.zz * theResult.z
                      + translation.z);
    }

    public final float[] toArray() {
        /* so that opengl can understand it */
        _myArrayRepresentation[0] = rotation.xx;
        _myArrayRepresentation[1] = rotation.yx;
        _myArrayRepresentation[2] = rotation.zx;
        _myArrayRepresentation[3] = ZERO;
        _myArrayRepresentation[4] = rotation.xy;
        _myArrayRepresentation[5] = rotation.yy;
        _myArrayRepresentation[6] = rotation.zy;
        _myArrayRepresentation[7] = ZERO;
        _myArrayRepresentation[8] = rotation.xz;
        _myArrayRepresentation[9] = rotation.yz;
        _myArrayRepresentation[10] = rotation.zz;
        _myArrayRepresentation[11] = ZERO;
        _myArrayRepresentation[12] = translation.x;
        _myArrayRepresentation[13] = translation.y;
        _myArrayRepresentation[14] = translation.z;
        _myArrayRepresentation[15] = ONE;
        return _myArrayRepresentation;
    }

    public String toString() {
        return rotation.xx + ", " + rotation.yx + ", " + rotation.zx + ", " + "0.0" + "\n" + rotation.xy + ", "
               + rotation.yy + ", " + rotation.zy + ", " + "0.0" + "\n" + rotation.xz + ", " + rotation.yz + ", "
               + rotation.zz + ", " + "0.0" + "\n" + translation.x + ", " + translation.y + ", " + translation.z
               + ", " + "1.0";
    }

    public static void main(String[] args) {
        /* multiplying matrices */
        TransformMatrix4f myScaleMatrix = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
        myScaleMatrix.rotation.setXAxis(new PVector(2, 0, 0));
        myScaleMatrix.rotation.setYAxis(new PVector(0, 2, 0));
        myScaleMatrix.rotation.setZAxis(new PVector(0, 0, 2));

        TransformMatrix4f myTranslateMatrix = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
        myTranslateMatrix.translation.set(2,
                                          3,
                                          4);

        myScaleMatrix.multiply(myScaleMatrix);
        myScaleMatrix.multiply(myTranslateMatrix);
        System.out.println(myScaleMatrix);

        /* transform position */
        System.out.println("\n### translate");
        System.out.println(myTranslateMatrix);
        System.out.println();
        PVector myVector = new PVector(10, 5, 7);
        myTranslateMatrix.transform(myVector);
        System.out.println(myVector);
    }
}
