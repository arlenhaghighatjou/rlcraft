package com.mujmajnkraft.bettersurvival.integration;

import de.Whitedraco.switchbow.helper.ArrowItemStackEqual;
import de.Whitedraco.switchbow.helper.QuiverArrowHelper;
import de.Whitedraco.switchbow.helper.SwitchBowHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public abstract class SwitchBowCompat {
   public SwitchBowCompat() {
   }

   public static ItemStack findAmmo(EntityPlayer player, ItemStack bow) {
      return ArrowItemStackEqual.containsArrow(QuiverArrowHelper.getArrowsInInvAndQuiver(player, player.inventory), SwitchBowHelper.getSelectionArrow(bow)) ? SwitchBowHelper.getSelectionArrow(bow) : ItemStack.EMPTY;
   }
}
