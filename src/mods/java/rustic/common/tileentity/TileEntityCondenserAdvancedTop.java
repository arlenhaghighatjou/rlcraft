package rustic.common.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class TileEntityCondenserAdvancedTop extends TileEntity {
   public TileEntityCondenserAdvancedTop() {
   }

   public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      TileEntity tile = this.world.getTileEntity(this.pos.down());
      return tile instanceof TileEntityCondenserAdvancedBottom ? ((TileEntityCondenserAdvancedBottom)tile).hasCapability(capability, facing) : false;
   }

   @Nullable
   public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      TileEntity tile = this.world.getTileEntity(this.pos.down());
      return (T)(tile instanceof TileEntityCondenserAdvancedBottom ? ((TileEntityCondenserAdvancedBottom)tile).getCapability(capability, facing) : null);
   }

   public void breakBlock(World world, BlockPos pos, IBlockState state) {
      this.invalidate();
      world.setTileEntity(pos, (TileEntity)null);
   }
}
