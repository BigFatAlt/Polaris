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
package com.rammelkast.polaris.net;

import java.util.concurrent.TimeUnit;

import com.rammelkast.polaris.net.codec.PacketDecoder;
import com.rammelkast.polaris.net.codec.PacketEncoder;
import com.rammelkast.polaris.net.codec.PacketFramer;
import com.rammelkast.polaris.viaversion.codec.ViaDecoder;
import com.rammelkast.polaris.viaversion.codec.ViaEncoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.RequiredArgsConstructor;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.protocol.ProtocolPipeline;

@RequiredArgsConstructor
public final class ConnectionBuilder extends ChannelInitializer<SocketChannel> {

	public static final String TIMEOUT_HANDLER = "timeout-handler";
	public static final String PACKET_FRAMER = "packet-framer";
	public static final String VIA_ENCODER = "via-encoder";
	public static final String VIA_DECODER = "via-decoder";
	public static final String PACKET_DECODER = "packet-decoder";
	public static final String PACKET_ENCODER = "packet-encoder";
	public static final String NET_CLIENT = "net-client";
	
	private final NetServer server;
	
	@Override
	protected void initChannel(final SocketChannel ch) throws Exception {
		final ChannelPipeline pipeline = ch.pipeline();
		final NetClient client = new NetClient();
		
		// ViaVersion
		final UserConnection user = new UserConnection(ch);
		{
			new ProtocolPipeline(user);
		}
		// End ViaVersion
		
		pipeline.addLast(TIMEOUT_HANDLER, new ReadTimeoutHandler(30, TimeUnit.SECONDS));
		pipeline.addLast(PACKET_FRAMER, new PacketFramer());
		// ViaVersion
		pipeline.addLast(VIA_DECODER, new ViaDecoder(user));
		pipeline.addLast(VIA_ENCODER, new ViaEncoder(user));
		// End ViaVersion
		pipeline.addLast(PACKET_DECODER, new PacketDecoder(client));
		pipeline.addLast(PACKET_ENCODER, new PacketEncoder(client));
		pipeline.addLast(NET_CLIENT, client);
	}

}
