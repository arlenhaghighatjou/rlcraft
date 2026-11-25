package slimeknights.mantle.block;

import javax.annotation.Nonnull;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.mantle.tileentity.TileInventory;

public abstract class BlockInventory extends BlockContainer {
   protected BlockInventory(Material material) {
      super(material);
   }

   public boolean hasTileEntity(IBlockState state) {
      return true;
   }

   @Nonnull
   public abstract TileEntity createNewTileEntity(@Nonnull World var1, int var2);

   protected abstract boolean openGui(EntityPlayer var1, World var2, BlockPos var3);

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float clickX, float clickY, float clickZ) {
      if (player.isSneaking()) {
         return false;
      } else {
         return !world.isRemote ? this.openGui(player, world, pos) : true;
      }
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
      if (stack.hasDisplayName()) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof TileInventory) {
            ((TileInventory)tileentity).setCustomName(stack.getDisplayName());
         }
      }

   }

   public void breakBlock(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof TileInventory) {
         InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileentity);
         worldIn.updateComparatorOutputLevel(pos, this);
      }

      super.breakBlock(worldIn, pos, state);
   }

   public int damageDropped(IBlockState state) {
      return this.getMetaFromState(state);
   }

   @Nonnull
   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.MODEL;
   }
}
