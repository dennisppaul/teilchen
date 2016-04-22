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

import java.io.Serializable;
import processing.core.PVector;

/**
 * generate random numbers.
 *
 * note that if different types are requested the random generator moves on
 * anyway.
 */
public class Random
        implements Serializable {

    private static final long serialVersionUID = -5871934750136232555L;

    private static final java.util.Random _mySeedGenerator = new java.util.Random(System.currentTimeMillis());

    private final java.util.Random myRandomNumberGenerator;

    private static final Random _myInstance;

    static {
        _myInstance = new Random();
    }

    public static float FLOAT(float theStart, float theEnd) {
        return _myInstance.getFloat(theStart, theEnd);
    }

    public static float INT(int theStart, int theEnd) {
        return _myInstance.getInt(theStart, theEnd);
    }

    public Random() {
        this(_mySeedGenerator.nextLong());
    }

    public Random(long theSeed) {
        myRandomNumberGenerator = new java.util.Random(theSeed);
    }

    public void setSeed(long theSeed) {
        myRandomNumberGenerator.setSeed(theSeed);
    }

    /**
     * return a random int value from theStart to theEnd, including both values.
     *
     * @param theStart int
     * @param theEnd   int
     *
     * @return int
     */
    public int getInt(int theStart,
                      int theEnd) {
        int myDiff = (theEnd + 1) - theStart;
        return myRandomNumberGenerator.nextInt(myDiff) + theStart;
    }

    public int getInt() {
        return myRandomNumberGenerator.nextInt();
    }

    /**
     * return a random float value from theStart to theEnd, excluding both
     * values.
     *
     * @param theStart float
     * @param theEnd   float
     *
     * @return float
     */
    public float getFloat(float theStart,
                          float theEnd) {
        final float myDiff = theEnd - theStart;
        final float myRandomValue = myRandomNumberGenerator.nextFloat() * myDiff;
        return myRandomValue + theStart;
    }

    public float getFloat() {
        return myRandomNumberGenerator.nextFloat();
    }

//    public static float getFloat(float theStart,
//                                 float theEnd) {
//        return _mySeedGenerator.getFloat(theStart, theEnd);
//    }
    public PVector getVector3f(float theStart, float theEnd) {
        return new PVector(getFloat(theStart, theEnd),
                           getFloat(theStart, theEnd),
                           getFloat(theStart, theEnd));
    }

    public static void main(String[] args) {
        long myTime;
        Random myRandom;

        myRandom = new Random();
        System.out.println(myRandom.getFloat(20, 100));
        System.out.println(myRandom.getFloat(20, 100));
        System.out.println(myRandom.getFloat(20, 100));
        System.out.println(myRandom.getFloat(20, 100));
        System.out.println("***");

        myRandom = new Random();
        System.out.println(myRandom.getFloat(20, 100));
        System.out.println(myRandom.getInt(20, 100));
        System.out.println(myRandom.getFloat(20, 100));
        System.out.println(myRandom.getFloat(20, 100));
        System.out.println("***");

        /* use this random generator */
        myTime = System.currentTimeMillis();
        myRandom = new Random(0);
        for (int i = 0; i < 50000000; i++) {
            float myValue = myRandom.getFloat(20, 100);
            if (myValue < 20 || myValue > 100) {
                System.out.println(i + "ERROR");
            }
        }
        System.out.println("DONE: " + (System.currentTimeMillis() - myTime));

        /* use maths random generator */
        myTime = System.currentTimeMillis();
        for (int i = 0; i < 50000000; i++) {
            float myValue = (float) Math.random() * (100 - 20) + 20;
            if (myValue < 20 || myValue > 100) {
                System.out.println(i + "ERROR");
            }
        }
        System.out.println("DONE: " + (System.currentTimeMillis() - myTime));

    }
}
