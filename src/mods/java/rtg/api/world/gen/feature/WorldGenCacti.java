package rtg.api.world.gen.feature;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenCacti extends WorldGenerator {
   private boolean sand;
   private int eHeight;
   private IBlockState soilBlock;

   public WorldGenCacti(boolean sandOnly) {
      this(sandOnly, 0);
   }

   public WorldGenCacti(boolean sandOnly, int extraHeight) {
      this(sandOnly, extraHeight, Blocks.SAND.getDefaultState());
   }

   public WorldGenCacti(boolean sandOnly, int extraHeight, IBlockState soilBlock) {
      this.sand = sandOnly;
      this.eHeight = extraHeight;
      this.setSoilBlock(soilBlock);
   }

   public boolean generate(World world, EaglercraftRandom rand, BlockPos pos) {
      int x = pos.getX();
      int y = pos.getY();
      int z = pos.getZ();
      int i1 = x;
      int j1 = y + rand.nextInt(4) - rand.nextInt(4);
      int k1 = z;
      if (world.isAirBlock(new BlockPos(x, j1, z))) {
         IBlockState b = world.getBlockState(new BlockPos(x, j1 - 1, z));
         if (b == this.soilBlock || !this.sand && (b == Blocks.GRASS.getDefaultState() || b == Blocks.DIRT.getDefaultState())) {
            int l1 = 1 + rand.nextInt(rand.nextInt(3) + 1);
            if (b == Blocks.GRASS.getDefaultState() || b == Blocks.DIRT.getDefaultState()) {
               world.setBlockState(new BlockPos(x, j1 - 1, z), this.soilBlock, 2);
            }

            for(int i2 = 0; i2 < l1 + this.eHeight; ++i2) {
               if (Blocks.CACTUS.canBlockStay(world, new BlockPos(i1, j1 + i2, k1))) {
                  world.setBlockState(new BlockPos(i1, j1 + i2, k1), Blocks.CACTUS.getDefaultState(), 2);
               }
            }
         }
      }

      return true;
   }

   public IBlockState getSoilBlock() {
      return this.soilBlock;
   }

   public WorldGenCacti setSoilBlock(IBlockState soilBlock) {
      this.soilBlock = soilBlock;
      return this;
   }
}
