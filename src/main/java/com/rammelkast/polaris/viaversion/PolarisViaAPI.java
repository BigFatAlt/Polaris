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

import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaAPI;
import us.myles.ViaVersion.api.boss.BossBar;
import us.myles.ViaVersion.api.boss.BossColor;
import us.myles.ViaVersion.api.boss.BossStyle;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.protocol.ProtocolRegistry;

public final class PolarisViaAPI implements ViaAPI<Player> {

	@Override
	public BossBar createBossBar(String arg0, BossColor arg1, BossStyle arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BossBar createBossBar(String arg0, float arg1, BossColor arg2, BossStyle arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPlayerVersion(Player player) {
		return getPlayerVersion(player.getUniqueId());
	}

	@Override
	public int getPlayerVersion(final UUID uuid) {
		if (!isInjected(uuid)) {
			return 47;
		}
		return Via.getManager().getConnection(uuid).getProtocolInfo().getProtocolVersion();
	}

	@Override
	public SortedSet<Integer> getSupportedVersions() {
		return ProtocolRegistry.getSupportedVersions();
	}

	@Override
	public String getVersion() {
		return Via.getPlatform().getPluginVersion();
	}

	@Override
	public boolean isInjected(final UUID uuid) {
		return Via.getManager().isClientConnected(uuid);
	}

	@Override
	public void sendRawPacket(final Player player, final ByteBuf buffer) {
		sendRawPacket(player.getUniqueId(), buffer);
	}

	@Override
	public void sendRawPacket(final UUID uuid, final ByteBuf buffer) {
		if (!isInjected(uuid))
			throw new IllegalArgumentException("This player is not controlled by ViaVersion!");
		final UserConnection user = Via.getManager().getConnection(uuid);
		user.sendRawPacket(buffer);
		throw new UnsupportedOperationException();
	}

}
