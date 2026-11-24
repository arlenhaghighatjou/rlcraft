package net.minecraft.server.management;

import java.util.Date;

import com.google.gson.JsonObject;

import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.lax1dude.eaglercraft.v1_8.mojang.authlib.GameProfile;

public class UserListBansEntry extends UserListEntryBan<GameProfile>
{
	public UserListBansEntry(GameProfile profile) { this(profile, null, null, null, null); }
	public UserListBansEntry(GameProfile profile, Date start, String banner, Date end, String reason) { super(profile, start, banner, end, reason); }
	public UserListBansEntry(JsonObject json) { super(readProfile(json), json); }

	protected void onSerialization(JsonObject data)
	{
		if (getValue() != null)
		{
			data.addProperty("uuid", getValue().getId() == null ? "" : getValue().getId().toString());
			data.addProperty("name", getValue().getName());
			super.onSerialization(data);
		}
	}

	static GameProfile readProfile(JsonObject json)
	{
		if (!json.has("uuid") || !json.has("name")) return null;
		try { return new GameProfile(EaglercraftUUID.fromString(json.get("uuid").getAsString()), json.get("name").getAsString()); }
		catch (Throwable ex) { return null; }
	}
}
