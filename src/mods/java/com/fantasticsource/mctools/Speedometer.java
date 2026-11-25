package com.fantasticsource.mctools;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class Speedometer {
   public static Map<EntityPlayerMP, Speedometer.Entry> playerMap = new LinkedHashMap(1000);
   public static Map<EntityLiving, Integer> recentTeleports = new LinkedHashMap(10);

   public Speedometer() {
   }

   @SubscribeEvent
   public static void playerUpdateEvent(PlayerTickEvent event) {
      EntityPlayer player = event.player;
      if (event.phase == Phase.END && player instanceof EntityPlayerMP) {
         updatePlayer((EntityPlayerMP)player);
      }

   }

   @SubscribeEvent
   public static void teleport(EnderTeleportEvent event) {
      EntityLivingBase livingBase = event.getEntityLiving();
      if (livingBase instanceof EntityPlayerMP) {
         remove((EntityPlayerMP)livingBase);
      } else if (livingBase instanceof EntityLiving) {
         recentTeleports.put((EntityLiving)livingBase, Integer.valueOf(3));
      }

   }

   @SubscribeEvent
   public static void dimChange(EntityTravelToDimensionEvent event) {
      Entity entity = event.getEntity();
      if (entity instanceof EntityPlayerMP) {
         remove((EntityPlayerMP)entity);
      } else if (entity instanceof EntityLiving) {
         recentTeleports.put((EntityLiving)entity, Integer.valueOf(3));
      }

   }

   @SubscribeEvent
   public static void playerLogout(PlayerLoggedOutEvent event) {
      if (event.player instanceof EntityPlayerMP) {
         remove((EntityPlayerMP)event.player);
      }

   }

   @SubscribeEvent
   public static void serverTick(ServerTickEvent event) {
      recentTeleports.entrySet().removeIf(Speedometer::checkRemoveTeleporting);
   }

   private static boolean checkRemoveTeleporting(java.util.Map.Entry<EntityLiving, Integer> entry) {
      int time = ((Integer)entry.getValue()).intValue();
      --time;
      if (time >= 0) {
         return true;
      } else {
         entry.setValue(Integer.valueOf(time));
         return false;
      }
   }

   private static void remove(EntityPlayerMP player) {
      playerMap.remove(player);
   }

   public static double getSpeed(Entity entity) {
      if (entity instanceof EntityPlayerMP) {
         Speedometer.Entry entry = (Speedometer.Entry)playerMap.get(entity);
         return entry == null ? 0.0D : entry.calculatedSpeed;
      } else {
         return recentTeleports.containsKey(entity) ? 0.0D : (new Vec3d(entity.prevPosX, entity.prevPosY, entity.prevPosZ)).distanceTo(entity.getPositionVector()) * 20.0D;
      }
   }

   public static double updatePlayer(EntityPlayerMP player) {
      Vec3d newVec = player.getPositionVector();
      Speedometer.Entry entry = (Speedometer.Entry)playerMap.get(player);
      if (entry == null) {
         playerMap.put(player, new Speedometer.Entry(newVec));
         return 0.0D;
      } else {
         entry.set(newVec, entry.position.distanceTo(newVec) * 20.0D);
         return entry.calculatedSpeed;
      }
   }

   public static class Entry {
      public Vec3d position;
      public double calculatedSpeed = 0.0D;
      private double[] speeds = new double[]{0.0D, 0.0D, 0.0D, 0.0D, 0.0D};

      public Entry(Vec3d position) {
         this.position = position;
      }

      public void set(Vec3d position, double speed) {
         this.position = position;
         this.calculatedSpeed = 0.0D;

         for(int i = this.speeds.length - 1; i > 0; --i) {
            this.speeds[i] = this.speeds[i - 1];
            this.calculatedSpeed += this.speeds[i];
         }

         this.speeds[0] = speed;
         this.calculatedSpeed = (this.calculatedSpeed + speed) / (double)this.speeds.length;
      }
   }
}
