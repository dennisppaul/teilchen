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
 * a more general 4x4 matrix.
 *
 *   xx yx zx wx
 *   xy yy zy wy
 *   xz yz zz wz
 *   xw yw zw ww
 *
 * also read 'The Matrix and Quaternions FAQ' at http://www.flipcode.com/documents/matrfaq.html
 */


package mathematik;


import java.io.Serializable;


public class Matrix4f
    implements Serializable {

    private static final long serialVersionUID = -1088603850006298206L;

    public float xx;

    public float xy;

    public float xz;

    public float xw;

    public float yx;

    public float yy;

    public float yz;

    public float yw;

    public float zx;

    public float zy;

    public float zz;

    public float zw;

    public float wx;

    public float wy;

    public float wz;

    public float ww;

    private final float[] _myArrayRepresentation = new float[16];

    public Matrix4f() {
    }


    public Matrix4f(TransformMatrix4f theTransformMatrix) {
        xx = theTransformMatrix.rotation.xx;
        yx = theTransformMatrix.rotation.yx;
        zx = theTransformMatrix.rotation.zx;
        wx = 0;

        xy = theTransformMatrix.rotation.xy;
        yy = theTransformMatrix.rotation.yy;
        zy = theTransformMatrix.rotation.zy;
        wy = 0;

        xz = theTransformMatrix.rotation.xz;
        yz = theTransformMatrix.rotation.yz;
        zz = theTransformMatrix.rotation.zz;
        wz = 0;

        xw = theTransformMatrix.translation.x;
        yw = theTransformMatrix.translation.y;
        zw = theTransformMatrix.translation.z;
        ww = 1;
    }


    public Matrix4f(Matrix4f theTransformMatrix) {
        xx = theTransformMatrix.xx;
        yx = theTransformMatrix.yx;
        zx = theTransformMatrix.zx;
        wx = theTransformMatrix.wx;

        xy = theTransformMatrix.xy;
        yy = theTransformMatrix.yy;
        zy = theTransformMatrix.zy;
        wy = theTransformMatrix.wy;

        xz = theTransformMatrix.xz;
        yz = theTransformMatrix.yz;
        zz = theTransformMatrix.zz;
        wz = theTransformMatrix.wz;

        xw = theTransformMatrix.xw;
        yw = theTransformMatrix.yw;
        zw = theTransformMatrix.zw;
        ww = theTransformMatrix.ww;
    }


    public Matrix4f(float[] theMatrix) {
        set(theMatrix);
    }


    public Matrix4f(double[] theMatrix) {
        set(theMatrix);
    }


    public Matrix4f(float xx,
                    float yx,
                    float zx,
                    float wx,
                    float xy,
                    float yy,
                    float zy,
                    float wy,
                    float xz,
                    float yz,
                    float zz,
                    float wz,
                    float xw,
                    float yw,
                    float zw,
                    float ww) {
        this.xx = xx;
        this.xy = xy;
        this.xz = xz;
        this.xw = xw;

        this.yx = yx;
        this.yy = yy;
        this.yz = yz;
        this.yw = yw;

        this.zx = zx;
        this.zy = zy;
        this.zz = zz;
        this.zw = zw;

        this.wx = wx;
        this.wy = wy;
        this.wz = wz;
        this.ww = ww;
    }


    public void set(float[] theMatrix) {
        xx = theMatrix[0];
        yx = theMatrix[1];
        zx = theMatrix[2];
        wx = theMatrix[3];

        xy = theMatrix[4];
        yy = theMatrix[5];
        zy = theMatrix[6];
        wy = theMatrix[7];

        xz = theMatrix[8];
        yz = theMatrix[9];
        zz = theMatrix[10];
        wz = theMatrix[11];

        xw = theMatrix[12];
        yw = theMatrix[13];
        zw = theMatrix[14];
        ww = theMatrix[15];
    }


    public void set(double[] theMatrix) {
        xx = (float) theMatrix[0];
        yx = (float) theMatrix[1];
        zx = (float) theMatrix[2];
        wx = (float) theMatrix[3];

        xy = (float) theMatrix[4];
        yy = (float) theMatrix[5];
        zy = (float) theMatrix[6];
        wy = (float) theMatrix[7];

        xz = (float) theMatrix[8];
        yz = (float) theMatrix[9];
        zz = (float) theMatrix[10];
        wz = (float) theMatrix[11];

        xw = (float) theMatrix[12];
        yw = (float) theMatrix[13];
        zw = (float) theMatrix[14];
        ww = (float) theMatrix[15];
    }


    public void set(float xx,
                    float yx,
                    float zx,
                    float wx,
                    float xy,
                    float yy,
                    float zy,
                    float wy,
                    float xz,
                    float yz,
                    float zz,
                    float wz,
                    float xw,
                    float yw,
                    float zw,
                    float ww) {
        this.xx = xx;
        this.xy = xy;
        this.xz = xz;
        this.xw = xw;

        this.yx = yx;
        this.yy = yy;
        this.yz = yz;
        this.yw = yw;

        this.zx = zx;
        this.zy = zy;
        this.zz = zz;
        this.zw = zw;

        this.wx = wx;
        this.wy = wy;
        this.wz = wz;
        this.ww = ww;
    }


    public void invert() {
        float myDeterminant = determinant();
        if (myDeterminant == 0.0) {
            return;
        }
        myDeterminant = 1 / myDeterminant;
        set(yy * (zz * ww - zw * wz) + yz * (zw * wy - zy * ww) + yw * (zy * wz - zz * wy),
            zy * (xz * ww - xw * wz) + zz * (xw * wy - xy * ww) + zw * (xy * wz - xz * wy),
            wy * (xz * yw - xw * yz) + wz * (xw * yy - xy * yw) + ww * (xy * yz - xz * yy),
            xy * (yw * zz - yz * zw) + xz * (yy * zw - yw * zy) + xw * (yz * zy - yy * zz),

            yz * (zx * ww - zw * wx) + yw * (zz * wx - zx * wz) + yx * (zw * wz - zz * ww),
            zz * (xx * ww - xw * wx) + zw * (xz * wx - xx * wz) + zx * (xw * wz - xz * ww),
            wz * (xx * yw - xw * yx) + ww * (xz * yx - xx * yz) + wx * (xw * yz - xz * yw),
            xz * (yw * zx - yx * zw) + xw * (yx * zz - yz * zx) + xx * (yz * zw - yw * zz),

            yw * (zx * wy - zy * wx) + yx * (zy * ww - zw * wy) + yy * (zw * wx - zx * ww),
            zw * (xx * wy - xy * wx) + zx * (xy * ww - xw * wy) + zy * (xw * wx - xx * ww),
            ww * (xx * yy - xy * yx) + wx * (xy * yw - xw * yy) + wy * (xw * yx - xx * yw),
            xw * (yy * zx - yx * zy) + xx * (yw * zy - yy * zw) + xy * (yx * zw - yw * zx),

            yx * (zz * wy - zy * wz) + yy * (zx * wz - zz * wx) + yz * (zy * wx - zx * wy),
            zx * (xz * wy - xy * wz) + zy * (xx * wz - xz * wx) + zz * (xy * wx - xx * wy),
            wx * (xz * yy - xy * yz) + wy * (xx * yz - xz * yx) + wz * (xy * yx - xx * yy),
            xx * (yy * zz - yz * zy) + xy * (yz * zx - yx * zz) + xz * (yx * zy - yy * zx));

        multiply(myDeterminant);
    }


    public void multiply(float scalar) {
        xx *= scalar;
        xy *= scalar;
        xz *= scalar;
        xw *= scalar;
        yx *= scalar;
        yy *= scalar;
        yz *= scalar;
        yw *= scalar;
        zx *= scalar;
        zy *= scalar;
        zz *= scalar;
        zw *= scalar;
        wx *= scalar;
        wy *= scalar;
        wz *= scalar;
        ww *= scalar;
    }


    public final void multiply(Matrix4f theMatrix) {
        multiply(this,
                 theMatrix);
    }


    public final void multiply(Matrix4f theA,
                               Matrix4f theB) {
        /** @todo here we still have an ugly bug :( */
        set(theA.xx * theB.xx + theA.yx * theB.xy + theA.zx * theB.xz + theA.wx * theB.xw,
            theA.xx * theB.yx + theA.yx * theB.yy + theA.zx * theB.yz + theA.wx * theB.yw,
            theA.xx * theB.zx + theA.yx * theB.zy + theA.zx * theB.zz + theA.wx * theB.zw,
            theA.xx * theB.wx + theA.yx * theB.wy + theA.zx * theB.wz + theA.wx * theB.ww,

            theA.xy * theB.xx + theA.yy * theB.xy + theA.zy * theB.xz + theA.wy * theB.xw,
            theA.xy * theB.yx + theA.yy * theB.yy + theA.zy * theB.yz + theA.wy * theB.yw,
            theA.xy * theB.zx + theA.yy * theB.zy + theA.zy * theB.zz + theA.wy * theB.zw,
            theA.xy * theB.wx + theA.yy * theB.wy + theA.zy * theB.wz + theA.wy * theB.ww,

            theA.xz * theB.xx + theA.yz * theB.xy + theA.zz * theB.xz + theA.wz * theB.xw,
            theA.xz * theB.yx + theA.yz * theB.yy + theA.zz * theB.yz + theA.wz * theB.yw,
            theA.xz * theB.zx + theA.yz * theB.zy + theA.zz * theB.zz + theA.wz * theB.zw,
            theA.xz * theB.wx + theA.yz * theB.wy + theA.zz * theB.wz + theA.wz * theB.ww,

            theA.xw * theB.xx + theA.yw * theB.xy + theA.zw * theB.xz + theA.ww * theB.xw,
            theA.xw * theB.yx + theA.yw * theB.yy + theA.zw * theB.yz + theA.ww * theB.yw,
            theA.xw * theB.zx + theA.yw * theB.zy + theA.zw * theB.zz + theA.ww * theB.zw,
            theA.xw * theB.wx + theA.yw * theB.wy + theA.zw * theB.wz + theA.ww * theB.ww);
    }


    public float determinant() {
        /** @todo check if this is correct */
        return (xx * yy - xy * yx) * (zz * ww - zw * wz) - (xx * yz - xz * yx) * (zy * ww - zw * wy)
            + (xx * yw - xw * yx) * (zy * wz - zz * wy) + (xy * yz - xz * yy) * (zx * ww - zw * wx)
            - (xy * yw - xw * yy) * (zx * wz - zz * wx) + (xz * yw - xw * yz) * (zx * wy - zy * wx);
    }


    public final void transform(Vector3f theResult) {
        theResult.set(xx * theResult.x + xy * theResult.y + xz * theResult.z + xw,
                      yx * theResult.x + yy * theResult.y + yz * theResult.z + yw,
                      zx * theResult.x + zy * theResult.y + zz * theResult.z + zw);
    }


    public final void transform(Vector4f theVector) {
        theVector.set(xx * theVector.x + xy * theVector.y + xz * theVector.z + xw * theVector.w,
                      yx * theVector.x + yy * theVector.y + yz * theVector.z + yw * theVector.w,
                      zx * theVector.x + zy * theVector.y + zz * theVector.z + zw * theVector.w,
                      wx * theVector.x + wy * theVector.y + wz * theVector.z + ww * theVector.w);
    }


    public float[] toArray() {
        /* opengl format */
        _myArrayRepresentation[0] = xx;
        _myArrayRepresentation[1] = yx;
        _myArrayRepresentation[2] = zx;
        _myArrayRepresentation[3] = wx;

        _myArrayRepresentation[4] = xy;
        _myArrayRepresentation[5] = yy;
        _myArrayRepresentation[6] = zy;
        _myArrayRepresentation[7] = wy;

        _myArrayRepresentation[8] = xz;
        _myArrayRepresentation[9] = yz;
        _myArrayRepresentation[10] = zz;
        _myArrayRepresentation[11] = wz;

        _myArrayRepresentation[12] = wx;
        _myArrayRepresentation[13] = wy;
        _myArrayRepresentation[14] = wz;
        _myArrayRepresentation[15] = ww;
        return _myArrayRepresentation;
    }


    public final void transpose() {
        float swap = yx;
        yx = xy;
        xy = swap;
        swap = zx;
        zx = xz;
        xz = swap;
        swap = wx;
        wx = xw;
        xw = swap;
        swap = zy;
        zy = yz;
        yz = swap;
        swap = wy;
        wy = yw;
        yw = swap;
        swap = wz;
        wz = zw;
        zw = swap;
    }


    public String toString() {
        return xx + ", " + yx + ", " + zx + ", " + wx + "\n" + xy + ", " + yy + ", " + zy + ", " + wy + "\n" + xz
            + ", " + yz + ", " + zz + ", " + wz + "\n" + xw + ", " + yw + ", " + zw + ", " + ww;
    }


    public static void main(String[] args) {
        TransformMatrix4f myTransMatrix = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
        myTransMatrix.rotation.setXYZRotation(new Vector3f(0.2f, 0.3f, 0.4f));
        myTransMatrix.translation.set(2,
                                      3,
                                      4);

        System.out.println("### transform matrix\n");

        System.out.println(myTransMatrix);
        System.out.println();

        Matrix4f myMatrix = new Matrix4f(myTransMatrix);
        System.out.println(myMatrix);
        System.out.println();

        myMatrix.invert();
        System.out.println(myMatrix);
        System.out.println();

        myMatrix.invert();
        System.out.println(myMatrix);
        System.out.println();

        /* transform */
        Vector3f myVector3f = new Vector3f(5, 8, 7);
        Vector4f myVector4f = new Vector4f(5, 8, 7, 1);
        Vector3f myVectorOther3f = new Vector3f(5, 8, 7);

        System.out.println();

        myMatrix.transform(myVector3f);
        System.out.println(myVector3f);

        myMatrix.transform(myVector4f);
        System.out.println(myVector4f);

        myTransMatrix.transform(myVectorOther3f);
        System.out.println(myVectorOther3f);
    }
}
