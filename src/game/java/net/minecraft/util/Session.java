package net.minecraft.util;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.lax1dude.eaglercraft.v1_8.mojang.authlib.GameProfile;
import net.lax1dude.eaglercraft.v1_8.profile.EaglerProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Session
{
    private static final EaglercraftUUID outOfGameUUID;
    private GameProfile profile;

    public Session()
    {
        this.reset();
    }

    public GameProfile getProfile()
    {
        return this.profile;
    }

    public String getUsername()
    {
        return this.profile.getName();
    }

    public String getPlayerID()
    {
        return this.profile.getId().toString().replace("-", "");
    }

    public String getToken()
    {
        return "";
    }

    public String getSessionID()
    {
        return "token::" + this.getPlayerID();
    }

    public void update(String serverUsername, EaglercraftUUID uuid)
    {
        this.profile = new GameProfile(uuid, serverUsername);
    }

    public void reset()
    {
        this.update(EaglerProfile.getName(), outOfGameUUID);
    }

    public void setLAN()
    {
        this.update(EaglerProfile.getName(), EntityPlayer.getOfflineUUID(EaglerProfile.getName()));
    }

    static
    {
        byte[] bytes = new byte[16];
        (new EaglercraftRandom()).nextBytes(bytes);
        outOfGameUUID = new EaglercraftUUID(bytes);
    }
}
