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
package com.rammelkast.polaris.viaversion;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.rammelkast.polaris.Polaris;
import com.rammelkast.polaris.entity.human.Player;
import com.rammelkast.polaris.task.Task;
import com.rammelkast.polaris.task.ViaTask;
import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.configuration.ConfigurationProvider;
import com.viaversion.viaversion.api.configuration.ViaVersionConfig;
import com.viaversion.viaversion.api.platform.PlatformTask;
import com.viaversion.viaversion.api.platform.ViaPlatform;
import com.viaversion.viaversion.libs.gson.JsonObject;

public final class PolarisViaPlatform implements ViaPlatform<Player> {

	private final PolarisViaAPI api;
	private final PolarisViaConfig config;
	private final Logger logger;

	public PolarisViaPlatform() {
		this.api = new PolarisViaAPI();
		this.config = new PolarisViaConfig();
		this.logger = Logger.getLogger("Polaris");
		{
			logger.setLevel(Level.WARNING);
		}
	}

	public void init() {
		Via.init(ViaManagerImpl.builder().platform(this).loader(new PolarisViaLoader()).injector(new PolarisViaInjector())
				.build());
	}

	@Override
	public ViaAPI<Player> getApi() {
		return this.api;
	}

	@Override
	public ViaVersionConfig getConf() {
		return this.config;
	}

	@Override
	public ConfigurationProvider getConfigurationProvider() {
		throw new UnsupportedOperationException();
	}

	@Override
	public File getDataFolder() {
		return null;
	}

	@Override
	public JsonObject getDump() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger("Polaris");
	}

	@Override
	public ViaCommandSender[] getOnlinePlayers() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getPlatformName() {
		return "Polaris";
	}

	@Override
	public String getPlatformVersion() {
		// TODO read
		return "dev";
	}

	@Override
	public String getPluginVersion() {
		return "4.0.2-SNAPSHOT";
	}

	@Override
	public boolean isOldClientsAllowed() {
		return true;
	}

	@Override
	public boolean isPluginEnabled() {
		// TODO config
		return true;
	}

	@Override
	public boolean kickPlayer(final UUID uniqueId, final String message) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onReload() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void sendMessage(final UUID uniqueId, final String message) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public PlatformTask<Task> runSync(final Runnable runnable) {
		return runSync(runnable, 0L);
	}

	@Override
	public PlatformTask<Task> runSync(final Runnable runnable, final long ticks) {
		return new ViaTask(
				Polaris.getServer().getSchedulerManager().buildTask(runnable).delay(ticks * 50L, TimeUnit.MILLISECONDS).schedule());
	}

	@Override
	public PlatformTask<Task> runRepeatingSync(final Runnable runnable, final long ticks) {
		return new ViaTask(
				Polaris.getServer().getSchedulerManager().buildTask(runnable).repeat(ticks * 50L, TimeUnit.MILLISECONDS).schedule());
	}

	@Override
	public PlatformTask<Task> runAsync(final Runnable runnable) {
		throw new UnsupportedOperationException();
	}

}
