package net.minecraft.server.management;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.io.Files;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.lax1dude.eaglercraft.v1_8.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerProfileCache
{
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
	private final Map<String, ProfileEntry> byName = new LinkedHashMap<>();
	private final Map<EaglercraftUUID, ProfileEntry> byUUID = new LinkedHashMap<>();
	private final File cacheFile;

	public PlayerProfileCache(File file)
	{
		cacheFile = file;
		load();
	}

	public void addEntry(GameProfile profile)
	{
		Calendar expiration = Calendar.getInstance();
		expiration.add(Calendar.MONTH, 1);
		addEntry(profile, expiration.getTime(), true);
	}

	private void addEntry(GameProfile profile, Date expiration, boolean save)
	{
		if (profile == null || profile.getId() == null || profile.getName() == null) return;
		ProfileEntry entry = new ProfileEntry(profile, expiration);
		ProfileEntry old = byUUID.put(profile.getId(), entry);
		if (old != null) byName.remove(old.profile.getName().toLowerCase(Locale.ROOT));
		byName.put(profile.getName().toLowerCase(Locale.ROOT), entry);
		if (save) save();
	}

	@Nullable
	public GameProfile getGameProfileForUsername(String username)
	{
		String key = username.toLowerCase(Locale.ROOT);
		ProfileEntry entry = byName.get(key);
		if (entry != null && entry.expiration.before(new Date()))
		{
			byName.remove(key);
			byUUID.remove(entry.profile.getId());
			entry = null;
		}
		if (entry == null)
		{
			GameProfile profile = new GameProfile(null, username);
			profile = new GameProfile(EntityPlayer.getUUID(profile), username);
			addEntry(profile);
			return profile;
		}
		return entry.profile;
	}

	@Nullable
	public GameProfile getProfileByUUID(EaglercraftUUID uuid)
	{
		ProfileEntry entry = byUUID.get(uuid);
		return entry == null ? null : entry.profile;
	}

	public String[] getUsernames()
	{
		String[] names = new String[byName.size()];
		int i = 0;
		for (ProfileEntry entry : byName.values()) names[i++] = entry.profile.getName();
		return names;
	}

	public void load()
	{
		if (!cacheFile.isFile()) return;
		try
		{
			JsonArray array = new JsonParser().parse(Files.toString(cacheFile, StandardCharsets.UTF_8)).getAsJsonArray();
			for (JsonElement element : array)
			{
				JsonObject json = element.getAsJsonObject();
				try
				{
					GameProfile profile = new GameProfile(EaglercraftUUID.fromString(json.get("uuid").getAsString()), json.get("name").getAsString());
					Date expiration = DATE_FORMAT.parse(json.get("expiresOn").getAsString());
					addEntry(profile, expiration, false);
				}
				catch (ParseException | IllegalArgumentException ex)
				{
				}
			}
		}
		catch (IOException | RuntimeException ex)
		{
		}
	}

	public void save()
	{
		JsonArray array = new JsonArray();
		for (ProfileEntry entry : byName.values())
		{
			JsonObject json = new JsonObject();
			json.addProperty("name", entry.profile.getName());
			json.addProperty("uuid", entry.profile.getId().toString());
			json.addProperty("expiresOn", DATE_FORMAT.format(entry.expiration));
			array.add(json);
		}
		try { Files.write(array.toString(), cacheFile, StandardCharsets.UTF_8); } catch (IOException ex) { }
	}

	private static class ProfileEntry
	{
		final GameProfile profile;
		final Date expiration;
		ProfileEntry(GameProfile profile, Date expiration) { this.profile = profile; this.expiration = expiration; }
	}
}
