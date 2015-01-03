/* Copyright (c) 2014, FRC3161
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

package ca.team3161.lib.robot;

import ca.team3161.lib.utils.Assert;
import ca.team3161.lib.utils.Utils;
import edu.wpi.first.wpilibj.SpeedController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implements a container for SpeedControllers.
 */
public final class Drivetrain implements SpeedController {
    private final List<SpeedController> speedControllers = new ArrayList<>();
    private float inversion = 1.0f;

    /**
     * Create a new Drivetrain instance.
     * @param controllers a varargs list or array of SpeedController objects. May be
     * all the same type, or may be mixed.
     */
    public Drivetrain(final SpeedController ... controllers) {
        this(Arrays.asList(controllers));
    }

    /**
     * Create a new Drivetrain instance.
     * @param controllers a collection of SpeedControllers for this Drivetrain to manage
     */
    public Drivetrain(final Collection<SpeedController> controllers) {
        Objects.requireNonNull(controllers);
        Assert.assertTrue("Must have at least one SpeedController per Drivetrain", controllers.size() > 0);
        speedControllers.addAll(controllers);
    }

    /**
     * Invert all PWM values for this Drivetrain.
     * @param inverted whether the PWM values should be inverted or not
     * @return this Drivetrain instance
     */
    public Drivetrain setInverted(final boolean inverted) {
        if (inverted) {
            inversion = -1.0f;
        } else {
            inversion = 1.0f;
        }
        return this;
    }

    /**
     * The current speed of this Drivetrain.
     * @return the current PWM value of the SpeedController collection (-1.0 to 1.0)
     */
    public double get() {
        // All of the SpeedControllers will always be set to the same value,
        // so simply get the value of the first one.
        return inversion * speedControllers.get(0).get();
    }

    /**
     * The speeds of all SpeedControllers within this Drivetrain.
     * They should all be nearly identical, other than error due to floating point
     * precision.
     * @return a list enumerating all the current PWM values of the SpeedController collection (-1.0 to 1.0)
     */
    public List<Double> getAll() {
        return speedControllers.stream().mapToDouble(SpeedController::get).boxed().collect(Collectors.toList());
    }

    /**
     * Set the pwm value (-1.0 to 1.0).
     * @param pwm the PWM value to assign to each SpeedController in the collection
     */
    public void set(double pwm) {
        speedControllers.forEach(c -> c.set(inversion * Utils.normalizePwm(pwm)));
    }

    /**
     * You probably shouldn't use this. Only included as required by SpeedController interface.
     * @param pwm the PWM value to assign to each SpeedController in the collection
     * @param syncGroup the update group to add this Set() to, pending UpdateSyncGroup(). If 0, update immediately.
     */
    public void set(double pwm, final byte syncGroup) {
        speedControllers.forEach(c -> c.set(inversion * Utils.normalizePwm(pwm), syncGroup));
    }

    /**
     * Disable each SpeedController in the collection.
     */
    public void disable() {
        speedControllers.forEach(SpeedController::disable);
    }

    /**
     * Call pidWrite on each SpeedController in this collection.
     * @param output Set the output to the value calculated by PIDController
     */
    public void pidWrite(final double output) {
        speedControllers.forEach(c -> c.pidWrite(output));
    }

}
