package net.ilexiconn.llibrary.server.capability;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.ilexiconn.llibrary.LLibrary;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;

public enum EntityDataHandler {
   INSTANCE;

   private final Map<Entity, List<IEntityData>> queuedEntityData = new IdentityHashMap();

   private EntityDataHandler() {
   }

   /** @deprecated */
   @Deprecated
   public <T extends Entity> void registerExtendedEntityData(T entity, IEntityData<T> entityData) {
      IEntityDataCapability dataCap = (IEntityDataCapability)entity.getCapability(LLibrary.ENTITY_DATA_CAPABILITY, (EnumFacing)null);
      if (dataCap == null) {
         List<IEntityData> data = (List)this.queuedEntityData.computeIfAbsent(entity, (e) -> {
            return (void)(new ArrayList());
         });
         data.add(entityData);
      } else {
         dataCap.registerData(entityData);
      }

   }

   @Nullable
   public <T extends Entity> IEntityData<T> getEntityData(T entity, String identifier) {
      IEntityDataCapability dataCap = (IEntityDataCapability)entity.getCapability(LLibrary.ENTITY_DATA_CAPABILITY, (EnumFacing)null);
      if (dataCap == null) {
         throw new IllegalStateException("Cannot get entity data on entity without data cap");
      } else {
         return dataCap.<T>getData(identifier);
      }
   }

   public <T extends Entity> List<IEntityData<T>> getEntityData(T entity) {
      IEntityDataCapability dataCap = (IEntityDataCapability)entity.getCapability(LLibrary.ENTITY_DATA_CAPABILITY, (EnumFacing)null);
      if (dataCap == null) {
         throw new IllegalStateException("Cannot get entity data on entity without data cap");
      } else {
         return dataCap.getData();
      }
   }

   /** @deprecated */
   @Deprecated
   public void putQueuedData(Entity entity, IEntityDataCapability dataCap) {
      List<IEntityData> queuedData = (List)this.queuedEntityData.remove(entity);
      if (queuedData != null) {
         queuedData.forEach(dataCap::registerData);
      }

   }

   /** @deprecated */
   @Deprecated
   public void releaseQueuedData(Entity entity) {
      this.queuedEntityData.remove(entity);
   }
}
