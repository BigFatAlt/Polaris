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
package com.rammelkast.polaris.net.codec;

import java.lang.ref.WeakReference;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rammelkast.polaris.net.NetClient;
import com.rammelkast.polaris.net.NetClient.NetState;
import com.rammelkast.polaris.net.packet.Packet;
import com.rammelkast.polaris.net.packet.PacketHandshake;
import com.rammelkast.polaris.net.packet.PacketManager;
import com.rammelkast.polaris.net.packet.PacketWrapper;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PacketDecoder extends ByteToMessageDecoder {

	private static final Logger LOGGER = LogManager.getLogger(PacketDecoder.class);
	
	private final NetClient client;
	
	@Override
	protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
		final PacketWrapper wrapper = new PacketWrapper(in, new WeakReference<>(this.client));
		while (in.readableBytes() > 0) {
			in.markReaderIndex();
			final int readable = in.readableBytes();
			final int packetLength = wrapper.readVarInt();
			if (readable < packetLength) {
				in.resetReaderIndex();
				LOGGER.warn("Skipped " + readable + " bytes (expected " + packetLength + ")");
				return;
			}
			
			final int packetId = wrapper.readVarInt();
			final NetState state = this.client.getState();
			Packet packet = null;
			try {
				switch (state) {
				case HANDSHAKE: {
					packet = new PacketHandshake();
					packet.read(wrapper);
					final PacketHandshake handshake = (PacketHandshake) packet;
					final int nextState = handshake.getNextState();
					if (nextState < 1 || nextState > 3) {
						ctx.disconnect();
						LOGGER.info("Disconnected {}: illegal handshake", this.client.getAddress());
						break;
					}
					this.client.setState(NetState.values()[nextState]);
					break;
				}
				default: {
					packet = PacketManager.fetchPacket(packetId, state);
					if (packet == null) {
						in.skipBytes(packetLength - this.getVarIntSize(packetId));
						LOGGER.warn("Skipped " + readable + " bytes (unknown packet)");
						throw new UnsupportedOperationException("Client sent unknown packet");
					}
					packet.read(wrapper);
					break;
				}
				}
				out.add(packet);
			} catch (Exception e) {
				throw new RuntimeException("Failed to decode packet:\nID: 0x" + Integer.toHexString(packetId) + "\nSize: " + packetLength + "\nReason: " + e.getMessage());
			}
			in.discardSomeReadBytes();
		}
	}
	
	private int getVarIntSize(final int varInt) {
		if ((varInt & 0xFFFFFF80) == 0) {
			return 1;
		} else if ((varInt & 0xFFFFC000) == 0) {
			return 2;
		} else if ((varInt & 0xFFE00000) == 0) {
			return 3;
		} else if ((varInt & 0xF0000000) == 0) {
			return 4;
		}
		return 5;
	}

}
