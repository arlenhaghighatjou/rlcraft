package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.entity.tile.TileEntityCanister;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;

public class BlockParasiteCanisterC extends BlockContainer {
   protected static final AxisAlignedBB TALL_GRASS_AABB = new AxisAlignedBB(0.1D, 0.0D, 0.1D, 0.9D, 0.55D, 0.9D);

   public BlockParasiteCanisterC(Material material, String name, float hardness, boolean creative, boolean tickRandom) {
      super(material);
      this.setRegistryName(name);
      this.setUnlocalizedName("srparasites." + name);
      this.setHardness(hardness);
      this.setTickRandomly(tickRandom);
      if (creative) {
         this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
      }

   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return TALL_GRASS_AABB;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      worldIn.setBlockState(pos, state, 3);
      if (stack.hasDisplayName()) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof TileEntityCanister) {
            ((TileEntityCanister)tileentity).setCustomName(stack.getDisplayName());
         }
      }

   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return true;
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof TileEntityCanister) {
         tileentity.updateContainingBlockInfo();
      }

   }

   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof IInventory) {
         InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileentity);
         worldIn.updateComparatorOutputLevel(pos, this);
      }

      super.breakBlock(worldIn, pos, state);
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (worldIn.isRemote) {
         return true;
      } else {
         ILockableContainer ilockablecontainer = this.getLockableContainer(worldIn, pos);
         if (ilockablecontainer != null) {
            playerIn.displayGUIChest(ilockablecontainer);
            playerIn.addStat(StatList.CHEST_OPENED);
         }

         return true;
      }
   }

   @Nullable
   public ILockableContainer getLockableContainer(World worldIn, BlockPos pos) {
      return this.getContainer(worldIn, pos, false);
   }

   @Nullable
   public ILockableContainer getContainer(World worldIn, BlockPos pos, boolean allowBlocking) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (!(tileentity instanceof TileEntityCanister)) {
         return null;
      } else {
         ILockableContainer ilockablecontainer = (TileEntityCanister)tileentity;
         return ilockablecontainer;
      }
   }

   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileEntityCanister();
   }

   public boolean hasComparatorInputOverride(IBlockState state) {
      return true;
   }

   public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
      return Container.calcRedstoneFromInventory(this.getLockableContainer(worldIn, pos));
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.MODEL;
   }
}
