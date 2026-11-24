package net.minecraft.server.management;

import java.io.File;

import com.google.gson.JsonObject;

import net.lax1dude.eaglercraft.v1_8.mojang.authlib.GameProfile;

public class UserListBans extends UserList<GameProfile, UserListBansEntry>
{
	public UserListBans(File file) { super(file); }
	protected UserListEntry<GameProfile> createEntry(JsonObject json) { return new UserListBansEntry(json); }
	public boolean isBanned(GameProfile profile) { return hasEntry(profile); }
	protected String getObjectKey(GameProfile profile) { return profile.getId().toString(); }

	public String[] getKeys()
	{
		String[] result = new String[getValues().size()];
		int i = 0;
		for (UserListBansEntry entry : getValues().values()) result[i++] = entry.getValue().getName();
		return result;
	}

	public GameProfile getBannedProfile(String name)
	{
		for (UserListBansEntry entry : getValues().values()) if (name.equalsIgnoreCase(entry.getValue().getName())) return entry.getValue();
		return null;
	}
}
