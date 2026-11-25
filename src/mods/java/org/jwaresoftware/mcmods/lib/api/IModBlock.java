package org.jwaresoftware.mcmods.lib.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.properties.IProperty;
import net.minecraft.item.ItemStack;

public interface IModBlock {
   @Nullable
   default IProperty<?>[] getNonRenderingProperties() {
      return null;
   }

   @Nonnull
   String getUnlocalizedName(@Nonnull ItemStack var1);
}
