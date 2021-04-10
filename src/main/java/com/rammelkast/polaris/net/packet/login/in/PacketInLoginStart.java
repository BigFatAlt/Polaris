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
package com.rammelkast.polaris.net.packet.login.in;

import java.util.UUID;

import com.rammelkast.polaris.net.NetClient;
import com.rammelkast.polaris.net.packet.Packet;
import com.rammelkast.polaris.net.packet.PacketWrapper;

import lombok.Getter;

@Getter
public final class PacketInLoginStart extends Packet {

	private String username;

	@Override
	public byte getId() {
		return 0x00;
	}

	@Override
	public void read(final PacketWrapper wrapper) {
		this.username = wrapper.readString();
	}

	@Override
	public void write(final PacketWrapper wrapper) {
		throw new IllegalAccessError("Packet cannot be written");
	}

	@Override
	public void onReceive(final NetClient client) {
		try {
			client.login(this.username, UUID.randomUUID());
		} catch (Exception ex) {
			client.disconnect("Failed to login", ex);
		}
	}

}
