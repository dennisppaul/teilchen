package teilchen.test;

import processing.core.PApplet;
import processing.core.PSurface;
import processing.core.PVector;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.force.Gravity;
import teilchen.force.ViscousDrag;
import teilchen.util.Overlap;

import javax.swing.JFrame;
import java.awt.Window;
import java.awt.geom.Ellipse2D;

public class TestPilingUp extends PApplet {

    private static final float   PARTICLE_RADIUS = 13;
    private              Physics mPhysics;

    public void settings() {
        size(640, 480);
    }

    public PSurface initSurface() {
        super.initSurface();
        JFrame frame = (JFrame) ((processing.awt.PSurfaceAWT.SmoothCanvas) getSurface().getNative()).getFrame();
        frame.setUndecorated(true);
        Window          window = (Window) frame;
        Ellipse2D.Float circle = new Ellipse2D.Float(0, 0, 400, 400);
        window.setShape(circle);
        window.setOpacity(0.7f); // Set opacity to 70%
        return surface;
    }

    public void setup() {
        hint(ENABLE_DEPTH_SORT);
        mPhysics = new Physics();

        /* create drag */
        mPhysics.add(new ViscousDrag());
        mPhysics.add(new Gravity(new PVector(0, 100f, 0)));
    }

    public void draw() {
        if (mousePressed) {
            Particle mParticle = mPhysics.makeParticle(mouseX, mouseY, 0);

            /* define a radius for the particle so it has dimensions */
            mParticle.radius(random(PARTICLE_RADIUS / 2) + PARTICLE_RADIUS);
        }


        /* move overlapping particles away from each other */
        for (int i = 0; i < 10; i++) {
            Overlap.resolveOverlap(mPhysics.particles());
        }

        /* update the particle system */
        mPhysics.step(1.0f / frameRate);

        /* draw particles and connecting line */
        background(255);

        /* constraint particles */
        for (int i = 0; i < mPhysics.particles().size(); i++) {
            if (mPhysics.particles(i).position().y > height - 10) {
                mPhysics.particles(i).position().y = height - 10;
            }
            if (mPhysics.particles(i).position().x > width) {
                mPhysics.particles(i).position().x = width;
            }
            if (mPhysics.particles(i).position().x < 0) {
                mPhysics.particles(i).position().x = 0;
            }
        }

        /* draw particles */
        fill(255);
        for (int i = 0; i < mPhysics.particles().size(); i++) {
            Particle p = mPhysics.particles().get(i);
            stroke(0, 191);
            ellipse(p.position().x, p.position().y, p.radius() * 2, p.radius() * 2);
        }
    }

    public static void main(String[] args) {
        PApplet.main(TestPilingUp.class.getName());
    }
}
