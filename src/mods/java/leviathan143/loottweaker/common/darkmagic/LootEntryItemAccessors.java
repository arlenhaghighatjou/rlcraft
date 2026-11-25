package leviathan143.loottweaker.common.darkmagic;

import java.lang.invoke.MethodHandle;
import leviathan143.loottweaker.common.lib.LootFunctions;
import net.minecraft.item.Item;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.functions.LootFunction;

public class LootEntryItemAccessors extends AbstractAccessors {
   private static final MethodHandle LootEntryItem$itemGetter;
   private static final MethodHandle LootEntryItem$functionsGetter;

   public LootEntryItemAccessors() {
   }

   public static Item getItem(LootEntryItem entry) {
      try {
         return LootEntryItem$itemGetter.invokeExact(entry);
      } catch (Throwable var2) {
         throw new RuntimeException("Could not invoke LootEntryItem item getter method handle", var2);
      }
   }

   public static LootFunction[] getFunctions(LootEntryItem entry) {
      LootFunction[] functions = getFunctionsUnsafe(entry);
      return functions != null ? functions : LootFunctions.NONE;
   }

   public static LootFunction[] getFunctionsUnsafe(LootEntryItem entry) {
      try {
         return LootEntryItem$functionsGetter.invokeExact(entry);
      } catch (Throwable var2) {
         throw new RuntimeException("Could not invoke LootEntryItem functions getter method handle", var2);
      }
   }

   static {
      try {
         LootEntryItem$itemGetter = createFieldGetter(LootEntryItem.class, "item", "item");
         LootEntryItem$functionsGetter = createFieldGetter(LootEntryItem.class, "functions", "functions");
      } catch (Throwable var1) {
         throw new RuntimeException("Failed to initialize test LootEntryItem accessor method handles", var1);
      }
   }
}
