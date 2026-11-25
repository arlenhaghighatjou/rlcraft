package paulevs.betternether.structures;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeModContainer;

public interface IStructureWorld {
   boolean doBlockNotify = false;

   void generate(World var1, BlockPos var2, Rotation var3);

   default void setBlockAndNotifyAdequately(World world, BlockPos pos, IBlockState state) {
      int flag = ForgeModContainer.fixVanillaCascading ? 18 : 2;
      world.setBlockState(pos, state, flag);
   }

   default boolean isTopSolid(World world, BlockPos pos) {
      return world.getBlockState(pos).isSideSolid(world, pos, EnumFacing.UP);
   }

   default float getAirFraction(World world, BlockPos pos, Rotation rotation) {
      return 1.0F;
   }

   default void generateSurface(World world, BlockPos pos, Random random) {
      Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];
      if ((double)this.getAirFraction(world, pos, rotation) > 0.8D) {
         this.generate(world, pos, rotation);
      }

   }

   default void generateSubterrain(World world, BlockPos pos, Random random) {
      Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];
      if ((double)this.getAirFraction(world, pos, rotation) < 0.4D) {
         this.generate(world, pos, rotation);
      }

   }

   default void generateLava(World world, BlockPos pos, Random random) {
      Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];
      this.generate(world, pos, rotation);
   }
}
