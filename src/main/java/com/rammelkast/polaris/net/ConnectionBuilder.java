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

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ConnectionBuilder extends ChannelInitializer<SocketChannel> {

	public static final String TIMEOUT_HANDLER = "timeout-handler";
	public static final String PACKET_DECODER = "packet-decoder";
	public static final String PACKET_ENCODER = "packet-encoder";
	public static final String NET_CLIENT = "net-client";
	
	private final NetServer server;
	
	@Override
	protected void initChannel(final SocketChannel ch) throws Exception {
		final ChannelPipeline pipeline = ch.pipeline();
		final NetClient client = new NetClient();
		
		pipeline.addLast(TIMEOUT_HANDLER, new ReadTimeoutHandler(30, TimeUnit.SECONDS));
		pipeline.addLast(PACKET_DECODER, new PacketDecoder(client));
		pipeline.addLast(PACKET_ENCODER, new PacketEncoder(client));
		pipeline.addLast(NET_CLIENT, client);
	}

}
