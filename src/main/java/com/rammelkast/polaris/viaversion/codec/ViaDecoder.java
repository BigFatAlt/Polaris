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
package com.rammelkast.polaris.viaversion.codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.RequiredArgsConstructor;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.exception.CancelDecoderException;

@RequiredArgsConstructor
public final class ViaDecoder extends MessageToMessageDecoder<ByteBuf> {

	private final UserConnection user;

	@Override
	protected void decode(final ChannelHandlerContext ctx, final ByteBuf msg, final List<Object> out) throws Exception {
		if (!this.user.checkIncomingPacket()) {
			throw CancelDecoderException.generate(null);
		}
		
        if (!this.user.shouldTransformPacket()) {
            out.add(msg.retain());
            return;
        }

        final ByteBuf buffer = ctx.alloc().buffer().writeBytes(msg);
        try {
            this.user.transformIncoming(buffer, CancelDecoderException::new);
            out.add(buffer.retain());
        } catch (Exception e) {
			if (!(e instanceof CancelDecoderException)) {
				e.printStackTrace();
			}
		} finally {
        	buffer.release();
        }
	}

}
