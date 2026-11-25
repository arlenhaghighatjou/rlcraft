package paulevs.betternether.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import paulevs.betternether.BetterNether;

public class BlockChestOfDrawers extends BlockInventoryUniversal {
   private static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.5D, 1.0D, 1.0D, 1.0D);
   private static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.5D);
   private static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.5D, 1.0D, 1.0D);
   private static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.5D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);

   public BlockChestOfDrawers() {
      super(Material.IRON);
      this.setHardness(3.0F);
      this.setResistance(10.0F);
      this.setSoundType(SoundType.METAL);
      this.setUnlocalizedName("chest_of_drawers");
      this.setRegistryName("chest_of_drawers");
      this.setCreativeTab(BetterNether.BN_TAB);
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

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      switch((EnumFacing)state.getValue(FACING)) {
      case NORTH:
         return NORTH_AABB;
      case SOUTH:
         return SOUTH_AABB;
      case EAST:
         return EAST_AABB;
      case WEST:
      default:
         return WEST_AABB;
      }
   }
}
