package teilchen.force.flowfield;

import java.util.ArrayList;
import processing.core.PVector;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.force.IForce;
import teilchen.util.TransformMatrix4f;
import teilchen.util.Util;


/*
 * todo:
 * make density switchable
 * clean redundancies
 */
public class FlowField implements IForce {

    public int n = 100; // The size of the calculation grid

    private int _myGridSize = n + 2; // Extra grid space for boundary

    private final int _myPixelSize = 10; // The size of each grid square on the screen

    // I unravelled the 1D arrays from Jos Stam's paper back to 2D arrays, as we don't have compile time macros in Java...
    private float[][] u = new float[_myGridSize][_myGridSize];

    private float[][] v = new float[_myGridSize][_myGridSize];

    private float[][] uPrev = new float[_myGridSize][_myGridSize];

    private float[][] vPrev = new float[_myGridSize][_myGridSize];

    private float[][] dens = new float[_myGridSize][_myGridSize];

    private float[][] densPrev = new float[_myGridSize][_myGridSize];

    private final float viscosity = 0.0001f; // Viscosity of fluid

    public float deltatime = 0.2f; // Rate of change

    private final float diff = 0.0001f; // Degree of diffusion of density over time

    private final int densityBrushSize = n / 10; // Size of the density area applied with the mouse

    public int velocityBrushSize = n / 20; // Ditto velocity

    private final int lineSpacing = n / n; // Spacing between velocity and normal lines

    public boolean showHair = true;

    public boolean showVelocity = true;

    public boolean calculateDensity = true;

    public boolean calculateVelocity = true;

    private PVector _myScale;

    private TransformMatrix4f _myTransform;

    private ArrayList<FlowFieldForce> _myForces;

    public float forceScale = 1;

    public FlowField() {
        n = 100;
        init();
    }

    public FlowField(int theResolutionX, int theResolutionY, PVector theScale) {
        n = theResolutionX;
        init();
        _myScale.set(theScale);
    }

    private void init() {
        _myScale = new PVector();
        _myTransform = new TransformMatrix4f();
        _myForces = new ArrayList<FlowFieldForce>();
        _myGridSize = n + 2;
        u = new float[_myGridSize][_myGridSize];
        v = new float[_myGridSize][_myGridSize];
        uPrev = new float[_myGridSize][_myGridSize];
        vPrev = new float[_myGridSize][_myGridSize];
        dens = new float[_myGridSize][_myGridSize];
        densPrev = new float[_myGridSize][_myGridSize];
    }

    public void reset() {
        initVelocity();
        initDensity();
    }

    public void addForce(FlowFieldForce theForce) {
        _myForces.add(theForce);
    }

    private void updateForces() {
        for (FlowFieldForce myForce : _myForces) {
            myForce.applyForce(this);
        }
    }

    public ArrayList<FlowFieldForce> forces() {
        return _myForces;
    }

    public void setForce() {
        initField(densPrev);
        initField(uPrev);
        initField(vPrev);
    }

    public float[][] u() {
        return u;
    }

    public float[][] v() {
        return v;
    }

//float[][] field, int x, int y, float s, float r
    public void setForceArea(float[][] field, int x, int y, float s, float r) {
        for (int i = (int) (clamp(x - r, 1, n)); i <= (int) (clamp(x + r, 1, n)); i++) {
            int dx = x - i;
            for (int j = (int) (clamp(y - r, 1, n)); j <= (int) (clamp(y + r, 1, n)); j++) {
                int dy = y - j;
                float f = 1 - ((float) Math.sqrt(dx * dx + dy * dy) / r);
                field[i][j] += clamp(f, 0, 1) * s;
            }
        }
    }

    public void setForce(float theX, float theY, PVector theStrength, float theRadius) {
        int x = (int) (theX / _myScale.x * n);
        int y = (int) (theY / _myScale.y * n);
        for (int i = (int) (clamp(x - theRadius, 1, n)); i <= (int) (clamp(x + theRadius, 1, n)); i++) {
            int dx = x - i;
            for (int j = (int) (clamp(y - theRadius, 1, n)); j <= (int) (clamp(y + theRadius, 1, n)); j++) {
                int dy = y - j;
                float f = 1 - ((float) Math.sqrt(dx * dx + dy * dy) / theRadius);
                u[i][j] += clamp(f, 0, 1) * theStrength.x;
                v[i][j] += clamp(f, 0, 1) * theStrength.y;
            }
        }
    }

//public void calculateVelocity(float[][] u, float[][] v, float[][] u0, float[][] v0, float visc, float dt
    public void calculateVelocity() {

        addSource(u, uPrev, deltatime);
        addSource(v, vPrev, deltatime);

        float[][] tmp;
        tmp = u;
        u = uPrev;
        uPrev = tmp;
        tmp = v;
        v = vPrev;
        vPrev = tmp;

        diffuse(1, u, uPrev, viscosity, deltatime);
        diffuse(2, v, vPrev, viscosity, deltatime);

        project(u, v, uPrev, vPrev);

        tmp = u;
        u = uPrev;
        uPrev = tmp;
        tmp = v;
        v = vPrev;
        vPrev = tmp;

        advect(1, u, uPrev, uPrev, vPrev, deltatime);
        advect(2, v, vPrev, uPrev, vPrev, deltatime);

        project(u, v, uPrev, vPrev);
    }

//        public void calculateDensity(float[][] x, float[][] x0, float[][] u, float[][] v, float diff, float dt) {
    public void calculateDensity() {
        float[][] tmp;

        addSource(dens, densPrev, deltatime);
        tmp = dens;
        dens = densPrev;
        densPrev = tmp;
        diffuse(0, dens, densPrev, diff, deltatime);
        tmp = dens;
        dens = densPrev;
        densPrev = tmp;
        advect(0, dens, densPrev, u, v, deltatime);
    }

    public void loop(final float theDeltaTime) {
        updateForces();
        setForce();
        deltatime = theDeltaTime;

        if (calculateVelocity) {
            calculateVelocity();
        }
        if (calculateDensity) {
            calculateDensity();
        }
    }

//    public void draw(GLContext theRenderContext) {
//        GL gl = ( theRenderContext).gl;
//        float vu;
//        float vv;
//
//        gl.glPushMatrix();
//        JoglUtil.applyTransform(gl,
//                _myTransformMode,
//                transform,
//                rotation,
//                scale);
//
//        material.begin(theRenderContext);
//
//        for (int y = 1; y <= n; y++) {
//            for (int x = 1; x <= n; x++) {
//                if (showHair) {
//                    if ((x % lineSpacing) == 0 && (y % lineSpacing) == 0) {
//
//                        float myRatioX = x / (float) n;
//                        float myRatioY = y / (float) n;
//                        vu = u[x][y];
//                        vv = v[x][y];
//                        Vector2f myDirection = new Vector2f(vu, vv);
//                        float VIEW_VECTOR_LENGTH = myDirection.length();
//                        float myAlpha = material.color.a * VIEW_VECTOR_LENGTH;
//                        gl.glLineWidth(10);
//
//                        float myTargetLength = VIEW_VECTOR_LENGTH;
//
//                        gl.glBegin(GL.GL_LINES);
//                        gl.glColor4f(material.color.r, material.color.g, material.color.b, 0);
//                        gl.glVertex3f(myRatioX,
//                                myRatioY,
//                                0);
//
//                        myTargetLength = VIEW_VECTOR_LENGTH * 0.05f;
//                        gl.glColor4f(material.color.r, material.color.g, material.color.b, myAlpha);
//                        gl.glVertex3f(myRatioX + vu * myTargetLength,
//                                myRatioY + vv * myTargetLength,
//                                0);
//                        gl.glEnd();
//
//                        gl.glBegin(GL.GL_LINES);
//                        gl.glColor4f(material.color.r, material.color.g, material.color.b, myAlpha);
//                        gl.glVertex3f(myRatioX + vu * myTargetLength,
//                                myRatioY + vv * myTargetLength,
//                                0);
//
//                        myTargetLength = VIEW_VECTOR_LENGTH * 0.6f;
//                        gl.glColor4f(material.color.r, material.color.g, material.color.b, 0);
//                        gl.glVertex3f(myRatioX + vu * myTargetLength,
//                                myRatioY + vv * myTargetLength,
//                                0);
//                        gl.glEnd();
//                    }
//                }
//                if (showVelocity) {
//                    if ((x % lineSpacing) == 0 && (y % lineSpacing) == 0) {
//
//                        float myRatioX = x / (float) n;
//                        float myRatioY = y / (float) n;
//                        vu = u[x][y];
//                        vv = v[x][y];
//                        Vector2f myDirection = new Vector2f(vu, vv);
//                        myDirection.normalize();
//                        myDirection.scale(3);
//
//                        gl.glBegin(GL.GL_LINES);
//                        gl.glVertex3f(myRatioX,
//                                myRatioY,
//                                0);
//                        gl.glVertex3f(myRatioX + vu,
//                                myRatioY + vv,
//                                0);
//                        gl.glEnd();
//                    }
//                }
//            }
//        }
//        gl.glPopMatrix();
//        material.end(theRenderContext);
//    }
    public PVector scale() {
        return _myScale;
    }

    public TransformMatrix4f transform() {
        return _myTransform;
    }

    public PVector position() {
        return _myTransform.translation;
    }

    private float clamp(float f, float minf, float maxf) {
        return Math.max(Math.min(f, maxf), minf);
    }

    private void initField(float[][] f) {
        for (int i = 0; i < _myGridSize; i++) {
            for (int j = 0; j < _myGridSize; j++) {
                f[i][j] = 0.0f;
            }
        }
    }

    private void initVelocity() {
        initField(u);
        initField(v);
        initField(uPrev);
        initField(vPrev);
    }

    private void initDensity() {
        initField(dens);
        initField(densPrev);
    }

    public PVector getForce(PVector thePosition) {
        PVector myDeltaPos = Util.clone(thePosition);
        myDeltaPos.sub(position());
        float myRatioX = myDeltaPos.x / _myScale.x;
        float myRatioY = myDeltaPos.y / _myScale.y;
        if (myRatioX >= 0 && myRatioX <= 1 && myRatioY >= 0 && myRatioY <= 1) {
            int myIndexX = (int) (myRatioX * (u.length - 1));
            int myIndexY = (int) (myRatioY * (v.length - 1));
            if (u[myIndexX] == null || v[myIndexY] == null) {
                System.out.println(myIndexX + " " + myIndexY);
            }
            PVector myForce = new PVector(u()[myIndexX][myIndexY], v()[myIndexX][myIndexY], 0);
            return myForce;
        }
        return new PVector();
    }

    private void addSource(float[][] x, float[][] s, float dt) {
        for (int i = 0; i < _myGridSize; i++) {
            for (int j = 0; j < _myGridSize; j++) {
                x[i][j] += s[i][j] * dt;
            }
        }
    }

    private void setBnd(int b, float[][] x) {
//        for (int i = 1; i <= n; i++) {
//            if (b == 1) {
//                x[0][i] = -x[1][i];
//            } else {
//                x[0][i] = x[1][i];
//            }
//            if (b == 1) {
//                x[n + 1][i] = -x[n][i];
//            } else {
//                x[n + 1][i] = x[n][i];
//            }
//            if (b == 2) {
//                x[i][0] = -x[i][1];
//            } else {
//                x[i][0] = x[i][1];
//            }
//            if (b == 2) {
//                x[i][n + 1] = -x[i][n];
//            } else {
//                x[i][n + 1] = x[i][n];
//            }
//
//        }
//        x[0][0] = 0.5f * (x[1][0] + x[0][1]);
//        x[0][n + 1] = 0.5f * (x[1][n + 1] + x[0][n]);
//        x[n + 1][0] = 0.5f * (x[n][0] + x[n + 1][1]);
//        x[n + 1][n + 1] = 0.5f * (x[n][n + 1] + x[n + 1][n]);
    }

    private void diffuse(int b, float[][] x, float[][] x0, float diff, float dt) {

        int i, j, k;
        float a = dt * diff * n * n;

        /* todo: why is it '20' */
//        for (k = 0; k < 20; k++) {
        for (i = 1; i <= n; i++) {
            for (j = 1; j <= n; j++) {
                x[i][j] = (x0[i][j] + a * (x[i - 1][j] + x[i + 1][j] + x[i][j - 1] + x[i][j + 1])) / (1 + 4 * a);
            }
        }
        setBnd(b, x);
//        }
    }

    private void project(float[][] u, float[][] v, float[][] p, float[][] div) {
        int i;
        int j;
        int k;
        float h;

        h = 1.0f / n;
        /* todo check loop */
        for (i = 1; i <= n; i++) {
            for (j = 1; j <= n; j++) {
                div[i][j] = -0.5f * h * (u[i + 1][j] - u[i - 1][j] + v[i][j + 1] - v[i][j - 1]);
                p[i][j] = 0;
            }
        }
        setBnd(0, div);
        setBnd(0, p);

        /* todo: why is it '20' */
        for (k = 0; k < 20; k++) {
            for (i = 1; i <= n; i++) {
                for (j = 1; j <= n; j++) {
                    p[i][j] = (div[i][j] + p[i - 1][j] + p[i + 1][j] + p[i][j - 1] + p[i][j + 1]) / 4;
                }
            }
            setBnd(0, p);
        }

        /* todo check loop */
        for (i = 1; i <= n; i++) {
            for (j = 1; j <= n; j++) {
                u[i][j] -= 0.5f * (p[i + 1][j] - p[i - 1][j]) / h;
                v[i][j] -= 0.5f * (p[i][j + 1] - p[i][j - 1]) / h;
            }
        }
        setBnd(1, u);
        setBnd(2, v);
    }

    private void advect(int b, float[][] d, float[][] d0, float[][] u, float[][] v, float dt) {
        int i;
        int j;
        int i0;
        int j0;
        int i1;
        int j1;
        float x;
        float y;
        float s0;
        float t0;
        float s1;
        float t1;
        float dt0;

        dt0 = dt * n;
        /* todo check loop */
        for (i = 1; i <= n; i++) {
            for (j = 1; j <= n; j++) {
                x = i - dt0 * u[i][j];
                y = j - dt0 * v[i][j];

                x = Math.max(0.5f, x);
                x = Math.min(n + 0.5f, x);

                i0 = (int) Math.floor(x);
                i1 = i0 + 1;

                y = Math.max(0.5f, y);
                y = Math.min(n + 0.5f, y);

                j0 = (int) Math.floor(y);
                j1 = j0 + 1;

                s1 = x - i0;
                s0 = 1 - s1;
                t1 = y - j0;
                t0 = 1 - t1;

                d[i][j] = s0 * (t0 * d0[i0][j0] + t1 * d0[i0][j1])
                          + s1 * (t0 * d0[i1][j0] + t1 * d0[i1][j1]);
            }
        }
        setBnd(b, d);
    }

    public void apply(float theDeltaTime, Physics theParticleSystem) {
        for (Particle myParticle : theParticleSystem.particles()) {
            if (!myParticle.fixed()) {
                PVector myForce = getForce(myParticle.position());
                myForce.mult(forceScale);
                myParticle.force().add(myForce);
            }
        }
    }

    public boolean dead() {
        return false;
    }

    public boolean active() {
        return true;
    }

    public void active(boolean theActiveState) {
    }
}
