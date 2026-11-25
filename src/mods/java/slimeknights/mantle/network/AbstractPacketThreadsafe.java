package slimeknights.mantle.network;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class AbstractPacketThreadsafe extends AbstractPacket {
   public AbstractPacketThreadsafe() {
   }

   public final IMessage handleClient(final NetHandlerPlayClient netHandler) {
      FMLCommonHandler.instance().getWorldThread(netHandler).addScheduledTask(new Runnable() {
         public void run() {
            AbstractPacketThreadsafe.this.handleClientSafe(netHandler);
         }
      });
      return null;
   }

   public final IMessage handleServer(final NetHandlerPlayServer netHandler) {
      FMLCommonHandler.instance().getWorldThread(netHandler).addScheduledTask(new Runnable() {
         public void run() {
            AbstractPacketThreadsafe.this.handleServerSafe(netHandler);
         }
      });
      return null;
   }

   public abstract void handleClientSafe(NetHandlerPlayClient var1);

   public abstract void handleServerSafe(NetHandlerPlayServer var1);
}
