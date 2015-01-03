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

package ca.team3161.lib.robot.pid;

/**
 * A PID loop, which uses a PIDSrc and a set of constants to iteratively determine
 * output values with which a system can reach and maintain a target value.
 */
public abstract class AbstractPID implements PID {
    
    /**
     * A PIDSrc sensor.
     */
    protected final PIDSrc source;
    
    /**
     * PID constants.
     */
    protected float deadband, kP, kI, kD, integralError, prevError, deltaError;
    
    /**
     * If this PID loop has reached its target.
     */
    protected boolean atTarget;
    
    /**
     * Create a new AbstractPID instance.
     * @param source the PIDSrc source sensor
     * @param deadband filter value - do not act when current error is within this bound
     * @param kP P constant
     * @param kI I constant
     * @param kD D constant
     */
    public AbstractPID(final PIDSrc source, final float deadband,
            final float kP, final float kI, final float kD) {
        this.source = source;
        this.deadband = deadband;
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.atTarget = false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void clear() {
        integralError = 0.0f;
        prevError = 0.0f;
        deltaError = 0.0f;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract float pid(final float target);
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final PIDSrc getSrc() {
        return this.source;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean atTarget() {
        return atTarget;
    }
    
}
