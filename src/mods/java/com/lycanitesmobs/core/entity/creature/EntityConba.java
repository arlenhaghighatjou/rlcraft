package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.FireProjectilesGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindAvoidTargetGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityConba extends TameableCreatureEntity implements IMob {
   AttackMeleeGoal aiAttackMelee;
   public boolean vespidInfection = false;
   public int vespidInfectionTime = 0;

   public EntityConba(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = true;
      this.setupMob();
   }

   protected void initEntityAI() {
      this.aiAttackMelee = (new AttackMeleeGoal(this)).setLongMemory(true).setEnabled(false);
      this.tasks.addTask(this.nextPriorityGoalIndex++, this.aiAttackMelee);
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new FireProjectilesGoal(this)).setProjectile("poop").setFireRate(20).setVelocity(1.2F));
      this.targetTasks.addTask(this.nextSpecialTargetIndex++, (new FindAvoidTargetGoal(this)).setTargetClass(EntityPlayer.class).setTameTargetting(false));
      this.targetTasks.addTask(this.nextSpecialTargetIndex++, (new FindAvoidTargetGoal(this)).setTargetClass(EntityVillager.class));
   }

   public String getSpeciesName() {
      String infection = "";
      if (this.vespidInfection) {
         String entityName = this.creatureInfo.getName();
         if (entityName != null) {
            infection = LanguageManager.translate("entity." + this.creatureInfo.modInfo.modid + "." + entityName + ".infected") + " ";
         }
      }

      return super.getSpeciesName();
   }

   public String getTextureName() {
      return this.vespidInfection ? super.getTextureName() + "_infected" : super.getTextureName();
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.onGround && !this.getEntityWorld().isRemote) {
         if (this.hasAvoidTarget()) {
            if (this.rand.nextInt(10) == 0) {
               this.leap(1.0F, 0.6D, this.getAttackTarget());
            }
         } else if (this.rand.nextInt(50) == 0 && this.isMoving()) {
            this.leap(1.0D, 0.6D);
         }
      }

      if (!this.getEntityWorld().isRemote) {
         if (!this.vespidInfection && "theswarm".equals(this.spawnEventType)) {
            this.vespidInfection = true;
         }

         if (this.vespidInfection && !this.getEntityWorld().isRemote) {
            this.aiAttackMelee.setEnabled(true);
            if (this.vespidInfectionTime++ >= 1200) {
               this.spawnVespidSwarm();
               this.setDead();
            }
         } else {
            this.aiAttackMelee.setEnabled(false);
         }
      }

      if (this.getEntityWorld().isRemote) {
         this.vespidInfection = this.extraAnimation01();
         if (this.vespidInfection) {
            for(int i = 0; i < 2; ++i) {
               this.getEntityWorld().spawnParticle(EnumParticleTypes.SPELL_WITCH, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
            }
         }
      }

   }

   public boolean shouldCreatureGroupFlee(EntityLivingBase target) {
      return this.isTamed() ? false : super.shouldCreatureGroupFlee(target);
   }

   public boolean canAttackEntity(EntityLivingBase target) {
      return !(target instanceof EntityVespid) && !(target instanceof EntityVespidQueen) ? super.canAttackEntity(target) : false;
   }

   public void attackRanged(Entity target, float range) {
      this.fireProjectile("poop", target, range, 0.0F, new Vec3d(0.0D, 0.0D, 0.0D), 1.2F, 2.0F, 1.0F);
      super.attackRanged(target, range);
   }

   public void onDeath(DamageSource damageSource) {
      if (!this.getEntityWorld().isRemote && this.vespidInfection) {
         this.spawnVespidSwarm();
      }

      super.onDeath(damageSource);
   }

   public void spawnVespidSwarm() {
      int j = 2 + this.rand.nextInt(5) + this.getEntityWorld().getDifficulty().getDifficultyId() - 1;

      for(int k = 0; k < j; ++k) {
         float f = ((float)(k % 2) - 0.5F) * this.width / 4.0F;
         float f1 = ((float)(k / 2) - 0.5F) * this.width / 4.0F;
         EntityVespid vespid = (EntityVespid)CreatureManager.getInstance().getCreature("vespid").createEntity(this.getEntityWorld());
         vespid.setLocationAndAngles(this.posX + (double)f, this.posY + 0.5D, this.posZ + (double)f1, this.rand.nextFloat() * 360.0F, 0.0F);
         vespid.applyVariant(this.getVariantIndex());
         vespid.setGrowingAge(vespid.growthTime);
         vespid.spawnEventType = this.spawnEventType;
         this.getEntityWorld().spawnEntity(vespid);
         if (this.getAttackTarget() != null) {
            vespid.setRevengeTarget(this.getAttackTarget());
         }
      }

   }

   public boolean petControlsEnabled() {
      return true;
   }

   public boolean extraAnimation01() {
      return !this.getEntityWorld().isRemote ? this.vespidInfection : this.extraAnimation01;
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

   public void readFromNBT(NBTTagCompound nbtTagCompound) {
      super.readFromNBT(nbtTagCompound);
      if (nbtTagCompound.hasKey("VespidInfection")) {
         this.vespidInfection = nbtTagCompound.getBoolean("VespidInfection");
      }

      if (nbtTagCompound.hasKey("VespidInfectionTime")) {
         this.vespidInfectionTime = nbtTagCompound.getInteger("VespidInfectionTime");
      }

   }

   public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
      super.writeEntityToNBT(nbtTagCompound);
      nbtTagCompound.setBoolean("VespidInfection", this.vespidInfection);
      if (this.vespidInfection) {
         nbtTagCompound.setInteger("VespidInfectionTime", this.vespidInfectionTime);
      }

   }
}
