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

import java.util.SortedSet;
import java.util.UUID;

import com.rammelkast.polaris.entity.human.Player;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.legacy.LegacyViaAPI;
import com.viaversion.viaversion.api.protocol.version.ServerProtocolVersion;
import com.viaversion.viaversion.legacy.LegacyAPI;

import io.netty.buffer.ByteBuf;

public final class PolarisViaAPI implements ViaAPI<Player> {

	private final LegacyAPI<Player> legacy = new LegacyAPI<>();
	
	@Override
	public int getPlayerVersion(Player player) {
		return getPlayerVersion(player.getUniqueId());
	}

	@Override
	public int getPlayerVersion(final UUID uuid) {
		if (!isInjected(uuid)) {
			return 47;
		}
		return Via.getManager().getConnectionManager().getConnectedClient(uuid).getProtocolInfo().getProtocolVersion();
	}

	@Override
	public SortedSet<Integer> getSupportedVersions() {
		return getFullSupportedVersions();
	}

	@Override
	public String getVersion() {
		return Via.getPlatform().getPluginVersion();
	}

	@Override
	public boolean isInjected(final UUID uuid) {
		return Via.getManager().getConnectionManager().isClientConnected(uuid);
	}

	@Override
	public void sendRawPacket(final Player player, final ByteBuf buffer) {
		sendRawPacket(player.getUniqueId(), buffer);
	}

	@Override
	public void sendRawPacket(final UUID uuid, final ByteBuf buffer) {
		if (!isInjected(uuid)) {
			throw new IllegalArgumentException("This player is not controlled by ViaVersion!");
		}
		final UserConnection user = Via.getManager().getConnectionManager().getConnectedClient(uuid);
		user.sendRawPacket(buffer);
		throw new UnsupportedOperationException();
	}

	@Override
	public ServerProtocolVersion getServerVersion() {
		return Via.getManager().getProtocolManager().getServerProtocolVersion();
	}

	@Override
	public UserConnection getConnection(final UUID uuid) {
		return Via.getManager().getConnectionManager().getConnectedClient(uuid);
	}

	@Override
	public SortedSet<Integer> getFullSupportedVersions() {
		return Via.getManager().getProtocolManager().getSupportedVersions();
	}

	@Override
	public LegacyViaAPI<Player> legacyAPI() {
		return this.legacy;
	}

}
