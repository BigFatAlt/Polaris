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

import com.rammelkast.polaris.entity.human.Player;
import com.rammelkast.polaris.net.packet.Packet;
import com.rammelkast.polaris.net.packet.PacketWrapper;

import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class PacketOutSpawnPlayer extends Packet {

	private final Player player;

	@Override
	public byte getId() {
		return 0x0C;
	}

	@Override
	public void read(final PacketWrapper wrapper) {
		throw new IllegalAccessError("Packet cannot be read");
	}

	@Override
	public void write(final PacketWrapper wrapper) {
		final ByteBuf buffer = wrapper.getBuffer();
		wrapper.writeVarInt(this.player.getEntityId());
		wrapper.writeUUID(this.player.getUniqueId());
		buffer.writeInt((int) (this.player.getLocation().getX() * 32));
		buffer.writeInt((int) (this.player.getLocation().getY() * 32));
		buffer.writeInt((int) (this.player.getLocation().getZ() * 32));
		buffer.writeByte(getYawAngle());
		buffer.writeByte(getPitchAngle());
		buffer.writeShort(0); // TODO item in hand
		this.player.getMetadata().write(wrapper);
	}
	
	private byte getYawAngle() {
		final float f = this.player.getLocation().getYaw() % 360;
        return (byte) ((f / 360f) * 256f);
	}
	
	private byte getPitchAngle() {
		final float f = this.player.getLocation().getPitch() % 360f;
        return (byte) ((f / 360f) * 256f);
	}

}
