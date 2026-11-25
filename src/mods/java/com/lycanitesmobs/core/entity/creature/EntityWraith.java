package com.lycanitesmobs.core.entity.creature;

import com.google.common.base.Predicate;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import java.util.Iterator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityWraith extends TameableCreatureEntity implements IMob {
   protected int detonateTimer = -1;

   public EntityWraith(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = true;
      this.setupMob();
      this.stepHeight = 1.0F;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setSpeed(2.0D).setLongMemory(false));
   }

   public void onLivingUpdate() {
      if (!this.getEntityWorld().isRemote) {
         if (this.detonateTimer == 0) {
            this.getEntityWorld().createExplosion(this, this.posX, this.posY, this.posZ, 1.0F, true);
            this.setDead();
         } else if (this.detonateTimer > 0) {
            --this.detonateTimer;
            if (this.getEntityWorld().getBlockState(this.getPosition()).getMaterial().isSolid()) {
               this.detonateTimer = 0;
            } else {
               Iterator i = this.getNearbyEntities(EntityLivingBase.class, (Predicate)null, 1.0D).iterator();

               label51:
               while(true) {
                  EntityLivingBase entity;
                  while(true) {
                     if (!i.hasNext()) {
                        break label51;
                     }

                     entity = (EntityLivingBase)i.next();
                     if (this.getPlayerOwner() == null || entity != this.getPlayerOwner()) {
                        if (!(entity instanceof TameableCreatureEntity)) {
                           break;
                        }

                        TameableCreatureEntity entityCreature = (TameableCreatureEntity)entity;
                        if (entityCreature.getPlayerOwner() == null || entityCreature.getPlayerOwner() != this.getPlayerOwner()) {
                           break;
                        }
                     }
                  }

                  this.detonateTimer = 0;
                  this.attackEntityAsMob(entity, 4.0D);
               }
            }
         }
      }

      if (this.getEntityWorld().isRemote && this.detonateTimer <= 5) {
         for(int i = 0; i < 2; ++i) {
            this.getEntityWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
            this.getEntityWorld().spawnParticle(EnumParticleTypes.FLAME, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
         }
      }

      super.onLivingUpdate();
   }

   public boolean rollWanderChance() {
      return this.getRNG().nextDouble() <= 0.25D;
   }

   public void chargeAttack() {
      this.leap(5.0D, (double)this.rotationPitch);
      this.detonateTimer = 10;
   }

   public boolean isFlying() {
      return true;
   }

   public void onDeath(DamageSource par1DamageSource) {
      if (!this.getEntityWorld().isRemote && this.getEntityWorld().getGameRules().getBoolean("mobGriefing")) {
         int explosionRadius = 1;
         if (this.subspecies != null) {
            explosionRadius = 3;
         }

         explosionRadius = Math.max(1, Math.round((float)explosionRadius * (float)this.sizeScale));
         this.getEntityWorld().createExplosion(this, this.posX, this.posY, this.posZ, (float)explosionRadius, true);
      }

      super.onDeath(par1DamageSource);
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public boolean canBurn() {
      return false;
   }

   public boolean waterDamage() {
      return true;
   }

   public float getBrightness() {
      return super.getBrightness();
   }

   @SideOnly(Side.CLIENT)
   public int getBrightnessForRender() {
      return super.getBrightnessForRender();
   }

   public void readFromNBT(NBTTagCompound nbtTagCompound) {
      super.readFromNBT(nbtTagCompound);
      if (nbtTagCompound.hasKey("DetonateTimer")) {
         this.detonateTimer = nbtTagCompound.getInteger("DetonateTimer");
      }

   }

   public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
      super.writeEntityToNBT(nbtTagCompound);
      if (this.detonateTimer > -1) {
         nbtTagCompound.setInteger("DetonateTimer", this.detonateTimer);
      }

   }
}
