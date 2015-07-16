/*
 * Copyright (c) 2015-2015, FRC3161.
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

package ca.team3161.lib.robot.subsystem;

import java.util.concurrent.Future;

/**
 * An interface for defining structures which require use of some (physical) resources,
 * and which performs a specific task with these resources, either once or periodically.
 */
public interface Subsystem {

    /**
     * Define a resource which this Subsystem requires exclusive access to while its
     * task runs.
     * @param resource the required resource.
     */
    void require(Object resource);

    /**
     * Check if this subsystem's task has been cancelled.
     * @return true iff cancelled.
     */
    boolean isCancelled();

    /**
     * Check if this subsystem's task has been started.
     * @return true iff started.
     */
    boolean isStarted();

    /**
     * Check if this subsystem's task is scheduled to execute.
     * @return true iff scheduled.
     */
    boolean isScheduled();

    /**
     * Check if this subsystem's task has been completed. This is never
     * true for repeating subsystems, which are always "Not Started",
     * "Scheduled", or "Cancelled".
     * @return true iff completed.
     */
    boolean isDone();

    /**
     * Stop this Subsystem's background task. If this is a one-shot subsystem,
     * the task will never be run if it has not already started. If it has already
     * started, the task may be cancelled partway through execution. If it has already
     * completed, this has no effect. For repeating subsystems, tasks in progress
     * may be cancelled partway through execution, and in any case, future tasks
     * will no longer be run.
     */
    void cancel();

    /**
     * Start (or restart) this Subsystem's background task.
     */
    void start();

    /**
     * Define the set of resourceLocks required for this Subsystem's task.
     * @see AbstractPooledSubsystem#require(Object)
     */
    void defineResources();

    /**
     * The background task to run.
     * @throws Exception in case the defined task throws any Exceptions
     */
    void task() throws Exception;

    /**
     * Get the job representing the execution of this Subsystem's task.
     * @return the Future job.
     */
    Future<?> getJob();
}
