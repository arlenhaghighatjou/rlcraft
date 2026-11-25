package net.ilexiconn.llibrary.server;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.server.capability.EntityDataCapabilityImplementation;
import net.ilexiconn.llibrary.server.capability.EntityDataHandler;
import net.ilexiconn.llibrary.server.capability.IEntityData;
import net.ilexiconn.llibrary.server.capability.IEntityDataCapability;
import net.ilexiconn.llibrary.server.entity.EntityProperties;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.ilexiconn.llibrary.server.entity.PropertiesTracker;
import net.ilexiconn.llibrary.server.event.CollectEntityDataEvent;
import net.ilexiconn.llibrary.server.network.PropertiesMessage;
import net.ilexiconn.llibrary.server.world.WorldDataHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.event.entity.player.PlayerEvent.StopTracking;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public enum ServerEventHandler {
   INSTANCE;

   private static final ResourceLocation EXTENDED_DATA_ID = new ResourceLocation("llibrary", "ExtendedEntityDataCapability");
   private int updateTimer;

   private ServerEventHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
      List<IEntityData> collectedData = new ArrayList();
      MinecraftForge.EVENT_BUS.post(new CollectEntityDataEvent((Entity)event.getObject(), collectedData));
      event.addCapability(EXTENDED_DATA_ID, new EntityDataCapabilityImplementation(collectedData));
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onFinishAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
      ICapabilityProvider extendedData = (ICapabilityProvider)event.getCapabilities().get(EXTENDED_DATA_ID);
      if (extendedData instanceof IEntityDataCapability) {
         EntityDataHandler.INSTANCE.putQueuedData((Entity)event.getObject(), (IEntityDataCapability)extendedData);
      } else {
         EntityDataHandler.INSTANCE.releaseQueuedData((Entity)event.getObject());
      }

   }

   @SubscribeEvent
   public void onCollectEntityData(CollectEntityDataEvent event) {
      Entity entity = event.getEntity();
      boolean cached = EntityPropertiesHandler.INSTANCE.hasEntityInCache(entity.getClass());
      List<String> entityPropertiesIDCache = !cached ? new ArrayList() : null;
      EntityPropertiesHandler.INSTANCE.getRegisteredProperties().filter((propEntry) -> {
         return ((Class)propEntry.getKey()).isAssignableFrom(entity.getClass());
      }).forEach((propEntry) -> {
         for(Class<? extends EntityProperties> propClass : (List)propEntry.getValue()) {
            try {
               Constructor<? extends EntityProperties> constructor = propClass.getConstructor();
               EntityProperties prop = (EntityProperties)constructor.newInstance();
               String propID = prop.getID();
               event.registerData(prop);
               if (!cached) {
                  entityPropertiesIDCache.add(propID);
               }
            } catch (Exception var9) {
               var9.printStackTrace();
            }
         }

      });
      if (!cached) {
         EntityPropertiesHandler.INSTANCE.addEntityToCache(entity.getClass(), entityPropertiesIDCache);
      }

   }

   @SubscribeEvent
   public void onPlayerClone(Clone event) {
      NBTTagCompound compound = new NBTTagCompound();
      IEntityDataCapability originalCap = (IEntityDataCapability)event.getOriginal().getCapability(LLibrary.ENTITY_DATA_CAPABILITY, (EnumFacing)null);
      IEntityDataCapability newCap = (IEntityDataCapability)event.getEntityPlayer().getCapability(LLibrary.ENTITY_DATA_CAPABILITY, (EnumFacing)null);
      if (originalCap != null && newCap != null) {
         originalCap.saveToNBT(compound);
         newCap.loadFromNBT(compound);
      }

   }

   @SubscribeEvent
   public void onEntityUpdate(LivingUpdateEvent event) {
      if (!event.getEntity().world.isRemote && event.getEntity() instanceof EntityPlayerMP) {
         EntityPlayerMP player = (EntityPlayerMP)event.getEntity();
         List<PropertiesTracker<?>> trackers = EntityPropertiesHandler.INSTANCE.getEntityTrackers(player);
         if (trackers != null && trackers.size() > 0) {
            boolean hasPlayer = false;

            for(PropertiesTracker<?> tracker : trackers) {
               if (hasPlayer = tracker.getEntity() == player) {
                  break;
               }
            }

            if (!hasPlayer) {
               EntityPropertiesHandler.INSTANCE.addTracker(player, player);
            }

            for(PropertiesTracker<?> tracker : trackers) {
               tracker.updateTracker();
               if (tracker.isTrackerReady()) {
                  tracker.onSync();
                  PropertiesMessage message = new PropertiesMessage(tracker, tracker.getEntity());
                  LLibrary.NETWORK_WRAPPER.sendTo(message, player);
                  tracker.reset();
               }
            }
         }

      }
   }

   @SubscribeEvent
   public void onJoinWorld(EntityJoinWorldEvent event) {
      if (!event.getWorld().isRemote && event.getEntity() instanceof EntityPlayerMP) {
         EntityPlayerMP player = (EntityPlayerMP)event.getEntity();
         EntityPropertiesHandler.INSTANCE.addTracker(player, player);
      }

   }

   @SubscribeEvent
   public void onEntityStartTracking(StartTracking event) {
      if (event.getEntityPlayer() instanceof EntityPlayerMP) {
         EntityPropertiesHandler.INSTANCE.addTracker((EntityPlayerMP)event.getEntityPlayer(), event.getTarget());
      }

   }

   @SubscribeEvent
   public void onEntityStopTracking(StopTracking event) {
      if (event.getEntityPlayer() instanceof EntityPlayerMP) {
         EntityPropertiesHandler.INSTANCE.removeTracker((EntityPlayerMP)event.getEntityPlayer(), event.getTarget());
      }

   }

   @SubscribeEvent
   public void onServerTickEvent(ServerTickEvent event) {
      if (event.phase == Phase.END) {
         ++this.updateTimer;
         if (this.updateTimer > 20) {
            this.updateTimer = 0;
            Iterator<Entry<EntityPlayerMP, List<PropertiesTracker<?>>>> iterator = EntityPropertiesHandler.INSTANCE.getTrackerIterator();

            while(iterator.hasNext()) {
               Entry<EntityPlayerMP, List<PropertiesTracker<?>>> trackerEntry = (Entry)iterator.next();
               EntityPlayerMP player = (EntityPlayerMP)trackerEntry.getKey();
               WorldServer playerWorld = DimensionManager.getWorld(player.dimension);
               if (player != null && !player.isDead && playerWorld != null && playerWorld.loadedEntityList.contains(player)) {
                  Iterator<PropertiesTracker<?>> it = ((List)trackerEntry.getValue()).iterator();

                  while(it.hasNext()) {
                     PropertiesTracker tracker = (PropertiesTracker)it.next();
                     Entity entity = tracker.getEntity();
                     WorldServer entityWorld = DimensionManager.getWorld(entity.dimension);
                     if (entity == null || entity.isDead || entityWorld == null || !entityWorld.loadedEntityList.contains(entity)) {
                        it.remove();
                        tracker.removeTracker();
                     }
                  }
               } else {
                  ((List)trackerEntry.getValue()).forEach(PropertiesTracker::removeTracker);
                  iterator.remove();
               }
            }
         }
      }

   }

   @SubscribeEvent
   public void onWorldLoad(Load event) {
      if (!event.getWorld().isRemote) {
         WorldDataHandler.INSTANCE.loadWorldData(event.getWorld().getSaveHandler(), event.getWorld());
      }

   }

   @SubscribeEvent
   public void onWorldSave(Save event) {
      if (!event.getWorld().isRemote) {
         WorldDataHandler.INSTANCE.saveWorldData(event.getWorld().getSaveHandler(), event.getWorld());
      }

   }
}
