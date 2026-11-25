package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityVapula extends TameableCreatureEntity implements IMob {
   public int blockBreakRadius = 0;
   public float fireDamageAbsorbed = 0.0F;

   public EntityVapula(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = true;
      this.setupMob();
      this.stepHeight = 1.0F;
      this.attackPhaseMax = 8;
      this.setAttackCooldownMax(60);
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(false).setMaxChaseDistanceSq(3.0F));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackRangedGoal(this)).setSpeed(0.75D).setRange(18.0F).setMinChaseDistance(10.0F).setCheckSight(false));
   }

   public void loadCreatureFlags() {
      this.blockBreakRadius = this.creatureInfo.getFlag("blockBreakRadius", this.blockBreakRadius);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote && this.isRareVariant() && !this.isPetType("familiar") && this.hasAttackTarget() && this.getDistance(this.getAttackTarget()) > 1.0F && this.getRNG().nextInt(20) == 0) {
         if (this.posY - 1.0D > this.getAttackTarget().posY) {
            this.leap(6.0F, -1.0D, this.getAttackTarget());
         } else if (this.posY + 1.0D < this.getAttackTarget().posY) {
            this.leap(6.0F, 1.0D, this.getAttackTarget());
         } else {
            this.leap(6.0F, 0.0D, this.getAttackTarget());
         }

         if (this.getEntityWorld().getGameRules().getBoolean("mobGriefing") && this.blockBreakRadius > -1 && !this.isTamed()) {
            this.destroyArea((int)this.posX, (int)this.posY, (int)this.posZ, 10.0F, true, this.blockBreakRadius);
         }
      }

      if (this.getEntityWorld().isRemote && !CreatureManager.getInstance().config.disableBlockParticles) {
         for(int i = 0; i < 2; ++i) {
            World var10000 = this.getEntityWorld();
            EnumParticleTypes var10001 = EnumParticleTypes.BLOCK_CRACK;
            double var10002 = this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width;
            double var10003 = this.posY + this.rand.nextDouble() * (double)this.height;
            double var10004 = this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width;
            int[] var10008 = new int[1];
            BlockTallGrass var10011 = Blocks.TALLGRASS;
            var10008[0] = BlockTallGrass.getStateId(Blocks.DIAMOND_BLOCK.getDefaultState());
            var10000.spawnParticle(var10001, var10002, var10003, var10004, 0.0D, 0.0D, 0.0D, var10008);
         }
      }

   }

   public float getAISpeedModifier() {
      return this.hasAttackTarget() && this.getAttackTarget() instanceof EntitySilverfish ? 4.0F : super.getAISpeedModifier();
   }

   public boolean attackMelee(Entity target, double damageScale) {
      if (!super.attackMelee(target, damageScale)) {
         return false;
      } else {
         if (target instanceof EntitySilverfish) {
            target.setDead();
         }

         return true;
      }
   }

   public void attackRanged(Entity target, float range) {
      this.fireProjectile("crystalshard", target, range, 0.0F, new Vec3d(0.0D, 0.0D, 0.0D), 0.6F, 2.0F, 1.0F);
      this.nextAttackPhase();
      super.attackRanged(target, range);
   }

   public int getRangedCooldown() {
      return this.getAttackPhase() < 7 ? super.getRangedCooldown() / 24 : super.getRangedCooldown();
   }

   public boolean isFlying() {
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

   public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
      if (!type.equals("cactus") && !type.equals("inWall")) {
         if (source.isFireDamage()) {
            this.fireDamageAbsorbed += damage;
            return false;
         } else {
            return super.isDamageTypeApplicable(type, source, damage);
         }
      } else {
         return false;
      }
   }

   public boolean canBurn() {
      return false;
   }

   public boolean canBreatheUnderwater() {
      return true;
   }
}
