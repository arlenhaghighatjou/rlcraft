package org.orecruncher.lib.compat;

import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import org.orecruncher.lib.ReflectedField;

public final class EntityUtil {
   private static final ReflectedField.IntegerField<Entity> nextStepDistance = new ReflectedField.IntegerField<Entity>(Entity.class, "nextStepDistance", "nextStepDistance", 0);

   public EntityUtil() {
   }

   public static int getNextStepDistance(@Nonnull Entity entity) {
      return nextStepDistance.get(entity);
   }

   public static void setNextStepDistance(@Nonnull Entity entity, int dist) {
      nextStepDistance.set(entity, dist);
   }

   @Nonnull
   public static String getClassName(@Nonnull Class<? extends Entity> entityClass) {
      ResourceLocation key = EntityList.getKey(entityClass);
      return key != null ? key.getResourcePath() : "EntityHasNoClass";
   }
}
