package vazkii.arl.block;

import javax.annotation.Nonnull;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockModContainer extends BlockMod implements ITileEntityProvider {
   public BlockModContainer(String name, Material materialIn, String... variants) {
      super(name, materialIn, variants);
      this.hasTileEntity = true;
   }

   public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
      super.breakBlock(worldIn, pos, state);
      worldIn.removeTileEntity(pos);
   }

   public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int eventID, int eventParam) {
      super.eventReceived(state, worldIn, pos, eventID, eventParam);
      TileEntity tileentity = worldIn.getTileEntity(pos);
      return tileentity != null && tileentity.receiveClientEvent(eventID, eventParam);
   }

   public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
      return null;
   }
}
