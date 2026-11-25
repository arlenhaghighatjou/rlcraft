package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.LevelUpTwoReflect;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LevelUpTwoHandler {
   LevelUpTwoReflect reflector;

   public LevelUpTwoHandler() {
      try {
         this.reflector = new LevelUpTwoReflect();
         CompatUtil.wrapSpecificHandler("LUStealthDamage", (x$0) -> {
            new LevelUpTwoHandler.LUStealthDamage(x$0);
         }, "levelup2.skills.combat.StealthDamage", "onDamage");
         CompatUtil.wrapSpecificHandler("LUSwordCrit", (x$0) -> {
            new LevelUpTwoHandler.LUSwordCrit(x$0);
         }, "levelup2.skills.combat.SwordCritBonus", "onHurting");
         CompatUtil.wrapSpecificHandler("LUSwordDamage", (x$0) -> {
            new LevelUpTwoHandler.LUSwordDamage(x$0);
         }, "levelup2.skills.combat.SwordDamageBonus", "onHurting");
         CompatUtil.wrapSpecificHandler("LUXPBonus", (x$0) -> {
            new LevelUpTwoHandler.LUXPBonus(x$0);
         }, "levelup2.skills.combat.XPBonusCombat", "getCombatBonus");
         CompatUtil.wrapSpecificHandler("LUStealthTarget", (x$0) -> {
            new LevelUpTwoHandler.LUStealthTarget(x$0);
         }, "levelup2.skills.combat.StealthBonus", "onTargetSet");
      } catch (Exception var2) {
         RLTweaker.logger.error("Failed to setup LevelUpTwoHandler!", var2);
         ErrorUtil.logSilent("LevelUpTwo Critical Setup Failure");
         if (var2 instanceof CriticalException) {
            throw new RuntimeException(var2);
         }
      }

   }

   private boolean isSourceDisallowed(DamageSource source) {
      if (source == null) {
         return false;
      } else {
         Entity immediateSrc = source.getImmediateSource();
         if (immediateSrc == null) {
            return false;
         } else {
            Entity trueSrc = source.getTrueSource();
            if (trueSrc == null) {
               return false;
            } else {
               return !ModConfig.server.leveluptwo.petsUseSkills && immediateSrc != trueSrc && immediateSrc instanceof EntityLivingBase && trueSrc instanceof EntityPlayer && !(immediateSrc instanceof EntityPlayer);
            }
         }
      }
   }

   public class LUStealthDamage {
      private IEventListener handler;

      public LUStealthDamage(IEventListener handler) {
         this.handler = handler;
         MinecraftForge.EVENT_BUS.register(this);
      }

      @SubscribeEvent
      public void onLivingHurt(LivingHurtEvent event) {
         if (!LevelUpTwoHandler.this.isSourceDisallowed(event.getSource())) {
            this.handler.invoke(event);
         }
      }
   }

   public class LUStealthTarget {
      private IEventListener handler;
      private Object skill;

      public LUStealthTarget(IEventListener handler) {
         this.handler = handler;
         this.skill = CompatUtil.getSubscriberInstance(handler);
         if (this.skill == null) {
            if (ModConfig.server.leveluptwo.stealthOverhaul) {
               RLTweaker.logger.error("Could not find StealthBonus subscriber instance! LevelUp2 Stealth Overhaul is disabled");
            }
         } else if (!this.skill.getClass().getName().equals("levelup2.skills.combat.StealthBonus")) {
            RLTweaker.logger.error("Subscriber instance of LUStealthTarget was the wrong class!: " + this.skill.getClass().getName());
            this.skill = null;
         } else {
            RLTweaker.logger.info("Gathered subscriber instance of class: " + this.skill.getClass().getName());
         }

         MinecraftForge.EVENT_BUS.register(this);
      }

      @SubscribeEvent
      public void onSetAttackTarget(LivingSetAttackTargetEvent event) {
         if (this.skill != null && ModConfig.server.leveluptwo.stealthOverhaul) {
            try {
               if (!LevelUpTwoHandler.this.reflector.skillIsActive(this.skill)) {
                  return;
               }

               if (event.getTarget() instanceof EntityPlayer && event.getEntityLiving() instanceof EntityLiving && this.isLivingStealth((EntityLiving)event.getEntityLiving())) {
                  EntityPlayer player = (EntityPlayer)event.getTarget();
                  EntityLiving mob = (EntityLiving)event.getEntityLiving();
                  if (mob.getRevengeTarget() == player) {
                     return;
                  }

                  if (!player.isSneaking()) {
                     return;
                  }

                  if (mob.getRevengeTimer() == mob.ticksExisted) {
                     return;
                  }

                  int skillLevel = LevelUpTwoHandler.this.reflector.getSkillLevel(player, "levelup:stealth");
                  if (skillLevel <= 0) {
                     return;
                  }

                  if ((float)MathHelper.floor(player.getDistanceSq(mob)) > this.calculateStealthDistance(skillLevel)) {
                     mob.setAttackTarget((EntityLivingBase)null);
                  }
               }
            } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var5) {
               ErrorUtil.logSilent("LevelUp2 Stealth Overhaul Invocation");
               this.handler.invoke(event);
            }

         } else {
            this.handler.invoke(event);
         }
      }

      private boolean isLivingStealth(EntityLiving living) {
         if (living instanceof EntityMob) {
            return true;
         } else {
            return LevelUpTwoHandler.this.reflector.isLycanitesAvailable() && ModConfig.server.leveluptwo.stealthOverhaulLycanites && LevelUpTwoHandler.this.reflector.canLycanitesStealth(living);
         }
      }

      private float calculateStealthDistance(int skillLevel) {
         float base = (float)ModConfig.server.leveluptwo.stealthOverhaulBaseDistance;
         float per = (float)ModConfig.server.leveluptwo.stealthOverhaulDistancePerLevel * (float)skillLevel;
         float clamped = Math.max(0.0F, base - per);
         return clamped * clamped;
      }
   }

   public class LUSwordCrit {
      private IEventListener handler;

      public LUSwordCrit(IEventListener handler) {
         this.handler = handler;
         MinecraftForge.EVENT_BUS.register(this);
      }

      @SubscribeEvent
      public void onLivingHurt(LivingHurtEvent event) {
         if (!LevelUpTwoHandler.this.isSourceDisallowed(event.getSource())) {
            this.handler.invoke(event);
         }
      }
   }

   public class LUSwordDamage {
      private IEventListener handler;

      public LUSwordDamage(IEventListener handler) {
         this.handler = handler;
         MinecraftForge.EVENT_BUS.register(this);
      }

      @SubscribeEvent
      public void onLivingHurt(LivingHurtEvent event) {
         if (!LevelUpTwoHandler.this.isSourceDisallowed(event.getSource())) {
            this.handler.invoke(event);
         }
      }
   }

   public class LUXPBonus {
      private IEventListener handler;

      public LUXPBonus(IEventListener handler) {
         this.handler = handler;
         MinecraftForge.EVENT_BUS.register(this);
      }

      @SubscribeEvent
      public void onLivingDeath(LivingDeathEvent event) {
         if (!LevelUpTwoHandler.this.isSourceDisallowed(event.getSource())) {
            this.handler.invoke(event);
         }
      }
   }
}
