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

package ca.team3161.lib.utils.controls;

import edu.wpi.first.wpilibj.GenericHID;

/**
 * A thin wrapper over the FRC Joystick class, configurable with per-axis filtering "modes"
 */
public class Joystick {

    private final GenericHID backingHID;
    private JoystickMode xAxisMode = new LinearJoystickMode();
    private JoystickMode yAxisMode = new LinearJoystickMode();

    /**
     * Construct a new Joystick, using simple LinearJoystickModes for both axes
     * @param port the port the Joystick is plugged into
     */
    public Joystick(final int port) {
        this(port, new LinearJoystickMode(), new LinearJoystickMode());
    }

    /**
     * Construct a new Joystick
     * @param port the port the Joystick is plugged into. Must be non-negative.
     * @param xAxisMode the mode for the X axis. Must not be null.
     * @param yAxisMode the mode for the Y axis. Must not be null.
     */
    public  Joystick(final int port, final JoystickMode xAxisMode, final JoystickMode yAxisMode) {
        if (port < 0) {
            throw new IllegalArgumentException("Port cannot be negative, was: " + Integer.toString(port));
        }
        if (xAxisMode == null) {
            throw new NullPointerException("JoystickModes cannot be null - received null X axis mode");
        }
        if (yAxisMode == null) {
            throw new NullPointerException("JoystickModes cannot be null - received null Y axis mode");
        }
        this.backingHID = new edu.wpi.first.wpilibj.Joystick(port);
        this.xAxisMode = xAxisMode;
        this.yAxisMode = yAxisMode;
    }

    /**
     * Set the JoystickMode for the X axis, after the Joystick has already been constructed
     * @param xAxisMode the mode for the Joystick X axis (linear curve, squared curve, etc)
     */
    public void setXAxisMode(final JoystickMode xAxisMode) {
        if (xAxisMode == null) {
            throw new NullPointerException();
        }
        this.xAxisMode = xAxisMode;
    }
    
    /**
     * Set the JoystickMode for the X axis, after the Joystick has already been constructed
     * @param yAxisMode the mode for the Joystick Y axis (linear curve, squared curve, etc)
     */
    public void setYAxisMode(final JoystickMode yAxisMode) {
        if (yAxisMode == null) {
            throw new NullPointerException();
        }
        this.yAxisMode = yAxisMode;
    }

    /**
     * Get the X-axis reading from this Joystick, adjusted by the xAxisMode
     * @return the value
     * @see ca.team3161.lib.utils.controls.Joystick#setXAxisMode(JoystickMode)
     */
    public double getX() {
        return xAxisMode.adjust(backingHID.getX());
    }

    /**
     * Get the Y-axis reading from this Joystick, adjusted by the yAxisMode
     * @return the value
     * @see ca.team3161.lib.utils.controls.Joystick#setYAxisMode(JoystickMode)
     */
    public double getY() {
        return yAxisMode.adjust(backingHID.getY());
    }

    /**
     * Check if a button is pressed
     * @param button identifier for the button to check
     * @return the button's pressed state
     * @see edu.wpi.first.wpilibj.Joystick#getRawButton(int)
     */
    public boolean getButton(final int button) {
        return backingHID.getRawButton(button);
    }

    /**
     * Get an arbitrary axis reading from this Joystick
     * @param axis identifier for the axis to check
     * @return the value
     * @see edu.wpi.first.wpilibj.Joystick#getRawAxis(int)
     */
    public double getRawAxis(final int axis) {
        return backingHID.getRawAxis(axis);
    }

    /**
     * Get the FRC/WPI-library Generic Human Interface Device which this Joystick wraps
     * @return the backing HID
     */
    public GenericHID getBackingHID() {
        return backingHID;
    }

}
