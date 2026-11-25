package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityTrite extends TameableCreatureEntity implements IMob {
   public EntityTrite(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = true;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.hasAttackTarget() && this.onGround && !this.getEntityWorld().isRemote && this.rand.nextInt(10) == 0) {
         this.leap(6.0F, 0.6D, this.getAttackTarget());
      }

   }

   public boolean canAttackEntity(EntityLivingBase target) {
      return !(target instanceof EntityAstaroth) && !(target instanceof EntityAsmodeus) ? super.canAttackEntity(target) : false;
   }

   public boolean canClimb() {
      return true;
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public boolean isPotionApplicable(PotionEffect potionEffect) {
      if (potionEffect.getPotion() == MobEffects.WITHER) {
         return false;
      } else if (ObjectManager.getEffect("decay") != null && potionEffect.getPotion() == ObjectManager.getEffect("decay")) {
         return false;
      } else {
         super.isPotionApplicable(potionEffect);
         return true;
      }
   }

   public boolean canBurn() {
      return false;
   }

   public float getFallResistance() {
      return 10.0F;
   }

   public boolean webProof() {
      return true;
   }
}
