package vazkii.quark.decoration.block;

import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.arl.block.BlockMod;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockBlazeRod extends BlockMod implements IQuarkBlock {
   protected static final AxisAlignedBB BLAZE_ROD_VERTICAL_AABB = new AxisAlignedBB(0.4375D, 0.0D, 0.4375D, 0.5625D, 1.0D, 0.5625D);
   protected static final AxisAlignedBB BLAZE_ROD_NS_AABB = new AxisAlignedBB(0.4375D, 0.4375D, 0.0D, 0.5625D, 0.5625D, 1.0D);
   protected static final AxisAlignedBB BLAZE_ROD_EW_AABB = new AxisAlignedBB(0.0D, 0.4375D, 0.4375D, 1.0D, 0.5625D, 0.5625D);
   public static final PropertyDirection FACING = BlockDirectional.FACING;

   public BlockBlazeRod() {
      super("blaze_rod", Material.IRON, new String[0]);
      this.setLightLevel(1.0F);
      this.setHardness(5.0F);
      this.setResistance(10.0F);
      this.setSoundType(SoundType.METAL);
   }

   public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
   }

   public ItemBlock createItemBlock(ResourceLocation res) {
      return null;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   protected boolean canSilkHarvest() {
      return false;
   }

   @Nonnull
   public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
      return new ItemStack(Items.BLAZE_ROD);
   }

   @Nonnull
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Items.BLAZE_ROD;
   }

   public int quantityDropped(IBlockState state, int fortune, @Nonnull Random random) {
      return 1;
   }

   @Nonnull
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      switch(((EnumFacing)state.getValue(FACING)).getAxis()) {
      case X:
         return BLAZE_ROD_EW_AABB;
      case Z:
         return BLAZE_ROD_NS_AABB;
      default:
         return BLAZE_ROD_VERTICAL_AABB;
      }
   }

   @Nonnull
   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      IBlockState iblockstate = worldIn.getBlockState(pos.offset(facing.getOpposite()));
      if (iblockstate.getBlock() == Blocks.END_ROD) {
         EnumFacing enumfacing = (EnumFacing)iblockstate.getValue(FACING);
         if (enumfacing == facing) {
            return this.getDefaultState().withProperty(FACING, facing.getOpposite());
         }
      }

      return this.getDefaultState().withProperty(FACING, facing);
   }

   @Nonnull
   public IBlockState withRotation(@Nonnull IBlockState state, Rotation rot) {
      return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
   }

   @Nonnull
   public IBlockState withMirror(@Nonnull IBlockState state, Mirror mirrorIn) {
      return state.withProperty(FACING, mirrorIn.mirror((EnumFacing)state.getValue(FACING)));
   }

   public int getMetaFromState(IBlockState state) {
      return ((EnumFacing)state.getValue(FACING)).getIndex();
   }

   @Nonnull
   public IBlockState getStateFromMeta(int meta) {
      IBlockState iblockstate = this.getDefaultState();
      iblockstate = iblockstate.withProperty(FACING, EnumFacing.getFront(meta));
      return iblockstate;
   }

   @Nonnull
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{FACING});
   }

   @Nonnull
   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }
}
