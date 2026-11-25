package com.alcatrazescapee.notreepunching.util;

import com.alcatrazescapee.alcatrazcore.util.CoreHelpers;
import com.alcatrazescapee.notreepunching.ModConfig;
import com.alcatrazescapee.notreepunching.NoTreePunching;
import com.alcatrazescapee.notreepunching.common.items.ItemKnife;
import com.alcatrazescapee.notreepunching.common.items.ItemRock;
import com.alcatrazescapee.notreepunching.common.items.ModItems;
import com.alcatrazescapee.notreepunching.util.types.Stone;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.BlockDoublePlant.EnumPlantType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class HarvestBlockHandler {
   private static final Set<Predicate<IBlockState>> EXCEPTIONS = new HashSet();
   private static final List<ItemStack> GRASS_DROPS = new ArrayList();

   public HarvestBlockHandler() {
   }

   public static void postInit() {
      reloadWhitelist();
      addGrassDrop(new ItemStack(ModItems.GRASS_FIBER));
      addGrassDrop(new ItemStack(ModItems.GRASS_FIBER, 2));
   }

   public static void reloadWhitelist() {
      EXCEPTIONS.clear();

      for(String s : ModConfig.GENERAL.alwaysBreakable) {
         try {
            EXCEPTIONS.add(createPredicate(s));
         } catch (IllegalArgumentException var5) {
            NoTreePunching.getLog().warn("Problem loading entry in alwaysBreakable: {}, {}", s, var5);
         }
      }

      EXCEPTIONS.removeIf(Objects::isNull);
   }

   public static void addExtraDrops(List<ItemStack> drops, IBlockState state, EntityPlayer player, ItemStack stack, boolean isSilkTouch) {
      Stone stone = Stone.getFromBlock(state);
      if (stone != null && stone.isEnabled() && ModConfig.GENERAL.enableStoneDropChanges && !isSilkTouch) {
         drops.clear();
         drops.add(new ItemStack(ItemRock.get(stone), 2 + Util.RNG.nextInt(3)));
      }

      if (state.getBlock() instanceof BlockLeaves && !isSilkTouch) {
         float stickChance = (float)ModConfig.BALANCE.leavesStickDropChance;
         if (CoreHelpers.doesStackMatchOre(stack, "toolKnife")) {
            stickChance += 0.3F;
         }

         if (Util.RNG.nextFloat() < stickChance) {
            drops.add(new ItemStack(Items.STICK, 1 + Util.RNG.nextInt(1)));
         }
      }

      if ((state.getBlock() instanceof BlockTallGrass || state.getBlock() instanceof BlockDoublePlant && (state.getValue(BlockDoublePlant.VARIANT) == EnumPlantType.FERN || state.getValue(BlockDoublePlant.VARIANT) == EnumPlantType.GRASS)) && stack.getItem() instanceof ItemKnife) {
         if ((double)Util.RNG.nextFloat() < ModConfig.BALANCE.tallGrassDropPlantFiberChance) {
            ItemStack drop = (ItemStack)GRASS_DROPS.get(Util.RNG.nextInt(GRASS_DROPS.size()));
            drops.add(drop.copy());
         }

         stack.damageItem(1, player);
         player.setHeldItem(EnumHand.MAIN_HAND, stack);
      }

   }

   public static boolean isInvalidTool(@Nullable ItemStack stack, @Nullable EntityPlayer player, IBlockState state) {
      if (player != null && stack != null) {
         if (isWhitelisted(state)) {
            return false;
         } else {
            int neededHarvestLevel = state.getBlock().getHarvestLevel(state);
            String neededToolClass = state.getBlock().getHarvestTool(state);
            if (neededToolClass == null) {
               return false;
            } else if (stack.isEmpty()) {
               return true;
            } else {
               for(String toolClass : stack.getItem().getToolClasses(stack)) {
                  if (neededToolClass.equals(toolClass) && stack.getItem().getHarvestLevel(stack, toolClass, player, state) >= neededHarvestLevel) {
                     return false;
                  }

                  if (neededToolClass.equals("shovel") && toolClass.equals("pickaxe") && stack.getItem().getHarvestLevel(stack, toolClass, player, state) >= 1) {
                     return false;
                  }

                  if (toolClass.equals("mattock") && (neededToolClass.equals("axe") || neededToolClass.equals("shovel"))) {
                     return false;
                  }
               }

               return true;
            }
         }
      } else {
         return true;
      }
   }

   public static float getSpeedModifier(String toolClass) {
      byte var2 = -1;
      switch(toolClass.hashCode()) {
      case -903145309:
         if (toolClass.equals("shovel")) {
            var2 = 3;
         }
         break;
      case -578028723:
         if (toolClass.equals("pickaxe")) {
            var2 = 2;
         }
         break;
      case 97038:
         if (toolClass.equals("axe")) {
            var2 = 0;
         }
         break;
      case 841375107:
         if (toolClass.equals("mattock")) {
            var2 = 1;
         }
      }

      switch(var2) {
      case 0:
      case 1:
         return 0.2F;
      case 2:
         return 0.125F;
      case 3:
      default:
         return 0.33F;
      }
   }

   public static boolean isWhitelisted(IBlockState state) {
      return EXCEPTIONS.stream().anyMatch((x) -> {
         return x.test(state);
      });
   }

   public static void addGrassDrop(ItemStack stack) {
      GRASS_DROPS.add(stack);
   }

   private static Predicate<IBlockState> createPredicate(String entry) throws IllegalArgumentException {
      if (entry.length() >= 9 && entry.substring(0, 9).equals("material:")) {
         try {
            Object obj = ObfuscationReflectionHelper.getPrivateValue(Material.class, (Object)null, new String[]{entry.substring(9).toUpperCase()});
            if (obj instanceof Material) {
               Material material = (Material)obj;
               return (state) -> {
                  return state.getMaterial() == material;
               };
            }
         } catch (Exception var5) {
            throw new IllegalArgumentException("Material is incorrect somehow", var5);
         }
      }

      int colon = entry.indexOf(58);
      int colon2 = entry.lastIndexOf(58);
      if (colon == colon2) {
         Block block = Block.getBlockFromName(entry);
         if (block == null) {
            throw new IllegalArgumentException("Block is null, no metadata");
         } else {
            return (state) -> {
               return state.getBlock() == block;
            };
         }
      } else {
         Block block = Block.getBlockFromName(entry.substring(0, colon2));
         if (block == null) {
            throw new IllegalArgumentException("Block is null, with metadata");
         } else {
            int meta = Integer.valueOf(entry.substring(colon2 + 1)).intValue();
            return (state) -> {
               return state.getBlock() == block && state.getBlock().getMetaFromState(state) == meta;
            };
         }
      }
   }
}
