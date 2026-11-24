/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public final class ArrayTypeAdapter<E> extends TypeAdapter<Object> {
	public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
			Class<?> type = typeToken.getRawType();
			if(!type.isArray()) {
				return null;
			}
			Class<?> componentType = type.getComponentType();
			if(componentType.isPrimitive()) {
				return null;
			}
			return new ArrayTypeAdapter(gson.getAdapter(componentType), componentType);
		}
	};

	private final Class<E> componentType;
	private final TypeAdapter<E> componentTypeAdapter;

	public ArrayTypeAdapter(TypeAdapter<E> componentTypeAdapter, Class<E> componentType) {
		this.componentTypeAdapter = componentTypeAdapter;
		this.componentType = componentType;
	}

	@Override
	public Object read(JsonReader in) throws IOException {
		if(in.peek() == JsonToken.NULL) {
			in.nextNull();
			return null;
		}
		ArrayList<E> list = new ArrayList<>();
		in.beginArray();
		while(in.hasNext()) {
			list.add(componentTypeAdapter.read(in));
		}
		in.endArray();
		@SuppressWarnings("unchecked")
		E[] array = (E[]) Array.newInstance(componentType, list.size());
		return list.toArray(array);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void write(JsonWriter out, Object array) throws IOException {
		if(array == null) {
			out.nullValue();
			return;
		}
		E[] arr = (E[]) array;
		out.beginArray();
		for(int i = 0; i < arr.length; ++i) {
			componentTypeAdapter.write(out, arr[i]);
		}
		out.endArray();
	}
}
