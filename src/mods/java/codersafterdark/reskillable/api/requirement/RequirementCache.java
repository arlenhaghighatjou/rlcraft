package codersafterdark.reskillable.api.requirement;

import codersafterdark.reskillable.api.event.CacheInvalidatedEvent;
import codersafterdark.reskillable.api.event.LevelUpEvent;
import codersafterdark.reskillable.api.event.LockUnlockableEvent;
import codersafterdark.reskillable.api.event.UnlockUnlockableEvent;
import codersafterdark.reskillable.api.requirement.logic.DoubleRequirement;
import codersafterdark.reskillable.api.requirement.logic.OuterRequirement;
import codersafterdark.reskillable.api.requirement.logic.impl.NOTRequirement;
import codersafterdark.reskillable.network.InvalidateRequirementPacket;
import codersafterdark.reskillable.network.PacketHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public class RequirementCache {
   private static Set<Class<? extends Requirement>> dirtyCacheTypes = new HashSet();
   private static Map<RequirementCache.SidedUUID, RequirementCache> cacheMap = new HashMap();
   private Map<Class<? extends Requirement>, Map<Requirement, Boolean>> requirementCache;
   private Set<Class<? extends Requirement>> recentlyInvalidated;
   private boolean valid;
   private boolean isRemote;
   private boolean dirtyCache;
   private UUID uuid;

   /** @deprecated */
   @Deprecated
   public RequirementCache(@Nonnull EntityPlayer player) {
      this(player.getUniqueID(), player.getEntityWorld().isRemote);
      cacheMap.put(new RequirementCache.SidedUUID(this.uuid, this.isRemote), this);
   }

   private RequirementCache(UUID uuid, boolean isClientPlayer) {
      this.requirementCache = new HashMap();
      this.recentlyInvalidated = new HashSet();
      this.valid = true;
      this.uuid = uuid;
      this.isRemote = isClientPlayer;
   }

   public static RequirementCache getCache(@Nonnull EntityPlayer player) {
      return getCache(player.getUniqueID(), player.getEntityWorld().isRemote);
   }

   public static RequirementCache getCache(UUID uuid, boolean isRemote) {
      RequirementCache.SidedUUID sidedUUID = new RequirementCache.SidedUUID(uuid, isRemote);
      if (cacheMap.containsKey(sidedUUID)) {
         return (RequirementCache)cacheMap.get(sidedUUID);
      } else {
         RequirementCache cache = new RequirementCache(uuid, isRemote);
         cacheMap.put(sidedUUID, cache);
         return cache;
      }
   }

   public static boolean hasCache(@Nonnull EntityPlayer player) {
      return hasCache(player.getUniqueID(), player.getEntityWorld().isRemote);
   }

   public static boolean hasCache(UUID uuid, boolean isRemote) {
      return cacheMap.containsKey(new RequirementCache.SidedUUID(uuid, isRemote));
   }

   public static void registerDirtyTypes() {
      registerRequirementType(NOTRequirement.class, DoubleRequirement.class);
   }

   public static void registerRequirementType(Class<? extends Requirement>... requirementClasses) {
      dirtyCacheTypes.addAll(Arrays.asList(requirementClasses));
   }

   public static void invalidateCache(EntityPlayer player, Class<? extends Requirement>... cacheTypes) {
      if (player != null) {
         invalidateCache(player.getUniqueID(), cacheTypes);
      }

   }

   public static void invalidateCache(UUID uuid, Class<? extends Requirement>... cacheTypes) {
      boolean hasServer = hasCache(uuid, false);
      boolean hasClient = hasCache(uuid, true);
      if (hasServer) {
         invalidateCacheNoPacket(uuid, false, cacheTypes);
         if (!hasClient) {
            RequirementCache cache = getCache(uuid, false);
            if (cache != null) {
               EntityPlayer player = cache.getPlayer();
               if (player != null) {
                  PacketHandler.INSTANCE.sendTo(new InvalidateRequirementPacket(uuid, cacheTypes), (EntityPlayerMP)player);
               }
            }
         }
      }

      if (hasClient) {
         invalidateCacheNoPacket(uuid, true, cacheTypes);
         if (!hasServer) {
            PacketHandler.INSTANCE.sendToServer(new InvalidateRequirementPacket(uuid, cacheTypes));
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public static void invalidateCacheNoPacket(UUID uuid, Class<? extends Requirement>... cacheTypes) {
      invalidateCacheNoPacket(uuid, true, cacheTypes);
      invalidateCacheNoPacket(uuid, false, cacheTypes);
   }

   public static void invalidateCacheNoPacket(UUID uuid, boolean isRemote, Class<? extends Requirement>... cacheTypes) {
      getCache(uuid, isRemote).invalidateCache(cacheTypes);
   }

   public static boolean requirementAchieved(EntityPlayer player, Requirement requirement) {
      return player != null && requirementAchieved(player.getUniqueID(), player.getEntityWorld().isRemote, requirement);
   }

   /** @deprecated */
   @Deprecated
   public static boolean requirementAchieved(UUID uuid, Requirement requirement) {
      return requirementAchieved(uuid, true, requirement) || requirementAchieved(uuid, false, requirement);
   }

   public static boolean requirementAchieved(UUID uuid, boolean isRemote, Requirement requirement) {
      return getCache(uuid, isRemote).requirementAchieved(requirement);
   }

   @SubscribeEvent
   public static void onLevelChange(LevelUpEvent.Post event) {
      invalidateCache(event.getEntityPlayer().getUniqueID(), SkillRequirement.class);
   }

   @SubscribeEvent
   public static void onUnlockableLocked(LockUnlockableEvent.Post event) {
      invalidateCache(event.getEntityPlayer().getUniqueID(), TraitRequirement.class);
   }

   @SubscribeEvent
   public static void onUnlockableUnlocked(UnlockUnlockableEvent.Post event) {
      invalidateCache(event.getEntityPlayer().getUniqueID(), TraitRequirement.class);
   }

   @SubscribeEvent
   public static void onAdvancement(AdvancementEvent event) {
      invalidateCache(event.getEntityPlayer().getUniqueID(), AdvancementRequirement.class);
   }

   @SubscribeEvent
   public static void onDisconnect(PlayerLoggedOutEvent event) {
      removeCache(event.player.getUniqueID(), true);
      removeCache(event.player.getUniqueID(), false);
   }

   public static void removeCache(EntityPlayer player) {
      removeCache(player.getUniqueID(), player.getEntityWorld().isRemote);
   }

   public static void removeCache(UUID uuid, boolean isRemote) {
      RequirementCache.SidedUUID sidedUUID = new RequirementCache.SidedUUID(uuid, isRemote);
      if (cacheMap.containsKey(sidedUUID)) {
         ((RequirementCache)cacheMap.get(sidedUUID)).valid = false;
         cacheMap.remove(sidedUUID);
      }

   }

   @Nullable
   private EntityPlayer getPlayer() {
      if (this.isRemote) {
         WorldClient world = Minecraft.getMinecraft().world;
         return world == null ? null : world.getPlayerEntityByUUID(this.uuid);
      } else {
         MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
         if (server != null) {
            for(WorldServer world : server.worlds) {
               EntityPlayer player = world.getPlayerEntityByUUID(this.uuid);
               if (player != null) {
                  return player;
               }
            }
         }

         return null;
      }
   }

   public boolean isValid() {
      return this.valid;
   }

   public void forceClear() {
      if (this.isValid()) {
         EntityPlayer player = this.getPlayer();
         if (player != null) {
            this.requirementCache.clear();
            this.recentlyInvalidated.clear();
            this.dirtyCache = false;
            MinecraftForge.EVENT_BUS.post(new CacheInvalidatedEvent(player, true));
            if (!this.isRemote) {
               PacketHandler.INSTANCE.sendTo(new InvalidateRequirementPacket(this.uuid, new Class[0]), (EntityPlayerMP)player);
            }
         }
      }

   }

   public boolean requirementAchieved(Requirement requirement) {
      if (requirement != null && this.isValid()) {
         EntityPlayer player = this.getPlayer();
         if (player == null) {
            return false;
         } else if (!requirement.isCacheable()) {
            return requirement.achievedByPlayer(player);
         } else {
            Class<? extends Requirement> clazz = requirement.getClass();
            Map<Requirement, Boolean> cache;
            if (this.requirementCache.containsKey(clazz)) {
               cache = (Map)this.requirementCache.get(clazz);
               if (cache.containsKey(requirement)) {
                  return ((Boolean)cache.get(requirement)).booleanValue();
               }
            } else {
               this.requirementCache.put(clazz, cache = new HashMap());
            }

            boolean achieved = requirement.achievedByPlayer(player);
            cache.put(requirement, Boolean.valueOf(achieved));
            if (!this.dirtyCache && dirtyCacheTypes.stream().anyMatch((dirtyType) -> {
               return dirtyType.isInstance(requirement);
            })) {
               this.dirtyCache = true;
            }

            this.recentlyInvalidated.removeAll((Collection)this.recentlyInvalidated.stream().filter((type) -> {
               return type.isInstance(requirement);
            }).collect(Collectors.toList()));
            if (requirement instanceof OuterRequirement) {
               this.recentlyInvalidated.removeAll(((OuterRequirement)requirement).getInternalTypes());
            }

            return achieved;
         }
      } else {
         return false;
      }
   }

   public void invalidateCache(Class<? extends Requirement>... cacheType) {
      EntityPlayer player = this.getPlayer();
      if (player != null) {
         List<Class<? extends Requirement>> dirtyTypes = this.dirtyCache ? new ArrayList(dirtyCacheTypes) : new ArrayList();
         if (cacheType != null) {
            for(Class<? extends Requirement> type : cacheType) {
               if (!this.recentlyInvalidated.contains(type)) {
                  dirtyTypes.add(type);
                  this.recentlyInvalidated.add(type);
               }
            }

            if (dirtyTypes.size() == dirtyCacheTypes.size()) {
               MinecraftForge.EVENT_BUS.post(new CacheInvalidatedEvent(player, false));
               return;
            }
         }

         if (dirtyTypes.isEmpty()) {
            MinecraftForge.EVENT_BUS.post(new CacheInvalidatedEvent(player, false));
         } else {
            Set<Class<? extends Requirement>> requirements = this.requirementCache.keySet();
            List<Class<? extends Requirement>> toRemove = new ArrayList();

            for(Class<? extends Requirement> requirement : requirements) {
               for(Class<? extends Requirement> dirtyType : dirtyTypes) {
                  if (dirtyType.isAssignableFrom(requirement)) {
                     toRemove.add(requirement);
                  }
               }
            }

            toRemove.forEach((requirementx) -> {
               Map var10000 = (Map)this.requirementCache.remove(requirement);
            });
            MinecraftForge.EVENT_BUS.post(new CacheInvalidatedEvent(player, !toRemove.isEmpty()));
         }
      }
   }

   private static class SidedUUID {
      private final UUID uuid;
      private final boolean isRemote;

      private SidedUUID(UUID uuid, boolean isRemote) {
         this.uuid = uuid;
         this.isRemote = isRemote;
      }

      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (!(obj instanceof RequirementCache.SidedUUID)) {
            return false;
         } else {
            RequirementCache.SidedUUID other = (RequirementCache.SidedUUID)obj;
            return this.isRemote == other.isRemote && this.uuid.equals(other.uuid);
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.isRemote, this.uuid});
      }
   }
}
