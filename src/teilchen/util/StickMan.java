package teilchen.util;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import teilchen.BasicParticle;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.force.MuscleSpring;

public class StickMan {

    private final StableSpringQuad _myQuad;

    private final float _myScale;

    private final BasicParticle myLeftFoot;

    private final BasicParticle myRightFoot;

    private final BasicParticle myLeftHand;

    private final BasicParticle myRightHand;

    public StickMan(Physics theParticleSystem, float theOffset, float theScale) {

        _myScale = theScale;

        /* body */
        _myQuad = new StableSpringQuad(theParticleSystem,
                                       new PVector(10 * theScale + theOffset, 50 * theScale),
                                       new PVector(40 * theScale + theOffset, 50 * theScale),
                                       new PVector(50 * theScale + theOffset, 0),
                                       new PVector(0 + theOffset, 0));
        /* legs */
        myLeftFoot = theParticleSystem.makeParticle(new PVector(10 * theScale + theOffset, 100 * theScale), 0.5f);
        theParticleSystem.makeSpring(_myQuad.a, myLeftFoot, 100, 0.1f);
        theParticleSystem.makeSpring(_myQuad.b, myLeftFoot, 100, 0.1f);
        theParticleSystem.makeSpring(_myQuad.d, myLeftFoot, 2, 1f);

        myRightFoot = theParticleSystem.makeParticle(new PVector(40 * theScale + theOffset, 100 * theScale), 0.5f);
        theParticleSystem.makeSpring(_myQuad.a, myRightFoot, 100, 0.1f);
        theParticleSystem.makeSpring(_myQuad.b, myRightFoot, 100, 0.1f);
        theParticleSystem.makeSpring(_myQuad.c, myRightFoot, 2, 1f);

        /* arms */
        myRightHand = theParticleSystem.makeParticle(new PVector(70 * theScale + theOffset, 0), 0.1f);
        theParticleSystem.makeSpring(_myQuad.c, myRightHand, 100, 0.1f);
        theParticleSystem.makeSpring(_myQuad.b, myRightHand, 10, 0.1f);

        myLeftHand = theParticleSystem.makeParticle(new PVector(-20 * theScale + theOffset, 0), 0.1f);
        theParticleSystem.makeSpring(_myQuad.d, myLeftHand, 100, 0.1f);
        theParticleSystem.makeSpring(_myQuad.a, myLeftHand, 10, 0.1f);

        theParticleSystem.makeSpring(myLeftHand, myRightHand, 10, 0.1f);

        myLeftFoot.radius(7 * theScale);
        myRightFoot.radius(7 * theScale);
        myLeftHand.radius(20 * theScale);
        myRightHand.radius(20 * theScale);
        _myQuad.a.radius(7 * theScale);
        _myQuad.a.radius(7 * theScale);
        _myQuad.a.radius(7 * theScale);
        _myQuad.a.radius(7 * theScale);

        final float myMass = 2f;
        myLeftFoot.mass(myMass * theScale);
        myRightFoot.mass(myMass * theScale);
        myLeftHand.mass(myMass * theScale);
        myRightHand.mass(myMass * theScale);
        _myQuad.a.mass(myMass * theScale);
        _myQuad.b.mass(myMass * theScale);
        _myQuad.c.mass(myMass * theScale);
        _myQuad.d.mass(myMass * theScale);

        /* make legs move */
        MuscleSpring myMuscleSpring = new MuscleSpring(myLeftFoot, myRightFoot);
        myMuscleSpring.amplitude(20 * theScale);
        myMuscleSpring.strength(100);
        myMuscleSpring.offset((float) Math.random() * 2 * PApplet.PI);
        theParticleSystem.add(myMuscleSpring);
    }

    public void draw(PGraphics g) {

        g.stroke(255, 0, 0, 127);

        /* draw arms */
        g.line(myRightHand.position().x, myRightHand.position().y,
               _myQuad.c.position().x, _myQuad.c.position().y);
        g.line(myLeftHand.position().x, myLeftHand.position().y,
               _myQuad.d.position().x, _myQuad.d.position().y);

        /* draw legs */
        g.line(myRightFoot.position().x, myRightFoot.position().y,
               _myQuad.b.position().x, _myQuad.b.position().y);
        g.line(myLeftFoot.position().x, myLeftFoot.position().y,
               _myQuad.a.position().x, _myQuad.a.position().y);

        /* draw body */
        g.line(_myQuad.a.position().x, _myQuad.a.position().y,
               _myQuad.b.position().x, _myQuad.b.position().y);
        g.line(_myQuad.b.position().x, _myQuad.b.position().y,
               _myQuad.c.position().x, _myQuad.c.position().y);
        g.line(_myQuad.c.position().x, _myQuad.c.position().y,
               _myQuad.d.position().x, _myQuad.d.position().y);
        g.line(_myQuad.d.position().x, _myQuad.d.position().y,
               _myQuad.a.position().x, _myQuad.a.position().y);

        /* draw head */
        g.ellipse((_myQuad.c.position().x + _myQuad.d.position().x) * 0.5f,
                  (_myQuad.c.position().y + _myQuad.d.position().y) * 0.5f,
                  40 * _myScale, 40 * _myScale);
    }

    public Particle anchor() {
        return _myQuad.a;
    }
}
