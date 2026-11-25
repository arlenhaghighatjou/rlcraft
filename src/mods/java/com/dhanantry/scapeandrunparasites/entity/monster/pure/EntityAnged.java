package com.dhanantry.scapeandrunparasites.entity.monster.pure;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeRangeSwitch;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackRangedStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPure;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityTendril;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileAngedball;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.SRPPacketEntityBodyDead;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityAnged extends EntityPPure implements IRangedAttackMob, EntityBodyParts {
   private EntityBody leftTendril;
   private EntityBody rightTendril;
   private float leftTendrilHealth;
   private float rightTendrilHealth;

   public EntityAnged(World worldIn) {
      super(worldIn);
      this.setSize(1.6F, 3.1F);
      this.type = 51;
      this.stepHeight = 1.0F;
      this.leftTendril = new EntityBody(this, 0.7F, 0.9F, 1.0F, 1.1F, 2.3F, 1, 1, true);
      this.rightTendril = new EntityBody(this, 0.7F, 0.9F, 1.0F, 1.1F, 2.3F, -1, 2, true);
      this.leftTendrilHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
      this.rightTendrilHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
   }

   public int getParasiteIDRegister() {
      return 25;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(6, new EntityAIAttackMeleeRangeSwitch(this, 5.0F));
      this.tasks.addTask(2, new EntityAIAttackMeleeStatus(this, 1.5D, false, 0.0D));
      this.tasks.addTask(4, new EntityAIAttackRangedStatus(this, 1.5D, 10, 20, 32.0F));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.ANGED_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.ANGED_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.ANGED_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.ANGED_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.pureFollow);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.leftTendrilHealth > 0.0F) {
         this.leftTendril.onUpdate();
      }

      if (this.rightTendrilHealth > 0.0F) {
         this.rightTendril.onUpdate();
      }

   }

   public float getEyeHeight() {
      return 3.0F;
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      boolean flag = super.attackEntityAsMob(entityIn);
      if (flag && entityIn instanceof EntityLivingBase) {
         ((EntityLivingBase)entityIn).knockBack(entityIn, 1.0F, this.posX - entityIn.posX, this.posZ - entityIn.posZ);
      }

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
                  tendril.setSkin(6);
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
                  tendril.setSkin(6);
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

   public static void registerFixesLodo(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityAnged.class);
   }

   protected SoundEvent getAmbientSound() {
      return SRPSounds.ANGED_GROWL;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.rand.nextBoolean() && this.getHitStatus() > 0 ? SRPSounds.MOBSILENCE : SRPSounds.ANGED_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.ANGED_DEATH;
   }

   public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
      boolean flag = super.scaryOrbEffect(in, mobs);
      if (flag) {
         ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.angedOrbEffects, mobs);
      }

      return flag;
   }

   public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
      Vec3d vec3d = this.getLook(1.0F);
      double d2 = target.posX - (this.posX + vec3d.x);
      double var10000 = target.getEntityBoundingBox().minY + (double)(target.height / 2.0F) - (0.5D + this.posY + (double)(this.height / 2.0F));
      double d4 = target.posZ - (this.posZ + vec3d.z);
      this.playSound(SRPSounds.EMANA_SHOOTING, 2.0F, 1.0F);
      double d3 = target.getEntityBoundingBox().minY + (double)(target.height / 4.0F) - (1.0D + this.posY + (double)(this.height / 2.0F));
      EntityProjectileAngedball entitylargefireball = new EntityProjectileAngedball(this.world, this, d2, d3, d4);
      entitylargefireball.posX = this.posX + vec3d.x;
      entitylargefireball.posY = this.posY + (double)this.getEyeHeight() - 0.2D;
      entitylargefireball.posZ = this.posZ + vec3d.z;
      this.world.spawnEntity(entitylargefireball);
   }

   public void setSwingingArms(boolean swingingArms) {
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

   public void setDead() {
      if (this.leftTendril != null) {
         this.world.removeEntityDangerously(this.leftTendril);
      }

      if (this.rightTendril != null) {
         this.world.removeEntityDangerously(this.rightTendril);
      }

      super.setDead();
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
      } else if (id == 22) {
         this.rightTendrilHealth = 0.0F;
      } else {
         super.handleStatusUpdate(id);
      }

   }
}
