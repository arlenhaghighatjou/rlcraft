package paulevs.betternether.blocks;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockNetherrack;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import paulevs.betternether.BetterNether;

public class BlockInkBushSeed extends Block implements IGrowable {
   public BlockInkBushSeed() {
      super(Material.PLANTS, MapColor.RED_STAINED_HARDENED_CLAY);
      this.setCreativeTab(BetterNether.BN_TAB);
      this.setSoundType(SoundType.PLANT);
      this.setRegistryName("ink_bush_seed");
      this.setUnlocalizedName("ink_bush_seed");
      this.setTickRandomly(true);
   }

   private boolean canStay(World world, BlockPos pos) {
      Block under = world.getBlockState(pos.down()).getBlock();
      return under instanceof BlockNetherrack || under == Blocks.SOUL_SAND;
   }

   public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      return this.canStay(worldIn, pos);
   }

   public boolean canUseBonemeal(World worldIn, EaglercraftRandom rand, BlockPos pos, IBlockState state) {
      return (double)worldIn.rand.nextFloat() < 0.45D;
   }

   public void grow(World world, EaglercraftRandom random, BlockPos pos, IBlockState state) {
      world.setBlockState(pos, BlocksRegister.BLOCK_INK_BUSH.getDefaultState());
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (!this.canStay(worldIn, pos)) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
      }

   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      if (this.canStay(worldIn, pos)) {
         worldIn.setBlockState(pos, state, 2);
      }

   }

   @Nullable
   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
      return NULL_AABB;
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return this.canStay(worldIn, pos);
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      if (!this.canStay(worldIn, pos)) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
      } else if (rand.nextInt(16) == 0) {
         worldIn.setBlockState(pos, BlocksRegister.BLOCK_INK_BUSH.getDefaultState());
      }

   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }
}
