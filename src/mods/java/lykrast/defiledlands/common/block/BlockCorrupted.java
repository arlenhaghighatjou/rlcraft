package lykrast.defiledlands.common.block;

import java.util.Random;
import lykrast.defiledlands.common.util.CorruptionHelper;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockCorrupted extends BlockGeneric {
   public BlockCorrupted() {
      this.setTickRandomly(true);
   }

   public BlockCorrupted(Material material, SoundType soundType) {
      super(material, soundType);
      this.setTickRandomly(true);
   }

   public BlockCorrupted(Material material, SoundType soundType, float hardness, float resistance) {
      super(material, soundType, hardness, resistance);
      this.setTickRandomly(true);
   }

   public BlockCorrupted(Material material, SoundType soundType, float hardness, float resistance, String tool, int harvestLevel) {
      super(material, soundType, hardness, resistance, tool, harvestLevel);
      this.setTickRandomly(true);
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      CorruptionHelper.spread(worldIn, pos, state, rand);
      super.updateTick(worldIn, pos, state, rand);
   }
}
