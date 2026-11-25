package com.fantasticsource.mctools;

import com.fantasticsource.tools.ReflectionTool;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WorldEventDistributor implements IWorldEventListener {
   private static Field worldEventListenersField = ReflectionTool.getField(World.class, "eventListeners", "eventListeners");
   private World world;
   private List<IWorldEventListener> normalListeners = new ArrayList();

   private WorldEventDistributor(World world, List<IWorldEventListener> originalListeners) {
      this.world = world;
      this.normalListeners.addAll(originalListeners);
      originalListeners.clear();
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST,
      receiveCanceled = true
   )
   public static void worldLoad(Load event) {
      try {
         World world = event.getWorld();
         List<IWorldEventListener> originalListeners = (List)worldEventListenersField.get(world);
         originalListeners.add(0, new WorldEventDistributor(world, originalListeners));
      } catch (IllegalAccessException var3) {
         MCTools.crash(var3, true);
      }

   }

   public void notifyBlockUpdate(World world, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
      WorldEventDistributor.DBlockUpdateEvent event = new WorldEventDistributor.DBlockUpdateEvent(world, pos, oldState, newState, flags);
      if (!MinecraftForge.EVENT_BUS.post(event)) {
         for(IWorldEventListener listener : this.normalListeners) {
            listener.notifyBlockUpdate(event.getWorld(), event.pos, event.oldState, event.newState, event.flags);
         }
      }

   }

   public void notifyLightSet(BlockPos pos) {
      WorldEventDistributor.DNotifyLightSetEvent event = new WorldEventDistributor.DNotifyLightSetEvent(this.world, pos);
      if (!MinecraftForge.EVENT_BUS.post(event)) {
         for(IWorldEventListener listener : this.normalListeners) {
            listener.notifyLightSet(event.pos);
         }
      }

   }

   public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
      WorldEventDistributor.DMarkBlockRangeForRenderUpdateEvent event = new WorldEventDistributor.DMarkBlockRangeForRenderUpdateEvent(this.world, x1, y1, z1, x2, y2, z2);
      if (!MinecraftForge.EVENT_BUS.post(event)) {
         for(IWorldEventListener listener : this.normalListeners) {
            listener.markBlockRangeForRenderUpdate(event.x1, event.y1, event.z1, event.x2, event.y2, event.z2);
         }
      }

   }

   public void playSoundToAllNearExcept(@Nullable EntityPlayer player, SoundEvent soundEvent, SoundCategory soundCategory, double x, double y, double z, float volume, float pitch) {
      WorldEventDistributor.DSoundEvent event = new WorldEventDistributor.DSoundEvent(this.world, player, soundEvent, soundCategory, x, y, z, volume, pitch);
      if (!MinecraftForge.EVENT_BUS.post(event)) {
         for(IWorldEventListener listener : this.normalListeners) {
            listener.playSoundToAllNearExcept(event.player, event.soundEvent, event.soundCategory, event.x, event.y, event.z, event.volume, event.pitch);
         }
      }

   }

   public void playRecord(SoundEvent soundEvent, BlockPos pos) {
      WorldEventDistributor.DPlayRecordEvent event = new WorldEventDistributor.DPlayRecordEvent(this.world, soundEvent, pos);
      if (!MinecraftForge.EVENT_BUS.post(event)) {
         for(IWorldEventListener listener : this.normalListeners) {
            listener.playRecord(event.soundEvent, event.pos);
         }
      }

   }

   public void spawnParticle(int particleID, boolean ignoreRange, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
      this.spawnParticle(particleID, ignoreRange, false, x, y, z, xSpeed, ySpeed, zSpeed, parameters);
   }

   public void spawnParticle(int id, boolean ignoreRange, boolean minimumParticles, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
      WorldEventDistributor.DSpawnParticleEvent event = new WorldEventDistributor.DSpawnParticleEvent(this.world, id, ignoreRange, minimumParticles, x, y, z, xSpeed, ySpeed, zSpeed, parameters);
      if (!MinecraftForge.EVENT_BUS.post(event)) {
         for(IWorldEventListener listener : this.normalListeners) {
            listener.spawnParticle(event.id, event.ignoreRange, event.minimumParticles, event.x, event.y, event.z, event.xSpeed, event.ySpeed, event.zSpeed, event.parameters);
         }
      }

   }

   public void onEntityAdded(Entity entity) {
      MinecraftForge.EVENT_BUS.post(new WorldEventDistributor.DEntityAddedEvent(this.world, entity));

      for(IWorldEventListener listener : this.normalListeners) {
         listener.onEntityAdded(entity);
      }

   }

   public void onEntityRemoved(Entity entity) {
      MinecraftForge.EVENT_BUS.post(new WorldEventDistributor.DEntityRemovedEvent(this.world, entity));

      for(IWorldEventListener listener : this.normalListeners) {
         listener.onEntityRemoved(entity);
      }

   }

   public void broadcastSound(int soundID, BlockPos pos, int data) {
      WorldEventDistributor.DBroadcastSoundEvent event = new WorldEventDistributor.DBroadcastSoundEvent(this.world, soundID, pos, data);
      if (!MinecraftForge.EVENT_BUS.post(event)) {
         for(IWorldEventListener listener : this.normalListeners) {
            listener.broadcastSound(event.soundID, event.pos, event.data);
         }
      }

   }

   public void playEvent(EntityPlayer player, int type, BlockPos pos, int data) {
      WorldEventDistributor.DPlayEvent event = new WorldEventDistributor.DPlayEvent(this.world, player, type, pos, data);
      if (!MinecraftForge.EVENT_BUS.post(event)) {
         for(IWorldEventListener listener : this.normalListeners) {
            listener.playEvent(event.player, event.type, event.pos, event.data);
         }
      }

   }

   public void sendBlockBreakProgress(int breakerID, BlockPos pos, int progress) {
      WorldEventDistributor.DSendBlockBreakProgressEvent event = new WorldEventDistributor.DSendBlockBreakProgressEvent(this.world, breakerID, pos, progress);
      if (!MinecraftForge.EVENT_BUS.post(event)) {
         for(IWorldEventListener listener : this.normalListeners) {
            listener.sendBlockBreakProgress(event.breakerID, event.pos, event.progress);
         }
      }

   }

   @Cancelable
   public static class DBlockUpdateEvent extends WorldEventDistributor.DWorldEvent {
      public BlockPos pos;
      public IBlockState oldState;
      public IBlockState newState;
      public int flags;

      private DBlockUpdateEvent(World world, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
         super(world);
         this.pos = pos;
         this.oldState = oldState;
         this.newState = newState;
         this.flags = flags;
      }
   }

   @Cancelable
   public static class DBroadcastSoundEvent extends WorldEventDistributor.DWorldEvent {
      public int soundID;
      public int data;
      public BlockPos pos;

      private DBroadcastSoundEvent(World world, int soundID, BlockPos pos, int data) {
         super(world);
         this.soundID = soundID;
         this.pos = pos;
         this.data = data;
      }
   }

   public static class DEntityAddedEvent extends WorldEventDistributor.DWorldEvent {
      private Entity entity;

      private DEntityAddedEvent(World world, Entity entity) {
         super(world);
         this.entity = entity;
      }

      public Entity getEntity() {
         return this.entity;
      }
   }

   public static class DEntityRemovedEvent extends WorldEventDistributor.DWorldEvent {
      private Entity entity;

      private DEntityRemovedEvent(World world, Entity entity) {
         super(world);
         this.entity = entity;
      }

      public Entity getEntity() {
         return this.entity;
      }
   }

   @Cancelable
   public static class DMarkBlockRangeForRenderUpdateEvent extends WorldEventDistributor.DWorldEvent {
      public int x1;
      public int y1;
      public int z1;
      public int x2;
      public int y2;
      public int z2;

      private DMarkBlockRangeForRenderUpdateEvent(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
         super(world);
         this.x1 = x1;
         this.y1 = y1;
         this.z1 = z1;
         this.x2 = x2;
         this.y2 = y2;
         this.z2 = z2;
      }
   }

   @Cancelable
   public static class DNotifyLightSetEvent extends WorldEventDistributor.DWorldEvent {
      public BlockPos pos;

      private DNotifyLightSetEvent(World world, BlockPos pos) {
         super(world);
         this.pos = pos;
      }
   }

   @Cancelable
   public static class DPlayEvent extends WorldEventDistributor.DWorldEvent {
      public EntityPlayer player;
      public int type;
      public int data;
      public BlockPos pos;

      private DPlayEvent(World world, EntityPlayer player, int type, BlockPos pos, int data) {
         super(world);
         this.player = player;
         this.type = type;
         this.pos = pos;
         this.data = data;
      }
   }

   @Cancelable
   public static class DPlayRecordEvent extends WorldEventDistributor.DWorldEvent {
      public SoundEvent soundEvent;
      public BlockPos pos;

      private DPlayRecordEvent(World world, SoundEvent soundEvent, BlockPos pos) {
         super(world);
         this.soundEvent = soundEvent;
         this.pos = pos;
      }
   }

   @Cancelable
   public static class DSendBlockBreakProgressEvent extends WorldEventDistributor.DWorldEvent {
      public int breakerID;
      public int progress;
      public BlockPos pos;

      private DSendBlockBreakProgressEvent(World world, int breakerID, BlockPos pos, int progress) {
         super(world);
         this.breakerID = breakerID;
         this.pos = pos;
         this.progress = progress;
      }
   }

   @Cancelable
   public static class DSoundEvent extends WorldEventDistributor.DWorldEvent {
      public EntityPlayer player;
      public SoundEvent soundEvent;
      public SoundCategory soundCategory;
      public double x;
      public double y;
      public double z;
      public float volume;
      public float pitch;
      public Entity entity;

      private DSoundEvent(World world, @Nullable EntityPlayer player, SoundEvent soundEvent, SoundCategory soundCategory, double x, double y, double z, float volume, float pitch) {
         super(world);
         this.entity = null;
         this.player = player;
         this.soundEvent = soundEvent;
         this.soundCategory = soundCategory;
         this.x = x;
         this.y = y;
         this.z = z;
         this.volume = volume;
         this.pitch = pitch;

         for(Entity entity : world.loadedEntityList) {
            if (entity.posX == x && entity.posY == y && entity.posZ == z) {
               this.entity = entity;
               break;
            }
         }

      }
   }

   @Cancelable
   public static class DSpawnParticleEvent extends WorldEventDistributor.DWorldEvent {
      public int id;
      public boolean ignoreRange;
      public boolean minimumParticles;
      public double x;
      public double y;
      public double z;
      public double xSpeed;
      public double ySpeed;
      public double zSpeed;
      public int[] parameters;

      private DSpawnParticleEvent(World world, int id, boolean ignoreRange, boolean minimumParticles, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
         super(world);
         this.id = id;
         this.ignoreRange = ignoreRange;
         this.minimumParticles = minimumParticles;
         this.x = x;
         this.y = y;
         this.z = z;
         this.xSpeed = xSpeed;
         this.ySpeed = ySpeed;
         this.zSpeed = zSpeed;
         this.parameters = parameters;
      }
   }

   public static class DWorldEvent extends Event {
      private World world;

      public DWorldEvent(World world) {
         this.world = world;
      }

      public World getWorld() {
         return this.world;
      }
   }
}
