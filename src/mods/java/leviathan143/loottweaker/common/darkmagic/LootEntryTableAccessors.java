package leviathan143.loottweaker.common.darkmagic;

import java.lang.invoke.MethodHandle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntryTable;

public class LootEntryTableAccessors extends AbstractAccessors {
   private static final MethodHandle LootEntryTable$tableGetter;

   public LootEntryTableAccessors() {
   }

   public static ResourceLocation getTable(LootEntryTable entry) {
      try {
         return LootEntryTable$tableGetter.invokeExact(entry);
      } catch (Throwable var2) {
         throw new RuntimeException("Could not invoke LootEntryTable table getter method handle", var2);
      }
   }

   static {
      try {
         LootEntryTable$tableGetter = createFieldGetter(LootEntryTable.class, "table", "table");
      } catch (Throwable var1) {
         throw new RuntimeException("Failed to initialize test LootEntryTable accessor method handles", var1);
      }
   }
}
