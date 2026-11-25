package com.creativemd.creativecore.common.gui.premade;

import com.creativemd.creativecore.common.gui.container.SubContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public abstract class SubContainerHeldItem extends SubContainer {
   public ItemStack stack;
   public int index;

   public SubContainerHeldItem(EntityPlayer player, ItemStack stack, int index) {
      super(player);
      this.index = index;
      this.stack = stack;
   }

   public void addPlayerSlotsToContainer(EntityPlayer player) {
      this.addPlayerSlotsToContainer(player, this.index);
   }

   public void addPlayerSlotsToContainer(EntityPlayer player, int x, int y) {
      this.addPlayerSlotsToContainer(player, x, y, this.index);
   }
}
