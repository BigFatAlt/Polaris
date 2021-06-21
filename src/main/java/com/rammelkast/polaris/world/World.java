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
package com.rammelkast.polaris.world;

import java.util.HashSet;
import java.util.Set;

import com.rammelkast.polaris.entity.human.Player;
import com.rammelkast.polaris.net.packet.Packet;
import com.rammelkast.polaris.net.packet.play.out.PacketOutChatMessage;
import com.rammelkast.polaris.util.Location;

import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public final class World {

	@Getter
	private final String name;
	@Getter
	private final Chunk[][] chunks;
	@Getter
	private final Set<Player> players = new HashSet<Player>();
	
	private Location spawnPoint;

	public World(final String name, final int size) {
		this.name = name;
		this.chunks = new Chunk[size][size];
		
		// TODO config
		this.spawnPoint = new Location(this, 125, 6, 125);

		// Generate world
		for (int x = 0; x < size; x++) {
			for (int z = 0; z < size; z++) {
				Chunk chunk = new Chunk(this, x, z);
				chunk.initializeSections();

				// set chunk content

				chunk.setBlock(0, 5, 0, 35, x & 0xF); // wool pillars for chunk marking
				chunk.setBlock(0, 6, 0, 35, x & 0xF); // wool pillars for chunk marking
				chunk.setBlock(0, 7, 0, 35, x & 0xF); // wool pillars for chunk marking
				chunk.setBlock(0, 8, 0, 35, x & 0xF); // wool pillars for chunk marking

				for (int x1 = 0; x1 < 16; x1++) {
					for (int z1 = 0; z1 < 16; z1++) {
						chunk.setBlock(x1, 1, z1, 7, 0);
						chunk.setBlock(x1, 2, z1, 1, 0);
						chunk.setBlock(x1, 3, z1, 3, 0);
						chunk.setBlock(x1, 4, z1, 2, 0);
					}
				}

				this.chunks[x][z] = chunk;
			}
		}
	}

	public Block getBlock(final int x, final int y, final int z) {
		final Chunk chunk = this.getChunk(x, z);
		return chunk.getBlock(x, y, z);
	}
	
	public Block getBlock(final Location location) {
		return this.getBlock(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}
	
	public Chunk getChunk(final int chunkX, final int chunkZ) {
		return this.chunks[(chunkX >> 4)][(chunkZ >> 4)];
	}
	
	public Chunk getChunk(final Location location) {
		return this.getChunk(location.getBlockX(), location.getBlockZ());
	}
	
	public Location getSpawnPoint() {
		return this.spawnPoint.clone();
	}

	public void destroy() {
		this.players.forEach(player -> player.kick("Shutting down"));
		this.players.clear();
	}
	
	public void broadcastPacket(final Packet... packets) {
		this.players.forEach(player -> {
			player.getClient().sendPacket(packets);
		});
	}
	
	public void broadcastMessage(final BaseComponent... message) {
		this.players.forEach(player -> {
			player.getClient().sendPacket(new PacketOutChatMessage(ComponentSerializer
					.toString(message), ChatMessageType.CHAT));
		});
	}

}
