package com.lycanitesmobs.core.network;

import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessagePlayerLeftClick implements IMessage {
   public MessagePlayerLeftClick() {
   }

   public static void onMessage(MessagePlayerLeftClick message, MessageContext ctx, EntityPlayer player) {
      EnumHand activeHand = player.getActiveHand();
      ItemStack activeStack = player.getHeldItem(activeHand);
      if (activeStack.getItem() instanceof ItemEquipment) {
         ((ItemEquipment)activeStack.getItem()).onItemLeftClick(player.getEntityWorld(), player, activeHand);
      }

   }

   public void fromBytes(ByteBuf buf) {
   }

   public void toBytes(ByteBuf buf) {
   }
}
