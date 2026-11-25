package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindAvoidTargetGoal;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityGorgomite extends BaseCreatureEntity implements IMob {
   private int swarmLimit = 5;

   public EntityGorgomite(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.ARTHROPOD;
      this.hasAttackSound = true;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(true));
      this.targetTasks.addTask(this.nextSpecialTargetIndex++, (new FindAvoidTargetGoal(this)).setTargetClass(EntityBalayang.class));
   }

   public void loadCreatureFlags() {
      this.swarmLimit = this.creatureInfo.getFlag("swarmLimit", this.swarmLimit);
   }

   public void onLivingUpdate() {
      if (!this.getEntityWorld().isRemote && this.hasAttackTarget() && this.getAttackTarget() instanceof EntityPlayer && this.updateTick % 60L == 0L) {
         this.allyUpdate();
      }

      super.onLivingUpdate();
   }

   public void allyUpdate() {
      if (!this.getEntityWorld().isRemote) {
         if (this.swarmLimit > 0 && this.countAllies(64.0D) < this.swarmLimit) {
            float random = this.rand.nextFloat();
            if (random <= 0.25F) {
               this.spawnAlly(this.posX - 2.0D + (double)(random * 4.0F), this.posY, this.posZ - 2.0D + (double)(random * 4.0F));
            }
         }

      }
   }

   public void spawnAlly(double x, double y, double z) {
      BaseCreatureEntity minion = (BaseCreatureEntity)this.creatureInfo.createEntity(this.getEntityWorld());
      minion.setLocationAndAngles(x, y, z, this.rand.nextFloat() * 360.0F, 0.0F);
      minion.setMinion(true);
      minion.applyVariant(this.getVariantIndex());
      this.getEntityWorld().spawnEntity(minion);
      if (this.getAttackTarget() != null) {
         minion.setRevengeTarget(this.getAttackTarget());
      }

   }

   public void onDeath(DamageSource par1DamageSource) {
      this.allyUpdate();
      super.onDeath(par1DamageSource);
   }

   public boolean canClimb() {
      return true;
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
      return type.equals("cactus") ? false : super.isDamageTypeApplicable(type, source, damage);
   }
}
