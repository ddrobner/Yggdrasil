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

import static java.util.Objects.requireNonNull;

import ca.team3161.lib.robot.subsystem.RepeatingPooledSubsystem;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.SpeedController;
import java.util.concurrent.TimeUnit;

/**
 * A Drivetrain controller that uses PID objects and is able to accurately drive straight and turn by degrees.
 */
public final class PIDDrivetrain extends RepeatingPooledSubsystem {

    public static final long SUBSYSTEM_TASK_PERIOD = 20L;
    private final SpeedController leftDrive, rightDrive;
    private final PID<? extends Encoder, Integer> leftEncoder, rightEncoder;
    private final PID<? extends Gyro, Float> turningPid, bearingPid;
    private volatile float turningDegreesTarget = 0.0f;
    private volatile int leftTicksTarget = 0, rightTicksTarget = 0;
    private Task t;
    private final Object notifier;

    /**
     * The task defining the action of driving straight forward (or backward).
     */
    private final Task driveTask = new DriveTask();

    /**
     * The task defining the action of turning in place.
     */
    private final Task turnTask = new TurnTask();

    /**
     * Create a new PIDDrivetrain instance.
     *
     * @param leftDrive    the left side drivetrain SpeedController
     * @param rightDrive   the right side drivetrain SpeedController
     * @param leftEncoder  the left side drivetrain Encoder
     * @param rightEncoder the right side drivetrain Encoder
     * @param turningPid   an AnglePidSrc (eg Gyro) to maintain a straight heading
     * @param bearingPid   an AnglePidSrc to orient to a vector while stationary
     */
    public PIDDrivetrain(final SpeedController leftDrive, final SpeedController rightDrive,
                         final PID<? extends Encoder, Integer> leftEncoder, final PID<? extends Encoder, Integer> rightEncoder,
                         final PID<? extends Gyro, Float> turningPid, final PID<? extends Gyro, Float> bearingPid) {
        super(SUBSYSTEM_TASK_PERIOD, TimeUnit.MILLISECONDS);
        this.leftDrive = requireNonNull(leftDrive);
        this.rightDrive = requireNonNull(rightDrive);
        this.leftEncoder = requireNonNull(leftEncoder);
        this.rightEncoder = requireNonNull(rightEncoder);
        this.turningPid = requireNonNull(turningPid);
        this.bearingPid = requireNonNull(bearingPid);
        this.notifier = new Object();
    }

    /**
     * Require the SpeedControllers and PID objects.
     */
    public void defineResources() {
        require(leftDrive);
        require(rightDrive);
        require(leftEncoder);
        require(rightEncoder);
        require(turningPid);
        require(bearingPid);
    }

    /**
     * Turn in place.
     * Positive degrees may be either clockwise or anticlockwise, depending on
     * the setup of your particular AnglePidSrc
     *
     * @param degrees how many degrees to turn
     */
    public void turnByDegrees(final float degrees) {
        setTask(turnTask);
        turningDegreesTarget = degrees;
    }

    /**
     * Drive forward a number of encoder ticks.
     *
     * @param ticks how many ticks to drive
     */
    public void setTicksTarget(final int ticks) {
        setTask(driveTask);
        leftTicksTarget = -ticks;
        rightTicksTarget = -ticks;
    }

    /**
     * Change the Task of this PIDDrivetrain.
     *
     * @param t the task type to switch to
     */
    private void setTask(final Task t) {
        this.t = requireNonNull(t);
        leftEncoder.clear();
        rightEncoder.clear();
        turningPid.clear();
        bearingPid.clear();
    }

    /**
     * Reset the state of the drivetrain so that it can be cleanly reused.
     */
    public void reset() {
        leftTicksTarget = 0;
        rightTicksTarget = 0;
        turningDegreesTarget = 0.0f;
        leftEncoder.clear();
        rightEncoder.clear();
        turningPid.clear();
        bearingPid.clear();
    }

    /**
     * Iteratively PID loop.
     */
    public void task() {
        t.run();
    }

    /**
     * Suspends the calling thread until the target is reached, at which point it will be awoken again.
     *
     * @throws InterruptedException if the calling thread is interrupted while waiting
     */
    public void waitForTarget() throws InterruptedException {
        synchronized (notifier) {
            notifier.wait();
        }
    }

    /**
     * An action this PIDDrivetrain may carry out.
     */
    public abstract class Task implements Runnable {
    }

    private class DriveTask extends Task {
        @Override
        public void run() {
            final double skew = bearingPid.pid(0.0f);
            leftDrive.set(leftEncoder.pid(leftTicksTarget) + skew);
            rightDrive.set(rightEncoder.pid(rightTicksTarget) - skew);
            if (leftEncoder.atTarget() || rightEncoder.atTarget()) {
                synchronized (notifier) {
                    notifier.notifyAll();
                }
            }

            if (bearingPid.atTarget()) {
                bearingPid.clear();
            }

            if (leftEncoder.atTarget()) {
                leftEncoder.clear();
            }

            if (rightEncoder.atTarget()) {
                rightEncoder.clear();
            }
        }
    }

    private class TurnTask extends Task {
        @Override
        public void run() {
            final double pidVal = turningPid.pid(turningDegreesTarget);
            leftDrive.set(pidVal);
            rightDrive.set(-pidVal);
            if (turningPid.atTarget()) {
                synchronized (notifier) {
                    notifier.notifyAll();
                }
                turningPid.clear();
            }
        }
    }

}
