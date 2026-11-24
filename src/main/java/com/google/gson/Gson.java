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

import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.internal.Primitives;
import com.google.gson.internal.Streams;
import com.google.gson.internal.bind.ArrayTypeAdapter;
import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.internal.bind.JsonTreeWriter;
import com.google.gson.internal.bind.NumberTypeAdapter;
import com.google.gson.internal.bind.ObjectTypeAdapter;
import com.google.gson.internal.bind.SerializationDelegatingTypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.MalformedJsonException;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Gson {
	static final boolean DEFAULT_LENIENT = false;
	static final boolean DEFAULT_PRETTY_PRINT = false;
	static final boolean DEFAULT_ESCAPE_HTML = true;
	static final boolean DEFAULT_SERIALIZE_NULLS = false;
	static final boolean DEFAULT_SPECIALIZE_FLOAT_VALUES = false;
	static final ToNumberStrategy DEFAULT_OBJECT_TO_NUMBER_STRATEGY = ToNumberPolicy.DOUBLE;
	static final ToNumberStrategy DEFAULT_NUMBER_TO_NUMBER_STRATEGY = ToNumberPolicy.LAZILY_PARSED_NUMBER;

	private final Map<TypeToken<?>, TypeAdapter<?>> typeTokenCache = new HashMap<>();
	private Map<TypeToken<?>, TypeAdapter<?>> pendingCalls = null;

	final List<TypeAdapterFactory> factories;
	final boolean serializeNulls;
	final boolean htmlSafe;
	final boolean prettyPrinting;
	final boolean lenient;
	final boolean serializeSpecialFloatingPointValues;
	final LongSerializationPolicy longSerializationPolicy;
	final List<TypeAdapterFactory> builderFactories;
	final List<TypeAdapterFactory> builderHierarchyFactories;
	final ToNumberStrategy objectToNumberStrategy;
	final ToNumberStrategy numberToNumberStrategy;

	public Gson() {
		this(DEFAULT_SERIALIZE_NULLS, DEFAULT_ESCAPE_HTML, DEFAULT_PRETTY_PRINT, DEFAULT_LENIENT,
				DEFAULT_SPECIALIZE_FLOAT_VALUES, LongSerializationPolicy.DEFAULT,
				Collections.<TypeAdapterFactory>emptyList(), Collections.<TypeAdapterFactory>emptyList(),
				Collections.<TypeAdapterFactory>emptyList(), DEFAULT_OBJECT_TO_NUMBER_STRATEGY,
				DEFAULT_NUMBER_TO_NUMBER_STRATEGY);
	}

	Gson(boolean serializeNulls, boolean htmlSafe, boolean prettyPrinting, boolean lenient,
			boolean serializeSpecialFloatingPointValues, LongSerializationPolicy longSerializationPolicy,
			List<TypeAdapterFactory> builderFactories, List<TypeAdapterFactory> builderHierarchyFactories,
			List<TypeAdapterFactory> factoriesToBeAdded, ToNumberStrategy objectToNumberStrategy,
			ToNumberStrategy numberToNumberStrategy) {
		this.serializeNulls = serializeNulls;
		this.htmlSafe = htmlSafe;
		this.prettyPrinting = prettyPrinting;
		this.lenient = lenient;
		this.serializeSpecialFloatingPointValues = serializeSpecialFloatingPointValues;
		this.longSerializationPolicy = longSerializationPolicy;
		this.builderFactories = builderFactories;
		this.builderHierarchyFactories = builderHierarchyFactories;
		this.objectToNumberStrategy = objectToNumberStrategy;
		this.numberToNumberStrategy = numberToNumberStrategy;

		List<TypeAdapterFactory> factories = new ArrayList<>();
		factories.add(TypeAdapters.JSON_ELEMENT_FACTORY);
		factories.add(ObjectTypeAdapter.getFactory(objectToNumberStrategy));
		factories.addAll(factoriesToBeAdded);
		factories.add(TypeAdapters.STRING_FACTORY);
		factories.add(TypeAdapters.INTEGER_FACTORY);
		factories.add(TypeAdapters.BOOLEAN_FACTORY);
		factories.add(TypeAdapters.BYTE_FACTORY);
		factories.add(TypeAdapters.SHORT_FACTORY);
		factories.add(TypeAdapters.newFactory(long.class, Long.class, longAdapter(longSerializationPolicy)));
		factories.add(TypeAdapters.newFactory(double.class, Double.class, doubleAdapter(serializeSpecialFloatingPointValues)));
		factories.add(TypeAdapters.newFactory(float.class, Float.class, floatAdapter(serializeSpecialFloatingPointValues)));
		factories.add(NumberTypeAdapter.getFactory(numberToNumberStrategy));
		factories.add(TypeAdapters.CHARACTER_FACTORY);
		factories.add(TypeAdapters.STRING_BUILDER_FACTORY);
		factories.add(TypeAdapters.STRING_BUFFER_FACTORY);
		factories.add(TypeAdapters.newFactory(BigDecimal.class, TypeAdapters.BIG_DECIMAL));
		factories.add(TypeAdapters.newFactory(BigInteger.class, TypeAdapters.BIG_INTEGER));
		factories.add(TypeAdapters.newFactory(LazilyParsedNumber.class, TypeAdapters.LAZILY_PARSED_NUMBER));
		factories.add(TypeAdapters.UUID_FACTORY);
		factories.add(TypeAdapters.BIT_SET_FACTORY);
		factories.add(ArrayTypeAdapter.FACTORY);
		factories.add(TypeAdapters.CLASS_FACTORY);
		factories.add(TypeAdapters.ENUM_FACTORY);
		this.factories = Collections.unmodifiableList(factories);
	}

	public GsonBuilder newBuilder() {
		return new GsonBuilder(this);
	}

	public boolean serializeNulls() {
		return serializeNulls;
	}

	public boolean htmlSafe() {
		return htmlSafe;
	}

	private TypeAdapter<Number> doubleAdapter(boolean serializeSpecialFloatingPointValues) {
		if(serializeSpecialFloatingPointValues) {
			return TypeAdapters.DOUBLE;
		}
		return new TypeAdapter<Number>() {
			@Override
			public Double read(JsonReader in) throws IOException {
				if(in.peek() == JsonToken.NULL) {
					in.nextNull();
					return null;
				}
				return in.nextDouble();
			}

			@Override
			public void write(JsonWriter out, Number value) throws IOException {
				if(value == null) {
					out.nullValue();
					return;
				}
				double doubleValue = value.doubleValue();
				checkValidFloatingPoint(doubleValue);
				out.value(doubleValue);
			}
		};
	}

	private TypeAdapter<Number> floatAdapter(boolean serializeSpecialFloatingPointValues) {
		if(serializeSpecialFloatingPointValues) {
			return TypeAdapters.FLOAT;
		}
		return new TypeAdapter<Number>() {
			@Override
			public Float read(JsonReader in) throws IOException {
				if(in.peek() == JsonToken.NULL) {
					in.nextNull();
					return null;
				}
				return (float) in.nextDouble();
			}

			@Override
			public void write(JsonWriter out, Number value) throws IOException {
				if(value == null) {
					out.nullValue();
					return;
				}
				float floatValue = value.floatValue();
				checkValidFloatingPoint(floatValue);
				out.value(value instanceof Float ? value : floatValue);
			}
		};
	}

	static void checkValidFloatingPoint(double value) {
		if(Double.isNaN(value) || Double.isInfinite(value)) {
			throw new IllegalArgumentException(value + " is not a valid double value as per JSON specification");
		}
	}

	private static TypeAdapter<Number> longAdapter(LongSerializationPolicy longSerializationPolicy) {
		if(longSerializationPolicy == LongSerializationPolicy.DEFAULT) {
			return TypeAdapters.LONG;
		}
		return new TypeAdapter<Number>() {
			@Override
			public Number read(JsonReader in) throws IOException {
				if(in.peek() == JsonToken.NULL) {
					in.nextNull();
					return null;
				}
				return in.nextLong();
			}

			@Override
			public void write(JsonWriter out, Number value) throws IOException {
				if(value == null) {
					out.nullValue();
					return;
				}
				out.value(value.toString());
			}
		};
	}

	@SuppressWarnings("unchecked")
	public synchronized <T> TypeAdapter<T> getAdapter(TypeToken<T> type) {
		TypeAdapter<?> cached = typeTokenCache.get(type);
		if(cached != null) {
			return (TypeAdapter<T>) cached;
		}
		boolean isInitialAdapterRequest = false;
		if(pendingCalls == null) {
			pendingCalls = new HashMap<>();
			isInitialAdapterRequest = true;
		}else {
			TypeAdapter<T> ongoingCall = (TypeAdapter<T>) pendingCalls.get(type);
			if(ongoingCall != null) {
				return ongoingCall;
			}
		}
		TypeAdapter<T> candidate = null;
		Map<TypeToken<?>, TypeAdapter<?>> calls = pendingCalls;
		try {
			FutureTypeAdapter<T> call = new FutureTypeAdapter<>();
			calls.put(type, call);
			for(int i = 0, l = factories.size(); i < l; ++i) {
				candidate = factories.get(i).create(this, type);
				if(candidate != null) {
					call.setDelegate(candidate);
					calls.put(type, candidate);
					break;
				}
			}
		}finally {
			if(isInitialAdapterRequest) {
				pendingCalls = null;
			}
		}
		if(candidate == null) {
			throw new IllegalArgumentException("GSON cannot handle " + type);
		}
		if(isInitialAdapterRequest) {
			typeTokenCache.putAll(calls);
		}
		return candidate;
	}

	public <T> TypeAdapter<T> getDelegateAdapter(TypeAdapterFactory skipPast, TypeToken<T> type) {
		boolean skipPastFound = !factories.contains(skipPast);
		for(int i = 0, l = factories.size(); i < l; ++i) {
			TypeAdapterFactory factory = factories.get(i);
			if(!skipPastFound) {
				if(factory == skipPast) {
					skipPastFound = true;
				}
				continue;
			}
			TypeAdapter<T> candidate = factory.create(this, type);
			if(candidate != null) {
				return candidate;
			}
		}
		throw new IllegalArgumentException("GSON cannot serialize " + type);
	}

	public <T> TypeAdapter<T> getAdapter(Class<T> type) {
		return getAdapter(TypeToken.get(type));
	}

	public JsonElement toJsonTree(Object src) {
		if(src == null) {
			return JsonNull.INSTANCE;
		}
		return toJsonTree(src, src.getClass());
	}

	public JsonElement toJsonTree(Object src, Type typeOfSrc) {
		JsonTreeWriter writer = new JsonTreeWriter();
		toJson(src, typeOfSrc, writer);
		return writer.get();
	}

	public String toJson(Object src) {
		if(src == null) {
			return toJson(JsonNull.INSTANCE);
		}
		return toJson(src, src.getClass());
	}

	public String toJson(Object src, Type typeOfSrc) {
		StringWriter writer = new StringWriter();
		toJson(src, typeOfSrc, writer);
		return writer.toString();
	}

	public void toJson(Object src, Appendable writer) throws JsonIOException {
		if(src != null) {
			toJson(src, src.getClass(), writer);
		}else {
			toJson(JsonNull.INSTANCE, writer);
		}
	}

	public void toJson(Object src, Type typeOfSrc, Appendable writer) throws JsonIOException {
		try {
			toJson(src, typeOfSrc, newJsonWriter(Streams.writerForAppendable(writer)));
		}catch(IOException e) {
			throw new JsonIOException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void toJson(Object src, Type typeOfSrc, JsonWriter writer) throws JsonIOException {
		TypeAdapter<Object> adapter = (TypeAdapter<Object>) getAdapter(TypeToken.get(typeOfSrc));
		boolean oldLenient = writer.isLenient();
		writer.setLenient(true);
		boolean oldHtmlSafe = writer.isHtmlSafe();
		writer.setHtmlSafe(htmlSafe);
		boolean oldSerializeNulls = writer.getSerializeNulls();
		writer.setSerializeNulls(serializeNulls);
		try {
			adapter.write(writer, src);
		}catch(IOException e) {
			throw new JsonIOException(e);
		}finally {
			writer.setLenient(oldLenient);
			writer.setHtmlSafe(oldHtmlSafe);
			writer.setSerializeNulls(oldSerializeNulls);
		}
	}

	public String toJson(JsonElement jsonElement) {
		StringWriter writer = new StringWriter();
		toJson(jsonElement, writer);
		return writer.toString();
	}

	public void toJson(JsonElement jsonElement, Appendable writer) throws JsonIOException {
		try {
			toJson(jsonElement, newJsonWriter(Streams.writerForAppendable(writer)));
		}catch(IOException e) {
			throw new JsonIOException(e);
		}
	}

	public JsonWriter newJsonWriter(Writer writer) throws IOException {
		JsonWriter jsonWriter = new JsonWriter(writer);
		if(prettyPrinting) {
			jsonWriter.setIndent("  ");
		}
		jsonWriter.setHtmlSafe(htmlSafe);
		jsonWriter.setLenient(lenient);
		jsonWriter.setSerializeNulls(serializeNulls);
		return jsonWriter;
	}

	public JsonReader newJsonReader(Reader reader) {
		JsonReader jsonReader = new JsonReader(reader);
		jsonReader.setLenient(lenient);
		return jsonReader;
	}

	public void toJson(JsonElement jsonElement, JsonWriter writer) throws JsonIOException {
		boolean oldLenient = writer.isLenient();
		writer.setLenient(true);
		boolean oldHtmlSafe = writer.isHtmlSafe();
		writer.setHtmlSafe(htmlSafe);
		boolean oldSerializeNulls = writer.getSerializeNulls();
		writer.setSerializeNulls(serializeNulls);
		try {
			Streams.write(jsonElement, writer);
		}catch(IOException e) {
			throw new JsonIOException(e);
		}finally {
			writer.setLenient(oldLenient);
			writer.setHtmlSafe(oldHtmlSafe);
			writer.setSerializeNulls(oldSerializeNulls);
		}
	}

	public <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
		return Primitives.wrap(classOfT).cast(fromJson(json, TypeToken.get(classOfT)));
	}

	@SuppressWarnings("unchecked")
	public <T> T fromJson(String json, Type typeOfT) throws JsonSyntaxException {
		return (T) fromJson(json, TypeToken.get(typeOfT));
	}

	public <T> T fromJson(String json, TypeToken<T> typeOfT) throws JsonSyntaxException {
		if(json == null) {
			return null;
		}
		return fromJson(new StringReader(json), typeOfT);
	}

	public <T> T fromJson(Reader json, Class<T> classOfT) throws JsonSyntaxException, JsonIOException {
		return Primitives.wrap(classOfT).cast(fromJson(json, TypeToken.get(classOfT)));
	}

	@SuppressWarnings("unchecked")
	public <T> T fromJson(Reader json, Type typeOfT) throws JsonIOException, JsonSyntaxException {
		return (T) fromJson(json, TypeToken.get(typeOfT));
	}

	public <T> T fromJson(Reader json, TypeToken<T> typeOfT) throws JsonIOException, JsonSyntaxException {
		JsonReader jsonReader = newJsonReader(json);
		T object = fromJson(jsonReader, typeOfT);
		assertFullConsumption(object, jsonReader);
		return object;
	}

	private static void assertFullConsumption(Object obj, JsonReader reader) {
		try {
			if(obj != null && reader.peek() != JsonToken.END_DOCUMENT) {
				throw new JsonSyntaxException("JSON document was not fully consumed.");
			}
		}catch(MalformedJsonException e) {
			throw new JsonSyntaxException(e);
		}catch(IOException e) {
			throw new JsonIOException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T fromJson(JsonReader reader, Type typeOfT) throws JsonIOException, JsonSyntaxException {
		return (T) fromJson(reader, TypeToken.get(typeOfT));
	}

	public <T> T fromJson(JsonReader reader, TypeToken<T> typeOfT) throws JsonIOException, JsonSyntaxException {
		boolean isEmpty = true;
		boolean oldLenient = reader.isLenient();
		reader.setLenient(true);
		try {
			reader.peek();
			isEmpty = false;
			return getAdapter(typeOfT).read(reader);
		}catch(EOFException e) {
			if(isEmpty) {
				return null;
			}
			throw new JsonSyntaxException(e);
		}catch(IllegalStateException e) {
			throw new JsonSyntaxException(e);
		}catch(IOException e) {
			throw new JsonSyntaxException(e);
		}finally {
			reader.setLenient(oldLenient);
		}
	}

	public <T> T fromJson(JsonElement json, Class<T> classOfT) throws JsonSyntaxException {
		return Primitives.wrap(classOfT).cast(fromJson(json, TypeToken.get(classOfT)));
	}

	@SuppressWarnings("unchecked")
	public <T> T fromJson(JsonElement json, Type typeOfT) throws JsonSyntaxException {
		return (T) fromJson(json, TypeToken.get(typeOfT));
	}

	public <T> T fromJson(JsonElement json, TypeToken<T> typeOfT) throws JsonSyntaxException {
		if(json == null) {
			return null;
		}
		return fromJson(new JsonTreeReader(json), typeOfT);
	}

	static class FutureTypeAdapter<T> extends SerializationDelegatingTypeAdapter<T> {
		private TypeAdapter<T> delegate = null;

		public void setDelegate(TypeAdapter<T> typeAdapter) {
			if(delegate != null) {
				throw new AssertionError("Delegate is already set");
			}
			delegate = typeAdapter;
		}

		private TypeAdapter<T> delegate() {
			if(delegate == null) {
				throw new IllegalStateException("Adapter for type with cyclic dependency has been used before dependency has been resolved");
			}
			return delegate;
		}

		@Override
		public TypeAdapter<T> getSerializationDelegate() {
			return delegate();
		}

		@Override
		public T read(JsonReader in) throws IOException {
			return delegate().read(in);
		}

		@Override
		public void write(JsonWriter out, T value) throws IOException {
			delegate().write(out, value);
		}
	}

	@Override
	public String toString() {
		return "{serializeNulls:" + serializeNulls + ",factories:" + factories + "}";
	}
}
