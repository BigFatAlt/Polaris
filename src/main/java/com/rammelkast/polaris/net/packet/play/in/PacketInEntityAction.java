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

import lombok.Getter;

@Getter
public final class PacketInEntityAction extends Packet {

	private int entityId;
	private EntityAction action;
	private int parameter;
	
	@Override
	public byte getId() {
		return 0x0B;
	}

	@Override
	public void read(final PacketWrapper wrapper) {
		this.entityId = wrapper.readVarInt();
		final int actionId = wrapper.readVarInt();
		{
			if (actionId < 0 || actionId > EntityAction.values().length - 1) {
				wrapper.getClient().disconnect("Invalid packet", new IllegalStateException("Invalid packet"));
			}
			this.action = EntityAction.values()[actionId];
		}
		this.parameter = wrapper.readVarInt();
	}

	@Override
	public void write(final PacketWrapper wrapper) {
		throw new IllegalAccessError("Packet cannot be written");
	}

	@Override
	public void onReceive(final NetClient client) {
		client.getPlayer().handleEntityAction(this);
	}
	
	public enum EntityAction {
		START_SNEAKING,
		STOP_SNEAKING,
		LEAVE_BED,
		START_SPRINTING,
		STOP_SPRINTING,
		JUMP_WITH_HORSE,
		OPEN_RIDDEN_HORSE_INVENTORY
	}
	
}
