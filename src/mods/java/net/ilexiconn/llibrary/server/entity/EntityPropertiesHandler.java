package net.ilexiconn.llibrary.server.entity;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Stream;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.server.capability.EntityDataHandler;
import net.ilexiconn.llibrary.server.capability.IEntityData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public enum EntityPropertiesHandler {
   INSTANCE;

   private Map<Class<? extends EntityProperties>, String> propertiesIDMap = new HashMap();
   private Map<Class<? extends Entity>, List<Class<? extends EntityProperties<?>>>> registeredProperties = new HashMap();
   private Map<Class<? extends Entity>, List<String>> entityPropertiesCache = new HashMap();
   private Map<EntityPlayerMP, List<PropertiesTracker<?>>> trackerMap = new IdentityHashMap();

   private EntityPropertiesHandler() {
   }

   public <E extends Entity, T extends EntityProperties<E>> void registerProperties(Class<T> propertiesClass) {
      T properties;
      try {
         Constructor<T> constructor = propertiesClass.getConstructor();
         properties = constructor.newInstance();
      } catch (Exception var5) {
         LLibrary.LOGGER.fatal("Failed to register entity properties", var5);
         return;
      }

      if (this.propertiesIDMap.containsValue(properties.getID())) {
         LLibrary.LOGGER.fatal("Duplicate entity properties with ID {}", properties.getID());
      } else {
         this.propertiesIDMap.put(propertiesClass, properties.getID());
         Class<E> entityClass = properties.getEntityClass();
         List<Class<? extends EntityProperties<?>>> list = (List)this.registeredProperties.computeIfAbsent(entityClass, (k) -> {
            return (void)(new ArrayList());
         });
         list.add(propertiesClass);
      }
   }

   public <T extends EntityProperties<?>> T getProperties(Entity entity, Class<T> propertiesClass) {
      if (entity != null) {
         String identifier = (String)this.propertiesIDMap.get(propertiesClass);
         if (identifier != null) {
            return (T)(EntityDataHandler.INSTANCE.getEntityData(entity, identifier));
         }
      }

      return (T)null;
   }

   public <T extends Entity> void addTracker(EntityPlayerMP player, T entity) {
      List<String> entityProperties = (List)this.entityPropertiesCache.get(entity.getClass());
      if (entityProperties != null) {
         List<PropertiesTracker<?>> trackerList = this.getEntityTrackers(player);

         for(String propID : entityProperties) {
            IEntityData extendedProperties = EntityDataHandler.INSTANCE.getEntityData(entity, propID);
            if (extendedProperties instanceof EntityProperties) {
               EntityProperties properties = (EntityProperties)extendedProperties;
               if (properties.getTrackingTime() >= 0) {
                  PropertiesTracker<T> tracker = properties.createTracker(entity);
                  tracker.setReady();
                  trackerList.add(tracker);
               }
            }
         }
      }

   }

   public void removeTracker(EntityPlayerMP player, Entity entity) {
      List<PropertiesTracker<?>> trackerList = this.getEntityTrackers(player);
      if (trackerList != null && !trackerList.isEmpty()) {
         Iterator<PropertiesTracker<?>> iterator = trackerList.iterator();

         while(iterator.hasNext()) {
            PropertiesTracker<?> tracker = (PropertiesTracker)iterator.next();
            if (tracker.getEntity().equals(entity)) {
               iterator.remove();
               tracker.removeTracker();
            }
         }
      }

   }

   public boolean hasEntityInCache(Class<? extends Entity> entityClass) {
      return this.entityPropertiesCache.containsKey(entityClass);
   }

   public void addEntityToCache(Class<? extends Entity> entityClass, List<String> propertyIDs) {
      this.entityPropertiesCache.put(entityClass, propertyIDs);
   }

   public Stream<Entry<Class<? extends Entity>, List<Class<? extends EntityProperties<?>>>>> getRegisteredProperties() {
      return this.registeredProperties.entrySet().stream();
   }

   public List<PropertiesTracker<?>> getEntityTrackers(EntityPlayerMP player) {
      return (List)this.trackerMap.computeIfAbsent(player, (p) -> {
         return new ArrayList();
      });
   }

   public Iterator<Entry<EntityPlayerMP, List<PropertiesTracker<?>>>> getTrackerIterator() {
      return this.trackerMap.entrySet().iterator();
   }

   @SubscribeEvent
   public void onPlayerLogOut(PlayerLoggedOutEvent event) {
      if (event.player instanceof EntityPlayerMP) {
         this.trackerMap.remove(event.player);
      }

   }

   @SubscribeEvent
   public void onPlayerClone(Clone event) {
      EntityPlayer player = event.getOriginal();
      if (player instanceof EntityPlayerMP) {
         this.trackerMap.remove(player);
      }

   }
}
