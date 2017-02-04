/*
 * Copyright (c) 2017, FRC3161.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice, this
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

package ca.team3161.lib.robot.motion.actuators;

import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tResourceType;
import edu.wpi.first.wpilibj.hal.HAL;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;

public abstract class LinearActuator extends PWM {

    private static final String LIVE_WINDOW_TYPE = "LinearActuator";

    private ITable liveWindowTable;
    private ITableListener liveWindowTableListener;

    public LinearActuator(final int channel) {
        super(channel);
        setBounds(getMaxPwmWidth(), 0, 0, 0, getMinPwmWidth());
        setPeriodMultiplier(PeriodMultiplier.k4X);

        LiveWindow.addActuator(LIVE_WINDOW_TYPE, getChannel(), this);
        HAL.report(tResourceType.kResourceType_Servo, getChannel());
    }

    protected abstract double getMinPwmWidth();

    protected abstract double getMaxPwmWidth();

    public double get() {
        return getPosition();
    }

    public String getSmartDashboardType() {
        return LIVE_WINDOW_TYPE;
    }

    @Override
    public void initTable(ITable subtable) {
        liveWindowTable = subtable;
        updateTable();
    }

    @Override
    public void updateTable() {
        if (liveWindowTable != null) {
            liveWindowTable.putNumber("Value", get());
        }
    }

    @Override
    public void startLiveWindowMode() {
        liveWindowTableListener = (itable, key, value, bln) -> setPosition((Double) value);
        liveWindowTable.addTableListener("Value", liveWindowTableListener, true);
    }

    @Override
    public void stopLiveWindowMode() {
        // TODO: Broken, should only remove the listener from "Value" only.
        liveWindowTable.removeTableListener(liveWindowTableListener);
    }

}
