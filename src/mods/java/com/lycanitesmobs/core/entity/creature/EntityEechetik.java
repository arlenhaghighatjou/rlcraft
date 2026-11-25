package com.lycanitesmobs.core.entity.creature;

import com.google.common.base.Predicate;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.PotionBase;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Blocks;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityEechetik extends TameableCreatureEntity implements IMob {
   public int myceliumRadius = 2;

   public EntityEechetik(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.ARTHROPOD;
      this.hasAttackSound = true;
      this.setupMob();
      this.stepHeight = 1.0F;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(true));
   }

   public void loadCreatureFlags() {
      this.myceliumRadius = this.creatureInfo.getFlag("myceliumRadius", this.myceliumRadius);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote && this.updateTick % 40L == 0L && this.hasAttackTarget()) {
         PotionBase plague = ObjectManager.getEffect("plague");
         if (plague != null) {
            PotionEffect potionEffect = new PotionEffect(plague, this.getEffectDuration(2), 1);

            for(Object entityObj : this.getNearbyEntities(EntityLivingBase.class, (Predicate)null, 2.0D)) {
               EntityLivingBase target = (EntityLivingBase)entityObj;
               if (target != this && this.canAttackClass(target.getClass()) && this.canAttackEntity(target) && this.getEntitySenses().canSee(target) && target.isPotionApplicable(potionEffect)) {
                  target.addPotionEffect(potionEffect);
               }
            }
         }
      }

      if (!this.getEntityWorld().isRemote && this.updateTick % 100L == 0L && this.myceliumRadius > 0 && !this.isTamed() && this.getEntityWorld().getGameRules().getBoolean("mobGriefing")) {
         int range = this.myceliumRadius;

         for(int w = -((int)Math.ceil((double)this.width) + range); (double)w <= Math.ceil((double)this.width) + (double)range; ++w) {
            for(int d = -((int)Math.ceil((double)this.width) + range); (double)d <= Math.ceil((double)this.width) + (double)range; ++d) {
               for(int h = -((int)Math.ceil((double)this.height) + range); (double)h <= Math.ceil((double)this.height); ++h) {
                  BlockPos blockPos = this.getPosition().add(w, h, d);
                  IBlockState blockState = this.getEntityWorld().getBlockState(blockPos);
                  IBlockState upperIBlockState = this.getEntityWorld().getBlockState(blockPos.up());
                  if (upperIBlockState.getBlock() == Blocks.AIR && blockState.getBlock() == Blocks.DIRT) {
                     this.getEntityWorld().setBlockState(blockPos, Blocks.MYCELIUM.getDefaultState());
                  }
               }
            }
         }
      }

      if (this.getEntityWorld().isRemote) {
         for(int i = 0; i < 2; ++i) {
            this.getEntityWorld().spawnParticle(EnumParticleTypes.PORTAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width * 2.0D, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width * 2.0D, 0.0D, 0.0D, 0.0D, new int[0]);
            this.getEntityWorld().spawnParticle(EnumParticleTypes.TOWN_AURA, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width * 2.0D, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width * 2.0D, 0.0D, 0.0D, 0.0D, new int[0]);
         }
      }

   }

   public boolean attackMelee(Entity target, double damageScale) {
      return super.attackMelee(target, damageScale);
   }

   public boolean isFlying() {
      return true;
   }

   public boolean isStrongSwimmer() {
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
      return damageSrc.isFireDamage() ? 0.0F : super.getDamageModifier(damageSrc);
   }

   public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
      return type.equals("inWall") ? false : super.isDamageTypeApplicable(type, source, damage);
   }

   public boolean canBreatheUnderwater() {
      return true;
   }
}
