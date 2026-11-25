package rustic.common.util;

import javax.annotation.Nonnull;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rustic.common.blocks.BlockRopeBase;
import rustic.common.blocks.ModBlocks;

public class DispenseRope extends BehaviorDefaultDispenseItem {
   private static final DispenseRope INSTANCE = new DispenseRope();

   public static DispenseRope getInstance() {
      return INSTANCE;
   }

   private DispenseRope() {
   }

   @Nonnull
   public ItemStack dispenseStack(@Nonnull IBlockSource source, @Nonnull ItemStack stack) {
      World world = source.getWorld();
      EnumFacing facing = (EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING);
      BlockPos pos = source.getBlockPos().offset(facing);
      if (world.getBlockState(pos).getBlock() == ModBlocks.ROPE) {
         if (this.addRope(world, pos)) {
            ItemStack ret = stack.copy();
            ret.shrink(1);
            return ret;
         }
      } else if (world.isAirBlock(pos) && world.getBlockState(pos.offset(facing)).getBlock() == ModBlocks.ROPE) {
         pos = pos.offset(facing);
         if (this.addRope(world, pos)) {
            ItemStack ret = stack.copy();
            ret.shrink(1);
            return ret;
         }
      }

      return super.dispenseStack(source, stack);
   }

   private boolean addRope(World world, BlockPos pos) {
      int yOffset;
      for(yOffset = 1; yOffset < 64 && world.getBlockState(pos.down(yOffset)).getBlock() == ModBlocks.ROPE; ++yOffset) {
         if (world.getBlockState(pos.down(yOffset)).getValue(BlockRopeBase.AXIS) != Axis.Y) {
            return false;
         }
      }

      if (ModBlocks.ROPE.canPlaceBlockAt(world, pos.down(yOffset))) {
         IBlockState state = ModBlocks.ROPE.getDefaultState().withProperty(BlockRopeBase.AXIS, Axis.Y);
         world.setBlockState(pos.down(yOffset), state, 3);
         SoundType soundType = ModBlocks.ROPE.getSoundType(state, world, pos, (Entity)null);
         world.playSound((double)pos.getX(), (double)(pos.getY() - yOffset), (double)pos.getZ(), soundType.getPlaceSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F, false);
         return true;
      } else {
         return false;
      }
   }
}
