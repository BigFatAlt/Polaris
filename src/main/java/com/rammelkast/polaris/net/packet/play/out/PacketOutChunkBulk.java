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
import com.rammelkast.polaris.net.packet.play.out.PacketOutChunkData.ChunkDataMessage;

import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class PacketOutChunkBulk extends Packet {

	private final ChunkDataMessage[] bulk;
	private final boolean skylight;
	
	@Override
	public byte getId() {
		return 0x26;
	}

	@Override
	public void read(final PacketWrapper wrapper) {
		throw new IllegalAccessError("Packet cannot be read");
	}

	@Override
	public void write(final PacketWrapper wrapper) {
		final ByteBuf buffer = wrapper.getBuffer();
		buffer.writeBoolean(this.skylight);
		wrapper.writeVarInt(this.bulk.length);
		
		for (ChunkDataMessage chunk : this.bulk) {
			buffer.writeInt(chunk.getX());
			buffer.writeInt(chunk.getZ());
			buffer.writeShort(chunk.getPrimaryMask());
		}
		
		for (ChunkDataMessage chunk : this.bulk) {
			buffer.writeBytes(chunk.getData());
		}
	}
	
}
