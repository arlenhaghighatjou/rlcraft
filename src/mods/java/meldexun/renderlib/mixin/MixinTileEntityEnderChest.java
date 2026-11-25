package meldexun.renderlib.mixin;

import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({TileEntityEnderChest.class})
public class MixinTileEntityEnderChest extends TileEntity {
   public MixinTileEntityEnderChest() {
   }

   public AxisAlignedBB getRenderBoundingBox() {
      BlockPos pos = this.getPos();
      double x0 = (double)pos.getX();
      double y0 = (double)pos.getY();
      double z0 = (double)pos.getZ();
      double x1 = (double)pos.getX() + 1.0D;
      double y1 = (double)pos.getY() + 1.6D;
      double z1 = (double)pos.getZ() + 1.0D;
      EnumFacing facing = EnumFacing.NORTH;
      if (this.hasWorld()) {
         World world = this.getWorld();
         IBlockState state = world.getBlockState(pos);
         if (state.getBlock() instanceof BlockEnderChest) {
            facing = (EnumFacing)state.getValue(BlockEnderChest.FACING);
         }
      }

      switch(facing) {
      case NORTH:
         z1 += 0.25D;
         break;
      case SOUTH:
         z0 -= 0.25D;
         break;
      case WEST:
         x1 += 0.25D;
         break;
      case EAST:
         x0 -= 0.25D;
         break;
      default:
         throw new IllegalArgumentException();
      }

      return new AxisAlignedBB(x0, y0, z0, x1, y1, z1);
   }
}
