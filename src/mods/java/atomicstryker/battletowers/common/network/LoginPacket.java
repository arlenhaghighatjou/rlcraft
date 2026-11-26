package atomicstryker.battletowers.common.network;

import atomicstryker.battletowers.common.AS_BattleTowersCore;
import atomicstryker.battletowers.common.network.NetworkHelper.IPacket;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import com.arlenh7.eaglercraft.netty.ChannelHandlerContext;

public class LoginPacket implements IPacket
{

    @Override
    public void writeBytes(ChannelHandlerContext ctx, ByteBuf bytes)
    {
        bytes.writeByte(AS_BattleTowersCore.instance.towerDestroyerEnabled);
    }
    
    @Override
    public void readBytes(ChannelHandlerContext ctx, ByteBuf bytes)
    {
        AS_BattleTowersCore.instance.towerDestroyerEnabled = bytes.readByte();   
    }
    
}