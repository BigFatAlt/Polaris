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

import java.util.concurrent.atomic.AtomicInteger;

import com.rammelkast.polaris.util.Location;
import com.rammelkast.polaris.util.Vector;
import com.rammelkast.polaris.world.World;

public abstract class Entity {

	public static final AtomicInteger ENTITY_ID_SUPPLIER = new AtomicInteger();
	
	protected final int entityId;
	protected Location location;
	protected Vector velocity;
	
	public Entity(final Location location) {
		this.entityId = ENTITY_ID_SUPPLIER.getAndIncrement();
		this.location = location;
		this.velocity = new Vector();
	}

	/**
	 * Returns a unique id for this entity
	 *
	 * @return Entity id
	 */
	public int getEntityId() {
		return this.entityId;
	}

	/**
     * Gets the entity's current position
     *
     * @return a new copy of Location containing the position of this entity
     */
    public Location getLocation() {
    	return this.location.clone();
    }

    /**
     * Teleports the entity to a new location
     * 
     * @param location New location to teleport to
     */
    public void teleport(final Location location) {
    	this.location = location;
    }

    /**
     * Sets this entity's velocity
     *
     * @param velocity New velocity to travel with
     */
    public void setVelocity(final Vector velocity) {
    	this.velocity = velocity;
    }

    /**
     * Gets this entity's current velocity
     *
     * @return Current traveling velocity of this entity
     */
    public Vector getVelocity() {
    	return this.velocity;
    }
    
    /**
     * Gets this entity's current world
     * 
     * @return Current world of this entity
     */
    public World getWorld() {
    	return this.location.getWorld();
    }
	
	/**
	 * Gets the entity's height
	 *
	 * @return height of entity
	 */
	public abstract double getHeight();

	/**
	 * Gets the entity's width
	 *
	 * @return width of entity
	 */
	public abstract double getWidth();

	/**
	 * Removes the entity
	 */
	public void remove() {}

	/**
	 * Returns true if this entity is dead
	 *
	 * @return True if it is dead
	 */
	public abstract boolean isDead();

}
