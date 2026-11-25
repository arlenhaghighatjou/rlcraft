package org.orecruncher.lib.compat;

import javax.annotation.Nonnull;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.datasync.DataParameter;
import org.orecruncher.lib.ReflectedField;

public final class EntityLivingBaseUtil {
   private static final ReflectedField.ObjectField<EntityLivingBase, DataParameter<Boolean>> hideParticles = new ReflectedField.ObjectField<EntityLivingBase, DataParameter<Boolean>>(EntityLivingBase.class, "HIDE_PARTICLES", "HIDE_PARTICLES");
   private static final ReflectedField.BooleanField<EntityLivingBase> isJumping = new ReflectedField.BooleanField<EntityLivingBase>(EntityLivingBase.class, "isJumping", "isJumping");

   public EntityLivingBaseUtil() {
   }

   public static DataParameter<Boolean> getHideParticles() {
      return (DataParameter)hideParticles.get((Object)null);
   }

   public static boolean isJumping(@Nonnull EntityLivingBase entity) {
      return isJumping.get(entity);
   }
}
