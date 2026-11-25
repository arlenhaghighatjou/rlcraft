package com.teammetallurgy.aquaculture.items;

import javax.annotation.Nonnull;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.Item.ToolMaterial;

public class AquaItemPickaxe extends ItemPickaxe {
   public AquaItemPickaxe(ToolMaterial toolMaterial) {
      super(toolMaterial);
   }

   @Nonnull
   public Item setUnlocalizedName(@Nonnull String name) {
      super.setUnlocalizedName(name);
      return this;
   }
}
