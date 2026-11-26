package tschipp.callablehorses.common.events;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.event.entity.player.PlayerEvent.StopTracking;
import net.minecraftforge.event.world.ChunkEvent.Load;
import net.minecraftforge.event.world.ChunkEvent.Unload;
import tschipp.callablehorses.CallableHorses;
import tschipp.callablehorses.common.HorseManager;
import tschipp.callablehorses.common.capabilities.horseowner.HorseOwnerProvider;
import tschipp.callablehorses.common.capabilities.horseowner.IHorseOwner;
import tschipp.callablehorses.common.capabilities.storedhorse.HorseProvider;
import tschipp.callablehorses.common.capabilities.storedhorse.IStoredHorse;
import tschipp.callablehorses.common.config.CallableHorsesConfig;
import tschipp.callablehorses.common.helper.HorseHelper;
import tschipp.callablehorses.common.worlddata.StoredHorsesWorldData;

public class EntityEvents {
   public EntityEvents() {
   }

   public static void register(EventBus bus) {
      bus.registerGeneric(AttachCapabilitiesEvent.class, Entity.class, EntityEvents::onAttachCaps);
      bus.register(Load.class, EntityEvents::onChunkLoad);
      bus.register(Clone.class, EntityEvents::onClone);
      bus.register(Unload.class, EntityEvents::onChunkUnload);
      bus.register(StopTracking.class, EntityEvents::onStopTracking);
      bus.register(StartTracking.class, EntityEvents::onStartTracking);
      bus.register(LivingUpdateEvent.class, EntityEvents::onLivingUpdate);
      bus.register(LivingDeathEvent.class, EntityEvents::onLivingDeath);
      bus.register(EntityJoinWorldEvent.class, EntityEvents::onJoinWorld);
      bus.register(LivingDropsEvent.class, EntityEvents::onEntityDrop);
   }

   public static void onAttachCaps(AttachCapabilitiesEvent<Entity> event) {
      if (event.getObject() instanceof EntityPlayer) {
         event.addCapability(new ResourceLocation("callablehorses", "horse_owner"), new HorseOwnerProvider());
      }

      if (event.getObject() instanceof AbstractHorse) {
         event.addCapability(new ResourceLocation("callablehorses", "stored_horse"), new HorseProvider());
      }

   }

   public static void onChunkLoad(Load event) {
      World world = event.getWorld();
      if (!world.isRemote) {
         Chunk chk = event.getChunk();
         ClassInheritanceMultiMap<Entity>[] entitylists = chk.getEntityLists();
         ClassInheritanceMultiMap<Entity>[] var4 = entitylists;
         int var5 = entitylists.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            for(Entity e : var4[var6]) {
               if (e instanceof AbstractHorse) {
                  IStoredHorse horse = HorseHelper.getHorseCap(e);
                  if (horse.isOwned()) {
                     StoredHorsesWorldData data = HorseHelper.getWorldData(world);
                     if (data.isDisbanded(horse.getStorageUUID())) {
                        HorseManager.clearHorse(horse);
                        data.clearDisbanded(horse.getStorageUUID());
                     } else {
                        int globalNum = HorseHelper.getHorseNum(e.world, horse.getStorageUUID());
                        if (globalNum > horse.getHorseNum()) {
                           e.posY = -200.0D;
                           e.setDead();
                           CallableHorses.LOGGER.debug(e + " was instantly despawned because its number is " + horse.getHorseNum() + " and the global num is " + globalNum);
                        }
                     }
                  }
               }
            }
         }
      }

   }

   public static void onClone(Clone event) {
      EntityPlayer original = event.getOriginal();
      EntityPlayer newPlayer = event.getEntityPlayer();
      IHorseOwner oldHorse = HorseHelper.getOwnerCap(original);
      IHorseOwner newHorse = HorseHelper.getOwnerCap(newPlayer);
      newHorse.setHorseNBT(oldHorse.getHorseNBT());
      newHorse.setHorseNum(oldHorse.getHorseNum());
      newHorse.setStorageUUID(oldHorse.getStorageUUID());
   }

   public static void onChunkUnload(Unload event) {
      World world = event.getWorld();
      if (!world.isRemote) {
         Chunk chk = event.getChunk();
         ClassInheritanceMultiMap<Entity>[] entitylists = chk.getEntityLists();
         ClassInheritanceMultiMap<Entity>[] var4 = entitylists;
         int var5 = entitylists.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            for(Entity e : var4[var6]) {
               HorseManager.saveHorse(e);
            }
         }
      }

   }

   public static void onStopTracking(StopTracking event) {
      EntityPlayer player = event.getEntityPlayer();
      World world = player.world;
      Entity e = event.getTarget();
      if (!world.isRemote) {
         HorseManager.saveHorse(e);
      }

   }

   public static void onStartTracking(StartTracking event) {
      EntityPlayer player = event.getEntityPlayer();
      if (!player.world.isRemote) {
         Entity target = event.getTarget();
         if (target instanceof AbstractHorse) {
            HorseHelper.sendHorseUpdateToClient(target, player);
         }
      }

   }

   public static void onLivingUpdate(LivingUpdateEvent event) {
      if (CallableHorsesConfig.settings.enableDebug || CallableHorsesConfig.settings.continuousAntiDupeChecking) {
         Entity e = event.getEntityLiving();
         if (e instanceof AbstractHorse) {
            IStoredHorse horse = HorseHelper.getHorseCap(e);
            if (CallableHorsesConfig.settings.enableDebug) {
               e.setCustomNameTag("Is Owned: " + horse.isOwned() + ", Storage UUID: " + horse.getStorageUUID() + ", Horse Number: " + horse.getHorseNum() + ", Horse UUID: " + e.getUniqueID());
            }

            if (CallableHorsesConfig.settings.continuousAntiDupeChecking) {
               int thisNum = horse.getHorseNum();
               int globalNum = HorseHelper.getHorseNum(e.world, horse.getStorageUUID());
               if (globalNum > thisNum) {
                  e.posY = -200.0D;
                  e.setDead();
               }
            }
         }
      }

   }

   public static void onLivingDeath(LivingDeathEvent event) {
      Entity e = event.getEntity();
      if (!e.world.isRemote && e instanceof AbstractHorse) {
         IStoredHorse horse = HorseHelper.getHorseCap(e);
         if (horse.isOwned()) {
            EntityPlayer owner = HorseHelper.getPlayerFromUUID(horse.getOwnerUUID(), e.world);
            if (owner != null) {
               IHorseOwner horseOwner = HorseHelper.getOwnerCap(owner);
               if (CallableHorsesConfig.settings.deathIsPermanent) {
                  horseOwner.clearHorse();
                  owner.sendMessage(new TextComponentString(TextFormatting.RED + I18n.translateToLocal("callablehorses.alert.death")));
               } else {
                  AbstractHorse deadHorse = horseOwner.getHorseEntity(owner.world);
                  HorseManager.prepDeadHorseForRespawning(deadHorse);
                  horseOwner.setHorseNBT(deadHorse.serializeNBT());
                  horseOwner.setLastSeenPosition(BlockPos.ORIGIN);
               }
            } else {
               CallableHorses.LOGGER.debug(e + " was marked as killed.");
               HorseHelper.getWorldData(e.world).markKilled(horse.getStorageUUID());
            }
         }
      }

   }

   public static void onJoinWorld(EntityJoinWorldEvent event) {
      Entity joiningEntity = event.getEntity();
      World world = event.getWorld();
      if (!world.isRemote && joiningEntity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)joiningEntity;
         IHorseOwner owner = HorseHelper.getOwnerCap(player);
         String ownedHorse = owner.getStorageUUID();
         if (!ownedHorse.isEmpty()) {
            StoredHorsesWorldData data = HorseHelper.getWorldData(world);
            if (data.wasKilled(ownedHorse)) {
               data.clearKilled(ownedHorse);
               if (CallableHorsesConfig.settings.deathIsPermanent) {
                  owner.clearHorse();
                  player.sendMessage(new TextComponentString(TextFormatting.RED + I18n.translateToLocal("callablehorses.alert.offlinedeath")));
               } else {
                  AbstractHorse deadHorse = owner.getHorseEntity(world);
                  HorseManager.prepDeadHorseForRespawning(deadHorse);
                  owner.setHorseNBT(deadHorse.serializeNBT());
                  owner.setLastSeenPosition(BlockPos.ORIGIN);
               }
            }

            if (data.wasOfflineSaved(ownedHorse)) {
               NBTTagCompound newNBT = data.getOfflineSavedHorse(ownedHorse);
               owner.setHorseNBT(newNBT);
               data.clearOfflineSavedHorse(ownedHorse);
            }
         }
      }

   }

   public static void onEntityDrop(LivingDropsEvent event) {
      if (CallableHorsesConfig.settings.disableHorseDrops) {
         Entity e = event.getEntity();
         if (e instanceof AbstractHorse) {
            IStoredHorse horse = HorseHelper.getHorseCap(e);
            if (horse.isOwned()) {
               event.getDrops().clear();
            }
         }
      }

   }
}
