package ivorius.reccomplex.network;

import ivorius.ivtoolkit.network.SchedulingMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class PacketEditInventoryItemHandler<P extends PacketEditInventoryItem> extends SchedulingMessageHandler<P, IMessage> {
   public PacketEditInventoryItemHandler() {
   }

   public void processServer(P message, MessageContext ctx, WorldServer server) {
      NetHandlerPlayServer playServer = ctx.getServerHandler();
      EntityPlayerMP player = playServer.player;
      this.affectItem(player, player.inventory.getStackInSlot(message.getInventorySlot()), message);
      player.openContainer.detectAndSendChanges();
   }

   public abstract void affectItem(EntityPlayerMP var1, ItemStack var2, P var3);
}
