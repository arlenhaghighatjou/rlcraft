package c4.champions.common.affix.affix;

import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.config.ConfigHandler;
import c4.champions.common.entity.EntityCinderSpark;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.EnumDifficulty;

public class AffixCinder extends AffixBase {
   public AffixCinder() {
      super("cinder", AffixCategory.OFFENSE);
   }

   public void onSpawn(EntityLiving entity, IChampionship cap) {
      entity.tasks.addTask(0, new AffixCinder.AIAttack(entity));
   }

   static class AIAttack extends EntityAIBase {
      private final EntityLiving entity;
      private int attackTime;

      AIAttack(EntityLiving entity) {
         this.entity = entity;
      }

      public boolean shouldExecute() {
         EntityLivingBase entitylivingbase = this.entity.getAttackTarget();
         return AffixBase.isValidAffixTarget(this.entity, entitylivingbase, false) && entitylivingbase.world.getDifficulty() != EnumDifficulty.PEACEFUL;
      }

      public void startExecuting() {
         this.attackTime = ConfigHandler.affix.cinder.attackInterval;
      }

      public void updateTask() {
         if (this.entity.world.getDifficulty() != EnumDifficulty.PEACEFUL) {
            --this.attackTime;
            EntityLivingBase entitylivingbase = this.entity.getAttackTarget();
            if (entitylivingbase != null) {
               this.entity.getLookHelper().setLookPositionWithEntity(entitylivingbase, 180.0F, 180.0F);
               if (this.attackTime <= 0) {
                  this.attackTime = ConfigHandler.affix.cinder.attackInterval + this.entity.getRNG().nextInt(5) * 10;
                  EntityCinderSpark spark = new EntityCinderSpark(this.entity.world, this.entity, entitylivingbase, this.entity.getHorizontalFacing().getAxis());
                  this.entity.world.spawnEntity(spark);
                  this.entity.playSound(SoundEvents.ENTITY_SHULKER_SHOOT, 2.0F, (this.entity.getRNG().nextFloat() - this.entity.getRNG().nextFloat()) * 0.2F + 1.0F);
               }
            }
         }

      }
   }
}
