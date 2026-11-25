package crafttweaker.mc1120.world;

import crafttweaker.api.block.IBlockState;
import crafttweaker.api.world.IBlockAccess;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IFacing;
import crafttweaker.mc1120.block.MCBlockState;
import java.util.Objects;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class MCBlockAccess implements IBlockAccess {
   private final net.minecraft.world.IBlockAccess access;

   public MCBlockAccess(net.minecraft.world.IBlockAccess access) {
      this.access = access;
   }

   public IBlockState getBlockState(IBlockPos pos) {
      return new MCBlockState(this.access.getBlockState((BlockPos)pos.getInternal()));
   }

   public boolean isAirBlock(IBlockPos pos) {
      return this.access.isAirBlock((BlockPos)pos.getInternal());
   }

   public int getStrongPower(IBlockPos pos, IFacing facing) {
      return this.access.getStrongPower((BlockPos)pos.getInternal(), (EnumFacing)facing.getInternal());
   }

   public Object getInternal() {
      return this.access;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MCBlockAccess that = (MCBlockAccess)o;
         return Objects.equals(this.access, that.access);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.access});
   }
}
