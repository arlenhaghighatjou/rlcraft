package com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus;

import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIBlockInfest;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIVenkrolSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationaryArchitect;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.world.World;

public class EntityVenkrolSV extends EntityPStationaryArchitect {
   public EntityAIVenkrolSummon summonV = new EntityAIVenkrolSummon(this, SRPConfigMobs.venkrollimit, 20 * SRPConfigMobs.venkrolCooldown, 1, SRPConfigMobs.venkrolCAMinimumV, (double)SRPConfigMobs.venkrolCAExtraM);
   private EntityAIBlockInfest infestation = new EntityAIBlockInfest(this, 2);

   public EntityVenkrolSV(World worldIn) {
      super(worldIn);
      this.setSize(1.0F, 8.8F);
      this.ignoreFrustumCheck = true;
      this.totalP = SRPConfigMobs.venkrolTotalActiveMobs;
      this.mobID = new int[this.totalP + SRPConfigMobs.venkrollimit];
      this.mobPT = new int[this.totalP + SRPConfigMobs.venkrollimit];
      this.stage = 3;
      this.experienceValue = SRPAttributes.XP_ADAPTED * 2;
      if (SRPAttributes.rsBlockI) {
         this.tasks.addTask(3, this.infestation);
      }

      for(int i = 0; i < this.mobID.length; ++i) {
         this.mobID[i] = -777;
      }

      this.setBODY(1.0F);
      this.tasks.addTask(2, this.summonV);
   }

   public int getParasiteIDRegister() {
      return 42;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.VENKROLSII_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.VENKROLSII_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.VENKROLSII_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(24.0D);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      this.freeDead();
      if (this.getParasiteStatus() == 0) {
         this.setBODY(0.04F);
      } else {
         this.setBODY(-0.04F);
      }

   }

   public float getEyeHeight() {
      return 8.5F;
   }

   public void setBODY(float in) {
      in = in + this.getBODY();
      if (in > 0.5F) {
         in = 0.5F;
      }

      if (in < 0.0F) {
         in = 0.0F;
      }

      this.body = in;
   }

   public float getBombDamage() {
      return (float)SRPAttributes.VENKROLSIV_ATTACK_DAMAGE;
   }
}
