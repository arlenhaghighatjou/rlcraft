package leviathan143.loottweaker.common.darkmagic;

import java.lang.invoke.MethodHandle;
import java.util.List;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class LootPoolAccessors extends AbstractAccessors {
   private static final MethodHandle lootPool$poolConditionsGetter;
   private static final MethodHandle LootPool$lootEntriesGetter;

   public LootPoolAccessors() {
   }

   public static List<LootCondition> getConditions(LootPool pool) {
      try {
         return lootPool$poolConditionsGetter.invokeExact(pool);
      } catch (Throwable var2) {
         throw new RuntimeException("Could not invoke loot table pool conditions getter method handle", var2);
      }
   }

   public static List<LootEntry> getEntries(LootPool lootPool) {
      try {
         return LootPool$lootEntriesGetter.invokeExact(lootPool);
      } catch (Throwable var2) {
         throw new RuntimeException("Could not invoke LootPool lootEntries getter method handle", var2);
      }
   }

   static {
      try {
         lootPool$poolConditionsGetter = createFieldGetter(LootPool.class, "poolConditions", "poolConditions");
         LootPool$lootEntriesGetter = createFieldGetter(LootPool.class, "lootEntries", "lootEntries");
      } catch (Throwable var1) {
         throw new RuntimeException("Failed to initialize LootPool accessor method handles", var1);
      }
   }
}
