package ivorius.reccomplex.utils.accessor;

import java.lang.reflect.Field;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
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

   public static void setEntityUniqueID(Entity entity, EaglercraftUUID uuid) {
      initializeUniqueID();

      try {
         uniqueID.set(entity, uuid);
      } catch (IllegalAccessException var3) {
         var3.printStackTrace();
      }

   }

   public static EaglercraftUUID getEntityUniqueID(Entity entity) {
      initializeUniqueID();

      try {
         return (EaglercraftUUID)uniqueID.get(entity);
      } catch (IllegalAccessException var2) {
         var2.printStackTrace();
         return null;
      }
   }
}
