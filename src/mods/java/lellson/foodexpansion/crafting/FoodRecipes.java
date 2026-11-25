package lellson.foodexpansion.crafting;

import java.util.ArrayList;
import java.util.List;
import lellson.foodexpansion.FoodExpansionConfig;
import lellson.foodexpansion.FoodItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class FoodRecipes {
   public FoodRecipes() {
   }

   public static void registerConditions() {
      CraftingHelper.register(new ResourceLocation("foodexpansion", "EnableJelly"), new FactoryJelly());
      CraftingHelper.register(new ResourceLocation("foodexpansion", "EnableBacon"), new FactoryBacon());
      CraftingHelper.register(new ResourceLocation("foodexpansion", "EnableBaconAndEgg"), new FactoryBaconAndEgg());
      CraftingHelper.register(new ResourceLocation("foodexpansion", "EnableCarrotSeedSoup"), new FactoryCarrotSeedSoup());
      CraftingHelper.register(new ResourceLocation("foodexpansion", "EnableCompressedFlesh"), new FactoryCompressedFlesh());
      CraftingHelper.register(new ResourceLocation("foodexpansion", "EnableChocolateBar"), new FactoryChocolateBar());
      CraftingHelper.register(new ResourceLocation("foodexpansion", "EnableSpiderSoup"), new FactorySpiderSoup());
      CraftingHelper.register(new ResourceLocation("foodexpansion", "EnableNetherWartSoup"), new FactoryNetherWartSoup());
      CraftingHelper.register(new ResourceLocation("foodexpansion", "EnableCactusFruit"), new FactoryCactusFruit());
      CraftingHelper.register(new ResourceLocation("foodexpansion", "EnableCarrotPie"), new FactoryCarrotPie());
      CraftingHelper.register(new ResourceLocation("foodexpansion", "EnableBlazeCream"), new FactoryBlazeCream());
      CraftingHelper.register(new ResourceLocation("foodexpansion", "EnableMelonSalad"), new FactoryMelonSalad());
      CraftingHelper.register(new ResourceLocation("foodexpansion", "EnableDough"), new FactoryDough());
      CraftingHelper.register(new ResourceLocation("foodexpansion", "EnableLollipop"), new FactoryLollipop());
      CraftingHelper.register(new ResourceLocation("foodexpansion", "EnableBeetrootNoodles"), new FactoryBeetrootNoodles());
      CraftingHelper.register(new ResourceLocation("foodexpansion", "EnableForbiddenFruit"), new FactoryForbiddenFruit());
      CraftingHelper.register(new ResourceLocation("foodexpansion", "EnableStarvingFruit"), new FactoryStarvingFruit());
   }

   public static void initSmeltingRecipes() {
      if (!FoodExpansionConfig.disableRecipeFriedEgg) {
         GameRegistry.addSmelting(new ItemStack(Items.EGG), new ItemStack(FoodItems.itemFriedEgg), 0.35F);
      }

      if (!FoodExpansionConfig.disableRecipeCookedBacon) {
         GameRegistry.addSmelting(new ItemStack(FoodItems.itemBacon), new ItemStack(FoodItems.itemCookedBacon), 0.35F);
      }

      if (!FoodExpansionConfig.disableRecipeCookedSquid) {
         GameRegistry.addSmelting(new ItemStack(FoodItems.itemSquid), new ItemStack(FoodItems.itemCookedSquid), 0.35F);
      }

      if (!FoodExpansionConfig.disableRecipeCompressedToLeather) {
         GameRegistry.addSmelting(new ItemStack(FoodItems.itemCompressedFlesh), new ItemStack(Items.LEATHER), 0.35F);
      }

      if (!FoodExpansionConfig.disableRecipeCookedHorseMeat) {
         GameRegistry.addSmelting(new ItemStack(FoodItems.itemHorseMeat), new ItemStack(FoodItems.itemCookedHorseMeat), 0.35F);
      }

      if (!FoodExpansionConfig.disableRecipeCookedMushroom) {
         GameRegistry.addSmelting(new ItemStack(Blocks.BROWN_MUSHROOM), new ItemStack(FoodItems.itemCookedMushroom), 0.35F);
         GameRegistry.addSmelting(new ItemStack(Blocks.RED_MUSHROOM), new ItemStack(FoodItems.itemCookedMushroom), 0.35F);
      }

      if (!FoodExpansionConfig.disableRecipeCookedBatWing) {
         GameRegistry.addSmelting(new ItemStack(FoodItems.itemBatWing), new ItemStack(FoodItems.itemCookedBatWing), 0.35F);
      }

      if (!FoodExpansionConfig.disableRecipeCookedWolfMeat) {
         GameRegistry.addSmelting(new ItemStack(FoodItems.itemWolfMeat), new ItemStack(FoodItems.itemCookedWolfMeat), 0.35F);
      }

      if (!FoodExpansionConfig.disableRecipeCookedOcelotMeat) {
         GameRegistry.addSmelting(new ItemStack(FoodItems.itemOcelotMeat), new ItemStack(FoodItems.itemCookedOcelotMeat), 0.35F);
      }

      if (!FoodExpansionConfig.disableRecipeCookedParrotMeat) {
         GameRegistry.addSmelting(new ItemStack(FoodItems.itemParrotMeat), new ItemStack(FoodItems.itemCookedParrotMeat), 0.35F);
      }

      if (!FoodExpansionConfig.disableRecipeCookedLlamaMeat) {
         GameRegistry.addSmelting(new ItemStack(FoodItems.itemLlamaMeat), new ItemStack(FoodItems.itemCookedLlamaMeat), 0.35F);
      }

      if (!FoodExpansionConfig.disableRecipeCookedPolarBearMeat) {
         GameRegistry.addSmelting(new ItemStack(FoodItems.itemPolarBearMeat), new ItemStack(FoodItems.itemCookedPolarBearMeat), 0.35F);
      }

      if (!FoodExpansionConfig.disableDough) {
         GameRegistry.addSmelting(new ItemStack(FoodItems.itemDough), new ItemStack(Items.BREAD), 0.35F);
      }

      if (!FoodExpansionConfig.disableRoastedSeed) {
         GameRegistry.addSmelting(new ItemStack(Items.WHEAT_SEEDS), new ItemStack(FoodItems.itemRoastedSeed), 0.35F);
         GameRegistry.addSmelting(new ItemStack(Items.MELON_SEEDS), new ItemStack(FoodItems.itemRoastedSeed), 0.35F);
         GameRegistry.addSmelting(new ItemStack(Items.PUMPKIN_SEEDS), new ItemStack(FoodItems.itemRoastedSeed), 0.35F);
         GameRegistry.addSmelting(new ItemStack(Items.BEETROOT_SEEDS), new ItemStack(FoodItems.itemRoastedSeed), 0.35F);
      }

   }

   public static void addShapeless(ItemStack output, boolean disable, ItemStack... input) {
      List<Ingredient> ingredients = new ArrayList();

      for(ItemStack stack : input) {
         ingredients.add(Ingredient.fromStacks(new ItemStack[]{stack}));
      }

      if (!disable) {
         GameRegistry.addShapelessRecipe(output.getItem().getRegistryName(), (ResourceLocation)null, output, (Ingredient[])ingredients.toArray(new Ingredient[ingredients.size()]));
      }

   }

   public static void addShaped(ItemStack output, boolean disable, Object[] object) {
      if (!disable) {
         GameRegistry.addShapedRecipe(output.getItem().getRegistryName(), (ResourceLocation)null, output, object);
      }

   }
}
