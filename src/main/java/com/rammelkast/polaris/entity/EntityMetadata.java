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

import java.util.function.BiConsumer;

import com.rammelkast.polaris.net.packet.PacketWrapper;

import lombok.AllArgsConstructor;
import lombok.Getter;

public final class EntityMetadata {
	private final Object[] values = new Object[0x1F];

	public void set(final int index, final Object value) {
		values[index] = value;
	}

	public void setByte(final int index, final int value) {
		set(index, (byte) value);
	}

	public void setInt(final int index, final int value) {
		set(index, value);
	}

	public void setFloat(int index, float value) {
		set(index, value);
	}

	public void setString(int index, String value) {
		set(index, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(final int index) {
		return (T) values[index];
	}

	public int getByte(final int index) {
		return this.<Byte>get(index);
	}

	public int getInt(final int index) {
		return this.<Integer>get(index);
	}

	public String getString(final int index) {
		return this.get(index);
	}

	public void write(final PacketWrapper wrapper) {
		for (int i = 0; i < values.length; i++) {
			final Object value = values[i];
			if (value == null) {
				continue;
			}
			
			final MetadataType type = typeOf(value);
			final int item = i | type.ordinal() << 5;
			wrapper.getBuffer().writeByte(item);
			type.getWriteHandler().accept(wrapper, value);
		}
		wrapper.getBuffer().writeByte(0x7F);
	}

	private MetadataType typeOf(final Object value) {
		if (value instanceof Byte) {
			return MetadataType.BYTE;
		} else if (value instanceof Short) {
			return MetadataType.SHORT;
		} else if (value instanceof Integer) {
			return MetadataType.INT;
		} else if (value instanceof Float) {
			return MetadataType.FLOAT;
		} else if (value instanceof String) {
			return MetadataType.STRING;
		}
		return null;
	}
	
	@AllArgsConstructor
	@Getter
	public static enum MetadataType {
	    BYTE((wrapper, obj) -> wrapper.getBuffer().writeByte((byte) obj)),
	    SHORT((wrapper, obj) -> wrapper.getBuffer().writeShort((short) obj)),
	    INT((wrapper, obj) -> wrapper.getBuffer().writeInt((int) obj)),
	    FLOAT((wrapper, obj) -> wrapper.getBuffer().writeFloat((float) obj)),
	    STRING((wrapper, obj) -> wrapper.writeString((String) obj))
	    ;

	    private final BiConsumer<PacketWrapper, Object> writeHandler;
	}
}