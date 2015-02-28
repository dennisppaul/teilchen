/*
 * Teilchen
 *
 * Copyright (C) 2015
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
package teilchen.integration;

import teilchen.Physics;

public interface IIntegrator {

    void step(final float theDeltaTime, final Physics theParticleSystem);
}
/**
 * from paul bourke http://astronomy.swin.edu.au/~pbourke/modelling/solver/
 */

/*
 //        A 1st order 1D DE solver.
 //        Assumes the function is not time dependent.
 //        Parameters
 //           h      - step size
 //           y0     - last value
 //           method - algorithm to use [0,5]
 //           fcn    - evaluate the derivative of the field

 double Solver1D(double h, double y0, int method, double ( * fcn) (double)) {
 double ynew;
 double k1, k2, k3, k4, k5, k6;

 switch (method) {
 case 0: // Euler method
 k1 = h * ( * fcn) (y0);
 ynew = y0 + k1;
 break;
 case 1: // Modified Euler
 k1 = h * ( * fcn) (y0);
 k2 = h * ( * fcn) (y0 + k1);
 ynew = y0 + (k1 + k2) / 2;
 break;
 case 2: // Heuns method
 k1 = h * ( * fcn) (y0);
 k2 = h * ( * fcn) (y0 + 2 * k1 / 3);
 ynew = y0 + k1 / 4 + 3 * k2 / 4;
 break;
 case 3: // Midpoint
 k1 = h * ( * fcn) (y0);
 k2 = h * ( * fcn) (y0 + k1 / 2);
 ynew = y0 + k2;
 break;
 case 4: // 4'th order Runge-kutta
 k1 = h * ( * fcn) (y0);
 k2 = h * ( * fcn) (y0 + k1 / 2);
 k3 = h * ( * fcn) (y0 + k2 / 2);
 k4 = h * ( * fcn) (y0 + k3);
 ynew = y0 + k1 / 6 + k2 / 3 + k3 / 3 + k4 / 6;
 break;
 case 5: // England 4'th order, six stage
 k1 = h * ( * fcn) (y0);
 k2 = h * ( * fcn) (y0 + k1 / 2);
 k3 = h * ( * fcn) (y0 + (k1 + k2) / 4);
 k4 = h * ( * fcn) (y0 - k2 + 2 * k3);
 k5 = h * ( * fcn) (y0 + (7 * k1 + 10 * k2 + k4) / 27);
 k6 = h * ( * fcn) (y0 + (28 * k1 - 125 * k2 + 546 * k3 + 54 * k4 - 378 * k5) / 625);
 ynew = y0 + k1 / 6 + 4 * k3 / 6 + k4 / 6;
 break;
 }

 return (ynew);
 }
 */

/* example C program */

/*
 int main(int argc, char * * argv) {
 double t;
 double dt = 0.1; // Step size
 double T = 100; // Simulation duration
 double y = 1; // Initial value

 for (t = 0; t < T; t += dt) {
 printf("%g %g\n", t, y);
 y = Solver1D(dt, y, MIDPOINT, (double ( * ) (double)) EvalFcn);
 }
 }

 // Sample derivative function
 double EvalFcn(double x) {
 return ( -0.05 * x);
 }
 */
