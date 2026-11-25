package vazkii.quark.decoration.entity;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import vazkii.quark.decoration.feature.GlassItemFrame;

public class EntityGlassItemFrame extends EntityFlatItemFrame {
   public EntityGlassItemFrame(World worldIn) {
      super(worldIn);
   }

   public EntityGlassItemFrame(World worldIn, BlockPos blockPos, EnumFacing face) {
      super(worldIn, blockPos, face);
   }

   protected void dropFrame() {
      this.entityDropItem(new ItemStack(GlassItemFrame.glass_item_frame, 1, 0), 0.0F);
   }

   @Nonnull
   public ItemStack getPickedResult(RayTraceResult target) {
      ItemStack held = this.getDisplayedItem();
      return held.isEmpty() ? new ItemStack(GlassItemFrame.glass_item_frame) : held.copy();
   }
}
