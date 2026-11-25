package com.tmtravlr.potioncore;

import java.util.EnumSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class PotionCoreTeleporter extends Teleporter {
   private WorldServer worldServer;
   private double x;
   private double y;
   private double z;
   private float pitch;
   private float yaw;
   private double motionX;
   private double motionY;
   private double motionZ;

   public PotionCoreTeleporter(WorldServer worldServerOld, double xToSet, double yToSet, double zToSet, float pitchToSet, float yawToSet, double motionXToSet, double motionYToSet, double motionZToSet) {
      super(worldServerOld);
      this.worldServer = worldServerOld;
      this.x = xToSet;
      this.y = yToSet;
      this.z = zToSet;
      this.pitch = pitchToSet;
      this.yaw = yawToSet;
      this.motionX = motionXToSet;
      this.motionY = motionYToSet;
      this.motionZ = motionZToSet;
   }

   public PotionCoreTeleporter(WorldServer worldServerOld, double xToSet, double yToSet, double zToSet, float pitchToSet, float yawToSet) {
      this(worldServerOld, xToSet, yToSet, zToSet, pitchToSet, yawToSet, 0.0D, 0.0D, 0.0D);
   }

   public void placeInPortal(Entity entity, float par8) {
      entity.setLocationAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
      entity.setRotationYawHead(this.yaw);
      entity.motionX = this.motionX;
      entity.motionY = this.motionY;
      entity.motionZ = this.motionZ;
      if (entity instanceof EntityPlayerMP) {
         EntityPlayerMP player = (EntityPlayerMP)entity;
         player.connection.sendPacket(new SPacketEntityVelocity(player));
      }

   }

   public static void teleportEntity(Entity entity, World newWorld, double x, double y, double z) {
      teleportEntity(entity, newWorld, x, y, z, entity.rotationPitch, entity.rotationYaw, 0.0D, 0.0D, 0.0D);
   }

   public static void teleportEntity(Entity entity, World newWorld, double x, double y, double z, float pitch, float yaw) {
      teleportEntity(entity, newWorld, x, y, z, pitch, yaw, 0.0D, 0.0D, 0.0D);
   }

   public static Entity teleportEntity(Entity entity, World newWorld, double x, double y, double z, float pitch, float yaw, double motionX, double motionY, double motionZ) {
      int dimension = newWorld.provider.getDimension();
      int currentDimension = entity.world.provider.getDimension();
      if (dimension != currentDimension) {
         return transferEntityToDimension(entity, dimension, x, y, z, pitch, yaw, motionX, motionY, motionZ);
      } else {
         entity.setLocationAndAngles(x, y, z, yaw, pitch);
         entity.setRotationYawHead(yaw);
         entity.motionX = motionX;
         entity.motionY = motionY;
         entity.motionZ = motionZ;
         return entity;
      }
   }

   public static void teleportPlayer(EntityPlayerMP player, EnumSet packetSet, World newWorld, double x, double y, double z) {
      teleportPlayer(player, packetSet, newWorld, x, y, z, player.rotationPitch, player.rotationYaw, 0.0D, 0.0D, 0.0D);
   }

   public static void teleportPlayer(EntityPlayerMP player, EnumSet packetSet, World newWorld, double x, double y, double z, float pitch, float yaw) {
      teleportPlayer(player, packetSet, newWorld, x, y, z, pitch, yaw, 0.0D, 0.0D, 0.0D);
   }

   public static void teleportPlayer(EntityPlayerMP player, EnumSet packetSet, World newWorld, double x, double y, double z, float pitch, float yaw, double motionX, double motionY, double motionZ) {
      int dimension = newWorld.provider.getDimension();
      int currentDimension = player.world.provider.getDimension();
      if (currentDimension != dimension) {
         if (!newWorld.isRemote) {
            if (currentDimension != 1) {
               player.mcServer.getPlayerList().transferPlayerToDimension(player, dimension, new PotionCoreTeleporter(player.mcServer.getWorld(dimension), x, y, z, pitch, yaw, motionX, motionY, motionZ));
            } else {
               forceTeleportPlayerFromEnd(player, dimension, new PotionCoreTeleporter(player.mcServer.getWorld(dimension), x, y, z, pitch, yaw, motionX, motionY, motionZ));
            }

            player.connection.sendPacket(new SPacketSetExperience(player.experience, player.experienceTotal, player.experienceLevel));
         }
      } else {
         if (packetSet != null) {
            player.connection.setPlayerLocation(x, y, z, yaw, pitch, packetSet);
         } else {
            player.connection.setPlayerLocation(x, y, z, yaw, pitch);
         }

         player.setRotationYawHead(yaw);
         player.motionX = motionX;
         player.motionY = motionY;
         player.motionZ = motionZ;
         player.connection.sendPacket(new SPacketEntityVelocity(player));
         newWorld.updateEntityWithOptionalForce(player, false);
      }

   }

   private static void forceTeleportPlayerFromEnd(EntityPlayerMP player, int newDimension, Teleporter colourfulTeleporter) {
      int j = player.dimension;
      WorldServer worldServerOld = player.mcServer.getWorld(player.dimension);
      player.dimension = newDimension;
      WorldServer worldServerNew = player.mcServer.getWorld(player.dimension);
      player.connection.sendPacket(new SPacketRespawn(player.dimension, player.world.getDifficulty(), player.world.getWorldInfo().getTerrainType(), player.interactionManager.getGameType()));
      worldServerOld.removeEntityDangerously(player);
      player.isDead = false;
      WorldProvider pOld = worldServerOld.provider;
      WorldProvider pNew = worldServerNew.provider;
      double moveFactor = pOld.getMovementFactor() / pNew.getMovementFactor();
      double d0 = player.posX * moveFactor;
      double d1 = player.posZ * moveFactor;
      float f = player.rotationYaw;
      worldServerOld.profiler.startSection("placing");
      d0 = (double)MathHelper.clamp((int)d0, -29999872, 29999872);
      d1 = (double)MathHelper.clamp((int)d1, -29999872, 29999872);
      if (player.isEntityAlive()) {
         player.setLocationAndAngles(d0, player.posY, d1, player.rotationYaw, player.rotationPitch);
         colourfulTeleporter.placeInPortal(player, f);
         worldServerNew.spawnEntity(player);
         worldServerNew.updateEntityWithOptionalForce(player, false);
      }

      worldServerOld.profiler.endSection();
      player.setWorld(worldServerNew);
      player.mcServer.getPlayerList().preparePlayer(player, worldServerOld);
      player.connection.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
      player.interactionManager.setWorld(worldServerNew);
      player.mcServer.getPlayerList().updateTimeAndWeatherForPlayer(player, worldServerNew);
      player.mcServer.getPlayerList().syncPlayerInventory(player);

      for(PotionEffect potioneffect : player.getActivePotionEffects()) {
         player.connection.sendPacket(new SPacketEntityEffect(player.getEntityId(), potioneffect));
      }

      FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, j, newDimension);
   }

   private static Entity transferEntityToDimension(Entity toTeleport, int newDimension, double x, double y, double z, float pitch, float yaw, double motionX, double motionY, double motionZ) {
      if (!toTeleport.isDead) {
         toTeleport.world.profiler.startSection("changeDimension");
         MinecraftServer minecraftserver = toTeleport.getServer();
         int oldDimension = toTeleport.dimension;
         WorldServer worldServerOld = minecraftserver.getWorld(oldDimension);
         WorldServer worldServerNew = minecraftserver.getWorld(newDimension);
         toTeleport.dimension = newDimension;
         toTeleport.world.removeEntity(toTeleport);
         toTeleport.isDead = false;
         toTeleport.world.profiler.startSection("reposition");
         if (oldDimension == 1) {
            forceTeleportEntityFromEnd(toTeleport, newDimension, new PotionCoreTeleporter(worldServerOld, x, y, z, pitch, yaw, motionX, motionY, motionZ), worldServerNew);
         } else {
            minecraftserver.getPlayerList().transferEntityToWorld(toTeleport, oldDimension, worldServerOld, worldServerNew, new PotionCoreTeleporter(worldServerOld, x, y, z, pitch, yaw, motionX, motionY, motionZ));
         }

         toTeleport.world.profiler.endStartSection("reloading");
         Entity entity = EntityList.createEntityByIDFromName(new ResourceLocation(EntityList.getEntityString(toTeleport)), worldServerNew);
         if (entity != null) {
            copyDataFromOld(entity, toTeleport);
            worldServerNew.spawnEntity(entity);
         }

         toTeleport.isDead = true;
         toTeleport.world.profiler.endSection();
         worldServerOld.resetUpdateEntityTick();
         worldServerNew.resetUpdateEntityTick();
         toTeleport.world.profiler.endSection();
         return entity;
      } else {
         return toTeleport;
      }
   }

   private static void copyDataFromOld(Entity newEntity, Entity oldEntity) {
      NBTTagCompound nbttagcompound = oldEntity.writeToNBT(new NBTTagCompound());
      nbttagcompound.removeTag("Dimension");
      newEntity.readFromNBT(nbttagcompound);
      newEntity.timeUntilPortal = oldEntity.timeUntilPortal;
      ObfuscationReflectionHelper.setPrivateValue(Entity.class, newEntity, ObfuscationReflectionHelper.getPrivateValue(Entity.class, oldEntity, new String[]{"lastPortalPos", "lastPortalPos"}), new String[]{"lastPortalPos", "lastPortalPos"});
      ObfuscationReflectionHelper.setPrivateValue(Entity.class, newEntity, ObfuscationReflectionHelper.getPrivateValue(Entity.class, oldEntity, new String[]{"lastPortalVec", "lastPortalVec"}), new String[]{"lastPortalVec", "lastPortalVec"});
      ObfuscationReflectionHelper.setPrivateValue(Entity.class, newEntity, ObfuscationReflectionHelper.getPrivateValue(Entity.class, oldEntity, new String[]{"teleportDirection", "teleportDirection"}), new String[]{"teleportDirection", "teleportDirection"});
   }

   private static void forceTeleportEntityFromEnd(Entity entity, int newDimension, Teleporter teleporter, WorldServer worldServerNew) {
      worldServerNew.spawnEntity(entity);
      entity.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
      worldServerNew.updateEntityWithOptionalForce(entity, false);
      teleporter.placeInPortal(entity, 0.0F);
      entity.setWorld(worldServerNew);
   }
}
