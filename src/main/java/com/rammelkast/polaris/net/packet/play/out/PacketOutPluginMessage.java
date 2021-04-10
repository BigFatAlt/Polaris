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

import com.rammelkast.polaris.net.packet.Packet;
import com.rammelkast.polaris.net.packet.PacketWrapper;

import io.netty.buffer.Unpooled;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class PacketOutPluginMessage extends Packet {

	private final String channel;
	private final byte[] data;

	public PacketOutPluginMessage(final String channel, final String message) {
		this.channel = channel;
		final PacketWrapper temp = new PacketWrapper(Unpooled.buffer(), null);
		{
			temp.writeString(message);
			this.data = temp.getBuffer().array();
			temp.getBuffer().release();
		}
	}
	
	@Override
	public byte getId() {
		return 0x3F;
	}

	@Override
	public void read(final PacketWrapper wrapper) {
		throw new IllegalAccessError("Packet cannot be written");
	}

	@Override
	public void write(final PacketWrapper wrapper) {
		wrapper.writeString(this.channel);
		wrapper.writeBytes(this.data);
	}

}
