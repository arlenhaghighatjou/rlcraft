package crafttweaker.mc1120.block;

import crafttweaker.api.block.IMobilityFlag;
import java.util.Objects;
import net.minecraft.block.material.EnumPushReaction;

public class MCMobilityFlag implements IMobilityFlag {
   private final EnumPushReaction mobilityFlag;

   public MCMobilityFlag(EnumPushReaction mobilityFlag) {
      this.mobilityFlag = mobilityFlag;
   }

   public boolean matches(IMobilityFlag other) {
      return this.mobilityFlag.equals(other.getInternal());
   }

   public EnumPushReaction getInternal() {
      return this.mobilityFlag;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MCMobilityFlag that = (MCMobilityFlag)o;
         return this.mobilityFlag == that.mobilityFlag;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.mobilityFlag});
   }
}
