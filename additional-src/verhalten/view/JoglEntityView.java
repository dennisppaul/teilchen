/*
 * Verhalten
 *
 * Copyright (C) 2005 Patrick Kochlik + Dennis Paul
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


package verhalten.view;


import gestalt.Gestalt;
import gestalt.context.GLContext;
import gestalt.impl.jogl.context.JoglGLContext;
import gestalt.impl.jogl.shape.JoglMaterial;
import gestalt.impl.jogl.shape.atom.JoglAtomPlane;
import gestalt.shape.AbstractShape;

import javax.media.opengl.GL;

import verhalten.IVerhaltenEntity;


public class JoglEntityView
    extends AbstractShape {

    private IVerhaltenEntity _myEntity;

    public JoglEntityView(IVerhaltenEntity theEntity) {
        _myEntity = theEntity;
        scale().set(10, 10, 10);
        material = new JoglMaterial();
        material().wireframe = true;
    }


    public void draw(GLContext theRenderContext) {
        /* material */
        material().begin(theRenderContext);

        /* draw entity */
        GL gl = ( (JoglGLContext) theRenderContext).gl;

        /* --- */
        gl.glPushMatrix();

        /* draw object */
//        if (_myEntity.isTagged()) {
//            gl.glColor3f(1, 0, 0);
//        }
        gl.glTranslatef(_myEntity.position().x, _myEntity.position().y, _myEntity.position().z);
        gl.glPushMatrix();
        gl.glScalef(scale().x, scale().y, scale().z);
        JoglAtomPlane.draw(gl, Gestalt.SHAPE_ORIGIN_CENTERED);
        gl.glPopMatrix();

        /** @todo replace this with a sphere */
        gestalt.util.JoglUtil.circle(gl, _myEntity.getBoundingRadius());

        /* --- */
        gl.glPopMatrix();

        /* material */
        material().end(theRenderContext);
    }
}
