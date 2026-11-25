package vazkii.quark.base.network.message;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBow;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.arl.network.NetworkMessage;
import vazkii.quark.management.feature.RotateArrowTypes;

public class MessageRotateArrows extends NetworkMessage<MessageRotateArrows> {
   public int direction;

   public MessageRotateArrows() {
   }

   public MessageRotateArrows(int direction) {
      this.direction = direction;
   }

   public IMessage handleMessage(MessageContext context) {
      EntityPlayerMP player = context.getServerHandler().player;
      player.getServerWorld().addScheduledTask(() -> {
         ItemBow bowItem = RotateArrowTypes.getHeldBow(player);
         if (bowItem != null) {
            RotateArrowTypes.rotateArrows(bowItem, player, this.direction);
         }

      });
      return null;
   }
}
