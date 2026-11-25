package net.silentchaos512.lib.recipe;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;

@FunctionalInterface
public interface IRecipeSerializer {
   JsonObject serialize(ItemStack var1, Object... var2);
}
