package net.minecraft.server.management;

import com.google.gson.JsonObject;

import net.lax1dude.eaglercraft.v1_8.mojang.authlib.GameProfile;

public class UserListWhitelistEntry extends UserListEntry<GameProfile>
{
	public UserListWhitelistEntry(GameProfile profile) { super(profile); }
	public UserListWhitelistEntry(JsonObject json) { super(UserListBansEntry.readProfile(json), json); }
	protected void onSerialization(JsonObject data)
	{
		if (getValue() != null)
		{
			data.addProperty("uuid", getValue().getId() == null ? "" : getValue().getId().toString());
			data.addProperty("name", getValue().getName());
		}
	}
}
