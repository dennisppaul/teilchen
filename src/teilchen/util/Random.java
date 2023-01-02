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

import processing.core.PVector;

import java.io.Serializable;

/**
 * generate random numbers.
 * <p>
 * note that if different types are requested the random generator moves on anyway.
 */
public class Random implements Serializable {

    private static final Random mInstance;
    private static final java.util.Random mSeedGenerator = new java.util.Random(System.currentTimeMillis());
    private static final long serialVersionUID = -5871934750136232555L;

    static {
        mInstance = new Random();
    }

    public Random() {
        this(mSeedGenerator.nextLong());
    }

    public Random(long pSeed) {
        mRandomNumberGenerator = new java.util.Random(pSeed);
    }

    public static float FLOAT(float pStart, float pEnd) {
        return mInstance.getFloat(pStart, pEnd);
    }

    public static float INT(int pStart, int pEnd) {
        return mInstance.getInt(pStart, pEnd);
    }

    public void setSeed(long pSeed) {
        mRandomNumberGenerator.setSeed(pSeed);
    }

    /**
     * return a random int value from theStart to theEnd, including both values.
     *
     * @param pStart int
     * @param pEnd   int
     * @return int
     */
    public int getInt(int pStart, int pEnd) {
        int mDiff = (pEnd + 1) - pStart;
        return mRandomNumberGenerator.nextInt(mDiff) + pStart;
    }

    public int getInt() {
        return mRandomNumberGenerator.nextInt();
    }

    /**
     * return a random float value from theStart to theEnd, excluding both values.
     *
     * @param pStart float
     * @param pEnd   float
     * @return float
     */
    public float getFloat(float pStart, float pEnd) {
        final float mDiff = pEnd - pStart;
        final float mRandomValue = mRandomNumberGenerator.nextFloat() * mDiff;
        return mRandomValue + pStart;
    }

    public float getFloat() {
        return mRandomNumberGenerator.nextFloat();
    }

    //    public static float getFloat(float theStart,
//                                 float theEnd) {
//        return mSeedGenerator.getFloat(theStart, theEnd);
//    }
    public PVector getVector3f(float pStart, float pEnd) {
        return new PVector(getFloat(pStart, pEnd), getFloat(pStart, pEnd), getFloat(pStart, pEnd));
    }

    public static void main(String[] args) {
        long mTime;
        Random mRandom;

        mRandom = new Random();
        System.out.println(mRandom.getFloat(20, 100));
        System.out.println(mRandom.getFloat(20, 100));
        System.out.println(mRandom.getFloat(20, 100));
        System.out.println(mRandom.getFloat(20, 100));
        System.out.println("***");

        mRandom = new Random();
        System.out.println(mRandom.getFloat(20, 100));
        System.out.println(mRandom.getInt(20, 100));
        System.out.println(mRandom.getFloat(20, 100));
        System.out.println(mRandom.getFloat(20, 100));
        System.out.println("***");

        /* use this random generator */
        mTime = System.currentTimeMillis();
        mRandom = new Random(0);
        for (int i = 0; i < 50000000; i++) {
            float mValue = mRandom.getFloat(20, 100);
            if (mValue < 20 || mValue > 100) {
                System.out.println(i + "ERROR");
            }
        }
        System.out.println("DONE: " + (System.currentTimeMillis() - mTime));

        /* use maths random generator */
        mTime = System.currentTimeMillis();
        for (int i = 0; i < 50000000; i++) {
            float mValue = (float) Math.random() * (100 - 20) + 20;
            if (mValue < 20 || mValue > 100) {
                System.out.println(i + "ERROR");
            }
        }
        System.out.println("DONE: " + (System.currentTimeMillis() - mTime));

    }
    private final java.util.Random mRandomNumberGenerator;
}
