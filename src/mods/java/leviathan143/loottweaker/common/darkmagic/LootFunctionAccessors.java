package leviathan143.loottweaker.common.darkmagic;

import java.lang.invoke.MethodHandle;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

public class LootFunctionAccessors extends AbstractAccessors {
   private static final MethodHandle LootFunction$conditionsSetter;

   public LootFunctionAccessors() {
   }

   public static void setConditions(LootFunction function, LootCondition[] conditions) {
      try {
         LootFunction$conditionsSetter.invokeExact(function, conditions);
      } catch (Throwable var3) {
         throw new RuntimeException("Could not invoke LootFunction conditions getter method handle", var3);
      }
   }

   static {
      try {
         LootFunction$conditionsSetter = createFieldSetter(LootFunction.class, "conditions", "conditions");
      } catch (Throwable var1) {
         throw new RuntimeException("Failed to initialize LootFunction accessor method handles", var1);
      }
   }
}
