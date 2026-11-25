package baubles.common.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOpenNormalInventory implements IMessage, IMessageHandler<PacketOpenNormalInventory, IMessage> {
   public PacketOpenNormalInventory() {
   }

   public void toBytes(ByteBuf buffer) {
   }

   public void fromBytes(ByteBuf buffer) {
   }

   public IMessage onMessage(PacketOpenNormalInventory message, final MessageContext ctx) {
      IThreadListener mainThread = (WorldServer)ctx.getServerHandler().player.world;
      mainThread.addScheduledTask(new Runnable() {
         public void run() {
            ctx.getServerHandler().player.openContainer.onContainerClosed(ctx.getServerHandler().player);
            ctx.getServerHandler().player.openContainer = ctx.getServerHandler().player.inventoryContainer;
         }
      });
      return null;
   }
}
