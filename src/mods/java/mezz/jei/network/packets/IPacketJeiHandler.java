package mezz.jei.network.packets;

import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

public interface IPacketJeiHandler {
   void readPacketData(PacketBuffer var1, EntityPlayer var2) throws IOException;
}
