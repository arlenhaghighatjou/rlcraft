/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gson.reflect;

import java.lang.reflect.Type;

public class TypeToken<T> {
	private final Class<? super T> rawType;
	private final int hashCode;

	@SuppressWarnings("unchecked")
	private TypeToken(Type type) {
		if(!(type instanceof Class)) {
			throw new IllegalArgumentException("Only class types are supported: " + type);
		}
		this.rawType = (Class<? super T>) type;
		this.hashCode = type.hashCode();
	}

	public final Class<? super T> getRawType() {
		return rawType;
	}

	public final Type getType() {
		return rawType;
	}

	public boolean isAssignableFrom(Class<?> cls) {
		return rawType.isAssignableFrom(cls);
	}

	@Override
	public final int hashCode() {
		return hashCode;
	}

	@Override
	public final boolean equals(Object o) {
		return o instanceof TypeToken<?> && ((TypeToken<?>) o).rawType == rawType;
	}

	@Override
	public final String toString() {
		return rawType.getName();
	}

	public static TypeToken<?> get(Type type) {
		return new TypeToken<>(type);
	}

	public static <T> TypeToken<T> get(Class<T> type) {
		return new TypeToken<>(type);
	}

	public static TypeToken<?> getArray(Type componentType) {
		return new TypeToken<>(java.lang.reflect.Array.newInstance((Class<?>) componentType, 0).getClass());
	}
}
