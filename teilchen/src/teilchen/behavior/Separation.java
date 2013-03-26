
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
package teilchen.behavior;


import mathematik.Vector3f;

import teilchen.IBehaviorParticle;
import teilchen.behavior.Util.ProximityStructure;
import java.io.Serializable;
import java.util.Vector;


public class Separation
        implements IBehavior,
                   Serializable {

    private static final long serialVersionUID = -4953599448151741585L;

    private float mProximity;

    private float mWeight;

    private final Vector3f mForce;

    private Vector<IBehaviorParticle> mNeighbors;

    public Separation() {
        mProximity = 100.0f;
        mWeight = 1.0f;
        mForce = new Vector3f();
    }

    public void update(float theDeltaTime, IBehaviorParticle pParent) {
        mForce.set(0, 0, 0);
        if (mNeighbors != null) {
            Vector<ProximityStructure> mCloseNeighbors = ProximityStructure.findProximityEntities(pParent, mNeighbors, mProximity);
            findAwayVector(mCloseNeighbors, mForce);
            mForce.scale(weight());
        }
    }

    private static void findAwayVector(Vector<ProximityStructure> mCloseNeighbors, final Vector3f pForce) {
        /* find away vector */
        if (!mCloseNeighbors.isEmpty()) {
            pForce.set(0, 0, 0);
            /**
             * @todo the vectors could be weighted according to distance: 1.0 -
             * distance ( for example )
             */
            for (ProximityStructure p : mCloseNeighbors) {
                pForce.add(p.distanceVec);
            }
            pForce.scale(1.0f / mCloseNeighbors.size());
            pForce.normalize();
            if (pForce.isNaN()) {
                pForce.set(0, 0, 0);
            }
        } else {
            pForce.set(0, 0, 0);
        }
    }

    public <E extends IBehaviorParticle> void neighbors(final Vector<E> pNeighbors) {
        /**
         * @todo well is this OK?
         */
        mNeighbors = (Vector<IBehaviorParticle>) pNeighbors;
    }

    public Vector3f force() {
        return mForce;
    }

    public float weight() {
        return mWeight;
    }

    public void weight(float pWeight) {
        mWeight = pWeight;
    }

    public float proximity() {
        return mProximity;
    }

    public void proximity(float thePrivacyRadius) {
        mProximity = thePrivacyRadius;
    }
}
