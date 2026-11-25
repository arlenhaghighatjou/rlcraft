package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public abstract class EntityPAncient extends EntityPMalleable {
   public EntityPAncient(World worldIn) {
      super(worldIn);
      this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus(this, EntityPlayer.class, 0, !SRPConfigSystems.useOneMind, false, (Predicate)null, SRPConfig.preeminentSneakPen, SRPConfig.preeminentInviPen));
      if (SRPConfig.mobattacking) {
         this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus(this, EntityLiving.class, 0, !SRPConfigSystems.useOneMind, !SRPConfigSystems.useOneMind, new Predicate<EntityLiving>() {
            public boolean apply(@Nullable EntityLiving entity) {
               return !(entity instanceof EntityWaterMob) && !(entity instanceof EntityAnimal) && !(entity instanceof EntityVillager) && !ParasiteEventEntity.checkEntity(entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
            }
         }, SRPConfig.preeminentSneakPen, SRPConfig.preeminentInviPen));
      }

      this.experienceValue = SRPAttributes.XP_ADAPTED;
      this.canD = SRPConfig.ancientdespawn;
      this.damageCap = SRPConfig.ancientCap;
      this.canModRender = 1;
      this.killcount = -10.0D;
      this.ignoreFrustumCheck = true;
      this.pointCap = SRPConfig.ancientPointCap;
      this.pointReduction = SRPConfig.ancientPointRed;
      this.chanceLearn = SRPConfig.ancientChanceLe;
      this.chanceLearnFire = SRPConfig.ancientChanceLeFire;
      this.DamageTypeCap = SRPConfig.ancientPointDamCap;
      this.MiniDamage = SRPConfig.ancientMinDamage;
      this.regen = SRPConfig.ancientRegen * SRPConfig.globalHealthMultiplier;
      this.oneMindDeathValue = SRPConfig.ancientOneMindDeathV;
      this.valueEvDeath = SRPConfig.ancientLoosingEPValue;
   }

   protected void fearPlayer(EntityLivingBase player) {
      try {
         if (player == null) {
            return;
         }

         if (!this.canEntityBeSeen(player)) {
            return;
         }

         if (!player.isPotionActive(SRPPotions.FEAR_E)) {
            player.addPotionEffect(new PotionEffect(SRPPotions.FEAR_E, 300, 3, false, false));
         } else if (player.getActivePotionEffect(SRPPotions.FEAR_E).getAmplifier() < 3) {
            player.addPotionEffect(new PotionEffect(SRPPotions.FEAR_E, 300, 3, false, false));
         }
      } catch (Exception var3) {
         ;
      }

   }

   public boolean attackEntityAsMob(Entity entityIn) {
      boolean flag = super.attackEntityAsMob(entityIn);
      if (flag && entityIn instanceof EntityLivingBase && this.rand.nextDouble() < 1.0D) {
         EntityLivingBase target = (EntityLivingBase)entityIn;
         if (!target.isPotionActive(SRPPotions.COTH_E)) {
            if (target.isPotionActive(SRPPotions.EPEL_E)) {
               if (this.rand.nextDouble() >= SRPConfigSystems.epelChanceCOTH) {
                  target.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
               }
            } else {
               target.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
            }
         }
      }

      return flag;
   }

   public void fall(float distance, float damageMultiplier) {
      if (distance >= 100.0F) {
         super.fall(distance, damageMultiplier);
      }

   }

   public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
      boolean flag = super.scaryOrbEffect(in, mobs);
      if (flag) {
         ;
      }

      return flag;
   }
}
