package net.mcft.copy.backpacks.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.mcft.copy.backpacks.api.BackpackHelper;
import net.mcft.copy.backpacks.api.IBackpack;
import net.mcft.copy.backpacks.config.ModConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageOpenBackpack implements IMessage {
   public MessageOpenBackpack() {
   }

   public static MessageOpenBackpack create() {
      return new MessageOpenBackpack();
   }

   public void fromBytes(ByteBuf buf) {
   }

   public void toBytes(ByteBuf buf) {
   }

   public static class Handler extends BackpacksMessageHandler<MessageOpenBackpack> {
      public Handler() {
      }

      public void handle(MessageOpenBackpack message, MessageContext ctx) {
         EntityPlayer player = getPlayer(ctx);
         IBackpack backpack = BackpackHelper.getBackpack(player);
         if (backpack != null && player.isEntityAlive() && ModConfig.server.enableSelfInteraction) {
            backpack.getType().onEquippedInteract(player, player, backpack);
         }

      }
   }
}
