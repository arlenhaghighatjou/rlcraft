package net.minecraft.server.management;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import net.minecraft.util.JsonUtils;

public class UserList<K, V extends UserListEntry<K>>
{
	private static final Logger LOGGER = LogManager.getLogger("UserList");
	private static final ParameterizedType ENTRY_LIST_TYPE = new ParameterizedType()
	{
		public Type[] getActualTypeArguments() { return new Type[] { UserListEntry.class }; }
		public Type getRawType() { return List.class; }
		public Type getOwnerType() { return null; }
	};
	protected final Gson gson;
	private final File saveFile;
	private final Map<String, V> values = new LinkedHashMap<>();
	private boolean lanServer = true;

	public UserList(File saveFile)
	{
		this.saveFile = saveFile;
		GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
		builder.registerTypeHierarchyAdapter(UserListEntry.class, new Serializer());
		gson = builder.create();
	}

	public boolean isLanServer() { return lanServer; }
	public void setLanServer(boolean state) { lanServer = state; }

	public void addEntry(V entry)
	{
		values.put(getObjectKey(entry.getValue()), entry);
		try { writeChanges(); } catch (IOException ex) { LOGGER.warn("Could not save user list", ex); }
	}

	public V getEntry(K value)
	{
		removeExpired();
		return values.get(getObjectKey(value));
	}

	public void removeEntry(K value)
	{
		values.remove(getObjectKey(value));
		try { writeChanges(); } catch (IOException ex) { LOGGER.warn("Could not save user list", ex); }
	}

	public File getSaveFile() { return saveFile; }
	public String[] getKeys() { return values.keySet().toArray(new String[values.size()]); }
	protected String getObjectKey(K value) { return value.toString(); }
	protected boolean hasEntry(K value) { return getEntry(value) != null; }
	protected UserListEntry<K> createEntry(JsonObject json) { return new UserListEntry<K>(null, json); }
	protected Map<String, V> getValues() { return values; }
	public boolean isEmpty() { return values.isEmpty(); }

	private void removeExpired()
	{
		List<String> expired = new ArrayList<>();
		for (Map.Entry<String, V> entry : values.entrySet()) if (entry.getValue().hasBanExpired()) expired.add(entry.getKey());
		for (String key : expired) values.remove(key);
	}

	public void writeChanges() throws IOException
	{
		File parent = saveFile.getParentFile();
		if (parent != null) parent.mkdirs();
		Files.write(gson.toJson(values.values()), saveFile, StandardCharsets.UTF_8);
	}

	@SuppressWarnings("unchecked")
	public void readSavedFile() throws IOException
	{
		if (!saveFile.exists()) return;
		Collection<UserListEntry<K>> loaded = JsonUtils.fromJson(gson, Files.toString(saveFile, StandardCharsets.UTF_8), ENTRY_LIST_TYPE, false);
		if (loaded != null)
		{
			values.clear();
			for (UserListEntry<K> entry : loaded) if (entry != null && entry.getValue() != null) values.put(getObjectKey(entry.getValue()), (V)entry);
		}
	}

	class Serializer implements JsonDeserializer<UserListEntry<K>>, JsonSerializer<UserListEntry<K>>
	{
		public JsonElement serialize(UserListEntry<K> entry, Type type, JsonSerializationContext context)
		{
			JsonObject json = new JsonObject();
			entry.onSerialization(json);
			return json;
		}

		public UserListEntry<K> deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException
		{
			return element.isJsonObject() ? UserList.this.createEntry(element.getAsJsonObject()) : null;
		}
	}
}
