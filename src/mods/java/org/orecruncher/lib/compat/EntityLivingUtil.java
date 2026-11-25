package org.orecruncher.lib.compat;

import javax.annotation.Nonnull;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import org.orecruncher.lib.ReflectedField;

public final class EntityLivingUtil {
   private static final ReflectedField.ObjectField<EntityLiving, EntityLivingBase> attackTarget = new ReflectedField.ObjectField<EntityLiving, EntityLivingBase>(EntityLiving.class, "attackTarget", "attackTarget");

   public EntityLivingUtil() {
   }

   public static EntityLivingBase getAttackTarget(@Nonnull EntityLiving entity) {
      return attackTarget.get(entity);
   }
}
