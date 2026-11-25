package com.dhanantry.scapeandrunparasites.entity.monster.deterrent;

import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatusAOE;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityWave;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityTonro extends EntityPStationary implements EntityCutomAttack {
   private float attackTimer;
   private boolean up;
   private int border;
   private boolean skillshockwave;

   public EntityTonro(World worldIn) {
      super(worldIn);
      this.setSize(0.7F, 4.5F);
      this.experienceValue = SRPAttributes.XP_ADAPTED * 2;
      this.type = 40;
      this.buriedT = 7.5D;
      this.damageCap = SRPConfig.turretCap;
      this.pointCap = SRPConfig.turretPointCap;
      this.pointReduction = SRPConfig.turretPointRed;
      this.chanceLearn = SRPConfig.turretChanceLe;
      this.chanceLearnFire = SRPConfig.turretChanceLeFire;
      this.DamageTypeCap = SRPConfig.turretPointDamCap;
      this.MiniDamage = SRPConfig.turretMinDamage;
      this.regen = SRPConfig.turretRegen;
   }

   public int getParasiteIDRegister() {
      return 29;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(3, new EntityAIAttackMeleeStatusAOE(this, 0.0D, false, 8.0D, 7.0D));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.tasks.addTask(2, new EntityAISkill(this, 20, 16, 1, true, 1));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.TONRO_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.TONRO_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.TONRO_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.up) {
         this.attackTimer = (float)((double)this.attackTimer + 0.15D);
         if (this.attackTimer > 1.0F) {
            this.up = false;
         }
      } else {
         this.attackTimer = (float)((double)this.attackTimer - 0.2D);
      }

   }

   public boolean attackEntityAsMob(Entity entityIn) {
      boolean flag = super.attackEntityAsMob(entityIn);
      if (flag) {
         entityIn.motionY += 0.5000000059604645D;
      }

      return flag;
   }

   public boolean attackEntityAsMobAOE(Entity entityIn) {
      this.up = true;
      this.attackTimer = 0.0F;
      this.world.setEntityState(this, (byte)12);
      boolean flag = false;
      this.playSound(SRPSounds.SWIPE, 3.0F, 1.0F);
      AxisAlignedBB axisalignedbb = (new AxisAlignedBB(entityIn.posX, entityIn.posY, entityIn.posZ, entityIn.posX + 1.0D, entityIn.posY + 1.0D, entityIn.posZ + 1.0D)).grow(2.0D);

      for(EntityLivingBase mob : this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb)) {
         if (mob != this && !(mob instanceof EntityParasiteBase) && this.canEntityBeSeen(mob) && this.attackEntityAsMob(mob)) {
            flag = true;
         }
      }

      return flag;
   }

   public float getEyeHeight() {
      return 3.8F;
   }

   public static void registerFixesTonro(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityTonro.class);
   }

   @SideOnly(Side.CLIENT)
   public float getAttackTimer() {
      return this.attackTimer;
   }

   @SideOnly(Side.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 12) {
         this.up = true;
         this.attackTimer = 0.0F;
      } else if (id == 100) {
         for(int i = 0; i <= 1; ++i) {
            this.spawnParticles(EnumParticleTypes.FLAME);
         }
      } else {
         super.handleStatusUpdate(id);
      }

   }

   public boolean getFinished(byte attID) {
      switch(attID) {
      case 1:
         return this.skillshockwave;
      default:
         return super.getFinished(attID);
      }
   }

   public void setFinished(byte attID, boolean in) {
      switch(attID) {
      case 1:
         this.skillshockwave = in;
         return;
      default:
         super.setFinished(attID, in);
      }
   }

   public void doSpecialSkill(byte id) {
      switch(id) {
      case 1:
         this.shockwave();
         return;
      default:
         super.doSpecialSkill(id);
      }
   }

   private void shockwave() {
      this.setParasiteStatus(10);
      this.getNavigator().clearPath();
      if (this.border == 0) {
         float v = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F + 2.0F;
         this.playSound(this.getHurtSound(DamageSource.GENERIC), 4.0F, v);
         ++this.border;
      } else {
         if (this.border <= 2) {
            this.world.setEntityState(this, (byte)100);
         }

         if (this.ticksExisted % 20 == 0) {
            ++this.border;
            if (this.getAttackTarget() == null) {
               this.skillshockwave = true;
               this.setParasiteStatus(0);
               this.border = 0;
            } else if (this.getAttackTarget().posY != this.posY) {
               this.skillshockwave = true;
               this.setParasiteStatus(0);
               this.border = 0;
            } else {
               if (this.border == 3) {
                  this.spawnShock();
               }

               if (this.border == 5) {
                  this.spawnShock();
               }

               if (this.border > 6) {
                  this.skillshockwave = true;
                  this.setParasiteStatus(0);
                  this.border = 0;
               }

            }
         }
      }
   }

   private void spawnShock() {
      EntityWave wa = new EntityWave(this.world);
      float f19 = MathHelper.sin(this.rotationYaw * 0.017453292F - this.randomYawVelocity * 0.01F);
      float f14 = 0.17453292F;
      float f16 = MathHelper.cos(f14);
      float f4 = MathHelper.cos(this.rotationYaw * 0.017453292F - this.randomYawVelocity * 0.01F);
      wa.setPositionAndUpdate(this.posX + -1.0D * (double)(f19 * 2.0F * f16), this.posY, this.posZ - -1.0D * (double)(f4 * 2.0F * f16));
      if (!wa.world.getCollisionBoxes(wa, wa.getEntityBoundingBox()).isEmpty()) {
         wa.setDead();
         this.skillshockwave = true;
         this.setParasiteStatus(0);
         this.border = 0;
      } else {
         wa.setDamages(this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() * 0.3D, this.MiniDamage, 1, 12);
         this.world.spawnEntity(wa);
         wa.setAttackTarget(this.getAttackTarget());
      }
   }
}
