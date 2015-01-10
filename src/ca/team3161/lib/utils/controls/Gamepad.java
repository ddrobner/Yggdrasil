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

import java.util.stream.Stream;

/**
 * An interface defining a Gamepad controller. All Gamepads are expected to
 * have two thumbsticks, a directional pad, and some clickable buttons.
 * Not all Gamepads will have variable triggers, and not all raw button or
 * axis mappings are the same. These details are left to specific Gamepad
 * implementations.
 */
public interface Gamepad {

    /**
     * Get the backing input device of this Gamepad.
     * @return the backing input device, eg Joystick
     */
    GenericHID getBackingHID();

    /**
     * Get the USB port (as numbered in the Driver Station) that this Gamepad is plugged into.
     * @return the USB port number
     */
    int getPort();

    /**
     * Get the value of an axis on a control. Generally between -1.0 and 1.0. Controls
     * and Axes should be provided by Gamepad implementations supplying their own valid
     * possible values. Controls and Axes defined by one Gamepad implementation should
     * not be used as parameters to other Gamepad implementations.
     * @param control the control (eg thumbstick) to check
     * @param axis the axis of the control to read
     * @return the value of the axis on the control
     */
    double getValue(Control control, Axis axis);

    /**
     * Get the value of a button on the controller. Buttons
     * should be provided by Gamepad implementations supplying their own valid
     * possible values. Buttons defined by one Gamepad implementation should
     * not be used as parameters to other Gamepad implementations.
     * @param button which button to check. The mapping from values here to
     * actual buttons will depend on the specific Gamepad implementation
     * @return whether the specified button is currently pressed or not
     */
    boolean getButton(Button button);

    /**
     * Set a mode to adjust input on one of the controls of this Gamepad. Controls
     * should be provided by Gamepad implementations supplying their own valid
     * possible values. Controls defined by one Gamepad implementation should
     * not be used as parameters to other Gamepad implementations. Likewise for axes.
     * @param control the control on which to set a mode
     * @param axis the axis of the control on which to set a mode
     * @param joystickMode the mode to set
     */
    void setMode(Control control, Axis axis, JoystickMode joystickMode);

    /**
     * Bind a button press on this gamepad to an action to be performed when the button
     * is pressed. Buttons should be provided by Gamepad implementations supplying their
     * own valid possible values. Buttons defined by one Gamepad implementation should
     * not be used as parameters to other Gamepad implementations.
     * @param button the button on which to bind an action
     * @param binding the action to be bound
     */
    default void bind(Button button, Runnable binding) {
        bind(button, PressType.PRESS, binding);
    }

    /**
     * Bind a button press on this gamepad to an action to be performed when the button
     * is pressed, released, or periodically while the button is held. Buttons should be
     * provided by Gamepad implementations supplying their own valid possible values.
     * Buttons defined by one Gamepad implementation should* not be used as parameters
     * to other Gamepad implementations.
     * @param button the button on which to bind an action
     * @param pressType the type of button press which should trigger the action
     * @param binding the action to be bound
     */
    void bind(Button button, PressType pressType, Runnable binding);

    /**
     * Remove all bindings for the given button. Buttons should be provided by Gamepad
     * implementations supplying their own valid possible values. Buttons defined by one
     * Gamepad implementation should not be used as parameters to other Gamepad implementations.
     * @param button the button for which to unbind all actions
     */
    default void unbind(Button button) {
        Stream.of(PressType.values()).forEach(t -> unbind(button, t));
    }

    /**
     * Remove a binding for the given button. Buttons should be provided by Gamepad
     * implementations supplying their own valid possible values. Buttons defined by one
     * Gamepad implementation should not be used as parameters to other Gamepad implementations.
     * @param button the button for which to unbind an action
     * @param pressType the type of button press for which to unbind an action
     */
    void unbind(Button button, PressType pressType);

    /**
     * Check if a given button has a bound action. Buttons should be provided by Gamepad
     * implementations supplying their own valid possible values. Buttons defined by one
     * Gamepad implementation should not be used as parameters to other Gamepad implementations.
     * @param button the button to check for any bindings
     * @return if the button has any bindings
     */
    default boolean hasBinding(Button button) {
        return Stream.of(PressType.values()).map(pressType -> hasBinding(button, pressType)).anyMatch(t -> t);
    }

    /**
     * Check if a given button has a bound action for a specific button press type. Buttons
     * should be provided by Gamepad implementations supplying their own valid possible values.
     * Buttons defined by one Gamepad implementation should not be used as parameters to other
     * Gamepad implementations.
     * @param button the button to check for bindings
     * @param pressType the type of button press to check for bindings
     * @return if the button has any bindings for the given press type
     */
    boolean hasBinding(Button button, PressType pressType);

    /**
     * Enable button bindings. If bindings are not enabled, then no bound actions will be executed.
     * By default, bindings are not enabled.
     */
    void enableBindings();

    /**
     * Disable button bindings. If bindings are not enabled, then no bound actions will be executed.
     * By default, bindings are not enabled.
     */
    void disableBindings();

    /**
     * A physical control on a Gamepad, eg a thumbstick or directional pad.
     */
    interface Control {
        int getIdentifier(Axis axis);
    }

    /**
     * An axis for a control, eg horizontal, vertical.
     */
    interface Axis {
        int getIdentifier();
    }

    /**
     * A physical button on a gamepad.
     */
    interface Button {
        int getIdentifier();
    }

    /**
     * Types of button press actions.
     */
    enum PressType {
        PRESS,
        RELEASE,
        HOLD,
    }

}
