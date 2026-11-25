package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.block.BlockLog;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityCalpod extends BaseCreatureEntity implements IMob {
   private int swarmLimit = 5;
   private boolean griefing = true;

   public EntityCalpod(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.ARTHROPOD;
      this.hasAttackSound = true;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(true));
   }

   public void loadCreatureFlags() {
      this.swarmLimit = this.creatureInfo.getFlag("swarmLimit", this.swarmLimit);
      this.griefing = this.creatureInfo.getFlag("griefing", this.griefing);
   }

   public void onLivingUpdate() {
      if (!this.getEntityWorld().isRemote && this.hasAttackTarget() && this.getAttackTarget() instanceof EntityPlayer && this.updateTick % 60L == 0L) {
         this.allyUpdate();
      }

      if (!this.getEntityWorld().isRemote && this.getAttackTarget() != null && this.getEntityWorld().getGameRules().getBoolean("mobGriefing") && this.griefing) {
         float distance = this.getAttackTarget().getDistance(this);
         if (distance <= this.width + 1.0F) {
            this.destroyAreaBlock((int)this.posX, (int)this.posY, (int)this.posZ, BlockLog.class, true, 0);
         }
      }

      super.onLivingUpdate();
   }

   public void allyUpdate() {
      if (!this.getEntityWorld().isRemote) {
         if (this.swarmLimit > 0 && this.countAllies(64.0D) < this.swarmLimit) {
            float random = this.rand.nextFloat();
            if (random <= 0.125F) {
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

   public float getFallResistance() {
      return 100.0F;
   }
}
