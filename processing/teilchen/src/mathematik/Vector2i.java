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


public class Vector2i
    implements Serializable {

    private static final long serialVersionUID = 1578087698419866994L;

    public int x;

    public int y;

    public Vector2i() {
        x = 0;
        y = 0;
    }


    public Vector2i(int theX,
                    int theY) {
        set(theX,
            theY);
    }


    public Vector2i(Vector2i theVector) {
        set(theVector);
    }


    public Vector2i(int[] theVector) {
        set(theVector);
    }


    public void set(int theX,
                    int theY) {
        x = theX;
        y = theY;
    }


    public void set(Vector2i theVector) {
        x = theVector.x;
        y = theVector.y;
    }


    public void set(int[] theVector) {
        x = theVector[0];
        y = theVector[1];
    }


    public final void sub(final Vector2i theVectorA,
                          final Vector2i theVectorB) {
        x = theVectorA.x - theVectorB.x;
        y = theVectorA.y - theVectorB.y;
    }


    public final void sub(final Vector2i theVector) {
        x -= theVector.x;
        y -= theVector.y;
    }


    public final void sub(final int theX, final int theY) {
        x -= theX;
        y -= theY;
    }


    public final void add(final Vector2i theVectorA,
                          final Vector2i theVectorB) {
        x = theVectorA.x + theVectorB.x;
        y = theVectorA.y + theVectorB.y;
    }


    public final void add(final Vector2i theVector) {
        x += theVector.x;
        y += theVector.y;
    }


    public final void add(final int theX, final int theY) {
        x += theX;
        y += theY;
    }


    public final void scale(float theScalar,
                            Vector2i theVector) {
        x = (int) (theScalar * theVector.x);
        y = (int) (theScalar * theVector.y);
    }


    public final void scale(float theScalar) {
        x *= theScalar;
        y *= theScalar;
    }


    public final void scale(Vector2i theVector) {
        x *= theVector.x;
        y *= theVector.y;
    }


    public final String toString() {
        return "(" + x + ", " + y + ")";
    }
}
