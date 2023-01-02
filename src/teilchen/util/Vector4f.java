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

import processing.core.PVector;

import java.io.Serializable;

public class Vector4f implements Serializable {

    private static final float ALMOST_THRESHOLD = 0.001f;
    private static final long serialVersionUID = 5083919691492230155L;
    public float w;
    public float x;
    public float y;
    public float z;

    public Vector4f() {
        x = 0;
        y = 0;
        z = 0;
        w = 0;
    }

    public Vector4f(float pX, float pY, float pZ, float pW) {
        set(pX, pY, pZ, pW);
    }

    public Vector4f(double pX, double pY, double pZ, double pW) {
        set(pX, pY, pZ, pW);
    }

    public Vector4f(float pX, float pY, float pZ) {
        set(pX, pY, pZ);
    }

    public Vector4f(double pX, double pY, double pZ) {
        set(pX, pY, pZ);
    }

    public Vector4f(float pX, float pY) {
        set(pX, pY);
    }

    public Vector4f(double pX, double pY) {
        set(pX, pY);
    }

    public Vector4f(float[] pVector) {
        set(pVector);
    }

    public Vector4f(double[] pVector) {
        set(pVector);
    }

    public Vector4f(Vector4f pVector) {
        set(pVector);
    }

    public Vector4f(PVector pVector) {
        set(pVector);
    }

    public final void set(float pX, float pY, float pZ, float pW) {
        x = pX;
        y = pY;
        z = pZ;
        w = pW;
    }

    public final void set(double pX, double pY, double pZ, double pW) {
        x = (float) pX;
        y = (float) pY;
        z = (float) pZ;
        w = (float) pW;
    }

    public final void set(float pX, float pY, float pZ) {
        x = pX;
        y = pY;
        z = pZ;
    }

    public final void set(double pX, double pY, double pZ) {
        x = (float) pX;
        y = (float) pY;
        z = (float) pZ;
    }

    public final void set(float pX, float pY) {
        x = pX;
        y = pY;
    }

    public final void set(double pX, double pY) {
        x = (float) pX;
        y = (float) pY;
    }

    public final void set(float[] pVector) {
        w = pVector[0];
        x = pVector[1];
        y = pVector[2];
        z = pVector[3];
    }

    public final void set(double[] pVector) {
        w = (float) pVector[0];
        x = (float) pVector[1];
        y = (float) pVector[2];
        z = (float) pVector[3];
    }

    public final void set(Vector4f pVector) {
        x = pVector.x;
        y = pVector.y;
        z = pVector.z;
        w = pVector.w;
    }

    public final void set(PVector pVector) {
        x = pVector.x;
        y = pVector.y;
        z = pVector.z;
    }

    public final void add(Vector4f pVectorA, Vector4f pVectorB) {
        w = pVectorA.w + pVectorB.w;
        x = pVectorA.x + pVectorB.x;
        y = pVectorA.y + pVectorB.y;
        z = pVectorA.z + pVectorB.z;
    }

    public final void add(Vector4f pVector) {
        w += pVector.w;
        x += pVector.x;
        y += pVector.y;
        z += pVector.z;
    }

    public final void sub(Vector4f pVectorA, Vector4f pVectorB) {
        w = pVectorA.w - pVectorB.w;
        x = pVectorA.x - pVectorB.x;
        y = pVectorA.y - pVectorB.y;
        z = pVectorA.z - pVectorB.z;
    }

    public final void sub(Vector4f pVector) {
        w -= pVector.w;
        x -= pVector.x;
        y -= pVector.y;
        z -= pVector.z;
    }

    public final void scale(float pScalar, Vector4f pVector) {
        w = pScalar * pVector.w;
        x = pScalar * pVector.x;
        y = pScalar * pVector.y;
        z = pScalar * pVector.z;
    }

    public final void scale(float pScalar) {
        w *= pScalar;
        x *= pScalar;
        y *= pScalar;
        z *= pScalar;
    }

    public final float lengthSquared() {
        return w * w + x * x + y * y + z * z;
    }

    public final float length() {
        return (float) Math.sqrt(w * w + x * x + y * y + z * z);
    }

    public final float dot(Vector4f pVector) {
        return x * pVector.x + y * pVector.y + z * pVector.z + w * pVector.w;
    }

    public void normalize(Vector4f pVector) {
        set(pVector);
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
        final float[] mArrayRepresentation = new float[4];
        mArrayRepresentation[0] = x;
        mArrayRepresentation[1] = y;
        mArrayRepresentation[2] = z;
        mArrayRepresentation[3] = w;
        return mArrayRepresentation;
    }

    public final boolean isNaN() {
        return Float.isNaN(x) || Float.isNaN(y) || Float.isNaN(z) || Float.isNaN(w);
    }

    public final boolean equals(Vector4f pVector) {
        return w == pVector.w && x == pVector.x && y == pVector.y && z == pVector.z;
    }

    public final boolean almost(Vector4f pVector) {
        return Math.abs(w) - Math.abs(pVector.w) < ALMOST_THRESHOLD && Math.abs(x) - Math.abs(pVector.x) < ALMOST_THRESHOLD && Math.abs(
                y) - Math.abs(pVector.y) < ALMOST_THRESHOLD && Math.abs(z) - Math.abs(pVector.z) < ALMOST_THRESHOLD;
    }

    public final String toString() {
        return "(" + x + ", " + y + ", " + z + ", " + w + ")";
    }
}
