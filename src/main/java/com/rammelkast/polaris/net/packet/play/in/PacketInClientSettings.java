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

import com.rammelkast.polaris.entity.human.Player.ClientSettings;
import com.rammelkast.polaris.net.NetClient;
import com.rammelkast.polaris.net.packet.Packet;
import com.rammelkast.polaris.net.packet.PacketWrapper;

import io.netty.buffer.ByteBuf;
import lombok.Getter;

@Getter
public final class PacketInClientSettings extends Packet {

	private String locale;
	private byte viewDistance;
	private byte chatMode;
	private boolean chatColors;
	private byte displayedSkinParts;
	
	@Override
	public byte getId() {
		return 0x15;
	}

	@Override
	public void read(final PacketWrapper wrapper) {
		final ByteBuf buffer = wrapper.getBuffer();
		this.locale = wrapper.readString();
		this.viewDistance = buffer.readByte();
		this.chatMode = buffer.readByte();
		this.chatColors = buffer.readBoolean();
		this.displayedSkinParts = buffer.readByte();
	}

	@Override
	public void write(final PacketWrapper wrapper) {
		throw new IllegalAccessError("Packet cannot be written");
	}

	@Override
	public void onReceive(final NetClient client) {
		client.getPlayer().updateClientSettings(new ClientSettings(this.locale, this.viewDistance, this.chatMode, this.chatColors, this.displayedSkinParts));
	}
	
}
