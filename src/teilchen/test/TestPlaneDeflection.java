package teilchen.test;

import processing.core.PApplet;
import processing.core.PVector;

public class TestPlaneDeflection extends PApplet {

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        textFont(createFont("Roboto Mono", 12));
    }

    public void draw() {
        background(255);

        PVector mNormal = new PVector(0, -50);
        PVector mRay = new PVector(mouseX - width / 2.0f, mouseY - height / 2.0f);

        noFill();
        stroke(0, 255, 0);
        line_to(new PVector(width / 2.0f, height / 2.0f), mNormal);
        stroke(255, 0, 0);
        line_to(new PVector(width / 2.0f, height / 2.0f), mRay);

        fill(0);
        float mDotProduct = PVector.dot(mNormal, mRay); /* no need to normalize */
        text(mDotProduct, 10, 22);
    }

    private void line_to(PVector p0, PVector p1) {
        line(p0.x, p0.y, p0.x + p1.x, p0.y + p1.y);
    }

    public static void main(String[] args) {
        PApplet.main(TestPlaneDeflection.class.getName());
    }
}