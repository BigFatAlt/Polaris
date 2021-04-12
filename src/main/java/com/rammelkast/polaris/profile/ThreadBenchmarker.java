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
package com.rammelkast.polaris.profile;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ThreadBenchmarker {

    public static ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

    static {
        assert(threadMXBean.isThreadCpuTimeSupported());
        assert(threadMXBean.isCurrentThreadCpuTimeSupported());

        threadMXBean.setThreadContentionMonitoringEnabled(true);
        threadMXBean.setThreadCpuTimeEnabled(true);
        assert(threadMXBean.isThreadCpuTimeEnabled());
    }

    protected final List<String> threads = new ArrayList<String>();
    private Map<Long, Long> lastCpuTimeMap = new HashMap<Long, Long>();
    private Map<Long, Long> lastUserTimeMap = new HashMap<Long, Long>();
    private Map<Long, Long> lastBlockedMap = new HashMap<Long, Long>();

    private Map<String, ThreadResult> resultMap = new HashMap<>();

    private boolean enabled = false;
    private volatile boolean stop = false;

    private Thread thread;

    private long time;

    public void enable() {
        if (enabled)
            throw new IllegalStateException("A benchmark is already running, please disable it first.");

        this.time = 1000L * 5;

        this.thread = new Thread(null, () -> {

            while (!stop) {
                try {
					refreshData();
				} catch (Exception e) {}

                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            stop = false;

        }, "Thread Benchmarker", 0L);

        this.thread.start();

        this.enabled = true;
    }

    public void disable() {
        this.stop = true;
        this.enabled = false;
    }

    public long getUsedMemory() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    public Map<String, ThreadResult> getResultMap() {
        return Collections.unmodifiableMap(resultMap);
    }

    private void refreshData() throws Exception {
        final ThreadInfo[] threadInfo = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds());
        for (ThreadInfo threadInfo2 : threadInfo) {
        	final String name = threadInfo2.getThreadName();
            boolean shouldBenchmark = false;
            for (String thread : threads) {
                if (name.startsWith(thread)) {
                    shouldBenchmark = true;
                    break;
                }
            }
            if (!shouldBenchmark)
                continue;

            final long id = threadInfo2.getThreadId();

            final long lastCpuTime = lastCpuTimeMap.getOrDefault(id, 0L);
            final long lastUserTime = lastUserTimeMap.getOrDefault(id, 0L);
            final long lastBlockedTime = lastBlockedMap.getOrDefault(id, 0L);

            final long blockedTime = threadInfo2.getBlockedTime();
            //long waitedTime = threadInfo2.getWaitedTime();
            final long cpuTime = threadMXBean.getThreadCpuTime(id);
            final long userTime = threadMXBean.getThreadUserTime(id);

            lastCpuTimeMap.put(id, cpuTime);
            lastUserTimeMap.put(id, userTime);
            lastBlockedMap.put(id, blockedTime);

            final double totalCpuTime = (double) (cpuTime - lastCpuTime) / 1000000D;
            final double totalUserTime = (double) (userTime - lastUserTime) / 1000000D;
            final long totalBlocked = blockedTime - lastBlockedTime;

            final double cpuPercentage = totalCpuTime / (double) time * 100L;
            final double userPercentage = totalUserTime / (double) time * 100L;
            final double blockedPercentage = totalBlocked / (double) time * 100L;

            final ThreadResult threadResult = new ThreadResult(cpuPercentage, userPercentage, blockedPercentage);
            resultMap.put(name, threadResult);
        }
    }
}