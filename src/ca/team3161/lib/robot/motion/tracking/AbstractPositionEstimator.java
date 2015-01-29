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

import ca.team3161.lib.robot.RepeatingSubsystem;
import ca.team3161.lib.robot.motion.Position;
import ca.team3161.lib.robot.utils.ChassisParameters;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;

import java.util.concurrent.TimeUnit;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public abstract class AbstractPositionEstimator extends RepeatingSubsystem {

    public static final double GRAVITATIONAL_ACCELERATION = 9.80665;
    /* global co-ordinates */
    protected double x, y, theta, theta0;
    /* robot co-ordinates */
    protected double dx, dy, dtheta;
    /* previous co-ordinates */
    protected double px, py, ptheta, pAx, pAy;
    /* predictions */
    protected double mudx, mudy, mudtheta;

    protected long time, pt, dt;
    protected double vx, vy, dw;
    protected double e1, e2, e3;

    protected final ChassisParameters chassisParameters;
    protected final Accelerometer accelerometer;
    protected final Gyro gyro;
    protected final Encoder frontLeftEncoder, frontRightEncoder, backLeftEncoder, backRightEncoder;

    protected double w1, w2, w3, w4;

    public AbstractPositionEstimator(final ChassisParameters chassisParameters,
                                     final Accelerometer accelerometer, final Gyro gyro,
                                     final Encoder frontLeftEncoder, final Encoder frontRightEncoder,
                                     final Encoder backLeftEncoder, final Encoder backRightEncoder) {
        super(10, TimeUnit.MILLISECONDS);
        this.chassisParameters = chassisParameters;
        this.accelerometer = accelerometer;
        this.gyro = gyro;
        this.frontLeftEncoder = frontLeftEncoder;
        this.frontRightEncoder = frontRightEncoder;
        this.backLeftEncoder = backLeftEncoder;
        this.backRightEncoder = backRightEncoder;

        final double distancePerPulse = 2 * Math.PI * chassisParameters.getWheelRadius() / chassisParameters.getEncoderCPR();
        frontLeftEncoder.setDistancePerPulse(distancePerPulse);
        frontRightEncoder.setDistancePerPulse(distancePerPulse);
        backLeftEncoder.setDistancePerPulse(distancePerPulse);
        backRightEncoder.setDistancePerPulse(distancePerPulse);
    }

    public void setInitialTheta(final double theta0) {
        this.theta0 = theta0;
    }

    @Override
    protected void defineResources() {
        require(accelerometer);
        require(gyro);
        require(frontLeftEncoder);
        require(frontRightEncoder);
        require(backLeftEncoder);
        require(backRightEncoder);
    }

    @Override
    protected void task() throws Exception {
        updateTime();
        updateSteerSpecificParameters();
        updateEstimate();
    }

    abstract protected void updateSteerSpecificParameters();

    protected void updateTime() {
        time = System.nanoTime();
        dt = time - pt;
        pt = time;
    }

    protected void updateEstimate() {
        mudtheta = dw;
        mudx = vx;
        mudy = vy;

        pAx = pAx + accelerometer.getX() * dt * GRAVITATIONAL_ACCELERATION;
        pAy = pAy + accelerometer.getY() * dt * GRAVITATIONAL_ACCELERATION;

        e1 = pAx - mudx;
        e2 = pAy - mudy;
        e3 = gyro.getRate() - mudtheta;

        dx = mudx + 0.1 * e1;
        dy = mudy + 0.1 * e2;
        dtheta = mudtheta + 0.5 * e3;

        theta = ptheta + dtheta * dt;
        x = px + dx * dt * cos(theta) + dy * dt * sin(theta);
        y = py + dx * dt * sin(theta) + dy * dt * cos(theta);
    }

    public Position getEstimate() {
        return new Position(x, y, theta);
    }
}
