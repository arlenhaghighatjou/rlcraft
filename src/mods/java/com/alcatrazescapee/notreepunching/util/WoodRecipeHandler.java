package com.alcatrazescapee.notreepunching.util;

import com.alcatrazescapee.alcatrazcore.inventory.crafting.InventoryCraftingEmpty;
import com.alcatrazescapee.alcatrazcore.util.CoreHelpers;
import com.alcatrazescapee.notreepunching.ModConfig;
import com.alcatrazescapee.notreepunching.NoTreePunching;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.IForgeRegistryModifiable;

@ParametersAreNonnullByDefault
public final class WoodRecipeHandler {
   private static final Map<ItemStack, ItemStack> WOOD_TYPES = new HashMap();
   private static int LOGS_FOUND = 0;

   public WoodRecipeHandler() {
   }

   public static void registerRecipes(Register<IRecipe> event) {
      IForgeRegistryModifiable<IRecipe> registry = (IForgeRegistryModifiable)event.getRegistry();
      findMatchingRecipes(registry.getValuesCollection(), registry);
      if (ModConfig.GENERAL.replaceLogRecipes) {
         registry.remove(new ResourceLocation("minecraft:stick"));
      }

   }

   public static void postInit() {
      findMatchingRecipes(ForgeRegistries.RECIPES.getValuesCollection(), (IForgeRegistryModifiable)ForgeRegistries.RECIPES);
   }

   public static void registerWoodRecipe(ItemStack log, ItemStack plank) {
      WOOD_TYPES.put(log, plank);
   }

   static boolean isLog(World world, BlockPos pos, IBlockState state) {
      ItemStack stack = state.getBlock().getPickBlock(state, (RayTraceResult)null, world, pos, (EntityPlayer)null);
      return WOOD_TYPES.keySet().stream().anyMatch((x) -> {
         return CoreHelpers.doStacksMatch(stack, x);
      });
   }

   static boolean isPlank(World world, BlockPos pos, IBlockState state) {
      ItemStack stack = state.getBlock().getPickBlock(state, (RayTraceResult)null, world, pos, (EntityPlayer)null);
      return WOOD_TYPES.values().stream().anyMatch((x) -> {
         return CoreHelpers.doStacksMatch(stack, x);
      });
   }

   static boolean isAxe(ItemStack stack) {
      return CoreHelpers.doesStackMatchOre(stack, "toolAxe") || CoreHelpers.doesStackMatchOre(stack, "toolWeakAxe") || stack.getItem() instanceof ItemAxe || stack.getItem().getToolClasses(stack).contains("axe");
   }

   static boolean isWeakAxe(ItemStack stack) {
      return CoreHelpers.doesStackMatchOre(stack, "toolWeakAxe");
   }

   @Nullable
   static ItemStack getPlankForLog(World world, BlockPos pos, IBlockState state) {
      ItemStack search = state.getBlock().getPickBlock(state, (RayTraceResult)null, world, pos, (EntityPlayer)null);
      return (ItemStack)WOOD_TYPES.entrySet().stream().filter((x) -> {
         return CoreHelpers.doStacksMatch((ItemStack)x.getKey(), search);
      }).map(Entry::getValue).findFirst().orElse((Object)null);
   }

   private static void findMatchingRecipes(Collection<IRecipe> recipes, IForgeRegistryModifiable<IRecipe> registry) {
      InventoryCraftingEmpty tempCrafting = new InventoryCraftingEmpty(3, 3);
      NonNullList<ItemStack> logs = OreDictionary.getOres("logWood", false);
      int previousLogsFound = LOGS_FOUND;

      for(ItemStack stack : logs) {
         Block block = Block.getBlockFromItem(stack.getItem());
         if (block != Blocks.AIR) {
            if (stack.getMetadata() == 32767) {
               NonNullList<ItemStack> variants = NonNullList.create();
               block.getSubBlocks(block.getCreativeTabToDisplayOn(), variants);

               for(ItemStack log : variants) {
                  tempCrafting.setInventorySlotContents(0, log);
                  IRecipe recipe = tryFindRecipeSafely(recipes, tempCrafting);
                  if (recipe != null) {
                     ItemStack plank = recipe.getCraftingResult(tempCrafting);
                     if (!plank.isEmpty()) {
                        WOOD_TYPES.put(log.copy(), plank.copy());
                        if (ModConfig.GENERAL.replaceLogRecipes) {
                           registry.remove(recipe.getRegistryName());
                        }

                        ResourceLocation loc = new ResourceLocation("notreepunching", "saw_planks_" + ++LOGS_FOUND);
                        registry.register((new ShapedOreRecipe(loc, plank, new Object[]{"S", "W", 'S', "toolSaw", 'W', log})).setRegistryName(loc));
                     }
                  }
               }
            } else {
               ItemStack log = stack.copy();
               tempCrafting.setInventorySlotContents(0, log);
               IRecipe recipe = tryFindRecipeSafely(recipes, tempCrafting);
               if (recipe != null) {
                  ItemStack plank = recipe.getCraftingResult(tempCrafting);
                  if (!plank.isEmpty()) {
                     WOOD_TYPES.put(log.copy(), plank.copy());
                     if (ModConfig.GENERAL.replaceLogRecipes) {
                        registry.remove(recipe.getRegistryName());
                     }

                     ResourceLocation loc = new ResourceLocation("notreepunching", "saw_planks_" + ++LOGS_FOUND);
                     registry.register((new ShapedOreRecipe(loc, plank, new Object[]{"S", "W", 'S', "toolSaw", 'W', log})).setRegistryName(loc));
                  }
               }
            }
         }
      }

      if (previousLogsFound == 0) {
         NoTreePunching.getLog().info("Found and replaced {} Log -> Planks recipes with Saw + Log -> Plank recipes. (First Pass)", Integer.valueOf(LOGS_FOUND));
      } else {
         NoTreePunching.getLog().info("Found and replaced additional {} Log -> Planks recipes with Saw + Log -> Plank recipes. (Second Pass)", Integer.valueOf(LOGS_FOUND - previousLogsFound));
      }

   }

   @Nullable
   private static IRecipe tryFindRecipeSafely(Collection<IRecipe> recipes, InventoryCrafting tempCrafting) {
      try {
         return (IRecipe)recipes.stream().filter((x) -> {
            return x.matches(tempCrafting, (World)null);
         }).findFirst().orElse((Object)null);
      } catch (Exception var3) {
         return null;
      }
   }
}
