package net.minecraft.network.login.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SPacketEncryptionRequest implements Packet<INetHandlerLoginClient>
{
    private String hashedServerId;
    private byte[] verifyToken;

    public SPacketEncryptionRequest()
    {
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.hashedServerId = buf.readString(20);
        buf.readByteArray(1024);
        this.verifyToken = buf.readByteArray(1024);
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
    }

    public void processPacket(INetHandlerLoginClient handler)
    {
        handler.handleEncryptionRequest(this);
    }

    @SideOnly(Side.CLIENT)
    public String getServerId()
    {
        return this.hashedServerId;
    }

    @SideOnly(Side.CLIENT)
    public byte[] getVerifyToken()
    {
        return this.verifyToken;
    }
}
