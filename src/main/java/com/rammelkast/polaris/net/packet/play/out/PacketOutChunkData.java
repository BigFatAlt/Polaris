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

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class PacketOutChunkData extends Packet {

	private final ChunkDataMessage chunkData;
	
	@Override
	public byte getId() {
		return 0x21;
	}

	@Override
	public void read(final PacketWrapper wrapper) {
		throw new IllegalAccessError("Packet cannot be read");
	}

	@Override
	public void write(final PacketWrapper wrapper) {
		final ByteBuf buffer = wrapper.getBuffer();
		buffer.writeInt(this.chunkData.getX());
		buffer.writeInt(this.chunkData.getZ());
		buffer.writeBoolean(this.chunkData.isContinuous());
		buffer.writeShort(this.chunkData.getPrimaryMask());
		wrapper.writeBytes(this.chunkData.getData());
	}
	
	@Data
	public static class ChunkDataMessage {
	    private final int x, z;
	    private final boolean continuous;
	    private final int primaryMask;
	    private final byte[] data;

	    public static ChunkDataMessage empty(int x, int z) {
	        return new ChunkDataMessage(x, z, true, 0, new byte[0]);
	    }
	}
	
}
