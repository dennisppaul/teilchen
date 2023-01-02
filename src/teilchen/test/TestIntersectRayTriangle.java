package teilchen.test;

import processing.core.PApplet;
import processing.core.PVector;

import static teilchen.util.Intersection.intersectRayTriangle;

public class TestIntersectRayTriangle extends PApplet {

    public void settings() {
        size(640, 480, P3D);
    }

    public void setup() {
    }

    public void draw() {
        background(50);

        translate(width / 2.0f, height / 2.0f);
        if (!mousePressed) {
            mRotationX = TWO_PI * mouseY / (float) height;
            mRotationY = TWO_PI * mouseX / (float) width;
        }
        rotateX(mRotationX);
        rotateY(mRotationY);

        PVector rayOrigin = new PVector(0, 0, 0);
        PVector rayVector = new PVector(mRayX, mRayY, -100);
        if (mousePressed) {
            mRayX = mouseX - width / 2.0f;
            mRayY = mouseY - height / 2.0f;
        }
        PVector v0 = new PVector(-100, -100, -200);
        PVector v1 = new PVector(100, -100, -200);
        PVector v2 = new PVector(0, 100, -200);
        PVector outIntersectionPoint = new PVector();
        boolean mSuccess = intersectRayTriangle(rayOrigin, rayVector, v0, v1, v2, outIntersectionPoint);

        noFill();
        stroke(255);
        drawTriangle(v0, v1, v2);
        stroke(255, 0, 0);
        drawRay(rayOrigin, rayVector);

        if (mSuccess) {
            noStroke();
            fill(0, 128, 255);
            pushMatrix();
            translate(outIntersectionPoint);
            sphere(20);
            popMatrix();
        }
    }

    private void drawRay(final PVector pRayOrigin, final PVector pRayDirection) {
        line(pRayOrigin, PVector.add(pRayOrigin, pRayDirection));
    }

    private void drawTriangle(final PVector v0, final PVector v1, final PVector v2) {
        beginShape(TRIANGLES);
        vertex(v0);
        vertex(v1);
        vertex(v2);
        endShape();
    }

    private void line(PVector v0, PVector v1) {
        beginShape(LINES);
        vertex(v0);
        vertex(v1);
        endShape();
    }

    private void translate(PVector v) {
        translate(v.x, v.y, v.z);
    }

    private void vertex(PVector v) {
        vertex(v.x, v.y, v.z);
    }

    public static void main(String[] args) {
        PApplet.main(TestIntersectRayTriangle.class.getName());
    }
    private float mRotationX = 0;
    private float mRotationY = 0;
    private float mRayX = 0;
    private float mRayY = 0;
}
