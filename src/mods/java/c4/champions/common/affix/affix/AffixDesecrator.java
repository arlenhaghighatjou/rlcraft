package c4.champions.common.affix.affix;

import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.config.ConfigHandler;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class AffixDesecrator extends AffixBase {
   public AffixDesecrator() {
      super("desecrator", AffixCategory.OFFENSE);
   }

   public void onSpawn(EntityLiving entity, IChampionship cap) {
      entity.tasks.addTask(0, new AffixDesecrator.AIAttack(entity));
   }

   public void onAttacked(EntityLiving entity, IChampionship cap, DamageSource source, float amount, LivingAttackEvent evt) {
      if (source.getImmediateSource() instanceof EntityAreaEffectCloud && source.getTrueSource() == entity) {
         evt.setCanceled(true);
      }

   }

   class AIAttack extends EntityAIBase {
      private final EntityLiving entity;
      private int attackTime;

      public AIAttack(EntityLiving entityLiving) {
         this.entity = entityLiving;
      }

      public boolean shouldExecute() {
         EntityLivingBase entitylivingbase = this.entity.getAttackTarget();
         return AffixBase.isValidAffixTarget(this.entity, entitylivingbase, true) && entitylivingbase.world.getDifficulty() != EnumDifficulty.PEACEFUL;
      }

      public void startExecuting() {
         this.attackTime = ConfigHandler.affix.desecrator.attackInterval;
      }

      public void updateTask() {
         if (this.entity.world.getDifficulty() != EnumDifficulty.PEACEFUL) {
            --this.attackTime;
            EntityLivingBase entitylivingbase = this.entity.getAttackTarget();
            if (entitylivingbase != null) {
               this.entity.getLookHelper().setLookPositionWithEntity(entitylivingbase, 180.0F, 180.0F);
               if (this.attackTime <= 0) {
                  this.attackTime = ConfigHandler.affix.desecrator.attackInterval + this.entity.getRNG().nextInt(5) * 10;
                  EntityAreaEffectCloud cloud = new EntityAreaEffectCloud(entitylivingbase.world, entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ);
                  cloud.setOwner(this.entity);
                  cloud.setRadius((float)ConfigHandler.affix.desecrator.cloudRadius);
                  cloud.setDuration(ConfigHandler.affix.desecrator.cloudDuration);
                  cloud.setRadiusOnUse(-0.5F);
                  cloud.setWaitTime(ConfigHandler.affix.desecrator.activationTicks);
                  cloud.setRadiusPerTick(-cloud.getRadius() / (float)cloud.getDuration());
                  cloud.addEffect(new PotionEffect(MobEffects.INSTANT_DAMAGE, 1, 1));
                  this.entity.world.spawnEntity(cloud);
               }
            }

            super.updateTask();
         }

      }
   }
}
