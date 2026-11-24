package net.minecraft.server.management;

import java.io.File;

import com.google.gson.JsonObject;

import net.lax1dude.eaglercraft.v1_8.mojang.authlib.GameProfile;

public class UserListOps extends UserList<GameProfile, UserListOpsEntry>
{
	public UserListOps(File file) { super(file); }
	protected UserListEntry<GameProfile> createEntry(JsonObject json) { return new UserListOpsEntry(json); }
	protected String getObjectKey(GameProfile profile) { return profile.getId().toString(); }
	public int getPermissionLevel(GameProfile profile) { UserListOpsEntry entry = getEntry(profile); return entry == null ? 0 : entry.getPermissionLevel(); }
	public boolean bypassesPlayerLimit(GameProfile profile) { UserListOpsEntry entry = getEntry(profile); return entry != null && entry.bypassesPlayerLimit(); }
	public GameProfile getGameProfileFromName(String name)
	{
		for (UserListOpsEntry entry : getValues().values()) if (name.equalsIgnoreCase(entry.getValue().getName())) return entry.getValue();
		return null;
	}
	public String[] getKeys()
	{
		String[] result = new String[getValues().size()]; int i = 0;
		for (UserListOpsEntry entry : getValues().values()) result[i++] = entry.getValue().getName();
		return result;
	}
}
