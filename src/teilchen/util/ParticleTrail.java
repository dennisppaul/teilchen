/*
 * Teilchen
 *
 * This file is part of the *teilchen* library (https://github.com/dennisppaul/teilchen).
 * Copyright (c) 2020 Dennis P Paul.
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

import teilchen.Particle;
import teilchen.Physics;
import teilchen.ShortLivedParticle;

import java.util.ArrayList;

public class ParticleTrail {

    public ParticleTrail(final Physics pTrailParticleSystem,
                         final Particle pParticle,
                         float pInterval,
                         float pFragmentLifetime) {
        mTrailParticleSystem = pTrailParticleSystem;
        mParticle = pParticle;
        mInterval = pInterval;
        mFragmentLifetime = pFragmentLifetime;
        mFragments = new ArrayList<>();
        mFixState = false;
    }

    private static <T extends Particle> T createParticle(Class<T> pParticleClass) {
        T mParticle;
        try {
            mParticle = pParticleClass.newInstance();
        } catch (Exception ex) {
            System.err.println(ex);
            mParticle = null;
        }
        return mParticle;
    }

    public void mass(float pMass) {
        mTrailParticleMass = pMass;
    }

    public float mass() {
        return mTrailParticleMass;
    }

    public void fix(boolean pFixState) {
        mFixState = pFixState;
    }

    public Particle particle() {
        return mParticle;
    }

    public void clear() {
        mFragments.clear();
    }

    public ArrayList<Particle> fragments() {
        return mFragments;
    }

    public void set() {
        /* this would be more precise but has other issues -- mCurrentTime -= mInterval; */
        mCurrentTime = 0;
        final Particle mParticle = makeParticle();
        mFragments.add(mParticle);
    }

    public void loop(float pDeltaTime) {
        mCurrentTime += pDeltaTime;

        if (mCurrentTime > mInterval) {
            set();
        }

        for (int i = 0; i < mFragments.size(); i++) {
            Particle mTrailFragment = mFragments.get(i);
            if (mTrailFragment.dead()) {
                mFragments.remove(mTrailFragment);
            }
        }
    }

    public <T extends Particle> void setParticleClass(Class<T> pClass) {
        mParticleClass = pClass;
    }

    protected Particle makeParticle() {
        final Particle mTrailFragment = createParticle(mParticleClass);
        mTrailFragment.mass(mTrailParticleMass);

        if (mTrailParticleSystem != null) {
            mTrailParticleSystem.add(mTrailFragment);
        }

        if (mTrailFragment instanceof ShortLivedParticle) {
            ((ShortLivedParticle) mTrailFragment).setMaxAge(mFragmentLifetime);
        }

        mTrailFragment.position().set(mParticle.position());
        mTrailFragment.fixed(mFixState);
        return mTrailFragment;
    }
    private final Physics mTrailParticleSystem;
    private final Particle mParticle;
    private final float mInterval;
    private final ArrayList<Particle> mFragments;
    private final float mFragmentLifetime;
    private float mCurrentTime;
    private boolean mFixState;
    private Class<? extends Particle> mParticleClass = ShortLivedParticle.class;
    private float mTrailParticleMass = 1.0f;
}
