

package mathematik;


import java.util.Random;


public abstract class PerlinNoise {

    //////////////////////////////////////////////////////////////

    // PERLIN NOISE

    // [toxi 040903]
    // octaves and amplitude amount per octave are now user controlled
    // via the noiseDetail() function.

    // [toxi 030902]
    // cleaned up code and now using bagel's cosine table to speed up

    // [toxi 030901]
    // implementation by the german demo group farbrausch
    // as used in their demo "art": http://www.farb-rausch.de/fr010src.zip
    static final int PERLIN_YWRAPB = 4;

    static final int PERLIN_YWRAP = 1 << PERLIN_YWRAPB;

    static final int PERLIN_ZWRAPB = 8;

    static final int PERLIN_ZWRAP = 1 << PERLIN_ZWRAPB;

    static final int PERLIN_SIZE = 4095;

    static int perlin_octaves = 4; // default to medium smooth

    static float perlin_amp_falloff = 0.5f; // 50% reduction/octave

    // [toxi 031112]
    // new vars needed due to recent change of cos table in PGraphics
    static int perlin_TWOPI,  perlin_PI;

    static float[] perlin_cosTable;

    static float[] perlin;

    static Random perlinRandom;

    public static int SEED = 0;

    /**
     * Computes the Perlin noise function value at point x.
     */
    public static float noise(float x) {
        // is this legit? it's a dumb way to do it (but repair it later)
        return noise(x, 0f, 0f);
    }

    /**
     * Computes the Perlin noise function value at the point x, y.
     */
    public static float noise(float x, float y) {
        return noise(x, y, 0f);
    }

    public static float noise(float x, float y, float z) {
        if (perlin == null) {
            if (perlinRandom == null) {
                perlinRandom = new Random(SEED);
            }
            perlin = new float[PERLIN_SIZE + 1];
            for (int i = 0; i < PERLIN_SIZE + 1; i++) {
                perlin[i] = perlinRandom.nextFloat(); //(float)Math.random();
            }
            // [toxi 031112]
            // noise broke due to recent change of cos table in PGraphics
            // this will take care of it
            perlin_cosTable = cosLUT;
            perlin_TWOPI = perlin_PI = SINCOS_LENGTH;
            perlin_PI >>= 1;
        }

        if (x < 0) {
            x = -x;
        }
        if (y < 0) {
            y = -y;
        }
        if (z < 0) {
            z = -z;
        }

        int xi = (int)x, yi = (int)y, zi = (int)z;
        float xf = (float)(x - xi);
        float yf = (float)(y - yi);
        float zf = (float)(z - zi);
        float rxf, ryf;

        float r = 0;
        float ampl = 0.5f;

        float n1, n2, n3;

        for (int i = 0; i < perlin_octaves; i++) {
            int of = xi + (yi << PERLIN_YWRAPB) + (zi << PERLIN_ZWRAPB);

            rxf = noise_fsc(xf);
            ryf = noise_fsc(yf);

            n1 = perlin[of & PERLIN_SIZE];
            n1 += rxf * (perlin[(of + 1) & PERLIN_SIZE] - n1);
            n2 = perlin[(of + PERLIN_YWRAP) & PERLIN_SIZE];
            n2 += rxf * (perlin[(of + PERLIN_YWRAP + 1) & PERLIN_SIZE] - n2);
            n1 += ryf * (n2 - n1);

            of += PERLIN_ZWRAP;
            n2 = perlin[of & PERLIN_SIZE];
            n2 += rxf * (perlin[(of + 1) & PERLIN_SIZE] - n2);
            n3 = perlin[(of + PERLIN_YWRAP) & PERLIN_SIZE];
            n3 += rxf * (perlin[(of + PERLIN_YWRAP + 1) & PERLIN_SIZE] - n3);
            n2 += ryf * (n3 - n2);

            n1 += noise_fsc(zf) * (n2 - n1);

            r += n1 * ampl;
            ampl *= perlin_amp_falloff;
            xi <<= 1;
            xf *= 2;
            yi <<= 1;
            yf *= 2;
            zi <<= 1;
            zf *= 2;

            if (xf >= 1.0f) {
                xi++;
                xf--;
            }
            if (yf >= 1.0f) {
                yi++;
                yf--;
            }
            if (zf >= 1.0f) {
                zi++;
                zf--;
            }
        }
        return r;
    }


    // [toxi 031112]
    // now adjusts to the size of the cosLUT used via
    // the new variables, defined above
    private static float noise_fsc(float i) {
        // using bagel's cosine table instead
        return 0.5f * (1.0f - perlin_cosTable[(int)(i * perlin_PI) % perlin_TWOPI]);
    }


    // precalculate sin/cos lookup tables [toxi]
    // circle resolution is determined from the actual used radii
    // passed to ellipse() method. this will automatically take any
    // scale transformations into account too

    // [toxi 031031]
    // changed table's precision to 0.5 degree steps
    // introduced new vars for more flexible code
    static final protected float sinLUT[];

    static final protected float cosLUT[];

    static final protected float SINCOS_PRECISION = 0.5f;

    static final protected int SINCOS_LENGTH = (int)(360f / SINCOS_PRECISION);

    static final float DEG_TO_RAD = (float)Math.PI / 180.0f;

    static final float RAD_TO_DEG = 180.0f / (float)Math.PI;


    static {
        sinLUT = new float[SINCOS_LENGTH];
        cosLUT = new float[SINCOS_LENGTH];
        for (int i = 0; i < SINCOS_LENGTH; i++) {
            sinLUT[i] = (float)Math.sin(i * DEG_TO_RAD * SINCOS_PRECISION);
            cosLUT[i] = (float)Math.cos(i * DEG_TO_RAD * SINCOS_PRECISION);
        }
    }
}
