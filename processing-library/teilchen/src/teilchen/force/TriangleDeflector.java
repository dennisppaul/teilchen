/*
 * Teilchen
 *
 * This file is part of the *teilchen* library (https://github.com/dennisppaul/teilchen).
 * Copyright (c) 2024 Dennis P Paul.
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

import static processing.core.PApplet.abs;
import static processing.core.PVector.add;
import static processing.core.PVector.mult;
import static processing.core.PVector.sub;
import static teilchen.util.Intersection.intersect_line_segment_plane;
import static teilchen.util.Util.calculateNormal;
import static teilchen.util.Util.distance_point_plane;
import static teilchen.util.Util.isNaN;
import static teilchen.util.Util.lengthSquared;
import static teilchen.util.Util.point_in_triangle;
import static teilchen.util.Util.project_point_onto_plane;
import static teilchen.util.Util.project_vector_onto_plane;
import static teilchen.util.Util.reflect;

public class TriangleDeflector implements Force {
    public boolean AUTO_UPDATE = true;
    private final PVector a;
    private final PVector b;
    private final PVector c;
    private final long fID;
    private final PVector fNormal;
    private boolean fActive;
    private float fCoefficientOfRestitution;
    private boolean fDead;
    private boolean fGotHit;

    public TriangleDeflector() {
        fID = Physics.getUniqueID();
        a = new PVector();
        b = new PVector();
        c = new PVector();

        fNormal = new PVector();
        fCoefficientOfRestitution = 1.0f;

        fActive = true;
        fDead = false;
        fGotHit = false;
    }

    public PVector a() {
        return a;
    }

    public PVector b() {
        return b;
    }

    public PVector c() {
        return c;
    }

    public void updateProperties() {
        calculateNormal(a, b, c, fNormal);
    }

    private boolean calculateIntersection(Particle mParticle, float delta_time) {
        if (!mParticle.fixed()) {
            if (isNaN(mParticle.velocity())) {
                return false;
            }
            if (lengthSquared(mParticle.velocity()) == 0) {
                return false;
            }

            /* proximity */
            final PVector mPrevPosition = add(mParticle.position(), mult(mParticle.velocity(), -delta_time));
            final float mDistanceToPlane = abs(distance_point_plane(mPrevPosition, a, fNormal));
            final float mDistanceEpsilon = 0.2f;
            PVector mIntersection = intersect_line_segment_plane(mParticle.position(), mPrevPosition, a, fNormal);
            if (point_in_triangle(a, b, c, mParticle.position()) && mDistanceToPlane < mDistanceEpsilon) {
                keepParticleOnPlane(mParticle, mPrevPosition);
                return true;
            }

            /* parallel */
            final float mAngle = abs(new PVector().set(mParticle.velocity()).normalize().dot(fNormal));
            final float mAngleEpsilon = 0.001f;
            if (mAngle < mAngleEpsilon && mDistanceToPlane < mDistanceEpsilon) {
                keepParticleOnPlane(mParticle, mPrevPosition);
                return true;
            }

            if (mIntersection != null && point_in_triangle(a, b, c, mIntersection)) {
                final PVector mSegmentCollisionPastPlane = sub(mParticle.position(), mIntersection);
                final PVector mPositionReflectionPast = reflect(mSegmentCollisionPastPlane, fNormal, false);
                mParticle.position().set(mIntersection);
                mParticle.position().add(mPositionReflectionPast);

                final PVector mSegmentCollisionBeforePlane = sub(mIntersection, mPrevPosition);
                final PVector mPositionReflectionBefore = reflect(mSegmentCollisionBeforePlane, fNormal, false);
                mParticle.old_position().set(mIntersection);
                mParticle.old_position().sub(mPositionReflectionBefore);

                final PVector mVelocityReflection = reflect(mParticle.velocity(), fNormal, false);
                mParticle.velocity().set(mVelocityReflection).mult(fCoefficientOfRestitution);

                mParticle.tag(true);
                return true;
            }
        }
        return false;
    }

    private void keepParticleOnPlane(Particle mParticle, PVector pPrevPosition) {
        mParticle.velocity().set(project_vector_onto_plane(mParticle.velocity(), fNormal));
        mParticle.position().set(project_point_onto_plane(mParticle.position(), a, fNormal));
        mParticle.old_position().set(pPrevPosition);
        mParticle.tag(true);
    }

    public void apply(final float pDeltaTime, final Physics pParticleSystem) {
        if (AUTO_UPDATE) {
            updateProperties();
        }

        fGotHit = false;
        for (final Particle mParticle : pParticleSystem.particles()) {
            if (!mParticle.fixed()) {
                boolean mDoesIntersect = calculateIntersection(mParticle, pDeltaTime);
                if (mDoesIntersect && !fGotHit) {
                    fGotHit = true;
                }
            }
        }
    }

    public boolean dead() {
        return fDead;
    }

    public void dead(boolean pDead) {
        fDead = pDead;
    }

    public boolean active() {
        return fActive;
    }

    public void active(boolean pActiveState) {
        fActive = pActiveState;
    }

    public long ID() {
        return fID;
    }

    public boolean hit() {
        return fGotHit;
    }

    public void coefficientofrestitution(float pCoefficientOfRestitution) {
        fCoefficientOfRestitution = pCoefficientOfRestitution;
    }

    public float coefficientofrestitution() {
        return fCoefficientOfRestitution;
    }

    public PVector normal() {
        return fNormal;
    }

    protected void markParticle(Particle pParticle) {
        pParticle.tag(true);
    }
}
