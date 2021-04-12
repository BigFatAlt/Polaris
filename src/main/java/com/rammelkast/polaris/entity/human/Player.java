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
package com.rammelkast.polaris.entity.human;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rammelkast.polaris.Polaris;
import com.rammelkast.polaris.net.NetClient;
import com.rammelkast.polaris.net.packet.Packet;
import com.rammelkast.polaris.net.packet.play.in.PacketInEntityAction;
import com.rammelkast.polaris.net.packet.play.out.PacketOutChunkData;
import com.rammelkast.polaris.net.packet.play.out.PacketOutChunkData.ChunkDataMessage;
import com.rammelkast.polaris.profile.Profiler;
import com.rammelkast.polaris.net.packet.play.out.PacketOutJoinGame;
import com.rammelkast.polaris.net.packet.play.out.PacketOutKeepAlive;
import com.rammelkast.polaris.net.packet.play.out.PacketOutPlayerListHeaderFooter;
import com.rammelkast.polaris.net.packet.play.out.PacketOutPlayerListItem;
import com.rammelkast.polaris.net.packet.play.out.PacketOutPlayerPositionLook;
import com.rammelkast.polaris.net.packet.play.out.PacketOutPluginMessage;
import com.rammelkast.polaris.net.packet.play.out.PacketOutSpawnPosition;
import com.rammelkast.polaris.util.Location;
import com.rammelkast.polaris.util.MathUtilties;
import com.rammelkast.polaris.world.Chunk;
import com.rammelkast.polaris.world.World;

import io.netty.util.concurrent.ScheduledFuture;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.chat.ComponentSerializer;

public class Player extends HumanEntity {

	private static final Logger LOGGER = LogManager.getLogger(Player.class);

	@Getter
	private final NetClient client;
	private final List<Chunk> loadedChunks = new ArrayList<Chunk>();
	private final ScheduledFuture<?> keepAliveTask;

	@Getter
	private boolean onGround;
	@Getter
	private ClientSettings clientSettings;
	@Getter
	@Setter
	private String clientBrand;
	private Reference<Chunk> chunk;

	public Player(final World world, final NetClient client) {
		super(client.getUsername(), client.getUniqueId(), GameMode.SURVIVAL, world.getSpawnPoint(), 20.0D, 20.0D);
		this.client = client;
		
		world.getPlayers().add(this);

		final Profiler profiler = Polaris.getServer().getProfiler();
		final Packet[] packets = new Packet[] {
			new PacketOutJoinGame(this.entityId, (byte) this.gameMode.getValue(), 0, (byte) 1, (byte) 60, "flat",
					false),
			new PacketOutPluginMessage("MC|Brand", "Polaris".getBytes(StandardCharsets.UTF_8)),
			new PacketOutSpawnPosition(this.location),
			new PacketOutPlayerListItem(0, world.getPlayers()),
			new PacketOutPlayerListHeaderFooter(
					ComponentSerializer
							.toString(new ComponentBuilder("Powered by Polaris").color(ChatColor.GOLD).create()),
					ComponentSerializer.toString(new ComponentBuilder("Ping: " + client.getPing() + " ms" + "\nMemory: "
							+ profiler.getMemoryUsed().toString() + "/" + profiler.getTotalMemory().toString() + " MB")
									.color(ChatColor.GRAY).create()))
		};
		client.sendPacket(packets);

		this.keepAliveTask = client.getEventLoop().scheduleAtFixedRate(() -> {
			client.sendPacket(new PacketOutKeepAlive(client.generateKeepAliveId()));
		}, 5, 5, TimeUnit.SECONDS); // Send keep alive every 5 seconds

		this.teleport(world.getSpawnPoint());
		this.updateMovement();
		this.teleport(world.getSpawnPoint());
		
		this.getWorld().broadcast(new ComponentBuilder(this.name + " joined.").color(ChatColor.YELLOW).create());
	}

	public void kick(final String message) {
		this.client.disconnect(message, null);
	}

	public void destroy() {
		this.keepAliveTask.cancel(false);
		this.getWorld().getPlayers().remove(this);
		this.getWorld().broadcast(new ComponentBuilder(this.name + " left.").color(ChatColor.YELLOW).create());
	}

	public void updatePosition(final boolean onGround) {
		this.onGround = onGround;
		this.updateMovement();
	}

	public void updatePosition(final float yaw, final float pitch, final boolean onGround) {
		this.location.setYaw(yaw);
		this.location.setPitch(pitch);
		this.onGround = onGround;
		this.updateMovement();
	}

	public void updatePosition(final double x, final double y, final double z, final boolean onGround) {
		this.location.setX(x);
		this.location.setY(y);
		this.location.setZ(z);
		this.onGround = onGround;
		this.updateMovement();
	}

	public void updatePosition(final double x, final double y, final double z, final float yaw, final float pitch,
			final boolean onGround) {
		this.location.setX(x);
		this.location.setY(y);
		this.location.setZ(z);
		this.location.setYaw(yaw);
		this.location.setPitch(pitch);
		this.onGround = onGround;
		this.updateMovement();
	}
	
	private void updateMovement() {
		final Chunk currentChunk = this.chunk == null ? null : this.chunk.get();
		Chunk newChunk;
		try {
			newChunk = this.getWorld().getChunk(this.location);
		} catch (ArrayIndexOutOfBoundsException exception) {
			this.teleport(this.getWorld().getSpawnPoint());
			return;
		}
		
		if (newChunk != currentChunk) {
			this.chunk = new WeakReference<Chunk>(newChunk);
			this.updateChunks();
		}
	}

	public void updateClientSettings(final ClientSettings clientSettings) {
		final int currentViewDistance = this.clientSettings == null ? -1 : this.clientSettings.viewDistance;
		final int newViewDistance = clientSettings.viewDistance;
		if (newViewDistance != currentViewDistance) {
			// Update view distance, resend chunks
			this.updateChunks();
		}
		this.clientSettings = clientSettings;
	}

	public void handleEntityAction(final PacketInEntityAction packetEntityAction) {
		if (this.entityId != packetEntityAction.getEntityId()) {
			LOGGER.warn("Entity ID on action does not match! (player={}, packet={})", this.entityId,
					packetEntityAction.getEntityId());
			return;
		}

		switch (packetEntityAction.getAction()) {
		case START_SPRINTING: {
			this.setSprinting(true);
			break;
		}
		case STOP_SPRINTING: {
			this.setSprinting(false);
			break;
		}
		case START_SNEAKING: {
			this.setSneaking(false);
			break;
		}
		case STOP_SNEAKING: {
			this.setSneaking(false);
			break;
		}
		default: {
			LOGGER.warn("{} performed unsupported entity action ({})", this.name,
					packetEntityAction.getAction().name());
			break;
		}
		}
	}

	public void handleChatMessage(final String message) {
		LOGGER.log(Level.toLevel("CHAT"), "{}: {}", this.name, message);
		this.getWorld().broadcast(new ComponentBuilder(this.name + ": " + message).color(ChatColor.GRAY).create());
	}

	private void updateChunks() {
		final long startTime = System.nanoTime();
		final int viewDistance = Math.min(this.clientSettings == null ? Polaris.VIEW_DISTANCE : this.clientSettings.viewDistance, Polaris.VIEW_DISTANCE);
		final int viewSquared = viewDistance * viewDistance;

		final Queue<Chunk> loadQueue = new LinkedList<Chunk>();
		final Queue<Chunk> unloadQueue = new LinkedList<Chunk>();
		for (Chunk[] tab : this.getWorld().getChunks()) {
			for (Chunk chunk : tab) {
				double distanceSquared = MathUtilties.distanceSquared((int) this.location.getX() / 16,
						(int) this.location.getZ() / 16, chunk.getX(), chunk.getZ());
				if (distanceSquared >= viewSquared && this.loadedChunks.contains(chunk)) {
					unloadQueue.add(chunk);
				} else if (distanceSquared < viewSquared && !this.loadedChunks.contains(chunk)) {
					loadQueue.add(chunk);
				}
			}
		}

		this.loadedChunks.removeAll(unloadQueue);
		this.loadedChunks.addAll(loadQueue);

		final List<Packet> packets = new ArrayList<Packet>();
		final int loadCount = loadQueue.size();
		if (loadCount > 0) {
			// TODO either do chunk bulk 1.8.x only, or keep using individual chunk packets
			/*final ChunkDataMessage[] newChunks = new ChunkDataMessage[loadCount];
			{
				Chunk chunk;
				int index = 0;
				while ((chunk = loadQueue.poll()) != null) {
					newChunks[index++] = chunk.toMessage(true);
				}
			}
			packets.add(new PacketOutChunkBulk(newChunks, true));*/
			
			Chunk chunk;
			while ((chunk = loadQueue.poll()) != null) {
				packets.add(new PacketOutChunkData(chunk.toMessage(true)));
			}
		}

		final int unloadCount = unloadQueue.size();
		if (unloadCount > 0) {
			Chunk chunk;
			while ((chunk = loadQueue.poll()) != null) {
				packets.add(new PacketOutChunkData(ChunkDataMessage.empty(chunk.getX(), chunk.getZ())));
			}
		}

		final long endTime = System.nanoTime();
		if (loadCount > 0 || unloadCount > 0) {
			LOGGER.info("Updated chunks for " + this.name + " (loaded " + loadCount + ", unloaded " + unloadCount
					+ ", took " + ((float) (endTime - startTime) / 1000000L) + " ms)");
		}
		this.client.sendPacket(packets.toArray(Packet[]::new));
	}

	public long getPing() {
		return this.client.getPing();
	}

	@Override
	public double getHeight() {
		return this.sneaking ? 1.5D : 1.8D;
	}

	@Override
	public double getWidth() {
		return 0.6D;
	}

	@Override
	public boolean isDead() {
		return this.health == 0.0D;
	}

	/**
	 * Gets the players' network address
	 * 
	 * @return the players' network address in string format
	 */
	public String getAddress() {
		return this.client.getAddress();
	}

	@Override
	public void teleport(final Location location) {
		super.teleport(location);
		this.client.sendPacket(new PacketOutPlayerPositionLook(location.getX(), location.getY(), location.getZ(),
				location.getYaw(), location.getPitch(), (byte) 0));
	}

	@RequiredArgsConstructor
	@Getter
	public static final class ClientSettings {
		private final String locale;
		private final int viewDistance;
		private final int chatMode;
		private final boolean chatColors;
		private final byte displayedSkinParts;
	}

}
