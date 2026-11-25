package crafttweaker.mc1120.world;

import crafttweaker.api.util.Position3f;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IFacing;
import crafttweaker.mc1120.util.MCPosition3f;
import java.util.Objects;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class MCBlockPos implements IBlockPos {
   private final BlockPos blockPos;

   public MCBlockPos(BlockPos blockPos) {
      this.blockPos = blockPos;
   }

   public MCBlockPos(int x, int y, int z) {
      this.blockPos = new BlockPos(x, y, z);
   }

   public int getX() {
      return this.blockPos.getX();
   }

   public int getY() {
      return this.blockPos.getY();
   }

   public int getZ() {
      return this.blockPos.getZ();
   }

   public IBlockPos getOffset(IFacing direction, int offset) {
      return new MCBlockPos(this.blockPos.offset((EnumFacing)direction.getInternal(), offset));
   }

   public Position3f asPosition3f() {
      return new MCPosition3f((float)this.getX(), (float)this.getY(), (float)this.getZ());
   }

   public Object getInternal() {
      return this.blockPos;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MCBlockPos that = (MCBlockPos)o;
         return Objects.equals(this.blockPos, that.blockPos);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.blockPos});
   }
}
