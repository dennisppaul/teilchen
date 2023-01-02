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

/**
 * a 3x3 matrix.
 * rotation
 * rxx ryx rzx rxy ryy rzy rxz ryy rzz
 * scale
 * sx 0 0 0 sy 0 0 0 sz
 * also read 'The Matrix and Quaternions FAQ' at http://www.flipcode.com/documents/matrfaq.html
 */

import processing.core.PVector;

import java.io.Serializable;

public class Matrix3f implements Serializable {

    public static final int IDENTITY = 1;
    private static final long serialVersionUID = 104839874874759581L;

    public Matrix3f() {
        xx = 0.0f;
        xy = 0.0f;
        xz = 0.0f;
        yx = 0.0f;
        yy = 0.0f;
        yz = 0.0f;
        zx = 0.0f;
        zy = 0.0f;
        zz = 0.0f;
    }

    public Matrix3f(Matrix3f pMatrix) {
        set(pMatrix);
    }

    public Matrix3f(int pType) {
        switch (pType) {
            case IDENTITY:
                setIdentity();
                break;
        }
    }

    public final void set(float[] mArrayRepresentation) {
        xx = mArrayRepresentation[0];
        yx = mArrayRepresentation[1];
        zx = mArrayRepresentation[2];
        xy = mArrayRepresentation[3];
        yy = mArrayRepresentation[4];
        zy = mArrayRepresentation[5];
        xz = mArrayRepresentation[6];
        yz = mArrayRepresentation[7];
        zz = mArrayRepresentation[8];
    }

    public final void set(Matrix3f pMatrix) {
        xx = pMatrix.xx;
        xy = pMatrix.xy;
        xz = pMatrix.xz;
        yx = pMatrix.yx;
        yy = pMatrix.yy;
        yz = pMatrix.yz;
        zx = pMatrix.zx;
        zy = pMatrix.zy;
        zz = pMatrix.zz;
    }

    public void setIdentity() {
        xx = 1.0f;
        xy = 0.0f;
        xz = 0.0f;
        yx = 0.0f;
        yy = 1.0f;
        yz = 0.0f;
        zx = 0.0f;
        zy = 0.0f;
        zz = 1.0f;
    }

    public final void setZero() {
        xx = 0.0f;
        xy = 0.0f;
        xz = 0.0f;
        yx = 0.0f;
        yy = 0.0f;
        yz = 0.0f;
        zx = 0.0f;
        zy = 0.0f;
        zz = 0.0f;
    }

    public void add(float pValue) {
        xx += pValue;
        xy += pValue;
        xz += pValue;
        yx += pValue;
        yy += pValue;
        yz += pValue;
        zx += pValue;
        zy += pValue;
        zz += pValue;
    }

    public void add(Matrix3f pMatrixA, Matrix3f pMatrixB) {
        xx = pMatrixA.xx + pMatrixB.xx;
        xy = pMatrixA.xy + pMatrixB.xy;
        xz = pMatrixA.xz + pMatrixB.xz;
        yx = pMatrixA.yx + pMatrixB.yx;
        yy = pMatrixA.yy + pMatrixB.yy;
        yz = pMatrixA.yz + pMatrixB.yz;
        zx = pMatrixA.zx + pMatrixB.zx;
        zy = pMatrixA.zy + pMatrixB.zy;
        zz = pMatrixA.zz + pMatrixB.zz;
    }

    public void add(Matrix3f pMatrix) {
        xx += pMatrix.xx;
        xy += pMatrix.xy;
        xz += pMatrix.xz;
        yx += pMatrix.yx;
        yy += pMatrix.yy;
        yz += pMatrix.yz;
        zx += pMatrix.zx;
        zy += pMatrix.zy;
        zz += pMatrix.zz;
    }

    public void sub(Matrix3f pMatrixA, Matrix3f pMatrixB) {
        xx = pMatrixA.xx - pMatrixB.xx;
        xy = pMatrixA.xy - pMatrixB.xy;
        xz = pMatrixA.xz - pMatrixB.xz;
        yx = pMatrixA.yx - pMatrixB.yx;
        yy = pMatrixA.yy - pMatrixB.yy;
        yz = pMatrixA.yz - pMatrixB.yz;
        zx = pMatrixA.zx - pMatrixB.zx;
        zy = pMatrixA.zy - pMatrixB.zy;
        zz = pMatrixA.zz - pMatrixB.zz;
    }

    public void sub(Matrix3f pMatrix) {
        xx -= pMatrix.xx;
        xy -= pMatrix.xy;
        xz -= pMatrix.xz;
        yx -= pMatrix.yx;
        yy -= pMatrix.yy;
        yz -= pMatrix.yz;
        zx -= pMatrix.zx;
        zy -= pMatrix.zy;
        zz -= pMatrix.zz;
    }

    public void transpose() {

        /*
         * NOTE if the matrix is a rotation matrix ie the determinant is 1, the
         * transpose is equivalent to the invers of the matrix.
         */
        float mSwap = yx;
        yx = xy;
        xy = mSwap;
        mSwap = zx;
        zx = xz;
        xz = mSwap;
        mSwap = zy;
        zy = yz;
        yz = mSwap;
    }

    public void transpose(Matrix3f pMatrix) {
        if (this != pMatrix) {
            xx = pMatrix.xx;
            xy = pMatrix.yx;
            xz = pMatrix.zx;
            yx = pMatrix.xy;
            yy = pMatrix.yy;
            yz = pMatrix.zy;
            zx = pMatrix.xz;
            zy = pMatrix.yz;
            zz = pMatrix.zz;
        } else {
            transpose();
        }
    }

    public final float determinant() {
        return xx * (yy * zz - yz * zy) + xy * (yz * zx - yx * zz) + xz * (yx * zy - yy * zx);
    }

    public final void invert() {
        float mDeterminant = determinant();
        if (mDeterminant == 0.0) {
            return;
        }
        mDeterminant = 1 / mDeterminant;
        set(yy * zz - zy * yz,
            zx * yz - yx * zz,
            yx * zy - zx * yy,
            zy * xz - xy * zz,
            xx * zz - zx * xz,
            zx * xy - xx * zy,
            xy * yz - yy * xz,
            yx * xz - xx * yz,
            xx * yy - yx * xy);
        multiply(mDeterminant);
    }

    public final void getXAxis(PVector pVector) {
        pVector.x = xx;
        pVector.y = yx;
        pVector.z = zx;
    }

    public final void getYAxis(PVector pVector) {
        pVector.x = xy;
        pVector.y = yy;
        pVector.z = zy;
    }

    public final void getZAxis(PVector pVector) {
        pVector.x = xz;
        pVector.y = yz;
        pVector.z = zz;
    }

    public final PVector getXAxis() {
        return new PVector(xx, yx, zx);
    }

    public final void setXAxis(PVector pVector) {
        xx = pVector.x;
        yx = pVector.y;
        zx = pVector.z;
    }

    public final PVector getYAxis() {
        return new PVector(xy, yy, zy);
    }

    public final void setYAxis(PVector pVector) {
        xy = pVector.x;
        yy = pVector.y;
        zy = pVector.z;
    }

    public final PVector getZAxis() {
        return new PVector(xz, yz, zz);
    }

    public final void setZAxis(PVector pVector) {
        xz = pVector.x;
        yz = pVector.y;
        zz = pVector.z;
    }

    public final void setXRotation(float pRadians) {
        float sin = (float) Math.sin(pRadians);
        float cos = (float) Math.cos(pRadians);

        xx = 1.0f;
        yx = 0.0f;
        zx = 0.0f;

        xy = 0.0f;
        yy = cos;
        zy = sin;

        xz = 0.0f;
        yz = -sin;
        zz = cos;
    }

    public final void setYRotation(float pRadians) {

        /**
         * @todo check why these differ from 'the matrix and quaternions faq'
         *
         * cos 0 sin(!) 0 1 0 -sin(!) 0 cos
         *
         */
        float sin = (float) Math.sin(pRadians);
        float cos = (float) Math.cos(pRadians);

        xx = cos;
        yx = 0.0f;
        zx = -sin;

        xy = 0.0f;
        yy = 1.0f;
        zy = 0.0f;

        xz = sin;
        yz = 0.0f;
        zz = cos;
    }

    public final void setZRotation(float pRadians) {
        float sin = (float) Math.sin(pRadians);
        float cos = (float) Math.cos(pRadians);

        xx = cos;
        yx = sin;
        zx = 0.0f;

        xy = -sin;
        yy = cos;
        zy = 0.0f;

        xz = 0.0f;
        yz = 0.0f;
        zz = 1.0f;
    }

    public final void setXYZRotation(PVector pRotation) {
        setXYZRotation(pRotation.x, pRotation.y, pRotation.z);
    }

    public final void setXYZRotation(float pX, float pY, float pZ) {
        /* using radiants */
        final float a = (float) Math.cos(pX);
        final float b = (float) Math.sin(pX);
        final float c = (float) Math.cos(pY);
        final float d = (float) Math.sin(pY);
        final float e = (float) Math.cos(pZ);
        final float f = (float) Math.sin(pZ);

        final float ad = a * d;
        final float bd = b * d;

        xx = c * e;
        yx = bd * e + a * f;
        zx = -ad * e + b * f;

        xy = -c * f;
        yy = -bd * f + a * e;
        zy = ad * f + b * e;

        xz = d;
        yz = -b * c;
        zz = a * c;
    }

    public final void setRotation(final Vector4f pRotation) {
        final float u = pRotation.x;
        final float v = pRotation.y;
        final float w = pRotation.z;

        final float rcos = (float) Math.cos(pRotation.w);
        final float rsin = (float) Math.sin(pRotation.w);

        xx = rcos + u * u * (1 - rcos);
        yx = w * rsin + v * u * (1 - rcos);
        zx = -v * rsin + w * u * (1 - rcos);

        xy = -w * rsin + u * v * (1 - rcos);
        yy = rcos + v * v * (1 - rcos);
        zy = u * rsin + w * v * (1 - rcos);

        xz = v * rsin + u * w * (1 - rcos);
        yz = -u * rsin + v * w * (1 - rcos);
        zz = rcos + w * w * (1 - rcos);
    }

    public final void multiply(float pValue) {
        xx *= pValue;
        xy *= pValue;
        xz *= pValue;
        yx *= pValue;
        yy *= pValue;
        yz *= pValue;
        zx *= pValue;
        zy *= pValue;
        zz *= pValue;
    }

    public final void multiply(Matrix3f pMatrix) {
        float tmp1 = xx * pMatrix.xx + xy * pMatrix.yx + xz * pMatrix.zx;
        float tmp2 = xx * pMatrix.xy + xy * pMatrix.yy + xz * pMatrix.zy;
        float tmp3 = xx * pMatrix.xz + xy * pMatrix.yz + xz * pMatrix.zz;
        float tmp4 = yx * pMatrix.xx + yy * pMatrix.yx + yz * pMatrix.zx;
        float tmp5 = yx * pMatrix.xy + yy * pMatrix.yy + yz * pMatrix.zy;
        float tmp6 = yx * pMatrix.xz + yy * pMatrix.yz + yz * pMatrix.zz;
        float tmp7 = zx * pMatrix.xx + zy * pMatrix.yx + zz * pMatrix.zx;
        float tmp8 = zx * pMatrix.xy + zy * pMatrix.yy + zz * pMatrix.zy;
        float tmp9 = zx * pMatrix.xz + zy * pMatrix.yz + zz * pMatrix.zz;
        xx = tmp1;
        xy = tmp2;
        xz = tmp3;
        yx = tmp4;
        yy = tmp5;
        yz = tmp6;
        zx = tmp7;
        zy = tmp8;
        zz = tmp9;
    }

    public final void transform(PVector pVector) {
        pVector.set(xx * pVector.x + yx * pVector.y + zx * pVector.z,
                    xy * pVector.x + yy * pVector.y + zy * pVector.z,
                    pVector.z = xz * pVector.x + yz * pVector.y + zz * pVector.z);
    }

    public void setScale(PVector pScale) {
        xx = pScale.x;
        yx = 0.0f;
        zx = 0.0f;

        xy = 0.0f;
        yy = pScale.y;
        zy = 0.0f;

        xz = 0.0f;
        yz = 0.0f;
        zz = pScale.z;
    }

    public final float[] toArray() {
        mArrayRepresentation[0] = xx;
        mArrayRepresentation[1] = yx;
        mArrayRepresentation[2] = zx;
        mArrayRepresentation[3] = xy;
        mArrayRepresentation[4] = yy;
        mArrayRepresentation[5] = zy;
        mArrayRepresentation[6] = xz;
        mArrayRepresentation[7] = yz;
        mArrayRepresentation[8] = zz;
        return mArrayRepresentation;
    }

    public final float[] toArray4f() {
        /* so that opengl can understand it */
        mArray4fRepresentation[0] = xx;
        mArray4fRepresentation[1] = yx;
        mArray4fRepresentation[2] = zx;
        mArray4fRepresentation[3] = 0;
        mArray4fRepresentation[4] = xy;
        mArray4fRepresentation[5] = yy;
        mArray4fRepresentation[6] = zy;
        mArray4fRepresentation[7] = 0;
        mArray4fRepresentation[8] = xz;
        mArray4fRepresentation[9] = yz;
        mArray4fRepresentation[10] = zz;
        mArray4fRepresentation[11] = 0;
        mArray4fRepresentation[12] = 0;
        mArray4fRepresentation[13] = 0;
        mArray4fRepresentation[14] = 0;
        mArray4fRepresentation[15] = 1;
        return mArray4fRepresentation;
    }

    public String toString() {
        return xx + ", " + yx + ", " + zx + "\n" + xy + ", " + yy + ", " + zy + "\n" + xz + ", " + yz + ", " + zz;
    }

    private void set(float xx, float yx, float zx, float xy, float yy, float zy, float xz, float yz, float zz) {
        this.xx = xx;
        this.xy = xy;
        this.xz = xz;
        this.yx = yx;
        this.yy = yy;
        this.yz = yz;
        this.zx = zx;
        this.zy = zy;
        this.zz = zz;
    }

    public static void main(String[] args) {
        Matrix3f mMatrix;

        {
            /* invert and transpose */

            System.out.println("### invert and transpose\n");

            mMatrix = new Matrix3f(Matrix3f.IDENTITY);
            mMatrix.setXYZRotation(new PVector(0.2f, 0.3f, 0.4f));
            System.out.println(mMatrix);
            System.out.println();

            mMatrix.transpose();
            System.out.println(mMatrix);
            System.out.println();

            mMatrix.transpose();
            System.out.println(mMatrix);
            System.out.println();

            mMatrix = new Matrix3f(Matrix3f.IDENTITY);
            mMatrix.setXYZRotation(new PVector(0.2f, 0.3f, 0.4f));

            mMatrix.invert();
            System.out.println(mMatrix);
            System.out.println();

            mMatrix.invert();
            System.out.println(mMatrix);
            System.out.println();
        }

        {
            /* x */

            mMatrix = new Matrix3f(Matrix3f.IDENTITY);

            System.out.println("### rotation x\n");

            mMatrix.setXYZRotation(new PVector(0.2f, 0.0f, 0.0f));
            System.out.println(mMatrix);
            System.out.println();

            mMatrix.setXRotation(0.2f);
            System.out.println(mMatrix);
            System.out.println();

            mMatrix.setRotation(new Vector4f(1, 0, 0, 0.2f));
            System.out.println(mMatrix);
            System.out.println();

            /* y */
            System.out.println("### rotation y\n");

            mMatrix.setXYZRotation(new PVector(0.0f, 0.3f, 0.0f));
            System.out.println(mMatrix);
            System.out.println();

            mMatrix.setYRotation(0.3f);
            System.out.println(mMatrix);
            System.out.println();

            mMatrix.setRotation(new Vector4f(0, 1, 0, 0.3f));
            System.out.println(mMatrix);
            System.out.println();

            /* z */
            System.out.println("### rotation z\n");

            mMatrix.setXYZRotation(new PVector(0.0f, 0.0f, 0.4f));
            System.out.println(mMatrix);
            System.out.println();

            mMatrix.setZRotation(0.4f);
            System.out.println(mMatrix);
            System.out.println();

            mMatrix.setRotation(new Vector4f(0, 0, 1, 0.4f));
            System.out.println(mMatrix);
            System.out.println();
        }
    }
    public float xx;
    public float xy;
    public float xz;
    public float yx;
    public float yy;
    public float yz;
    public float zx;
    public float zy;
    public float zz;
    private final float[] mArrayRepresentation = new float[9];
    private final float[] mArray4fRepresentation = new float[16];
}
