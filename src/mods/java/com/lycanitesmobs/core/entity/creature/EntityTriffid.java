package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class EntityTriffid extends TameableCreatureEntity implements IMob {
   public EntityTriffid(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.spawnsUnderground = false;
      this.hasAttackSound = true;
      this.spreadFire = true;
      this.trueSight = true;
      this.canGrow = true;
      this.babySpawnChance = 0.1D;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setTargetClass(EntityPlayer.class).setLongMemory(false));
      this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.getAir() >= 0) {
         if (this.isInWater()) {
            this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 60, 2));
         } else if (this.waterContact()) {
            this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 60, 1));
         }
      }

   }

   public float getDamageModifier(DamageSource damageSrc) {
      if (damageSrc.isFireDamage()) {
         return 2.0F;
      } else {
         if (damageSrc.getTrueSource() != null) {
            ItemStack heldItem = ItemStack.EMPTY;
            if (damageSrc.getTrueSource() instanceof EntityLivingBase) {
               EntityLivingBase entityLiving = (EntityLivingBase)damageSrc.getTrueSource();
               if (!entityLiving.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) {
                  heldItem = entityLiving.getHeldItem(EnumHand.MAIN_HAND);
               }
            }

            if (ObjectLists.isAxe(heldItem)) {
               return 2.0F;
            }
         }

         return super.getDamageModifier(damageSrc);
      }
   }

   public float getFallResistance() {
      return 100.0F;
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
