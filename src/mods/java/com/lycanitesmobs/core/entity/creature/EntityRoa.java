package com.lycanitesmobs.core.entity.creature;

import com.google.common.base.Predicate;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupBoss;
import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityRoa extends RideableCreatureEntity implements IMob {
   protected int whirlpoolRange = 6;
   protected int whirlpoolEnergy = 0;
   protected int whirlpoolEnergyMax = 100;
   protected boolean whirlpoolRecharging = true;
   protected int mountedWhirlpool = 0;

   public EntityRoa(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.spawnsOnLand = false;
      this.spawnsInWater = true;
      this.hasAttackSound = true;
      this.babySpawnChance = 0.0D;
      this.canGrow = true;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(false));
   }

   public void loadCreatureFlags() {
      this.whirlpoolRange = this.creatureInfo.getFlag("whirlpoolRange", this.whirlpoolRange);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote) {
         if (this.whirlpoolRecharging && ++this.whirlpoolEnergy >= this.whirlpoolEnergyMax) {
            this.whirlpoolRecharging = false;
         }

         this.whirlpoolEnergy = Math.min(this.whirlpoolEnergy, this.whirlpoolEnergyMax);
         if (this.canWhirlpool()) {
            Iterator var1 = this.getNearbyEntities(Entity.class, (Predicate)null, (double)this.whirlpoolRange).iterator();

            label92:
            while(true) {
               Entity entity;
               EntityPlayerMP player;
               while(true) {
                  while(true) {
                     if (!var1.hasNext()) {
                        if (--this.whirlpoolEnergy <= 0) {
                           this.whirlpoolRecharging = true;
                        }
                        break label92;
                     }

                     entity = (Entity)var1.next();
                     if (entity != this && entity.getClass() != this.getClass() && entity != this.getControllingPassenger() && !(entity instanceof IGroupBoss) && !(entity instanceof IGroupHeavy)) {
                        if (!(entity instanceof EntityLivingBase)) {
                           break;
                        }

                        EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
                        if (!entityLivingBase.isPotionActive(ObjectManager.getEffect("weight")) && this.canAttackEntity(entityLivingBase) && (entity.isInWater() || this.spawnEventType.equalsIgnoreCase("sharknado"))) {
                           break;
                        }
                     }
                  }

                  player = null;
                  if (!(entity instanceof EntityPlayerMP)) {
                     break;
                  }

                  player = (EntityPlayerMP)entity;
                  if (!player.capabilities.isCreativeMode) {
                     break;
                  }
               }

               double xDist = this.posX - entity.posX;
               double zDist = this.posZ - entity.posZ;
               double xzDist = Math.max((double)MathHelper.sqrt(xDist * xDist + zDist * zDist), 0.01D);
               double factor = 0.1D;
               double motionCap = 10.0D;
               if (entity.motionX < motionCap && entity.motionX > -motionCap && entity.motionZ < motionCap && entity.motionZ > -motionCap) {
                  entity.addVelocity(xDist / xzDist * factor + entity.motionX * factor, 0.0D, zDist / xzDist * factor + entity.motionZ * factor);
               }

               if (player != null) {
                  player.connection.sendPacket(new SPacketEntityVelocity(entity));
               }
            }
         }
      }

      if (this.mountedWhirlpool > 0) {
         --this.mountedWhirlpool;
      }

   }

   public void riderEffects(EntityLivingBase rider) {
      rider.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 105, 1));
      super.riderEffects(rider);
   }

   public boolean extraAnimation01() {
      return this.getEntityWorld().isRemote ? super.extraAnimation01() : this.canWhirlpool();
   }

   public boolean canWhirlpool() {
      if (this.getEntityWorld().isRemote) {
         return this.extraAnimation01();
      } else if ("sharknado".equals(this.spawnEventType)) {
         return true;
      } else if (!this.isInWater()) {
         return false;
      } else if (this.getControllingPassenger() != null && this.mountedWhirlpool > 0) {
         return true;
      } else {
         return !this.whirlpoolRecharging && this.hasAttackTarget() && this.getDistance(this.getAttackTarget()) <= (float)(this.whirlpoolRange * 3);
      }
   }

   public float getBlockPathWeight(int x, int y, int z) {
      int waterWeight = 10;
      Block block = this.getEntityWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
      if (block == Blocks.WATER) {
         return (super.getBlockPathWeight(x, y, z) + 1.0F) * (float)(waterWeight + 1);
      } else if (this.getEntityWorld().isRaining() && this.getEntityWorld().canBlockSeeSky(new BlockPos(x, y, z))) {
         return (super.getBlockPathWeight(x, y, z) + 1.0F) * (float)(waterWeight + 1);
      } else if (this.getAttackTarget() != null) {
         return super.getBlockPathWeight(x, y, z);
      } else {
         return this.waterContact() ? -999999.0F : super.getBlockPathWeight(x, y, z);
      }
   }

   public boolean isStrongSwimmer() {
      return true;
   }

   public boolean canWalk() {
      return false;
   }

   public boolean isFlying() {
      return "sharknado".equals(this.spawnEventType) ? true : super.isFlying();
   }

   public double getMountedYOffset() {
      return (double)this.height * 0.25D;
   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   public boolean canBreatheAir() {
      return "sharknado".equals(this.spawnEventType);
   }

   public void mountAbility(Entity rider) {
      if (!this.getEntityWorld().isRemote) {
         if (this.getStamina() >= this.getStaminaCost()) {
            this.applyStaminaCost();
            this.mountedWhirlpool = 20;
         }
      }
   }

   public float getStaminaCost() {
      return 2.0F;
   }

   public int getStaminaRecoveryWarmup() {
      return 80;
   }

   public float getStaminaRecoveryMax() {
      return 2.0F;
   }

   public void onDismounted(Entity entity) {
      super.onDismounted(entity);
      if (entity != null && entity instanceof EntityLivingBase) {
         ((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 100, 1));
      }

   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public boolean petControlsEnabled() {
      return true;
   }
}
