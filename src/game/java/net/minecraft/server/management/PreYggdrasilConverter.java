package net.minecraft.server.management;

import net.lax1dude.eaglercraft.v1_8.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtils;

public final class PreYggdrasilConverter
{
	private PreYggdrasilConverter()
	{
	}

	public static String convertMobOwnerIfNeeded(MinecraftServer server, String username)
	{
		if (StringUtils.isNullOrEmpty(username) || username.length() > 16) return username;
		GameProfile profile = server == null ? null : server.getPlayerProfileCache().getGameProfileForUsername(username);
		if (profile == null) profile = new GameProfile(null, username);
		return EntityPlayer.getUUID(profile).toString();
	}
}
