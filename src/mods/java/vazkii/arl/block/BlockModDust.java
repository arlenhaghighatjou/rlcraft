package vazkii.arl.block;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.interf.IBlockColorProvider;

public abstract class BlockModDust extends BlockMod implements IBlockColorProvider {
   public static final PropertyEnum<BlockModDust.EnumAttachPosition> NORTH = PropertyEnum.create("north", BlockModDust.EnumAttachPosition.class);
   public static final PropertyEnum<BlockModDust.EnumAttachPosition> EAST = PropertyEnum.create("east", BlockModDust.EnumAttachPosition.class);
   public static final PropertyEnum<BlockModDust.EnumAttachPosition> SOUTH = PropertyEnum.create("south", BlockModDust.EnumAttachPosition.class);
   public static final PropertyEnum<BlockModDust.EnumAttachPosition> WEST = PropertyEnum.create("west", BlockModDust.EnumAttachPosition.class);
   protected static final AxisAlignedBB[] WIRE_AABB = new AxisAlignedBB[]{new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 0.8125D), new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 0.8125D), new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 1.0D), new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 0.8125D, 0.0625D, 0.8125D), new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 0.8125D, 0.0625D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.8125D, 0.0625D, 0.8125D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.8125D, 0.0625D, 1.0D), new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 1.0D, 0.0625D, 0.8125D), new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 1.0D, 0.0625D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 1.0D, 0.0625D, 0.8125D), new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 1.0D, 0.0625D, 1.0D), new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 1.0D, 0.0625D, 0.8125D), new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 0.8125D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D)};

   public BlockModDust(String name) {
      super(name, Material.CIRCUITS);
      this.setDefaultState(this.blockState.getBaseState().withProperty(NORTH, BlockModDust.EnumAttachPosition.NONE).withProperty(EAST, BlockModDust.EnumAttachPosition.NONE).withProperty(SOUTH, BlockModDust.EnumAttachPosition.NONE).withProperty(WEST, BlockModDust.EnumAttachPosition.NONE));
      this.setHardness(0.0F);
      this.setSoundType(SoundType.STONE);
   }

   @Nonnull
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return WIRE_AABB[getAABBIndex(state.getActualState(source, pos))];
   }

   private static int getAABBIndex(IBlockState state) {
      int i = 0;
      boolean north = state.getValue(NORTH) != BlockModDust.EnumAttachPosition.NONE;
      boolean east = state.getValue(EAST) != BlockModDust.EnumAttachPosition.NONE;
      boolean south = state.getValue(SOUTH) != BlockModDust.EnumAttachPosition.NONE;
      boolean west = state.getValue(WEST) != BlockModDust.EnumAttachPosition.NONE;
      if (north || south && !east && !west) {
         i |= 1 << EnumFacing.NORTH.getHorizontalIndex();
      }

      if (east || west && !north && !south) {
         i |= 1 << EnumFacing.EAST.getHorizontalIndex();
      }

      if (south || north && !east && !west) {
         i |= 1 << EnumFacing.SOUTH.getHorizontalIndex();
      }

      if (west || east && !north && !south) {
         i |= 1 << EnumFacing.WEST.getHorizontalIndex();
      }

      return i;
   }

   @Nonnull
   public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      state = state.withProperty(WEST, this.getAttachPosition(worldIn, pos, EnumFacing.WEST));
      state = state.withProperty(EAST, this.getAttachPosition(worldIn, pos, EnumFacing.EAST));
      state = state.withProperty(NORTH, this.getAttachPosition(worldIn, pos, EnumFacing.NORTH));
      state = state.withProperty(SOUTH, this.getAttachPosition(worldIn, pos, EnumFacing.SOUTH));
      return state;
   }

   protected BlockModDust.EnumAttachPosition getAttachPosition(IBlockAccess worldIn, BlockPos pos, EnumFacing direction) {
      BlockPos blockpos = pos.offset(direction);
      IBlockState iblockstate = worldIn.getBlockState(pos.offset(direction));
      if (!this.canConnectTo(worldIn.getBlockState(blockpos), direction, worldIn, blockpos) && (iblockstate.isNormalCube() || !this.canConnectUpwardsTo(worldIn, blockpos.down()))) {
         IBlockState iblockstate1 = worldIn.getBlockState(pos.up());
         if (!iblockstate1.isNormalCube()) {
            boolean flag = worldIn.getBlockState(blockpos).isSideSolid(worldIn, blockpos, EnumFacing.UP) || worldIn.getBlockState(blockpos).getBlock() == Blocks.GLOWSTONE;
            if (flag && this.canConnectUpwardsTo(worldIn, blockpos.up())) {
               if (iblockstate.isBlockNormalCube()) {
                  return BlockModDust.EnumAttachPosition.UP;
               }

               return BlockModDust.EnumAttachPosition.SIDE;
            }
         }

         return BlockModDust.EnumAttachPosition.NONE;
      } else {
         return BlockModDust.EnumAttachPosition.SIDE;
      }
   }

   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
      return null;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean canPlaceBlockAt(World worldIn, @Nonnull BlockPos pos) {
      return worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP) || worldIn.getBlockState(pos.down()).getBlock() == Blocks.GLOWSTONE;
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (!worldIn.isRemote && !this.canPlaceBlockAt(worldIn, pos)) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
      }

   }

   protected boolean canConnectUpwardsTo(IBlockAccess worldIn, BlockPos pos) {
      return this.canConnectTo(worldIn.getBlockState(pos), (EnumFacing)null, worldIn, pos);
   }

   protected boolean canConnectTo(IBlockState blockState, @Nullable EnumFacing side, IBlockAccess world, BlockPos pos) {
      Block block = blockState.getBlock();
      return block == this;
   }

   @Nonnull
   public ItemStack getItem(World worldIn, BlockPos pos, @Nonnull IBlockState state) {
      return new ItemStack(this.getItemDropped(state, worldIn.rand, 0));
   }

   @Nonnull
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState();
   }

   public int getMetaFromState(IBlockState state) {
      return 0;
   }

   @Nonnull
   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   @Nonnull
   public IBlockState withRotation(@Nonnull IBlockState state, Rotation rot) {
      switch(rot) {
      case CLOCKWISE_180:
         return state.withProperty(NORTH, state.getValue(SOUTH)).withProperty(EAST, state.getValue(WEST)).withProperty(SOUTH, state.getValue(NORTH)).withProperty(WEST, state.getValue(EAST));
      case COUNTERCLOCKWISE_90:
         return state.withProperty(NORTH, state.getValue(EAST)).withProperty(EAST, state.getValue(SOUTH)).withProperty(SOUTH, state.getValue(WEST)).withProperty(WEST, state.getValue(NORTH));
      case CLOCKWISE_90:
         return state.withProperty(NORTH, state.getValue(WEST)).withProperty(EAST, state.getValue(NORTH)).withProperty(SOUTH, state.getValue(EAST)).withProperty(WEST, state.getValue(SOUTH));
      default:
         return state;
      }
   }

   @Nonnull
   public IBlockState withMirror(@Nonnull IBlockState state, Mirror mirrorIn) {
      switch(mirrorIn) {
      case LEFT_RIGHT:
         return state.withProperty(NORTH, state.getValue(SOUTH)).withProperty(SOUTH, state.getValue(NORTH));
      case FRONT_BACK:
         return state.withProperty(EAST, state.getValue(WEST)).withProperty(WEST, state.getValue(EAST));
      default:
         return super.withMirror(state, mirrorIn);
      }
   }

   @Nonnull
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{NORTH, EAST, SOUTH, WEST});
   }

   @Nonnull
   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos blockPos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   @Nonnull
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Item.getItemFromBlock(this);
   }

   public abstract int getColor(IBlockAccess var1, IBlockState var2, BlockPos var3, int var4);

   public IBlockColor getBlockColor() {
      return (state, world, pos, tint) -> {
         return this.getColor(world, state, pos, tint);
      };
   }

   @SideOnly(Side.CLIENT)
   public IItemColor getItemColor() {
      return (stack, tint) -> {
         return 16777215;
      };
   }

   protected static enum EnumAttachPosition implements BlockMetaVariants.EnumBase {
      UP,
      SIDE,
      NONE;

      private EnumAttachPosition() {
      }
   }
}
