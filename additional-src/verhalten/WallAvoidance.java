/*
 * Verhalten
 *
 * Copyright (C) 2005 Patrick Kochlik + Dennis Paul
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


/*
 * 'avoid' steers away from an obstacle.
 */

/** @todo add some whiskers in arbitrary configuration. how? hmm. */


package verhalten;


import java.util.Vector;

import mathematik.Matrix3f;
import mathematik.Vector3f;


public class WallAvoidance
    implements IVerhaltenBehavior {

    private Vector3f _myDebugDirection;

    private Vector3f _myIntersection;

    private Vector<Whisker> _myWhisker;

    public WallAvoidance() {
        _myDebugDirection = new Vector3f();
        _myIntersection = new Vector3f();
        _myWhisker = new Vector<Whisker> ();
        /* create two test whiskers -- left and right*/
        _myWhisker.add(new Whisker(new Vector3f(100, 50, 1)));
        _myWhisker.add(new Whisker(new Vector3f(100, -50, 1)));
    }


    public boolean get(final Engine theEngine,
                       Vector<IVerhaltenWall> theWalls,
                       Vector3f theDirection) {
        theDirection.set(0, 0, 0);
        /* find closest intersection point with any wall */
        float myClosestSquaredIntersectionDistance = Float.MAX_VALUE;
        IVerhaltenWall myClosestWall = null;
        for (int i = 0; i < theWalls.size(); i++) {
            IVerhaltenWall myWall = theWalls.get(i);
            for (int j = 0; j < _myWhisker.size(); j++) {
                _myWhisker.get(j).update(theEngine.transform().rotation);
                float myIntersectionRatio = Util.intersectRayTriangle(theEngine.position(),
                                                                      _myWhisker.get(j).position,
                                                                      myWall.pointA(),
                                                                      myWall.pointB(),
                                                                      myWall.pointC(),
                                                                      _myIntersection,
                                                                      true);
                if (!Float.isNaN(myIntersectionRatio) &&
                    myIntersectionRatio >= 0.0f &&
                    myIntersectionRatio <= 1.0f) {
                    float mySquaredIntersectionDistance = _myIntersection.distanceSquared(theEngine.position());
                    if (mySquaredIntersectionDistance < myClosestSquaredIntersectionDistance) {
                        myClosestSquaredIntersectionDistance = mySquaredIntersectionDistance;
                        myClosestWall = myWall;
                    }
                }
            }
        }
        /* compute result */
        if (myClosestWall != null) {
            theDirection.set(myClosestWall.normal());
            theDirection.scale(100);
            _myDebugDirection.set(theDirection);
            return true;
        } else {
            theDirection.set(0, 0, 0);
            _myDebugDirection.set(theDirection);
            return false;
        }
    }


    public Vector<Whisker> getWhiskers() {
        return _myWhisker;
    }


    public Vector3f direction() {
        return _myDebugDirection;
    }


    public class Whisker {

        public Vector3f offset;

        public Vector3f position;

        private Vector3f myForward;

        private Vector3f mySide;

        private Vector3f myUp;

        public Whisker(Vector3f theOffset) {
            offset = theOffset;
            position = new Vector3f();
            myForward = new Vector3f();
            mySide = new Vector3f();
            myUp = new Vector3f();
        }


        void update(Matrix3f theTransform) {
            /* get whisker position from rotation matrix */
            theTransform.getXAxis(myForward);
            theTransform.getYAxis(mySide);
            theTransform.getZAxis(myUp);
            /* get new position */
            myForward.scale(offset.x);
            mySide.scale(offset.y);
            myUp.scale(offset.z);
            position.set(myForward);
            position.add(mySide);
            position.add(myUp);
        }
    }
}
