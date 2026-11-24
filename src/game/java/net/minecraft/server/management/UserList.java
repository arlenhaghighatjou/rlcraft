package net.minecraft.server.management;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class UserList<K, V extends UserListEntry<K>>
{
	private static final Logger LOGGER = LogManager.getLogger("UserList");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private final VFile2 saveFile;
	private final Map<String, V> values = new LinkedHashMap<>();
	private boolean lanServer = true;

	public UserList(VFile2 saveFile)
	{
		this.saveFile = saveFile;
	}

	public boolean isLanServer() { return lanServer; }
	public void setLanServer(boolean state) { lanServer = state; }

	public void addEntry(V entry)
	{
		values.put(getObjectKey(entry.getValue()), entry);
		writeChanges();
	}

	public V getEntry(K value)
	{
		removeExpired();
		return values.get(getObjectKey(value));
	}

	public void removeEntry(K value)
	{
		values.remove(getObjectKey(value));
		writeChanges();
	}

	public VFile2 getSaveFile() { return saveFile; }
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

	public void writeChanges()
	{
		JsonArray array = new JsonArray();
		for (V entry : values.values())
		{
			JsonObject json = new JsonObject();
			entry.onSerialization(json);
			array.add(json);
		}
		saveFile.setAllChars(GSON.toJson(array));
	}

	@SuppressWarnings("unchecked")
	public void readSavedFile()
	{
		if (!saveFile.exists()) return;
		JsonArray array = JsonParser.parseString(saveFile.getAllChars()).getAsJsonArray();
		values.clear();
		for (JsonElement element : array)
		{
			if (!element.isJsonObject()) continue;
			UserListEntry<K> entry = createEntry(element.getAsJsonObject());
			if (entry != null && entry.getValue() != null) values.put(getObjectKey(entry.getValue()), (V)entry);
		}
	}
}
