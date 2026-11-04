package teilchen.test;

import processing.core.PApplet;
import processing.core.PSurface;
import processing.core.PVector;
import teilchen.BasicParticle;
import teilchen.Physics;
import teilchen.force.Gravity;
import teilchen.force.ViscousDrag;
import teilchen.util.Overlap;

import javax.swing.JFrame;
import java.awt.Window;
import java.awt.geom.Ellipse2D;

public class TestPilingUp extends PApplet {

    private static final float   PARTICLE_RADIUS = 8;
    private              Physics mPhysics;

    public void settings() {
        size(640, 480);
    }

    public PSurface initSurface() {
        super.initSurface();
        JFrame frame = (JFrame) ((processing.awt.PSurfaceAWT.SmoothCanvas) getSurface().getNative()).getFrame();
        frame.setUndecorated(true);
        Window          window = (Window) frame;
        Ellipse2D.Float circle = new Ellipse2D.Float(0, 0, 480, 480);
        window.setShape(circle);
        window.setOpacity(0.7f); // Set opacity to 70%
        return surface;
    }

    public void setup() {
        mPhysics = new Physics();

        /* create drag */
        mPhysics.add(new ViscousDrag(0.8f));
        mPhysics.add(new Gravity(new PVector(0, 100f, 0)));
    }

    private static class ColorParticle extends BasicParticle {
        int fill_color;
    }

    public void draw() {
        if (mousePressed) {
            colorMode(HSB, 360, 100, 100);
            ColorParticle mParticle = new ColorParticle();
            mPhysics.add(mParticle);
            /* set color */
            mParticle.fill_color = color(random(360), 100, 100);
            /* set position */
            mParticle.position().set(mouseX, mouseY);
            /* define a radius for the particle so it has dimensions */
            mParticle.radius(random(PARTICLE_RADIUS / 2) + PARTICLE_RADIUS);
            colorMode(RGB, 255, 255, 255);
        }


        /* move overlapping particles away from each other */
        final int mIterations = 26;
        for (int i = 0; i < mIterations; i++) {
            float mDeltaTime = 1.0f / frameRate / mIterations;
            mPhysics.step(mDeltaTime);
            constraint_particles();
            Overlap.resolveOverlap(mPhysics.particles());
        }


        /* draw particles and connecting line */
        background(255);

        /* draw particles */
        fill(255);
        for (int i = 0; i < mPhysics.particles().size(); i++) {
            ColorParticle p = (ColorParticle) mPhysics.particles().get(i);
            noStroke();
            fill(p.fill_color);
            ellipse(p.position().x, p.position().y, p.radius() * 2, p.radius() * 2);
        }
    }

    private void constraint_particles() {
        for (int i = 0; i < mPhysics.particles().size(); i++) {
            if (mPhysics.particles(i).position().y > height - 10) {
                mPhysics.particles(i).position().y = height - 10;
                mPhysics.particles(i).velocity().y = 0f;
            }
            if (mPhysics.particles(i).position().x > width) {
                mPhysics.particles(i).position().x = width;
                mPhysics.particles(i).velocity().x = 0f;
            }
            if (mPhysics.particles(i).position().x < 0) {
                mPhysics.particles(i).position().x = 0;
                mPhysics.particles(i).velocity().x = 0f;
            }
        }
    }

    public static void main(String[] args) {
        PApplet.main(TestPilingUp.class.getName());
    }
}
