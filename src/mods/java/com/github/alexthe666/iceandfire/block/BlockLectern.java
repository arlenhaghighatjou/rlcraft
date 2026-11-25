package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityLectern;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockLectern extends BlockContainer {
   public static final PropertyDirection FACING = PropertyDirection.create("facing", Plane.HORIZONTAL);
   public Item itemBlock;

   public BlockLectern() {
      super(Material.WOOD);
      this.setHardness(2.0F);
      this.setResistance(5.0F);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
      this.setSoundType(SoundType.WOOD);
      this.setCreativeTab(IceAndFire.TAB);
      this.setUnlocalizedName("iceandfire.lectern");
      this.setRegistryName("iceandfire", "lectern");
      GameRegistry.registerTileEntity(TileEntityLectern.class, "lectern");
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.4375D, 0.875D);
   }

   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof TileEntityLectern) {
         InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityLectern)tileentity);
         worldIn.updateComparatorOutputLevel(pos, this);
      }

      super.breakBlock(worldIn, pos, state);
   }

   public boolean isOpaqueCube(IBlockState blockstate) {
      return false;
   }

   public boolean isFullCube(IBlockState blockstate) {
      return false;
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      IBlockState iblockstate = worldIn.getBlockState(pos.down());
      Block block = iblockstate.getBlock();
      return iblockstate.isSideSolid(worldIn, pos, EnumFacing.UP);
   }

   /** @deprecated */
   @Deprecated
   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      this.checkFall(worldIn, pos);
   }

   private boolean checkFall(World worldIn, BlockPos pos) {
      if (!this.canPlaceBlockAt(worldIn, pos)) {
         worldIn.destroyBlock(pos, true);
         return false;
      } else {
         return true;
      }
   }

   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(FACING, placer.getHorizontalFacing().getOpposite());
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
   }

   public int getMetaFromState(IBlockState state) {
      return ((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{FACING});
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.MODEL;
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (playerIn.isSneaking()) {
         return false;
      } else {
         playerIn.openGui(IceAndFire.INSTANCE, 2, worldIn, pos.getX(), pos.getY(), pos.getZ());
         return true;
      }
   }

   public TileEntity createNewTileEntity(World world, int meta) {
      return new TileEntityLectern();
   }
}
