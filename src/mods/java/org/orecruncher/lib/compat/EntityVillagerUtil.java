package org.orecruncher.lib.compat;

import javax.annotation.Nonnull;
import net.minecraft.entity.passive.EntityVillager;
import org.orecruncher.lib.ReflectedField;

public class EntityVillagerUtil {
   private static final ReflectedField.IntegerField<EntityVillager> careerId = new ReflectedField.IntegerField<EntityVillager>(EntityVillager.class, "careerId", "careerId", 1);

   public EntityVillagerUtil() {
   }

   public static int getCareerId(@Nonnull EntityVillager entity) {
      return careerId.get(entity);
   }
}
