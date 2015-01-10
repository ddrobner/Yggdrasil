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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * A Gamepad implementation describing the Logitech DualAction gamepad.
 */
public final class LogitechDualAction extends AbstractController {

    /**
     * {@inheritDoc}.
     */
    public enum LogitechControl implements Control {
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

    /**
     * Create a new LogitechDualAction gamepad/controller.
     * @param port the USB port for this controller
     */
    public LogitechDualAction(final int port) {
        super(port, 20, TimeUnit.MILLISECONDS);
        for (final Control control : LogitechControl.values()) {
            for (final Axis axis : LogitechAxis.values()) {
                controlsModeMap.put(new ModeIdentifier(control, axis), new LinearJoystickMode());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getValue(final Control control, final Axis axis) {
        Objects.requireNonNull(control);
        Objects.requireNonNull(axis);
        if (!(control instanceof LogitechControl)) {
            System.err.println("Gamepad on port " + this.port + " getValue() called with invalid control "
            + control);
        }
        if (!(axis instanceof LogitechAxis)) {
            System.err.println("Gamepad on port " + this.port + " getValue() called with invalid axis "
                                       + control);
        }
        return controlsModeMap.entrySet()
                       .stream().filter(e -> e.getKey().getControl().equals(control)
                                                     && e.getKey().getAxis().equals(axis))
                       .collect(Collectors.toList()).get(0).getValue().adjust(backingHID.getRawAxis(control.getIdentifier(axis)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getButton(final Button button) {
        Objects.requireNonNull(button);
        if (!(button instanceof LogitechButton)) {
            System.err.println("Gamepad on port " + this.port + " getButton() called with invalid button "
                                       + button);
        }
        return backingHID.getRawButton(button.getIdentifier());
    }

    /**
     *
     */
    @Override
    protected Set<Button> getButtons() {
        return new HashSet<>(Arrays.asList(LogitechButton.values()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMode(final Control control, final Axis axis, final JoystickMode joystickMode) {
        Objects.requireNonNull(control);
        Objects.requireNonNull(axis);
        Objects.requireNonNull(joystickMode);
        if (!(control instanceof LogitechControl)) {
            System.err.println("Gamepad on port " + this.port + " setMode() called with invalid control "
                                       + control);
        }
        controlsModeMap.put(new ModeIdentifier(control, axis), joystickMode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bind(final Button button, final PressType pressType, final Runnable binding) {
        Objects.requireNonNull(button);
        Objects.requireNonNull(pressType);
        Objects.requireNonNull(binding);
        if (!(button instanceof LogitechButton)) {
            System.err.println("Gamepad on port " + this.port + " bind() called with invalid button "
                                       + button);
        }
        buttonBindings.put(new Binding(button, pressType), binding);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unbind(final Button button, final PressType pressType) {
        Objects.requireNonNull(button);
        Objects.requireNonNull(pressType);
        if (!(button instanceof LogitechButton)) {
            System.err.println("Gamepad on port " + this.port + " unbind() called with invalid button "
                                       + button);
        }
        buttonBindings.entrySet().removeIf(e -> e.getKey().getButton().equals(button)
                                                        && e.getKey().getPressType().equals(pressType));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasBinding(final Button button, final PressType pressType) {
        Objects.requireNonNull(button);
        if (!(button instanceof LogitechButton)) {
            System.err.println("Gamepad on port " + this.port + " hasBinding() called with invalid button "
                                       + button);
        }
        return buttonBindings.keySet().stream().anyMatch(b -> b.getButton().equals(button) && b.getPressType().equals(pressType));
    }


} 
