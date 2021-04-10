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

import com.rammelkast.polaris.net.NetClient;
import com.rammelkast.polaris.net.packet.Packet;
import com.rammelkast.polaris.net.packet.PacketWrapper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class PacketEncoder extends MessageToByteEncoder<Packet> {

	private final NetClient client;

	@Override
	protected void encode(final ChannelHandlerContext ctx, final Packet packet, final ByteBuf out) throws Exception {
		final PacketWrapper wrapperIn = new PacketWrapper(Unpooled.buffer(), new WeakReference<>(this.client));
		{
			wrapperIn.writeVarInt(packet.getId());
			packet.write(wrapperIn);
		}

		final int packetSize = wrapperIn.getBuffer().readableBytes();
		out.ensureWritable(this.getVarIntSize(packetSize));
		final PacketWrapper wrapperOut = new PacketWrapper(out, new WeakReference<>(this.client));
		{
			wrapperOut.writeVarInt(packetSize);
			wrapperOut.getBuffer().writeBytes(wrapperIn.getBuffer());
		}

		wrapperIn.getBuffer().release();
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
