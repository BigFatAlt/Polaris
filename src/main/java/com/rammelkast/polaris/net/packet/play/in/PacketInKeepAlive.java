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
package com.rammelkast.polaris.net.packet.play.in;

import com.rammelkast.polaris.net.NetClient;
import com.rammelkast.polaris.net.packet.Packet;
import com.rammelkast.polaris.net.packet.PacketWrapper;
import com.rammelkast.polaris.net.packet.play.out.PacketOutPlayerListHeaderFooter;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.chat.ComponentSerializer;

@Getter
public final class PacketInKeepAlive extends Packet {

	private int keepAliveId;

	@Override
	public byte getId() {
		return 0x00;
	}

	@Override
	public void read(final PacketWrapper wrapper) {
		this.keepAliveId = wrapper.readVarInt();
	}

	@Override
	public void write(final PacketWrapper wrapper) {
		throw new IllegalAccessError("Packet cannot be written");
	}

	@Override
	public void onReceive(final NetClient client) {
		if (client.getKeepAliveId() == this.keepAliveId) {
			client.setPing(System.currentTimeMillis() - client.getLastKeepAlive());
			// TODO remove - this is debug
			client.sendPacket(new PacketOutPlayerListHeaderFooter(
					ComponentSerializer
							.toString(new ComponentBuilder("Powered by Polaris").color(ChatColor.GOLD).create()),
					ComponentSerializer.toString(
							new ComponentBuilder("Ping: " + client.getPing() + " ms").color(ChatColor.GRAY).create())));
		} else {
			client.disconnect("Invalid keepalive ID", null);
		}
	}

}
