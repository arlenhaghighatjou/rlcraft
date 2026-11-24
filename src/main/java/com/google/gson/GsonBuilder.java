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

package com.google.gson;

import static com.google.gson.Gson.DEFAULT_ESCAPE_HTML;
import static com.google.gson.Gson.DEFAULT_LENIENT;
import static com.google.gson.Gson.DEFAULT_NUMBER_TO_NUMBER_STRATEGY;
import static com.google.gson.Gson.DEFAULT_OBJECT_TO_NUMBER_STRATEGY;
import static com.google.gson.Gson.DEFAULT_PRETTY_PRINT;
import static com.google.gson.Gson.DEFAULT_SERIALIZE_NULLS;
import static com.google.gson.Gson.DEFAULT_SPECIALIZE_FLOAT_VALUES;

import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.internal.bind.TreeTypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class GsonBuilder {
	private LongSerializationPolicy longSerializationPolicy = LongSerializationPolicy.DEFAULT;
	private final List<TypeAdapterFactory> factories = new ArrayList<>();
	private final List<TypeAdapterFactory> hierarchyFactories = new ArrayList<>();
	private boolean serializeNulls = DEFAULT_SERIALIZE_NULLS;
	private boolean escapeHtmlChars = DEFAULT_ESCAPE_HTML;
	private boolean prettyPrinting = DEFAULT_PRETTY_PRINT;
	private boolean serializeSpecialFloatingPointValues = DEFAULT_SPECIALIZE_FLOAT_VALUES;
	private boolean lenient = DEFAULT_LENIENT;
	private ToNumberStrategy objectToNumberStrategy = DEFAULT_OBJECT_TO_NUMBER_STRATEGY;
	private ToNumberStrategy numberToNumberStrategy = DEFAULT_NUMBER_TO_NUMBER_STRATEGY;

	public GsonBuilder() {
	}

	GsonBuilder(Gson gson) {
		this.serializeNulls = gson.serializeNulls;
		this.escapeHtmlChars = gson.htmlSafe;
		this.prettyPrinting = gson.prettyPrinting;
		this.lenient = gson.lenient;
		this.serializeSpecialFloatingPointValues = gson.serializeSpecialFloatingPointValues;
		this.longSerializationPolicy = gson.longSerializationPolicy;
		this.factories.addAll(gson.builderFactories);
		this.hierarchyFactories.addAll(gson.builderHierarchyFactories);
		this.objectToNumberStrategy = gson.objectToNumberStrategy;
		this.numberToNumberStrategy = gson.numberToNumberStrategy;
	}

	public GsonBuilder serializeNulls() {
		this.serializeNulls = true;
		return this;
	}

	public GsonBuilder setLongSerializationPolicy(LongSerializationPolicy serializationPolicy) {
		this.longSerializationPolicy = Objects.requireNonNull(serializationPolicy);
		return this;
	}

	public GsonBuilder setObjectToNumberStrategy(ToNumberStrategy objectToNumberStrategy) {
		this.objectToNumberStrategy = Objects.requireNonNull(objectToNumberStrategy);
		return this;
	}

	public GsonBuilder setNumberToNumberStrategy(ToNumberStrategy numberToNumberStrategy) {
		this.numberToNumberStrategy = Objects.requireNonNull(numberToNumberStrategy);
		return this;
	}

	public GsonBuilder setPrettyPrinting() {
		prettyPrinting = true;
		return this;
	}

	public GsonBuilder setLenient() {
		lenient = true;
		return this;
	}

	public GsonBuilder disableHtmlEscaping() {
		this.escapeHtmlChars = false;
		return this;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public GsonBuilder registerTypeAdapter(Type type, Object typeAdapter) {
		Objects.requireNonNull(type);
		$Gson$Preconditions.checkArgument(typeAdapter instanceof JsonSerializer<?>
				|| typeAdapter instanceof JsonDeserializer<?> || typeAdapter instanceof TypeAdapter<?>);
		if(typeAdapter instanceof JsonSerializer<?> || typeAdapter instanceof JsonDeserializer<?>) {
			factories.add(TreeTypeAdapter.newFactoryWithMatchRawType(TypeToken.get(type), typeAdapter));
		}
		if(typeAdapter instanceof TypeAdapter<?>) {
			factories.add(TypeAdapters.newFactory(TypeToken.get(type), (TypeAdapter) typeAdapter));
		}
		return this;
	}

	public GsonBuilder registerTypeAdapterFactory(TypeAdapterFactory factory) {
		factories.add(Objects.requireNonNull(factory));
		return this;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public GsonBuilder registerTypeHierarchyAdapter(Class<?> baseType, Object typeAdapter) {
		Objects.requireNonNull(baseType);
		$Gson$Preconditions.checkArgument(typeAdapter instanceof JsonSerializer<?>
				|| typeAdapter instanceof JsonDeserializer<?> || typeAdapter instanceof TypeAdapter<?>);
		if(typeAdapter instanceof JsonDeserializer || typeAdapter instanceof JsonSerializer) {
			hierarchyFactories.add(TreeTypeAdapter.newTypeHierarchyFactory(baseType, typeAdapter));
		}
		if(typeAdapter instanceof TypeAdapter<?>) {
			factories.add(TypeAdapters.newTypeHierarchyFactory(baseType, (TypeAdapter) typeAdapter));
		}
		return this;
	}

	public GsonBuilder serializeSpecialFloatingPointValues() {
		serializeSpecialFloatingPointValues = true;
		return this;
	}

	public Gson create() {
		List<TypeAdapterFactory> factories = new ArrayList<>(this.factories.size() + this.hierarchyFactories.size());
		factories.addAll(this.factories);
		Collections.reverse(factories);
		List<TypeAdapterFactory> hierarchyFactories = new ArrayList<>(this.hierarchyFactories);
		Collections.reverse(hierarchyFactories);
		factories.addAll(hierarchyFactories);
		return new Gson(serializeNulls, escapeHtmlChars, prettyPrinting, lenient, serializeSpecialFloatingPointValues,
				longSerializationPolicy, new ArrayList<>(this.factories), new ArrayList<>(this.hierarchyFactories),
				factories, objectToNumberStrategy, numberToNumberStrategy);
	}
}
