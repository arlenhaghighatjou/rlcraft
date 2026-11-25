package meldexun.renderlib.mixin;

import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({TileEntityShulkerBox.class})
public class MixinTileEntityShulkerBox extends TileEntity {
   public MixinTileEntityShulkerBox() {
   }

   public AxisAlignedBB getRenderBoundingBox() {
      BlockPos pos = this.getPos();
      double x0 = (double)pos.getX();
      double y0 = (double)pos.getY();
      double z0 = (double)pos.getZ();
      double x1 = (double)pos.getX() + 1.0D;
      double y1 = (double)pos.getY() + 1.0D;
      double z1 = (double)pos.getZ() + 1.0D;
      if (this.hasWorld()) {
         World world = this.getWorld();
         IBlockState state = world.getBlockState(pos);
         if (state.getBlock() instanceof BlockShulkerBox) {
            EnumFacing facing = (EnumFacing)state.getValue(BlockShulkerBox.FACING);
            Axis axis = facing.getAxis();
            if (axis != Axis.X) {
               x0 -= 0.25D;
               x1 += 0.25D;
            }

            if (axis != Axis.Y) {
               y0 -= 0.25D;
               y1 += 0.25D;
            }

            if (axis != Axis.Z) {
               z0 -= 0.25D;
               z1 += 0.25D;
            }

            switch(facing) {
            case DOWN:
               y0 -= 0.5D;
               break;
            case UP:
               y1 += 0.5D;
               break;
            case NORTH:
               z0 -= 0.5D;
               break;
            case SOUTH:
               z1 += 0.5D;
               break;
            case WEST:
               x0 -= 0.5D;
               break;
            case EAST:
               x1 += 0.5D;
            }
         }
      }

      return new AxisAlignedBB(x0, y0, z0, x1, y1, z1);
   }
}
