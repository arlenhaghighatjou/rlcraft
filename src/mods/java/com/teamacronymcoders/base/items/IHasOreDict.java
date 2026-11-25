package com.teamacronymcoders.base.items;

import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;

public interface IHasOreDict {
   @Nonnull
   Map<ItemStack, String> getOreDictNames(@Nonnull Map<ItemStack, String> var1);
}
