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
package com.rammelkast.polaris.entity;

import com.rammelkast.polaris.entity.human.Player;
import com.rammelkast.polaris.util.Location;

public abstract class Damageable extends Entity {

	protected double health, maxHealth;
	
	public Damageable(final Location location, final double health, final double maxHealth) {
		super(location);
		this.health = health;
		this.maxHealth = maxHealth;
	}
	
	/**
	 * Deals the given amount of damage to this entity.
	 *
	 * @param amount Amount of damage to deal
	 */
	public void damage(final double amount) {
		this.health = Math.max(this.health - amount, 0);
	}

	/**
	 * Deals the given amount of damage to this entity, from a specified entity.
	 *
	 * @param amount Amount of damage to deal
	 * @param source Entity which to attribute this damage from
	 */
	public void damage(final double amount, final Entity source) {
		this.damage(amount);
	}

	/**
	 * Gets the entity's health from 0 to {@link #getMaxHealth()}, where 0 is dead.
	 *
	 * @return Health represented from 0 to max
	 */
	public double getHealth() {
		return this.health;
	}

	/**
	 * Sets the entity's health from 0 to {@link #getMaxHealth()}, where 0 is dead.
	 *
	 * @param health New health represented from 0 to max
	 */
	public void setHealth(final double health) {
		this.health = health;
	}

	/**
	 * Gets the maximum health this entity has.
	 *
	 * @return Maximum health
	 */
	public double getMaxHealth() {
		return this.maxHealth;
	}

	/**
	 * Sets the maximum health this entity can have.
	 * <p>
	 * If the health of the entity is above the value provided it will be set to
	 * that value.
	 * <p>
	 * Note: An entity with a health bar ({@link Player}, {@link EnderDragon},
	 * {@link Wither}, etc...} will have their bar scaled accordingly.
	 *
	 * @param health amount of health to set the maximum to
	 */
	public void setMaxHealth(final double health) {
		this.maxHealth = health;
	}

}
