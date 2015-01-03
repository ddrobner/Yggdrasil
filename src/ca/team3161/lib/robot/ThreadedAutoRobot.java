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

package ca.team3161.lib.robot;

import edu.wpi.first.wpilibj.IterativeRobot;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A subclass of IterativeRobot. Autonomous is run in a new Thread, leaving the main robot thread
 * responsible (generally) solely for handling FMS events, Watchdog, etc. This allows
 * autonomous scripts to use convenient semantics such as Thread sleeping rather than periodically
 * checking Timer objects.
 */
public abstract class ThreadedAutoRobot extends IterativeRobot {

    private volatile int accumulatedTime = 0;
    private final Lock modeLock = new ReentrantLock();
    private Future<?> autoJob;

    /**
     * At the start of the autonomous period, start a new background task
     * using the behaviour described in the concrete subclass implementation's
     * autonomousThreaded() method.
     * This new background task allows us to use Thread.sleep rather than a timer,
     * while also not disrupting normal background functions of the
     * robot such as feeding the Watchdog or responding to FMS events.
     * modeLock is used to ensure that the robot is never simultaneously
     * executing both autonomous and teleop routines at the same time.
     */
    @Override
    public final void autonomousInit() {
        accumulatedTime = 0;
        autoJob = Executors.newSingleThreadExecutor().submit(() -> {
            try {
                modeLock.lockInterruptibly();
                autonomousThreaded();
            } catch (final Exception e) {
                e.printStackTrace();
            } finally {
                modeLock.unlock();
            }
        });
    }

    /**
     * Add a delay to the autonomous routine.
     * This also ensures that the autonomous routine does not continue
     * to run after the FMS notifies us that the autonomous period
     * has ended.
     * @param millis how many milliseconds to wait for (approximate)
     * @throws InterruptedException if the autonomous thread is woken up early, for any reason
     */
    public final void waitFor(final long millis) throws InterruptedException {
        accumulatedTime += millis;
        if (accumulatedTime > getAutonomousPeriodLengthSeconds()) {
            autoJob.cancel(true);
        }
        Thread.sleep(millis);
        if (!isAutonomous()) {
            autoJob.cancel(true);
        }
    }

    /**
     * Handles running teleopThreadsafe periodically.
     * Do not override this in subclasses, or else there may be no guarantee
     * that the autonomous thread and the main robot thread, executing teleop
     * code, will not attempt to run concurrently.
     */
    @Override
    public final void teleopPeriodic() {
        if (autoJob != null) {
            autoJob.cancel(true);
        }
        modeLock.lock();
        teleopThreadsafe();
        modeLock.unlock();
    }

    /**
     * Called once when the robot enters the teleop mode.
     */
    @Override
    public abstract void teleopInit();

    /**
     * Periodically called during robot teleop mode to enable operator control.
     * This is the only way teleop mode should be handled - do not directly call
     * teleopPeriodic from within this method or unbounded recursion will occur,
     * resulting in a stack overflow and crashed robot code. teleopContinuous
     * is likewise unsupported.
     */
    public abstract void teleopThreadsafe();

    /**
     * The one-shot autonomous "script" to be run in a new Thread.
     * @throws Exception this method failing should never catch the caller unaware - may lead to unpredictable behaviour if so
     */
    public abstract void autonomousThreaded() throws Exception;

    /**
     * Define the length of the Autonomous period, in seconds.
     * @return the length of the Autonomous period, in seconds.
     */
    public abstract int getAutonomousPeriodLengthSeconds();
}
