/*
 * Copyright (c) 2014-2015, FRC3161
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
 * A PID loop, which uses a PIDSrc and a set of constants to iteratively determine
 * output values with which a system can reach and maintain a target value.
 *
 * @param <T> the type of sensor used as input to this PID system.
 * @param <V> the type of value read from the sensor used as input to this PID system.
 */
public interface PID<T extends PIDSource, V extends Number> {

    /**
     * Reset the state of this PID loop.
     */
    void clear();

    /**
     * Iterate the PID loop.
     *
     * @param target the desired target value. Units depend on the context of this PID
     * @return the output value to set to eg a SpeedController to reach the specified target
     */
    V pid(V target);

    /**
     * Get the source sensor of this PID.
     *
     * @return the PIDSrc (PID source sensor) used by this PID loop
     */
    PIDSrc<T, V> getSrc();

    /**
     * Check if this PID has reached its target value.
     *
     * @return whether this PID loop has reached the specified target value
     */
    boolean atTarget();

    /**
     * Set the Proportional constant for this PID.
     *
     * @param kP the Proportional constant.
     */
    void setkP(float kP);

    /**
     * Get the Proportional constant for this PID.
     *
     * @return the Proportional constant.
     */
    float getkP();

    /**
     * Set the Integral constant for this PID.
     *
     * @param kI the Integral constant.
     */
    void setkI(float kI);

    /**
     * Get the Integral constant for this PID.
     *
     * @return the Integral constant.
     */
    float getkI();

    /**
     * Set the Derivative constant for this PID.
     *
     * @param kD the Derivative constant.
     */
    void setkD(float kD);

    /**
     * Get the Derivative constant for this PID.
     *
     * @return the Derivative constant.
     */
    float getkD();
}
