package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.actions.BreakDoorGoal;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityTroll extends TameableCreatureEntity implements IMob {
   public boolean griefing = true;
   public boolean stoneForm = false;

   public EntityTroll(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = false;
      this.solidCollision = true;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextDistractionGoalIndex++, new BreakDoorGoal(this));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackRangedGoal(this)).setSpeed(0.75D).setRange(40.0F).setMinChaseDistance(10.0F).setLongMemory(false));
      if (this.getNavigator() instanceof PathNavigateGround) {
         PathNavigateGround pathNavigateGround = (PathNavigateGround)this.getNavigator();
         pathNavigateGround.setBreakDoors(true);
      }

   }

   public void loadCreatureFlags() {
      this.griefing = this.creatureInfo.getFlag("griefing", this.griefing);
   }

   public String getTextureName() {
      return this.stoneForm ? super.getTextureName() + "_stone" : super.getTextureName();
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.stoneForm) {
         if (this.isDaytime() && this.getEntityWorld().canBlockSeeSky(this.getPosition())) {
            this.stoneForm = true;
         }
      } else if (!this.isDaytime() || !this.getEntityWorld().canBlockSeeSky(this.getPosition())) {
         this.stoneForm = false;
      }

      if (!this.getEntityWorld().isRemote && this.getAttackTarget() != null && this.getEntityWorld().getGameRules().getBoolean("mobGriefing") && this.griefing) {
         float distance = this.getAttackTarget().getDistance(this);
         if (distance <= this.width + 4.0F) {
            this.destroyArea((int)this.posX, (int)this.posY, (int)this.posZ, 0.5F, true);
         }
      }

   }

   public float getAISpeedModifier() {
      return this.stoneForm ? 0.125F : 1.0F;
   }

   public void attackRanged(Entity target, float range) {
      this.fireProjectile("boulderblast", target, range, 0.0F, new Vec3d(0.0D, 0.0D, 0.0D), 1.2F, 2.0F, 1.0F);
      super.attackRanged(target, range);
   }

   public float getDamageModifier(DamageSource damageSrc) {
      if (damageSrc.getTrueSource() != null) {
         ItemStack heldItem = ItemStack.EMPTY;
         if (damageSrc.getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase entityLiving = (EntityLivingBase)damageSrc.getTrueSource();
            if (!entityLiving.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) {
               heldItem = entityLiving.getHeldItem(EnumHand.MAIN_HAND);
            }
         }

         if (ObjectLists.isPickaxe(heldItem)) {
            return 3.0F;
         }
      }

      return super.getDamageModifier(damageSrc);
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public float getFallResistance() {
      return 50.0F;
   }

   public boolean canBurn() {
      return !this.stoneForm;
   }

   public boolean petControlsEnabled() {
      return true;
   }
}
