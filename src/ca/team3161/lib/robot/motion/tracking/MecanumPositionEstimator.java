/*
 * Copyright (c) 2015, FRC3161.
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

package ca.team3161.lib.robot.motion.tracking;

import ca.team3161.lib.robot.utils.ChassisParameters;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;

public class MecanumPositionEstimator extends AbstractPositionEstimator {
    public MecanumPositionEstimator(final ChassisParameters chassisParameters,
                                    final Accelerometer accelerometer, final Gyro gyro,
                                    final Encoder frontLeftEncoder, final Encoder frontRightEncoder,
                                    final Encoder backLeftEncoder, final Encoder backRightEncoder) {
        super(chassisParameters, accelerometer, gyro, frontLeftEncoder, frontRightEncoder, backLeftEncoder, backRightEncoder);
    }

    @Override
    protected void updateSteerSpecificParameters() {
        w1 = frontRightEncoder.getRate();
        w2 = frontLeftEncoder.getRate();
        w3 = backRightEncoder.getRate();
        w4 = backLeftEncoder.getRate();

        vx = (w1 + w2 + w3 + w4) / 4;
        vy = (w1 - w2 + w3 - w4) / 4;
        dw = (w1 - w2 - w3 + w4) / 2 / (chassisParameters.getWheelBaseLength() + chassisParameters.getWheelBaseWidth());
    }
}
