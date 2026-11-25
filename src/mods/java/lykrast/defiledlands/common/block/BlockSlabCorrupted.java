package lykrast.defiledlands.common.block;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import lykrast.defiledlands.common.util.CorruptionHelper;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockSlabCorrupted extends BlockSlab {
   public BlockSlabCorrupted(Material materialIn) {
      super(materialIn);
      this.setTickRandomly(true);
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      CorruptionHelper.spread(worldIn, pos, state, rand);
      super.updateTick(worldIn, pos, state, rand);
   }
}
