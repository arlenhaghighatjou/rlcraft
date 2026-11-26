package org.jwaresoftware.mcmods.lib;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ObjectHolderRegistry;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockCake;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.BlockStone;
import net.minecraft.block.IGrowable;
import net.minecraft.block.BlockStone.EnumType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@ParametersAreNonnullByDefault
public final class Whatis {
   public static Item wheat_seeds = null;
   public static Block red_mushroom = null;
   public static Block brown_mushroom = null;
   public static Block brown_mushroom_block = null;
   public static Block red_mushroom_block = null;
   public static Block hopper = null;

   public static final boolean isCobblestone(IBlockState what) {
      return what.getBlock() == Blocks.COBBLESTONE || what.getBlock() == Blocks.MOSSY_COBBLESTONE;
   }

   public static final boolean isRawstone(IBlockState what) {
      return what.getBlock() == Blocks.STONE ? what.getValue(BlockStone.VARIANT) == EnumType.STONE : false;
   }

   public static final boolean isCake(IBlockState what) {
      return what.getBlock() == Blocks.CAKE || what.getBlock() instanceof BlockCake;
   }

   public static final boolean isLikelyTree(World world, BlockPos blockpos, @Nonnull IBlockState blockstate) {
      if (!blockstate.getBlock().isWood(world, blockpos)) {
         return false;
      } else if (!Locations.isOverworld(world)) {
         return false;
      } else {
         byte radius = 3;
         int i = radius + 1;
         int leaf_block_count = 0;
         if (world.isAreaLoaded(blockpos.add(-i, -i, -i), blockpos.add(i, i, i))) {
            for(BlockPos test_blockpos : BlockPos.getAllInBox(blockpos.add(-radius, -radius, -radius), blockpos.add(radius, radius, radius))) {
               IBlockState test_blockstate = world.getBlockState(test_blockpos);
               if (test_blockstate.getBlock().isLeaves(test_blockstate, world, test_blockpos)) {
                  ++leaf_block_count;
               }
            }
         }

         return leaf_block_count > 6;
      }
   }

   public static final boolean isLikelySapling(World world, IBlockState blockstate) {
      Block block = blockstate.getBlock();
      if (block instanceof BlockSapling) {
         return true;
      } else {
         boolean likely = block.getClass().getSimpleName().endsWith("Sapling");
         if (likely && block instanceof IGrowable) {
            if (!(block instanceof BlockBush)) {
               boolean found = false;

               for(IProperty p : blockstate.getPropertyKeys()) {
                  if (p instanceof PropertyInteger && ("stage".equals(p.getName()) || "age".equals(p.getName()))) {
                     found = true;
                     break;
                  }
               }

               likely = found;
            }
         } else {
            likely = false;
         }

         return likely;
      }
   }

   public static boolean isLikelySpawner(IBlockState blockstate) {
      Block block = blockstate.getBlock();
      return block == Blocks.MOB_SPAWNER || block == Blocks.MONSTER_EGG || block instanceof BlockSilverfish;
   }

   private Whatis() {
   }

   public static final void init() {
   }

   public static void registerHolders() {
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("minecraft:wheat_seeds"), v -> wheat_seeds = v);
      ObjectHolderRegistry.INSTANCE.register(Block.class, new ResourceLocation("minecraft:red_mushroom"), v -> red_mushroom = v);
      ObjectHolderRegistry.INSTANCE.register(Block.class, new ResourceLocation("minecraft:brown_mushroom"), v -> brown_mushroom = v);
      ObjectHolderRegistry.INSTANCE.register(Block.class, new ResourceLocation("minecraft:brown_mushroom_block"), v -> brown_mushroom_block = v);
      ObjectHolderRegistry.INSTANCE.register(Block.class, new ResourceLocation("minecraft:red_mushroom_block"), v -> red_mushroom_block = v);
      ObjectHolderRegistry.INSTANCE.register(Block.class, new ResourceLocation("minecraft:hopper"), v -> hopper = v);
   }
}
