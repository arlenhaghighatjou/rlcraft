package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.core.ModBlocks;
import com.github.alexthe666.iceandfire.core.ModItems;
import com.github.alexthe666.iceandfire.structures.WorldUtils;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSilverPile extends Block {
   public static final PropertyInteger LAYERS = PropertyInteger.create("layers", 1, 8);
   protected static final AxisAlignedBB[] SNOW_AABB = new AxisAlignedBB[]{new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.625D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)};
   public Item itemBlock;

   public BlockSilverPile() {
      super(Material.GROUND);
      this.setDefaultState(this.blockState.getBaseState().withProperty(LAYERS, Integer.valueOf(1)));
      this.setTickRandomly(true);
      this.setCreativeTab(IceAndFire.TAB);
      this.setUnlocalizedName("iceandfire.silverpile");
      this.setHardness(0.3F);
      this.setSoundType(ModBlocks.SOUND_TYPE_GOLD);
      this.setRegistryName("iceandfire", "silverpile");
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return SNOW_AABB[((Integer)state.getValue(LAYERS)).intValue()];
   }

   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      return ((Integer)worldIn.getBlockState(pos).getValue(LAYERS)).intValue() < 5;
   }

   /** @deprecated */
   @Deprecated
   public boolean canEntitySpawn(IBlockState state, Entity entityIn) {
      return false;
   }

   public boolean isTopSolid(IBlockState state) {
      return ((Integer)state.getValue(LAYERS)).intValue() == 7;
   }

   public AxisAlignedBB getSelectedBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
      int i = ((Integer)blockState.getValue(LAYERS)).intValue() - 1;
      float f = 0.125F;
      AxisAlignedBB axisalignedbb = blockState.getBoundingBox(worldIn, pos);
      return new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.maxX, (double)((float)i * f), axisalignedbb.maxZ);
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      IBlockState iblockstate = worldIn.getBlockState(pos.down());
      Block block = iblockstate.getBlock();
      return block != Blocks.ICE && block != Blocks.PACKED_ICE ? (iblockstate.getBlock().isLeaves(iblockstate, worldIn, pos.down()) ? true : (block == this && ((Integer)iblockstate.getValue(LAYERS)).intValue() >= 7 ? true : iblockstate.isOpaqueCube() && iblockstate.getMaterial().blocksMovement())) : false;
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      ItemStack item = playerIn.inventory.getCurrentItem();
      if (!item.isEmpty() && item.getItem() != null && item.getItem() == Item.getItemFromBlock(ModBlocks.silverPile) && !item.isEmpty() && this.getMetaFromState(state) < 7) {
         WorldUtils.setBlock(worldIn, pos.getX(), pos.getY(), pos.getZ(), ModBlocks.silverPile, this.getMetaFromState(state) + 1, 3);
         if (!playerIn.capabilities.isCreativeMode) {
            item.shrink(1);
            if (item.isEmpty()) {
               playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, ItemStack.EMPTY);
            }
         }

         return true;
      } else {
         return super.onBlockActivated(worldIn, pos, state, playerIn, hand, side, hitX, hitY, hitZ);
      }
   }

   public boolean isOpaqueCube(IBlockState blockstate) {
      return false;
   }

   public boolean isFullCube(IBlockState blockstate) {
      return false;
   }

   public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
      if (world instanceof World) {
         this.checkAndDropBlock((World)world, pos, ((World)world).getBlockState(neighbor));
      }

   }

   private boolean checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
      if (!this.canPlaceBlockAt(worldIn, pos)) {
         worldIn.destroyBlock(pos, true);
         return false;
      } else {
         return true;
      }
   }

   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return ModItems.silverNugget;
   }

   @SideOnly(Side.CLIENT)
   public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      return side == EnumFacing.UP ? true : super.shouldSideBeRendered(blockState, blockAccess, pos, side);
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(LAYERS, Integer.valueOf((meta & 7) + 1));
   }

   public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
      return ((Integer)worldIn.getBlockState(pos).getValue(LAYERS)).intValue() == 1;
   }

   public int getMetaFromState(IBlockState state) {
      return ((Integer)state.getValue(LAYERS)).intValue() - 1;
   }

   public int quantityDropped(IBlockState state, int fortune, Random random) {
      return ((Integer)state.getValue(LAYERS)).intValue() + 1;
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{LAYERS});
   }
}
