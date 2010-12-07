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
 * visualize the output of the 'obstacle avoidance' behavior.
 */

package verhalten.view;


import java.util.Vector;
import javax.media.opengl.GL;

import gestalt.Gestalt;
import gestalt.context.GLContext;
import gestalt.impl.jogl.context.JoglGLContext;
import gestalt.impl.jogl.shape.atom.JoglAtomPlane;

import mathematik.Vector3f;

import verhalten.IVerhaltenEntity;
import verhalten.ObstacleAvoidance;
import verhalten.Verhalten;


public class JoglObstacleAvoidanceView
    implements IBehaviorView {

    private ObstacleAvoidance _myBehavior;

    public JoglObstacleAvoidanceView(ObstacleAvoidance theBehavior) {
        _myBehavior = theBehavior;
    }


    public void draw(GLContext theRenderContext, IVerhaltenEntity theEntity) {
        /* -- */
        GL gl = ( (JoglGLContext) theRenderContext).gl;
        gl.glPushMatrix();

        /* draw direction */
        gl.glTranslatef(theEntity.position().x, theEntity.position().y, theEntity.position().z);

        Vector3f myColor = Verhalten.DEBUG_COLOR_OBSTACLEAVOIDANCE;
        gl.glColor3f(myColor.x, myColor.y, myColor.z);
        Vector3f myDirection = _myBehavior.direction();

        if (myDirection != null) {
            gl.glBegin(GL.GL_LINES);
            gl.glVertex3f(0, 0, 0);
            gl.glVertex3f(myDirection.x,
                          myDirection.y,
                          myDirection.z);
            gl.glEnd();
        }

        /* -- */
        gl.glPopMatrix();

        /* draw feelers */
        {
            gl.glPushMatrix();
            gl.glMultMatrixf(theEntity.transform().toArray(), 0);

            float myBoxLength = _myBehavior.getBoxLength();
            float myBoxWidth = theEntity.getBoundingRadius();

            gl.glColor3f(1, 1, 0);
            gl.glBegin(GL.GL_LINE_LOOP);
            gl.glVertex3f(0, -myBoxWidth / 2, 0);
            gl.glVertex3f(0, myBoxWidth / 2, 0);
            gl.glVertex3f(myBoxLength, myBoxWidth / 2, 0);
            gl.glVertex3f(myBoxLength, -myBoxWidth / 2, 0);
            gl.glEnd();

            gl.glPopMatrix();
        }

        /* draw obstacles */
        {
            gl.glPushMatrix();
            gl.glMultMatrixf(theEntity.transform().toArray(), 0);

            gl.glColor3f(1, 1, 0);
            gl.glPushMatrix();
            gl.glScalef(20, 20, 20);
            JoglAtomPlane.draw(gl, Gestalt.SHAPE_ORIGIN_CENTERED);
            gl.glPopMatrix();

            Vector<Vector3f> myObstacles = _myBehavior.getLocalObstaclePositions();
            if (myObstacles != null) {
                gl.glBegin(GL.GL_LINES);
                gl.glColor3f(1, 1, 0);
                for (int i = 0; i < myObstacles.size(); i++) {
                    gl.glVertex3f(0, 0, 0);
                    gl.glVertex3f(myObstacles.get(i).x,
                                  myObstacles.get(i).y,
                                  myObstacles.get(i).z);
                }
                gl.glEnd();
            }
            gl.glPopMatrix();
        }
    }
}
