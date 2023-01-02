package teilchen.test;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PMatrix3D;
import processing.core.PVector;
import processing.opengl.PGraphics3D;

public class TestProjectUnprojectPoint extends PApplet {

    private static final boolean USE_P3D = true;

    private static void screen(PMatrix3D modelview,
                               PMatrix3D projection,
                               PVector pInput,
                               float pWidth,
                               float pHeight,
                               PVector pResult) {
        float ax = modelview.m00 * pInput.x + modelview.m01 * pInput.y + modelview.m02 * pInput.z + modelview.m03;
        float ay = modelview.m10 * pInput.x + modelview.m11 * pInput.y + modelview.m12 * pInput.z + modelview.m13;
        float az = modelview.m20 * pInput.x + modelview.m21 * pInput.y + modelview.m22 * pInput.z + modelview.m23;
        float aw = modelview.m30 * pInput.x + modelview.m31 * pInput.y + modelview.m32 * pInput.z + modelview.m33;
        screen(projection, ax, ay, az, aw, pWidth, pHeight, pResult);
    }

    private static void screen(PMatrix3D projection,
                               float x,
                               float y,
                               float z,
                               float w,
                               float pWidth,
                               float pHeight,
                               PVector pResult) {
        float ox = projection.m00 * x + projection.m01 * y + projection.m02 * z + projection.m03 * w;
        float oy = projection.m10 * x + projection.m11 * y + projection.m12 * z + projection.m13 * w;
        float oz = projection.m20 * x + projection.m21 * y + projection.m22 * z + projection.m23 * w;
        float ow = projection.m30 * x + projection.m31 * y + projection.m32 * z + projection.m33 * w;
        if (ow != 0) {
            ox /= ow;
            oy /= ow;
            oz /= ow;
        }
        pResult.x = pWidth * (1 + ox) / 2.0f;
        pResult.y = pHeight * (1 + oy) / 2.0f;
        pResult.y = pHeight - pResult.y; // Turning value upside down because of Processing's inverted Y axis.
        pResult.z = (oz + 1) / 2.0f;
    }

    private static void setup_frustum(PMatrix3D mProjection,
                                      float left,
                                      float right,
                                      float bottom,
                                      float top,
                                      float znear,
                                      float zfar) {
        float n2 = 2 * znear;
        float w = right - left;
        float h = top - bottom;
        float d = zfar - znear;

        mProjection.set(n2 / w,
                        0,
                        (right + left) / w,
                        0,
                        0,
                        -n2 / h,
                        (top + bottom) / h,
                        0,
                        0,
                        0,
                        -(zfar + znear) / d,
                        -(n2 * zfar) / d,
                        0,
                        0,
                        -1,
                        0);
    }

    private static void setup_matrices(PMatrix3D mModelview, PMatrix3D mProjection, float pWidth, float pHeight) {
        float defCameraFOV = 60 * (PConstants.PI / 180.0f); // at least for now
        float defCameraX = pWidth / 2.0f;
        float defCameraY = pHeight / 2.0f;
        float defCameraZ = defCameraY / ((float) Math.tan(defCameraFOV / 2.0f));
        float defCameraNear = defCameraZ / 10.0f;
        float defCameraFar = defCameraZ * 10.0f;
        float defCameraAspect = (float) pWidth / (float) pHeight;
        setup_projection(mProjection, defCameraFOV, defCameraAspect, defCameraNear, defCameraFar);
        setup_modelview(mModelview, defCameraX, defCameraY, defCameraZ, defCameraX, defCameraY, 0, 0, 1, 0);
    }

    private static void setup_modelview(PMatrix3D mModelview,
                                        float eyeX,
                                        float eyeY,
                                        float eyeZ,
                                        float centerX,
                                        float centerY,
                                        float centerZ,
                                        float upX,
                                        float upY,
                                        float upZ) {

        // Calculating Z vector
        float z0 = eyeX - centerX;
        float z1 = eyeY - centerY;
        float z2 = eyeZ - centerZ;
        float eyeDist = PApplet.sqrt(z0 * z0 + z1 * z1 + z2 * z2);
        if (eyeDist != 0) {
            z0 /= eyeDist;
            z1 /= eyeDist;
            z2 /= eyeDist;
        }

        // Calculating Y vector
        float y0 = upX;
        float y1 = upY;
        float y2 = upZ;

        // Computing X vector as Y cross Z
        float x0 = y1 * z2 - y2 * z1;
        float x1 = -y0 * z2 + y2 * z0;
        float x2 = y0 * z1 - y1 * z0;

        // Recompute Y = Z cross X
        y0 = z1 * x2 - z2 * x1;
        y1 = -z0 * x2 + z2 * x0;
        y2 = z0 * x1 - z1 * x0;

        // Cross product gives area of parallelogram, which is < 1.0 for
        // non-perpendicular unit-length vectors; so normalize x, y here:
        float xmag = PApplet.sqrt(x0 * x0 + x1 * x1 + x2 * x2);
        if (xmag != 0) {
            x0 /= xmag;
            x1 /= xmag;
            x2 /= xmag;
        }

        float ymag = PApplet.sqrt(y0 * y0 + y1 * y1 + y2 * y2);
        if (ymag != 0) {
            y0 /= ymag;
            y1 /= ymag;
            y2 /= ymag;
        }

        mModelview.set(x0, x1, x2, 0, y0, y1, y2, 0, z0, z1, z2, 0, 0, 0, 0, 1);

        float tx = -eyeX;
        float ty = -eyeY;
        float tz = -eyeZ;
        mModelview.translate(tx, ty, tz);
    }

    private static void setup_projection(PMatrix3D mProjection, float fov, float aspect, float zNear, float zFar) {
        float ymax = zNear * (float) Math.tan(fov / 2);
        float ymin = -ymax;
        float xmin = ymin * aspect;
        float xmax = ymax * aspect;
        setup_frustum(mProjection, xmin, xmax, ymin, ymax, zNear, zFar);
    }

    public void settings() {
        if (USE_P3D) {
            size(640, 480, P3D);
        } else {
            size(640, 480);
        }
    }

    public void setup() {
    }

    public void draw() {
        background(255);

        PVector mInput = new PVector(mouseX, mouseY, sin(TWO_PI * frameCount / 30.0f) * 100.0f);
        PVector mResult = new PVector();

        if (USE_P3D) {
            PGraphics3D p3d = (PGraphics3D) g;
            screen(p3d.modelview, p3d.projection, mInput, width, height, mResult);

            fill(255, 127, 0);
            noStroke();
            pushMatrix();
            translate(mInput.x, mInput.y, mInput.z);
            circle(0, 0, 20);
            popMatrix();

            noFill();
            stroke(0, 127, 255);
            circle(mResult.x, mResult.y, 30);
        }

        /* use custom projection */
        PMatrix3D mModelview = new PMatrix3D();
        PMatrix3D mProjection = new PMatrix3D();
        setup_matrices(mModelview, mProjection, width, height);
        screen(mModelview, mProjection, mInput, width, height, mResult);

        noStroke();
        fill(0, 255, 127);
        circle(mInput.x, mInput.y, 10);

        noFill();
        stroke(0, 255, 127);
        circle(mResult.x, mResult.y, 40);
    }

    public void model(PMatrix3D modelview, PMatrix3D cameraInv, PVector pInput, PVector pResult) {
        float ax = modelview.m00 * pInput.x + modelview.m01 * pInput.y + modelview.m02 * pInput.z + modelview.m03;
        float ay = modelview.m10 * pInput.x + modelview.m11 * pInput.y + modelview.m12 * pInput.z + modelview.m13;
        float az = modelview.m20 * pInput.x + modelview.m21 * pInput.y + modelview.m22 * pInput.z + modelview.m23;
        float aw = modelview.m30 * pInput.x + modelview.m31 * pInput.y + modelview.m32 * pInput.z + modelview.m33;

        float ox = cameraInv.m00 * ax + cameraInv.m01 * ay + cameraInv.m02 * az + cameraInv.m03 * aw;
        float oy = cameraInv.m10 * ax + cameraInv.m11 * ay + cameraInv.m12 * az + cameraInv.m13 * aw;
        float oz = cameraInv.m20 * ax + cameraInv.m21 * ay + cameraInv.m22 * az + cameraInv.m23 * aw;
        float ow = cameraInv.m30 * ax + cameraInv.m31 * ay + cameraInv.m32 * az + cameraInv.m33 * aw;

        pResult.x = (ow != 0) ? ox / ow : ox;
        pResult.y = (ow != 0) ? oy / ow : oy;
        pResult.z = (ow != 0) ? oz / ow : oz;
    }

    public static void main(String[] args) {
        PApplet.main(TestProjectUnprojectPoint.class.getName());
    }
}
