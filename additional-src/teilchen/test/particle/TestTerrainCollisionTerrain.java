/*
 * Teilchen
 *
 * Copyright (C) 2013
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


package teilchen.test.particle;

import gestalt.candidates.JoglTerrain;
import gestalt.context.DisplayCapabilities;
import gestalt.context.GLContext;
import gestalt.material.Material;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmaps;
import gestalt.material.texture.bitmap.ByteBitmap;
import gestalt.render.AnimatorRenderer;
import gestalt.shape.AbstractShape;
import gestalt.shape.Plane;
import gestalt.shape.PointSpriteCloud;
import gestalt.shape.atom.AtomCube;

import mathematik.TransformMatrix4f;
import mathematik.Vector3f;

import data.Resource;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.force.Gravity;
import teilchen.force.ViscousDrag;
import teilchen.gestalt.util.TerrainCollider;

import java.util.Iterator;

import javax.media.opengl.GL;


public class TestTerrainCollisionTerrain
    extends AnimatorRenderer {

    private TerrainCollider _myTerrain;

    private Vector3f _myRotation;

    private Physics _myParticleSystem;

    private PointSpriteCloud _myParticleView;

    private JoglTerrain _myTerrainGeometry;

    private TexturePlugin _myParticleTexture;

    public void setup() {
        /* setup renderer */
        light().enable = true;
        cameramover(true);
        fpscounter(true);
        framerate(UNDEFINED);

        camera().setMode(CAMERA_MODE_LOOK_AT);
        camera().position().set( -256.22897, -394.05145, 469.3587);
        camera().upvector().set(0, 0, 1);

        /* physics */
        _myParticleSystem = new Physics();

        Gravity myGravity = new Gravity();
        myGravity.force().set( -20, 0, -50);
        _myParticleSystem.add(myGravity);

        ViscousDrag myViscousDrag = new ViscousDrag();
        myViscousDrag.coefficient = 0.85f;
        _myParticleSystem.add(myViscousDrag);

        /* setup world */
        ByteBitmap myStillBitmap = Bitmaps.getBitmap(Resource.getStream("ground.png"));
        TexturePlugin myGroundTexture = drawablefactory().texture();
        myGroundTexture.load(Bitmaps.getBitmap(Resource.getStream("ground_texture.png")));
        _myTerrainGeometry = new JoglTerrain(20, 20, new Vector3f(30f, 20f, 50f));
        _myTerrainGeometry.position().x -= (20 * 30) / 2f;
        _myTerrainGeometry.position().y -= (20 * 20) / 2f;
        _myTerrainGeometry.material().lit = true;
        _myTerrainGeometry.material().addPlugin(myGroundTexture);
        _myTerrainGeometry.deform(myStillBitmap);
        bin(BIN_3D).add(_myTerrainGeometry);

        _myTerrain = new TerrainCollider(_myTerrainGeometry);
        _myTerrain.scale().set(_myTerrainGeometry.scale());
        _myTerrain.transform().translation.set(_myTerrainGeometry.position());
        _myParticleSystem.add(_myTerrain);
        bin(BIN_3D).add(new JoglTerrainCollisionView(_myTerrain));

        _myParticleTexture = drawablefactory().texture();
        _myParticleTexture.load(Bitmaps.getBitmap(Resource.getStream("flower-particle.png")));
        _myParticleView = new PointSpriteCloud();
        _myParticleView.loadBitmap(Bitmaps.getBitmap(Resource.getStream("flower-particle.png")));
        _myParticleView.material().blendmode = MATERIAL_BLEND_INVERS_MULTIPLY;
        _myParticleView.material().depthtest = false;
        _myParticleView.material().color4f().set(1.0f, 0.5f);
        _myParticleView.POINT_SIZE = 40;
//        bin(BIN_3D).add(_myParticleView);

        _myTerrainGeometry.position().z = -10; // HACK!

        _myRotation = new Vector3f();
    }


    public void loop(float theDeltaTime) {
        light().position().set(camera().position());
        /* handle world */
        _myParticleSystem.step(theDeltaTime);

        if (event().mouseDown) {
            Vector3f myPosition = new Vector3f(event().mouseX, event().mouseY, 100);
            Particle myParticle = _myParticleSystem.makeParticle();
            myParticle.position().set(myPosition);
//            _myParticleView.vertices().add(myParticle.position());

            addParticle(_myParticleTexture, myPosition);
        }

        /* handle input */
        if (event().keyDown) {
            if (event().keyCode == KEYCODE_M) {
                Iterator<Particle> iter = _myParticleSystem.particles().iterator();
                while (iter.hasNext()) {
                    Particle item = iter.next();
                    item.position().x += theDeltaTime * 20;
                }
            }
            if (event().keyCode == KEYCODE_N) {
                Iterator<Particle> iter = _myParticleSystem.particles().iterator();
                while (iter.hasNext()) {
                    Particle item = iter.next();
                    item.position().x -= theDeltaTime * 20;
                }
            }
            if (event().keyCode == KEYCODE_SPACE) {
                _myParticleSystem.particles().clear();
                _myParticleView.vertices().clear();
            }
            if (!event().shift) {
                if (event().keyCode == KEYCODE_J) {
                    _myTerrain.transform().translation.x -= theDeltaTime * 20;
                }
                if (event().keyCode == KEYCODE_L) {
                    _myTerrain.transform().translation.x += theDeltaTime * 20;
                }
                if (event().keyCode == KEYCODE_K) {
                    _myTerrain.transform().translation.y -= theDeltaTime * 20;
                }
                if (event().keyCode == KEYCODE_I) {
                    _myTerrain.transform().translation.y += theDeltaTime * 20;
                }
            } else {
                if (event().keyCode == KEYCODE_J) {
                    _myRotation.z += theDeltaTime;
                    _myTerrain.transform().rotation.setXYZRotation(_myRotation);
                }
                if (event().keyCode == KEYCODE_L) {
                    _myRotation.z -= theDeltaTime;
                    _myTerrain.transform().rotation.setXYZRotation(_myRotation);
                }
                if (event().keyCode == KEYCODE_I) {
                    _myRotation.y += theDeltaTime;
                    _myTerrain.transform().rotation.setXYZRotation(_myRotation);
                }
                if (event().keyCode == KEYCODE_K) {
                    _myRotation.y -= theDeltaTime;
                    _myTerrain.transform().rotation.setXYZRotation(_myRotation);
                }
            }
        }
    }


    private void addParticle(TexturePlugin theTexture, Vector3f thePosition) {
        Plane myParticlePlane = drawablefactory().plane();
        myParticlePlane.scale().set(20, 20, 1);
        myParticlePlane.material().addPlugin(theTexture);
        myParticlePlane.material().blendmode = MATERIAL_BLEND_INVERS_MULTIPLY;
        myParticlePlane.material().transparent = false;
        bin(BIN_3D).add(myParticlePlane);

        Particle myParticle = _myParticleSystem.makeParticle();
        myParticle.position().set(thePosition);
        myParticlePlane.setPositionRef(myParticle.position());
    }


    public void keyPressed(char theChar, int theKeyCode) {
        if (theKeyCode == KEYCODE_B) {
            for (int x = 0; x < displaycapabilities().width; x += 10) {
                for (int y = 0; y < displaycapabilities().height; y += 10) {
                    Vector3f myPosition = new Vector3f(x - displaycapabilities().width / 2,
                                                       y - displaycapabilities().height / 2,
                                                       50);
                    Particle myParticle = _myParticleSystem.makeParticle();
                    myParticle.position().set(myPosition);
//                    _myParticleView.vertices().add(myParticle.position());
                }
            }
        }
    }


    private class JoglTerrainCollisionView
        extends AbstractShape {

        private final TerrainCollider _myWorld;

        public JoglTerrainCollisionView(TerrainCollider theWorld) {
            _myWorld = theWorld;
            material = new Material();
            material().wireframe = true;
            material().transparent = true;
        }


        public void draw(GLContext theRenderContext) {
            /* material */
            material().begin(theRenderContext);

            /* collect data */
            final TransformMatrix4f myTransform = _myWorld.transform();
            final Vector3f myScale = new Vector3f(_myWorld.scale());

            /* draw world */
            GL gl = (  theRenderContext).gl;
            gl.glPushMatrix();
            /* rotation + translation */
            gl.glMultMatrixf(myTransform.toArray(), 0);
            /* scale */
            gl.glScalef(myScale.x, myScale.y, myScale.z);
            for (int x = 0; x < _myWorld.width; x++) {
                for (int y = 0; y < _myWorld.height; y++) {
                    gl.glPushMatrix();
                    gl.glTranslatef(x, y, 0);
                    gl.glScalef(1, 1, _myWorld.heightfield[x][y]);
                    gl.glColor4f(1, 1, 1, 0.08f);
                    AtomCube.draw(gl, SHAPE_ORIGIN_BOTTOM_LEFT);
                    gl.glPopMatrix();
                }
            }
            gl.glPopMatrix();

            /* material */
            material().end(theRenderContext);
        }
    }


    public static void main(String[] args) {
        DisplayCapabilities dc = new DisplayCapabilities();
        dc.fullscreen = true;
        dc.antialiasinglevel = 4;
        new TestTerrainCollisionTerrain().init(dc);
    }
}
