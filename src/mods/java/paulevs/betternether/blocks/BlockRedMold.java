package paulevs.betternether.blocks;

import net.minecraft.util.math.AxisAlignedBB;

public class BlockRedMold extends BlockMold {
   private static final AxisAlignedBB AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.5D, 0.875D);

   public BlockRedMold() {
      super("red_mold");
   }
}
