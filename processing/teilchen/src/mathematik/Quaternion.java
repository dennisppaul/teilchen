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


public class Quaternion {

    public float w;

    public float x;

    public float y;

    public float z;

    public Quaternion() {
        reset();
    }


    public Quaternion(float theW, float theX, float theY, float theZ) {
        w = theW;
        x = theX;
        y = theY;
        z = theZ;
    }


    public void reset() {
        w = 1.0f;
        x = 0.0f;
        y = 0.0f;
        z = 0.0f;
    }


    public void set(float theW, Vector3f theVector3f) {
        w = theW;
        x = theVector3f.x;
        y = theVector3f.y;
        z = theVector3f.z;
    }


    public void set(Quaternion theQuaternion) {
        w = theQuaternion.w;
        x = theQuaternion.x;
        y = theQuaternion.y;
        z = theQuaternion.z;
    }


    public void multiply(Quaternion theA, Quaternion theB) {
        w = theA.w * theB.w - theA.x * theB.x - theA.y * theB.y - theA.z * theB.z;
        x = theA.w * theB.x + theA.x * theB.w + theA.y * theB.z - theA.z * theB.y;
        y = theA.w * theB.y + theA.y * theB.w + theA.z * theB.x - theA.x * theB.z;
        z = theA.w * theB.z + theA.z * theB.w + theA.x * theB.y - theA.y * theB.x;
    }


    public Vector4f getVectorAndAngle() {
        final Vector4f theResult = new Vector4f();

        float s = (float) Math.sqrt(1.0f - w * w);
        if (s < Mathematik.EPSILON) {
            s = 1.0f;
        }

        theResult.w = (float) Math.acos(w) * 2.0f;
        theResult.x = x / s;
        theResult.y = y / s;
        theResult.z = z / s;

        return theResult;
    }
}
