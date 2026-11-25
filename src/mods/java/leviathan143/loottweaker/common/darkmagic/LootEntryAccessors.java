package leviathan143.loottweaker.common.darkmagic;

import java.lang.invoke.MethodHandle;
import leviathan143.loottweaker.common.lib.LootConditions;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class LootEntryAccessors extends AbstractAccessors {
   private static final MethodHandle LootEntry$weightGetter;
   private static final MethodHandle LootEntry$weightSetter;
   private static final MethodHandle LootEntry$qualityGetter;
   private static final MethodHandle LootEntry$qualitySetter;
   private static final MethodHandle LootEntry$conditionsGetter;
   private static final MethodHandle LootEntry$conditionsSetter;
   private static final MethodHandle LootEntry$nameSetter;

   public LootEntryAccessors() {
   }

   public static int getWeight(LootEntry entry) {
      try {
         return LootEntry$weightGetter.invokeExact(entry);
      } catch (Throwable var2) {
         throw new RuntimeException("Could not invoke LootEntry weight getter method handle", var2);
      }
   }

   public static void setWeight(LootEntry entry, int weight) {
      try {
         LootEntry$weightSetter.invokeExact(entry, weight);
      } catch (Throwable var3) {
         throw new RuntimeException("Could not invoke LootEntry weight setter method handle", var3);
      }
   }

   public static int getQuality(LootEntry entry) {
      try {
         return LootEntry$qualityGetter.invokeExact(entry);
      } catch (Throwable var2) {
         throw new RuntimeException("Could not invoke LootEntry quality getter method handle", var2);
      }
   }

   public static void setQuality(LootEntry entry, int quality) {
      try {
         LootEntry$qualitySetter.invokeExact(entry, quality);
      } catch (Throwable var3) {
         throw new RuntimeException("Could not invoke LootEntry quality setter method handle", var3);
      }
   }

   public static LootCondition[] getConditions(LootEntry entry) {
      LootCondition[] conditions = getConditionsUnsafe(entry);
      return conditions != null ? conditions : LootConditions.NONE;
   }

   public static LootCondition[] getConditionsUnsafe(LootEntry entry) {
      try {
         return LootEntry$conditionsGetter.invokeExact(entry);
      } catch (Throwable var2) {
         throw new RuntimeException("Could not invoke LootEntry conditions getter method handle", var2);
      }
   }

   public static void setConditions(LootEntry entry, LootCondition[] conditions) {
      try {
         LootEntry$conditionsSetter.invokeExact(entry, conditions);
      } catch (Throwable var3) {
         throw new RuntimeException("Could not invoke LootEntry conditions setter method handle", var3);
      }
   }

   public static void setName(LootEntry entry, String name) {
      try {
         LootEntry$nameSetter.invokeExact(entry, name);
      } catch (Throwable var3) {
         throw new RuntimeException("Could not invoke LootEntry name setter method handle", var3);
      }
   }

   static {
      try {
         LootEntry$weightGetter = createFieldGetter(LootEntry.class, "weight", "weight");
         LootEntry$weightSetter = createFieldSetter(LootEntry.class, "weight", "weight");
         LootEntry$qualityGetter = createFieldGetter(LootEntry.class, "quality", "quality");
         LootEntry$qualitySetter = createFieldSetter(LootEntry.class, "quality", "quality");
         LootEntry$conditionsGetter = createFieldGetter(LootEntry.class, "conditions", "conditions");
         LootEntry$conditionsSetter = createFieldSetter(LootEntry.class, "conditions", "conditions");
         LootEntry$nameSetter = createFieldSetter(LootEntry.class, "entryName");
      } catch (Throwable var1) {
         throw new RuntimeException("Failed to initialize test LootEntry accessor method handles", var1);
      }
   }
}
