/*
 * Copyright (c) 2015-2015, FRC3161.
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

package ca.team3161.lib.robot.utils;

import static ca.team3161.lib.utils.Utils.requireNonNegative;
import static java.lang.Double.compare;

/**
 * Describes physical attributes of a Robot.
 */
public class ChassisParameters {

    private final double wheelRadius, wheelBaseLength, wheelBaseWidth, encoderWheelGearRatio, encoderCPR;

    /**
     * Construct a new ChassisParameters object.
     *
     * @param wheelRadius           the radius of the wheels on this Robot's drivetrain
     * @param wheelBaseLength       the length between the furthest forward and further backward axles on this Robot's drivetrain
     * @param wheelBaseWidth        the width between the left and ride side wheels on this Robot's drivetrain
     * @param encoderWheelGearRatio the gear ratio between the output shaft the encoders are mounted on and the wheels
     * @param encoderCPR            the number of "ticks" the encoders on the drivetrain will count for each full revolution
     */
    public ChassisParameters(final double wheelRadius, final double wheelBaseLength, final double wheelBaseWidth,
                             final double encoderWheelGearRatio, final double encoderCPR) {
        this.wheelRadius = requireNonNegative(wheelRadius);
        this.wheelBaseLength = requireNonNegative(wheelBaseLength);
        this.wheelBaseWidth = requireNonNegative(wheelBaseWidth);
        this.encoderWheelGearRatio = requireNonNegative(encoderWheelGearRatio);
        this.encoderCPR = requireNonNegative(encoderCPR);
    }

    /**
     * Get the wheel radius.
     *
     * @return the wheel radius
     */
    public double getWheelRadius() {
        return wheelRadius;
    }

    /**
     * Get the wheelbase length.
     *
     * @return the wheelbase length
     */
    public double getWheelBaseLength() {
        return wheelBaseLength;
    }

    /**
     * Get the wheelbase width.
     *
     * @return the wheelbase width
     */
    public double getWheelBaseWidth() {
        return wheelBaseWidth;
    }

    /**
     * Get the gear ratio between what the encoders measure and what the wheels experience.
     *
     * @return the gear ratio
     */
    public double getEncoderWheelGearRatio() {
        return encoderWheelGearRatio;
    }

    /**
     * Get the number of ticks per revolution for the drivetrain encoders.
     *
     * @return the number of ticks per revolution
     */
    public double getEncoderCPR() {
        return encoderCPR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ChassisParameters that = (ChassisParameters) o;

        if (compare(that.encoderCPR, encoderCPR) != 0) {
            return false;
        }
        if (compare(that.encoderWheelGearRatio, encoderWheelGearRatio) != 0) {
            return false;
        }
        if (compare(that.wheelBaseLength, wheelBaseLength) != 0) {
            return false;
        }
        if (compare(that.wheelBaseWidth, wheelBaseWidth) != 0) {
            return false;
        }
        if (compare(that.wheelRadius, wheelRadius) != 0) {
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(wheelRadius);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(wheelBaseLength);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(wheelBaseWidth);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(encoderWheelGearRatio);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(encoderCPR);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
