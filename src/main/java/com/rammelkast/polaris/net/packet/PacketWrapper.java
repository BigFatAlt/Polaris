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
package com.rammelkast.polaris.net.packet;

import java.lang.ref.Reference;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import com.rammelkast.polaris.net.NetClient;
import com.rammelkast.polaris.util.Location;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class PacketWrapper {

	@Getter
	private final ByteBuf buffer;
	private final Reference<NetClient> client;
	
	public NetClient getClient() {
		return this.client.get();
	}

	public void writeVarInt(int value) {
		do {
			byte temp = (byte) (value & 0b01111111);
			value >>>= 7;
			if (value != 0) {
				temp |= 0b10000000;
			}
			buffer.writeByte(temp);
		} while (value != 0);
	}

	public int readVarInt() {
		int numRead = 0;
		int result = 0;
		byte read;
		do {
			read = buffer.readByte();

			final int value = (read & 0b01111111);
			result |= (value << (7 * numRead));

			numRead++;
			if (numRead > 5) {
				throw new RuntimeException("VarInt too big");
			}
		} while ((read & 0b10000000) != 0);
		return result;
	}

	public String readString() {
		final int length = this.readVarInt();
		byte[] content = new byte[length];
		{
			this.buffer.readBytes(content);
		}
		return new String(content, StandardCharsets.UTF_8);
	}

	public void writeString(final String value) {
		final byte[] content = value.getBytes(StandardCharsets.UTF_8);
		this.writeBytes(content);
	}

	public void writeBytes(final byte[] content) {
		this.writeVarInt(content.length);
		this.buffer.writeBytes(content);
	}

	public void writeLocation(final Location location) {
		final long x = (long) location.getX();
		final long y = (long) location.getY();
		final long z = (long) location.getZ();
		this.buffer.writeLong(((x & 0x3FFFFFF) << 38) | ((y & 0xFFF) << 26) | (z & 0x3FFFFFF));
	}
	
	public void writeUUID(final UUID uuid) {
        this.buffer.writeLong(uuid.getMostSignificantBits());
        this.buffer.writeLong(uuid.getLeastSignificantBits());
    }
}
