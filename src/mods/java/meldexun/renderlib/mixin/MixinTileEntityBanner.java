package meldexun.renderlib.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBanner.BlockBannerHanging;
import net.minecraft.block.BlockBanner.BlockBannerStanding;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({TileEntityBanner.class})
public class MixinTileEntityBanner extends TileEntity {
   public MixinTileEntityBanner() {
   }

   public AxisAlignedBB getRenderBoundingBox() {
      BlockPos pos = this.getPos();
      double x0 = (double)pos.getX();
      double y0 = (double)pos.getY();
      double z0 = (double)pos.getZ();
      double x1 = (double)pos.getX() + 1.0D;
      double y1 = (double)pos.getY() + 1.0D;
      double z1 = (double)pos.getZ() + 1.0D;
      Block block = this.getBlockType();
      if (block instanceof BlockBannerHanging) {
         --y0;
      } else if (block instanceof BlockBannerStanding) {
         ++y1;
      }

      return new AxisAlignedBB(x0, y0, z0, x1, y1, z1);
   }
}
