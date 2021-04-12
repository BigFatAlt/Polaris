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

import java.util.List;

import com.rammelkast.polaris.net.packet.PacketWrapper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.CorruptedFrameException;

public final class PacketFramer extends ByteToMessageCodec<ByteBuf> {

	@Override
	protected void encode(final ChannelHandlerContext ctx, final ByteBuf msg, final ByteBuf out) throws Exception {
		final PacketWrapper wrapper = new PacketWrapper(out, null);
		int bodyLength = msg.readableBytes();
		int headerLength = getVarIntSize(bodyLength);
		out.ensureWritable(headerLength + bodyLength);

		wrapper.writeVarInt(bodyLength);
		out.writeBytes(msg);
	}

	@Override
	protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
		in.markReaderIndex();
        final byte[] buf = new byte[3];
        for (int i = 0; i < buf.length; i++) {
            if (!in.isReadable()) {
                in.resetReaderIndex();
                return;
            }

            buf[i] = in.readByte();
            if (buf[i] >= 0) {
                int length = new PacketWrapper(Unpooled.wrappedBuffer(buf), null).readVarInt();
                if (in.readableBytes() < length) {
                    in.resetReaderIndex();
                } else {
                    out.add(in.readBytes(length));
                }
                return;
            }
        }
        throw new CorruptedFrameException("length wider than 21-bit");
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
