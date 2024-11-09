package teilchen.test;

import processing.core.PApplet;
import processing.core.PVector;
import teilchen.util.Util;

import static teilchen.util.Util.point_in_triangle;

public class TestCircumcenterTriangle extends PApplet {

    private final PVector a = new PVector();
    private final PVector b = new PVector();
    private final PVector c = new PVector();

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        a.set(100, 100);
        b.set(200, 100);
        c.set(150, 200);
    }

    public void draw() {
        a.set(mouseX, mouseY);

        background(255);
        stroke(0);
        triangle(a.x, a.y, b.x, b.y, c.x, c.y);

        PVector cct = new PVector();
        float mRadius = Util.circumcenter_triangle(a, b, c, cct);

        noFill();
        if (point_in_triangle(a, b, c, cct)) {
            stroke(0, 255, 0);
        } else {
            stroke(255, 0, 0);
        }
        circle(cct.x, cct.y, mRadius * 2);

        stroke(0);
        circle(cct.x, cct.y, 3);
    }

    public static void main(String[] args) {
        PApplet.main(TestCircumcenterTriangle.class.getName());
    }
}