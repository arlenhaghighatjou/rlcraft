package vazkii.quark.base.block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockMod;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;

public class BlockQuarkWall extends BlockMod implements IQuarkBlock {
   public static final PropertyBool UP = PropertyBool.create("up");
   public static final PropertyBool NORTH = PropertyBool.create("north");
   public static final PropertyBool EAST = PropertyBool.create("east");
   public static final PropertyBool SOUTH = PropertyBool.create("south");
   public static final PropertyBool WEST = PropertyBool.create("west");
   protected static final AxisAlignedBB[] AABB_BY_INDEX = new AxisAlignedBB[]{new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D), new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D), new AxisAlignedBB(0.0D, 0.0D, 0.25D, 0.75D, 1.0D, 1.0D), new AxisAlignedBB(0.25D, 0.0D, 0.0D, 0.75D, 1.0D, 0.75D), new AxisAlignedBB(0.3125D, 0.0D, 0.0D, 0.6875D, 0.875D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.75D, 1.0D, 0.75D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.75D, 1.0D, 1.0D), new AxisAlignedBB(0.25D, 0.0D, 0.25D, 1.0D, 1.0D, 0.75D), new AxisAlignedBB(0.25D, 0.0D, 0.25D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.3125D, 1.0D, 0.875D, 0.6875D), new AxisAlignedBB(0.0D, 0.0D, 0.25D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.25D, 0.0D, 0.0D, 1.0D, 1.0D, 0.75D), new AxisAlignedBB(0.25D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.75D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)};
   protected static final AxisAlignedBB[] CLIP_AABB_BY_INDEX = new AxisAlignedBB[]{AABB_BY_INDEX[0].setMaxY(1.5D), AABB_BY_INDEX[1].setMaxY(1.5D), AABB_BY_INDEX[2].setMaxY(1.5D), AABB_BY_INDEX[3].setMaxY(1.5D), AABB_BY_INDEX[4].setMaxY(1.5D), AABB_BY_INDEX[5].setMaxY(1.5D), AABB_BY_INDEX[6].setMaxY(1.5D), AABB_BY_INDEX[7].setMaxY(1.5D), AABB_BY_INDEX[8].setMaxY(1.5D), AABB_BY_INDEX[9].setMaxY(1.5D), AABB_BY_INDEX[10].setMaxY(1.5D), AABB_BY_INDEX[11].setMaxY(1.5D), AABB_BY_INDEX[12].setMaxY(1.5D), AABB_BY_INDEX[13].setMaxY(1.5D), AABB_BY_INDEX[14].setMaxY(1.5D), AABB_BY_INDEX[15].setMaxY(1.5D)};
   private final IBlockState parent;

   public BlockQuarkWall(String name, IBlockState state) {
      super(name, state.getMaterial(), new String[0]);
      this.parent = state;
      this.setDefaultState(this.blockState.getBaseState().withProperty(UP, Boolean.FALSE).withProperty(NORTH, Boolean.FALSE).withProperty(EAST, Boolean.FALSE).withProperty(SOUTH, Boolean.FALSE).withProperty(WEST, Boolean.FALSE));
      this.setCreativeTab(CreativeTabs.DECORATIONS);
   }

   public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
      return this.parent.getBlock().getExplosionResistance(world, pos, exploder, explosion) * 5.0F / 3.0F;
   }

   public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
      return this.parent.getBlockHardness(worldIn, pos);
   }

   @Nonnull
   public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
      return this.parent.getBlock().getSoundType(this.parent, world, pos, entity);
   }

   public int getMetaFromState(IBlockState state) {
      return 0;
   }

   @Nonnull
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      state = this.getActualState(state, source, pos);
      return AABB_BY_INDEX[getAABBIndex(state)];
   }

   @Nullable
   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
      blockState = this.getActualState(blockState, worldIn, pos);
      return CLIP_AABB_BY_INDEX[getAABBIndex(blockState)];
   }

   private static int getAABBIndex(IBlockState state) {
      int i = 0;
      if (((Boolean)state.getValue(NORTH)).booleanValue()) {
         i |= 1 << EnumFacing.NORTH.getHorizontalIndex();
      }

      if (((Boolean)state.getValue(EAST)).booleanValue()) {
         i |= 1 << EnumFacing.EAST.getHorizontalIndex();
      }

      if (((Boolean)state.getValue(SOUTH)).booleanValue()) {
         i |= 1 << EnumFacing.SOUTH.getHorizontalIndex();
      }

      if (((Boolean)state.getValue(WEST)).booleanValue()) {
         i |= 1 << EnumFacing.WEST.getHorizontalIndex();
      }

      return i;
   }

   public boolean canPlaceTorchOnTop(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
      return true;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      return false;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   private boolean canConnectTo(IBlockAccess worldIn, BlockPos pos) {
      boolean var10000;
      label40: {
         IBlockState iblockstate = worldIn.getBlockState(pos);
         Block block = iblockstate.getBlock();
         Material material = iblockstate.getMaterial();
         if (block != Blocks.BARRIER) {
            if (block == this || block instanceof BlockFenceGate) {
               break label40;
            }

            if (material.isOpaque() && iblockstate.isFullCube()) {
               if (material != Material.GOURD) {
                  break label40;
               }
            } else if (block instanceof BlockQuarkWall || block instanceof BlockWall) {
               break label40;
            }
         }

         var10000 = false;
         return var10000;
      }

      var10000 = true;
      return var10000;
   }

   @SideOnly(Side.CLIENT)
   public boolean shouldSideBeRendered(IBlockState blockState, @Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos, EnumFacing side) {
      return side != EnumFacing.DOWN || super.shouldSideBeRendered(blockState, blockAccess, pos, side);
   }

   @Nonnull
   public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      boolean flag = this.canConnectTo(worldIn, pos.north());
      boolean flag1 = this.canConnectTo(worldIn, pos.east());
      boolean flag2 = this.canConnectTo(worldIn, pos.south());
      boolean flag3 = this.canConnectTo(worldIn, pos.west());
      boolean flag4 = flag && !flag1 && flag2 && !flag3 || !flag && flag1 && !flag2 && flag3;
      return state.withProperty(UP, Boolean.valueOf(!flag4 || !worldIn.isAirBlock(pos.up()))).withProperty(NORTH, Boolean.valueOf(flag)).withProperty(EAST, Boolean.valueOf(flag1)).withProperty(SOUTH, Boolean.valueOf(flag2)).withProperty(WEST, Boolean.valueOf(flag3));
   }

   @Nonnull
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{UP, NORTH, EAST, WEST, SOUTH});
   }

   @Nonnull
   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return face != EnumFacing.UP && face != EnumFacing.DOWN ? BlockFaceShape.MIDDLE_POLE_THICK : BlockFaceShape.CENTER_BIG;
   }

   public IProperty[] getIgnoredProperties() {
      return new IProperty[]{UP, NORTH, EAST, WEST, SOUTH};
   }

   public static void initWall(Block base, int meta, Block block) {
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(block, 6), new Object[]{"BBB", "BBB", 'B', ProxyRegistry.newStack(base, 1, meta)});
   }
}
