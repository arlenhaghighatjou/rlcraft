package org.jwaresoftware.mcmods.lib.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface ICurative {
   default boolean onCureDirectly(EntityPlayer player, ItemStack curative) {
      return false;
   }
}
