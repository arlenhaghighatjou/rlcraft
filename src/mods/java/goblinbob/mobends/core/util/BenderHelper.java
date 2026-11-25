package goblinbob.mobends.core.util;

import goblinbob.mobends.core.bender.EntityBender;
import goblinbob.mobends.core.bender.EntityBenderRegistry;
import goblinbob.mobends.core.data.LivingEntityData;
import goblinbob.mobends.core.mutators.Mutator;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;

public class BenderHelper {
   public BenderHelper() {
   }

   public static boolean isEntityAnimated(EntityLivingBase entity) {
      EntityBender<?> bender = EntityBenderRegistry.instance.getForEntity(entity);
      return bender != null && bender.isAnimated();
   }

   public static <T extends EntityLivingBase> Mutator<?, ?, ?> getMutatorForRenderer(Class<T> entityClass, RenderLivingBase<T> renderer) {
      EntityBender<?> bender = EntityBenderRegistry.instance.getForEntityClass(entityClass);
      return bender != null ? bender.getMutator(renderer) : null;
   }

   public static <D extends LivingEntityData<E>, E extends EntityLivingBase> D getData(E entity, RenderLivingBase<? extends EntityLivingBase> renderer) {
      EntityBender<EntityLivingBase> entityBender = EntityBenderRegistry.instance.<EntityLivingBase>getForEntity(entity);
      if (entityBender == null) {
         return (D)null;
      } else {
         Mutator<D, E, ?> mutator = entityBender.getMutator(renderer);
         return (D)(mutator == null ? null : mutator.getData(entity));
      }
   }
}
