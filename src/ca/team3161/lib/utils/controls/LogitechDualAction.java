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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * A Gamepad implementation describing the Logitech DualAction gamepad.
 */
public final class LogitechDualAction extends RepeatingSubsystem implements Gamepad {

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

    /* The actual FIRST-provided input device that we are implementing a
    * convenience wrapper around.
    */
    private final GenericHID backingHID;
    private final Map<ModeIdentifier, JoystickMode> controlsModeMap = new HashMap<>();
    private final Map<Binding, Runnable> buttonBindings = new ConcurrentHashMap<>();
    private final Map<Button, Boolean> buttonStates = new ConcurrentHashMap<>();
    private final int port;
    
    /**
     * Create a new LogitechDualAction gamepad/controller.
     * @param port the USB port for this controller
     */
    public LogitechDualAction(final int port) {
        super(20, TimeUnit.MILLISECONDS);
        Assert.assertTrue(port >= 0);
        this.port = port;
        backingHID = new Joystick(port); // Joystick happens to work well here, but any GenericHID should be fine
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
    public GenericHID getBackingHID() {
        return backingHID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPort() {
        return this.port;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void enableBindings() {
        start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disableBindings() {
        cancel();
    }

    @Override
    protected void defineResources() {
        // none!
    }

    @Override
    protected void task() throws Exception {
        final Map<Button, Boolean> previousButtonStates = new HashMap<>(buttonStates);
        for (final Button button : LogitechButton.values()) {
            buttonStates.put(button, getButton(button));
        }
        synchronized (buttonBindings) {
            for (final Map.Entry<Binding, Runnable> binding : buttonBindings.entrySet()) {
                final Button button = binding.getKey().getButton();
                final PressType pressType = binding.getKey().getPressType();
                final Runnable action = binding.getValue();
                switch (pressType) {
                    case PRESS:
                        if (buttonStates.get(button) && !previousButtonStates.get(button)) {
                            action.run();
                        }
                        break;
                    case RELEASE:
                        if (!buttonStates.get(button) && previousButtonStates.get(button)) {
                            action.run();
                        }
                        break;
                    case HOLD:
                        if (buttonStates.get(button)) {
                            action.run();
                        }
                        break;
                    default:
                        System.err.println("Gamepad on port " + Integer.toString(getPort())
                        + " has binding for unknown button press type " + pressType);
                        break;
                }
            }
        }
    }

    private static class Binding {
        private final Button button;
        private final PressType pressType;

        public Binding(final Button button, final PressType pressType) {
            Objects.requireNonNull(button);
            Objects.requireNonNull(pressType);
            this.button = button;
            this.pressType = pressType;
        }

        public Button getButton() {
            return button;
        }

        public PressType getPressType() {
            return pressType;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final Binding binding = (Binding) o;

            if (!button.equals(binding.button)) {
                return false;
            }
            if (pressType != binding.pressType) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = button.hashCode();
            result = 31 * result + pressType.hashCode();
            return result;
        }
    }

    private static class ModeIdentifier {
        private final Control control;
        private final Axis axis;

        public ModeIdentifier(final Control control, final Axis axis) {
            Objects.requireNonNull(control);
            Objects.requireNonNull(axis);
            this.control = control;
            this.axis = axis;
        }

        public Control getControl() {
            return control;
        }

        public Axis getAxis() {
            return axis;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final ModeIdentifier that = (ModeIdentifier) o;

            if (!axis.equals(that.axis)) {
                return false;
            }
            if (!control.equals(that.control)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = control.hashCode();
            result = 31 * result + axis.hashCode();
            return result;
        }
    }

} 
