package lykrast.defiledlands.common.world.feature;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import lykrast.defiledlands.common.util.CorruptionHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenCorruptionPost extends WorldGenerator {
   public WorldGenCorruptionPost() {
   }

   public boolean generate(World worldIn, EaglercraftRandom rand, BlockPos position) {
      for(BlockPos pos = position; pos.getY() > 0; pos = pos.down()) {
         CorruptionHelper.corrupt(worldIn, pos, worldIn.getBlockState(pos));
      }

      return true;
   }
}
