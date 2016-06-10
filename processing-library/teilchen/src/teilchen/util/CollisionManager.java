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
package teilchen.util;

import java.util.ArrayList;
import processing.core.PVector;
import teilchen.Particle;
import teilchen.Physics;
import static teilchen.Physics.EPSILON;
import teilchen.constraint.Stick;
import teilchen.cubicle.CubicleParticle;
import teilchen.cubicle.CubicleWorld;
import teilchen.cubicle.ICubicleEntity;
import teilchen.force.IForce;
import teilchen.force.Spring;
import static teilchen.util.Util.distance;
import static teilchen.util.Util.lengthSquared;

/**
 * this manager uses it s own particle system. we could make it more integrated
 * by using a shared physic world. this would of course make everthing more
 * slower.
 */
public class CollisionManager {

    public boolean HINT_IGNORE_STILL_OR_FIXED = false;

    private float mCollisionSpringConstant;

    private float mCollisionSpringDamping;

    private final Physics mCollisionPhysics;

    private float mMinimumDistance;

    private PVector mResolveSamePosition;

    public enum ResolverType {
//        COLLISION_STICK, COLLISION_SPRING,

        SPRING, STICK

    }
    private final Random mRandom;

    private ResolverType mResolverType;

    private float mCollisionResolverIntervalCounter = 1;

    private float mCollisionResolverInterval = 0;

    private int mDistanceMode = DISTANCE_MODE_FIXED;

    public static final int DISTANCE_MODE_RADIUS = 0;

    public static final int DISTANCE_MODE_FIXED = 1;

    public CollisionManager() {
        this(new Physics());
    }

    public CollisionManager(final Physics thePhysics) {
        mCollisionPhysics = thePhysics;
        mResolveSamePosition = new PVector(1, 1, 1);
        mCollisionSpringConstant = 20.0f;
        mCollisionSpringDamping = 1.0f;
        mMinimumDistance = 20;
        mResolverType = ResolverType.SPRING;
        mRandom = new Random();
    }

    public void distancemode(int theDistanceMode) {
        mDistanceMode = theDistanceMode;
    }

    public void setResolverType(ResolverType theResolverType) {
        mResolverType = theResolverType;
    }

    public PVector resolveSamePosition() {
        return mResolveSamePosition;
    }

    public void springDamping(float theSpringDamping) {
        mCollisionSpringDamping = theSpringDamping;
    }

    public float springDamping() {
        return mCollisionSpringDamping;
    }

    public void springConstant(float theSpringConstant) {
        mCollisionSpringConstant = theSpringConstant;
    }

    public float springConstant() {
        return mCollisionSpringConstant;
    }

    public void minimumDistance(float theMinimumDistance) {
        mMinimumDistance = theMinimumDistance;
    }

    public float minimumDistance() {
        return mMinimumDistance;
    }

    public Physics collision() {
        return mCollisionPhysics;
    }

    public ArrayList<IForce> collision_forces() {
        return mCollisionPhysics.forces();
    }

    public void loop(float theDeltaTime) {

//        /* collision resolver */
//        if (mCollisionResolverIntervalCounter > mCollisionResolverInterval) {
//            mCollisionResolverIntervalCounter = 0;
////            createCollisionResolvers();
//        } else {
//            mCollisionResolverIntervalCounter += theDeltaTime;
//        }

        /* physics */
        mCollisionPhysics.step(theDeltaTime);

//        /* remove collision resolver */
//        removeCollisionResolver();
    }

    public void autoloop(float theDeltaTime) {
        /* collision resolver */
        if (mCollisionResolverIntervalCounter > mCollisionResolverInterval) {
            mCollisionResolverIntervalCounter = 0;
//            mCollisionResolverIntervalCounter -= mCollisionResolverInterval;
//            mCollisionResolverIntervalCounter %= mCollisionResolverInterval;
            createCollisionResolvers();
        } else {
            mCollisionResolverIntervalCounter += theDeltaTime;
        }

        /* physics */
        mCollisionPhysics.step(theDeltaTime);

        /* remove collision resolver */
        removeCollisionResolver();
    }

    public void removeCollisionResolver() {
        mCollisionPhysics.forces().clear();
        mCollisionPhysics.constraints().clear();
    }

    public void createCollisionResolvers() {
        for (int i = 0; i < mCollisionPhysics.particles().size(); i++) {
            createCollisionResolver(mCollisionPhysics.particles().get(i), i);
        }
    }

    private void createCollisionResolver(final Particle theParticle, final int theStart) {
        if (HINT_IGNORE_STILL_OR_FIXED) {
            if (theParticle.fixed() || theParticle.still()) {
                return;
            }
        }
        for (int j = theStart; j < mCollisionPhysics.particles().size(); j++) {
            Particle myOtherParticle = mCollisionPhysics.particles().get(j);
            if (theParticle != myOtherParticle) { // && !myOtherParticle.fixed()) {
                final float myDistance = Util.distance(theParticle.position(), myOtherParticle.position());
                final float myMinimumDistance = getMinimumDistance(theParticle, myOtherParticle);
                if (myDistance < myMinimumDistance) {
                    if (theParticle.fixed() && myOtherParticle.fixed()) {
//                        continue;
                    }
                    /**
                     * because of the way we handle the collision resolver
                     * creation there is no need to check for multiple spring
                     * connections.
                     * checkSpringConnectionExistence(mCollisionPhysics.getForces(),
                     * myParticle, myOtherParticle);
                     */
                    if (mResolverType == ResolverType.SPRING) {
                        Spring mySpring = new Spring(theParticle,
                                                     myOtherParticle,
                                                     mCollisionSpringConstant,
                                                     mCollisionSpringDamping,
                                                     myMinimumDistance);
                        mCollisionPhysics.add(mySpring);
                    } else if (mResolverType == ResolverType.STICK) {
                        Stick mySpring = new Stick(theParticle,
                                                   myOtherParticle,
                                                   myMinimumDistance);
                        mCollisionPhysics.add(mySpring);
                    }

                    /* hack to prevent particles from being in the same place */
                    if (myDistance < EPSILON && myDistance > -EPSILON) {
                        myOtherParticle.position().x += mRandom.getFloat(mResolveSamePosition.x * -0.5f,
                                                                         mResolveSamePosition.x * 0.5f);
                        myOtherParticle.position().y += mRandom.getFloat(mResolveSamePosition.y * -0.5f,
                                                                         mResolveSamePosition.y * 0.5f);
                        myOtherParticle.position().z += mRandom.getFloat(mResolveSamePosition.z * -0.5f,
                                                                         mResolveSamePosition.z * 0.5f);
                    }
                }
            }
        }
    }

    public void createCollisionResolvers(final CubicleWorld theWorld) {
        for (int i = 0; i < mCollisionPhysics.particles().size(); i++) {
            final Particle myParticle = mCollisionPhysics.particles().get(i);
            if (myParticle instanceof CubicleParticle) {
                createCollisionResolver(theWorld, (CubicleParticle) myParticle);
            }
        }
    }

    private void createCollisionResolver(final CubicleWorld theWorld, final CubicleParticle theParticle) {
        if (HINT_IGNORE_STILL_OR_FIXED) {
            if (theParticle.fixed() || theParticle.still()) {
                return;
            }
        }

        final ArrayList<ICubicleEntity> myNeigbors = theWorld.getLocalEntities(theParticle);
        if (myNeigbors.size() > 1) {
            for (int j = 0; j < myNeigbors.size(); j++) {
                final ICubicleEntity myEntity = myNeigbors.get(j);
                if (myEntity instanceof Particle) {
                    final Particle myOtherParticle = (Particle) myEntity;
                    if (theParticle != myOtherParticle) {
                        final float myDistance = Util.distance(theParticle.position(), myOtherParticle.position());
                        final float myMinimumDistance = getMinimumDistance(theParticle, myOtherParticle);
                        if (myDistance < myMinimumDistance) {
                            if (theParticle.fixed() && myOtherParticle.fixed()) {
                                continue;
                            }
                            /**
                             * because of the way we handle the collision
                             * resolver creation there is no need to check for
                             * multiple spring connections.
                             * checkSpringConnectionExistence(mCollisionPhysics.getForces(),
                             * myParticle, myOtherParticle);
                             */
                            if (mResolverType == ResolverType.SPRING) {
                                Spring mySpring = new Spring(theParticle,
                                                             myOtherParticle,
                                                             mCollisionSpringConstant,
                                                             mCollisionSpringDamping,
                                                             myMinimumDistance);
                                mCollisionPhysics.add(mySpring);
                            } else if (mResolverType == ResolverType.STICK) {
                                Stick mySpring = new Stick(theParticle,
                                                           myOtherParticle,
                                                           myMinimumDistance);
                                mCollisionPhysics.add(mySpring);
                            }

                            /* hack to prevent particles from being in the same place */
                            if (myDistance < EPSILON && myDistance > -EPSILON) {
                                myOtherParticle.position().x += mRandom.getFloat(mResolveSamePosition.x * -0.5f,
                                                                                 mResolveSamePosition.x * 0.5f);
                                myOtherParticle.position().y += mRandom.getFloat(mResolveSamePosition.y * -0.5f,
                                                                                 mResolveSamePosition.y * 0.5f);
                                myOtherParticle.position().z += mRandom.getFloat(mResolveSamePosition.z * -0.5f,
                                                                                 mResolveSamePosition.z * 0.5f);
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
                  distance(theA.position(), theB.position()));
        }

        public CollisionSpring(Particle theA,
                               Particle theB,
                               final float theSpringConstant,
                               final float theSpringDamping) {
            super(theA,
                  theB,
                  theSpringConstant,
                  theSpringDamping,
                  distance(theA.position(), theB.position()));
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
            if (!mA.fixed() || !mB.fixed()) {
                float a2bX = mA.position().x - mB.position().x;
                float a2bY = mA.position().y - mB.position().y;
                float a2bZ = mA.position().z - mB.position().z;
                final float myInversDistance = fastInverseSqrt(a2bX * a2bX + a2bY * a2bY + a2bZ * a2bZ);
                final float myDistance = 1.0F / myInversDistance;

                if (myDistance < mRestLength) {
                    if (myDistance == 0.0F) {
                        a2bX = 0.0F;
                        a2bY = 0.0F;
                        a2bZ = 0.0F;
                    } else {
                        a2bX *= myInversDistance;
                        a2bY *= myInversDistance;
                        a2bZ *= myInversDistance;
                    }

                    final float mSpringForce = -(myDistance - mRestLength) * mSpringConstant;
                    final float Va2bX = mA.velocity().x - mB.velocity().x;
                    final float Va2bY = mA.velocity().y - mB.velocity().y;
                    final float Va2bZ = mA.velocity().z - mB.velocity().z;
                    final float mDampingForce = -mSpringDamping * (a2bX * Va2bX + a2bY * Va2bY + a2bZ * Va2bZ);
                    final float r = mSpringForce + mDampingForce;
                    a2bX *= r;
                    a2bY *= r;
                    a2bZ *= r;
                    if (!mA.fixed()) {
                        mA.force().add(a2bX, a2bY, a2bZ);
                    }

                    if (!mB.fixed()) {
                        mB.force().add(-a2bX, -a2bY, -a2bZ);
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
            if (!mA.fixed() || !mB.fixed()) {
                PVector.sub(mA.position(), mB.position(), mTempDistanceVector);
                final float myDistanceSquared = lengthSquared(mTempDistanceVector);
                if (myDistanceSquared > 0) {
                    if (myDistanceSquared < mRestLength * mRestLength) {
                        final float myDistance = (float) Math.sqrt(myDistanceSquared);
                        final float myDifference = mRestLength - myDistance;
                        if (myDifference > EPSILON || myDifference < -EPSILON) {
                            if (!mOneWay) {
                                final float myDifferenceScale = 0.5f * myDifference / myDistance;
                                PVector.mult(mTempDistanceVector, myDifferenceScale, mTempVector);
                                mA.position().add(mTempVector);
                                mB.position().sub(mTempVector);
                            } else {
                                final float myDifferenceScale = myDifference / myDistance;
                                PVector.mult(mTempDistanceVector, myDifferenceScale, mTempVector);
                                mB.position().sub(mTempVector);
                            }
                        }
                    }
                } else {
                    mB.position().set(mA.position());
                    mB.position().x += mRestLength;
                }
            }
        }
    }
}
