package dev.satyrn.wolfarmor.api.util;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ObjectHolderRegistry;
import net.minecraft.item.crafting.IRecipe;

public abstract class Recipes {
   public static IRecipe LEATHER_ARMOR_DYES = null;

   public Recipes() {
   }

   public static void registerHolders() {
      ObjectHolderRegistry.INSTANCE.register(IRecipe.class, new ResourceLocation("wolfarmor:leather_wolf_armor_dyed"), v -> LEATHER_ARMOR_DYES = v);
   }
}
