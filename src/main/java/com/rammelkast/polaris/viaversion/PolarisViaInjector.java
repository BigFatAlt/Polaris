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
package com.rammelkast.polaris.viaversion;

import com.rammelkast.polaris.net.ConnectionBuilder;
import com.viaversion.viaversion.api.platform.ViaInjector;
import com.viaversion.viaversion.libs.gson.JsonObject;

public final class PolarisViaInjector implements ViaInjector {
	
	@Override
	public String getDecoderName() {
		return ConnectionBuilder.VIA_DECODER;
	}

	@Override
	public JsonObject getDump() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEncoderName() {
		return ConnectionBuilder.VIA_ENCODER;
	}

	@Override
	public int getServerProtocolVersion() throws Exception {
		return 47;
	}

	@Override
	public void inject() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void uninject() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
