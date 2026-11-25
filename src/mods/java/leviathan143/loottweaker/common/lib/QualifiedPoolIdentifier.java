package leviathan143.loottweaker.common.lib;

import net.minecraft.util.ResourceLocation;

public class QualifiedPoolIdentifier {
   private final ResourceLocation tableId;
   private final String poolId;

   public QualifiedPoolIdentifier(ResourceLocation tableId, String poolId) {
      this.tableId = tableId;
      this.poolId = poolId;
   }

   public ResourceLocation getTableId() {
      return this.tableId;
   }

   public String getPoolId() {
      return this.poolId;
   }

   public String toString() {
      return String.format("pool '%s' of table '%s'", this.poolId, this.tableId);
   }
}
