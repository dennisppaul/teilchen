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


package teilchen.force;


import teilchen.Particle;
import teilchen.Physics;


public class MuscleSpring
    extends Spring {

    private float _myAmplitude = 1;

    private float _myOffset = 0;

    private float _myFrequency = 1;

    private float _myInitialRestLength;

    private float _myCurrentTime;

    private boolean _myAutomaticContraction = true;

    public MuscleSpring(Particle theA, Particle theB) {
        super(theA, theB);
        _myInitialRestLength = _myRestLength;
    }


    public MuscleSpring(final Particle theA,
                        final Particle theB,
                        final float theSpringConstant,
                        final float theSpringDamping,
                        final float theRestLength) {
        super(theA,
              theB,
              theSpringConstant,
              theSpringDamping,
              theRestLength);
        _myInitialRestLength = _myRestLength;
    }


    public void setRestLengthByPosition() {
        _myInitialRestLength = _myA.position().distance(_myB.position());
    }


    public float restlength() {
        return _myInitialRestLength;
    }


    public void restlength(float theRestLength) {
        _myInitialRestLength = theRestLength;
    }


    public void frequency(final float theFrequency) {
        _myFrequency = theFrequency;
    }


    public float frequency() {
        return _myFrequency;
    }


    public void amplitude(final float theAmplitude) {
        _myAmplitude = theAmplitude;
    }


    public float amplitude() {
        return _myAmplitude;
    }


    /**
     * set the offset of the contraction in radians.
     * @param theOffset float
     */
    public void offset(final float theOffset) {
        _myOffset = theOffset;
    }


    public float offset() {
        return _myOffset;
    }


    public void apply(final float theDeltaTime, final Physics theParticleSystem) {

        if (_myAutomaticContraction) {
            _myCurrentTime += theDeltaTime;

            final float myOffset = (float) Math.sin(_myCurrentTime * _myFrequency + _myOffset) * _myAmplitude;
            _myRestLength = _myInitialRestLength + myOffset;
        }

        super.apply(theDeltaTime, theParticleSystem);
    }
}
