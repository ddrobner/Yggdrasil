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

package ca.team3161.lib.utils.controls;

import java.util.function.Function;

/**
 * Interface for "Mode" objects which apply some function to a Joystick's input.
 * Examples include squaring the raw input values from the Joystick so that, for
 * example, a raw reading of 0.5 on the X axis will instead read out as 0.25.
 * Implementing a JoystickMode is also how one might handle setting an input
 * deadband on their Joysticks.
 */
@FunctionalInterface
public interface JoystickMode extends Function<Double, Double> {

    /**
     * Applies some transformation function to the input and returns a result.
     *
     * @param raw the value to adjust
     * @return the adjusted value
     */
    double adjust(double raw);

    default Double apply(Double value) {
        return adjust(value);
    }

    default JoystickMode compose(JoystickMode before) {
        return raw -> adjust(before.adjust(raw));
    }

    default JoystickMode andThen(JoystickMode after) {
        return raw -> after.adjust(adjust(raw));
    }

}
