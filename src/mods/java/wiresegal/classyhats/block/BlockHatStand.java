package wiresegal.classyhats.block;

import com.teamwizardry.librarianlib.features.kotlin.CommonUtilMethods;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J*\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u0016J\u0016\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e2\u0006\u0010\u0007\u001a\u00020\bH\u0016J\"\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\f2\u0006\u0010\u0013\u001a\u00020\u00112\u0006\u0010\u0014\u001a\u00020\u0011H\u0016J\u0010\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0007\u001a\u00020\bH\u0016J\u0010\u0010\u0017\u001a\u00020\u00162\u0006\u0010\u0018\u001a\u00020\bH\u0016¨\u0006\u0019"},
   d2 = {"Lwiresegal/classyhats/block/BlockHatStand;", "Lwiresegal/classyhats/block/BlockHatContainer;", "()V", "getBlockFaceShape", "Lnet/minecraft/block/state/BlockFaceShape;", "world", "Lnet/minecraft/world/IBlockAccess;", "state", "Lnet/minecraft/block/state/IBlockState;", "pos", "Lnet/minecraft/util/math/BlockPos;", "facing", "Lnet/minecraft/util/EnumFacing;", "getCollisionBoxes", "", "Lnet/minecraft/util/math/AxisAlignedBB;", "getPickBlock", "Lnet/minecraft/item/ItemStack;", "side", "hat", "container", "isFullCube", "", "isOpaqueCube", "blockState", "classyhats"}
)
public final class BlockHatStand extends BlockHatContainer {
   @NotNull
   public ItemStack getPickBlock(@Nullable EnumFacing side, @NotNull ItemStack hat, @NotNull ItemStack container) {
      Intrinsics.checkParameterIsNotNull(hat, "hat");
      Intrinsics.checkParameterIsNotNull(container, "container");
      return side == EnumFacing.UP && CommonUtilMethods.isNotEmpty(hat) ? hat : container;
   }

   @NotNull
   public List<AxisAlignedBB> getCollisionBoxes(@NotNull IBlockState state) {
      Intrinsics.checkParameterIsNotNull(state, "state");
      return CollectionsKt.listOf(new AxisAlignedBB[]{new AxisAlignedBB(0.0D, 0.75D, 0.0D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.25D, 0.625D, 0.25D, 0.75D, 0.75D, 0.75D), new AxisAlignedBB(0.3125D, 0.25D, 0.3125D, 0.6875D, 0.625D, 0.6875D), new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.25D, 0.8125D)});
   }

   public boolean isFullCube(@NotNull IBlockState state) {
      Intrinsics.checkParameterIsNotNull(state, "state");
      return false;
   }

   public boolean isOpaqueCube(@NotNull IBlockState blockState) {
      Intrinsics.checkParameterIsNotNull(blockState, "blockState");
      return false;
   }

   @NotNull
   public BlockFaceShape getBlockFaceShape(@NotNull IBlockAccess world, @NotNull IBlockState state, @NotNull BlockPos pos, @Nullable EnumFacing facing) {
      // $FF: Couldn't be decompiled
   }

   public BlockHatStand() {
      super("hat_stand");
   }
}
