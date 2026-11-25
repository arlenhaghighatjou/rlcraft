package paulevs.betternether.blocks;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockNetherrack;
import net.minecraft.block.SoundType;
import net.minecraft.block.Block.EnumOffsetType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import paulevs.betternether.items.ItemsRegister;

public class BlockBlackApple extends Block {
   private static final AxisAlignedBB SELECT_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);

   public BlockBlackApple() {
      super(Material.PLANTS, MapColor.WHITE_STAINED_HARDENED_CLAY);
      this.setHardness(1.0F);
      this.setRegistryName("black_apple");
      this.setUnlocalizedName("black_apple");
      this.setSoundType(SoundType.PLANT);
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return SELECT_AABB;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   @Nullable
   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
      return NULL_AABB;
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      Block under = worldIn.getBlockState(pos.down()).getBlock();
      return under instanceof BlockNetherrack || under == Blocks.SOUL_SAND;
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (!this.canPlaceBlockAt(worldIn, pos)) {
         worldIn.destroyBlock(pos, true);
      }

   }

   public EnumOffsetType getOffsetType() {
      return EnumOffsetType.XZ;
   }

   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return ItemsRegister.BLACK_APPLE;
   }
}
