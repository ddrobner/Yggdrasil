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

package ca.team3161.lib.robot.pid;

import edu.wpi.first.wpilibj.PIDSource;

/**
 * A PID source that specifically returns values to be interpreted as
 * degrees.
 *
 * @param <T> the type of the PIDSource sensor for this AnglePidSrc (eg potentiometer)
 */
public interface PIDAngleValueSrc<T extends PIDSource> extends PIDSrc<T, Float> {

    /**
     * The minimal angle of this sensor.
     *
     * @return the minimal angle of this sensor
     */
    Float getMinAngle();

    /**
     * The maximal angle of this sensor.
     *
     * @return the maximal angle of this sensor
     */
    Float getMaxAngle();

    /**
     * Get the angle measured by this sensor.
     * @return the angle value.
     */
    default Float getAngle() {
        return getPIDValue();
    }
}
