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

    private final float[] mArrayRepresentation = new float[16];

    public TransformMatrix4f() {
        translation = new PVector();
        rotation = new Matrix3f();
    }

    public TransformMatrix4f(int pType) {
        translation = new PVector();
        rotation = new Matrix3f(pType);
        toArray();
    }

    public TransformMatrix4f(TransformMatrix4f pMatrix4f) {
        this();
        set(pMatrix4f);
        toArray();
    }

    public TransformMatrix4f(float[] pMatrixArray) {
        this();
        set(pMatrixArray);
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

    public final void set(float[] pArrayRepresentation) {
        rotation.xx = pArrayRepresentation[0];
        rotation.yx = pArrayRepresentation[1];
        rotation.zx = pArrayRepresentation[2];
        /* 3 */
        rotation.xy = pArrayRepresentation[4];
        rotation.yy = pArrayRepresentation[5];
        rotation.zy = pArrayRepresentation[6];
        /* 7 */
        rotation.xz = pArrayRepresentation[8];
        rotation.yz = pArrayRepresentation[9];
        rotation.zz = pArrayRepresentation[10];
        /* 11 */
        translation.x = pArrayRepresentation[12];
        translation.y = pArrayRepresentation[13];
        translation.z = pArrayRepresentation[14];
        /* 15 */
    }

    public final void multiply(float pValue) {
        rotation.xx *= pValue;
        rotation.xy *= pValue;
        rotation.xz *= pValue;
        translation.x *= pValue;
        rotation.yx *= pValue;
        rotation.yy *= pValue;
        rotation.yz *= pValue;
        translation.y *= pValue;
        rotation.zx *= pValue;
        rotation.zy *= pValue;
        rotation.zz *= pValue;
        translation.z *= pValue;
    }

    public final void multiply(float pValue,
                               TransformMatrix4f pMatrix4f) {
        rotation.xx = pMatrix4f.rotation.xx * pValue;
        rotation.xy = pMatrix4f.rotation.xy * pValue;
        rotation.xz = pMatrix4f.rotation.xz * pValue;
        translation.x = pMatrix4f.translation.x * pValue;
        rotation.yx = pMatrix4f.rotation.yx * pValue;
        rotation.yy = pMatrix4f.rotation.yy * pValue;
        rotation.yz = pMatrix4f.rotation.yz * pValue;
        translation.y = pMatrix4f.translation.y * pValue;
        rotation.zx = pMatrix4f.rotation.zx * pValue;
        rotation.zy = pMatrix4f.rotation.zy * pValue;
        rotation.zz = pMatrix4f.rotation.zz * pValue;
        translation.z = pMatrix4f.translation.z * pValue;
    }

    public final void multiply(TransformMatrix4f pMatrix4f) {
        float tmp1 = rotation.xx * pMatrix4f.rotation.xx + rotation.xy * pMatrix4f.rotation.yx
                     + rotation.xz * pMatrix4f.rotation.zx + translation.x * TransformMatrix4f.ZERO;
        float tmp2 = rotation.xx * pMatrix4f.rotation.xy + rotation.xy * pMatrix4f.rotation.yy
                     + rotation.xz * pMatrix4f.rotation.zy + translation.x * TransformMatrix4f.ZERO;
        float tmp3 = rotation.xx * pMatrix4f.rotation.xz + rotation.xy * pMatrix4f.rotation.yz
                     + rotation.xz * pMatrix4f.rotation.zz + translation.x * TransformMatrix4f.ZERO;
        float tmp4 = rotation.xx * pMatrix4f.translation.x + rotation.xy * pMatrix4f.translation.y
                     + rotation.xz * pMatrix4f.translation.z + translation.x * TransformMatrix4f.ONE;

        float tmp5 = rotation.yx * pMatrix4f.rotation.xx + rotation.yy * pMatrix4f.rotation.yx
                     + rotation.yz * pMatrix4f.rotation.zx + translation.y * TransformMatrix4f.ZERO;
        float tmp6 = rotation.yx * pMatrix4f.rotation.xy + rotation.yy * pMatrix4f.rotation.yy
                     + rotation.yz * pMatrix4f.rotation.zy + translation.y * TransformMatrix4f.ZERO;
        float tmp7 = rotation.yx * pMatrix4f.rotation.xz + rotation.yy * pMatrix4f.rotation.yz
                     + rotation.yz * pMatrix4f.rotation.zz + translation.y * TransformMatrix4f.ZERO;
        float tmp8 = rotation.yx * pMatrix4f.translation.x + rotation.yy * pMatrix4f.translation.y
                     + rotation.yz * pMatrix4f.translation.z + translation.y * TransformMatrix4f.ONE;

        float tmp9 = rotation.zx * pMatrix4f.rotation.xx + rotation.zy * pMatrix4f.rotation.yx
                     + rotation.zz * pMatrix4f.rotation.zx + translation.z * TransformMatrix4f.ZERO;
        float tmp10 = rotation.zx * pMatrix4f.rotation.xy + rotation.zy * pMatrix4f.rotation.yy
                      + rotation.zz * pMatrix4f.rotation.zy + translation.z * TransformMatrix4f.ZERO;
        float tmp11 = rotation.zx * pMatrix4f.rotation.xz + rotation.zy * pMatrix4f.rotation.yz
                      + rotation.zz * pMatrix4f.rotation.zz + translation.z * TransformMatrix4f.ZERO;
        float tmp12 = rotation.zx * pMatrix4f.translation.x + rotation.zy * pMatrix4f.translation.y
                      + rotation.zz * pMatrix4f.translation.z + translation.z * TransformMatrix4f.ONE;

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

    public final void transform(PVector pResult) {
        /**
         * @todo check if this is right...
         */
        pResult.set(rotation.xx * pResult.x
                      + rotation.xy * pResult.y
                      + rotation.xz * pResult.z
                      + translation.x,
                      rotation.yx * pResult.x
                      + rotation.yy * pResult.y
                      + rotation.yz * pResult.z
                      + translation.y,
                      rotation.zx * pResult.x
                      + rotation.zy * pResult.y
                      + rotation.zz * pResult.z
                      + translation.z);
    }

    public final float[] toArray() {
        /* so that opengl can understand it */
        mArrayRepresentation[0] = rotation.xx;
        mArrayRepresentation[1] = rotation.yx;
        mArrayRepresentation[2] = rotation.zx;
        mArrayRepresentation[3] = ZERO;
        mArrayRepresentation[4] = rotation.xy;
        mArrayRepresentation[5] = rotation.yy;
        mArrayRepresentation[6] = rotation.zy;
        mArrayRepresentation[7] = ZERO;
        mArrayRepresentation[8] = rotation.xz;
        mArrayRepresentation[9] = rotation.yz;
        mArrayRepresentation[10] = rotation.zz;
        mArrayRepresentation[11] = ZERO;
        mArrayRepresentation[12] = translation.x;
        mArrayRepresentation[13] = translation.y;
        mArrayRepresentation[14] = translation.z;
        mArrayRepresentation[15] = ONE;
        return mArrayRepresentation;
    }

    public String toString() {
        return rotation.xx + ", " + rotation.yx + ", " + rotation.zx + ", " + "0.0" + "\n" + rotation.xy + ", "
               + rotation.yy + ", " + rotation.zy + ", " + "0.0" + "\n" + rotation.xz + ", " + rotation.yz + ", "
               + rotation.zz + ", " + "0.0" + "\n" + translation.x + ", " + translation.y + ", " + translation.z
               + ", " + "1.0";
    }

    public static void main(String[] args) {
        /* multiplying matrices */
        TransformMatrix4f mScaleMatrix = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
        mScaleMatrix.rotation.setXAxis(new PVector(2, 0, 0));
        mScaleMatrix.rotation.setYAxis(new PVector(0, 2, 0));
        mScaleMatrix.rotation.setZAxis(new PVector(0, 0, 2));

        TransformMatrix4f mTranslateMatrix = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
        mTranslateMatrix.translation.set(2,
                                          3,
                                          4);

        mScaleMatrix.multiply(mScaleMatrix);
        mScaleMatrix.multiply(mTranslateMatrix);
        System.out.println(mScaleMatrix);

        /* transform position */
        System.out.println("\n### translate");
        System.out.println(mTranslateMatrix);
        System.out.println();
        PVector mVector = new PVector(10, 5, 7);
        mTranslateMatrix.transform(mVector);
        System.out.println(mVector);
    }
}
