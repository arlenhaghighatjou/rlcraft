package com.alcatrazescapee.notreepunching.common.items;

import com.alcatrazescapee.alcatrazcore.item.tool.ItemAxeCore;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;

@ParametersAreNonnullByDefault
public class ItemSaw extends ItemAxeCore {
   public ItemSaw(ToolMaterial material) {
      super(material, Math.max(material.getAttackDamage() - 1.0F, 1.0F), -2.8F);
      this.setNoRepair();
      this.setContainerItem(this);
   }

   @Nonnull
   public ItemStack getContainerItem(ItemStack stack) {
      ItemStack copy = stack.copy();
      copy.setItemDamage(copy.getItemDamage() + 1);
      return copy.getItemDamage() >= copy.getMaxDamage() ? ItemStack.EMPTY : copy;
   }

   public boolean hasContainerItem(ItemStack stack) {
      return true;
   }
}
