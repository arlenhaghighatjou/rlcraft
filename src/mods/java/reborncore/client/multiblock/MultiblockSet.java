package reborncore.client.multiblock;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class MultiblockSet {
   private final Multiblock[] mbs;

   public MultiblockSet(Multiblock[] mbs) {
      this.mbs = mbs;
   }

   public MultiblockSet(Multiblock mb) {
      this(mb.createRotations());
   }

   public Multiblock getForEntity(Entity e) {
      return this.getForRotation((double)e.rotationYaw);
   }

   public Multiblock getForRotation(double rotation) {
      int facing = MathHelper.floor(rotation * 4.0D / 360.0D + 0.5D) & 3;
      return this.getForIndex(facing);
   }

   public Multiblock getForIndex(int index) {
      return this.mbs[index];
   }
}
