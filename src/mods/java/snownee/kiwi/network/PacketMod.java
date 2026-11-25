package snownee.kiwi.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface PacketMod {
   void writeDataTo(ByteBuf var1);

   void readDataFrom(ByteBuf var1);

   @SideOnly(Side.CLIENT)
   void handleClient(EntityPlayerSP var1);

   void handleServer(EntityPlayerMP var1);
}
