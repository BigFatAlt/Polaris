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
package com.rammelkast.polaris.net.packet.play.out;

import java.util.Set;

import com.rammelkast.polaris.entity.human.Player;
import com.rammelkast.polaris.net.packet.Packet;
import com.rammelkast.polaris.net.packet.PacketWrapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class PacketOutPlayerListItem extends Packet {

	private final int action;
	private final Set<Player> players;

	@Override
	public byte getId() {
		return 0x38;
	}

	@Override
	public void read(final PacketWrapper wrapper) {
		throw new IllegalAccessError("Packet cannot be read");
	}

	@Override
	public void write(final PacketWrapper wrapper) {
		wrapper.writeVarInt(this.action);
		wrapper.writeVarInt(this.players.size());
		this.players.forEach(player -> {
			wrapper.writeUUID(player.getUniqueId());
			wrapper.writeString(player.getName());
			// TODO gameprofile
			wrapper.writeVarInt(0);
			wrapper.writeVarInt(player.getGameMode().getValue());
			wrapper.writeVarInt((int) player.getPing());
			wrapper.getBuffer().writeBoolean(false);
		});
	}

}
