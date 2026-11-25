package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.targeting.DefendEntitiesGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindAttackTargetGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityMakaAlpha extends AgeableCreatureEntity {
   public EntityMakaAlpha(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = true;
      this.attackCooldownMax = 10;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.targetTasks.addTask(this.nextFindTargetIndex++, (new FindAttackTargetGoal(this)).addTargets(this.getClass()));
      this.targetTasks.addTask(this.nextSpecialTargetIndex++, new DefendEntitiesGoal(this, EntityMaka.class));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setTargetClass(EntityPlayer.class).setLongMemory(false));
      this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.hasAttackTarget() && this.getAttackTarget() instanceof EntityMakaAlpha && (this.getHealth() / this.getMaxHealth() <= 0.25F || this.getAttackTarget().getHealth() / this.getAttackTarget().getMaxHealth() <= 0.25F)) {
         this.setAttackTarget((EntityLivingBase)null);
      }

   }

   public float getBlockPathWeight(int x, int y, int z) {
      IBlockState blockState = this.getEntityWorld().getBlockState(new BlockPos(x, y - 1, z));
      Block block = blockState.getBlock();
      if (block != Blocks.AIR) {
         if (blockState.getMaterial() == Material.GRASS) {
            return 10.0F;
         }

         if (blockState.getMaterial() == Material.GROUND) {
            return 7.0F;
         }
      }

      return super.getBlockPathWeight(x, y, z);
   }

   public boolean canBeLeashedTo(EntityPlayer player) {
      return true;
   }

   public boolean canAttackEntity(EntityLivingBase target) {
      if (target instanceof EntityMaka) {
         return false;
      } else {
         return !(target instanceof EntityMakaAlpha) || this.getHealth() / this.getMaxHealth() > 0.25F && target.getHealth() / target.getMaxHealth() > 0.25F ? super.canAttackEntity(target) : false;
      }
   }

   public boolean canAttackOwnSpecies() {
      return true;
   }

   public void setAttackTarget(EntityLivingBase entity) {
      if (entity == null && this.getAttackTarget() instanceof EntityMakaAlpha) {
         this.heal((this.getMaxHealth() - this.getHealth()) / 2.0F);
         this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 400, 2, false, false));
         this.getAttackTarget().heal((this.getMaxHealth() - this.getHealth()) / 2.0F);
         this.getAttackTarget().addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 400, 2, false, false));
      }

      super.setAttackTarget(entity);
   }

   public boolean rollAttackTargetChance(EntityLivingBase target) {
      if (!(target instanceof EntityPlayer) && target.getClass() != this.getClass()) {
         return true;
      } else {
         return this.getRNG().nextDouble() <= 0.01D;
      }
   }

   public boolean isProtective(Entity entity) {
      return entity instanceof EntityMaka ? true : super.isProtective(entity);
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public float getDamageModifier(DamageSource damageSrc) {
      float damageMod = super.getDamageModifier(damageSrc);
      if (damageSrc.getTrueSource() instanceof EntityMakaAlpha) {
         damageMod *= 2.0F;
      }

      return damageMod;
   }

   public AgeableCreatureEntity createChild(AgeableCreatureEntity partner) {
      return (AgeableCreatureEntity)CreatureManager.getInstance().getCreature("maka").createEntity(this.getEntityWorld());
   }
}
