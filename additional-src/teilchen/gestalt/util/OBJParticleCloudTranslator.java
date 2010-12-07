/*
 * Particles
 *
 * Copyright (C) 2010
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


import java.util.Vector;

import gestalt.candidates.JoglPointSpriteCloud;
import gestalt.impl.jogl.shape.JoglMesh;
import gestalt.model.ModelData;
import gestalt.model.ModelLoaderOBJ;
import gestalt.render.Drawable;
import gestalt.util.scenewriter.DrawableOBJTranslator;
import gestalt.util.scenewriter.MeshTranslator;
import gestalt.util.scenewriter.SceneWriter;

import data.Resource;
import teilchen.Particle;


public class OBJParticleCloudTranslator
        implements DrawableOBJTranslator {

    private final JoglMesh _myMesh;

    private final Vector<Particle> _myParticles;

    public OBJParticleCloudTranslator(Vector<Particle> theParticles) {
        ModelData myModelData = ModelLoaderOBJ.getModelData(Resource.getStream("ikosaeder.obj"));
        _myMesh = new JoglMesh(myModelData.vertices, 3,
                               myModelData.vertexColors, 4,
                               myModelData.texCoordinates, 2,
                               myModelData.normals,
                               myModelData.primitive);
        _myParticles = theParticles;
    }

    public boolean isClass(Drawable theDrawable) {
        return theDrawable instanceof JoglPointSpriteCloud;
    }

    public void parse(SceneWriter theParent, Drawable theDrawable) {
        /* write unique header */
        int myUniqueID = theParent.bumpUniqueObjectID();
        theParent.print(SceneWriter.GROUP + " particlecloud" + myUniqueID);
        theParent.println();

        /* send individual cubes */
        System.out.println("### WRITING POINT CLOUD.");
        for (int i = 0; i < _myParticles.size(); i++) {
            final Particle myParticle = _myParticles.get(i);
            _myMesh.scale().x = myParticle.radius() * 2;
            _myMesh.scale().z = _myMesh.scale().y = _myMesh.scale().x;
            _myMesh.position().set(myParticle.position());
            MeshTranslator.sendMesh(theParent, _myMesh);
            if (i % 50 == 0) {
                System.out.println(i + "/" + _myParticles.size());
            }
        }
        System.out.println(_myParticles.size() + "/" + _myParticles.size());
        System.out.println("### DONE.");
    }
}
