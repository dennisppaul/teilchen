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


import teilchen.IConnection;
import teilchen.Particle;
import teilchen.Physics;


public class Spring
        implements IForce,
                   IConnection {

    protected float _mySpringConstant;

    protected float _mySpringDamping;

    protected float _myRestLength;

    protected final Particle _myA;

    protected final Particle _myB;

    protected boolean _myOneWay;

    protected boolean _myActive;

    public Spring(Particle theA, Particle theB) {
        this(theA,
             theB,
             2.0f, 0.1f,
             theA.position().distance(theB.position()));
    }

    public Spring(Particle theA, Particle theB, float theRestLength) {
        this(theA,
             theB,
             2.0f, 0.1f,
             theRestLength);
    }

    public Spring(Particle theA,
                  Particle theB,
                  final float theSpringConstant,
                  final float theSpringDamping) {
        this(theA,
             theB,
             theSpringConstant,
             theSpringDamping,
             theA.position().distance(theB.position()));
    }

    public Spring(final Particle theA,
                  final Particle theB,
                  final float theSpringConstant,
                  final float theSpringDamping,
                  final float theRestLength) {
        _mySpringConstant = theSpringConstant;
        _mySpringDamping = theSpringDamping;
        _myRestLength = theRestLength;
        _myA = theA;
        _myB = theB;
        _myOneWay = false;
        _myActive = true;
    }

    public void setRestLengthByPosition() {
        _myRestLength = _myA.position().distance(_myB.position());
    }

    public float restlength() {
        return _myRestLength;
    }

    public void restlength(float theRestLength) {
        _myRestLength = theRestLength;
    }

    public final Particle a() {
        return _myA;
    }

    public final Particle b() {
        return _myB;
    }

    public final float currentLength() {
        return _myA.position().distance(_myB.position());
    }

    /**
     * spring constant.
     * @return float
     */
    public final float strength() {
        return _mySpringConstant;
    }

    /**
     * spring constant.
     * @param theSpringConstant float
     */
    public final void strength(float theSpringConstant) {
        _mySpringConstant = theSpringConstant;
    }

    public final float damping() {
        return _mySpringDamping;
    }

    public final void damping(float theSpringDamping) {
        _mySpringDamping = theSpringDamping;
    }

    public void setOneWay(boolean theOneWayState) {
        _myOneWay = theOneWayState;
    }

    public void apply(final float theDeltaTime, final Physics theParticleSystem) {
//        if (!_myA.fixed() || !_myB.fixed()) {
        float a2bX = _myA.position().x - _myB.position().x;
        float a2bY = _myA.position().y - _myB.position().y;
        float a2bZ = _myA.position().z - _myB.position().z;
        final float myInversDistance = fastInverseSqrt(a2bX * a2bX + a2bY * a2bY + a2bZ * a2bZ);
        final float myDistance = 1.0F / myInversDistance;

        if (myDistance == 0.0F) {
            a2bX = 0.0F;
            a2bY = 0.0F;
            a2bZ = 0.0F;
        } else {
            a2bX *= myInversDistance;
            a2bY *= myInversDistance;
            a2bZ *= myInversDistance;
        }

      final  float mSpringForce = -(myDistance - _myRestLength) * _mySpringConstant;
      final  float Va2bX = _myA.velocity().x - _myB.velocity().x;
      final  float Va2bY = _myA.velocity().y - _myB.velocity().y;
      final  float Va2bZ = _myA.velocity().z - _myB.velocity().z;
      final  float mDampingForce = -_mySpringDamping * (a2bX * Va2bX + a2bY * Va2bY + a2bZ * Va2bZ);
      final  float r = mSpringForce + mDampingForce;
        a2bX *= r;
        a2bY *= r;
        a2bZ *= r;

        if (_myOneWay) {
            if (!_myB.fixed()) {
                _myB.force().add(-2 * a2bX, -2 * a2bY, -2 * a2bZ);
            }
        } else {
            if (!_myA.fixed()) {
                _myA.force().add(a2bX, a2bY, a2bZ);
            }
            if (!_myB.fixed()) {
                _myB.force().add(-a2bX, -a2bY, -a2bZ);
            }
        }
//        }
    }

    protected static float fastInverseSqrt(float v) {
        final float half = 0.5f * v;
        int i = Float.floatToIntBits(v);
        i = 0x5f375a86 - (i >> 1);
        v = Float.intBitsToFloat(i);
        return v * (1.5f - half * v * v);
    }

    public boolean dead() {
        return _myA.dead() || _myB.dead();
    }

    public boolean active() {
        return _myActive;
    }

    public void active(boolean theActiveState) {
        _myActive = theActiveState;
    }
}
