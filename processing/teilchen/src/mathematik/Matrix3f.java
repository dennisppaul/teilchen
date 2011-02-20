/*
 * Mathematik
 *
 * Copyright (C) 2009 Patrick Kochlik + Dennis Paul
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

/**
 * a 3x3 matrix.
 *
 *  rotation
 *
 *    rxx ryx rzx
 *    rxy ryy rzy
 *    rxz ryy rzz
 *
 *  scale
 *
 *    sx 0  0
 *    0  sy 0
 *    0  0  sz
 *
 *
 * also read 'The Matrix and Quaternions FAQ' at http://www.flipcode.com/documents/matrfaq.html
 *
 */


package mathematik;


import java.io.Serializable;


public class Matrix3f
    implements Serializable {

    private static final long serialVersionUID = 104839874874759581L;

    public static final int IDENTITY = 1;

    public float xx;

    public float xy;

    public float xz;

    public float yx;

    public float yy;

    public float yz;

    public float zx;

    public float zy;

    public float zz;

    private final float[] _myArrayRepresentation = new float[9];

    private final float[] _myArray4fRepresentation = new float[16];

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


    public Matrix3f(Matrix3f theMatrix) {
        set(theMatrix);
    }


    public Matrix3f(int theType) {
        switch (theType) {
            case IDENTITY:
                setIdentity();
                break;
        }
    }


    private void set(float xx,
                     float yx,
                     float zx,
                     float xy,
                     float yy,
                     float zy,
                     float xz,
                     float yz,
                     float zz) {
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


    public final void set(float[] _myArrayRepresentation) {
        xx = _myArrayRepresentation[0];
        yx = _myArrayRepresentation[1];
        zx = _myArrayRepresentation[2];
        xy = _myArrayRepresentation[3];
        yy = _myArrayRepresentation[4];
        zy = _myArrayRepresentation[5];
        xz = _myArrayRepresentation[6];
        yz = _myArrayRepresentation[7];
        zz = _myArrayRepresentation[8];
    }


    public final void set(Matrix3f theMatrix) {
        xx = theMatrix.xx;
        xy = theMatrix.xy;
        xz = theMatrix.xz;
        yx = theMatrix.yx;
        yy = theMatrix.yy;
        yz = theMatrix.yz;
        zx = theMatrix.zx;
        zy = theMatrix.zy;
        zz = theMatrix.zz;
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


    public void add(float theValue) {
        xx += theValue;
        xy += theValue;
        xz += theValue;
        yx += theValue;
        yy += theValue;
        yz += theValue;
        zx += theValue;
        zy += theValue;
        zz += theValue;
    }


    public void add(Matrix3f theMatrixA,
                    Matrix3f theMatrixB) {
        xx = theMatrixA.xx + theMatrixB.xx;
        xy = theMatrixA.xy + theMatrixB.xy;
        xz = theMatrixA.xz + theMatrixB.xz;
        yx = theMatrixA.yx + theMatrixB.yx;
        yy = theMatrixA.yy + theMatrixB.yy;
        yz = theMatrixA.yz + theMatrixB.yz;
        zx = theMatrixA.zx + theMatrixB.zx;
        zy = theMatrixA.zy + theMatrixB.zy;
        zz = theMatrixA.zz + theMatrixB.zz;
    }


    public void add(Matrix3f theMatrix) {
        xx += theMatrix.xx;
        xy += theMatrix.xy;
        xz += theMatrix.xz;
        yx += theMatrix.yx;
        yy += theMatrix.yy;
        yz += theMatrix.yz;
        zx += theMatrix.zx;
        zy += theMatrix.zy;
        zz += theMatrix.zz;
    }


    public void sub(Matrix3f theMatrixA,
                    Matrix3f theMatrixB) {
        xx = theMatrixA.xx - theMatrixB.xx;
        xy = theMatrixA.xy - theMatrixB.xy;
        xz = theMatrixA.xz - theMatrixB.xz;
        yx = theMatrixA.yx - theMatrixB.yx;
        yy = theMatrixA.yy - theMatrixB.yy;
        yz = theMatrixA.yz - theMatrixB.yz;
        zx = theMatrixA.zx - theMatrixB.zx;
        zy = theMatrixA.zy - theMatrixB.zy;
        zz = theMatrixA.zz - theMatrixB.zz;
    }


    public void sub(Matrix3f theMatrix) {
        xx -= theMatrix.xx;
        xy -= theMatrix.xy;
        xz -= theMatrix.xz;
        yx -= theMatrix.yx;
        yy -= theMatrix.yy;
        yz -= theMatrix.yz;
        zx -= theMatrix.zx;
        zy -= theMatrix.zy;
        zz -= theMatrix.zz;
    }


    public void transpose() {

        /*
         * NOTE if the matrix is a rotation matrix ie the determinant is 1, the
         * transpose is equivalent to the invers of the matrix.
         */

        float mySwap = yx;
        yx = xy;
        xy = mySwap;
        mySwap = zx;
        zx = xz;
        xz = mySwap;
        mySwap = zy;
        zy = yz;
        yz = mySwap;
    }


    public void transpose(Matrix3f theMatrix) {
        if (this != theMatrix) {
            xx = theMatrix.xx;
            xy = theMatrix.yx;
            xz = theMatrix.zx;
            yx = theMatrix.xy;
            yy = theMatrix.yy;
            yz = theMatrix.zy;
            zx = theMatrix.xz;
            zy = theMatrix.yz;
            zz = theMatrix.zz;
        } else {
            transpose();
        }
    }


    public final float determinant() {
        return xx * (yy * zz - yz * zy) + xy * (yz * zx - yx * zz) + xz * (yx * zy - yy * zx);
    }


    public final void invert() {
        float myDeterminant = determinant();
        if (myDeterminant == 0.0) {
            return;
        }
        myDeterminant = 1 / myDeterminant;
        set(yy * zz - zy * yz,
            zx * yz - yx * zz,
            yx * zy - zx * yy,
            zy * xz - xy * zz,
            xx * zz - zx * xz,
            zx * xy - xx * zy,
            xy * yz - yy * xz,
            yx * xz - xx * yz,
            xx * yy - yx * xy);
        multiply(myDeterminant);
    }


    public final void setXAxis(Vector3f theVector) {
        xx = theVector.x;
        yx = theVector.y;
        zx = theVector.z;
    }


    public final void setYAxis(Vector3f theVector) {
        xy = theVector.x;
        yy = theVector.y;
        zy = theVector.z;
    }


    public final void setZAxis(Vector3f theVector) {
        xz = theVector.x;
        yz = theVector.y;
        zz = theVector.z;
    }


    public final void getXAxis(Vector3f theVector) {
        theVector.x = xx;
        theVector.y = yx;
        theVector.z = zx;
    }


    public final void getYAxis(Vector3f theVector) {
        theVector.x = xy;
        theVector.y = yy;
        theVector.z = zy;
    }


    public final void getZAxis(Vector3f theVector) {
        theVector.x = xz;
        theVector.y = yz;
        theVector.z = zz;
    }


    public final Vector3f getXAxis() {
        return new Vector3f(xx, yx, zx);
    }


    public final Vector3f getYAxis() {
        return new Vector3f(xy, yy, zy);
    }


    public final Vector3f getZAxis() {
        return new Vector3f(xz, yz, zz);
    }


    public final void setXRotation(float theRadians) {
        float sin = (float) Math.sin(theRadians);
        float cos = (float) Math.cos(theRadians);

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


    public final void setYRotation(float theRadians) {

        /**
         * @todo check why these differ from 'the matrix and quaternions faq'
         *
         * cos 0 sin(!) 0 1 0 -sin(!) 0 cos
         *
         */

        float sin = (float) Math.sin(theRadians);
        float cos = (float) Math.cos(theRadians);

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


    public final void setZRotation(float theRadians) {
        float sin = (float) Math.sin(theRadians);
        float cos = (float) Math.cos(theRadians);

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


    public final void setXYZRotation(Vector3f theRotation) {
        setXYZRotation(theRotation.x,
                       theRotation.y,
                       theRotation.z);
    }


    public final void setXYZRotation(float theX,
                                     float theY,
                                     float theZ) {
        /* using radiants */
        final float a = (float) Math.cos(theX);
        final float b = (float) Math.sin(theX);
        final float c = (float) Math.cos(theY);
        final float d = (float) Math.sin(theY);
        final float e = (float) Math.cos(theZ);
        final float f = (float) Math.sin(theZ);

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


    public final void setRotation(final Vector4f theRotation) {
        final float u = theRotation.x;
        final float v = theRotation.y;
        final float w = theRotation.z;

        final float rcos = (float) Math.cos(theRotation.w);
        final float rsin = (float) Math.sin(theRotation.w);

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


    public final void multiply(float theValue) {
        xx *= theValue;
        xy *= theValue;
        xz *= theValue;
        yx *= theValue;
        yy *= theValue;
        yz *= theValue;
        zx *= theValue;
        zy *= theValue;
        zz *= theValue;
    }


    public final void multiply(Matrix3f theMatrix) {
        float tmp1 = xx * theMatrix.xx + xy * theMatrix.yx + xz * theMatrix.zx;
        float tmp2 = xx * theMatrix.xy + xy * theMatrix.yy + xz * theMatrix.zy;
        float tmp3 = xx * theMatrix.xz + xy * theMatrix.yz + xz * theMatrix.zz;
        float tmp4 = yx * theMatrix.xx + yy * theMatrix.yx + yz * theMatrix.zx;
        float tmp5 = yx * theMatrix.xy + yy * theMatrix.yy + yz * theMatrix.zy;
        float tmp6 = yx * theMatrix.xz + yy * theMatrix.yz + yz * theMatrix.zz;
        float tmp7 = zx * theMatrix.xx + zy * theMatrix.yx + zz * theMatrix.zx;
        float tmp8 = zx * theMatrix.xy + zy * theMatrix.yy + zz * theMatrix.zy;
        float tmp9 = zx * theMatrix.xz + zy * theMatrix.yz + zz * theMatrix.zz;
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


    public final void transform(Vector3f theVector) {
        theVector.set(xx * theVector.x + yx * theVector.y + zx * theVector.z,
                      xy * theVector.x + yy * theVector.y + zy * theVector.z,
                      theVector.z = xz * theVector.x + yz * theVector.y + zz * theVector.z);
    }


    public void setScale(Vector3f theScale) {
        xx = theScale.x;
        yx = 0.0f;
        zx = 0.0f;

        xy = 0.0f;
        yy = theScale.y;
        zy = 0.0f;

        xz = 0.0f;
        yz = 0.0f;
        zz = theScale.z;
    }


    public final float[] toArray() {
        _myArrayRepresentation[0] = xx;
        _myArrayRepresentation[1] = yx;
        _myArrayRepresentation[2] = zx;
        _myArrayRepresentation[3] = xy;
        _myArrayRepresentation[4] = yy;
        _myArrayRepresentation[5] = zy;
        _myArrayRepresentation[6] = xz;
        _myArrayRepresentation[7] = yz;
        _myArrayRepresentation[8] = zz;
        return _myArrayRepresentation;
    }


    public final float[] toArray4f() {
        /* so that opengl can understand it */
        _myArray4fRepresentation[0] = xx;
        _myArray4fRepresentation[1] = yx;
        _myArray4fRepresentation[2] = zx;
        _myArray4fRepresentation[3] = 0;
        _myArray4fRepresentation[4] = xy;
        _myArray4fRepresentation[5] = yy;
        _myArray4fRepresentation[6] = zy;
        _myArray4fRepresentation[7] = 0;
        _myArray4fRepresentation[8] = xz;
        _myArray4fRepresentation[9] = yz;
        _myArray4fRepresentation[10] = zz;
        _myArray4fRepresentation[11] = 0;
        _myArray4fRepresentation[12] = 0;
        _myArray4fRepresentation[13] = 0;
        _myArray4fRepresentation[14] = 0;
        _myArray4fRepresentation[15] = 1;
        return _myArray4fRepresentation;
    }


    public String toString() {
        return xx + ", " + yx + ", " + zx + "\n" + xy + ", " + yy + ", " + zy + "\n" + xz + ", " + yz + ", " + zz;
    }


    public static void main(String[] args) {
        Matrix3f myMatrix;

        {
            /* invert and transpose */

            System.out.println("### invert and transpose\n");

            myMatrix = new Matrix3f(Matrix3f.IDENTITY);
            myMatrix.setXYZRotation(new Vector3f(0.2f, 0.3f, 0.4f));
            System.out.println(myMatrix);
            System.out.println();

            myMatrix.transpose();
            System.out.println(myMatrix);
            System.out.println();

            myMatrix.transpose();
            System.out.println(myMatrix);
            System.out.println();

            myMatrix = new Matrix3f(Matrix3f.IDENTITY);
            myMatrix.setXYZRotation(new Vector3f(0.2f, 0.3f, 0.4f));

            myMatrix.invert();
            System.out.println(myMatrix);
            System.out.println();

            myMatrix.invert();
            System.out.println(myMatrix);
            System.out.println();
        }

        {
            /* x */

            myMatrix = new Matrix3f(Matrix3f.IDENTITY);

            System.out.println("### rotation x\n");

            myMatrix.setXYZRotation(new Vector3f(0.2f, 0.0f, 0.0f));
            System.out.println(myMatrix);
            System.out.println();

            myMatrix.setXRotation(0.2f);
            System.out.println(myMatrix);
            System.out.println();

            myMatrix.setRotation(new Vector4f(1, 0, 0, 0.2f));
            System.out.println(myMatrix);
            System.out.println();

            /* y */

            System.out.println("### rotation y\n");

            myMatrix.setXYZRotation(new Vector3f(0.0f, 0.3f, 0.0f));
            System.out.println(myMatrix);
            System.out.println();

            myMatrix.setYRotation(0.3f);
            System.out.println(myMatrix);
            System.out.println();

            myMatrix.setRotation(new Vector4f(0, 1, 0, 0.3f));
            System.out.println(myMatrix);
            System.out.println();

            /* z */

            System.out.println("### rotation z\n");

            myMatrix.setXYZRotation(new Vector3f(0.0f, 0.0f, 0.4f));
            System.out.println(myMatrix);
            System.out.println();

            myMatrix.setZRotation(0.4f);
            System.out.println(myMatrix);
            System.out.println();

            myMatrix.setRotation(new Vector4f(0, 0, 1, 0.4f));
            System.out.println(myMatrix);
            System.out.println();
        }
    }
}
