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

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;

/**
 * An object which manages all the {@link Task}'s.
 * <p>
 * {@link Task} first need to be built with {@link #buildTask(Runnable)}, you can then specify a delay with as example
 * {@link TaskBuilder#delay(long, net.minestom.server.utils.time.TimeUnit)}
 * or {@link TaskBuilder#repeat(long, net.minestom.server.utils.time.TimeUnit)},
 * and to finally schedule: {@link TaskBuilder#schedule()}.
 * <p>
 * Shutdown tasks are built with {@link #buildShutdownTask(Runnable)} and are executed, as the name implies, when the server stops.
 */
public final class SchedulerManager {
	
	private static final Logger LOGGER = LogManager.getLogger(SchedulerManager.class);

    private static boolean instanced;
    // A counter for all normal tasks
    private final AtomicInteger counter;
    // A counter for all shutdown tasks
    private final AtomicInteger shutdownCounter;
    //A threaded execution
    private final ExecutorService batchesPool;
    // A single threaded scheduled execution
    private final ScheduledExecutorService timerExecutionService;
    // All the registered tasks (task id = task)
    protected final Int2ObjectMap<Task> tasks;
    // All the registered shutdown tasks (task id = task)
    protected final Int2ObjectMap<Task> shutdownTasks;

    /**
     * Default constructor
     */
    public SchedulerManager() {
        if (instanced) {
            throw new IllegalStateException("You cannot instantiate a SchedulerManager," +
                    " use MinecraftServer.getSchedulerManager()");
        }
        SchedulerManager.instanced = true;

        this.counter = new AtomicInteger();
        this.shutdownCounter = new AtomicInteger();

        this.batchesPool = Executors.newCachedThreadPool();
        this.timerExecutionService = Executors.newSingleThreadScheduledExecutor();
        this.tasks = new Int2ObjectOpenHashMap<>();
        this.shutdownTasks = new Int2ObjectOpenHashMap<>();
    }

    /**
     * Initializes a new {@link TaskBuilder} for creating a {@link Task}.
     *
     * @param runnable The {@link Task} to run when scheduled
     * @return the {@link TaskBuilder}
     */

    public TaskBuilder buildTask(Runnable runnable) {
        return new TaskBuilder(this, runnable);
    }

    /**
     * Initializes a new {@link TaskBuilder} for creating a shutdown {@link Task}.
     *
     * @param runnable The shutdown {@link Task} to run when scheduled
     * @return the {@link TaskBuilder}
     */

    public TaskBuilder buildShutdownTask(Runnable runnable) {
        return new TaskBuilder(this, runnable, true);
    }

    /**
     * Removes/Forces the end of a {@link Task}.
     * <p>
     * {@link Task#cancel()} can also be used instead.
     *
     * @param task The {@link Task} to remove
     */
    public void removeTask(Task task) {
        task.cancel();
    }

    /**
     * Shutdowns all normal tasks and call the registered shutdown tasks.
     */
    public void shutdown() {
        LOGGER.info("Executing all shutdown tasks..");
        for (Task task : this.getShutdownTasks()) {
            task.runRunnable();
        }
        LOGGER.info("Shutting down the scheduled execution service and batches pool.");
        this.timerExecutionService.shutdown();
        this.batchesPool.shutdown();
        try {
            batchesPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Increments the current counter value.
     *
     * @return the updated counter value
     */
    protected int getCounterIdentifier() {
        return this.counter.incrementAndGet();
    }

    /**
     * Increments the current shutdown counter value.
     *
     * @return the updated shutdown counter value
     */
    protected int getShutdownCounterIdentifier() {
        return this.shutdownCounter.incrementAndGet();
    }

    /**
     * Gets a {@link Collection} with all the registered {@link Task}.
     * <p>
     * Be aware that the collection is not thread-safe.
     *
     * @return a {@link Collection} with all the registered {@link Task}
     */
    public ObjectCollection<Task> getTasks() {
        return tasks.values();
    }

    /**
     * Gets a {@link Collection} with all the registered shutdown {@link Task}.
     *
     * @return a {@link Collection} with all the registered shutdown {@link Task}
     */
    public ObjectCollection<Task> getShutdownTasks() {
        return shutdownTasks.values();
    }

    /**
     * Gets the execution service for all the registered {@link Task}.
     *
     * @return the execution service for all the registered {@link Task}
     */
    public ExecutorService getBatchesPool() {
        return batchesPool;
    }
    /**
     * Gets the scheduled execution service for all the registered {@link Task}.
     *
     * @return the scheduled execution service for all the registered {@link Task}
     */
    public ScheduledExecutorService getTimerExecutionService() {
        return timerExecutionService;
    }
}