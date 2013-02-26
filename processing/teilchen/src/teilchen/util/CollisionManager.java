/*
 * Teilchen
 *
 * Copyright (C) 2013
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


package teilchen.util;


import java.util.Vector;

import mathematik.Random;
import mathematik.Vector3f;

import teilchen.Particle;
import teilchen.Physics;
import teilchen.constraint.Stick;
import teilchen.cubicle.CubicleParticle;
import teilchen.cubicle.CubicleWorld;
import teilchen.cubicle.ICubicleEntity;
import teilchen.force.IForce;
import teilchen.force.Spring;


/**
 * this manager uses it s own particle system.
 * we could make it more integrated by using a shared physic world.
 * this would of course make everthing more slower.
 */
public class CollisionManager {

    public boolean HINT_IGNORE_STILL_OR_FIXED = false;

    private float _myCollisionSpringConstant;

    private float _myCollisionSpringDamping;

    private final Physics _myCollisionPhysics;

    private float mMinimumDistance;

    private Vector3f _myResolveSamePosition;

    public enum ResolverType {
//        COLLISION_STICK, COLLISION_SPRING,

        SPRING, STICK
    }

    private final Random _myRandom;

    private ResolverType _myResolverType;

    private float _myCollisionResolverIntervalCounter = 1;

    private float _myCollisionResolverInterval = 0;

    private int mDistanceMode = DISTANCE_MODE_FIXED;

    public static final int DISTANCE_MODE_RADIUS = 0;

    public static final int DISTANCE_MODE_FIXED = 1;

    public CollisionManager() {
        this(new Physics());
    }

    public CollisionManager(final Physics thePhysics) {
        _myCollisionPhysics = thePhysics;
        _myResolveSamePosition = new Vector3f(1, 1, 1);
        _myCollisionSpringConstant = 20.0f;
        _myCollisionSpringDamping = 1.0f;
        mMinimumDistance = 20;
        _myResolverType = ResolverType.SPRING;
        _myRandom = new Random();
    }

    public void distancemode(int theDistanceMode) {
        mDistanceMode = theDistanceMode;
    }

    public void setResolverType(ResolverType theResolverType) {
        _myResolverType = theResolverType;
    }

    public Vector3f resolveSamePosition() {
        return _myResolveSamePosition;
    }

    public void springDamping(float theSpringDamping) {
        _myCollisionSpringDamping = theSpringDamping;
    }

    public float springDamping() {
        return _myCollisionSpringDamping;
    }

    public void springConstant(float theSpringConstant) {
        _myCollisionSpringConstant = theSpringConstant;
    }

    public float springConstant() {
        return _myCollisionSpringConstant;
    }

    public void minimumDistance(float theMinimumDistance) {
        mMinimumDistance = theMinimumDistance;
    }

    public float minimumDistance() {
        return mMinimumDistance;
    }

    public Physics collision() {
        return _myCollisionPhysics;
    }

    public Vector<IForce> collision_forces() {
        return _myCollisionPhysics.forces();
    }

    public void loop(float theDeltaTime) {

//        /* collision resolver */
//        if (_myCollisionResolverIntervalCounter > _myCollisionResolverInterval) {
//            _myCollisionResolverIntervalCounter = 0;
////            createCollisionResolvers();
//        } else {
//            _myCollisionResolverIntervalCounter += theDeltaTime;
//        }

        /* physics */
        _myCollisionPhysics.step(theDeltaTime);

//        /* remove collision resolver */
//        removeCollisionResolver();
    }

    public void autoloop(float theDeltaTime) {
        /* collision resolver */
        if (_myCollisionResolverIntervalCounter > _myCollisionResolverInterval) {
            _myCollisionResolverIntervalCounter = 0;
//            _myCollisionResolverIntervalCounter -= _myCollisionResolverInterval;
//            _myCollisionResolverIntervalCounter %= _myCollisionResolverInterval;
            createCollisionResolvers();
        } else {
            _myCollisionResolverIntervalCounter += theDeltaTime;
        }

        /* physics */
        _myCollisionPhysics.step(theDeltaTime);

        /* remove collision resolver */
        removeCollisionResolver();
    }

    public void removeCollisionResolver() {
        _myCollisionPhysics.forces().clear();
        _myCollisionPhysics.constraints().clear();
    }

    public void createCollisionResolvers() {
        for (int i = 0; i < _myCollisionPhysics.particles().size(); i++) {
            createCollisionResolver(_myCollisionPhysics.particles().get(i), i);
        }
    }

    private void createCollisionResolver(final Particle theParticle, final int theStart) {
        if (HINT_IGNORE_STILL_OR_FIXED) {
            if (theParticle.fixed() || theParticle.still()) {
                return;
            }
        }
        for (int j = theStart; j < _myCollisionPhysics.particles().size(); j++) {
            Particle myOtherParticle = _myCollisionPhysics.particles().get(j);
            if (theParticle != myOtherParticle) { // && !myOtherParticle.fixed()) {
                final float myDistance = theParticle.position().distance(myOtherParticle.position());
                final float myMinimumDistance = getMinimumDistance(theParticle, myOtherParticle);
                if (myDistance < myMinimumDistance) {
                    if (theParticle.fixed() && myOtherParticle.fixed()) {
//                        continue;
                    }
                    /**
                     * because of the way we handle the collision resolver creation
                     * there is no need to check for multiple spring connections.
                     * checkSpringConnectionExistence(_myCollisionPhysics.getForces(),
                     *                                myParticle,
                     *                                myOtherParticle);
                     */
                    if (_myResolverType == ResolverType.SPRING) {
                        Spring mySpring = new Spring(theParticle,
                                                     myOtherParticle,
                                                     _myCollisionSpringConstant,
                                                     _myCollisionSpringDamping,
                                                     myMinimumDistance);
                        _myCollisionPhysics.add(mySpring);
                    } else if (_myResolverType == ResolverType.STICK) {
                        Stick mySpring = new Stick(theParticle,
                                                   myOtherParticle,
                                                   myMinimumDistance);
                        _myCollisionPhysics.add(mySpring);
                    }

                    /* hack to prevent particles from being in the same place */
                    if (myDistance < mathematik.Mathematik.EPSILON && myDistance > -mathematik.Mathematik.EPSILON) {
                        myOtherParticle.position().x += _myRandom.getFloat(_myResolveSamePosition.x * -0.5f,
                                                                           _myResolveSamePosition.x * 0.5f);
                        myOtherParticle.position().y += _myRandom.getFloat(_myResolveSamePosition.y * -0.5f,
                                                                           _myResolveSamePosition.y * 0.5f);
                        myOtherParticle.position().z += _myRandom.getFloat(_myResolveSamePosition.z * -0.5f,
                                                                           _myResolveSamePosition.z * 0.5f);
                    }
                }
            }
        }
    }

    public void createCollisionResolvers(final CubicleWorld theWorld) {
        for (int i = 0; i < _myCollisionPhysics.particles().size(); i++) {
            final Particle myParticle = _myCollisionPhysics.particles().get(i);
            if (myParticle instanceof CubicleParticle) {
                createCollisionResolver(theWorld, (CubicleParticle)myParticle);
            }
        }
    }

    private void createCollisionResolver(final CubicleWorld theWorld, final CubicleParticle theParticle) {
        if (HINT_IGNORE_STILL_OR_FIXED) {
            if (theParticle.fixed() || theParticle.still()) {
                return;
            }
        }

        final Vector<ICubicleEntity> myNeigbors = theWorld.getLocalEntities(theParticle);
        if (myNeigbors.size() > 1) {
            for (int j = 0; j < myNeigbors.size(); j++) {
                final ICubicleEntity myEntity = myNeigbors.get(j);
                if (myEntity instanceof Particle) {
                    final Particle myOtherParticle = (Particle)myEntity;
                    if (theParticle != myOtherParticle) {
                        final float myDistance = theParticle.position().distance(myOtherParticle.position());
                        final float myMinimumDistance = getMinimumDistance(theParticle, myOtherParticle);
                        if (myDistance < myMinimumDistance) {
                            if (theParticle.fixed() && myOtherParticle.fixed()) {
                                continue;
                            }
                            /**
                             * because of the way we handle the collision resolver creation
                             * there is no need to check for multiple spring connections.
                             * checkSpringConnectionExistence(_myCollisionPhysics.getForces(),
                             *                                myParticle,
                             *                                myOtherParticle);
                             */
                            if (_myResolverType == ResolverType.SPRING) {
                                Spring mySpring = new Spring(theParticle,
                                                             myOtherParticle,
                                                             _myCollisionSpringConstant,
                                                             _myCollisionSpringDamping,
                                                             myMinimumDistance);
                                _myCollisionPhysics.add(mySpring);
                            } else if (_myResolverType == ResolverType.STICK) {
                                Stick mySpring = new Stick(theParticle,
                                                           myOtherParticle,
                                                           myMinimumDistance);
                                _myCollisionPhysics.add(mySpring);
                            }

                            /* hack to prevent particles from being in the same place */
                            if (myDistance < mathematik.Mathematik.EPSILON
                                    && myDistance > -mathematik.Mathematik.EPSILON) {
                                myOtherParticle.position().x += _myRandom.getFloat(_myResolveSamePosition.x * -0.5f,
                                                                                   _myResolveSamePosition.x * 0.5f);
                                myOtherParticle.position().y += _myRandom.getFloat(_myResolveSamePosition.y * -0.5f,
                                                                                   _myResolveSamePosition.y * 0.5f);
                                myOtherParticle.position().z += _myRandom.getFloat(_myResolveSamePosition.z * -0.5f,
                                                                                   _myResolveSamePosition.z * 0.5f);
                            }
                        }
                    }
                }
            }
        }
    }

    private float getMinimumDistance(Particle theParticle, Particle myOtherParticle) {
        final float myMinimumDistance;

        if (mDistanceMode == DISTANCE_MODE_RADIUS) {
            myMinimumDistance = theParticle.radius() + myOtherParticle.radius();
        } else {
            myMinimumDistance = mMinimumDistance;
        }
        return myMinimumDistance;
    }

    public static class CollisionSpring
            extends Spring {

        public CollisionSpring(Particle theA, Particle theB) {
            super(theA,
                  theB,
                  2.0f, 0.1f,
                  theA.position().distance(theB.position()));
        }

        public CollisionSpring(Particle theA,
                               Particle theB,
                               final float theSpringConstant,
                               final float theSpringDamping) {
            super(theA,
                  theB,
                  theSpringConstant,
                  theSpringDamping,
                  theA.position().distance(theB.position()));
        }

        public CollisionSpring(final Particle theA,
                               final Particle theB,
                               final float theSpringConstant,
                               final float theSpringDamping,
                               final float theRestLength) {
            super(theA,
                  theB,
                  theSpringConstant,
                  theSpringDamping,
                  theRestLength);
        }

        public void apply(final float theDeltaTime, final Physics theParticleSystem) {
            if (!_myA.fixed() || !_myB.fixed()) {
                float a2bX = _myA.position().x - _myB.position().x;
                float a2bY = _myA.position().y - _myB.position().y;
                float a2bZ = _myA.position().z - _myB.position().z;
                final float myInversDistance = fastInverseSqrt(a2bX * a2bX + a2bY * a2bY + a2bZ * a2bZ);
                final float myDistance = 1.0F / myInversDistance;

                if (myDistance < _myRestLength) {
                    if (myDistance == 0.0F) {
                        a2bX = 0.0F;
                        a2bY = 0.0F;
                        a2bZ = 0.0F;
                    } else {
                        a2bX *= myInversDistance;
                        a2bY *= myInversDistance;
                        a2bZ *= myInversDistance;
                    }

                    final float mSpringForce = -(myDistance - _myRestLength) * _mySpringConstant;
                    final float Va2bX = _myA.velocity().x - _myB.velocity().x;
                    final float Va2bY = _myA.velocity().y - _myB.velocity().y;
                    final float Va2bZ = _myA.velocity().z - _myB.velocity().z;
                    final float mDampingForce = -_mySpringDamping * (a2bX * Va2bX + a2bY * Va2bY + a2bZ * Va2bZ);
                    final float r = mSpringForce + mDampingForce;
                    a2bX *= r;
                    a2bY *= r;
                    a2bZ *= r;
                    if (!_myA.fixed()) {
                        _myA.force().add(a2bX, a2bY, a2bZ);
                    }

                    if (!_myB.fixed()) {
                        _myB.force().add(-a2bX, -a2bY, -a2bZ);
                    }
                }
            }
        }
    }

    public static class CollisionStick
            extends Stick {

        public CollisionStick(Particle theA, Particle theB) {
            super(theA, theB);
        }

        public CollisionStick(final Particle theA,
                              final Particle theB,
                              final float theRestLength) {
            super(theA, theB, theRestLength);
        }

        public void apply(Physics theParticleSystem) {
            if (!_myA.fixed() || !_myB.fixed()) {
                _myTempDistanceVector.sub(_myA.position(), _myB.position());
                final float myDistanceSquared = _myTempDistanceVector.lengthSquared();
                if (myDistanceSquared > 0) {
                    if (myDistanceSquared < _myRestLength * _myRestLength) {
                        final float myDistance = (float)Math.sqrt(myDistanceSquared);
                        final float myDifference = _myRestLength - myDistance;
                        if (myDifference > EPSILON || myDifference < -EPSILON) {
                            if (!_myOneWay) {
                                final float myDifferenceScale = 0.5f * myDifference / myDistance;
                                _myTempVector.scale(myDifferenceScale, _myTempDistanceVector);
                                _myA.position().add(_myTempVector);
                                _myB.position().sub(_myTempVector);
                            } else {
                                final float myDifferenceScale = myDifference / myDistance;
                                _myTempVector.scale(myDifferenceScale, _myTempDistanceVector);
                                _myB.position().sub(_myTempVector);
                            }
                        }
                    }
                } else {
                    _myB.position().set(_myA.position());
                    _myB.position().x += _myRestLength;
                }
            }
        }
    }
}
