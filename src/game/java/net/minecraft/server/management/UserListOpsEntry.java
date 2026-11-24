package net.minecraft.server.management;

import com.google.gson.JsonObject;

import net.lax1dude.eaglercraft.v1_8.mojang.authlib.GameProfile;

public class UserListOpsEntry extends UserListEntry<GameProfile>
{
	private final int permissionLevel;
	private final boolean bypassesPlayerLimit;

	public UserListOpsEntry(GameProfile player, int level, boolean bypass) { super(player); permissionLevel = level; bypassesPlayerLimit = bypass; }
	public UserListOpsEntry(JsonObject json)
	{
		super(UserListBansEntry.readProfile(json), json);
		permissionLevel = json.has("level") ? json.get("level").getAsInt() : 0;
		bypassesPlayerLimit = json.has("bypassesPlayerLimit") && json.get("bypassesPlayerLimit").getAsBoolean();
	}
	public int getPermissionLevel() { return permissionLevel; }
	public boolean bypassesPlayerLimit() { return bypassesPlayerLimit; }
	protected void onSerialization(JsonObject data)
	{
		if (getValue() != null)
		{
			data.addProperty("uuid", getValue().getId() == null ? "" : getValue().getId().toString());
			data.addProperty("name", getValue().getName());
			data.addProperty("level", permissionLevel);
			data.addProperty("bypassesPlayerLimit", bypassesPlayerLimit);
		}
	}
}
