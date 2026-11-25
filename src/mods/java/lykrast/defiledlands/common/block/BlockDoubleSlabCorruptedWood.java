package lykrast.defiledlands.common.block;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import lykrast.defiledlands.common.init.ModBlocks;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockDoubleSlabCorruptedWood extends BlockSlabCorruptedWood {
   public BlockDoubleSlabCorruptedWood() {
   }

   public boolean isDouble() {
      return true;
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.SOLID;
   }

   public ItemBlock getItemBlock() {
      return null;
   }

   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return Item.getItemFromBlock(ModBlocks.slabHalfTenebra);
   }
}
