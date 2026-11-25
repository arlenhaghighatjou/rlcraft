package leviathan143.loottweaker.common.darkmagic;

import java.lang.invoke.MethodHandle;
import java.util.Map;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraft.world.storage.loot.functions.LootFunction.Serializer;

public class LootFunctionManagerAccessors extends AbstractAccessors {
   private static final MethodHandle LootFunctionManager$CLASS_TO_SERIALIZER_MAPGetter;

   public LootFunctionManagerAccessors() {
   }

   public static Map<Class<? extends LootFunction>, Serializer<?>> getClassToSerialiserMap() {
      try {
         return LootFunctionManager$CLASS_TO_SERIALIZER_MAPGetter.invokeExact();
      } catch (Throwable var1) {
         throw new RuntimeException("Could not invoke LootFunctionManager class to serialiser map getter method handle", var1);
      }
   }

   static {
      try {
         LootFunctionManager$CLASS_TO_SERIALIZER_MAPGetter = createFieldGetter(LootFunctionManager.class, "CLASS_TO_SERIALIZER_MAP", "CLASS_TO_SERIALIZER_MAP");
      } catch (Throwable var1) {
         throw new RuntimeException("Failed to initialize LootFunctionManager accessor method handles", var1);
      }
   }
}
