package com.alcatrazescapee.notreepunching.util.types;

import com.alcatrazescapee.alcatrazcore.util.CoreHelpers;
import com.alcatrazescapee.notreepunching.ModConfig;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStone.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

public enum Stone {
   STONE(true, false),
   ANDESITE(true, true),
   GRANITE(true, true),
   DIORITE(true, true),
   SANDSTONE(true, false),
   RED_SANDSTONE(true, false),
   MARBLE(false, true),
   LIMESTONE(false, true),
   BASALT(false, true),
   SLATE(false, true);

   private final boolean isDefault;
   private final boolean hasCobblestone;

   @Nonnull
   public static Stone getFromBlock(IBlockState state, EaglercraftRandom random) {
      Stone stone = getFromBlock(state);
      if (stone == STONE && ModConfig.GENERAL.enableRandomStoneWorldGenVariants) {
         switch(random.nextInt(5)) {
         case 0:
            return GRANITE;
         case 1:
            return ANDESITE;
         case 2:
            return DIORITE;
         }
      }

      return stone == null ? STONE : stone;
   }

   @Nullable
   public static Stone getFromBlock(IBlockState state) {
      if (state.getBlock() == Blocks.STONE) {
         switch((EnumType)state.getValue(BlockStone.VARIANT)) {
         case STONE:
            return STONE;
         case DIORITE:
            return DIORITE;
         case GRANITE:
            return GRANITE;
         case ANDESITE:
            return ANDESITE;
         }
      }

      if (state.getBlock() == Blocks.SANDSTONE) {
         return SANDSTONE;
      } else if (state.getBlock() == Blocks.RED_SANDSTONE) {
         return RED_SANDSTONE;
      } else {
         String registryName = state.getBlock().getRegistryName().toString();
         int meta = state.getBlock().getMetaFromState(state);
         if (meta == 0) {
            byte var4 = -1;
            switch(registryName.hashCode()) {
            case -1504156436:
               if (registryName.equals("quark:limestone")) {
                  var4 = 0;
               }
               break;
            case -544867555:
               if (registryName.equals("rustic:slate")) {
                  var4 = 6;
               }
               break;
            case 414893164:
               if (registryName.equals("chisel:limestone")) {
                  var4 = 1;
               }
               break;
            case 474726233:
               if (registryName.equals("quark:basalt")) {
                  var4 = 4;
               }
               break;
            case 647073753:
               if (registryName.equals("chisel:basalt")) {
                  var4 = 5;
               }
               break;
            case 789618049:
               if (registryName.equals("quark:marble")) {
                  var4 = 2;
               }
               break;
            case 961965569:
               if (registryName.equals("chisel:marble")) {
                  var4 = 3;
               }
            }

            switch(var4) {
            case 0:
            case 1:
               return LIMESTONE;
            case 2:
            case 3:
               return MARBLE;
            case 4:
            case 5:
               return BASALT;
            case 6:
               return SLATE;
            }
         }

         return null;
      }
   }

   private Stone(boolean isDefault, boolean hasCobblestone) {
      this.isDefault = isDefault;
      this.hasCobblestone = hasCobblestone;
   }

   private static ItemStack getStackByRegistryName(String... names) {
      for(String name : names) {
         ItemStack stack = CoreHelpers.getStackByRegistryName(name, 1, 0);
         if (!stack.isEmpty()) {
            return stack;
         }
      }

      return ItemStack.EMPTY;
   }

   @Nonnull
   public ItemStack getStoneStack() {
      switch(this) {
      case STONE:
         return new ItemStack(Blocks.STONE);
      case SANDSTONE:
         return new ItemStack(Blocks.SANDSTONE);
      case DIORITE:
         return new ItemStack(Blocks.STONE, 1, EnumType.DIORITE.getMetadata());
      case GRANITE:
         return new ItemStack(Blocks.STONE, 1, EnumType.GRANITE.getMetadata());
      case ANDESITE:
         return new ItemStack(Blocks.STONE, 1, EnumType.ANDESITE.getMetadata());
      case SLATE:
         return CoreHelpers.getStackByRegistryName("rustic:slate", 1, 0);
      case MARBLE:
         return getStackByRegistryName("quark:marble", "chisel:marble");
      case LIMESTONE:
         return getStackByRegistryName("quark:limestone", "chisel:limestone");
      case BASALT:
         return getStackByRegistryName("quark:basalt", "chisel:basalt");
      default:
         return ItemStack.EMPTY;
      }
   }

   public boolean isDefault() {
      return this.isDefault;
   }

   public boolean hasCobblestone() {
      return this.hasCobblestone;
   }

   public boolean isEnabled() {
      if (this.isDefault()) {
         return true;
      } else {
         switch(this) {
         case SLATE:
            return Loader.isModLoaded("rustic") && ModConfig.COMPAT.enableRusticCompat;
         case MARBLE:
         case LIMESTONE:
         case BASALT:
            return Loader.isModLoaded("quark") && ModConfig.COMPAT.enableQuarkCompat || Loader.isModLoaded("chisel") && ModConfig.COMPAT.enableChiselCompat;
         default:
            return false;
         }
      }
   }
}
