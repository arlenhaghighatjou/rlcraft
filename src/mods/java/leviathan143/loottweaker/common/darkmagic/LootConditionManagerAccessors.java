package leviathan143.loottweaker.common.darkmagic;

import java.lang.invoke.MethodHandle;
import java.util.Map;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.conditions.LootCondition.Serializer;

public class LootConditionManagerAccessors extends AbstractAccessors {
   private static final MethodHandle LootConditionManager$CLASS_TO_SERIALIZER_MAPGetter;

   public LootConditionManagerAccessors() {
   }

   public static Map<Class<? extends LootCondition>, Serializer<?>> getClassToSerialiserMap() {
      try {
         return LootConditionManager$CLASS_TO_SERIALIZER_MAPGetter.invokeExact();
      } catch (Throwable var1) {
         throw new RuntimeException("Could not invoke LootFunctionManager class to serialiser map getter method handle", var1);
      }
   }

   static {
      try {
         LootConditionManager$CLASS_TO_SERIALIZER_MAPGetter = createFieldGetter(LootConditionManager.class, "CLASS_TO_SERIALIZER_MAP", "CLASS_TO_SERIALIZER_MAP");
      } catch (Throwable var1) {
         throw new RuntimeException("Failed to initialize LootFunctionManager accessor method handles", var1);
      }
   }
}
