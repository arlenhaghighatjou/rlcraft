package com.charles445.simpledifficulty.register;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.charles445.simpledifficulty.api.SDFluids;
import com.charles445.simpledifficulty.api.SDItems;
import com.charles445.simpledifficulty.api.SDPotions;
import com.charles445.simpledifficulty.register.crafting.FixedBrewingOreRecipe;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class RegisterRecipes {
   public static boolean POTION_RECIPES_AS_VANILLA = true;

   public RegisterRecipes() {
   }

   public static class Registrar {
      public Registrar() {
      }

      public static void register(EventBus bus) {
         bus.registerGeneric(Register.class, IRecipe.class, Registrar::registerRecipes);
      }

      public static void registerRecipes(Register<IRecipe> event) {
         GameRegistry.addSmelting(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER), new ItemStack(SDItems.purifiedWaterBottle, 1), 0.0F);
         GameRegistry.addSmelting(Items.WATER_BUCKET, FluidUtil.getFilledBucket(new FluidStack(SDFluids.purifiedWater, 1000)), 0.0F);
         if (RegisterRecipes.POTION_RECIPES_AS_VANILLA) {
            PotionHelper.addMix(PotionTypes.AWKWARD, SDItems.ice_chunk, SDPotions.cold_resist_type);
            PotionHelper.addMix(PotionTypes.AWKWARD, SDItems.magma_chunk, SDPotions.heat_resist_type);
            PotionHelper.addMix(SDPotions.cold_resist_type, Items.REDSTONE, SDPotions.long_cold_resist_type);
            PotionHelper.addMix(SDPotions.heat_resist_type, Items.REDSTONE, SDPotions.long_heat_resist_type);
         } else {
            registerSameItemPotionRecipes(PotionTypes.AWKWARD, SDItems.ice_chunk, SDPotions.cold_resist_type);
            registerSameItemPotionRecipes(PotionTypes.AWKWARD, SDItems.magma_chunk, SDPotions.heat_resist_type);
            registerSameItemPotionRecipes(SDPotions.cold_resist_type, Items.REDSTONE, SDPotions.long_cold_resist_type);
            registerSameItemPotionRecipes(SDPotions.heat_resist_type, Items.REDSTONE, SDPotions.long_heat_resist_type);
            registerConversionPotionRecipes(SDPotions.cold_resist_type);
            registerConversionPotionRecipes(SDPotions.heat_resist_type);
            registerConversionPotionRecipes(SDPotions.long_cold_resist_type);
            registerConversionPotionRecipes(SDPotions.long_heat_resist_type);
         }

      }

      private static void registerConversionPotionRecipes(PotionType potionType) {
         registerPotionRecipe(Items.POTIONITEM, potionType, Items.DRAGON_BREATH, Items.LINGERING_POTION, potionType);
         registerPotionRecipe(Items.SPLASH_POTION, potionType, Items.DRAGON_BREATH, Items.LINGERING_POTION, potionType);
         registerPotionRecipe(Items.POTIONITEM, potionType, Items.GUNPOWDER, Items.SPLASH_POTION, potionType);
         registerPotionRecipe(Items.LINGERING_POTION, potionType, Items.GUNPOWDER, Items.SPLASH_POTION, potionType);
      }

      private static void registerSameItemPotionRecipes(PotionType itemInPotionType, Item ingredient, PotionType itemOutPotionType) {
         registerPotionRecipe(Items.POTIONITEM, itemInPotionType, ingredient, Items.POTIONITEM, itemOutPotionType);
         registerPotionRecipe(Items.LINGERING_POTION, itemInPotionType, ingredient, Items.LINGERING_POTION, itemOutPotionType);
         registerPotionRecipe(Items.SPLASH_POTION, itemInPotionType, ingredient, Items.SPLASH_POTION, itemOutPotionType);
      }

      private static void registerPotionRecipe(Item itemIn, PotionType itemInPotionType, Item ingredient, Item itemOut, PotionType itemOutPotionType) {
         BrewingRecipeRegistry.addRecipe(new FixedBrewingOreRecipe(toPotion(new ItemStack(itemIn), itemInPotionType), new ItemStack(ingredient), toPotion(new ItemStack(itemOut), itemOutPotionType)));
      }

      private static ItemStack toPotion(ItemStack stack, PotionType potionType) {
         return PotionUtils.addPotionToItemStack(stack, potionType);
      }
   }
}
