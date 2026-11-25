package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.PotionBase;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.StealthGoal;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityLurker extends TameableCreatureEntity {
   public EntityLurker(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.ARTHROPOD;
      this.hasAttackSound = true;
      this.trueSight = true;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextPriorityGoalIndex++, (new StealthGoal(this)).setStealthTime(20).setStealthAttack(true).setStealthMove(true));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(false));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.getAttackTarget() != null) {
         PotionBase stalkingEffect = ObjectManager.getEffect("plague");
         if (stalkingEffect != null && this.getAttackTarget().isPotionActive(stalkingEffect)) {
            this.setAvoidTarget(this.getAttackTarget());
         } else {
            this.setAvoidTarget((EntityLivingBase)null);
         }
      } else {
         this.setAvoidTarget((EntityLivingBase)null);
      }

      if (this.onGround && !this.getEntityWorld().isRemote && this.rand.nextInt(10) == 0) {
         if (this.hasAttackTarget()) {
            this.leap(6.0F, 0.4D, this.getAttackTarget());
         } else if (this.hasAvoidTarget()) {
            this.leap(4.0D, 0.4D);
         }
      }

   }

   public boolean canStealth() {
      if (this.getEntityWorld().isRemote) {
         return false;
      } else {
         if (this.hasAttackTarget()) {
            if (this.getAttackTarget() instanceof EntityPlayer) {
               EntityPlayer playerTarget = (EntityPlayer)this.getAttackTarget();
               ItemStack itemstack = playerTarget.inventory.getCurrentItem();
               if (this.isTamingItem(itemstack)) {
                  return false;
               }
            }

            PotionBase stalkingEffect = ObjectManager.getEffect("plague");
            if (stalkingEffect != null && !this.getAttackTarget().isPotionActive(stalkingEffect)) {
               return false;
            }

            if ((double)this.getDistance(this.getAttackTarget()) < 25.0D) {
               return false;
            }
         } else if (this.isMoving()) {
            return false;
         }

         return true;
      }
   }

   public void startStealth() {
      if (this.getEntityWorld().isRemote) {
         EnumParticleTypes particle = EnumParticleTypes.SMOKE_LARGE;
         double d0 = this.rand.nextGaussian() * 0.02D;
         double d1 = this.rand.nextGaussian() * 0.02D;
         double d2 = this.rand.nextGaussian() * 0.02D;

         for(int i = 0; i < 100; ++i) {
            this.getEntityWorld().spawnParticle(particle, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2, new int[0]);
         }
      }

      super.startStealth();
   }

   public boolean canClimb() {
      return true;
   }

   public boolean petControlsEnabled() {
      return true;
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   @SideOnly(Side.CLIENT)
   public boolean isInvisibleToPlayer(EntityPlayer player) {
      return this.isTamed() && this.getOwner() == player ? false : this.isInvisible();
   }
}
