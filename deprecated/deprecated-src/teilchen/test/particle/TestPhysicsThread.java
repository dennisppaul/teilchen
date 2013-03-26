package teilchen.test.particle;


//import claylike.arcball.ArcBall;
import java.util.Vector;

import mathematik.Vector3f;

import data.Resource;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.force.Attractor;
import teilchen.force.Gravity;
import teilchen.force.Spring;
import teilchen.force.ViscousDrag;
import teilchen.gestalt.util.PhysicsThread;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;


public class TestPhysicsThread
    extends PApplet {

    private Physics _myParticleSystem;

    private Vector<MyPlate> _myPlates;

    private static final boolean RUN_EXTRA_THREAD = true;

    private PhysicsThread _myPhysicsThread;

    private Attractor _myAttractor;

    private float _myCounter;

    private PImage _myTexture;

    public void setup() {
        size(1024, 768, OPENGL);
        frameRate(60);
  //      new ArcBall(this);
        hint(DISABLE_DEPTH_TEST);
        hint(DISABLE_DEPTH_SORT);
        textFont(createFont("Courier", 10));
        textureMode(NORMAL);
        _myTexture = loadImage(Resource.getPath("square.png"));

        if (RUN_EXTRA_THREAD) {
            _myPhysicsThread = new PhysicsThread();
            _myPhysicsThread.framerate(120);
            _myParticleSystem = _myPhysicsThread.physics();
        } else {
            _myParticleSystem = new Physics();
        }

        Gravity myGravity = new Gravity();
        myGravity.force().y = 50;
        _myParticleSystem.add(myGravity);

        ViscousDrag myViscousDrag = new ViscousDrag();
        myViscousDrag.coefficient = 0.85f;
        _myParticleSystem.add(myViscousDrag);

        _myAttractor = new Attractor();
        _myAttractor.radius(400);
        _myAttractor.strength( -200);
        _myAttractor.position().set(0, 0, 5);
        _myParticleSystem.add(_myAttractor);

        _myPlates = new Vector<MyPlate> ();
        final float myPlateSize = 20;
        int X = (int) (width / myPlateSize);
        int Y = (int) (height / myPlateSize);
        for (int x = -X / 2 + 1; x < X / 2; x++) {
            for (int y = -Y / 2; y < Y / 2; y++) {
                MyPlate myMyPlate = new MyPlate(_myParticleSystem);
                myMyPlate.setPosition(new Vector3f(x * myPlateSize, y * myPlateSize));
                myMyPlate.platesize(myPlateSize);
                myMyPlate.padding(1f);
                _myPlates.add(myMyPlate);
            }
        }

        if (RUN_EXTRA_THREAD) {
            _myPhysicsThread.start();
        }
    }


    public void draw() {

        _myAttractor.position().set(mouseX - width / 2, mouseY - height / 2);

        final float myDeltaTime = 1f / frameRate;

        for (MyPlate myMyPlate : _myPlates) {
            myMyPlate.loop();
        }

        if (!RUN_EXTRA_THREAD) {
            _myParticleSystem.step(myDeltaTime);
        }

        /* draw */
        background(255);

        /* draw plates */
        pushMatrix();
        translate(width / 2, height / 2);

        _myCounter += myDeltaTime;
        pointLight(255, 255, 255,
                   cos(_myCounter * PI * 0.45f * 0.1f) * 500,
                   sin(_myCounter * PI * 0.1f) * 50,
                   100);
        ambientLight(80, 80, 80);

        fill(255);
        noStroke();
        beginShape(QUADS);
        texture(_myTexture);
        for (MyPlate myMyPlate : _myPlates) {
            myMyPlate.draw(g);
        }
        endShape();
        popMatrix();

        /* show framerate */
        fill(255, 0, 0, 127);
        text( (int) frameRate, 10, 20);

        if (RUN_EXTRA_THREAD) {
            fill(0, 255, 0, 127);
            text(_myPhysicsThread.framerate(), 10, 30);
        }
    }


    public static class MyPlate {
        private Particle _myParticleFixed;

        private Particle _myParticle;

        public float _myPlateSize = 20;

        private float _myPadding = 0;

        private Spring _myConnection;

        private Vector3f _myNormal;

        private Vector3f _myEdge;

        private Vector3f _mySide;

        public MyPlate(Physics _myParticleSystem) {

            _myParticleFixed = _myParticleSystem.makeParticle();
            _myParticleFixed.position().set(0, 0, 0);
            _myParticleFixed.fixed(true);

            _myParticle = _myParticleSystem.makeParticle();
            _myParticle.position().set(0, _myPlateSize, 0);
            _myParticle.fixed(false);

            _myConnection = new Spring(_myParticleFixed, _myParticle);
            _myConnection.setOneWay(true);
            _myConnection.restlength(_myPlateSize);
            _myConnection.strength(200);
            _myConnection.damping(10);
            _myParticleSystem.add(_myConnection);

            _mySide = new Vector3f(1, 0, 0);
            _myNormal = new Vector3f(0, 0, 1);
            _myEdge = new Vector3f(0, 1, 0);
        }


        public void padding(float thePadding) {
            _myPadding = thePadding;
        }


        public void setPosition(Vector3f theNewPosition) {
            _myParticleFixed.position().set(theNewPosition);
            _myParticle.position().set(theNewPosition);
            _myParticle.position().y += _myPlateSize;
        }


        private void platesize(float thePlateSize) {
            _myPlateSize = thePlateSize;
            _myConnection.restlength(_myPlateSize);
            _myParticle.position().set(_myParticleFixed.position());
            _myParticle.position().y += _myPlateSize;
        }


        public void loop() {
            /* constraint */
            _myParticle.position().x = _myParticleFixed.position().x;
            _myParticle.velocity().x = 0;

            /* normal */
            _myEdge.sub(_myParticleFixed.position(), _myParticle.position());
            _myEdge.normalize();
            _myNormal.cross(_myEdge, _mySide);
            _myNormal.normalize();
        }


        public void draw(PGraphics pg) {
            pg.normal(_myNormal.x, _myNormal.y, _myNormal.z);
            pg.vertex(_myParticle.position().x - _myPlateSize / 2 + _myPadding,
                      _myParticle.position().y + _myEdge.y * _myPadding,
                      _myParticle.position().z,
                      0, 0);
            pg.vertex(_myParticle.position().x + _myPlateSize / 2 - _myPadding,
                      _myParticle.position().y + _myEdge.y * _myPadding,
                      _myParticle.position().z,
                      1, 0);
            pg.vertex(_myParticleFixed.position().x + _myPlateSize / 2 - _myPadding,
                      _myParticleFixed.position().y - _myEdge.y * _myPadding,
                      _myParticleFixed.position().z,
                      1, 1);
            pg.vertex(_myParticleFixed.position().x - _myPlateSize / 2 + _myPadding,
                      _myParticleFixed.position().y - _myEdge.y * _myPadding,
                      _myParticleFixed.position().z,
                      0, 1);
        }
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {TestPhysicsThread.class.getName()});
    }
}
