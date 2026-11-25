package com.tmtravlr.qualitytools.reforging;

import com.tmtravlr.qualitytools.QualityToolsMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockReforgingStation extends BlockContainer {
   public static final Block INSTANCE = ((Block)(new BlockReforgingStation()).setRegistryName("reforging_station")).setUnlocalizedName("reforging_station").setHardness(5.0F).setResistance(2000.0F).setCreativeTab(QualityToolsMod.tabQualityTools);
   public static final Item ITEM_INSTANCE = (Item)(new ItemBlock(INSTANCE)).setRegistryName(INSTANCE.getRegistryName());
   protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);

   public BlockReforgingStation() {
      super(Material.IRON);
      this.setDefaultState(this.blockState.getBaseState());
      this.setSoundType(SoundType.ANVIL);
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.MODEL;
   }

   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileEntityReforgingStation();
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return AABB;
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (worldIn.isRemote) {
         return true;
      } else {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof TileEntityReforgingStation) {
            playerIn.openGui(QualityToolsMod.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
         }

         return true;
      }
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      if (stack.hasDisplayName()) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof TileEntityBrewingStand) {
            ((TileEntityBrewingStand)tileentity).setName(stack.getDisplayName());
         }
      }

   }

   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof TileEntityReforgingStation) {
         InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityReforgingStation)tileentity);
      }

      super.breakBlock(worldIn, pos, state);
   }

   public boolean hasComparatorInputOverride(IBlockState state) {
      return true;
   }

   public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
      return Container.calcRedstone(worldIn.getTileEntity(pos));
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }
}
