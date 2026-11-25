package ivorius.reccomplex.utils.accessor;

import java.lang.reflect.Field;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class RCAccessorEntity {
   private static Field uniqueID;

   public RCAccessorEntity() {
   }

   private static void initializeUniqueID() {
      if (uniqueID == null) {
         uniqueID = ReflectionHelper.findField(Entity.class, "entityUniqueID", "entityUniqueID");
      }

   }

   public static void setEntityUniqueID(Entity entity, UUID uuid) {
      initializeUniqueID();

      try {
         uniqueID.set(entity, uuid);
      } catch (IllegalAccessException var3) {
         var3.printStackTrace();
      }

   }

   public static UUID getEntityUniqueID(Entity entity) {
      initializeUniqueID();

      try {
         return (UUID)uniqueID.get(entity);
      } catch (IllegalAccessException var2) {
         var2.printStackTrace();
         return null;
      }
   }
}
