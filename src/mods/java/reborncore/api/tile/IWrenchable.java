package reborncore.api.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IWrenchable {
   EnumFacing getFacing(World var1, BlockPos var2);

   default boolean canSetFacing(World world, BlockPos pos, EnumFacing newFacing, EntityPlayer player) {
      return true;
   }

   boolean setFacing(World var1, BlockPos var2, EnumFacing var3, EntityPlayer var4);
}
