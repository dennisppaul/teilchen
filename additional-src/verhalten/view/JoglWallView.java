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


/*
 * a rough jogl view of a wall.
 */


package verhalten.view;


import javax.media.opengl.GL;

import gestalt.context.GLContext;
import gestalt.impl.jogl.context.JoglGLContext;
import gestalt.impl.jogl.shape.JoglMaterial;
import gestalt.shape.AbstractShape;

import mathematik.Vector3f;

import verhalten.IVerhaltenWall;


public class JoglWallView
    extends AbstractShape {

    private IVerhaltenWall _myWall;

    public JoglWallView(IVerhaltenWall theWall) {
        _myWall = theWall;
        material = new JoglMaterial();
    }


    public void draw(GLContext theRenderContext) {
        /* material */
        material().begin(theRenderContext);

        /* draw entity */
        GL gl = ( (JoglGLContext) theRenderContext).gl;

        /* draw wall */
        gl.glColor3f(1, 1, 1);
        gl.glBegin(GL.GL_LINE_LOOP);
        gl.glVertex3f(_myWall.pointA().x, _myWall.pointA().y, _myWall.pointA().z);
        gl.glVertex3f(_myWall.pointB().x, _myWall.pointB().y, _myWall.pointB().z);
        gl.glVertex3f(_myWall.pointC().x, _myWall.pointC().y, _myWall.pointC().z);
        gl.glEnd();

        /* draw normal */
        Vector3f myCenterOfMass = new Vector3f();
        myCenterOfMass.add(_myWall.pointA());
        myCenterOfMass.add(_myWall.pointB());
        myCenterOfMass.add(_myWall.pointC());
        myCenterOfMass.scale(0.33f);
        gl.glColor3f(1, 1, 0);
        float myNormalScaler = 30;
        gl.glBegin(GL.GL_LINES);
        gl.glVertex3f(myCenterOfMass.x,
                      myCenterOfMass.y,
                      myCenterOfMass.z);
        gl.glVertex3f(myCenterOfMass.x + _myWall.normal().x * myNormalScaler,
                      myCenterOfMass.y + _myWall.normal().y * myNormalScaler,
                      myCenterOfMass.z + _myWall.normal().z * myNormalScaler);
        gl.glEnd();

        /* material */
        material().end(theRenderContext);
    }
}
