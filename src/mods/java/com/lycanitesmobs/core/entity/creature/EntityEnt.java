package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityEnt extends TameableCreatureEntity implements IMob {
   public EntityEnt(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.spawnsUnderground = false;
      this.hasAttackSound = true;
      this.spreadFire = true;
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
            this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 60, 1));
         } else if (this.waterContact()) {
            this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 60, 0));
         }
      }

   }

   public boolean canAttackEntity(EntityLivingBase target) {
      return target instanceof EntityTreant ? false : super.canAttackEntity(target);
   }

   public boolean attackMelee(Entity target, double damageScale) {
      if (!super.attackMelee(target, damageScale)) {
         return false;
      } else {
         float leeching = Math.max(1.0F, this.getAttackDamage(damageScale));
         this.heal(leeching);
         return true;
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

   public boolean petControlsEnabled() {
      return true;
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public ResourceLocation getTexture() {
      if ("Twisted Ent".equals(this.getCustomNameTag())) {
         String textureName = this.getTextureName() + "_twisted";
         if (AssetManager.getTexture(textureName) == null) {
            AssetManager.addTexture(textureName, this.creatureInfo.modInfo, "textures/entity/" + textureName.toLowerCase() + ".png");
         }

         return AssetManager.getTexture(textureName);
      } else if ("Salty Tree".equals(this.getCustomNameTag())) {
         String textureName = this.getTextureName() + "_saltytree";
         if (AssetManager.getTexture(textureName) == null) {
            AssetManager.addTexture(textureName, this.creatureInfo.modInfo, "textures/entity/" + textureName.toLowerCase() + ".png");
         }

         return AssetManager.getTexture(textureName);
      } else {
         return super.getTexture();
      }
   }
}
