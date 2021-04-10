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
package com.rammelkast.polaris;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rammelkast.polaris.net.NetServer;
import com.rammelkast.polaris.world.World;

import lombok.Getter;

public final class Polaris {

	// TODO config
	public static final int WORLD_SIZE    = 32;
	public static final int VIEW_DISTANCE = 5;
	public static final int NETWORK_PORT  = 25565;
	
	private static final Logger LOGGER = LogManager.getLogger(Polaris.class);
	
	@Getter
	private static Polaris server;
	
	@Getter
	private final World world;
	private final NetServer netServer;
	
	private boolean running;
	
	public Polaris() {
		if (Polaris.server != null) {
			throw new IllegalStateException("Cannot initialize Polaris twice");
		}
		
		LOGGER.info("Loading Polaris b1");
		
		final long ramStart = Runtime.getRuntime().freeMemory();
		this.world = new World("world", WORLD_SIZE);
		final long ramEnd = Runtime.getRuntime().freeMemory();
		LOGGER.info("World is using " + new BigDecimal(((float) (ramStart - ramEnd) / (1024 * 1024))).setScale(1, RoundingMode.HALF_UP) + " MB of memory");
		
		this.netServer = new NetServer(NETWORK_PORT);
		
		Polaris.server = this;
	}
	
	public void start() {
		this.netServer.start();
		
		this.running = true;
		LOGGER.info("Startup complete.");
	}
	
	public void shutdown() {
		if (!this.running) {
			throw new IllegalStateException("Cannot shut down twice");
		}
		
		this.running = false;
		this.world.destroy();
		this.netServer.shutdown();
		System.gc();
	}
	
}
