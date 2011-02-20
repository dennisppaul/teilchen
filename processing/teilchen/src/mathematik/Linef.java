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


public class Linef<T extends Vectorf>
    implements Serializable {

    private static final long serialVersionUID = -1748179277316146123L;

    public T p1;

    public T p2;

    public Linef(Class<T> theClass) {
        try {
            p1 = theClass.newInstance();
            p2 = theClass.newInstance();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        }
    }

    /*
     * Linef<Vector2f> myLine = new Linef<Vector2f> (Vector2f.class);
     * Linef<Vector3f> myLine3f = new Linef<Vector3f> (Vector3f.class);
     */
}
