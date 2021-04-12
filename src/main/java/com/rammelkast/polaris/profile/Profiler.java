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

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.Getter;

public final class Profiler {

	private final Runtime runtime = Runtime.getRuntime();
	@Getter
	private final ThreadBenchmarker threadBenchmarker = new ThreadBenchmarker();
	
	public void start() {
		this.threadBenchmarker.enable();
	}
	
	public void stop() {
		this.threadBenchmarker.disable();
	}
	
	public void addThread(final String name) {
		synchronized (this.threadBenchmarker.threads) {
			this.threadBenchmarker.threads.add(name);
		}
	}
	
	public void removeThread(final String name) {
		synchronized (this.threadBenchmarker.threads) {
			this.threadBenchmarker.threads.remove(name);
		}
	}
	
	/**
	 * Gets the total memory used for this VM in megabytes
	 *  
	 * @return total memory used
	 */
	public BigDecimal getMemoryUsed() {
		final long memoryUsed = this.runtime.totalMemory() - this.runtime.freeMemory();
		return new BigDecimal(((float) (memoryUsed) / (1024 * 1024))).setScale(1, RoundingMode.HALF_UP);
	}
	
	/**
	 * Gets the total available memory for this VM in megabytes
	 *  
	 * @return total available memory
	 */
	public BigDecimal getTotalMemory() {
		final long totalMemory = this.runtime.totalMemory();
		return new BigDecimal(((float) (totalMemory) / (1024 * 1024))).setScale(1, RoundingMode.HALF_UP);
	}
	
}
