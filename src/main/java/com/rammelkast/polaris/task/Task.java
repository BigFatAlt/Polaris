/**
 * Polaris Minecraft Server Software
 * Copyright 2021 Marco Moesman
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rammelkast.polaris.task;

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;

/**
 * An Object that represents a task that is scheduled for execution on the
 * application.
 * <p>
 * Tasks are built in {@link SchedulerManager} and scheduled by a
 * {@link TaskBuilder}.
 */
public final class Task implements Runnable {

	// Manages all tasks
	private final SchedulerManager schedulerManager;
	// The task logic
	private final Runnable runnable;
	// Task identifier
	private final int id;
	// True if the task planned for the application shutdown
	private final boolean shutdown;
	// Delay value for the task execution
	private final long delay;
	// Repeat value for the task execution
	private final long repeat;
	// Task completion/execution
	private ScheduledFuture<?> future;
	// The thread of the task
	private volatile Thread currentThreadTask;

	/**
	 * Creates a task.
	 *
	 * @param schedulerManager The manager for the task
	 * @param runnable         The task to run when scheduled
	 * @param shutdown         Defines whether the task is a shutdown task
	 * @param delay            The time to delay
	 * @param repeat           The time until the repetition
	 */
	public Task(SchedulerManager schedulerManager, Runnable runnable, boolean shutdown, long delay, long repeat) {
		this.schedulerManager = schedulerManager;
		this.runnable = runnable;
		this.shutdown = shutdown;
		this.id = shutdown ? this.schedulerManager.getShutdownCounterIdentifier()
				: this.schedulerManager.getCounterIdentifier();
		this.delay = delay;
		this.repeat = repeat;
	}

	/**
	 * Executes the task.
	 */
	@Override
	public void run() {
		this.schedulerManager.getBatchesPool().execute(() -> {
			this.currentThreadTask = Thread.currentThread();
			try {
				this.runnable.run();
			} catch (Exception e) {
				System.err.printf("An exception in %s task %s is occurred! (%s)%n", this.shutdown ? "shutdown" : "",
						this.id, e.getMessage());
				e.printStackTrace();
			} finally {
				if (this.repeat == 0)
					this.finish();
				this.currentThreadTask = null;
			}
		});
	}

	/**
	 * Executes the internal runnable.
	 * <p>
	 * Should probably use {@link #schedule()} instead.
	 */
	public void runRunnable() {
		this.runnable.run();
	}

	/**
	 * Sets up the task for correct execution.
	 */
	public void schedule() {
		this.future = this.repeat == 0L
				? this.schedulerManager.getTimerExecutionService().schedule(this, this.delay, TimeUnit.MILLISECONDS)
				: this.schedulerManager.getTimerExecutionService().scheduleAtFixedRate(this, this.delay, this.repeat,
						TimeUnit.MILLISECONDS);
	}

	/**
	 * Gets the current status of the task.
	 *
	 * @return the current stats of the task
	 */
	public TaskStatus getStatus() {
		if (this.future == null)
			return TaskStatus.SCHEDULED;
		if (this.future.isCancelled())
			return TaskStatus.CANCELLED;
		if (this.future.isDone())
			return TaskStatus.FINISHED;
		return TaskStatus.SCHEDULED;
	}

	/**
	 * Cancels this task. If the task is already running, the thread in which it is
	 * running is interrupted. If the task is not currently running, Minestom will
	 * safely terminate it.
	 */
	public void cancel() {
		if (this.future != null) {
			this.future.cancel(false);

			Thread current = this.currentThreadTask;
			if (current != null)
				current.interrupt();

			this.finish();
		}
	}

	/**
	 * Gets the id of this task.
	 *
	 * @return the task id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Removes the task from the {@link SchedulerManager} map.
	 */
	private void finish() {
		Int2ObjectMap<Task> taskMap = shutdown ? this.schedulerManager.shutdownTasks : this.schedulerManager.tasks;

		synchronized (taskMap) {
			taskMap.remove(getId());
		}
	}

	@Override
	public boolean equals(Object object) {
		if (this == object)
			return true;
		if (object == null || getClass() != object.getClass())
			return false;
		Task task = (Task) object;
		return id == task.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}