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


package verhalten.test;


import java.util.Vector;

import gestalt.render.AnimatorRenderer;
import gestalt.shape.Plane;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.FontProducer;
import gestalt.util.CameraMover;

import mathematik.Vector3f;
import mathematik.Vector3i;

import teilchen.cubicle.CubicleAtom;
import teilchen.cubicle.CubicleWorld;
import teilchen.cubicle.ICubicleEntity;
import teilchen.gestalt.util.JoglCubicleWorldView;
import verhalten.Engine;
import verhalten.IVerhaltenEntity;
import verhalten.Seek;
import verhalten.Separation;
import verhalten.Wander;
import verhalten.view.JoglEngineView;
import verhalten.view.JoglSeekView;
import verhalten.view.JoglSeparationView;


/*
 * objects moving in a cubicle world.
 * one of them tries to eat the others.
 * bad dog.
 */

public class TestTagInCubicleWorld
    extends AnimatorRenderer {

    private Vector<Prey> _myPrey;

    private Predator _myPredator;

    private Plane _myScore;

    private FontProducer _myFont;

    private int _myNumberOfTags;

    private float _myTimeSinceLastTag;

    private CubicleWorld _myWorld;

    private TexturePlugin _myScoreTexture;

    public void setup() {
        /* setup renderer */
        framerate(60);

        /* setup world */
        _myWorld = new CubicleWorld(10, 10, 1);
        _myWorld.cellscale().set(displaycapabilities().width / 10, displaycapabilities().height / 10, 20);
        _myWorld.transform().translation.x = -displaycapabilities().width / 2;
        _myWorld.transform().translation.y = -displaycapabilities().height / 2;
        bin(BIN_3D).add(new JoglCubicleWorldView(_myWorld));

        /* entities */
        int myNumberOfPrey = 10;
        _myPrey = new Vector<Prey> ();
        for (int i = 0; i < myNumberOfPrey; i++) {
            Prey myPrey = new Prey();
            _myPrey.add(myPrey);
            bin(BIN_3D).add(myPrey.getView());
            _myWorld.add(myPrey);
        }

        _myPredator = new Predator();
        _myWorld.add(_myPredator);
        bin(BIN_3D).add(_myPredator.getView());

        /* setup scoreboard */
        _myTimeSinceLastTag = 0;
        _myNumberOfTags = 0;
        _myFont = FontProducer.fromInstalledFont("Helvetica", FONT_QUALITY_HIGH);
        _myFont.setSize(14);
        _myScore = drawablefactory().plane();
        _myScore.origin(SHAPE_ORIGIN_BOTTOM_LEFT);
        _myScoreTexture = drawablefactory().texture();
        _myScore.material().addPlugin(_myScoreTexture);
        _myScore.position().x = -displaycapabilities().width / 2 + 10;
        _myScore.position().y = -displaycapabilities().height / 2 + 10;
        updateScoreBoard();
        _myScore.setPlaneSizeToTextureSize();
        bin(BIN_2D_FOREGROUND).add(_myScore);
    }


    private void updateScoreBoard() {
        _myScoreTexture.load(_myFont.getBitmap( (_myNumberOfTags / _myTimeSinceLastTag) + " TAGS/SECOND"));
        _myScore.setPlaneSizeToTextureSize();
    }


    public void loop(float theDeltaTime) {
        _myTimeSinceLastTag += theDeltaTime;

        for (int i = 0; i < _myPrey.size(); i++) {
            _myPrey.get(i).loop(theDeltaTime);
        }

        _myPredator.loop(theDeltaTime);

        /* handle world */
        _myWorld.update();

        /* handle camera */
        CameraMover.handleKeyEvent(camera(), event(), theDeltaTime);
    }


    private void teleport(Engine theEngine) {
        /* teleport */
        if (theEngine.position().x > displaycapabilities().width / 2) {
            theEngine.position().x -= displaycapabilities().width;
        }
        if (theEngine.position().x < displaycapabilities().width / -2) {
            theEngine.position().x += displaycapabilities().width;
        }
        if (theEngine.position().y > displaycapabilities().height / 2) {
            theEngine.position().y -= displaycapabilities().height;
        }
        if (theEngine.position().y < displaycapabilities().height / -2) {
            theEngine.position().y += displaycapabilities().height;
        }
    }


    private class Predator
        implements ICubicleEntity {

        private Vector3i _myCubiclePosition;

        private Engine engine;

        private Seek _mySeek;

        private JoglEngineView _myView;

        public Predator() {
            /* setup engine */
            engine = new Engine();
            engine.setMaximumForce(200.0f);
            engine.setMaximumSpeed(100.0f);
            engine.velocity().set(Math.random() - 0.5f,
                                  Math.random() - 0.5f);
            engine.position().x = displaycapabilities().width * 0.33f;

            /* setup behavior */
            _mySeek = new Seek();

            /* setup view */
            _myView = new JoglEngineView(engine);
            _myView.material().color4f().set(0, 0.5f, 1);
            _myView.addBehavior(new JoglSeekView(_mySeek));
            bin(BIN_3D).add(_myView);

            /* cubicle stuff */
            _myCubiclePosition = new Vector3i();

        }


        public JoglEngineView getView() {
            return _myView;
        }


        public void loop(float theDeltaTime) {
            /* find closest prey */
            Prey myClosestPrey = null;
            float myClosestDistance = Float.MAX_VALUE;

            Vector<CubicleAtom> myVisiblePrey = _myWorld.getAtoms(_myCubiclePosition.x,
                                                                     _myCubiclePosition.y,
                                                                     _myCubiclePosition.z,
                                                                     2, 2, 0);
            for (int j = 0; j < myVisiblePrey.size(); j++) {
                Vector<ICubicleEntity> myEntitiesFromCubicle = myVisiblePrey.get(j).data();
                for (int i = 0; i < myEntitiesFromCubicle.size(); i++) {
                    ICubicleEntity myEntity = myEntitiesFromCubicle.get(i);
                    if (myEntity != this) {
                        float myDistanceToPrey = myEntity.position().distance(engine.position());
                        if (myDistanceToPrey < myClosestDistance &&
                            myEntity instanceof Prey) {
                            myClosestPrey = (Prey) myEntity;
                            myClosestDistance = myDistanceToPrey;
                        }
                    }
                }
            }

            /* seek */
            Vector3f mySeparation = new Vector3f();
            if (myClosestPrey != null) {
                _mySeek.setPoint(myClosestPrey.engine.position());
                _mySeek.get(engine,
                            mySeparation);
                /* tagged */
                if (engine.position().distance(myClosestPrey.engine.position()) <
                    engine.getBoundingRadius() + myClosestPrey.engine.getBoundingRadius()) {
                    /* kill prey */
                    _myWorld.remove(myClosestPrey);
                    _myPrey.remove(myClosestPrey);
                    bin(BIN_3D).remove(myClosestPrey.getView());
                    _myNumberOfTags++;
                    updateScoreBoard();
                }
            }

            engine.apply(theDeltaTime, mySeparation);
            teleport(engine);
        }


        /* -> cubicle obligations */

        public Vector3i cubicle() {
            return _myCubiclePosition;
        }


        public Vector3f position() {
            return engine.position();
        }


        public boolean leaving(int theX, int theY, int theZ) {
            if (theX == cubicle().x &&
                theY == cubicle().y &&
                theZ == cubicle().z) {
                return false;
            }
            return true;
        }


        public boolean isActive() {
            return true;
        }

        /* --- */

    }


    private class Prey
        implements ICubicleEntity {

        private Vector3i _myCubiclePosition;

        public Engine engine;

        private Separation _mySeparation;

        private Wander _myWander;

        private JoglEngineView _myView;

        public Prey() {
            /* engine */
            engine = new Engine();
            engine.setMaximumForce(200.0f);
            engine.setMaximumSpeed(70.0f);
            engine.velocity().set(Math.random() - 0.5f,
                                  Math.random() - 0.5f);
            engine.position().x = displaycapabilities().width * 0.66f;

            /* behavior */
            _mySeparation = new Separation();
            _mySeparation.setPrivacyRadius(200);
            _myWander = new Wander();
            _myWander.setRadius(10);

            /* setup view */
            _myView = new JoglEngineView(engine);
            _myView.material().color4f().set(1, 1, 1);
            _myView.addBehavior(new JoglSeparationView(_mySeparation));

            /* cubicle stuff */
            _myCubiclePosition = new Vector3i();
        }


        /* -> cubicle obligations */

        public Vector3i cubicle() {
            return _myCubiclePosition;
        }


        public Vector3f position() {
            return engine.position();
        }


        public boolean leaving(int theX, int theY, int theZ) {
            if (theX == cubicle().x &&
                theY == cubicle().y &&
                theZ == cubicle().z) {
                return false;
            }
            return true;
        }


        /* --- */

        public JoglEngineView getView() {
            return _myView;
        }


        public void loop(float theDeltaTime) {
            Vector3f mySeparation = new Vector3f();
            Vector3f myWanderDirection = new Vector3f();
            _mySeparation.get(new IVerhaltenEntity[] {_myPredator.engine},
                              engine.position(),
                              mySeparation);
            if (mySeparation.lengthSquared() == 0) {
                _myWander.get(engine, theDeltaTime, myWanderDirection);
            }

            mySeparation.add(myWanderDirection);

            engine.apply(theDeltaTime, mySeparation);
            teleport(engine);
        }


        public boolean isActive() {
            return true;
        }

    }


    public static void main(String[] args) {
        new TestTagInCubicleWorld().init();
    }
}
