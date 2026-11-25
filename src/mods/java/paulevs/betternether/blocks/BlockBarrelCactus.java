package paulevs.betternether.blocks;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.Block.EnumOffsetType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import paulevs.betternether.BetterNether;

public class BlockBarrelCactus extends Block {
   private static final AxisAlignedBB BARREL_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D);
   private static final AxisAlignedBB BARREL_COLLIDE_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.8125D, 0.9375D);

   public BlockBarrelCactus() {
      super(Material.CACTUS, MapColor.ORANGE_STAINED_HARDENED_CLAY);
      this.setCreativeTab(BetterNether.BN_TAB);
      this.setRegistryName("barrel_cactus");
      this.setUnlocalizedName("barrel_cactus");
      this.setTickRandomly(true);
      this.setSoundType(SoundType.CLOTH);
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return BARREL_AABB;
   }

   @Nullable
   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
      return BARREL_COLLIDE_AABB;
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      this.canStay(worldIn, pos);
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   private boolean canStay(World worldIn, BlockPos pos) {
      Block under = worldIn.getBlockState(pos.down()).getBlock();
      if (under != Blocks.GRAVEL) {
         worldIn.destroyBlock(pos, true);
         return false;
      } else {
         return true;
      }
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos.down()).getBlock() == Blocks.GRAVEL;
   }

   public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
      entityIn.attackEntityFrom(DamageSource.CACTUS, 1.5F);
   }

   public EnumOffsetType getOffsetType() {
      return EnumOffsetType.XYZ;
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }
}
