/*
 * Mathematik
 *
 * Copyright (C) 2009 Patrick Kochlik + Dennis Paul
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


package mathematik;


import java.util.Vector;

import processing.core.PApplet;


/**
 * b-spline source code from:
 * Tim Lambert
 * 'nice page on curves, splines etc.'
 * http://www.cse.unsw.edu.au/~lambert/splines/
 */


public abstract class BSpline {

    /* the basis function for a cubic B spline */
    private static float b(int i, float t) {
        switch (i) {
            case -2:
                return ( ( ( -t + 3) * t - 3) * t + 1) / 6;
            case -1:
                return ( ( (3 * t - 6) * t) * t + 4) / 6;
            case 0:
                return ( ( ( -3 * t + 3) * t + 3) * t + 1) / 6;
            case 1:
                return (t * t * t) / 6;
        }
        return 0; //we only get here if an invalid i is specified
    }


    /* evaluate a point on the B spline */
    private static Vector3f p(Vector<Vector3f> thePoints, int i, float t) {
        Vector3f p = new Vector3f();
        for (int j = -2; j <= 1; j++) {
            p.x += b(j, t) * thePoints.get(i + j).x;
            p.y += b(j, t) * thePoints.get(i + j).y;
            p.z += b(j, t) * thePoints.get(i + j).z;
        }
        return p;
    }


    public static Vector<Vector3f> curve(Vector<Vector3f> thePoints, int theSteps, Vector<Vector3f> theResult) {
        for (int i = 2; i < thePoints.size() - 1; i++) {
            for (int j = 1; j <= theSteps; j++) {
                theResult.add(p(thePoints, i, j / (float) theSteps));
            }
        }
        return theResult;
    }


    public static Vector<Vector3f> curve(Vector<Vector3f> thePoints, int theSteps) {
        return curve(thePoints, theSteps, new Vector<Vector3f> ());
    }


    public static Vector<Vector3f> closeCurve(Vector<Vector3f> thePoints) {
        /* copy points */
        Vector<Vector3f> myClosedPoints = new Vector<Vector3f> ();
        for (int i = 0; i < thePoints.size(); i++) {
            myClosedPoints.add(thePoints.get(i));
        }

        /* repeat first three points */
        if (thePoints.size() > 2) {
            myClosedPoints.add(thePoints.get(0));
            myClosedPoints.add(thePoints.get(1));
            myClosedPoints.add(thePoints.get(2));
        }
        return myClosedPoints;
    }


    public static class SketchTestBSpline
        extends PApplet {

        private Vector<Vector3f> _myPoints = new Vector<Vector3f> ();

        public void setup() {
            size(640, 480, OPENGL);
            smooth();
        }


        public void keyPressed() {}


        public void mousePressed() {
            _myPoints.add(new Vector3f(mouseX, mouseY));
        }


        public void draw() {
            background(255);
            if (!_myPoints.isEmpty()) {
                /* control points */
                stroke(255, 0, 0);
                for (int i = 0; i < _myPoints.size(); i++) {
                    cross(_myPoints.get(i), 10);
                }

                /* b-spline */
                Vector<Vector3f> myClosedPoints = BSpline.closeCurve(_myPoints);
                Vector<Vector3f> myResult = BSpline.curve(myClosedPoints, 20);

                /* closed */
                stroke(0, 0, 255);
                for (int i = 0; i < myResult.size(); i++) {
                    final int myNextID = (i + 1) % myResult.size();
                    line(myResult.get(i).x, myResult.get(i).y, myResult.get(i).z,
                         myResult.get(myNextID).x, myResult.get(myNextID).y, myResult.get(myNextID).z);
                }
            }
        }


        private void cross(Vector3f p, float theSize) {
            line(p.x - theSize / 2, p.y, p.z,
                 p.x + theSize / 2, p.y, p.z);
            line(p.x, p.y - theSize / 2, p.z,
                 p.x, p.y + theSize / 2, p.z);
        }
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {SketchTestBSpline.class.getName()});
    }
}
