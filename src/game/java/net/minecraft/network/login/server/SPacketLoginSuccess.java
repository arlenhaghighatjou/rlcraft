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
	private int selectedProtocol = 3;

    public SPacketLoginSuccess()
    {
    }

    public SPacketLoginSuccess(GameProfile profileIn)
    {
        this.profile = profileIn;
    }

	public SPacketLoginSuccess(GameProfile profile, int selectedProtocol)
	{
		this(profile);
		this.selectedProtocol = selectedProtocol;
	}

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        String s = buf.readString(36);
        String s1 = buf.readString(16);
        EaglercraftUUID uuid = s.length() > 0 ? EaglercraftUUID.fromString(s) : null;
        this.profile = new GameProfile(uuid, s1);
		this.selectedProtocol = buf.isReadable() ? buf.readUnsignedShort() : 3;
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        EaglercraftUUID uuid = this.profile.getId();
        buf.writeString(uuid == null ? "" : uuid.toString());
        buf.writeString(this.profile.getName());
		if (this.selectedProtocol != 3) buf.writeShort(this.selectedProtocol);
    }

	public int getSelectedProtocol()
	{
		return this.selectedProtocol;
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
