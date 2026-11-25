package com.teammetallurgy.aquaculture.items;

import javax.annotation.Nonnull;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.Item.ToolMaterial;

public class AquaItemSpade extends ItemSpade {
   public AquaItemSpade(ToolMaterial toolMaterial) {
      super(toolMaterial);
   }

   @Nonnull
   public Item setUnlocalizedName(@Nonnull String name) {
      super.setUnlocalizedName(name);
      return this;
   }
}
