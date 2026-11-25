package com.creativemd.creativecore.common.utils.mc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class BlockUtils {
   public BlockUtils() {
   }

   public static IBlockState getState(ItemStack stack) {
      Block block = Block.getBlockFromItem(stack.getItem());
      return block != null && !(block instanceof BlockAir) ? getState(block, stack.getMetadata()) : Blocks.AIR.getDefaultState();
   }

   public static IBlockState getState(Block block, int meta) {
      try {
         return block.getStateFromMeta(meta);
      } catch (Exception var3) {
         return block.getDefaultState();
      }
   }
}
