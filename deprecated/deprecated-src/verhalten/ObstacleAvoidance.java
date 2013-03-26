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

/** @todo get needs to accept Vectors as well as arrays */
/** @todo clean up code */
/** @todo seperate lateral and breeak force */


package verhalten;


import java.util.Vector;
import mathematik.TransformMatrix4f;
import mathematik.Vector3f;


public class ObstacleAvoidance
    implements IVerhaltenBehavior {

    private float MinDetectionBoxLength;

    private float MaxDouble;

    private float _myBoxLength;

    private Vector3f _myDebugDirection;

    private Vector<IVerhaltenEntity> _myCloseEntities;

    private Vector<Vector3f> _myCloseEntitiesLocalPositions;

    public ObstacleAvoidance() {
        MinDetectionBoxLength = 50;
        MaxDouble = Float.MAX_VALUE;
        _myDebugDirection = new Vector3f();
    }


    public Vector<Vector3f> getLocalObstaclePositions() {
        return _myCloseEntitiesLocalPositions;
    }


    public float getBoxLength() {
        return _myBoxLength;
    }


    private void processGet(final Engine theEngine,
                            Vector3f theDirection) {
        // this will keep track of the closest intersecting obstacle (CIB)
        IVerhaltenEntity ClosestIntersectingObstacle = null;

        // this will be used to track the distance to the CIB
        float DistToClosestIP = MaxDouble;

        // this will record the transformed local coordinates of the CIB
        Vector3f LocalPosOfClosestObstacle = null;

        /* find the closest obstacle */
        for (int i = 0; i < _myCloseEntities.size(); i++) {
            IVerhaltenEntity curOb = _myCloseEntities.get(i);
            // curOb.setTag(false);

            Vector3f LocalPos = new Vector3f(curOb.position());
            toLocalSpace(theEngine.transform(), LocalPos);

            /* store positions for debug view */
            _myCloseEntitiesLocalPositions.add(LocalPos);

            // if the local position has a negative x value then it must lay
            // behind the agent. (in which case it can be ignored)
            if (LocalPos.x >= 0) {
                // if the distance from the x axis to the object's position is
                // less
                // than its radius + half the width of the detection box then
                // there
                // is a potential intersection.
                float ExpandedRadius = curOb.getBoundingRadius() + theEngine.getBoundingRadius();

                if (Math.abs(LocalPos.y) < ExpandedRadius) {
                    // now to do a line/circle intersection test. The center of
                    // the
                    // circle is represented by (cX, cY). The intersection
                    // points are
                    // given by the formula x = cX +/-sqrt(r^2-cY^2) for y=0.
                    // We only need to look at the smallest positive value of x
                    // because
                    // that will be the closest point of intersection.
                    float cX = LocalPos.x;
                    float cY = LocalPos.y;

                    // we only need to calculate the sqrt part of the above
                    // equation once
                    float SqrtPart = (float) Math.sqrt(ExpandedRadius * ExpandedRadius - cY * cY);

                    float ip = cX - SqrtPart;

                    if (ip <= 0.0) {
                        ip = cX + SqrtPart;
                    }

                    // test to see if this is the closest so far. If it is keep
                    // a
                    // record of the obstacle and its local coordinates
                    if (ip < DistToClosestIP) {
                        DistToClosestIP = ip;
                        ClosestIntersectingObstacle = curOb;
                        LocalPosOfClosestObstacle = LocalPos;
                    }
                }
            }
        }

        // if (ClosestIntersectingObstacle != null && LocalPosOfClosestObstacle
        // != null) {
        // ClosestIntersectingObstacle.setTag(true);
        // }

        // if we have found an intersecting obstacle, calculate a steering
        // force away from it
        theDirection.set(0, 0, 0);

        if (ClosestIntersectingObstacle != null && LocalPosOfClosestObstacle != null) {
            theDirection.sub(theEngine.position(), ClosestIntersectingObstacle.position());
            float myDistance = theDirection.length();
            theDirection.normalize();
            theDirection.scale(_myBoxLength - myDistance);
        }

        _myDebugDirection.set(theDirection);
    }


    public void get(final Engine theEngine,
                    Vector<IVerhaltenEntity> theObstacles,
                    Vector3f theDirection) {
        /* display obstacles for debug */
        _myCloseEntities = new Vector<IVerhaltenEntity> ();
        _myCloseEntitiesLocalPositions = new Vector<Vector3f> ();

        // the detection box length is proportional to the agent's velocity
        _myBoxLength = MinDetectionBoxLength + (theEngine.getSpeed() / theEngine.getMaximumSpeed())
                       * MinDetectionBoxLength;

        // tag all obstacles within range of the box for processing
        for (int i = 0; i < theObstacles.size(); i++) {
            if (theObstacles.get(i).position().distance(theEngine.position()) < _myBoxLength) {
                _myCloseEntities.add(theObstacles.get(i));
            }
        }
        processGet(theEngine, theDirection);
    }


    public void get(final Engine theEngine,
                    IVerhaltenEntity[] theObstacles,
                    Vector3f theDirection) {

        /* display obstacles for debug */
        _myCloseEntities = new Vector<IVerhaltenEntity> ();
        _myCloseEntitiesLocalPositions = new Vector<Vector3f> ();

        // the detection box length is proportional to the agent's velocity
        _myBoxLength = MinDetectionBoxLength + (theEngine.getSpeed() / theEngine.getMaximumSpeed())
                       * MinDetectionBoxLength;

        // tag all obstacles within range of the box for processing
        for (int i = 0; i < theObstacles.length; i++) {
            if (theObstacles[i].position().distance(theEngine.position()) < _myBoxLength) {
                _myCloseEntities.add(theObstacles[i]);
            }
        }
        processGet(theEngine, theDirection);
    }


    private void toLocalSpace(TransformMatrix4f theTransform,
                              Vector3f thePoint) {
        thePoint.sub(theTransform.translation);
        theTransform.rotation.transform(thePoint);
    }


    // private void toWorldSpace(TransformMatrix4f theTransform, Vector3f
    // thePoint) {
    // Matrix3f myInvers = new Matrix3f(theTransform.rotation);
    // myInvers.transpose();
    // myInvers.transform(thePoint);
    // thePoint.add(theTransform.translation);
    // }

    public Vector3f direction() {
        return _myDebugDirection;
    }
}
