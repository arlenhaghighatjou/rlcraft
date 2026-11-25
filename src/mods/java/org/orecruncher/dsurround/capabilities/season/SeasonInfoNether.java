package org.orecruncher.dsurround.capabilities.season;

import javax.annotation.Nonnull;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SeasonInfoNether extends SeasonInfo {
   public SeasonInfoNether(@Nonnull World world) {
      super(world);
   }

   @Nonnull
   public BlockPos getPrecipitationHeight(@Nonnull BlockPos pos) {
      return new BlockPos(pos.getX(), 0, pos.getZ());
   }

   public boolean canWaterFreeze(@Nonnull BlockPos pos) {
      return false;
   }
}
