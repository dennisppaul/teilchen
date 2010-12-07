/*
 * Particles
 *
 * Copyright (C) 2010
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * {@link http://www.gnu.org/licenses/lgpl.html}
 *
 */


package teilchen.test.particle;


import java.util.Vector;

import processing.core.PApplet;


/**
 * source code from paul bourke ( http://astronomy.swin.edu.au/~pbourke/ )
 */

public class TestOtherIntegrators
    extends PApplet {
    /*
     * A 1st order 1D DE solver.
     * Assumes the function is not time dependent.
     * h      - step size
     * y0     - last value
     * method - algorithm to use
     */
    private float Solver1D(float theDeltaTime, float y, int theMethod, EvalFcn theFunc) {

        float ynew = 0;

        switch (theMethod) {
            case 0: { // Euler method
                float k1 = theDeltaTime * theFunc.eval(y);
                ynew = y + k1;
            }
            break;

            case 1: { // Modified Euler
                float k1, k2;
                k1 = theDeltaTime * theFunc.eval(y);
                k2 = theDeltaTime * theFunc.eval(y + k1);
                ynew = y + (k1 + k2) / 2;
            }
            break;
            case 2: { // Heuns method
                float k1, k2;
                k1 = theDeltaTime * theFunc.eval(y);
                k2 = theDeltaTime * theFunc.eval(y + 2 * k1 / 3);
                ynew = y + k1 / 4 + 3 * k2 / 4;
            }
            break;
            case 3: { // Midpoint
                float k1, k2;
                k1 = theDeltaTime * theFunc.eval(y);
                k2 = theDeltaTime * theFunc.eval(y + k1 / 2);
                ynew = y + k2;
            }
            break;
            case 4: { // 4'th order Runge-kutta
                float k1, k2, k3, k4;
                k1 = theDeltaTime * theFunc.eval(y);
                k2 = theDeltaTime * theFunc.eval(y + k1 / 2);
                k3 = theDeltaTime * theFunc.eval(y + k2 / 2);
                k4 = theDeltaTime * theFunc.eval(y + k3);
                ynew = y + k1 / 6 + k2 / 3 + k3 / 3 + k4 / 6;
            }
            break;
            case 5: { // England 4'th order, six stage
                float k1, k2, k3, k4; //, k5, k6;
                k1 = theDeltaTime * theFunc.eval(y);
                k2 = theDeltaTime * theFunc.eval(y + k1 / 2);
                k3 = theDeltaTime * theFunc.eval(y + (k1 + k2) / 4);
                k4 = theDeltaTime * theFunc.eval(y - k2 + 2 * k3);
//                k5 = theDeltaTime * theFunc.eval(y + (7 * k1 + 10 * k2 + k4) / 27);
//                k6 = theDeltaTime * theFunc.eval(y + (28 * k1 - 125 * k2 + 546 * k3 + 54 * k4 - 378 * k5) / 625);
                ynew = y + k1 / 6 + 4 * k3 / 6 + k4 / 6;
            }
            break;
        }

        return ynew;
    }


    private float t;

    float dt = 1 / 120f; // deltatime

    Vector<Particle1f> myParticle = new Vector<Particle1f> ();

    public void setup() {
        size(640, 480);
        background(255);
        myParticle.add(new Particle1f());
        myParticle.add(new Particle1f());
        myParticle.add(new Particle1f());
        myParticle.add(new Particle1f());
        myParticle.add(new Particle1f());
        myParticle.add(new Particle1f());
    }


    public void draw() {
        for (int i = 0; i < myParticle.size(); i++) {
            /* calculate values */
            float newVel = Solver1D(dt / myParticle.get(i).mass, myParticle.get(i).vy, i, new EvalVel());
            float newPos = Solver1D(dt, myParticle.get(i).y, i, new EvalPos(myParticle.get(i)));
            myParticle.get(i).vy = newVel;
            myParticle.get(i).y = newPos;
            /* draw */
            stroke(i * 30 + 50, 0, 0, 127);
            point(t * 7, myParticle.get(i).y + i);
        }
        t += dt;
    }


    private class Particle1f {
        float y = 100; // position

        float vy = 10; // velocity

        float mass = 1; // mass
    }


    private interface EvalFcn {
        float eval(float theValue);
    }


    private class EvalVel
        implements EvalFcn {
        public float eval(float theValue) {
            return 0.01f * (mouseX - width / 2); // constant value
        }
    }


    private class EvalPos
        implements EvalFcn {

        private final Particle1f _myParticle;

        EvalPos(Particle1f theParticle) {
            _myParticle = theParticle;
        }


        public float eval(float theValue) {
            return _myParticle.vy;
        }
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {TestOtherIntegrators.class.getName()});
    }
}

///*
//   Update the forces on each particle
// */
//void CalculateForces(
//    PARTICLE * p, int np,
//    PARTICLEPHYS phys,
//    PARTICLESPRING * s, int ns) {
//    int i, p1, p2;
//    XYZ down = {0.0, 0.0, -1.0};
//    XYZ zero = {0.0, 0.0, 0.0};
//    XYZ f;
//    double len, dx, dy, dz;
//
//    for (i = 0; i < np; i++) {
//        p[i].f = zero;
//        if (p[i].fixed) {
//            continue;
//        }
//
//        /* Gravitation */
//        p[i].f.x += phys.gravitational * p[i].m * down.x;
//        p[i].f.y += phys.gravitational * p[i].m * down.y;
//        p[i].f.z += phys.gravitational * p[i].m * down.z;
//
//        /* Viscous drag */
//        p[i].f.x -= phys.viscousdrag * p[i].v.x;
//        p[i].f.y -= phys.viscousdrag * p[i].v.y;
//        p[i].f.z -= phys.viscousdrag * p[i].v.z;
//    }
//
//    /* Handle the spring interaction */
//    for (i = 0; i < ns; i++) {
//        p1 = s[i].from;
//        p2 = s[i].to;
//        dx = p[p1].p.x - p[p2].p.x;
//        dy = p[p1].p.y - p[p2].p.y;
//        dz = p[p1].p.z - p[p2].p.z;
//        len = sqrt(dx * dx + dy * dy + dz * dz);
//        f.x = s[i].springconstant * (len - s[i].restlength);
//        f.x += s[i].dampingconstant * (p[p1].v.x - p[p2].v.x) * dx / len;
//        f.x *= -dx / len;
//        f.y = s[i].springconstant * (len - s[i].restlength);
//        f.y += s[i].dampingconstant * (p[p1].v.y - p[p2].v.y) * dy / len;
//        f.y *= -dy / len;
//        f.z = s[i].springconstant * (len - s[i].restlength);
//        f.z += s[i].dampingconstant * (p[p1].v.z - p[p2].v.z) * dz / len;
//        f.z *= -dz / len;
//        if (!p[p1].fixed) {
//            p[p1].f.x += f.x;
//            p[p1].f.y += f.y;
//            p[p1].f.z += f.z;
//        }
//        if (!p[p2].fixed) {
//            p[p2].f.x -= f.x;
//            p[p2].f.y -= f.y;
//            p[p2].f.z -= f.z;
//        }
//    }
//}

///*
//   Perform one step of the solver
// */
//void UpdateParticles(
//    PARTICLE * p, int np,
//    PARTICLEPHYS phys,
//    PARTICLESPRING * s, int ns,
//    double dt, int method) {
//    int i;
//    PARTICLE * ptmp;
//    PARTICLEDERIVATIVES * deriv;
//
//    deriv = (PARTICLEDERIVATIVES * ) malloc(np * sizeof(PARTICLEDERIVATIVES));
//
//    switch (method) {
//        case 0: /* Euler */
//            CalculateForces(p, np, phys, s, ns);
//            CalculateDerivatives(p, np, deriv);
//            for (i = 0; i < np; i++) {
//                p[i].p.x += deriv[i].dpdt.x * dt;
//                p[i].p.y += deriv[i].dpdt.y * dt;
//                p[i].p.z += deriv[i].dpdt.z * dt;
//                p[i].v.x += deriv[i].dvdt.x * dt;
//                p[i].v.y += deriv[i].dvdt.y * dt;
//                p[i].v.z += deriv[i].dvdt.z * dt;
//            }
//            break;
//        case 1: /* Midpoint */
//            CalculateForces(p, np, phys, s, ns);
//            CalculateDerivatives(p, np, deriv);
//            ptmp = (PARTICLE * ) malloc(np * sizeof(PARTICLE));
//            for (i = 0; i < np; i++) {
//                ptmp[i] = p[i];
//                ptmp[i].p.x += deriv[i].dpdt.x * dt / 2;
//                ptmp[i].p.y += deriv[i].dpdt.y * dt / 2;
//                ptmp[i].p.z += deriv[i].dpdt.z * dt / 2;
//                ptmp[i].p.x += deriv[i].dvdt.x * dt / 2;
//                ptmp[i].p.y += deriv[i].dvdt.y * dt / 2;
//                ptmp[i].p.z += deriv[i].dvdt.z * dt / 2;
//            }
//            CalculateForces(ptmp, np, phys, s, ns);
//            CalculateDerivatives(ptmp, np, deriv);
//            for (i = 0; i < np; i++) {
//                p[i].p.x += deriv[i].dpdt.x * dt;
//                p[i].p.y += deriv[i].dpdt.y * dt;
//                p[i].p.z += deriv[i].dpdt.z * dt;
//                p[i].v.x += deriv[i].dvdt.x * dt;
//                p[i].v.y += deriv[i].dvdt.y * dt;
//                p[i].v.z += deriv[i].dvdt.z * dt;
//            }
//            free(ptmp);
//            break;
//    }
//
//    free(deriv);
//}
//
///*
//   Calculate the derivatives
//   dp/dt = v
//   dv/dt = f / m
// */
//void CalculateDerivatives(
//    PARTICLE * p, int np,
//    PARTICLEDERIVATIVES * deriv) {
//    int i;
//
//    for (i = 0; i < np; i++) {
//        deriv[i].dpdt.x = p[i].v.x;
//        deriv[i].dpdt.y = p[i].v.y;
//        deriv[i].dpdt.z = p[i].v.z;
//        deriv[i].dvdt.x = p[i].f.x / p[i].m;
//        deriv[i].dvdt.y = p[i].f.y / p[i].m;
//        deriv[i].dvdt.z = p[i].f.z / p[i].m;
//    }
//}
//
///*
//   A 1st order 1D DE solver.
//   Assumes the function is not time dependent.
//   Parameters
//      h      - step size
//      y0     - last value
//      method - algorithm to use [0,5]
//      fcn    - evaluate the derivative of the field
// */
//double Solver1D(double h, double y0, int method, double ( * fcn) (double)) {
//    double ynew;
//    double k1, k2, k3, k4, k5, k6;
//
//    switch (method) {
//        case 0: /* Euler method */
//            k1 = h * ( * fcn) (y0);
//            ynew = y0 + k1;
//            break;
//        case 1: /* Modified Euler */
//            k1 = h * ( * fcn) (y0);
//            k2 = h * ( * fcn) (y0 + k1);
//            ynew = y0 + (k1 + k2) / 2;
//            break;
//        case 2: /* Heuns method */
//            k1 = h * ( * fcn) (y0);
//            k2 = h * ( * fcn) (y0 + 2 * k1 / 3);
//            ynew = y0 + k1 / 4 + 3 * k2 / 4;
//            break;
//        case 3: /* Midpoint */
//            k1 = h * ( * fcn) (y0);
//            k2 = h * ( * fcn) (y0 + k1 / 2);
//            ynew = y0 + k2;
//            break;
//        case 4: /* 4'th order Runge-kutta */
//            k1 = h * ( * fcn) (y0);
//            k2 = h * ( * fcn) (y0 + k1 / 2);
//            k3 = h * ( * fcn) (y0 + k2 / 2);
//            k4 = h * ( * fcn) (y0 + k3);
//            ynew = y0 + k1 / 6 + k2 / 3 + k3 / 3 + k4 / 6;
//            break;
//        case 5: /* England 4'th order, six stage */
//            k1 = h * ( * fcn) (y0);
//            k2 = h * ( * fcn) (y0 + k1 / 2);
//            k3 = h * ( * fcn) (y0 + (k1 + k2) / 4);
//            k4 = h * ( * fcn) (y0 - k2 + 2 * k3);
//            k5 = h * ( * fcn) (y0 + (7 * k1 + 10 * k2 + k4) / 27);
//            k6 = h * ( * fcn) (y0 + (28 * k1 - 125 * k2 + 546 * k3 + 54 * k4 - 378 * k5) / 625);
//            ynew = y0 + k1 / 6 + 4 * k3 / 6 + k4 / 6;
//            break;
//    }
//
//    return (ynew);
//}
