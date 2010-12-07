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
 * visualize the output of the 'flee' behavior.
 */

package verhalten.view;


import javax.media.opengl.GL;

import gestalt.context.GLContext;
import gestalt.impl.jogl.context.JoglGLContext;

import mathematik.Vector3f;

import verhalten.IVerhaltenEntity;
import verhalten.Verhalten;
import verhalten.Wander;


public class JoglWanderView
    implements IBehaviorView {

    private Wander _myBehavior;

    public JoglWanderView(Wander theBehavior) {
        _myBehavior = theBehavior;
    }


    public void draw(GLContext theRenderContext, IVerhaltenEntity theEntity) {
        /* -- */
        GL gl = ( (JoglGLContext) theRenderContext).gl;
        gl.glPushMatrix();

        /* draw direction */
        gl.glTranslatef(theEntity.position().x, theEntity.position().y, theEntity.position().z);

        Vector3f myColor = Verhalten.DEBUG_COLOR_WANDER;
        gl.glColor3f(myColor.x, myColor.y, myColor.z);
        Vector3f myDirection = _myBehavior.direction();

        gl.glBegin(GL.GL_LINES);
        gl.glVertex3f(0, 0, 0);
        gl.glVertex3f(myDirection.x,
                      myDirection.y,
                      myDirection.z);
        gl.glEnd();

        gl.glColor4f(Verhalten.DEBUG_COLOR_WANDER.x,
                     Verhalten.DEBUG_COLOR_WANDER.y,
                     Verhalten.DEBUG_COLOR_WANDER.z,
                     0.5f);
        gl.glTranslatef(_myBehavior.getForward().x,
                        _myBehavior.getForward().y,
                        _myBehavior.getForward().z);

        gl.glBegin(GL.GL_LINES);
        gl.glVertex3f(0, 0, 0);
        gl.glVertex3f(_myBehavior.getTarget().x,
                      _myBehavior.getTarget().y,
                      _myBehavior.getTarget().z);
        gl.glEnd();

        gestalt.util.JoglUtil.circle(gl, _myBehavior.getRadius());

        /* -- */
        gl.glPopMatrix();
    }
}
