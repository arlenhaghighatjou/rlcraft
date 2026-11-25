package com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent;

import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGiveEffectsArea;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPreeminent;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.util.DamageSource;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityVesta extends EntityPPreeminent implements EntityBodyParts {
   private EntityBody head;

   public EntityVesta(World worldIn) {
      super(worldIn);
      this.setSize(1.75F, 3.6F);
      this.stepHeight = 1.0F;
      this.borderOrb = -1;
      this.canTeleportToo = true;
      this.ignoreFrustumCheck = true;
      this.type = 31;
      this.head = new EntityBody(this, 3.8F, 3.8F, 1.0F, 3.1F, 1.6F, 1, 1, false, 0.2F);
   }

   public int getParasiteIDRegister() {
      return 88;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(0, new EntityAISwimmingDiving(this, 0.15D));
      this.tasks.addTask(2, new EntityAIWaterLeapAtTargetStatus(this, 0.7F, 1.5D, 3, 20, 0));
      this.tasks.addTask(3, new EntityAIAttackMeleeStatus(this, 1.3D, false, 8.0D, 10));
      this.tasks.addTask(6, new EntityAIGetFollowers(this, 2, 16));
      this.tasks.addTask(3, new EntityAIGiveEffectsArea(this, SRPAttributes.VESTA_CD, SRPAttributes.VESTA_RANGE, SRPConfigMobs.vestaeffects));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.VESTA_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.VESTA_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.242D);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(2.0D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.VESTA_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.preeminentFollow);
   }

   protected void entityInit() {
      super.entityInit();
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      this.head.onUpdate();
      if (this.srpTicks == 25) {
         AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.getPosition())).grow(32.0D);

         for(EntityParasiteBase mob : this.world.getEntitiesWithinAABB(EntityParasiteBase.class, axisalignedbb)) {
            SRPPotions.applyStackPotion(SRPPotions.LINK_E, mob, 6666, 0);
            SRPPotions.applyStackPotion(SRPPotions.FOSTER_E, mob, 6666, 0);
         }
      }

   }

   public boolean attackEntityBodyFrom(DamageSource source, float amount, int id, boolean notify) {
      if (this.rand.nextBoolean()) {
         SRPPotions.applyStackPotion(SRPPotions.BLEED_E, this, 80, 0);
      }

      return this.attackEntityFrom(source, amount * 3.0F);
   }

   public void setBodyPartDead(int id) {
   }

   public void setDead() {
      if (this.head != null) {
         this.world.removeEntityDangerously(this.head);
      }

      super.setDead();
   }

   public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
      boolean flag = super.scaryOrbEffect(in, mobs);
      if (flag) {
         ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.vestaOrbEffects, mobs);
      }

      return flag;
   }

   public float getEyeHeight() {
      return 1.5F;
   }

   public static void registerFixesBano(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityVesta.class);
   }

   public void onKillEntity(EntityLivingBase entityLivingIn) {
      super.onKillEntity(entityLivingIn);
      this.particleStatus((byte)5);
   }

   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      IEntityLivingData floo = super.onInitialSpawn(difficulty, livingdata);
      if (this.rand.nextDouble() < SRPConfig.variantChance) {
         switch(2) {
         case 2:
            this.setSkin(1);
            this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.VESTA_ARMOR * 1.5D);
            this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.1694D);
         }
      }

      return floo;
   }
}
