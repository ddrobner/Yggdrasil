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

import ca.team3161.lib.robot.RepeatingSubsystem;
import ca.team3161.lib.utils.Assert;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A Gamepad implementation describing the Logitech DualAction gamepad.
 */
public final class LogitechDualAction extends RepeatingSubsystem implements Gamepad {

    /**
     * {@inheritDoc}.
     */
    public enum LogitechControls implements Control {
        LEFT_STICK,
        RIGHT_STICK,
        DPAD;

        @Override
        public int getIdentifier(final Axis axis) {
            return this.ordinal() * 2 + axis.getIdentifier() + 1;
        }
    }

    /**
     * {@inheritDoc}.
     */
    public enum LogitechButton implements Button {
        LEFT_BUMPER,
        RIGHT_BUMPER,
        LEFT_TRIGGER,
        RIGHT_TRIGGER,
        SELECT,
        START;

        @Override
        public int getIdentifier() {
            return super.ordinal() + 5;
        }
    }

    /**
     * {@inheritDoc}.
     */
    public enum LogitechAxis implements Axis {
        X,
        Y,
        Z;

        @Override
        public int getIdentifier() {
            return super.ordinal();
        }
    }

    /* The actual FIRST-provided input device that we are implementing a
    * convenience wrapper around.
    */
    private final GenericHID backingHID;
    private final Map<Control, JoystickMode> controlsModeMap = new HashMap<>();
    private final Map<Button, Runnable> buttonBindings = new HashMap<>();
    
    /**
     * Create a new LogitechDualAction gamepad/controller.
     * @param port the USB port for this controller
     */
    public LogitechDualAction(final int port) {
        super(20, TimeUnit.MILLISECONDS);
        Assert.assertTrue(port > 0);
        backingHID = new Joystick(port); // Joystick happens to work well here, but any GenericHID should be fine
        EnumSet.allOf(LogitechControls.class).stream().forEach(control -> controlsModeMap.put(control, new LinearJoystickMode()));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GenericHID getBackingHID() {
        return backingHID;
    }
    
    /**
     * Get a stick axis value.
     * @param axis which axis to get
     * @return the value from this axis, or 0 if the raw value falls within the
     * deadzone
     */
    @Override
    public double getValue(final Control controls, final Axis axis) {
        return controlsModeMap.get(controls).adjust(backingHID.getRawAxis(controls.getIdentifier(axis)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getButton(final Button button) {
        return backingHID.getRawButton(button.getIdentifier());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMode(final Control control, final JoystickMode joystickMode) {
        controlsModeMap.put(control, joystickMode);
    }

    public void bind(final Button button, final Runnable binding) {
        buttonBindings.put(button, binding);
    }

    public void unbind(final Button button) {
        buttonBindings.remove(button);
    }

    public void enableBindings() {
        start();
    }

    public void disableBindings() {
        cancel();
    }

    @Override
    protected void defineResources() {
        // none!
    }

    @Override
    protected void task() throws Exception {
        synchronized (buttonBindings) {
            buttonBindings.entrySet().stream().filter(e -> getButton(e.getKey())).forEach(e -> e.getValue().run());
        }
    }

} 
