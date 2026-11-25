package com.chaosthedude.realistictorches.handler;

import com.chaosthedude.realistictorches.RealisticTorches;
import com.chaosthedude.realistictorches.blocks.RealisticTorchesBlocks;
import com.chaosthedude.realistictorches.config.ConfigHandler;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

public class RecipeHandler {
   public RecipeHandler() {
   }

   public static void removeRecipes() {
      for(String name : ConfigHandler.removeRecipes) {
         Block block = Block.getBlockFromName(name);
         if (block != null) {
            removeRecipe(new ItemStack(block));
         } else {
            Item item = (Item)Item.REGISTRY.getObject(new ResourceLocation(name));
            if (item != null) {
               removeRecipe(new ItemStack(item));
            }
         }
      }

   }

   public static void removeRecipe(ItemStack s) {
      int recipeCount = 0;

      for(IRecipe currentRecipe : ForgeRegistries.RECIPES) {
         ItemStack output = currentRecipe.getRecipeOutput();
         if (output != null && output.getItem() == s.getItem() && !currentRecipe.getRegistryName().toString().contains("realistictorches")) {
            ForgeRegistries.RECIPES.register(new NullReplacementRecipe(currentRecipe));
            ++recipeCount;
         }
      }

      RealisticTorches.logger.info("Removed " + recipeCount + " torch recipe" + (recipeCount > 1 ? "s." : "."));
   }

   public static void registerOres() {
      if (ConfigHandler.oreDictionaryEnabled) {
         OreDictionary.registerOre("blockTorch", RealisticTorchesBlocks.torchLit);
         OreDictionary.registerOre("blockTorch", Blocks.TORCH);
      }

   }
}
