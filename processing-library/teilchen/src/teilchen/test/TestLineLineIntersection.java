package teilchen.test;

import processing.core.PApplet;
import processing.core.PVector;
import teilchen.util.Intersection;

import static teilchen.util.Util.project_point_onto_line;
import static teilchen.util.Util.project_point_onto_line_segment;

public class TestLineLineIntersection extends PApplet {

    private final PVector p1 = new PVector();
    private final PVector p2 = new PVector();
    private final PVector p3 = new PVector();
    private final PVector p4 = new PVector();
    private final PVector pa = new PVector();
    private final PVector pb = new PVector();
    private final PVector pc = new PVector();
    private final PVector pp = new PVector();

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        p1.set(100, 100);
        p2.set(200, 200);
        p3.set(100, 200);
        p4.set(200, 100);
        pp.set(width / 2.0f, height / 2.0f);
    }

    public void draw() {
        background(255);

        stroke(0);
        line(p1.x, p1.y, p2.x, p2.y);
        line(p3.x, p3.y, p4.x, p4.y);

        Intersection.intersect_line_line(p1, p2, p3, p4, pa);
        Intersection.intersect_line_line(p1, p2, p3, p4, pb, pc);
        noStroke();
        fill(255, 0, 0);
        circle(pa.x, pa.y, 15);
        fill(0, 255, 0);
        circle(pb.x, pb.y, 10);
        fill(0, 0, 255);
        circle(pc.x, pc.y, 5);

        PVector mPP = project_point_onto_line_segment(pp, p1, p2);
        PVector mPPS = project_point_onto_line(pp, p1, p2);
        fill(127, 0, 255);
        circle(pp.x, pp.y, 15);
        fill(0, 127, 255);
        circle(mPP.x, mPP.y, 5);
        fill(255, 127, 0);
        circle(mPPS.x, mPPS.y, 10);
    }

    public void keyPressed() {
        switch (key) {
            case '1':
                p1.set(mouseX, mouseY);
                break;
            case '2':
                p2.set(mouseX, mouseY);
                break;
            case '3':
                p3.set(mouseX, mouseY);
                break;
            case '4':
                p4.set(mouseX, mouseY);
                break;
        }
    }

    public static void main(String[] args) {
        PApplet.main(TestLineLineIntersection.class.getName());
    }
}