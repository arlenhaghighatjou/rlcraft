package xzeroair.trinkets.network.trinketcontainer;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.network.ThreadSafePacket;

public class OpenTrinketGui extends ThreadSafePacket {
   private int guiID;

   public OpenTrinketGui() {
   }

   public OpenTrinketGui(int guiID) {
      this.guiID = guiID;
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.guiID);
   }

   public void fromBytes(ByteBuf buf) {
      this.guiID = buf.readInt();
   }

   public void handleClientSafe(NetHandlerPlayClient client) {
      Minecraft mc = Minecraft.getMinecraft();
      World world = mc.player.getEntityWorld();
      mc.player.openGui(Trinkets.instance, this.guiID, world, 0, 0, 0);
   }

   public void handleServerSafe(NetHandlerPlayServer server) {
      EntityPlayerMP entity = server.player;
      if (this.guiID == 99) {
         entity.openContainer.onContainerClosed(entity);
         entity.openContainer = entity.inventoryContainer;
      } else {
         entity.openContainer.onContainerClosed(entity);
         entity.openGui(Trinkets.instance, this.guiID, entity.world, 0, 0, 0);
      }

   }
}
