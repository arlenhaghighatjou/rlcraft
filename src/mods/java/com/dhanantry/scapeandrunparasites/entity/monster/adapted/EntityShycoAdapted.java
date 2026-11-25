package com.dhanantry.scapeandrunparasites.entity.monster.adapted;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatusAOE;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIBlockResidue;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIEvade;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanClimb;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAdapted;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityTendril;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityWave;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityShyco;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.SRPPacketEntityBodyDead;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.PathNavigateClimberStatus;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityShycoAdapted extends EntityPAdapted implements EntityCutomAttack, EntityBodyParts, EntityCanClimb {
   private float attackTimer;
   private boolean up;
   private double extraDamage;
   private double currentDamage;
   private double hpLeft;
   private EntityBody leftTendril;
   private EntityBody rightTendril;
   private float leftTendrilHealth;
   private float rightTendrilHealth;
   private static final DataParameter<Byte> CLIMBING = EntityDataManager.createKey(EntityShycoAdapted.class, DataSerializers.BYTE);
   private int border;
   private boolean skillshockwave;

   public EntityShycoAdapted(World worldIn) {
      super(worldIn);
      this.setSize(0.901F, 3.5F);
      this.stepHeight = 1.0F;
      this.extraDamage = this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue();
      this.leftTendril = new EntityBody(this, 0.6F, 2.0F, 1.0F, 0.7F, 1.4F, 1, 1, true);
      this.rightTendril = new EntityBody(this, 0.6F, 2.0F, 1.0F, 0.7F, 1.4F, -1, 2, true);
      this.leftTendrilHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
      this.rightTendrilHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
   }

   public int getParasiteIDRegister() {
      return 51;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(0, new EntityAISwimmingDiving(this, 0.11D));
      this.tasks.addTask(2, new EntityAISkill(this, 40, 16, 5, true, 1));
      this.tasks.addTask(2, new EntityAIWaterLeapAtTargetStatus(this, 0.7F, 1.5D, 3, 20, 0));
      this.tasks.addTask(3, new EntityAIAttackMeleeStatusAOE(this, 1.3D, false, 8.0D, 4.0D));
      this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
      if (SRPConfig.parasiteGenResidue) {
         this.tasks.addTask(9, new EntityAIBlockResidue(this, 2));
      }

      this.tasks.addTask(6, new EntityAIGetFollowers(this, 3, 32));
      this.tasks.addTask(2, new EntityAIEvade(this, 25, 10, 4));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.SHYCO_HEALTH + SRPAttributes.SHYCO_A_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.SHYCO_ARMOR + SRPAttributes.SHYCO_A_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.SHYCO_KD_RESISTANCE + SRPAttributes.SHYCO_A_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.SHYCO_ATTACK_DAMAGE + SRPAttributes.SHYCO_A_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.adaptedFollow);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.rand.nextDouble() < this.hpLeft && this.world.isRemote) {
         this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 106, 0);
      }

      if (this.up) {
         this.attackTimer = (float)((double)this.attackTimer + 0.2D);
         if ((double)this.attackTimer > 1.2D) {
            this.up = false;
         }
      } else {
         this.attackTimer = (float)((double)this.attackTimer - 0.1D);
      }

      if (this.leftTendrilHealth > 0.0F) {
         this.leftTendril.onUpdate();
      }

      if (this.rightTendrilHealth > 0.0F) {
         this.rightTendril.onUpdate();
      }

      if (!this.world.isRemote) {
         this.setBesideClimbableBlock(this.collidedHorizontally);
      }

   }

   public byte climbStatus() {
      return ((Byte)this.dataManager.get(CLIMBING)).byteValue();
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

   public boolean attackEntityFrom(DamageSource source, float amount) {
      boolean flag = super.attackEntityFrom(source, amount);
      if (flag) {
         this.currentDamage = this.extraDamage * (1.0D - (double)(this.getHealth() / this.getMaxHealth()) * SRPAttributes.SHYCO_A_I_DAMAGE);
      }

      this.hpLeft = (double)(1.0F - this.getHealth() / this.getMaxHealth());
      return flag;
   }

   public boolean attackEntityBodyFrom(DamageSource source, float amount, int id, boolean notify) {
      if (this.world.isRemote) {
         return false;
      } else {
         boolean flag = this.attackEntityFrom(source, amount);
         if (!flag) {
            return false;
         } else {
            if (this.leftTendril.getId() == id) {
               this.leftTendrilHealth -= amount;
               if (this.leftTendrilHealth <= 0.0F) {
                  EntityTendril tendril = new EntityTendril(this.world);
                  tendril.setSkin(1);
                  tendril.copyLocationAndAnglesFrom(this.leftTendril);
                  this.world.spawnEntity(tendril);
                  this.world.removeEntityDangerously(this.leftTendril);
                  this.world.setEntityState(this, (byte)11);
                  this.cutResistances(SRPConfig.adaptedPointDamCap / 2);
                  SRPMain.network.sendToAll(new SRPPacketEntityBodyDead(this.getEntityId(), id));
               }
            } else if (this.rightTendril.getId() == id) {
               this.rightTendrilHealth -= amount;
               if (this.rightTendrilHealth <= 0.0F) {
                  EntityTendril tendril = new EntityTendril(this.world);
                  tendril.setSkin(1);
                  tendril.copyLocationAndAnglesFrom(this.rightTendril);
                  this.world.spawnEntity(tendril);
                  this.world.removeEntityDangerously(this.rightTendril);
                  this.world.setEntityState(this, (byte)22);
                  this.cutResistances(SRPConfig.adaptedPointDamCap / 2);
                  SRPMain.network.sendToAll(new SRPPacketEntityBodyDead(this.getEntityId(), id));
               }
            }

            return flag;
         }
      }
   }

   public void setBodyPartDead(int id) {
      if (this.leftTendril.getId() == id) {
         this.world.removeEntityDangerously(this.leftTendril);
      } else if (this.rightTendril.getId() == id) {
         this.world.removeEntityDangerously(this.rightTendril);
      }

   }

   public boolean attackEntityAsMob(Entity entityIn) {
      boolean flag = super.attackEntityAsMob(entityIn);
      if (flag && entityIn instanceof EntityLivingBase) {
         entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float)this.currentDamage);
         switch(this.getSkin()) {
         case 5:
            SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (EntityLivingBase)entityIn, 100, 0);
            break;
         case 6:
            SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (EntityLivingBase)entityIn, 100, 0);
         }
      }

      this.hpLeft = (double)(1.0F - this.getHealth() / this.getMaxHealth());
      return flag;
   }

   public float getEyeHeight() {
      return 3.3F;
   }

   public void onDeath(DamageSource cause) {
      if (!this.world.isRemote) {
         if (SRPConfigWorld.coloniesActivated) {
            if (ParasiteEventWorld.numberofColonies(this.world) >= 1) {
               ParasiteEventEntity.checkColony(this.world, cause, this);
               ParasiteEventEntity.spawnNext(this, new EntityShyco(this.world), true, false);
            } else {
               super.onDeath(cause);
            }
         } else {
            super.onDeath(cause);
         }
      }

   }

   public static void registerFixesShyco(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityShycoAdapted.class);
   }

   protected SoundEvent getAmbientSound() {
      return this.getParasiteStatus() != 0 ? SRPSounds.MOBSILENCE : SRPSounds.ASHYCO_GROWL;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.rand.nextBoolean() && this.getHitStatus() > 0 ? SRPSounds.MOBSILENCE : SRPSounds.ASHYCO_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.ASHYCO_DEATH;
   }

   public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
      boolean flag = super.scaryOrbEffect(in, mobs);
      if (flag) {
         ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.shycoadaptedOrbEffects, mobs);
      }

      return flag;
   }

   protected void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound(SRPSounds.MONSTER_STEP, 0.15F, 1.0F);
   }

   public void setDead() {
      if (this.leftTendril != null) {
         this.world.removeEntityDangerously(this.leftTendril);
      }

      if (this.rightTendril != null) {
         this.world.removeEntityDangerously(this.rightTendril);
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
         AxisAlignedBB axisalignedbb = (new AxisAlignedBB(entityIn.posX, entityIn.posY, entityIn.posZ, entityIn.posX + 1.0D, entityIn.posY + 1.0D, entityIn.posZ + 1.0D)).grow(3.0D);

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

   protected void collideWithEntity(Entity entityIn) {
      super.collideWithEntity(entityIn);
      if (!this.world.isRemote) {
         if (entityIn instanceof EntityLivingBase && !(entityIn instanceof EntityParasiteBase) && this.getSkin() == 5) {
            SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (EntityLivingBase)entityIn, 100, 0);
         }

      }
   }

   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      IEntityLivingData floo = super.onInitialSpawn(difficulty, livingdata);
      if (this.rand.nextDouble() < SRPConfig.variantChance) {
         switch(this.rand.nextInt(3)) {
         case 0:
            this.setSkin(1);
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((SRPAttributes.SHYCO_HEALTH + SRPAttributes.SHYCO_A_HEALTH) * 0.5D);
            this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue((SRPAttributes.SHYCO_ATTACK_DAMAGE + SRPAttributes.SHYCO_A_ATTACK_DAMAGE) * 2.0D);
            this.setHealth((float)this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue());
            break;
         case 1:
            this.setSkin(5);
            break;
         case 2:
            this.setSkin(6);
         }
      }

      return floo;
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setFloat("parasiteleftTendril", this.leftTendrilHealth);
      compound.setFloat("parasiterightTendril", this.rightTendrilHealth);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      if (compound.hasKey("parasiteleftTendril", 99)) {
         this.leftTendrilHealth = compound.getFloat("parasiteleftTendril");
         if (this.leftTendrilHealth <= 0.0F) {
            this.world.setEntityState(this, (byte)11);
         }
      }

      if (compound.hasKey("parasiterightTendril", 99)) {
         this.rightTendrilHealth = compound.getFloat("parasiterightTendril");
         if (this.rightTendrilHealth <= 0.0F) {
            this.world.setEntityState(this, (byte)22);
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public float getAttackTimer() {
      return this.attackTimer;
   }

   @SideOnly(Side.CLIENT)
   public float getLeft() {
      return this.leftTendrilHealth;
   }

   @SideOnly(Side.CLIENT)
   public float getRight() {
      return this.rightTendrilHealth;
   }

   @SideOnly(Side.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 11) {
         this.leftTendrilHealth = 0.0F;
      } else if (id == 12) {
         this.up = true;
         this.attackTimer = 0.0F;
      } else if (id == 22) {
         this.rightTendrilHealth = 0.0F;
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
      if (!this.onGround) {
         this.skillshockwave = true;
         this.setParasiteStatus(0);
         this.border = 0;
      } else {
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
