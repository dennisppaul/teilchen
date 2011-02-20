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


public class Vector2f
    implements Serializable, Vectorf {

    private static final long serialVersionUID = -4652533170291767614L;

    public float x;

    public float y;

//    private float[] _myArrayRepresentation = new float[2];

    private static final float ALMOST_THRESHOLD = 0.001f;

    public Vector2f() {
        x = 0.0f;
        y = 0.0f;
    }


    public Vector2f(float theX,
                    float theY) {
        set(theX,
            theY);
    }


    public Vector2f(double theX,
                    double theY) {
        set(theX,
            theY);
    }


    public Vector2f(float[] theVector) {
        set(theVector);
    }


    public Vector2f(double[] theVector) {
        set(theVector);
    }


    public Vector2f(int[] theVector) {
        set(theVector);
    }


    public Vector2f(Vector2f theVector) {
        set(theVector);
    }


    public Vector2f(Vector3f theVector) {
        set(theVector.x, theVector.y);
    }


    public Object clone() {
        return cloneVector();
    }


    public Vector2f cloneVector() {
        return new Vector2f(x, y);
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
        x = theVector[0];
        y = theVector[1];
    }


    public final void set(double[] theVector) {
        x = (float) theVector[0];
        y = (float) theVector[1];
    }


    public final void set(int[] theVector) {
        x = theVector[0];
        y = theVector[1];
    }


    public final void set(Vector2f theVector) {
        x = theVector.x;
        y = theVector.y;
    }


    public final void add(Vector2f theVectorA,
                          Vector2f theVectorB) {
        x = theVectorA.x + theVectorB.x;
        y = theVectorA.y + theVectorB.y;
    }


    public void add(final int theX, final int theY) {
        x += theX;
        y += theY;
    }


    public void add(final float theX, final float theY) {
        x += theX;
        y += theY;
    }


    public void add(final double theX, final double theY) {
        x += theX;
        y += theY;
    }


    public final void add(Vector2f theVector) {
        x += theVector.x;
        y += theVector.y;
    }


    public final void sub(Vector2f theVectorA,
                          Vector2f theVectorB) {
        x = theVectorA.x - theVectorB.x;
        y = theVectorA.y - theVectorB.y;
    }


    public final void sub(Vector2f theVector) {
        x -= theVector.x;
        y -= theVector.y;
    }


    public final void sub(float theX, float theY) {
        x -= theX;
        y -= theY;
    }


    public final void scale(float theScalar,
                            Vector2f theVector) {
        x = theScalar * theVector.x;
        y = theScalar * theVector.y;
    }


    public final void scale(float theScalar) {
        x *= theScalar;
        y *= theScalar;
    }


    public final void scale(Vector2f theVector) {
        x *= theVector.x;
        y *= theVector.y;
    }


    public final void scale(float theXScalar, float theYScalar) {
        x *= theXScalar;
        y *= theYScalar;
    }


    public float direction() {
        return (float) Math.atan2(y, x);
    }


    public final float dot(Vector2f theVector) {
        return x * theVector.x + y * theVector.y;
    }


    public final void cross(Vector2f theVector) {
        x = theVector.y;
        y = -theVector.x;
    }


    public final void cross() {
        final float myX = y;
        y = -x;
        x = myX;
    }


    public final float length() {
        return (float) Math.sqrt(x * x + y * y);
    }


    public final float lengthSquared() {
        return x * x + y * y;
    }


    public final void normalize(Vector2f theVector) {
        float inverseMag = 1.0f / (float) Math.sqrt(theVector.x * theVector.x + theVector.y * theVector.y);
        x = theVector.x * inverseMag;
        y = theVector.y * inverseMag;
    }


    public final void normalize() {
        float inverseMag = 1.0f / (float) Math.sqrt(x * x + y * y);
        x *= inverseMag;
        y *= inverseMag;
    }


    public final float angle(Vector2f theVector) {
        float d = dot(theVector) / (length() * theVector.length());
        if (d < -1.0f) {
            d = -1.0f;
        }
        if (d > 1.0f) {
            d = 1.0f;
        }
        return (float) Math.acos(d);
    }


    public final void rotate(float theRadians) {
        final float myX = (float) (x * Math.cos(theRadians) - y * Math.sin(theRadians));
        final float myY = (float) (y * Math.cos(theRadians) + x * Math.sin(theRadians));
        x = myX;
        y = myY;
    }


    public final float distanceSquared(Vector2f thePoint) {
        float dx = x - thePoint.x;
        float dy = y - thePoint.y;
        return dx * dx + dy * dy;
    }


    public final float distance(Vector2f thePoint) {
        float dx = x - thePoint.x;
        float dy = y - thePoint.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }


    public final float[] toArray() {
        final float[] _myArrayRepresentation = new float[2];
        _myArrayRepresentation[0] = x;
        _myArrayRepresentation[1] = y;
        return _myArrayRepresentation;
    }


    public final boolean isNaN() {
        if (Float.isNaN(x) || Float.isNaN(y)) {
            return true;
        } else {
            return false;
        }
    }


    public final boolean equals(Vector2f theVector) {
        if (x == theVector.x && y == theVector.y) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Returns true if both x and y are zero
     * @return boolean, true if the vector is zero
     */
    public boolean equalsZero() {
        return x == 0 && y == 0;
    }


    public final boolean almost(Vector2f theVector) {
        if (Math.abs(x - theVector.x) < ALMOST_THRESHOLD
            && Math.abs(y - theVector.y) < ALMOST_THRESHOLD) {
            return true;
        } else {
            return false;
        }
    }


    public final String toString() {
        return "(" + x + ", " + y + ")";
    }
}
