package org.orecruncher.lib.chunk;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public interface IBlockAccessEx extends IBlockAccess {
   int reference();

   int worldReference();

   @Nullable
   World getWorld();

   @Nonnull
   IBlockState getBlockState(int var1, int var2, int var3);

   int getLightFor(@Nonnull EnumSkyBlock var1, @Nonnull BlockPos var2);

   @Nonnull
   BlockPos getTopSolidOrLiquidBlock(@Nonnull BlockPos var1);

   boolean isAvailable(int var1, int var2);

   default boolean isAvailable(@Nonnull BlockPos pos) {
      return this.isAvailable(pos.getX(), pos.getZ());
   }

   @Nonnull
   BlockPos getPrecipitationHeight(@Nonnull BlockPos var1);
}
