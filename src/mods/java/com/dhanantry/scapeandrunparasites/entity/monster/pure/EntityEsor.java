package com.dhanantry.scapeandrunparasites.entity.monster.pure;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatusAOE;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIEvadeDash;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPure;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityTendril;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.SRPPacketEntityBodyDead;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.PathNavigateClimberStatus;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityEsor extends EntityPPure implements EntityCutomAttack, EntityBodyParts {
   private float attackTimer;
   private boolean up;
   private EntityBody leftTendril;
   private EntityBody rightTendril;
   private float leftTendrilHealth;
   private float rightTendrilHealth;
   private static final DataParameter<Byte> CLIMBING = EntityDataManager.createKey(EntityEsor.class, DataSerializers.BYTE);
   private int border;
   private boolean skillEnraged;

   public EntityEsor(World worldIn) {
      super(worldIn);
      this.setSize(0.901F, 4.2F);
      this.stepHeight = 1.0F;
      this.leftTendril = new EntityBody(this, 0.6F, 2.0F, 1.0F, 0.9F, 1.5F, 1, 1, true);
      this.rightTendril = new EntityBody(this, 0.6F, 2.0F, 1.0F, 0.9F, 1.5F, -1, 2, true);
      this.leftTendrilHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
      this.rightTendrilHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
      this.ignoreFrustumCheck = true;
      this.skillEnraged = false;
   }

   public int getParasiteIDRegister() {
      return 50;
   }

   protected void initEntityAI() {
      this.tasks.addTask(0, new EntityAISkill(this, 40, 100, 10, true, 14));
      this.setskillLeapValues(1.2F, 2.5D, 7);
      this.tasks.addTask(2, new EntityAISkill(this, 100, 7, true, 1));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
      this.tasks.addTask(0, new EntityAISwimmingDiving(this, 0.12D));
      this.tasks.addTask(2, new EntityAIWaterLeapAtTargetStatus(this, 0.7F, 1.5D, 3, 20, 7));
      this.tasks.addTask(3, new EntityAIAttackMeleeStatusAOE(this, 1.3D, false, 8.0D, 5.0D));
      this.tasks.addTask(2, new EntityAIEvadeDash(this, 20, 2, 5));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.ESOR_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.ESOR_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.255D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.ESOR_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.ESOR_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.pureFollow);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.up) {
         this.attackTimer = (float)((double)this.attackTimer + 0.2D);
         if (this.attackTimer > 1.0F) {
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

   public void setAttackTarget(EntityLivingBase entitylivingbaseIn) {
      if (this.border <= 0) {
         super.setAttackTarget(entitylivingbaseIn);
      }
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
      EntityLiving.registerFixesMob(fixer, EntityEsor.class);
   }

   protected SoundEvent getAmbientSound() {
      return this.getParasiteStatus() != 0 ? SRPSounds.MOBSILENCE : SRPSounds.ESOR_GROWL;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.rand.nextBoolean() && this.getHitStatus() > 0 ? SRPSounds.MOBSILENCE : SRPSounds.ESOR_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.ESOR_DEATH;
   }

   public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
      boolean flag = super.scaryOrbEffect(in, mobs);
      if (flag) {
         ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.esorOrbEffects, mobs);
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
                  tendril.setSkin(5);
                  tendril.copyLocationAndAnglesFrom(this.leftTendril);
                  this.world.spawnEntity(tendril);
                  this.world.removeEntityDangerously(this.leftTendril);
                  this.world.setEntityState(this, (byte)11);
                  this.cutResistances(SRPConfig.purePointDamCap / 2);
                  SRPMain.network.sendToAll(new SRPPacketEntityBodyDead(this.getEntityId(), id));
               }
            } else if (this.rightTendril.getId() == id) {
               this.rightTendrilHealth -= amount;
               if (this.rightTendrilHealth <= 0.0F) {
                  EntityTendril tendril = new EntityTendril(this.world);
                  tendril.setSkin(5);
                  tendril.copyLocationAndAnglesFrom(this.rightTendril);
                  this.world.spawnEntity(tendril);
                  this.world.removeEntityDangerously(this.rightTendril);
                  this.world.setEntityState(this, (byte)22);
                  this.cutResistances(SRPConfig.purePointDamCap / 2);
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

   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      return super.onInitialSpawn(difficulty, livingdata);
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
         return this.skillEnraged;
      default:
         return super.getFinished(attID);
      }
   }

   public void setFinished(byte attID, boolean in) {
      switch(attID) {
      case 1:
         this.skillEnraged = in;
         return;
      default:
         super.setFinished(attID, in);
      }
   }

   public void doSpecialSkill(byte id) {
      switch(id) {
      case 1:
         this.smash();
         return;
      default:
         super.doSpecialSkill(id);
      }
   }

   private void smash() {
      if (!this.onGround) {
         this.skillEnraged = true;
         this.setParasiteStatus(0);
         this.border = 0;
      } else {
         if (this.border == 2) {
            float v = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F + 2.0F;
            this.playSound(this.getHurtSound(DamageSource.GENERIC), 4.0F, v);
         }

         if (this.border < 20) {
            this.world.setEntityState(this, (byte)100);
            this.setParasiteStatus(25);
            this.getNavigator().clearPath();
            this.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 110, 100, false, false));
         }

         ++this.border;
         if (this.border >= 20) {
            this.setParasiteStatus(3);
            if (this.border == 0) {
               this.playSound(SRPSounds.SWIPE, 5.0F, 1.0F);
            }

            if (this.border % 7 == 0) {
               this.playSound(SRPSounds.SWIPE, 5.0F, 1.0F);
            }

            AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow(6.0D, 3.0D, 6.0D);

            for(EntityLivingBase mob : this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb)) {
               if (mob != this && !(mob instanceof EntityParasiteBase) && this.canEntityBeSeen(mob)) {
                  this.attackEntityAsMob(mob);
                  mob.knockBack(mob, 2.0F, mob.posX - this.posX, mob.posZ - this.posZ);
               }
            }
         }

         if (this.border > 100) {
            this.skillEnraged = true;
            this.setParasiteStatus(0);
            this.border = 0;
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow(24.0D, 5.0D, 24.0D);

            for(EntityParasiteBase mob : this.world.getEntitiesWithinAABB(EntityParasiteBase.class, axisalignedbb)) {
               if (mob != this && this.canEntityBeSeen(mob) && SRPConfigSystems.rageEnable) {
                  mob.addPotionEffect(new PotionEffect(SRPPotions.RAGE_E, 1200, 1, false, false));
               }
            }
         }

      }
   }
}
