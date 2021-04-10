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

import java.util.HashMap;
import java.util.Map;

import com.rammelkast.polaris.net.NetClient.NetState;
import com.rammelkast.polaris.net.packet.login.in.PacketInLoginStart;
import com.rammelkast.polaris.net.packet.play.in.PacketInAnimation;
import com.rammelkast.polaris.net.packet.play.in.PacketInChatMessage;
import com.rammelkast.polaris.net.packet.play.in.PacketInClientSettings;
import com.rammelkast.polaris.net.packet.play.in.PacketInEntityAction;
import com.rammelkast.polaris.net.packet.play.in.PacketInKeepAlive;
import com.rammelkast.polaris.net.packet.play.in.PacketInPlayerFlying;
import com.rammelkast.polaris.net.packet.play.in.PacketInPlayerLook;
import com.rammelkast.polaris.net.packet.play.in.PacketInPlayerPosition;
import com.rammelkast.polaris.net.packet.play.in.PacketInPlayerPositionLook;
import com.rammelkast.polaris.net.packet.play.in.PacketInPluginMessage;
import com.rammelkast.polaris.net.packet.status.in.PacketInStatusPing;
import com.rammelkast.polaris.net.packet.status.in.PacketInStatusRequest;

public final class PacketManager {

	public static final Map<Integer, Class<? extends Packet>> STATUS = new HashMap<Integer, Class<? extends Packet>>();
	public static final Map<Integer, Class<? extends Packet>> LOGIN = new HashMap<Integer, Class<? extends Packet>>();
	public static final Map<Integer, Class<? extends Packet>> PLAY = new HashMap<Integer, Class<? extends Packet>>();
	
	static {
		// Register packets
		STATUS.put(0x00, PacketInStatusRequest.class);
		STATUS.put(0x01, PacketInStatusPing.class);
		
		LOGIN.put(0x00, PacketInLoginStart.class);
		
		PLAY.put(0x00, PacketInKeepAlive.class);
		PLAY.put(0x0a, PacketInAnimation.class);
		PLAY.put(0x0b, PacketInEntityAction.class);
		PLAY.put(0x01, PacketInChatMessage.class);
		PLAY.put(0x03, PacketInPlayerFlying.class);
		PLAY.put(0x04, PacketInPlayerPosition.class);
		PLAY.put(0x05, PacketInPlayerLook.class);
		PLAY.put(0x06, PacketInPlayerPositionLook.class);
		PLAY.put(0x15, PacketInClientSettings.class);
		PLAY.put(0x17, PacketInPluginMessage.class);
	}
	
	public static Packet fetchPacket(final int id, final NetState state) throws Exception {
		switch (state) {
		case HANDSHAKE: {
			throw new IllegalStateException("Cannot fetch packets in handshake state");
		} case LOGIN: {
			final Class<? extends Packet> packet = LOGIN.get(id);
			if (packet == null) {
				return null;
			}
			return packet.getDeclaredConstructor().newInstance();
		} case STATUS: {
			final Class<? extends Packet> packet = STATUS.get(id);
			if (packet == null) {
				return null;
			}
			return packet.getDeclaredConstructor().newInstance();
		} case PLAY: {
			final Class<? extends Packet> packet = PLAY.get(id);
			if (packet == null) {
				return null;
			}
			return packet.getDeclaredConstructor().newInstance();
		} default:
			throw new IllegalStateException("Illegal net state");
		}
	}
	
}
