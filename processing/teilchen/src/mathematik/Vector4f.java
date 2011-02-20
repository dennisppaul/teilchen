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


package mathematik;


import java.io.Serializable;


public class Vector4f
    implements Serializable, Vectorf {

    private static final long serialVersionUID = 5083919691492230155L;

    public float w;

    public float x;

    public float y;

    public float z;

    private static final float ALMOST_THRESHOLD = 0.001f;

    public Vector4f() {
        x = 0;
        y = 0;
        z = 0;
        w = 0;
    }


    public Vector4f(float theX,
                    float theY,
                    float theZ,
                    float theW) {
        set(theX,
            theY,
            theZ,
            theW);
    }


    public Vector4f(double theX,
                    double theY,
                    double theZ,
                    double theW) {
        set(theX,
            theY,
            theZ,
            theW);
    }


    public Vector4f(float theX,
                    float theY,
                    float theZ) {
        set(theX,
            theY,
            theZ);
    }


    public Vector4f(double theX,
                    double theY,
                    double theZ) {
        set(theX,
            theY,
            theZ);
    }


    public Vector4f(float theX,
                    float theY) {
        set(theX,
            theY);
    }


    public Vector4f(double theX,
                    double theY) {
        set(theX,
            theY);
    }


    public Vector4f(float[] theVector) {
        set(theVector);
    }


    public Vector4f(double[] theVector) {
        set(theVector);
    }


    public Vector4f(Vector4f theVector) {
        set(theVector);
    }


    public Vector4f(Vector3f theVector) {
        set(theVector);
    }


    public Vector4f(Vector2f theVector) {
        set(theVector);
    }


    public final void set(float theX,
                          float theY,
                          float theZ,
                          float theW) {
        x = theX;
        y = theY;
        z = theZ;
        w = theW;
    }


    public final void set(double theX,
                          double theY,
                          double theZ,
                          double theW) {
        x = (float) theX;
        y = (float) theY;
        z = (float) theZ;
        w = (float) theW;
    }


    public final void set(float theX,
                          float theY,
                          float theZ) {
        x = theX;
        y = theY;
        z = theZ;
    }


    public final void set(double theX,
                          double theY,
                          double theZ) {
        x = (float) theX;
        y = (float) theY;
        z = (float) theZ;
    }


    public final void set(float theX,
                          float theY) {
        x = theX;
        y = theY;
    }


    public final void set(double theX,
                          double theY) {
        x = (float) theX;
        y = (float) theY;
    }


    public final void set(float[] theVector) {
        w = theVector[0];
        x = theVector[1];
        y = theVector[2];
        z = theVector[3];
    }


    public final void set(double[] theVector) {
        w = (float) theVector[0];
        x = (float) theVector[1];
        y = (float) theVector[2];
        z = (float) theVector[3];
    }


    public final void set(Vector4f theVector) {
        x = theVector.x;
        y = theVector.y;
        z = theVector.z;
        w = theVector.w;
    }


    public final void set(Vector3f theVector) {
        x = theVector.x;
        y = theVector.y;
        z = theVector.z;
    }


    public final void set(Vector2f theVector) {
        x = theVector.x;
        y = theVector.y;
    }


    public final void add(Vector4f theVectorA,
                          Vector4f theVectorB) {
        w = theVectorA.w + theVectorB.w;
        x = theVectorA.x + theVectorB.x;
        y = theVectorA.y + theVectorB.y;
        z = theVectorA.z + theVectorB.z;
    }


    public final void add(Vector4f theVector) {
        w += theVector.w;
        x += theVector.x;
        y += theVector.y;
        z += theVector.z;
    }


    public final void sub(Vector4f theVectorA,
                          Vector4f theVectorB) {
        w = theVectorA.w - theVectorB.w;
        x = theVectorA.x - theVectorB.x;
        y = theVectorA.y - theVectorB.y;
        z = theVectorA.z - theVectorB.z;
    }


    public final void sub(Vector4f theVector) {
        w -= theVector.w;
        x -= theVector.x;
        y -= theVector.y;
        z -= theVector.z;
    }


    public final void scale(float theScalar,
                            Vector4f theVector) {
        w = theScalar * theVector.w;
        x = theScalar * theVector.x;
        y = theScalar * theVector.y;
        z = theScalar * theVector.z;
    }


    public final void scale(float theScalar) {
        w *= theScalar;
        x *= theScalar;
        y *= theScalar;
        z *= theScalar;
    }


    public final float lengthSquared() {
        return w * w + x * x + y * y + z * z;
    }


    public final float length() {
        return (float) Math.sqrt(w * w + x * x + y * y + z * z);
    }


    public final float dot(Vector4f theVector) {
        return x * theVector.x + y * theVector.y + z * theVector.z + w * theVector.w;
    }


    public void normalize(Vector4f theVector) {
        set(theVector);
        normalize();
    }


    public final void normalize() {
        final float d = 1 / length();
        x *= d;
        y *= d;
        z *= d;
        w *= d;
    }


    public final float[] toArray() {
        final float[] _myArrayRepresentation = new float[4];
        _myArrayRepresentation[0] = x;
        _myArrayRepresentation[1] = y;
        _myArrayRepresentation[2] = z;
        _myArrayRepresentation[3] = w;
        return _myArrayRepresentation;
    }


    public final boolean isNaN() {
        if (Float.isNaN(x) || Float.isNaN(y) || Float.isNaN(z) || Float.isNaN(w)) {
            return true;
        } else {
            return false;
        }
    }


    public final boolean equals(Vector4f theVector) {
        if (w == theVector.w && x == theVector.x && y == theVector.y && z == theVector.z) {
            return true;
        } else {
            return false;
        }
    }


    public final boolean almost(Vector4f theVector) {
        if (Math.abs(w) - Math.abs(theVector.w) < ALMOST_THRESHOLD
            && Math.abs(x) - Math.abs(theVector.x) < ALMOST_THRESHOLD
            && Math.abs(y) - Math.abs(theVector.y) < ALMOST_THRESHOLD
            && Math.abs(z) - Math.abs(theVector.z) < ALMOST_THRESHOLD) {
            return true;
        } else {
            return false;
        }
    }


    public final String toString() {
        return "(" + x + ", " + y + ", " + z + ", " + w + ")";
    }

}
