package leviathan143.loottweaker.common.darkmagic;

import java.lang.invoke.MethodHandle;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

public class EntityLivingAccessors extends AbstractAccessors {
   private static final MethodHandle entityLiving$getLootTable;

   public EntityLivingAccessors() {
   }

   public static ResourceLocation getLootTable(EntityLiving living) {
      try {
         return entityLiving$getLootTable.invokeExact(living);
      } catch (Throwable var2) {
         throw new RuntimeException("Could not invoke entity loot table getter method handle", var2);
      }
   }

   static {
      try {
         entityLiving$getLootTable = createMethodInvoker(EntityLiving.class, "getLootTable", "getLootTable", new Class[0]);
      } catch (Throwable var1) {
         throw new RuntimeException("Failed to initialize EntityLiving accessor method handles", var1);
      }
   }
}
