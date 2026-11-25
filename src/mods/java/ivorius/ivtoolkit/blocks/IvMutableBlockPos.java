package ivorius.ivtoolkit.blocks;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;

public class IvMutableBlockPos {
   public IvMutableBlockPos() {
   }

   public static MutableBlockPos add(MutableBlockPos pos, BlockPos add) {
      return pos.setPos(pos.getX() + add.getX(), pos.getY() + add.getY(), pos.getZ() + add.getZ());
   }

   public static MutableBlockPos offset(BlockPos pos, MutableBlockPos dest, EnumFacing facing) {
      return offset(pos, dest, facing, 1);
   }

   public static MutableBlockPos offset(BlockPos pos, MutableBlockPos dest, EnumFacing facing, int amount) {
      return dest.setPos(pos.getX() + facing.getFrontOffsetX() * amount, pos.getY() + facing.getFrontOffsetY() * amount, pos.getZ() + facing.getFrontOffsetZ() * amount);
   }
}
