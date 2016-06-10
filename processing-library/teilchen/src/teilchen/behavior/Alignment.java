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
package teilchen.behavior;

import java.io.Serializable;
import java.util.ArrayList;
import processing.core.PVector;
import teilchen.IBehaviorParticle;
import teilchen.behavior.Util.ProximityStructure;
import static teilchen.util.Util.isNaN;

public class Alignment
        implements IBehavior,
                   Serializable {

    private static final long serialVersionUID = -4953599448151741585L;

    private float mProximity;

    private float mWeight;

    private final PVector mForce;

    private ArrayList<IBehaviorParticle> mNeighbors;

    public Alignment() {
        mProximity = 100.0f;
        mWeight = 1.0f;
        mForce = new PVector();
    }

    public void update(float theDeltaTime, IBehaviorParticle pParent) {
        mForce.set(0, 0, 0);
        if (mNeighbors != null) {
            ArrayList<ProximityStructure> mCloseNeighbors = ProximityStructure.findProximityEntities(pParent, mNeighbors, mProximity);
            findCommonVelocity(mCloseNeighbors, mForce);
            mForce.mult(weight());
        }
    }

    private static void findCommonVelocity(ArrayList<ProximityStructure> mCloseNeighbors, final PVector pForce) {
        /* find away vector */
        pForce.set(0, 0, 0);
        if (!mCloseNeighbors.isEmpty()) {
            /**
             * @todo the vectors could be weighted according to distance: 1.0 -
             * distance ( for example )
             */
            for (ProximityStructure p : mCloseNeighbors) {
                pForce.add(p.particle.velocity());
            }
            pForce.mult(1.0f / mCloseNeighbors.size());
            pForce.normalize();
            if (isNaN(pForce)) {
                pForce.set(0, 0, 0);
            }
        }
    }

    public <E extends IBehaviorParticle> void neighbors(final ArrayList<E> pNeighbors) {
        /**
         * @todo well is this OK?
         */
        mNeighbors = (ArrayList<IBehaviorParticle>) pNeighbors;
    }

    public PVector force() {
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
