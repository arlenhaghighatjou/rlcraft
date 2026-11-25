package leviathan143.loottweaker.common.darkmagic;

import com.google.gson.Gson;
import java.lang.invoke.MethodHandle;
import net.minecraft.world.storage.loot.LootTableManager;

public class LootTableManagerAccessors extends AbstractAccessors {
   private static final MethodHandle lootTableManager$GSON_INSTANCEGetter;

   public LootTableManagerAccessors() {
   }

   public static Gson getGsonInstance() {
      try {
         return lootTableManager$GSON_INSTANCEGetter.invokeExact();
      } catch (Throwable var1) {
         throw new RuntimeException("Could not invoke loot table serialiser getter method handle", var1);
      }
   }

   static {
      try {
         lootTableManager$GSON_INSTANCEGetter = createFieldGetter(LootTableManager.class, "GSON_INSTANCE", "GSON_INSTANCE");
      } catch (Throwable var1) {
         throw new RuntimeException("Failed to initialize LootTableManager accessor method handles", var1);
      }
   }
}
