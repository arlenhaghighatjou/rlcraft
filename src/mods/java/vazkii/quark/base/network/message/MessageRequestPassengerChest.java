package vazkii.quark.base.network.message;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.arl.network.NetworkMessage;
import vazkii.quark.management.entity.EntityChestPassenger;

public class MessageRequestPassengerChest extends NetworkMessage<MessageRequestPassengerChest> {
   public MessageRequestPassengerChest() {
   }

   public IMessage handleMessage(MessageContext context) {
      EntityPlayer player = context.getServerHandler().player;
      if (player.isRiding() && player.openContainer == player.inventoryContainer) {
         Entity riding = player.getRidingEntity();
         if (riding instanceof EntityBoat) {
            for(Entity passenger : riding.getPassengers()) {
               if (passenger instanceof EntityChestPassenger) {
                  player.displayGUIChest((EntityChestPassenger)passenger);
               }
            }
         }
      }

      return null;
   }
}
