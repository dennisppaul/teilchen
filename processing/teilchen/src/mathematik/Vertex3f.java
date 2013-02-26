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


import gestalt.shape.Color;


public class Vertex3f {
    public Vector3f normal;

    public Vector3f texcoord;

    public Vector3f position;

    public Color color;

    public Vertex3f() {
        this(false);
    }


    public Vertex3f(boolean theInitialize) {
        if (theInitialize) {
            normal = new Vector3f(0, 0, 1);
            texcoord = new Vector3f(0, 0, 0);
            position = new Vector3f(0, 0, 0);
            color = new Color(1, 1, 1, 1);
        }
    }
}
