package net.minecraft.network.login.server;

import net.lax1dude.eaglercraft.v1_8.mojang.authlib.GameProfile;
import java.io.IOException;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SPacketLoginSuccess implements Packet<INetHandlerLoginClient>
{
    private GameProfile profile;

    public SPacketLoginSuccess()
    {
    }

    public SPacketLoginSuccess(GameProfile profileIn)
    {
        this.profile = profileIn;
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        String s = buf.readString(36);
        String s1 = buf.readString(16);
        EaglercraftUUID uuid = s.length() > 0 ? EaglercraftUUID.fromString(s) : null;
        this.profile = new GameProfile(uuid, s1);
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        EaglercraftUUID uuid = this.profile.getId();
        buf.writeString(uuid == null ? "" : uuid.toString());
        buf.writeString(this.profile.getName());
    }

    public void processPacket(INetHandlerLoginClient handler)
    {
        handler.handleLoginSuccess(this);
    }

    @SideOnly(Side.CLIENT)
    public GameProfile getProfile()
    {
        return this.profile;
    }
}
