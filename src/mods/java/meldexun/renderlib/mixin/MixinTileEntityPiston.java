package meldexun.renderlib.mixin;

import net.minecraft.block.BlockPistonMoving;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({TileEntityPiston.class})
public class MixinTileEntityPiston extends TileEntity {
   @Shadow
   private boolean extending;

   public MixinTileEntityPiston() {
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
         if (state.getBlock() instanceof BlockPistonMoving) {
            EnumFacing facing = (EnumFacing)state.getValue(BlockPistonMoving.FACING);
            if (this.extending) {
               facing = facing.getOpposite();
            }

            switch(facing) {
            case DOWN:
               --y0;
               break;
            case UP:
               ++y1;
               break;
            case NORTH:
               --z0;
               break;
            case SOUTH:
               ++z1;
               break;
            case WEST:
               --x0;
               break;
            case EAST:
               ++x1;
            }
         }
      }

      return new AxisAlignedBB(x0, y0, z0, x1, y1, z1);
   }
}
