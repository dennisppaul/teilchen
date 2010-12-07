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
 * a rough jogl view of an engine
 */


package verhalten.view;


import gestalt.Gestalt;
import gestalt.context.GLContext;
import gestalt.impl.jogl.context.JoglGLContext;
import gestalt.impl.jogl.shape.JoglMaterial;
import gestalt.impl.jogl.shape.atom.JoglAtomPlane;
import gestalt.shape.AbstractShape;

import java.util.Vector;

import javax.media.opengl.GL;

import verhalten.Engine;


public class JoglEngineView
    extends AbstractShape {

    private Engine _myEngine;

    private Vector<IBehaviorView> _myBehaviors;

    public JoglEngineView(Engine theEngine) {
        _myEngine = theEngine;
        scale().set(10, 10, 10);
        material = new JoglMaterial();
        material().wireframe = true;
        _myBehaviors = new Vector<IBehaviorView> ();
    }


    public void addBehavior(IBehaviorView theBehavior) {
        _myBehaviors.add(theBehavior);
    }


    public void draw(GLContext theRenderContext) {
        /* material */
        material().begin(theRenderContext);

        /* draw entity */
        GL gl = ( (JoglGLContext) theRenderContext).gl;

        /* --- */
        gl.glPushMatrix();

        /* draw object */
        gl.glTranslatef(_myEngine.position().x, _myEngine.position().y, _myEngine.position().z);
        gl.glPushMatrix();
        gl.glScalef(scale().x, scale().y, scale().z);
        JoglAtomPlane.draw(gl, Gestalt.SHAPE_ORIGIN_CENTERED);
        gl.glPopMatrix();

        /** @todo replace this with a sphere */
        gestalt.util.JoglUtil.circle(gl, _myEngine.getBoundingRadius());

        /* draw velocity -- 1, 2 and 3 seconds ahead */
        gl.glBegin(GL.GL_LINES);
        gl.glVertex3f(0, 0, 0);
        gl.glVertex3f(_myEngine.velocity().x,
                      _myEngine.velocity().y,
                      _myEngine.velocity().z);
        gl.glEnd();

        /* --- */
        gl.glPopMatrix();

        /* draw behaviors */
        gl.glPushMatrix();
        for (int i = 0; i < _myBehaviors.size(); i++) {
            _myBehaviors.get(i).draw(theRenderContext, _myEngine);
        }
        gl.glPopMatrix();

        /* material */
        material().end(theRenderContext);
    }
}
