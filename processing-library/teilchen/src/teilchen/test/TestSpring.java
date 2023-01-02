package teilchen.test;

import teilchen.Physics;
import teilchen.force.Spring;
import teilchen.integration.Euler;
import teilchen.integration.Midpoint;
import teilchen.integration.RungeKutta;

import static processing.core.PApplet.nf;

public class TestSpring {

    public static void main(String[] args) {
        Physics mPhysics = new Physics();

        mPhysics.setIntegratorRef(new Euler());
        mPhysics.setIntegratorRef(new Midpoint());
        mPhysics.setIntegratorRef(new RungeKutta());

        /* test particles with distance 0.0 */
        Spring s = mPhysics.makeSpring(mPhysics.makeParticle(0, 0), mPhysics.makeParticle(0, 0));

        System.out.println("integrator ......... : " + mPhysics.getIntegrator().getClass().getSimpleName());
        System.out.println("num of springs ..... : " + nf(mPhysics.forces().size(), 2));
        System.out.println("num of particles ... : " + nf(mPhysics.forces().size(), 2));

        for (int i = 0; i < 10; i++) {
            System.out.println("step ............... : " + nf(i, 3));
            mPhysics.step(1.0f / 30.0f);
            System.out.println(s.currentLength());
        }
    }
}
