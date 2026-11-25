package com.dhanantry.scapeandrunparasites.entity.monster.adapted;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatusAOE;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIBlockResidue;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGiveEffectsArea;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAdapted;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityTendril;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityBano;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.SRPPacketEntityBodyDead;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityBanoAdapted extends EntityPAdapted implements EntityCutomAttack, EntityBodyParts {
   private float attackTimer;
   private boolean up;
   private EntityBody leftTendril;
   private EntityBody rightTendril;
   private float leftTendrilHealth;
   private float rightTendrilHealth;

   public EntityBanoAdapted(World worldIn) {
      super(worldIn);
      this.setSize(1.3F, 3.8F);
      this.leftTendril = new EntityBody(this, 0.8F, 1.1F, 1.0F, 1.1F, 2.2F, 1, 1, true);
      this.rightTendril = new EntityBody(this, 0.8F, 1.1F, 1.0F, 1.1F, 2.2F, -1, 2, true);
      this.leftTendrilHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
      this.rightTendrilHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
      this.ignoreFrustumCheck = true;
   }

   public int getParasiteIDRegister() {
      return 56;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(0, new EntityAISwimmingDiving(this, 0.11D));
      this.tasks.addTask(2, new EntityAIAttackMeleeStatusAOE(this, 1.3D, false, 8.0D, 5.0D));
      this.tasks.addTask(3, new EntityAIGiveEffectsArea(this, SRPAttributes.ZETMO_A_CD, SRPAttributes.ZETMO_A_RANGE, SRPConfigMobs.zetmoadaptedeffects));
      if (SRPConfig.parasiteGenResidue) {
         this.tasks.addTask(9, new EntityAIBlockResidue(this, 2));
      }

      this.tasks.addTask(6, new EntityAIGetFollowers(this, 3, 32));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.ZETMO_HEALTH + SRPAttributes.ZETMO_A_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.ZETMO_ARMOR + SRPAttributes.ZETMO_A_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.22D);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.ZETMO_KD_RESISTANCE + SRPAttributes.ZETMO_A_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.ZETMO_ATTACK_DAMAGE + SRPAttributes.ZETMO_A_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.adaptedFollow);
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

      if (this.leftTendrilHealth > 0.0F) {
         this.leftTendril.onUpdate();
      }

      if (this.rightTendrilHealth > 0.0F) {
         this.rightTendril.onUpdate();
      }

   }

   public boolean attackEntityAsMob(Entity entityIn) {
      boolean flag = super.attackEntityAsMob(entityIn);
      if (flag && entityIn instanceof EntityLivingBase) {
         switch(this.getSkin()) {
         case 5:
            SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (EntityLivingBase)entityIn, 100, 0);
            break;
         case 6:
            SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (EntityLivingBase)entityIn, 100, 0);
         }
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
                  tendril.setSkin(4);
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
                  tendril.setSkin(4);
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

   protected void collideWithEntity(Entity entityIn) {
      super.collideWithEntity(entityIn);
      if (!this.world.isRemote) {
         if (entityIn instanceof EntityLivingBase && !(entityIn instanceof EntityParasiteBase) && this.getSkin() == 5) {
            SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (EntityLivingBase)entityIn, 100, 0);
         }

      }
   }

   public float getEyeHeight() {
      return 3.3F;
   }

   public void onDeath(DamageSource cause) {
      if (!this.world.isRemote) {
         if (SRPConfigWorld.coloniesActivated) {
            if (ParasiteEventWorld.numberofColonies(this.world) >= 1) {
               ParasiteEventEntity.checkColony(this.world, cause, this);
               ParasiteEventEntity.spawnNext(this, new EntityBano(this.world), true, false);
            } else {
               super.onDeath(cause);
            }
         } else {
            super.onDeath(cause);
         }
      }

   }

   public static void registerFixesBano(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityBanoAdapted.class);
   }

   protected SoundEvent getAmbientSound() {
      return this.getParasiteStatus() != 0 ? SRPSounds.MOBSILENCE : SRPSounds.AZETMO_GROWL;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.rand.nextBoolean() && this.getHitStatus() > 0 ? SRPSounds.MOBSILENCE : SRPSounds.AZETMO_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.AZETMO_DEATH;
   }

   public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
      boolean flag = super.scaryOrbEffect(in, mobs);
      if (flag) {
         ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.zetmoadaptedOrbEffects, mobs);
      }

      return flag;
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

   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      IEntityLivingData floo = super.onInitialSpawn(difficulty, livingdata);
      if (this.rand.nextDouble() < SRPConfig.variantChance) {
         switch(this.rand.nextInt(2)) {
         case 0:
            this.setSkin(5);
            break;
         case 1:
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
      } else {
         super.handleStatusUpdate(id);
      }

   }
}
