package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.GoalConditions;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.FireProjectilesGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityChupacabra extends TameableCreatureEntity {
   public EntityChupacabra(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = true;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setSpeed(1.5D));
      this.tasks.addTask(this.nextIdleGoalIndex, (new FireProjectilesGoal(this)).setProjectile("chaosorb").setFireRate(40).setVelocity(1.0F).setScale(0.5F).setConditions((new GoalConditions()).setRareVariantOnly(true)));
   }

   public boolean shouldCreatureGroupFlee(EntityLivingBase target) {
      return false;
   }

   public boolean attackMelee(Entity target, double damageScale) {
      if (!super.attackMelee(target, damageScale)) {
         return false;
      } else {
         if ((target instanceof EntityAnimal || target instanceof BaseCreatureEntity && ((BaseCreatureEntity)target).creatureInfo.isFarmable()) && target.height >= 1.0F) {
            this.breed();
         }

         float leeching = Math.max(1.0F, this.getAttackDamage(damageScale) / 2.0F);
         this.heal(leeching);
         return true;
      }
   }

   public boolean canBeTempted() {
      return this.isChild();
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

   public boolean isBreedingItem(ItemStack itemStack) {
      return false;
   }
}
