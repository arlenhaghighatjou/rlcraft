package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.debug.DebugUtil;
import com.charles445.rltweaker.util.ErrorUtil;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MotionCheckHandler {
   public Set<UUID> rotationErrors = new HashSet();
   public Set<UUID> motionErrors = new HashSet();

   public MotionCheckHandler() {
      MinecraftForge.EVENT_BUS.register(this);
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onLivingUpdateHighest(LivingUpdateEvent event) {
      this.handleLivingUpdate(event, EventPriority.HIGHEST);
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onLivingUpdateLowest(LivingUpdateEvent event) {
      this.handleLivingUpdate(event, EventPriority.LOWEST);
   }

   private void handleLivingUpdate(LivingUpdateEvent event, EventPriority priority) {
      if (ModConfig.server.minecraft.motionChecker) {
         EntityLivingBase entity = event.getEntityLiving();
         World world = entity.getEntityWorld();
         if (!world.isRemote) {
            if (this.testMotion(entity.motionX, entity, priority)) {
               entity.motionX = 0.0D;
               event.setCanceled(true);
               entity.velocityChanged = true;
            }

            if (this.testMotion(entity.motionY, entity, priority)) {
               entity.motionY = 0.0D;
               event.setCanceled(true);
               entity.velocityChanged = true;
            }

            if (this.testMotion(entity.motionZ, entity, priority)) {
               entity.motionZ = 0.0D;
               event.setCanceled(true);
               entity.velocityChanged = true;
            }

            if (this.testAngle(entity.rotationPitch, entity, priority)) {
               this.fixAngle(entity);
            } else if (this.testAngle(entity.rotationYaw, entity, priority)) {
               this.fixAngle(entity);
            }

         }
      }
   }

   private void fixAngle(EntityLivingBase entity) {
      entity.rotationPitch = 0.0F;
      entity.rotationYaw = 0.0F;
   }

   private boolean testAngle(float angle, EntityLivingBase entity, EventPriority priority) {
      if (!Float.isFinite(angle)) {
         if (!this.rotationErrors.contains(entity.getUniqueID())) {
            this.rotationErrors.add(entity.getUniqueID());
            RLTweaker.logger.error("Entity has bad rotation! " + priority.name() + " " + angle + " " + this.dumpEntity(entity));
            ErrorUtil.logSilent("Motion Checker Angle " + priority.name());
            if (ModConfig.server.minecraft.debug) {
               DebugUtil.messageAll("Entity has bad angles! " + priority.name() + " " + angle);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean testMotion(double motion, Entity entity, EventPriority priority) {
      if (motion <= ModConfig.server.minecraft.motionCheckerSpeedCap && motion >= -ModConfig.server.minecraft.motionCheckerSpeedCap && Double.isFinite(motion)) {
         return false;
      } else {
         if (!this.motionErrors.contains(entity.getUniqueID())) {
            this.motionErrors.add(entity.getUniqueID());
            RLTweaker.logger.error("Entity moving too fast! " + priority.name() + " " + motion + " " + this.dumpEntity(entity));
            ErrorUtil.logSilent("Motion Checker Speed " + priority.name());
            if (ModConfig.server.minecraft.debug) {
               DebugUtil.messageAll("Entity moving too fast! " + priority.name() + " " + motion);
            }
         }

         return true;
      }
   }

   private String dumpEntity(Entity entity) {
      StringBuilder sb = new StringBuilder();
      sb.append(entity.getClass().getName());
      sb.append(" - ");
      sb.append(entity.posX);
      sb.append(" ");
      sb.append(entity.posY);
      sb.append(" ");
      sb.append(entity.posZ);
      sb.append(" ");
      sb.append(entity.motionX);
      sb.append(" ");
      sb.append(entity.motionY);
      sb.append(" ");
      sb.append(entity.motionZ);
      if (entity instanceof EntityLivingBase) {
         sb.append(" hurt time:");
         sb.append(((EntityLivingBase)entity).hurtTime);
      }

      sb.append(" dead:");
      sb.append(entity.isDead);
      sb.append(" pitch:");
      sb.append(entity.rotationPitch);
      sb.append(" yaw:");
      sb.append(entity.rotationYaw);
      if (entity instanceof EntityLiving) {
         EntityLiving living = (EntityLiving)entity;
         DamageSource damagesource = living.getLastDamageSource();
         if (damagesource != null) {
            sb.append(" damagesource class:");
            sb.append(damagesource.getClass().getName());
         }

         EntityLivingBase revenge = living.getRevengeTarget();
         if (revenge != null) {
            sb.append(" revenge target class:");
            sb.append(revenge.getClass().getName());
         }

         EntityLivingBase attack = living.getAttackTarget();
         if (attack != null) {
            sb.append(" attack target class:");
            sb.append(attack.getClass().getName());
         }
      }

      return sb.toString();
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onKnockback(LivingKnockBackEvent event) {
      if (ModConfig.server.minecraft.motionChecker) {
         if (event.getRatioX() == 0.0D && event.getRatioZ() == 0.0D) {
            ErrorUtil.logSilent("Motion Checker Knockback Same Position");
            event.setCanceled(true);
         }

      }
   }
}
