package ivorius.reccomplex.block;

import com.google.common.collect.Lists;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.block.materials.RCMaterials;
import ivorius.reccomplex.utils.UnstableBlock;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockGenericSpace extends BlockTyped implements UnstableBlock {
   public static final PropertyBool VISIBLE = PropertyBool.create("visible");
   public static final AxisAlignedBB SPACE_AABB;

   public BlockGenericSpace() {
      super(RCMaterials.materialNegativeSpace);
      this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, Integer.valueOf(0)).withProperty(VISIBLE, Boolean.valueOf(true)));
   }

   @Nonnull
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return SPACE_AABB;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isNormalCube(IBlockState state) {
      return false;
   }

   @Nonnull
   public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, @Nonnull IBlockState state, int fortune) {
      return Lists.newArrayList();
   }

   @Nullable
   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
      return NULL_AABB;
   }

   @Nonnull
   public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return RCConfig.hideRedundantNegativeSpace && Stream.of(EnumFacing.VALUES).allMatch((f) -> {
         return this.hideableAtSide(state, worldIn, pos, f);
      }) ? state.withProperty(VISIBLE, Boolean.valueOf(false)) : state.withProperty(VISIBLE, Boolean.valueOf(true));
   }

   protected boolean hideableAtSide(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing f) {
      IBlockState sideState = worldIn.getBlockState(pos.offset(f));
      return sideState.getBlock() == this && Objects.equals(sideState.getValue(TYPE), sideState.getValue(TYPE));
   }

   @Nonnull
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{TYPE, VISIBLE});
   }

   static {
      float lowB = 0.3125F;
      float highB = 1.0F - lowB;
      SPACE_AABB = new AxisAlignedBB((double)lowB, (double)lowB, (double)lowB, (double)highB, (double)highB, (double)highB);
   }
}
