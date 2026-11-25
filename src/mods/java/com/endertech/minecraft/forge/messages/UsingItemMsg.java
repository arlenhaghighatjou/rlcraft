package com.endertech.minecraft.forge.messages;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UsingItemMsg extends ForgeNetMsg {
   public UsingItemMsg.Type type = UsingItemMsg.Type.start;

   public UsingItemMsg() {
   }

   public UsingItemMsg(UsingItemMsg.Type type) {
      this.type = type;
   }

   public static class Handler extends ForgeNetMsgHandler<UsingItemMsg> {
      public Handler() {
      }

      public IMessage onMessage(UsingItemMsg message, MessageContext ctx) {
         World world = this.getWorld(ctx);
         EntityPlayer player = this.getPlayer(ctx);
         if (world != null && player != null) {
            this.addScheduledTask(ctx, () -> {
               ItemStack stack = player.inventory.getCurrentItem();
               if (stack != null && stack.getItem() != null) {
                  switch(message.type) {
                  case start:
                     stack.getItem().onItemRightClick(world, player, EnumHand.MAIN_HAND);
                     break;
                  case end:
                     stack.getItem().onPlayerStoppedUsing(stack, world, player, Integer.MAX_VALUE);
                  }
               }

            });
         }

         return null;
      }
   }

   public static enum Type {
      start,
      end;

      private Type() {
      }
   }
}
