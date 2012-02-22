/*
 * Particles
 *
 * Copyright (C) 2012
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


package teilchen.gestalt.util;


import gestalt.Gestalt;
import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.material.Material;
import gestalt.shape.atom.AtomCube;
import gestalt.shape.AbstractShape;

import javax.media.opengl.GL;

import mathematik.TransformMatrix4f;
import mathematik.Vector3f;
import teilchen.cubicle.CubicleAtom;
import teilchen.cubicle.CubicleWorld;


public class JoglCubicleWorldView
    extends AbstractShape {

    private CubicleWorld _myWorld;

    public JoglCubicleWorldView(CubicleWorld theWorld) {
        _myWorld = theWorld;
        material = new Material();
        material().wireframe = true;
        material().transparent = true;
    }


    public void draw(GLContext theRenderContext) {
        /* material */
        material().begin(theRenderContext);

        /* collect data */
        final CubicleAtom[][][] myData = _myWorld.getDataRef();
        final TransformMatrix4f myTransform = _myWorld.transform();
        final Vector3f myScale = _myWorld.cellscale();

        /* draw world */
        GL gl = (  theRenderContext).gl;
        gl.glPushMatrix();
        /* rotation + translation */
        gl.glMultMatrixf(myTransform.toArray(), 0);
        /* scale */
        gl.glScalef(myScale.x, myScale.y, myScale.z);
        for (int x = 0; x < myData.length; x++) {
            for (int y = 0; y < myData[x].length; y++) {
                for (int z = 0; z < myData[x][y].length; z++) {
                    CubicleAtom myCubicle = myData[x][y][z];
                    gl.glPushMatrix();
                    gl.glTranslatef(x, y, z);
                    if (myCubicle.size() > 0) {
                        gl.glColor4f(1, 0.5f, 0, 0.5f);
                    } else {
                        gl.glColor4f(1, 1, 1, 0.125f);
                    }
                    AtomCube.draw(gl, Gestalt.SHAPE_ORIGIN_BOTTOM_LEFT);
                    gl.glPopMatrix();
                }
            }
        }
        gl.glPopMatrix();

        /* material */
        material().end(theRenderContext);
    }
}
