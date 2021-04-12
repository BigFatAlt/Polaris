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

import com.google.gson.JsonObject;
import com.rammelkast.polaris.Polaris;
import com.rammelkast.polaris.entity.human.Player;
import com.rammelkast.polaris.task.Task;

import us.myles.ViaVersion.ViaManager;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaAPI;
import us.myles.ViaVersion.api.ViaVersionConfig;
import us.myles.ViaVersion.api.command.ViaCommandSender;
import us.myles.ViaVersion.api.configuration.ConfigurationProvider;
import us.myles.ViaVersion.api.platform.TaskId;
import us.myles.ViaVersion.api.platform.ViaConnectionManager;
import us.myles.ViaVersion.api.platform.ViaPlatform;

public final class PolarisViaPlatform implements ViaPlatform<Player> {

	private final PolarisViaAPI api;
	private final PolarisViaConfig config;
	private final Logger logger;
	
	private final ViaConnectionManager connectionManager = new ViaConnectionManager();

	public PolarisViaPlatform() {
		this.api = new PolarisViaAPI();
		this.config = new PolarisViaConfig();
		this.logger = Logger.getLogger("Polaris");
		{
			logger.setLevel(Level.WARNING);
		}
	}

	public void init() {
		Via.init(ViaManager.builder().platform(this).loader(new PolarisViaLoader()).injector(new PolarisViaInjector())
				.build());
		Via.getManager().init();
	}

	@Override
	public void cancelTask(final TaskId taskId) {
		((Task) taskId.getObject()).cancel();
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
    public ViaConnectionManager getConnectionManager() {
        return this.connectionManager;
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
		return "3.2.2-SNAPSHOT";
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
	public TaskId runAsync(final Runnable task) {
		throw new UnsupportedOperationException();
	}

	@Override
	public TaskId runRepeatingSync(final Runnable task, final Long delay) {
		// Delay * 20L -> ticks to milliseconds
		final Task schedule = Polaris.getServer().getSchedulerManager().buildTask(task)
				.repeat(delay * 20L, TimeUnit.MILLISECONDS).schedule();
		return () -> schedule;
	}

	@Override
	public TaskId runSync(final Runnable task) {
		final Task schedule = Polaris.getServer().getSchedulerManager().buildTask(task).schedule();
		return () -> schedule;
	}

	@Override
	public TaskId runSync(final Runnable task, final Long delay) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void sendMessage(final UUID uniqueId, final String message) {
		throw new UnsupportedOperationException();
	}

}
