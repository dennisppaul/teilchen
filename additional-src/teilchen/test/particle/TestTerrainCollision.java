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


package teilchen.test.particle;


import java.util.Iterator;
import javax.media.opengl.GL;

import gestalt.shape.PointSpriteCloud;
import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.material.Material;
import gestalt.shape.atom.AtomCube;
import gestalt.render.AnimatorRenderer;
import gestalt.shape.AbstractShape;
import gestalt.shape.Plane;
import gestalt.material.texture.Bitmaps;

import mathematik.TransformMatrix4f;
import mathematik.Vector2f;
import mathematik.Vector3f;

import data.Resource;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.gestalt.util.TerrainCollider;
import teilchen.force.Gravity;
import teilchen.force.ViscousDrag;
import processing.core.PApplet;


/*
 * test the cubicle world concept
 */

public class TestTerrainCollision
    extends AnimatorRenderer {

    private TerrainCollider _myTerrain;

    private Vector3f _myRotation;

    private Physics _myParticleSystem;

    private PointSpriteCloud _myParticleView;

    public void setup() {
        /* setup renderer */
        cameramover(true);
        fpscounter(true);
        framerate(UNDEFINED);

        camera().setMode(CAMERA_MODE_LOOK_AT);
        camera().position().set( -256.22897, -394.05145, 469.3587);
        camera().upvector().set(0, 0, 1);

        Plane myPlane = drawablefactory().plane();
        myPlane.scale().set(500, 500);
        myPlane.material().addTexture().load(Bitmaps.getBitmap(Resource.getStream("flower-particle.png")));
        myPlane.material().blendmode = MATERIAL_BLEND_INVERS_MULTIPLY;
        myPlane.material().color.set(1, 1, 1, 0.75f);
        bin(BIN_3D).add(myPlane);

        /* physics */
        _myParticleSystem = new Physics();

        Gravity myGravity = new Gravity();
        myGravity.force().set( -20, 0, -50);
        _myParticleSystem.add(myGravity);

        ViscousDrag myViscousDrag = new ViscousDrag();
        myViscousDrag.coefficient = 0.85f;
        _myParticleSystem.add(myViscousDrag);

        /* setup world */
        _myTerrain = new TerrainCollider(32, 24);
        _myTerrain.scale().set(20, 20, 100);
        _myTerrain.transform().translation.set( -32 * 20 / 2, -24 * 20 / 2);
        _myParticleSystem.add(_myTerrain);
        bin(BIN_3D).add(new JoglTerrainCollisionView(_myTerrain));

        /* create random height field */
        final Vector2f myNoise = new Vector2f();
        for (int x = 0; x < _myTerrain.heightfield.length; x++) {
            myNoise.x += 0.03f;
            for (int y = 0; y < _myTerrain.heightfield[x].length; y++) {
                Foo.noise(myNoise.x, myNoise.y, 0);
                myNoise.y += 0.06f;
                _myTerrain.heightfield[x][y] = Foo.noise(myNoise.x, myNoise.y, 0) * 0.5f;
            }
        }

        _myParticleView = new PointSpriteCloud();
        _myParticleView.loadBitmap(Bitmaps.getBitmap(Resource.getStream("flower-particle.png")));
        _myParticleView.material().blendmode = MATERIAL_BLEND_INVERS_MULTIPLY;
        _myParticleView.material().depthtest = false;
        _myParticleView.material().color.set(1.0f, 0.5f);
        _myParticleView.POINT_SIZE = 40;
        bin(BIN_3D).add(_myParticleView);

        _myRotation = new Vector3f();
    }


    public void loop(float theDeltaTime) {

        /* handle world */
        _myParticleSystem.step(theDeltaTime);

        if (event().mouseDown) {
            Vector3f myPosition = new Vector3f(event().mouseX, event().mouseY, 0);
            Particle myParticle = _myParticleSystem.makeParticle();
            myParticle.position().set(myPosition);
            _myParticleView.vertices().add(myParticle.position());
        }

        /* handle input */
        if (event().keyDown) {
            if (event().keyCode == KEYCODE_M) {
                Iterator<Particle> iter = _myParticleSystem.particles().iterator();
                while (iter.hasNext()) {
                    Particle item = (Particle) iter.next();
                    item.position().x += theDeltaTime * 20;
                }
            }
            if (event().keyCode == KEYCODE_N) {
                Iterator<Particle> iter = _myParticleSystem.particles().iterator();
                while (iter.hasNext()) {
                    Particle item = (Particle) iter.next();
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


    public void keyPressed(char theChar, int theKeyCode) {
        if (theKeyCode == KEYCODE_B) {
            for (int x = 0; x < displaycapabilities().width; x += 10) {
                for (int y = 0; y < displaycapabilities().height; y += 10) {
                    Vector3f myPosition = new Vector3f(x - displaycapabilities().width / 2,
                                                       y - displaycapabilities().height / 2,
                                                       50);
                    Particle myParticle = _myParticleSystem.makeParticle();
                    myParticle.position().set(myPosition);
                    _myParticleView.vertices().add(myParticle.position());
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
            final Vector3f myScale = _myWorld.scale();

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
        new TestTerrainCollision().init();
    }


    public static class Foo {

        /* this is ruthlessly borrowed from processing. i ll return it as soon as i m done with it. */

        static final int PERLIN_YWRAPB = 4;

        static final int PERLIN_YWRAP = 1 << PERLIN_YWRAPB;

        static final int PERLIN_ZWRAPB = 8;

        static final int PERLIN_ZWRAP = 1 << PERLIN_ZWRAPB;

        static final int PERLIN_SIZE = 4095;

        static int perlin_octaves = 4; // default to medium smooth

        static float perlin_amp_falloff = 0.5f; // 50% reduction/octave

        static int perlin_TWOPI, perlin_PI;

        static float[] perlin_cosTable;

        static float[] perlin;

        static java.util.Random perlinRandom;

        static final float DEG_TO_RAD = PApplet.PI / 180.0f;

        static final protected float sinLUT[];

        static final protected float cosLUT[];

        static final protected float SINCOS_PRECISION = 0.5f;

        static final protected int SINCOS_LENGTH = (int) (360f / SINCOS_PRECISION);

        static {
            sinLUT = new float[SINCOS_LENGTH];
            cosLUT = new float[SINCOS_LENGTH];
            for (int i = 0; i < SINCOS_LENGTH; i++) {
                sinLUT[i] = (float) Math.sin(i * DEG_TO_RAD * SINCOS_PRECISION);
                cosLUT[i] = (float) Math.cos(i * DEG_TO_RAD * SINCOS_PRECISION);
            }
        }


        private static float noise_fsc(float i) {
            // using bagel's cosine table instead
            return 0.5f * (1.0f - perlin_cosTable[ (int) (i * perlin_PI) % perlin_TWOPI]);
        }


        public static float noise(float x, float y, float z) {
            if (perlin == null) {
                if (perlinRandom == null) {
                    perlinRandom = new java.util.Random();
                }
                perlin = new float[PERLIN_SIZE + 1];
                for (int i = 0; i < PERLIN_SIZE + 1; i++) {
                    perlin[i] = perlinRandom.nextFloat(); //(float)Math.random();
                }
                // [toxi 031112]
                // noise broke due to recent change of cos table in PGraphics
                // this will take care of it
                perlin_cosTable = cosLUT;
                perlin_TWOPI = perlin_PI = SINCOS_LENGTH;
                perlin_PI >>= 1;
            }

            if (x < 0) {
                x = -x;
            }
            if (y < 0) {
                y = -y;
            }
            if (z < 0) {
                z = -z;
            }

            int xi = (int) x, yi = (int) y, zi = (int) z;
            float xf = (float) (x - xi);
            float yf = (float) (y - yi);
            float zf = (float) (z - zi);
            float rxf, ryf;

            float r = 0;
            float ampl = 0.5f;

            float n1, n2, n3;

            for (int i = 0; i < perlin_octaves; i++) {
                int of = xi + (yi << PERLIN_YWRAPB) + (zi << PERLIN_ZWRAPB);

                rxf = noise_fsc(xf);
                ryf = noise_fsc(yf);

                n1 = perlin[of & PERLIN_SIZE];
                n1 += rxf * (perlin[ (of + 1) & PERLIN_SIZE] - n1);
                n2 = perlin[ (of + PERLIN_YWRAP) & PERLIN_SIZE];
                n2 += rxf * (perlin[ (of + PERLIN_YWRAP + 1) & PERLIN_SIZE] - n2);
                n1 += ryf * (n2 - n1);

                of += PERLIN_ZWRAP;
                n2 = perlin[of & PERLIN_SIZE];
                n2 += rxf * (perlin[ (of + 1) & PERLIN_SIZE] - n2);
                n3 = perlin[ (of + PERLIN_YWRAP) & PERLIN_SIZE];
                n3 += rxf * (perlin[ (of + PERLIN_YWRAP + 1) & PERLIN_SIZE] - n3);
                n2 += ryf * (n3 - n2);

                n1 += noise_fsc(zf) * (n2 - n1);

                r += n1 * ampl;
                ampl *= perlin_amp_falloff;
                xi <<= 1;
                xf *= 2;
                yi <<= 1;
                yf *= 2;
                zi <<= 1;
                zf *= 2;

                if (xf >= 1.0f) {
                    xi++;
                    xf--;
                }
                if (yf >= 1.0f) {
                    yi++;
                    yf--;
                }
                if (zf >= 1.0f) {
                    zi++;
                    zf--;
                }
            }
            return r;
        }
    }

}
