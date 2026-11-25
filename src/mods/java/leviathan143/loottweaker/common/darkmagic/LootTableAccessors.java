package leviathan143.loottweaker.common.darkmagic;

import java.lang.invoke.MethodHandle;
import java.util.List;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;

public class LootTableAccessors extends AbstractAccessors {
   private static final MethodHandle lootTable$poolsGetter;

   public LootTableAccessors() {
   }

   public static List<LootPool> getPools(LootTable table) {
      try {
         return lootTable$poolsGetter.invokeExact(table);
      } catch (Throwable var2) {
         throw new RuntimeException("Could not invoke loot table pool getter method handle", var2);
      }
   }

   static {
      try {
         lootTable$poolsGetter = createFieldGetter(LootTable.class, "pools", "pools");
      } catch (Throwable var1) {
         throw new RuntimeException("Failed to initialize LootTable accessor method handles", var1);
      }
   }
}
