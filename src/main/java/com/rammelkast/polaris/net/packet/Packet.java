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
package com.rammelkast.polaris.net.packet;

import com.rammelkast.polaris.net.NetClient;

public abstract class Packet {

	public abstract byte getId();
	
	public abstract void read(final PacketWrapper wrapper);
	
	public abstract void write(final PacketWrapper wrapper);
	
	public void onReceive(final NetClient client) {}
	
	public void onSend(final NetClient client) {}
	
}
