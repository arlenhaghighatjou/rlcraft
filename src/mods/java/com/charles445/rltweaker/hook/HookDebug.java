package com.charles445.rltweaker.hook;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.debug.DebugUtil;
import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

public class HookDebug {
   public static Set<String> traceWatcher = Sets.newConcurrentHashSet();

   public HookDebug() {
   }

   public static void printObject(Object o) {
      if (o != null) {
         String s = o.toString();
         RLTweaker.logger.debug(s);
         DebugUtil.messageAll(s);
      }
   }

   public static void printFloat(float f) {
      String s = "" + f;
      DebugUtil.messageAll(s);
   }

   public static void checkUpdateRotation(float original, float f, float delta) {
      if (!Float.isFinite(original)) {
         DebugUtil.messageAll("original is non finite");
      }

      if (!Float.isFinite(f)) {
         DebugUtil.messageAll("f is non finite");
      }

      if (!Float.isFinite(delta)) {
         DebugUtil.messageAll("delta is non finite");
      }

   }

   public static void checkHelperUpdateRotation(double helperX, double helperY, double helperZ, EntityLiving entity) {
      boolean isBroken = false;
      if (!Double.isFinite(helperX)) {
         DebugUtil.messageAll("helperX is non finite");
         isBroken = true;
      }

      if (!Double.isFinite(helperY)) {
         DebugUtil.messageAll("helperY is non finite");
         isBroken = true;
      }

      if (!Double.isFinite(helperZ)) {
         DebugUtil.messageAll("helperZ is non finite");
         isBroken = true;
      }

      if (!Double.isFinite(entity.posX)) {
         DebugUtil.messageAll("targetX is non finite");
         isBroken = true;
      }

      if (!Double.isFinite(entity.posY)) {
         DebugUtil.messageAll("targetY is non finite");
         isBroken = true;
      }

      if (!Double.isFinite(entity.posZ)) {
         DebugUtil.messageAll("targetZ is non finite");
         isBroken = true;
      }

      if (isBroken) {
         DebugUtil.messageAll("Entity Current Location" + entity.posX + " " + entity.posY + " " + entity.posZ);
         if (entity.getAttackTarget() != null) {
            DebugUtil.messageAll("Target Current Location" + entity.getAttackTarget().posX + " " + entity.getAttackTarget().posY + " " + entity.getAttackTarget().posZ);
         }
      }

   }

   public static void checkEntityDead(Entity entity) {
      if (entity instanceof EntityLiving) {
         EntityLiving living = (EntityLiving)entity;
         if (living.isNoDespawnRequired()) {
            DebugUtil.messageAll("Persistent entity set dead: " + entity.getPosition().toString());
            (new Throwable()).printStackTrace();
         }
      }

   }

   public static void traceWatcher() {
      boolean displayTrace = false;

      for(StackTraceElement element : Thread.currentThread().getStackTrace()) {
         String ste = element.toString();
         if (!traceWatcher.contains(ste)) {
            displayTrace = true;
            System.out.println("New Trace: " + ste);
            traceWatcher.add(ste);
         }
      }

      if (displayTrace) {
         (new Throwable()).printStackTrace();
      }

   }
}
