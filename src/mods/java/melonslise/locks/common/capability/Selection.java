package melonslise.locks.common.capability;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class Selection implements ISelection {
   public static final ResourceLocation ID = new ResourceLocation("locks", "lock_selection");
   public BlockPos pos;

   public Selection() {
   }

   public BlockPos get() {
      return this.pos;
   }

   public void set(BlockPos pos) {
      this.pos = pos;
   }
}
