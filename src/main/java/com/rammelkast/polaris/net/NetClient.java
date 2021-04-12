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

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rammelkast.polaris.Polaris;
import com.rammelkast.polaris.entity.human.Player;
import com.rammelkast.polaris.net.packet.Packet;
import com.rammelkast.polaris.net.packet.login.out.PacketOutLoginDisconnect;
import com.rammelkast.polaris.net.packet.login.out.PacketOutLoginSuccess;
import com.rammelkast.polaris.net.packet.play.out.PacketOutPlayDisconnect;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.chat.ComponentSerializer;

@RequiredArgsConstructor
public final class NetClient extends ChannelInboundHandlerAdapter {

	private static final AtomicInteger KEEPALIVE_ID_SUPPLIER = new AtomicInteger();
	private static final Logger LOGGER = LogManager.getLogger(NetClient.class);
	
	private final Queue<Packet> packetQueue = new LinkedList<Packet>();
	@Getter
	@Setter
	private NetState state = NetState.HANDSHAKE;
	@Getter
	private boolean connected = true;
	private ChannelHandlerContext channel;
	@Getter
	private String username;
	@Getter
	private UUID uniqueId;
	@Getter
	private Player player;
	@Getter
	@Setter
	private long lastKeepAlive, ping;
	@Getter
	private int keepAliveId;
	
	@Override
	public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
		this.channel = ctx;
		if (this.connected == false) {
			LOGGER.warn("Skipping packet: connection closed");
			return;
		}
		
		((Packet) msg).onReceive(this);
		this.flush();
	}

	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
	}

	@Override
	public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
		// Do not disconnect twice
		if (!this.connected) {
			return;
		}
		
		synchronized (this.packetQueue) {
			this.connected = false;
			this.packetQueue.clear();
		}
		
		if (this.player != null) {
			this.player.destroy();
			this.player = null;
			LOGGER.info("{} disconnected (left the game)", this.username);
		}
		super.channelInactive(ctx);
	}
	
	public void login(final String username, final UUID uniqueId) {
		this.username = username;
		this.uniqueId = uniqueId;
		LOGGER.info("{} connected from {} with UUID {}", username, this.getAddress(), uniqueId);
		
		if (player != null) {
			throw new IllegalStateException("Cannot login twice");
		}

		this.sendPacket(new PacketOutLoginSuccess(uniqueId, username));
		this.setState(NetState.PLAY);
		
		this.player = new Player(Polaris.getServer().getWorld(), this);
	}
	
	public String getAddress() {
		return ((InetSocketAddress) channel.channel().remoteAddress()).getAddress().toString();
	}
	
	public void flush() {
		synchronized (this.packetQueue) {
			Packet packet;
			while ((packet = this.packetQueue.poll()) != null && this.connected) {
				this.channel.write(packet);
				packet.onSend(this);
			}
			this.channel.flush();
		}
	}
	
	public void sendPacket(Packet... packets) {
		if (!this.connected) {
			return;
		}
		
		synchronized (this.packetQueue) {
			for (Packet packet : packets) {
				this.packetQueue.offer(packet);
			}
		}
		this.flush();
	}
	
	public void disconnect(final String message, final Exception exception) {
		this.disconnect(new ComponentBuilder("Disconnected: " + message).color(ChatColor.RED).create());
		if (exception != null) {
			LOGGER.error(exception);
		}
	}
	
	public void disconnect(final BaseComponent... message) {
		NetClient.this.connected = false;
		
		switch (this.state) {
		case LOGIN: {
			ChannelFuture future = this.channel.write(new PacketOutLoginDisconnect(ComponentSerializer.toString(message)));
			future.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) {
					NetClient.this.channel.close();
				}
			});
			this.channel.flush();
			break;
		} case PLAY: {
			ChannelFuture future = this.channel.write(new PacketOutPlayDisconnect(ComponentSerializer.toString(message)));
			future.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) {
					NetClient.this.channel.close();
				}
			});
			this.channel.flush();
			break;
		} default:
			this.channel.disconnect();
			break;
		}
		
		if (this.username != null) {
			LOGGER.info("{} disconnected", this.username);
		}
	}
	
	public EventLoop getEventLoop() {
		return this.channel.channel().eventLoop();
	}
	
	public int generateKeepAliveId() {
		this.keepAliveId = KEEPALIVE_ID_SUPPLIER.incrementAndGet();
		return this.keepAliveId;
	}
	
	public enum NetState {
		HANDSHAKE, STATUS, LOGIN, PLAY
	}
	
}
