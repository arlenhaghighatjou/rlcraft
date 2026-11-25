package com.ferreusveritas.dynamictrees;

import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.items.Seed;
import com.ferreusveritas.dynamictrees.trees.Species;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

@EventBusSubscriber(
   modid = "dynamictrees"
)
public class ModRecipes {
   public ModRecipes() {
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void register(Register<IRecipe> event) {
      IForgeRegistry<IRecipe> registry = event.getRegistry();
      ModItems.dendroPotion.registerRecipes(registry);
      GameRegistry.addShapelessRecipe(new ResourceLocation("dynamictrees", "dirtbucket"), (ResourceLocation)null, new ItemStack(ModItems.dirtBucket), new Ingredient[]{Ingredient.fromItem(Items.BUCKET), Ingredient.fromItem(Item.getItemFromBlock(Blocks.DIRT))});

      for(EnumType woodType : EnumType.values()) {
         Species species = TreeRegistry.findSpecies(new ResourceLocation("dynamictrees", woodType.getName().replace("_", "")));
         ItemStack saplingStack = new ItemStack(Blocks.SAPLING, 1, woodType.getMetadata());
         ItemStack seedStack = species.getSeedStack(1);
         createDirtBucketExchangeRecipes(saplingStack, seedStack, true);
      }

      if (ModConfigs.enableAppleTrees) {
         createDirtBucketExchangeRecipes(new ItemStack(Items.APPLE), TreeRegistry.findSpecies(new ResourceLocation("dynamictrees", "apple")).getSeedStack(1), false);
      }

   }

   public static void createDirtBucketExchangeRecipes(ItemStack saplingStack, ItemStack seedStack, boolean seedIsSapling) {
      createDirtBucketExchangeRecipes(saplingStack, seedStack, seedIsSapling, "seed");
   }

   public static void createDirtBucketExchangeRecipes(ItemStack saplingStack, ItemStack seedStack, boolean seedIsSapling, String suffix) {
      ResourceLocation name = seedStack.getItem() instanceof Seed ? ((Seed)seedStack.getItem()).getSpecies(seedStack).getRegistryName() : seedStack.getItem().getRegistryName();
      createDirtBucketExchangeRecipes(saplingStack, seedStack, seedIsSapling, suffix, name);
   }

   public static void createDirtBucketExchangeRecipes(ItemStack saplingStack, ItemStack seedStack, boolean seedIsSapling, String suffix, ResourceLocation species) {
      createSaplingToSeedWithDirtBucketRecipe(saplingStack, seedStack, suffix, species);
      if (seedIsSapling) {
         createSeedToSaplingWithDirtBucketRecipe(seedStack, saplingStack, "sapling", species);
      }

   }

   public static void createSeedToSaplingWithDirtBucketRecipe(ItemStack seedStack, ItemStack saplingStack, String suffix, ResourceLocation species) {
      if (ModConfigs.compatRecipeForSaplings && !saplingStack.isEmpty() && !seedStack.isEmpty()) {
         String speciesPath = species.getResourcePath();
         String speciesDomain = species.getResourceDomain();
         GameRegistry.addShapelessRecipe(new ResourceLocation(speciesDomain, speciesPath + suffix), (ResourceLocation)null, saplingStack, new Ingredient[]{Ingredient.fromStacks(new ItemStack[]{seedStack}), Ingredient.fromItem(ModItems.dirtBucket)});
         OreDictionary.registerOre("treeSapling", seedStack);
      }

   }

   public static void createSaplingToSeedWithDirtBucketRecipe(ItemStack saplingStack, ItemStack seedStack, String suffix, ResourceLocation species) {
      if (!saplingStack.isEmpty() && !seedStack.isEmpty()) {
         String speciesPath = species.getResourcePath();
         String speciesDomain = species.getResourceDomain();
         GameRegistry.addShapelessRecipe(new ResourceLocation(speciesDomain, speciesPath + suffix), (ResourceLocation)null, seedStack, new Ingredient[]{Ingredient.fromStacks(new ItemStack[]{saplingStack}), Ingredient.fromItem(ModItems.dirtBucket)});
      }

   }

   public static void createDirtBucketExchangeRecipesWithFruit(ItemStack saplingStack, ItemStack seedStack, ItemStack fruitStack, boolean seedIsSapling, boolean requiresBonemeal) {
      createDirtBucketExchangeRecipesWithFruit(saplingStack, seedStack, fruitStack, seedIsSapling, "seed", requiresBonemeal);
   }

   public static void createDirtBucketExchangeRecipesWithFruit(ItemStack saplingStack, ItemStack seedStack, ItemStack fruitStack, boolean seedIsSapling, String suffix, boolean requiresBonemeal) {
      ResourceLocation name = seedStack.getItem() instanceof Seed ? ((Seed)seedStack.getItem()).getSpecies(seedStack).getRegistryName() : seedStack.getItem().getRegistryName();
      createDirtBucketExchangeRecipesWithFruit(saplingStack, seedStack, fruitStack, seedIsSapling, suffix, name, requiresBonemeal);
   }

   public static void createDirtBucketExchangeRecipesWithFruit(ItemStack saplingStack, ItemStack seedStack, ItemStack fruitStack, boolean seedIsSapling, String suffix, ResourceLocation species, boolean requiresBonemeal) {
      createDirtBucketExchangeRecipes(saplingStack, seedStack, seedIsSapling, suffix, species);
      createFruitToSeedRecipe(seedStack, fruitStack, species, requiresBonemeal);
   }

   private static void createFruitToSeedRecipe(ItemStack seedStack, ItemStack fruitStack, ResourceLocation species, boolean requiresBonemeal) {
      if (fruitStack != null && !fruitStack.isEmpty()) {
         ResourceLocation seedFromFruit = new ResourceLocation(species.getResourceDomain(), species.getResourcePath() + "seedfromfruit");
         Ingredient fruit = Ingredient.fromStacks(new ItemStack[]{fruitStack});
         Ingredient bonemeal = Ingredient.fromStacks(new ItemStack[]{new ItemStack(Items.DYE, 1, 15)});
         Ingredient dirtBucket = Ingredient.fromItem(ModItems.dirtBucket);
         if (requiresBonemeal) {
            GameRegistry.addShapelessRecipe(seedFromFruit, (ResourceLocation)null, seedStack, new Ingredient[]{fruit, bonemeal, dirtBucket});
         } else {
            GameRegistry.addShapelessRecipe(seedFromFruit, (ResourceLocation)null, seedStack, new Ingredient[]{fruit, dirtBucket});
         }
      }

   }
}
