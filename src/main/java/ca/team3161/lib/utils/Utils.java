/*
 * Copyright (c) 2014-2017, FRC3161
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following disclaimer in the documentation and/or
 *   other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package ca.team3161.lib.utils;

import java.util.function.Consumer;
import java.util.function.Supplier;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Pretty printing arrays, rounding doubles, ensuring PWM values fall within
 * the range [-1.0, 1.0], etc.
 */
public class Utils {

    private Utils() {
    }

    /**
     * Normalize a PWM value so it remains in the range [-1.0, 1.0]
     * If a value above 1.0 is given, 1.0 is returned. If a value below -1.0
     * is given, -1.0 is returned. For other values, they are simply returned
     *
     * @param val the value to normalize
     * @return the normalized value
     */
    public static double normalizePwm(final double val) {
        if (val < -1.0d) {
            return -1.0d;
        }
        if (val > 1.0d) {
            return 1.0d;
        }
        return val;
    }

    /**
     * Normalize a PWM value so it remains in the range [-1.0, 1.0]
     * If a value above 1.0 is given, 1.0 is returned. If a value below -1.0
     * is given, -1.0 is returned. For other values, they are simply returned
     *
     * @param val the value to normalize
     * @return the normalized value
     */
    public static float normalizePwm(final float val) {
        if (val < -1.0f) {
            return -1.0f;
        }
        if (val > 1.0f) {
            return 1.0f;
        }
        return val;
    }

    /**
     * Assert that an int is greater than 0.
     *
     * @param in the argument which must be non-negative
     * @return the argument back
     * @throws IllegalArgumentException if the argument is less than 0
     */
    public static int requireNonNegative(int in) {
        if (in < 0) {
            throw new IllegalArgumentException();
        }
        return in;
    }

    /**
     * Assert that a long is greater than 0.
     *
     * @param in the argument which must be non-negative
     * @return the argument back
     * @throws IllegalArgumentException if the argument is less than 0
     */
    public static long requireNonNegative(long in) {
        if (in < 0) {
            throw new IllegalArgumentException();
        }
        return in;
    }

    /**
     * Assert that a double is greater than 0.
     *
     * @param in the argument which must be non-negative
     * @return the argument back
     * @throws IllegalArgumentException if the argument is less than 0
     */
    public static double requireNonNegative(double in) {
        if (in < 0) {
            throw new IllegalArgumentException();
        }
        return in;
    }

    /**
     * Assert that a float is greater than 0.
     *
     * @param in the argument which must be non-negative
     * @return the argument back
     * @throws IllegalArgumentException if the argument is less than 0
     */
    public static float requireNonNegative(float in) {
        if (in < 0) {
            throw new IllegalArgumentException();
        }
        return in;
    }

    public static boolean between(double a, double val, double b) {
        return a <= val && val <= b;
    }

    public static boolean between(float a, float val, float b) {
        return a <= val && val <= b;
    }

    public static boolean between(int a, int val, int b) {
        return a <= val && val <= b;
    }

    public static boolean between(long a, long val, long b) {
        return a <= val && val <= b;
    }

    public static <T> T safeInit(String label, Supplier<T> supplier) {
        try {
            T t = supplier.get();
            if (t == null) {
                throw new NullPointerException(String.format("\"%s\" initialized to null", label));
            }
            return t;
        } catch (Exception e) {
            DriverStation.reportError(String.format("Failed to initialize \"%s\"", label), e.getStackTrace());
            e.printStackTrace();
            throw e;
        }
    }

    public static void safeExec(String label, Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            DriverStation.reportError(String.format("Running \"%s\" threw exception", label), e.getStackTrace());
            e.printStackTrace();
        }
    }

    public static <T> Consumer<T> safeExec(String label, Consumer<T> consumer) {
        return (T t) -> {
            try {
                consumer.accept(t);
            } catch (Exception e) {
                DriverStation.reportError(String.format("Running \"%s\" threw exception", label), e.getStackTrace());
                e.printStackTrace();
            }
        };
    }

}
