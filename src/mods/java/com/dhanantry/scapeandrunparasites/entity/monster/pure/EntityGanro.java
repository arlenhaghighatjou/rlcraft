package com.dhanantry.scapeandrunparasites.entity.monster.pure;

import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatusAOE;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIEvadeDash;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPure;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.PathNavigateClimberStatus;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityGanro extends EntityPPure implements EntityCutomAttack, EntityBodyParts {
   private float attackTimer;
   private boolean up;
   public EntityBody tendril1;
   public EntityBody tendril2;
   private byte left1;
   private byte right2;
   private static final DataParameter<Byte> CLIMBING = EntityDataManager.createKey(EntityGanro.class, DataSerializers.BYTE);
   private int attacking;
   private double targetX;
   private double targetY;
   private double targetZ;
   private boolean skillCharge;

   public EntityGanro(World worldIn) {
      super(worldIn);
      this.setSize(0.901F, 4.2F);
      this.stepHeight = 1.0F;
      this.tendril1 = new EntityBody(this, 0.7F, 0.9F, 1.0F, 0.7F, 3.7F, 1, 1, true);
      this.tendril2 = new EntityBody(this, 0.7F, 0.9F, 1.0F, 0.7F, 3.7F, -1, 2, true);
      this.left1 = 1;
      this.right2 = 1;
      this.adaptationCap = 0.95F;
      this.ignoreFrustumCheck = true;
      this.skillCharge = false;
   }

   public int getParasiteIDRegister() {
      return 33;
   }

   protected void initEntityAI() {
      this.tasks.addTask(0, new EntityAISkill(this, 80, 100, 10, true, 14));
      this.setskillLeapValues(1.2F, 2.5D, 5);
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
      this.tasks.addTask(0, new EntityAISwimmingDiving(this, 0.12D));
      this.tasks.addTask(2, new EntityAIWaterLeapAtTargetStatus(this, 0.7F, 1.5D, 3, 20, 0));
      this.tasks.addTask(3, new EntityAIAttackMeleeStatusAOE(this, 1.3D, false, 8.0D, 4.0D));
      this.tasks.addTask(2, new EntityAISkill(this, 40, 32, 8, true, 1));
      this.tasks.addTask(2, new EntityAIEvadeDash(this, 30, 2, 4, 1.0D, 15));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.GANRO_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.GANRO_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.27D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.GANRO_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.GANRO_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.pureFollow);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.up) {
         this.attackTimer = (float)((double)this.attackTimer + 0.2D);
         if ((double)this.attackTimer > 1.5D) {
            this.up = false;
         }
      } else {
         this.attackTimer = (float)((double)this.attackTimer - 0.1D);
      }

      this.tendril1.onUpdate();
      this.tendril2.onUpdate();
      if (!this.world.isRemote) {
         this.setBesideClimbableBlock(this.collidedHorizontally);
      }

   }

   protected boolean spawnT(EntityLivingBase in, int range2, int mini2, int check, int type) {
      if (this.canEntityBeSeen(in)) {
         return false;
      } else {
         return this.rand.nextInt(4) == 0 ? super.spawnT(in, range2, mini2, check, type) : super.spawnT(in, range2, mini2, check, 2);
      }
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(CLIMBING, Byte.valueOf((byte)0));
   }

   public boolean isBesideClimbableBlock() {
      return (((Byte)this.dataManager.get(CLIMBING)).byteValue() & 1) != 0;
   }

   public void setBesideClimbableBlock(boolean climbing) {
      byte b0 = ((Byte)this.dataManager.get(CLIMBING)).byteValue();
      if (climbing) {
         b0 = (byte)(b0 | 1);
      } else {
         b0 = (byte)(b0 & -2);
      }

      this.dataManager.set(CLIMBING, Byte.valueOf(b0));
   }

   public boolean isOnLadder() {
      return this.isBesideClimbableBlock();
   }

   protected PathNavigate createNavigator(World worldIn) {
      return new PathNavigateClimberStatus(this, worldIn);
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      boolean flag = super.attackEntityAsMob(entityIn);
      if (flag) {
         entityIn.motionY += 0.5000000059604645D;
      }

      return flag;
   }

   public void fall(float distance, float damageMultiplier) {
   }

   public float getEyeHeight() {
      return 3.5F;
   }

   public static void registerFixesLodo(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityGanro.class);
   }

   protected SoundEvent getAmbientSound() {
      return this.getParasiteStatus() != 0 ? SRPSounds.MOBSILENCE : SRPSounds.GANRO_GROWL;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.rand.nextBoolean() && this.getHitStatus() > 0 ? SRPSounds.MOBSILENCE : SRPSounds.GANRO_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.GANRO_DEATH;
   }

   public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
      boolean flag = super.scaryOrbEffect(in, mobs);
      if (flag) {
         ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.ganroOrbEffects, mobs);
      }

      return flag;
   }

   public void setDead() {
      if (this.tendril1 != null) {
         this.world.removeEntityDangerously(this.tendril1);
      }

      if (this.tendril2 != null) {
         this.world.removeEntityDangerously(this.tendril2);
      }

      super.setDead();
   }

   public boolean attackEntityAsMobAOE(Entity entityIn) {
      if (this.borderOrb != 0) {
         return false;
      } else {
         this.up = true;
         this.attackTimer = 0.0F;
         this.world.setEntityState(this, (byte)12);
         boolean flag = false;
         this.playSound(SRPSounds.SWIPE, 2.0F, 1.0F);
         AxisAlignedBB axisalignedbb = (new AxisAlignedBB(entityIn.posX, entityIn.posY, entityIn.posZ, entityIn.posX + 1.0D, entityIn.posY + 1.0D, entityIn.posZ + 1.0D)).grow(2.0D);

         for(EntityLivingBase mob : this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb)) {
            if (mob instanceof EntityParasiteBase) {
               if (this.getAttackTarget() == mob) {
                  this.setAttackTarget((EntityLivingBase)null);
                  return false;
               }
            } else if (mob != this && this.canEntityBeSeen(mob) && this.attackEntityAsMob(mob)) {
               flag = true;
            }
         }

         return flag;
      }
   }

   public boolean attackEntityBodyFrom(DamageSource source, float amount, int id, boolean notify) {
      if (this.rand.nextBoolean()) {
         SRPPotions.applyStackPotion(SRPPotions.BLEED_E, this, 80, 0);
      }

      return this.attackEntityFrom(source, amount * 3.0F);
   }

   public void setBodyPartDead(int id) {
      if (this.tendril1.getId() == id) {
         this.tendril1.setDead();
      } else if (this.tendril2.getId() == id) {
         this.tendril2.setDead();
      }

   }

   @SideOnly(Side.CLIENT)
   public float getAttackTimer() {
      return this.attackTimer;
   }

   @SideOnly(Side.CLIENT)
   public byte getLeft() {
      return this.left1;
   }

   @SideOnly(Side.CLIENT)
   public byte getRight() {
      return this.right2;
   }

   @SideOnly(Side.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 11) {
         this.left1 = 0;
      } else if (id == 12) {
         this.up = true;
         this.attackTimer = 0.0F;
      } else if (id == 22) {
         this.right2 = 0;
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
         return this.skillCharge;
      default:
         return super.getFinished(attID);
      }
   }

   public void setFinished(byte attID, boolean in) {
      switch(attID) {
      case 1:
         this.skillCharge = in;
         return;
      default:
         super.setFinished(attID, in);
      }
   }

   public void doSpecialSkill(byte id) {
      switch(id) {
      case 1:
         this.charge();
         return;
      default:
         super.doSpecialSkill(id);
      }
   }

   private void charge() {
      ++this.attacking;
      if (this.attacking < 20) {
         this.world.setEntityState(this, (byte)100);
         if (this.attacking == 2) {
            float v = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F + 2.0F;
            this.playSound(this.getHurtSound(DamageSource.GENERIC), 4.0F, v);
         }

         EntityLivingBase entitylivingbase = this.getAttackTarget();
         if (entitylivingbase == null || !this.onGround || this.isInWater() || entitylivingbase.posY > this.posY && entitylivingbase.onGround) {
            this.skillCharge = true;
            this.attacking = 0;
            this.setParasiteStatus(0);
            return;
         }

         if (!entitylivingbase.isEntityAlive()) {
            this.skillCharge = true;
            this.attacking = 0;
            this.setParasiteStatus(0);
            return;
         }

         if (this.attacking <= 19) {
            double dis = (double)this.getDistance(entitylivingbase);
            this.setParasiteStatus(3);
            this.getNavigator().clearPath();
            this.targetX = this.posX + 15.0D * (entitylivingbase.posX - this.posX) / dis;
            this.targetY = this.posY + 15.0D * (entitylivingbase.posY - this.posY) / dis;
            this.targetZ = this.posZ + 15.0D * (entitylivingbase.posZ - this.posZ) / dis;
         }
      }

      if (this.attacking == 20) {
         this.getNavigator().tryMoveToXYZ(this.targetX, this.targetY, this.targetZ, 3.0D);
      }

      if (this.attacking >= 20) {
         for(EntityLivingBase mob : this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(2.0D, 0.0D, 2.0D))) {
            if (mob != this && !(mob instanceof EntityParasiteBase)) {
               float f = (float)MathHelper.atan2(mob.posZ - this.posZ, mob.posX - this.posX);
               EntityDamage damage = new EntityDamage(this.world, mob.posX, mob.posY, mob.posZ, f, this, 1.0F, false, 0.5F);
               this.world.spawnEntity(damage);
            }
         }
      }

      this.skillBreakBlocks();
      if (!this.onGround) {
         this.motionX *= 0.7D;
         this.motionZ *= 0.7D;
      }

      if (this.attacking >= 60 && this.posX == this.prevPosX && this.posZ == this.prevPosZ) {
         this.attacking = 0;
         this.skillCharge = true;
         this.setParasiteStatus(2);
      }

   }
}
