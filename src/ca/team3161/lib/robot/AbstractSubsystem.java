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

package ca.team3161.lib.robot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.Lock;

/**
 * An abstract implementation of the Subsystem interface.
 *
 * Manages acquiring and releasing required resources and handles starting/stopping
 * tasks. Subclasses are expected to define the semantics of how tasks are run
 * (shared or independent workers, one-shot or repeating, etc).
 */
public abstract class AbstractSubsystem implements Subsystem {

    /**
     * A list of resourceLocks which this Subsystem requires.
     * @see ca.team3161.lib.robot.ResourceTracker
     */
    protected final List<Lock> resourceLocks = new ArrayList<>();

    protected Future<?> job;

    /**
     * Define a required resource for this Subsystem when its task is executed.
     * @param resource a sensor, speed controller, etc. that this subsystem
     * needs exclusive access to during its task
     */
    @Override
    public final void require(final Object resource) {
        Objects.requireNonNull(resource);
        resourceLocks.add(ResourceTracker.track(resource));
    }

    protected final void acquireResources() throws InterruptedException {
        resourceLocks.forEach(Lock::tryLock);
    }

    protected final void releaseResources() {
        resourceLocks.forEach(Lock::unlock);
    }

    /**
     * Get this subsystem's task.
     * @return the current task for this subsystem, if any
     */
    @Override
    public final Future<?> getJob() {
        return job;
    }

    /**
     * Check if this Subsystem's task has been canceled.
     * @return if this Subsystem's background task has been canceled
     */
    @Override
    public final boolean getCancelled() {
        return (getJob() != null && getJob().isCancelled());
    }

    @Override
    public final boolean getStarted() {
        return getJob() != null && (!getJob().isCancelled() && !getJob().isDone());
    }

    /**
     * Cancel the background task of this Subsystem (stop it from running, if it
     * is a recurring task).
     */
    @Override
    public final void cancel() {
        if (getJob() != null) {
            getJob().cancel(true);
        }
    }

    /**
     * The task for this Subsystem to run.
     */
    protected class RunTask implements Runnable {
        @Override
        public void run() {
            try {
                acquireResources();
                task();
            } catch (final Exception e) {
                e.printStackTrace();
            } finally {
                releaseResources();
            }
        }
    }

    /**
     * Get the executor service which executes this subsystem's tasks.
     * This service may or may not be shared with other subsystems;
     * for Independent subsystems it is never shared, and for Pooled
     * subsystems it is always shared.
     * @return the executor service.
     * @see ca.team3161.lib.robot.AbstractIndependentSubsystem
     * @see ca.team3161.lib.robot.AbstractPooledSubsystem
     */
    protected abstract ScheduledExecutorService getExecutorService();

}
