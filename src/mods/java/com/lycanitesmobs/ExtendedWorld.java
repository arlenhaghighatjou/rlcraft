package com.lycanitesmobs;

import com.lycanitesmobs.core.config.ConfigSpawning;
import com.lycanitesmobs.core.dungeon.instance.DungeonInstance;
import com.lycanitesmobs.core.entity.BossEntry;
import com.lycanitesmobs.core.mobevent.MobEvent;
import com.lycanitesmobs.core.mobevent.MobEventManager;
import com.lycanitesmobs.core.mobevent.MobEventPlayerClient;
import com.lycanitesmobs.core.mobevent.MobEventPlayerServer;
import com.lycanitesmobs.core.network.MessageMobEvent;
import com.lycanitesmobs.core.network.MessageWorldEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

public class ExtendedWorld extends WorldSavedData {
   private static final String EXT_PROP_NAME = "LycanitesMobs";
   private static final Map<World, ExtendedWorld> loadedExtWorlds = new HashMap();
   public World world;
   public boolean initialized = false;
   public long lastSpawnerTime = 0L;
   public long lastEventScheduleTime = 0L;
   public long lastEventUpdateTime = 0L;
   public boolean useTotalWorldTime = true;
   public Map<String, MobEventPlayerServer> serverMobEventPlayers = new HashMap();
   public Map<String, MobEventPlayerClient> clientMobEventPlayers = new HashMap();
   public MobEventPlayerServer serverWorldEventPlayer = null;
   public MobEventPlayerClient clientWorldEventPlayer = null;
   private long worldEventStartTargetTime = 0L;
   private long worldEventLastStartedTime = 0L;
   private String worldEventName = "";
   private int worldEventCount = -1;
   public Map<EaglercraftUUID, BossEntry> bosses = new HashMap();
   public Map<EaglercraftUUID, DungeonInstance> dungeons = new HashMap();

   public static ExtendedWorld getForWorld(World world) {
      return world == null ? null : (ExtendedWorld)loadedExtWorlds.computeIfAbsent(world, (k) -> {
         WorldSavedData worldSavedData = world.getPerWorldStorage().getOrLoadData(ExtendedWorld.class, "LycanitesMobs");
         ExtendedWorld worldExt;
         if (worldSavedData != null) {
            worldExt = (ExtendedWorld)worldSavedData;
            worldExt.world = world;
            worldExt.init();
         } else {
            worldExt = new ExtendedWorld(world);
            world.getPerWorldStorage().setData("LycanitesMobs", worldExt);
         }

         return worldExt;
      });
   }

   public static void remove(World world) {
      loadedExtWorlds.remove(world);
   }

   public ExtendedWorld(String prop_name) {
      super("LycanitesMobs");
   }

   public ExtendedWorld(World world) {
      super("LycanitesMobs");
      this.world = world;
   }

   public void init() {
      if (!this.initialized) {
         this.initialized = true;
         this.lastSpawnerTime = this.world.getTotalWorldTime() - 1L;
         this.lastEventScheduleTime = this.world.getTotalWorldTime() - 1L;
         this.lastEventUpdateTime = this.world.getTotalWorldTime() - 1L;
         if (!this.world.isRemote && !"".equals(this.getWorldEventName()) && this.serverWorldEventPlayer == null) {
            long savedLastStartedTime = this.getWorldEventLastStartedTime();
            this.startMobEvent(this.getWorldEventName(), (EntityPlayer)null, new BlockPos(0, 0, 0), 1, -1);
            if (this.serverWorldEventPlayer != null) {
               this.serverWorldEventPlayer.changeStartedWorldTime(savedLastStartedTime);
            }
         }

      }
   }

   public void loadConfig() {
      ConfigSpawning config = ConfigSpawning.getConfig(LycanitesMobs.modInfo, "mobevents");
   }

   protected String getConfigEntryName(String name) {
      return name + " " + this.world.provider.getDimensionType().getName() + " (" + this.world.provider.getDimension() + ")";
   }

   public long getWorldEventStartTargetTime() {
      return this.worldEventStartTargetTime;
   }

   public long getWorldEventLastStartedTime() {
      return this.worldEventLastStartedTime;
   }

   public String getWorldEventName() {
      return this.worldEventName;
   }

   public MobEvent getWorldEvent() {
      return this.getWorldEventName() != null && !"".equals(this.getWorldEventName()) ? MobEventManager.getInstance().getMobEvent(this.getWorldEventName()) : null;
   }

   public int getWorldEventCount() {
      return this.worldEventCount;
   }

   public void setWorldEventStartTargetTime(long setLong) {
      if (this.worldEventStartTargetTime != setLong) {
         this.markDirty();
      }

      this.worldEventStartTargetTime = setLong;
      if (setLong > 0L) {
         LycanitesMobs.logDebug("MobEvents", "Next random mob will start after " + (this.worldEventStartTargetTime - this.world.getTotalWorldTime()) / 20L + "secs.");
      }

   }

   public void setWorldEventLastStartedTime(long setLong) {
      if (this.worldEventLastStartedTime != setLong) {
         this.markDirty();
      }

      this.worldEventLastStartedTime = setLong;
   }

   public void setWorldEventName(String setString) {
      if (!this.worldEventName.equals(setString)) {
         this.markDirty();
      }

      this.worldEventName = setString;
   }

   public void increaseMobEventCount() {
      ++this.worldEventCount;
   }

   public int getRandomEventDelay(EaglercraftRandom random) {
      int min = Math.max(200, MobEventManager.getInstance().minTicksUntilEvent);
      int max = Math.max(200, MobEventManager.getInstance().maxTicksUntilEvent);
      return max <= min ? min : min + random.nextInt(max - min);
   }

   public void startWorldEvent(MobEvent mobEvent) {
      if (mobEvent == null) {
         LycanitesMobs.logWarning("", "Tried to start a null world event, stopping any event instead.");
         this.stopWorldEvent();
      } else {
         if (!this.world.isRemote) {
            boolean extended = false;
            if (this.serverWorldEventPlayer != null) {
               extended = this.serverWorldEventPlayer.mobEvent == mobEvent;
            }

            if (!extended) {
               this.serverWorldEventPlayer = mobEvent.getServerEventPlayer(this.world);
            }

            this.serverWorldEventPlayer.extended = extended;
            this.setWorldEventName(mobEvent.name);
            this.increaseMobEventCount();
            this.setWorldEventStartTargetTime(0L);
            this.setWorldEventLastStartedTime(this.world.getTotalWorldTime());
            this.serverWorldEventPlayer.onStart();
            this.updateAllClientsEvents();
         }

         if (this.world.isRemote) {
            boolean extended = false;
            if (this.clientWorldEventPlayer != null) {
               extended = this.clientWorldEventPlayer.mobEvent == mobEvent;
            }

            if (!extended) {
               this.clientWorldEventPlayer = mobEvent.getClientEventPlayer(this.world);
            }

            this.clientWorldEventPlayer.extended = extended;
            if (LycanitesMobs.proxy.getClientPlayer() != null) {
               this.clientWorldEventPlayer.onStart(LycanitesMobs.proxy.getClientPlayer());
            }
         }

      }
   }

   public void stopWorldEvent() {
      if (this.serverWorldEventPlayer != null) {
         this.serverWorldEventPlayer.onFinish();
         this.setWorldEventName("");
         this.serverWorldEventPlayer = null;
         this.updateAllClientsEvents();
      }

      if (this.clientWorldEventPlayer != null) {
         if (LycanitesMobs.proxy.getClientPlayer() != null) {
            this.clientWorldEventPlayer.onFinish(LycanitesMobs.proxy.getClientPlayer());
         }

         this.clientWorldEventPlayer = null;
      }

   }

   public void startMobEvent(MobEvent mobEvent, EntityPlayer player, BlockPos pos, int level, int variant) {
      if (mobEvent == null) {
         LycanitesMobs.logWarning("", "Tried to start a null mob event.");
      } else {
         if (!this.world.isRemote) {
            MobEventPlayerServer mobEventPlayerServer = mobEvent.getServerEventPlayer(this.world);
            this.serverMobEventPlayers.put(mobEvent.name, mobEventPlayerServer);
            mobEventPlayerServer.player = player;
            mobEventPlayerServer.origin = pos;
            mobEventPlayerServer.level = level;
            mobEventPlayerServer.variant = variant;
            mobEventPlayerServer.onStart();
            this.updateAllClientsEvents();
         }

         if (this.world.isRemote) {
            MobEventPlayerClient mobEventPlayerClient;
            if (this.clientMobEventPlayers.containsKey(mobEvent.name)) {
               mobEventPlayerClient = (MobEventPlayerClient)this.clientMobEventPlayers.get(mobEvent.name);
            } else {
               mobEventPlayerClient = mobEvent.getClientEventPlayer(this.world);
               this.clientMobEventPlayers.put(mobEvent.name, mobEventPlayerClient);
            }

            if (LycanitesMobs.proxy.getClientPlayer() != null) {
               mobEventPlayerClient.onStart(LycanitesMobs.proxy.getClientPlayer());
            }
         }

      }
   }

   public MobEvent startMobEvent(String mobEventName, EntityPlayer player, BlockPos pos, int level, int subspecies) {
      if (MobEventManager.getInstance().mobEvents.containsKey(mobEventName)) {
         MobEvent mobEvent = (MobEvent)MobEventManager.getInstance().mobEvents.get(mobEventName);
         if (!mobEvent.isEnabled()) {
            LycanitesMobs.logWarning("", "Tried to start a mob event that was disabled with the name: '" + mobEventName + "' on " + (this.world.isRemote ? "Client" : "Server"));
            return null;
         } else {
            mobEvent.trigger(this.world, player, pos, level, subspecies);
            return mobEvent;
         }
      } else {
         LycanitesMobs.logWarning("", "Tried to start a mob event with the invalid name: '" + mobEventName + "' on " + (this.world.isRemote ? "Client" : "Server"));
         return null;
      }
   }

   public void stopMobEvent(String mobEventName) {
      if (!this.world.isRemote && this.serverMobEventPlayers.containsKey(mobEventName)) {
         ((MobEventPlayerServer)this.serverMobEventPlayers.get(mobEventName)).onFinish();
         this.serverMobEventPlayers.remove(mobEventName);
         this.updateAllClientsEvents();
      }

      if (this.world.isRemote && this.clientMobEventPlayers.containsKey(mobEventName)) {
         if (LycanitesMobs.proxy.getClientPlayer() != null) {
            ((MobEventPlayerClient)this.clientMobEventPlayers.get(mobEventName)).onFinish(LycanitesMobs.proxy.getClientPlayer());
         }

         this.clientMobEventPlayers.remove(mobEventName);
      }

   }

   public MobEventPlayerServer getMobEventPlayerServer(String mobEventName) {
      if (mobEventName != null && !"".equals(mobEventName)) {
         if (mobEventName.equals(this.getWorldEventName())) {
            return this.serverWorldEventPlayer;
         } else {
            return this.serverMobEventPlayers.containsKey(mobEventName) ? (MobEventPlayerServer)this.serverMobEventPlayers.get(mobEventName) : null;
         }
      } else {
         return null;
      }
   }

   public MobEventPlayerClient getMobEventPlayerClient(String mobEventName) {
      if (mobEventName != null && !"".equals(mobEventName)) {
         if (mobEventName.equals(this.worldEventName)) {
            return this.clientWorldEventPlayer;
         } else {
            return this.clientMobEventPlayers.containsKey(mobEventName) ? (MobEventPlayerClient)this.clientMobEventPlayers.get(mobEventName) : null;
         }
      } else {
         return null;
      }
   }

   public void bossUpdate(Entity entity) {
      if (entity.isEntityAlive()) {
         if (!this.bosses.containsKey(entity.getUniqueID())) {
            this.bosses.put(entity.getUniqueID(), new BossEntry());
         }

         ((BossEntry)this.bosses.get(entity.getUniqueID())).update(entity);
      }

   }

   public void overrideBossRange(Entity entity, int rangeOverride) {
      if (this.bosses.containsKey(entity.getUniqueID())) {
         ((BossEntry)this.bosses.get(entity.getUniqueID())).nearbyRange = rangeOverride;
      }

   }

   public void bossRemoved(Entity entity) {
      this.bosses.remove(entity.getUniqueID());
   }

   public boolean isBossNearby(Vec3d pos) {
      for(BossEntry bossEntry : this.bosses.values()) {
         if (bossEntry != null && bossEntry.entity != null && bossEntry.entity.isEntityAlive() && bossEntry.entity.getDistanceSq(pos.x, pos.y, pos.z) <= (double)(bossEntry.nearbyRange * bossEntry.nearbyRange)) {
            return true;
         }
      }

      return false;
   }

   public void updateAllClientsEvents() {
      BlockPos pos = this.serverWorldEventPlayer != null ? this.serverWorldEventPlayer.origin : new BlockPos(0, 0, 0);
      int level = this.serverWorldEventPlayer != null ? this.serverWorldEventPlayer.level : 0;
      int subspecies = this.serverWorldEventPlayer != null ? this.serverWorldEventPlayer.variant : -1;
      MessageWorldEvent message = new MessageWorldEvent(this.getWorldEventName(), pos, level, subspecies);
      LycanitesMobs.packetHandler.sendToDimension(message, this.world.provider.getDimension());

      for(MobEventPlayerServer mobEventPlayerServer : this.serverMobEventPlayers.values()) {
         MessageMobEvent messageMobEvent = new MessageMobEvent(mobEventPlayerServer.mobEvent != null ? mobEventPlayerServer.mobEvent.name : "", mobEventPlayerServer.origin, mobEventPlayerServer.level, mobEventPlayerServer.variant);
         LycanitesMobs.packetHandler.sendToDimension(messageMobEvent, this.world.provider.getDimension());
      }

   }

   public void addDungeonInstance(DungeonInstance dungeonInstance, EaglercraftUUID uuid) {
      dungeonInstance.uuid = uuid;
      this.dungeons.put(dungeonInstance.uuid, dungeonInstance);
      this.markDirty();
   }

   public List<DungeonInstance> getNearbyDungeonInstances(ChunkPos chunkPos, int range) {
      List<DungeonInstance> nearbyDungeons = new ArrayList();

      for(DungeonInstance dungeonInstance : this.dungeons.values()) {
         if (dungeonInstance.world == null) {
            dungeonInstance.init(this.world);
         }

         if (dungeonInstance.isChunkPosWithin(chunkPos, range)) {
            nearbyDungeons.add(dungeonInstance);
         }
      }

      return nearbyDungeons;
   }

   public void readFromNBT(NBTTagCompound nbtTagCompound) {
      if (nbtTagCompound.hasKey("WorldEventStartTargetTime")) {
         this.worldEventStartTargetTime = nbtTagCompound.getLong("WorldEventStartTargetTime");
      }

      if (nbtTagCompound.hasKey("WorldEventLastStartedTime")) {
         this.worldEventLastStartedTime = nbtTagCompound.getLong("WorldEventLastStartedTime");
      }

      if (nbtTagCompound.hasKey("WorldEventName")) {
         this.worldEventName = nbtTagCompound.getString("WorldEventName");
      }

      if (nbtTagCompound.hasKey("WorldEventCount")) {
         this.worldEventCount = nbtTagCompound.getInteger("WorldEventCount");
      }

      if (nbtTagCompound.hasKey("Dungeons")) {
         NBTTagList nbtDungeonList = nbtTagCompound.getTagList("Dungeons", 10);

         for(int i = 0; i < nbtDungeonList.tagCount(); ++i) {
            try {
               NBTTagCompound dungeonNBT = nbtDungeonList.getCompoundTagAt(i);
               DungeonInstance dungeonInstance = new DungeonInstance();
               dungeonInstance.readFromNBT(dungeonNBT);
               if (dungeonInstance.uuid != null && !this.dungeons.containsKey(dungeonInstance.uuid)) {
                  this.dungeons.put(dungeonInstance.uuid, dungeonInstance);
               }
            } catch (Exception var6) {
               LycanitesMobs.logWarning("Dungeon", "An exception occurred when loading a dungeon from NBT.");
            }
         }
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
      nbtTagCompound.setLong("WorldEventStartTargetTime", this.worldEventStartTargetTime);
      nbtTagCompound.setLong("WorldEventLastStartedTime", this.worldEventLastStartedTime);
      nbtTagCompound.setString("WorldEventName", this.worldEventName);
      nbtTagCompound.setInteger("WorldEventCount", this.worldEventCount);
      NBTTagList nbtDungeonList = new NBTTagList();

      for(DungeonInstance dungeonInstance : this.dungeons.values()) {
         NBTTagCompound dungeonNBT = new NBTTagCompound();
         dungeonNBT = dungeonInstance.writeToNBT(dungeonNBT);
         if (dungeonNBT != null) {
            nbtDungeonList.appendTag(dungeonNBT);
         }
      }

      nbtTagCompound.setTag("Dungeons", nbtDungeonList);
      return nbtTagCompound;
   }
}
