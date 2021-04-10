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

import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;
import com.rammelkast.polaris.entity.LivingEntity;
import com.rammelkast.polaris.util.Location;

public abstract class HumanEntity extends LivingEntity {

	protected String name;
	protected UUID uniqueId;
	protected GameMode gameMode;
	
	protected boolean sprinting, sneaking;
	
	public HumanEntity(final String name, final UUID uniqueId, final GameMode gameMode, final Location location, final double health, final double maxHealth) {
		super(location, health, maxHealth);
		this.name = name;
		this.uniqueId = uniqueId;
		this.gameMode = gameMode;
	}

	/**
     * Returns the name of this human
     *
     * @return Human name
     */
    public String getName() {
    	return this.name;
    }
    
    /**
     * Sets this human's current name
     *
     * @param name New name
     */
    public void setName(final String name) {
    	this.name = name;
    }
    
	/**
     * Returns the UUID of this human
     *
     * @return Human UUID
     */
    public UUID getUniqueId() {
    	return this.uniqueId;
    }
    
	/**
     * Returns the sprinting state of this human
     *
     * @return Human sprinting state
     */
    public boolean isSprinting() {
    	return this.sprinting;
    }
    
    /**
     * Sets this human's current sprinting state
     *
     * @param sprinting New sprinting state
     */
    public void setSprinting(final boolean sprinting) {
    	this.sprinting = sprinting;
    }
    
	/**
     * Returns the sneaking state of this human
     *
     * @return Human sneaking state
     */
    public boolean isSneaking() {
    	return this.sneaking;
    }
    
    /**
     * Sets this human's current sneaking state
     *
     * @param sneaking New sneaking state
     */
    public void setSneaking(final boolean sneaking) {
    	this.sneaking = sneaking;
    }
	
    /**
     * Gets this human's current {@link GameMode}
     *
     * @return Current game mode
     */
    public GameMode getGameMode() {
    	return this.gameMode;
    }

    /**
     * Sets this human's current {@link GameMode}
     *
     * @param mode New game mode
     */
    public void setGameMode(final GameMode mode) {
    	this.gameMode = mode;
    }
    
    /**
     * Represents the various type of game modes that {@link HumanEntity}s may
     * have
     */
    public static enum GameMode {
        /**
         * Creative mode may fly, build instantly, become invulnerable and create
         * free items.
         */
        CREATIVE(1),

        /**
         * Survival mode is the "normal" gameplay type, with no special features.
         */
        SURVIVAL(0),

        /**
         * Adventure mode cannot break blocks without the correct tools.
         */
        ADVENTURE(2),

        /**
         * Spectator mode cannot interact with the world in anyway and is
         * invisible to normal players. This grants the player the
         * ability to no-clip through the world.
         */
        SPECTATOR(3);

        private final int value;
        private static final Map<Integer, GameMode> BY_ID = Maps.newHashMap();

        private GameMode(final int value) {
            this.value = value;
        }

        /**
         * Gets the mode value associated with this GameMode
         *
         * @return An integer value of this gamemode
         */
        public int getValue() {
            return value;
        }

        /**
         * Gets the GameMode represented by the specified value
         *
         * @param value Value to check
         * @return Associative {@link GameMode} with the given value, or null if
         *     it doesn't exist
         */
        public static GameMode getByValue(final int value) {
            return BY_ID.get(value);
        }

        static {
            for (GameMode mode : values()) {
                BY_ID.put(mode.getValue(), mode);
            }
        }
    }
    
}
