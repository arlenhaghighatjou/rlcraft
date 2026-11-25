package meldexun.renderlib.mixin;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({TileEntityEnchantmentTable.class})
public class MixinTileEntityEnchantingTable extends TileEntity {
   public MixinTileEntityEnchantingTable() {
   }

   public AxisAlignedBB getRenderBoundingBox() {
      BlockPos pos = this.getPos();
      double x0 = (double)pos.getX();
      double y0 = (double)pos.getY();
      double z0 = (double)pos.getZ();
      double x1 = (double)pos.getX() + 1.0D;
      double y1 = (double)pos.getY() + 1.3D;
      double z1 = (double)pos.getZ() + 1.0D;
      return new AxisAlignedBB(x0, y0, z0, x1, y1, z1);
   }
}
