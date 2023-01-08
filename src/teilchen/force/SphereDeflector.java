/*
 * Teilchen
 *
 * This file is part of the *teilchen* library (https://github.com/dennisppaul/teilchen).
 * Copyright (c) 2023 Dennis P Paul.
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

package teilchen.force;

import processing.core.PVector;
import teilchen.Particle;
import teilchen.Physics;

import static teilchen.util.Util.reflect;

public class SphereDeflector implements IForce {

    private final PVector fPosition = new PVector();
    private float fRadius;
    private boolean fActive = true;
    private float fCoefficientOfRestitution = 1.0f;
    private boolean fDead = false;
    private final long fID;

    public SphereDeflector() {
        fID = Physics.getUniqueID();
    }

    public void calculateIntersection(Particle particle, float delta_time) {
        final PVector mDeflectorToParticle = PVector.sub(particle.position(), fPosition);
        final float mDistance = mDeflectorToParticle.mag();
        final float mMinimumDistance = fRadius + particle.radius();
        final float mOverlap = mDistance - mMinimumDistance;

        if (mOverlap < 0) {
            /* resolve collision */
            final PVector mNormal = PVector.mult(mDeflectorToParticle, 1.0f / mDistance);
            final PVector mPointOnCircle = PVector.mult(mNormal, mMinimumDistance).add(fPosition);
            final PVector mOverlapVector = PVector.sub(particle.position(), mPointOnCircle);
            particle.position().set(mPointOnCircle);
            if (mOverlap > -particle.radius()) {
                final PVector mPositionFraction = reflect(mOverlapVector, mNormal, false);
                particle.position().add(mPositionFraction);
            }
            /* reflect velocity */
            final PVector mReflection = reflect(particle.velocity(), mNormal, false);
            particle.velocity().set(mReflection).mult(fCoefficientOfRestitution);
            particle.tag(true);
        }
    }

    @Override
    public void apply(float pDeltaTime, Physics pParticleSystem) {
        for (final Particle mParticle : pParticleSystem.particles()) {
            if (!mParticle.fixed()) {
                calculateIntersection(mParticle, pDeltaTime);
            }
        }
    }

    @Override
    public boolean dead() {
        return fDead;
    }

    @Override
    public void dead(boolean pDead) {
        fDead = pDead;
    }

    @Override
    public boolean active() {
        return fActive;
    }

    @Override
    public void active(boolean pActiveState) {
        fActive = pActiveState;
    }

    public long ID() {
        return fID;
    }

    public void coefficientofrestitution(float pCoefficientOfRestitution) {
        fCoefficientOfRestitution = pCoefficientOfRestitution;
    }

    public PVector position() {
        return fPosition;
    }

    public float radius() {
        return fRadius;
    }

    public void radius(float radius) {
        fRadius = radius;
    }
}