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
import teilchen.Particle;
import teilchen.Physics;
import teilchen.ShortLivedParticle;

public class ParticleTrail {

    private final Physics _myTrailParticleSystem;

    private final Particle _myParticle;

    private final float _myInterval;

    private final ArrayList<Particle> _myFragments;

    private final float _myFragmentLifetime;

    private float _myCurrentTime;

    private boolean _myFixState;

    private Class<? extends Particle> _myParticleClass = ShortLivedParticle.class;

    private float mTrailParticleMass = 1.0f;

    public ParticleTrail(final Physics theTrailParticleSystem,
                         final Particle theParticle,
                         float theInterval,
                         float theFragmentLifetime) {
        _myTrailParticleSystem = theTrailParticleSystem;
        _myParticle = theParticle;
        _myInterval = theInterval;
        _myFragmentLifetime = theFragmentLifetime;
        _myFragments = new ArrayList<>();
        _myFixState = false;
    }

    public void mass(float pMass) {
        mTrailParticleMass = pMass;
    }

    public float mass() {
        return mTrailParticleMass;
    }

    public void fix(boolean theFixState) {
        _myFixState = theFixState;
    }

    public Particle particle() {
        return _myParticle;
    }

    public void clear() {
        _myFragments.clear();
    }

    public ArrayList<Particle> fragments() {
        return _myFragments;
    }

    public void set() {
        /* this would be more precise but has other issues -- _myCurrentTime -= _myInterval; */
        _myCurrentTime = 0;
        final Particle myParticle = makeParticle();
        _myFragments.add(myParticle);
    }

    public void loop(float theDeltaTime) {
        _myCurrentTime += theDeltaTime;

        if (_myCurrentTime > _myInterval) {
            set();
        }

        for (int i = 0; i < _myFragments.size(); i++) {
            Particle myTrailFragment = _myFragments.get(i);
            if (myTrailFragment.dead()) {
                _myFragments.remove(myTrailFragment);
            }
        }
    }

    public <T extends Particle> void setParticleClass(Class<T> theClass) {
        _myParticleClass = theClass;
    }

    private static <T extends Particle> T createParticle(Class<T> theParticleClass) {
        T myParticle;
        try {
            myParticle = theParticleClass.newInstance();
        } catch (Exception ex) {
            System.err.println(ex);
            myParticle = null;
        }
        return myParticle;
    }

    protected Particle makeParticle() {
        final Particle myTrailFragment = createParticle(_myParticleClass);
        myTrailFragment.mass(mTrailParticleMass);

        if (_myTrailParticleSystem != null) {
            _myTrailParticleSystem.add(myTrailFragment);
        }

        if (myTrailFragment instanceof ShortLivedParticle) {
            ((ShortLivedParticle) myTrailFragment).setMaxAge(_myFragmentLifetime);
        }

        myTrailFragment.position().set(_myParticle.position());
        myTrailFragment.fixed(_myFixState);
        return myTrailFragment;
    }
}
