package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindAttackTargetGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityJoustAlpha extends AgeableCreatureEntity {
   public EntityJoustAlpha(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = true;
      this.attackCooldownMax = 10;
      this.trueSight = true;
      this.setupMob();
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(this.nextFindTargetIndex++, (new FindAttackTargetGoal(this)).addTargets(this.getClass()));
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(false));
   }

   public float getBlockPathWeight(int x, int y, int z) {
      IBlockState blockState = this.getEntityWorld().getBlockState(new BlockPos(x, y - 1, z));
      if (blockState.getBlock() != Blocks.AIR) {
         if (blockState.getMaterial() == Material.SAND) {
            return 10.0F;
         }

         if (blockState.getMaterial() == Material.CLAY) {
            return 7.0F;
         }

         if (blockState.getMaterial() == Material.ROCK) {
            return 5.0F;
         }
      }

      return super.getBlockPathWeight(x, y, z);
   }

   public boolean canAttackOwnSpecies() {
      return true;
   }

   public void setAttackTarget(EntityLivingBase entity) {
      if (entity == null && this.getAttackTarget() instanceof EntityJoustAlpha && this.getHealth() < this.getMaxHealth()) {
         this.heal((this.getMaxHealth() - this.getHealth()) / 2.0F);
         this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, 2, false, true));
      }

      super.setAttackTarget(entity);
   }

   public boolean isProtective(Entity entity) {
      return entity instanceof EntityJoust ? true : super.isProtective(entity);
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
      return type.equals("cactus") ? false : super.isDamageTypeApplicable(type, source, damage);
   }

   public AgeableCreatureEntity createChild(AgeableCreatureEntity partner) {
      return (AgeableCreatureEntity)CreatureManager.getInstance().getCreature("joust").createEntity(this.getEntityWorld());
   }
}
