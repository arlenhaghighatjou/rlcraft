package goblinbob.mobends.core.bender;

import goblinbob.mobends.core.Core;
import goblinbob.mobends.core.configuration.CoreClientConfig;
import goblinbob.mobends.standard.main.ModConfig;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.entity.EntityLivingBase;

public class EntityBenderRegistry {
   public static final EntityBenderRegistry instance = new EntityBenderRegistry();
   private final Map<Class<? extends EntityLivingBase>, EntityBender<?>> entityClassToBenderMap = new HashMap();
   private final Map<EntityLivingBase, EntityBender<?>> entityToBenderMap = new HashMap();

   public EntityBenderRegistry() {
   }

   public void registerBender(EntityBender<?> entityBender) {
      Core.LOG.info(String.format("Registering %s", entityBender.getKey()));
      this.entityClassToBenderMap.put(entityBender.entityClass, entityBender);
   }

   public void applyConfiguration(CoreClientConfig config) {
      for(EntityBender<?> entityBender : this.entityClassToBenderMap.values()) {
         entityBender.setAnimate(config.isEntityAnimated(entityBender.getKey()));
      }

   }

   public Collection<EntityBender<?>> getRegistered() {
      return this.entityClassToBenderMap.values();
   }

   public Collection<EntityBender<?>> getRegistered(EntityBenderRegistry.Filter filter) {
      List<EntityBender<?>> benderList = new ArrayList(this.entityClassToBenderMap.values());
      if (filter.query != null) {
         benderList.removeIf((bender) -> {
            return !bender.getUnlocalizedName().toLowerCase().contains(filter.query.toLowerCase());
         });
      }

      benderList.sort(Comparator.comparing(EntityBender::getKey));
      return benderList;
   }

   public <E extends EntityLivingBase> EntityBender<E> getForEntityClass(Class<E> c) {
      return (EntityBender)this.entityClassToBenderMap.get(c);
   }

   public <E extends EntityLivingBase> EntityBender<E> getForEntity(E entity) {
      return (EntityBender)this.entityToBenderMap.computeIfAbsent(entity, (key) -> {
         if (ModConfig.shouldKeepEntityAsVanilla(entity)) {
            return null;
         } else {
            Class<? extends EntityLivingBase> entityClass = entity.getClass();

            for(EntityBender<?> entityBender : this.entityClassToBenderMap.values()) {
               if (entityBender.entityClass.equals(entityClass)) {
                  return entityBender;
               }
            }

            for(EntityBender<?> entityBender : this.entityClassToBenderMap.values()) {
               if (entityBender.entityClass.isInstance(entity)) {
                  return entityBender;
               }
            }

            return null;
         }
      });
   }

   public <E extends EntityLivingBase> void clearCache(E entity) {
      this.entityToBenderMap.remove(entity);
   }

   public void clearCache() {
      this.entityToBenderMap.clear();
   }

   public void refreshMutators() {
      this.clearCache();

      for(EntityBender<?> entityBender : this.entityClassToBenderMap.values()) {
         entityBender.refreshMutation();
      }

   }

   public static class Filter {
      public boolean ascending = false;
      public EntityBenderRegistry.Filter.SortingKey sortingKey = EntityBenderRegistry.Filter.SortingKey.NAME;
      public String query = null;

      public Filter() {
      }

      public static enum SortingKey {
         NAME;

         private SortingKey() {
         }
      }
   }
}
