package net.minecraft.server.management;

import java.io.File;

import com.google.gson.JsonObject;

import net.lax1dude.eaglercraft.v1_8.mojang.authlib.GameProfile;

public class UserListWhitelist extends UserList<GameProfile, UserListWhitelistEntry>
{
	public UserListWhitelist(File file) { super(file); }
	protected UserListEntry<GameProfile> createEntry(JsonObject json) { return new UserListWhitelistEntry(json); }
	protected String getObjectKey(GameProfile profile) { return profile.getId().toString(); }
	public boolean isWhitelisted(GameProfile profile) { return hasEntry(profile); }
	public GameProfile getByName(String name)
	{
		for (UserListWhitelistEntry entry : getValues().values()) if (name.equalsIgnoreCase(entry.getValue().getName())) return entry.getValue();
		return null;
	}
	public String[] getKeys()
	{
		String[] result = new String[getValues().size()]; int i = 0;
		for (UserListWhitelistEntry entry : getValues().values()) result[i++] = entry.getValue().getName();
		return result;
	}
}
