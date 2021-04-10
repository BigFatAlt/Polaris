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
package com.rammelkast.polaris.net.packet.status.out;

import org.json.JSONObject;

import com.rammelkast.polaris.Polaris;
import com.rammelkast.polaris.net.packet.Packet;
import com.rammelkast.polaris.net.packet.PacketWrapper;

import net.md_5.bungee.api.ChatColor;

public final class PacketOutStatusResponse extends Packet {

	@Override
	public byte getId() {
		return 0x00;
	}

	@Override
	public void read(final PacketWrapper wrapper) {
		throw new IllegalAccessError("Packet cannot be read");
	}

	@Override
	public void write(final PacketWrapper wrapper) {
		final JSONObject response = new JSONObject();
		{
			// Protocol
			final JSONObject version = new JSONObject();
			{
				version.put("name", "Polaris");
				version.put("protocol", 47);
			}
			response.put("version", version);

			// Players
			final JSONObject players = new JSONObject();
			{
				// TODO config & update
				players.put("max", 100);
				players.put("online", Polaris.getServer().getWorld().getPlayers().size());
			}
			response.put("players", players);

			// Description
			final JSONObject description = new JSONObject();
			{
				// TODO config
				description.put("text", ChatColor.GOLD + "Powered by Polaris\n" + ChatColor.RESET + "https://github.com/Rammelkast/Polaris");
			}
			response.put("description", description);
		}
		wrapper.writeString(response.toString());
	}

}
