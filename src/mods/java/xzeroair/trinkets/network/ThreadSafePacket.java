package xzeroair.trinkets.network;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class ThreadSafePacket extends BasicPacket {
   public ThreadSafePacket() {
   }

   public ThreadSafePacket(int entityID, NBTTagCompound tag) {
      super(entityID, tag);
   }

   public final IMessage handleClient(final NetHandlerPlayClient client) {
      FMLCommonHandler.instance().getWorldThread(client).addScheduledTask(new Runnable() {
         public void run() {
            ThreadSafePacket.this.handleClientSafe(client);
         }
      });
      return null;
   }

   public final IMessage handleServer(final NetHandlerPlayServer server) {
      FMLCommonHandler.instance().getWorldThread(server).addScheduledTask(new Runnable() {
         public void run() {
            ThreadSafePacket.this.handleServerSafe(server);
         }
      });
      return null;
   }

   public abstract void handleClientSafe(NetHandlerPlayClient var1);

   public abstract void handleServerSafe(NetHandlerPlayServer var1);
}
