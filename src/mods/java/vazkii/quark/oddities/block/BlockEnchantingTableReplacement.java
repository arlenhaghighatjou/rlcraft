package vazkii.quark.oddities.block;

import javax.annotation.Nonnull;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.quark.base.Quark;
import vazkii.quark.oddities.tile.TileMatrixEnchanter;
import vazkii.quark.oddities.tile.TileMatrixEnchanterBase;

public class BlockEnchantingTableReplacement extends BlockEnchantmentTable {
   public BlockEnchantingTableReplacement() {
      this.setHardness(5.0F);
      this.setResistance(2000.0F);
   }

   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileMatrixEnchanter();
   }

   public boolean onBlockActivated(World worldIn, @Nonnull BlockPos pos, IBlockState state, @Nonnull EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (worldIn.getTileEntity(pos) instanceof TileEntityEnchantmentTable) {
         worldIn.setTileEntity(pos, this.createNewTileEntity(worldIn, 0));
      }

      playerIn.openGui(Quark.instance, 3, worldIn, pos.getX(), pos.getY(), pos.getZ());
      return true;
   }

   public void onBlockPlacedBy(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase placer, ItemStack stack) {
      super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
      if (stack.hasDisplayName()) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof TileMatrixEnchanterBase) {
            ((TileMatrixEnchanterBase)tileentity).setCustomName(stack.getDisplayName());
         }
      }

   }

   public void breakBlock(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof TileMatrixEnchanterBase) {
         TileMatrixEnchanterBase enchanter = (TileMatrixEnchanterBase)tileentity;
         enchanter.dropItem(0);
         enchanter.dropItem(1);
      }

   }
}
