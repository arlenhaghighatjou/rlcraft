package com.teamacronymcoders.base.items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class ItemEventHandler {
   public ItemEventHandler() {
   }

   @SubscribeEvent
   public void entityRightClicked(EntityInteractSpecific entityInteract) {
      ItemStack itemStack = entityInteract.getItemStack();
      if (!itemStack.isEmpty() && itemStack.getItem() instanceof IRightClickEntity && ((IRightClickEntity)itemStack.getItem()).rightClickEntity(itemStack, entityInteract.getTarget(), entityInteract.getEntityPlayer(), entityInteract.getEntityPlayer().getEntityWorld(), entityInteract.getHand())) {
         entityInteract.setResult(Result.DENY);
      }

   }
}
