package vazkii.quark.base.network.message;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.arl.network.NetworkMessage;
import vazkii.quark.base.Quark;
import vazkii.quark.oddities.inventory.ContainerBackpack;

public class MessageHandleBackpack extends NetworkMessage<MessageHandleBackpack> {
   public boolean open;

   public MessageHandleBackpack() {
   }

   public MessageHandleBackpack(boolean open) {
      this.open = open;
   }

   public IMessage handleMessage(MessageContext context) {
      EntityPlayerMP player = context.getServerHandler().player;
      player.mcServer.addScheduledTask(() -> {
         if (this.open) {
            player.openGui(Quark.instance, 2, player.world, 0, 0, 0);
         } else {
            ContainerBackpack.saveCraftingInventory(player);
            player.openContainer = player.inventoryContainer;
         }

      });
      return null;
   }
}
