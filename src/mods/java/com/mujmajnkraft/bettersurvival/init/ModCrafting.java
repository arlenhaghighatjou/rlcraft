package com.mujmajnkraft.bettersurvival.init;

import com.mujmajnkraft.bettersurvival.config.ForgeConfigHandler;
import com.mujmajnkraft.bettersurvival.recipe.BlankRecipe;
import java.util.ArrayList;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModCrafting {
   public static ArrayList<IRecipe> orerecipes = new ArrayList();
   public static ArrayList<IRecipe> normalrecipes = new ArrayList();

   public ModCrafting() {
   }

   public static void register() {
      GameRegistry.addShapedRecipe(new ResourceLocation("mujmajnkraftsbettersurvival:smallshield"), new ResourceLocation("mujmajnkraftsbettersurvivalother"), new ItemStack(ModItems.smallshield), new Object[]{" W ", "WIW", " W ", 'W', Blocks.PLANKS, 'I', Items.IRON_INGOT});
      GameRegistry.addShapedRecipe(new ResourceLocation("mujmajnkraftsbettersurvival:bigshield"), new ResourceLocation("mujmajnkraftsbettersurvivalother"), new ItemStack(ModItems.bigshield), new Object[]{" WI", "SWI", " WI", 'W', Blocks.PLANKS, 'I', Items.IRON_INGOT, 'S', Items.STICK});
      if (!ForgeConfigHandler.weapons.allowVanillaShields) {
         for(ResourceLocation resourceLocation : ForgeRegistries.RECIPES.getKeys()) {
            IRecipe recipe = (IRecipe)ForgeRegistries.RECIPES.getValue(resourceLocation);
            ItemStack is = recipe.getRecipeOutput();
            if (is.getItem() == Items.SHIELD) {
               ForgeRegistries.RECIPES.register(new BlankRecipe(recipe));
            }
         }
      }

      Ingredient ink = Ingredient.fromStacks(new ItemStack[]{new ItemStack(Items.DYE, 1, 0)});
      Item chorus = Items.CHORUS_FRUIT;
      Item redstone = Items.REDSTONE;
      Item glowstone = Items.GLOWSTONE_DUST;
      Item fermentedeye = Items.FERMENTED_SPIDER_EYE;
      Item apple = Items.GOLDEN_APPLE;
      PotionHelper.addMix(PotionTypes.AWKWARD, ink, ModPotionTypes.blindness);
      PotionHelper.addMix(ModPotionTypes.blindness, redstone, ModPotionTypes.longblindness);
      PotionHelper.addMix(PotionTypes.REGENERATION, fermentedeye, ModPotionTypes.decay);
      PotionHelper.addMix(PotionTypes.LONG_REGENERATION, fermentedeye, ModPotionTypes.longdecay);
      PotionHelper.addMix(PotionTypes.STRONG_REGENERATION, fermentedeye, ModPotionTypes.strongdecay);
      PotionHelper.addMix(ModPotionTypes.decay, redstone, ModPotionTypes.longdecay);
      PotionHelper.addMix(ModPotionTypes.decay, glowstone, ModPotionTypes.strongdecay);
      PotionHelper.addMix(PotionTypes.AWKWARD, chorus, ModPotionTypes.warp);
      PotionHelper.addMix(ModPotionTypes.warp, glowstone, ModPotionTypes.strongwarp);
      PotionHelper.addMix(ModPotionTypes.warp, fermentedeye, ModPotionTypes.antiwarp);
      PotionHelper.addMix(ModPotionTypes.antiwarp, redstone, ModPotionTypes.longantiwarp);
      PotionHelper.addMix(ModPotionTypes.milk, fermentedeye, ModPotionTypes.dispel);
      PotionHelper.addMix(ModPotionTypes.milk, apple, ModPotionTypes.cure);
   }
}
