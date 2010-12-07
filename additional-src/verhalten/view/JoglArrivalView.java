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
 * visualize the output of the 'arrival' behavior.
 */


package verhalten.view;


import javax.media.opengl.GL;

import gestalt.context.GLContext;
import gestalt.impl.jogl.context.JoglGLContext;

import mathematik.Vector3f;

import verhalten.Arrival;
import verhalten.Engine;
import verhalten.IVerhaltenEntity;
import verhalten.Verhalten;


public class JoglArrivalView
    implements IBehaviorView {

    private Arrival _myBehavior;

    public JoglArrivalView(Arrival theBehavior) {
        _myBehavior = theBehavior;
    }


    public void draw(GLContext theRenderContext, IVerhaltenEntity theEntity) {
        /* -- */
        GL gl = ( (JoglGLContext) theRenderContext).gl;
        Vector3f myColor = getColor();

        /* draw arrival areas */
        gestalt.util.JoglUtil.circle(gl, _myBehavior.getPosition(), _myBehavior.getInnerRadius());
        gestalt.util.JoglUtil.circle(gl, _myBehavior.getPosition(), _myBehavior.getOutterRadius());

        /** @todo hack... */
        if (theEntity instanceof Engine) {
            gl.glColor4f(myColor.x, myColor.y, myColor.z, 0.75f);
            gestalt.util.JoglUtil.circle(gl, _myBehavior.getPosition(), ( (Engine) theEntity).getSpeed());
        }

        /* draw direction */
        gl.glPushMatrix();
        gl.glTranslatef(theEntity.position().x, theEntity.position().y, theEntity.position().z);

        gl.glColor3f(myColor.x, myColor.y, myColor.z);
        Vector3f myDirection = _myBehavior.direction();

        gl.glBegin(GL.GL_LINES);
        gl.glVertex3f(0, 0, 0);
        gl.glVertex3f(myDirection.x,
                      myDirection.y,
                      myDirection.z);
        gl.glEnd();

        /* -- */
        gl.glPopMatrix();
    }


    private Vector3f getColor() {
        if (_myBehavior.hasArrived()) {
            return new Vector3f(Verhalten.DEBUG_COLOR_ARRIVAL.x * 0.33f,
                                Verhalten.DEBUG_COLOR_ARRIVAL.y * 0.33f,
                                Verhalten.DEBUG_COLOR_ARRIVAL.z * 0.33f);
        }
        if (_myBehavior.isArriving()) {
            return new Vector3f(Verhalten.DEBUG_COLOR_ARRIVAL.x * 0.66f,
                                Verhalten.DEBUG_COLOR_ARRIVAL.y * 0.66f,
                                Verhalten.DEBUG_COLOR_ARRIVAL.z * 0.66f);
        }
        return Verhalten.DEBUG_COLOR_ARRIVAL;
    }


    public boolean isActive() {
        return true;
    }
}
