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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public final class NetServer extends Thread {

	private static final Logger LOGGER = LogManager.getLogger(NetServer.class);

	private final int port;
	private final int threadCount;
	private final Class<? extends ServerChannel> channel;
	private final EventLoopGroup bossGroup, workerGroup;
	private final ServerBootstrap bootstrap;
	private final ConnectionBuilder connectionBuilder;

	public NetServer(int port) {
		this.port = port;
		// TODO Config option
		this.threadCount = Runtime.getRuntime().availableProcessors();
		// Always pick best transport type
		if (Epoll.isAvailable()) {
			this.channel = EpollServerSocketChannel.class;
			this.bossGroup = new EpollEventLoopGroup(2);
			this.workerGroup = new EpollEventLoopGroup(this.threadCount);
		} else if (KQueue.isAvailable()) {
			this.channel = KQueueServerSocketChannel.class;
			this.bossGroup = new KQueueEventLoopGroup(2);
			this.workerGroup = new KQueueEventLoopGroup(this.threadCount);
		} else {
			this.channel = NioServerSocketChannel.class;
			this.bossGroup = new NioEventLoopGroup(2);
			this.workerGroup = new NioEventLoopGroup(this.threadCount);
		}
		
		this.connectionBuilder = new ConnectionBuilder(this);
		this.bootstrap = new ServerBootstrap().group(this.bossGroup, this.workerGroup)
				.childOption(ChannelOption.TCP_NODELAY, true).channel(this.channel).childHandler(this.connectionBuilder);
	}

	@Override
	public void run() {
		try {
            ChannelFuture future = this.bootstrap.bind(port).sync();
            if (!future.isSuccess()) {
                throw new IllegalStateException("Could not bind NetServer to port " + this.port);
            }
        } catch (InterruptedException ex) {
            LOGGER.error(ex);
        }
	}
	
	public void shutdown() {
		try {
			this.workerGroup.shutdownGracefully();
			this.bossGroup.shutdownGracefully();
			this.join();
		} catch (Exception e) {}
	}

}
