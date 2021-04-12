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

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import us.myles.ViaVersion.api.ViaVersionConfig;

public final class PolarisViaConfig implements ViaVersionConfig {
	
	@Override
	public boolean isCheckForUpdates() {
		return false;
	}

	@Override
	public void setCheckForUpdates(boolean b) {
	}

	@Override
	public boolean isPreventCollision() {
		return false;
	}

	@Override
	public boolean isNewEffectIndicator() {
		return false;
	}

	@Override
	public boolean isShowNewDeathMessages() {
		return false;
	}

	@Override
	public boolean isSuppressMetadataErrors() {
		return true;
	}

	@Override
	public boolean isShieldBlocking() {
		return false;
	}

	@Override
	public boolean isHologramPatch() {
		return true;
	}

	@Override
	public boolean isPistonAnimationPatch() {
		return false;
	}

	@Override
	public boolean isBossbarPatch() {
		return true;
	}

	@Override
	public boolean isBossbarAntiflicker() {
		return false;
	}

	@Override
	public double getHologramYOffset() {
		return -0.96;
	}

	@Override
	public boolean isAutoTeam() {
		return false;
	}

	@Override
	public int getMaxPPS() {
		return -1;
	}

	@Override
	public String getMaxPPSKickMessage() {
		return null;
	}

	@Override
	public int getTrackingPeriod() {
		return -1;
	}

	@Override
	public int getWarningPPS() {
		return -1;
	}

	@Override
	public int getMaxWarnings() {
		return -1;
	}

	@Override
	public String getMaxWarningsKickMessage() {
		return null;
	}

	@Override
	public boolean isAntiXRay() {
		return false;
	}

	@Override
	public boolean isSendSupportedVersions() {
		return false;
	}

	@Override
	public boolean isSimulatePlayerTick() {
		return false;
	}

	@Override
	public boolean isItemCache() {
		return false;
	}

	@Override
	public boolean isNMSPlayerTicking() {
		return false;
	}

	@Override
	public boolean isReplacePistons() {
		return false;
	}

	@Override
	public int getPistonReplacementId() {
		return -1;
	}

	@Override
	public boolean isForceJsonTransform() {
		return false;
	}

	@Override
	public boolean is1_12NBTArrayFix() {
		return true;
	}

	@Override
	public boolean is1_13TeamColourFix() {
		return true;
	}

	@Override
	public boolean is1_12QuickMoveActionFix() {
		return false;
	}

	@Override
	public IntSet getBlockedProtocols() {
		return new IntArraySet();
	}

	@Override
	public String getBlockedDisconnectMsg() {
		return null;
	}

	@Override
	public String getReloadDisconnectMsg() {
		return null;
	}

	@Override
	public boolean isSuppressConversionWarnings() {
		return false;
	}

	@Override
	public boolean isDisable1_13AutoComplete() {
		return false;
	}

	@Override
	public boolean isMinimizeCooldown() {
		return true;
	}

	@Override
	public boolean isServersideBlockConnections() {
		return true;
	}

	@Override
	public String getBlockConnectionMethod() {
		return "packet";
	}

	@Override
	public boolean isReduceBlockStorageMemory() {
		return false;
	}

	@Override
	public boolean isStemWhenBlockAbove() {
		return true;
	}

	@Override
	public boolean isVineClimbFix() {
		return false;
	}

	@Override
	public boolean isSnowCollisionFix() {
		return false;
	}

	@Override
	public boolean isInfestedBlocksFix() {
		return false;
	}

	@Override
	public int get1_13TabCompleteDelay() {
		return 0;
	}

	@Override
	public boolean isTruncate1_14Books() {
		return false;
	}

	@Override
	public boolean isLeftHandedHandling() {
		return true;
	}

	@Override
	public boolean is1_9HitboxFix() {
		return true;
	}

	@Override
	public boolean is1_14HitboxFix() {
		return true;
	}

	@Override
	public boolean isNonFullBlockLightFix() {
		return false;
	}

	@Override
	public boolean is1_14HealthNaNFix() {
		return true;
	}

	@Override
	public boolean is1_15InstantRespawn() {
		return false;
	}

	@Override
	public boolean isIgnoreLong1_16ChannelNames() {
		return false;
	}
}